import { useState } from 'react';

import useTable from '@/shared/hooks/useTable';
import PageHeader from '@/shared/components/page/PageHeader';
import ModalFormCustom from '@/shared/components/modal/ModalFormCustom';
import { useFormModal } from '@/shared/hooks/useFormModal';
import { FormModalMode } from '@/shared/types/form-modal-mode-type';
import FilterTableCustom from '@/shared/components/table/FilterTableCustom';
import TablePaginationCustom from '@/shared/components/table/TablePaginationCustom';
import ActionGroup from '@/shared/components/table/ActionGroup';
import { CheckOutlined, EyeOutlined, DeleteOutlined, FormOutlined } from '@ant-design/icons';
import type { SectionForm } from '@/shared/components/modal/ModalFormCustom';
import { courseClassSessionRoleAdminApi } from '../api/course-class-session-api';
import {
  CourseClassSessionStatus,
  type CourseClassSession,
} from '../types/course-class-session-type';
import CourseClassSessionStatusTag from '../components/CourseClassSessionStatusTag';
import type { CourseClassSessionFilterParams } from '../types/course-class-session-filter-params-type';
import { courseClassSessionFilters } from '../constants/course-class-filter-table';
import { courseClassSessionFormFields } from '../constants/course-class-form-fields';
import AttendanceModal from '../components/AttendanceModal';
import { mappedTimeInformation } from '../utils';
import { courseFormFields } from '@/features/courses/constants/course-form-fields';

const CourseClassSessionPage = () => {
  const { getAll, done, cancel } = courseClassSessionRoleAdminApi;

  const { open, mode, selectedRecord, openView, close } = useFormModal<CourseClassSession>();

  const {
    data: courseClassSessions,

    loading,

    pagination,

    filterValues,

    handleFilterChange,

    handleFilterSubmit,

    handleFilterReset,

    handleChangePage,

    handleActive,

    handleInActive,

    refetch,
  } = useTable<CourseClassSession, CourseClassSessionFilterParams>({
    fetchApi: getAll,
    activeApi: done,
    inActiveApi: cancel,
  });

  const [openAttendance, setOpenAttendance] = useState(false);

  const [selectedSession, setSelectedSession] = useState<CourseClassSession | null>(null);

  const handleAttendance = (session: CourseClassSession) => {
    setSelectedSession(session);
    setOpenAttendance(true);
  };

  const sectionsCourseClassSessionForm: SectionForm[] = [
    {
      key: 'courseClassSession',
      label: 'Thông tin lịch học',
      fields: courseClassSessionFormFields,
    },
    {
      key: 'class',
      label: 'Thông tin khóa học',
      fields: courseFormFields,
      isDisabled: true,
      isHidden: ({ dataForm }: { dataForm: any }) => dataForm && !dataForm.courseId,
    },
  ];

  const columns = [
    {
      title: 'Tên khóa học',
      dataIndex: 'courseName',
    },
    {
      title: 'Tên lớp học',
      dataIndex: 'courseClassName',
    },
    {
      title: 'Giáo viên chính',
      dataIndex: 'mainTeacherName',
    },
    {
      title: 'Thời gian',
      dataIndex: 'time',
      align: 'center' as const,
      render: (_: string, record: CourseClassSession) => (
        <span>
          {mappedTimeInformation({
            scheduleSlotName: record.scheduleSlotName,
            startTime: record.startTime,
            endTime: record.endTime,
          })}
        </span>
      ),
    },
    {
      title: 'Trạng thái',
      dataIndex: 'status',
      align: 'center' as const,
      render: (_: any, record: CourseClassSession) => {
        return (
          <CourseClassSessionStatusTag status={record.status} statusText={record.statusText} />
        );
      },
    },
    {
      title: 'Tác vụ',
      align: 'center' as const,
      render: (_: any, record: CourseClassSession) => {
        return (
          <ActionGroup<CourseClassSession>
            record={record}
            actions={[
              {
                show: () => true,
                icon: <EyeOutlined />,
                tooltip: 'Chi tiết',
                onClick: openView,
              },
              {
                show: (r) => r.status === CourseClassSessionStatus.SCHEDULED,

                icon: <FormOutlined />,

                tooltip: 'Điểm danh',

                color: '#1677ff',

                onClick: handleAttendance,
              },
              {
                show: (r) => r.status === CourseClassSessionStatus.SCHEDULED,
                icon: <CheckOutlined />,
                tooltip: 'Hoàn thành',
                color: '#52c41a',
                onClick: () => handleActive(record.id),
                isPopconfirm: true,
              },
              {
                show: (r) => r.status === CourseClassSessionStatus.SCHEDULED,
                icon: <DeleteOutlined />,
                tooltip: 'Hủy',
                danger: true,
                onClick: () => handleInActive(record.id),
                isPopconfirm: true,
              },
            ]}
          />
        );
      },
    },
  ];

  return (
    <div className="flex flex-col h-full">
      <PageHeader title="Quản lý lịch học" subtitle="Danh sách lịch học" />

      <div className="mb-4">
        <FilterTableCustom
          dataFilters={courseClassSessionFilters}
          values={filterValues}
          onChange={handleFilterChange}
          onReset={handleFilterReset}
          onSubmit={handleFilterSubmit}
        />
      </div>

      <TablePaginationCustom<CourseClassSession>
        columns={columns}
        data={courseClassSessions}
        loading={loading}
        pagination={pagination}
        onChangePage={handleChangePage}
      />

      <ModalFormCustom<CourseClassSession>
        open={open}
        title="Lịch học"
        mode={mode}
        initialValues={selectedRecord}
        disabled={mode === FormModalMode.VIEW}
        onCancel={close}
        onSuccess={refetch}
        sections={sectionsCourseClassSessionForm}
      />

      <AttendanceModal
        open={openAttendance}
        sessionId={selectedSession?.id}
        onCancel={() => {
          setOpenAttendance(false);
          setSelectedSession(null);
        }}
        onSuccess={() => {
          refetch();
        }}
      />
    </div>
  );
};

export default CourseClassSessionPage;
