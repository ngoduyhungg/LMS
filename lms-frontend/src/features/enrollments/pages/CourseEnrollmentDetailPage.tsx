import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Typography, Table, Tag, Progress, Button, Popconfirm, message } from 'antd';
import { StopOutlined, ArrowLeftOutlined } from '@ant-design/icons';
import { adminEnrollmentApi } from '../api/enrollment-api';
import type { StudentEnrollmentDetail } from '../types/enrollment-type';

const { Title, Text } = Typography;

const CourseEnrollmentDetailPage: React.FC = () => {
  const { courseId } = useParams<{ courseId: string }>();
  const navigate = useNavigate();
  const [messageApi, contextHolder] = message.useMessage();

  const [enrollments, setEnrollments] = useState<StudentEnrollmentDetail[]>([]);
  const [loading, setLoading] = useState(true);
  const [totalRecords, setTotalRecords] = useState(0);

  const fetchDetail = async () => {
    if (!courseId) return;
    try {
      setLoading(true);
      const res = await adminEnrollmentApi.getCourseEnrollments(courseId, { page: 0, size: 20 });
      
      // HOTFIX: Lấy đúng thuộc tính "items" từ Backend
      const data = Array.isArray(res) ? res : (res.items || res.content || res.data || []);
      setEnrollments(data);
      setTotalRecords(res.total || data.length);
    } catch (error: any) {
      messageApi.destroy();
      messageApi.error('Lỗi khi tải danh sách học viên.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDetail();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [courseId]);

  const handleCancel = async (enrollmentId: string | number) => {
    try {
      const updatedEnrollment = await adminEnrollmentApi.cancel(enrollmentId);
      messageApi.destroy();
      messageApi.success(`Đã hủy ghi danh #${enrollmentId} thành công.`);
      setEnrollments((prev) => 
        prev.map((enr) => (enr.enrollmentId === enrollmentId ? updatedEnrollment : enr))
      );
    } catch (error: any) {
      messageApi.destroy();
      messageApi.error(error.response?.data?.message || 'Lỗi khi hủy ghi danh.');
    }
  };

  const columns = [
    {
      title: 'Mã GD',
      dataIndex: 'enrollmentId',
      key: 'enrollmentId',
      width: 90,
      render: (id: string | number) => <Text strong>#{id}</Text>
    },
    {
      title: 'Học viên',
      key: 'student',
      render: (_: any, record: StudentEnrollmentDetail) => (
        <div className="flex flex-col">
          <Text className="font-semibold text-gray-800">{record.studentName || 'Chưa cập nhật tên'}</Text>
          <Text type="secondary" className="text-xs">{record.studentEmail || `ID: ${record.studentId}`}</Text>
        </div>
      )
    },
    {
      title: 'Trạng thái',
      dataIndex: 'status',
      key: 'status',
      width: 140,
      render: (status: string) => {
        let color = 'default';
        let label = status;
        switch (status) {
          case 'ACTIVE': color = 'processing'; label = 'Đang học'; break;
          case 'COMPLETED': color = 'success'; label = 'Hoàn thành'; break;
          case 'CANCELLED': color = 'error'; label = 'Đã hủy'; break;
          case 'EXPIRED': color = 'warning'; label = 'Hết hạn'; break;
        }
        return <Tag color={color} className="m-0 font-medium border-0">{label}</Tag>;
      },
    },
    {
      title: 'Tiến độ học tập',
      dataIndex: 'progressPercentage',
      key: 'progressPercentage',
      width: 200,
      render: (progress: number | null) => {
        const safeProgress = progress || 0; 
        return (
          <Progress percent={safeProgress} size="small" status={safeProgress === 100 ? 'success' : 'active'} className="m-0" />
        );
      },
    },
    {
      title: 'Ngày ghi danh',
      dataIndex: 'enrolledAt',
      key: 'enrolledAt',
      render: (date: string) => date ? new Date(date).toLocaleString('vi-VN') : '-',
    },
    {
      title: 'Ngày hoàn thành',
      dataIndex: 'completedAt',
      key: 'completedAt',
      render: (date: string) => date ? <Text className="text-green-600 font-medium">{new Date(date).toLocaleString('vi-VN')}</Text> : '-',
    },
    {
      title: 'Thao tác',
      key: 'action',
      width: 120,
      align: 'center' as const,
      render: (_: any, record: StudentEnrollmentDetail) => {
        const isCancelled = record.status === 'CANCELLED';
        return (
          <Popconfirm
            title="Xác nhận Hủy ghi danh?"
            description={`Bạn có chắc muốn hủy ghi danh của học viên ${record.studentName || record.studentId}?`}
            onConfirm={() => handleCancel(record.enrollmentId)}
            okText="Hủy ghi danh"
            cancelText="Đóng"
            okButtonProps={{ danger: true }}
            disabled={isCancelled}
            placement="left"
          >
            <Button 
              danger 
              size="small"
              icon={<StopOutlined />} 
              disabled={isCancelled}
            >
              Cancel
            </Button>
          </Popconfirm>
        );
      },
    },
  ];

  return (
    <div className="mx-auto max-w-7xl p-4 md:p-6 lg:p-8">
      {contextHolder}
      
      <Button 
        type="text" 
        icon={<ArrowLeftOutlined />} 
        onClick={() => navigate('/enrollments')} 
        className="mb-6 hover:bg-gray-100"
      >
        Quay lại Tổng quan
      </Button>

      <div className="mb-6 flex flex-col gap-2">
        <Title level={2} className="mb-0 text-2xl font-bold text-gray-800">Chi tiết Khóa học #{courseId}</Title>
        <Text className="text-gray-500">Danh sách học viên và tiến độ học tập chi tiết.</Text>
      </div>

      <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
        <Table
          dataSource={enrollments}
          columns={columns}
          rowKey="enrollmentId"
          loading={loading}
          pagination={{ 
            pageSize: 20, 
            total: totalRecords,
            showTotal: (total) => <span className="font-medium text-gray-500">Tổng cộng {total} ghi danh</span> 
          }}
          scroll={{ x: 1000 }}
          locale={{ emptyText: 'Chưa có học viên nào ghi danh khóa học này.' }}
        />
      </div>
    </div>
  );
};

export default CourseEnrollmentDetailPage;