import { Button, Space } from 'antd';
import { useEffect, useState } from 'react';
import { attendanceOptions } from '../constants/attendance-status';
import { courseClassSessionRoleAdminApi } from '../api/course-class-session-api';
import {
  AttendanceStatus,
  type AttendanceData,
  type AttendanceStudent,
  type AttendancePayload,
} from '../types/attendance-type';
import SelectCustom from '@/shared/components/select/SelectCustom';
import InputTextAreaCustom from '@/shared/components/input/InputTextAreaCustom';
import ModalCustom from '@/shared/components/modal/ModalCustom';
import TableCustom from '@/shared/components/table/TableCustom';
import { useNotification } from '@/shared/hooks/useNotification';

interface AttendanceModalProps {
  open: boolean;

  sessionId?: string;

  onCancel: () => void;

  onSuccess?: () => void;
}

const AttendanceModal = ({ open, sessionId, onCancel, onSuccess }: AttendanceModalProps) => {
  const { attendance: attendanceApi, takeAttendance: takeAttendanceApi } =
    courseClassSessionRoleAdminApi;
  const { showNotification } = useNotification();

  const [loading, setLoading] = useState(false);

  const [saving, setSaving] = useState(false);

  const [attendanceData, setAttendanceData] = useState<AttendanceData | null>(null);

  const fetchAttendance = async () => {
    if (!sessionId) {
      return;
    }

    try {
      setLoading(true);

      const response = await attendanceApi(sessionId);

      setAttendanceData(response.data);
    } finally {
      setLoading(false);
    }
  };

  const handleFieldColumnChange = (
    studentId: string,
    field: keyof AttendanceStudent,
    value: any,
  ) => {
    if (!attendanceData) return;

    setAttendanceData({
      ...attendanceData,

      students: attendanceData.students.map((student) =>
        student.studentId === studentId
          ? {
              ...student,
              [field]: value,
            }
          : student,
      ),
    });
  };

  const handleSubmit = async () => {
    if (!attendanceData || !sessionId) {
      return;
    }

    try {
      setSaving(true);

      await takeAttendanceApi(sessionId, {
        attendances: attendanceData.students.map((student) => ({
          studentId: student.studentId,

          status: student.status ?? AttendanceStatus.PRESENT,

          note: student.note,
        })) as AttendancePayload[],
      });

      showNotification('success', 'Thành công', 'Điểm danh thành công');

      onSuccess?.();

      fetchAttendance();
    } catch (error) {
      showNotification('error', 'Lỗi', 'Có lỗi xảy ra khi lưu dữ liệu');
    } finally {
      setSaving(false);
    }
  };

  const columns = [
    {
      title: 'Mã học viên',
      dataIndex: 'studentCode',
    },

    {
      title: 'Họ tên',
      dataIndex: 'fullName',
    },

    {
      title: 'Trạng thái',
      width: 250,
      render: (_: any, record: AttendanceStudent) => (
        <SelectCustom
          value={record.status}
          options={attendanceOptions}
          onChange={(value) => handleFieldColumnChange(record.studentId, 'status', value)}
        />
      ),
    },

    {
      title: 'Ghi chú',
      render: (_: any, record: AttendanceStudent) => (
        <InputTextAreaCustom
          value={record.note ?? ''}
          onChange={(e) => handleFieldColumnChange(record.studentId, 'note', e.target.value)}
        />
      ),
    },
  ];

  useEffect(() => {
    if (open && sessionId) {
      fetchAttendance();
    }
  }, [open, sessionId]);

  return (
    <ModalCustom
      open={open}
      title="Điểm danh"
      onCancel={onCancel}
      width={1200}
      isCentered={false}
      footer={
        <Space>
          <Button onClick={onCancel}>Hủy</Button>

          <Button type="primary" loading={saving} onClick={handleSubmit}>
            Lưu điểm danh
          </Button>
        </Space>
      }
    >
      <TableCustom
        rowKey="studentId"
        loading={loading}
        columns={columns}
        dataSource={attendanceData?.students ?? []}
      />
    </ModalCustom>
  );
};

export default AttendanceModal;
