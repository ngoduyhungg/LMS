import useTable from '@/shared/hooks/useTable';
import PageHeader from '@/shared/components/page/PageHeader';
import { Button } from 'antd';
import ModalFormCustom from '@/shared/components/modal/ModalFormCustom';
import { useFormModal } from '@/shared/hooks/useFormModal';
import { FormModalMode } from '@/shared/types/form-modal-mode-type';
import FilterTableCustom from '@/shared/components/table/FilterTableCustom';
import TablePaginationCustom from '@/shared/components/table/TablePaginationCustom';
import ActionGroup from '@/shared/components/table/ActionGroup';
import { EyeOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import type { SectionForm } from '@/shared/components/modal/ModalFormCustom';
import { courseClassRoleAdminApi } from '../api/course-class-api';
import { CourseClassStatus, type CourseClass } from '../types/course-class-type';
import { courseClassFormFields } from '../constants/course-class-form-fields';
import type { CourseClassFilterParams } from '../types/course-class-filter-params-type';
import CourseClassStatusTag from '../components/CourseClassStatusTag';
import { courseClassFilters } from '../constants/course-class-filter-table';
import { formatDate } from '@/shared/utils/date';
import { courseFormFields } from '@/features/courses/constants/course-form-fields';

const CourseClassPage = () => {
  const { getAll, create, update, remove } = courseClassRoleAdminApi;

  const { open, mode, selectedRecord, openCreate, openView, openEdit, close } =
    useFormModal<CourseClass>();

  const {
    data: courseClasses,

    loading,

    pagination,

    filterValues,

    handleFilterChange,

    handleFilterSubmit,

    handleFilterReset,

    handleChangePage,

    handleDelete,

    refetch,
  } = useTable<CourseClass, CourseClassFilterParams>({
    fetchApi: getAll,
    removeApi: remove,
  });

  const sectionsCourseClassForm: SectionForm[] = [
    {
      key: 'courseClass',
      label: 'Thông tin lớp học',
      fields: courseClassFormFields,
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
      title: 'Mã lớp học',
      dataIndex: 'classCode',
    },
    {
      title: 'Tên lớp học',
      dataIndex: 'name',
    },
    {
      title: 'Tên khóa học',
      dataIndex: 'courseName',
    },
    {
      title: 'Giáo viên chính',
      dataIndex: 'mainTeacherName',
    },
    {
      title: 'Phòng học',
      dataIndex: 'roomName',
    },
    {
      title: 'Ngày bắt đầu',
      dataIndex: 'startDate',
      align: 'center' as const,
      render: (value: string) => formatDate(value),
    },
    {
      title: 'Ngày kết thúc',
      dataIndex: 'endDate',
      align: 'center' as const,
      render: (value: string) => formatDate(value),
    },
    {
      title: 'Lịch học',
      dataIndex: 'scheduleInformation',
      render: (value: string[]) => (
        <div className="flex flex-col">
          {value?.map((item) => (
            <span key={item}>{item}</span>
          ))}
        </div>
      ),
    },
    {
      title: 'Trạng thái',
      dataIndex: 'status',
      align: 'center' as const,
      render: (_: any, record: CourseClass) => {
        return <CourseClassStatusTag status={record.status} statusText={record.statusText} />;
      },
    },
    {
      title: 'Tác vụ',
      align: 'center' as const,
      render: (_: any, record: CourseClass) => {
        return (
          <ActionGroup<CourseClass>
            record={record}
            actions={[
              {
                show: () => true,
                icon: <EyeOutlined />,
                tooltip: 'Chi tiết',
                onClick: openView,
              },
              {
                show: (r) => r.status !== CourseClassStatus.CLOSED,
                icon: <EditOutlined />,
                tooltip: 'Sửa',
                onClick: openEdit,
              },
              {
                show: (r) => r.status === CourseClassStatus.OPEN,
                icon: <DeleteOutlined />,
                tooltip: 'Xóa',
                danger: true,
                onClick: () => handleDelete(record.id),
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
      <PageHeader
        title="Quản lý lớp học"
        subtitle="Danh sách lớp học"
        extra={
          <Button type="primary" onClick={openCreate}>
            + Thêm lớp học
          </Button>
        }
      />

      <div className="mb-4">
        <FilterTableCustom
          dataFilters={courseClassFilters}
          values={filterValues}
          onChange={handleFilterChange}
          onReset={handleFilterReset}
          onSubmit={handleFilterSubmit}
        />
      </div>

      <TablePaginationCustom<CourseClass>
        columns={columns}
        data={courseClasses}
        loading={loading}
        pagination={pagination}
        onChangePage={handleChangePage}
      />

      <ModalFormCustom<CourseClass>
        open={open}
        title="Lớp học"
        mode={mode}
        initialValues={selectedRecord}
        disabled={mode === FormModalMode.VIEW}
        onCancel={close}
        onSuccess={refetch}
        onSubmit={
          mode === FormModalMode.CREATE ? create : (values) => update(selectedRecord!.id, values)
        }
        sections={sectionsCourseClassForm}
      />
    </div>
  );
};

export default CourseClassPage;
