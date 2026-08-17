import useTable from '@/shared/hooks/useTable';
import type { Student } from '../types/student-type';
import PageHeader from '@/shared/components/page/PageHeader';
import { Button } from 'antd';
import { studentFormFields } from '../constants/student-form-fields';
import ModalFormCustom from '@/shared/components/modal/ModalFormCustom';
import { studentRoleAdminApi } from '../api/student-api';
import { useFormModal } from '@/shared/hooks/useFormModal';
import { FormModalMode } from '@/shared/types/form-modal-mode-type';
import { studentFilters } from '../constants/student-filter-table';
import type { StudentFilterParams } from '../types/student-filter-params-type';
import FilterTableCustom from '@/shared/components/table/FilterTableCustom';
import { generalInfoFormFields } from '@/features/users/constants/general-info-form-fields';
import ActionGroup from '@/shared/components/table/ActionGroup';
import {
  EyeOutlined,
  EditOutlined,
  DeleteOutlined,
  CheckOutlined,
  CloseOutlined,
} from '@ant-design/icons';
import TablePaginationCustom from '@/shared/components/table/TablePaginationCustom';
import StudentStatusTag from '../components/StudentStatusTag';
import { STUDENT_STATUS } from '../types/student-status-type';
import type { SectionForm } from '@/shared/components/modal/ModalFormCustom';

const StudentPage = () => {
  const { getAll, create, update, active, paused, remove } = studentRoleAdminApi;

  const { open, mode, selectedRecord, openCreate, openView, openEdit, close } =
    useFormModal<Student>();

  const {
    data: students,

    loading,

    pagination,

    filterValues,

    handleFilterChange,

    handleFilterSubmit,

    handleFilterReset,

    handleChangePage,

    handleDelete,

    handleActive,

    handleInActive,

    refetch,
  } = useTable<Student, StudentFilterParams>({
    fetchApi: getAll,
    removeApi: remove,
    activeApi: active,
    inActiveApi: paused,
  });

  const sectionsStudentForm: SectionForm[] = [
    {
      key: 'general',
      label: 'Thông tin chung',
      fields: generalInfoFormFields,
    },
    {
      key: 'student',
      label: 'Thông tin học viên',
      fields: studentFormFields,
    },
  ];

  const columns = [
    {
      title: 'Mã học sinh',
      dataIndex: 'studentCode',
    },
    {
      title: 'Họ tên',
      dataIndex: 'fullName',
    },

    {
      title: 'Email',
      dataIndex: 'email',
    },

    {
      title: 'Số điện thoại',
      dataIndex: 'phone',
    },

    {
      title: 'Địa chỉ',
      dataIndex: 'address',
    },

    {
      title: 'Tên phụ huynh',
      dataIndex: 'parentName',
    },

    {
      title: 'Trạng thái',
      dataIndex: 'status',
      align: 'center' as const,
      render: (_: any, record: any) => {
        return (
          <StudentStatusTag status={record.studentStatus} statusText={record.studentStatusText} />
        );
      },
    },

    {
      title: 'Tác vụ',
      align: 'center' as const,
      render: (_: any, record: Student) => {
        return (
          <ActionGroup<Student>
            record={record}
            actions={[
              {
                show: () => true,
                icon: <EyeOutlined />,
                tooltip: 'Chi tiết',
                onClick: openView,
              },
              {
                show: (r) => r.studentStatus !== STUDENT_STATUS.DROPPED,
                icon: <EditOutlined />,
                tooltip: 'Sửa',
                onClick: openEdit,
              },

              {
                show: (r) => r.studentStatus === STUDENT_STATUS.ACTIVE,
                icon: <CloseOutlined />,
                tooltip: 'Tạm ngưng',
                danger: true,
                onClick: () => handleInActive(record.id),
                isPopconfirm: true,
              },

              {
                show: (r) => r.studentStatus === STUDENT_STATUS.PAUSED,
                icon: <CheckOutlined />,
                tooltip: 'Tham gia',
                color: '#52c41a',
                onClick: () => handleActive(record.id),
                isPopconfirm: true,
              },

              {
                show: (r) => r.studentStatus === STUDENT_STATUS.PAUSED,
                icon: <DeleteOutlined />,
                tooltip: 'Bỏ học',
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
        title="Quản lý học viên"
        subtitle="Danh sách học viên"
        extra={
          <Button type="primary" onClick={openCreate}>
            + Thêm học viên
          </Button>
        }
      />

      <div className="mb-4">
        <FilterTableCustom
          dataFilters={studentFilters}
          values={filterValues}
          onChange={handleFilterChange}
          onReset={handleFilterReset}
          onSubmit={handleFilterSubmit}
        />
      </div>

      <TablePaginationCustom<Student>
        columns={columns}
        data={students}
        loading={loading}
        pagination={pagination}
        onChangePage={handleChangePage}
      />

      <ModalFormCustom<Student>
        open={open}
        title="Học Viên"
        mode={mode}
        initialValues={selectedRecord}
        disabled={mode === FormModalMode.VIEW}
        onCancel={close}
        onSuccess={refetch}
        onSubmit={
          mode === FormModalMode.CREATE
            ? create
            : (values) => update(selectedRecord!.userId, values)
        }
        sections={sectionsStudentForm}
      />
    </div>
  );
};

export default StudentPage;
