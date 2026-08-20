import useTable from '@/shared/hooks/useTable';
import PageHeader from '@/shared/components/page/PageHeader';
import { Button } from 'antd';
import ModalFormCustom from '@/shared/components/modal/ModalFormCustom';
import { useFormModal } from '@/shared/hooks/useFormModal';
import { FormModalMode } from '@/shared/types/form-modal-mode-type';
import FilterTableCustom from '@/shared/components/table/FilterTableCustom';
import { generalInfoFormFields } from '@/features/users/constants/general-info-form-fields';
import { teacherRoleAdminApi } from '../api/teacher-api';
import type { Teacher } from '../types/teacher-type';
import type { TeacherFilterParams } from '../types/teacher-filter-params-type';
import { teacherFormFields } from '../constants/teacher-form-fields';
import { teacherFilters } from '../constants/teacher-filter-table';
import {
  EyeOutlined,
  EditOutlined,
  DeleteOutlined,
  CheckOutlined,
  CloseOutlined,
} from '@ant-design/icons';
import ActionGroup from '@/shared/components/table/ActionGroup';
import TablePaginationCustom from '@/shared/components/table/TablePaginationCustom';
import TeacherStatusTag from '../components/TeacherStatusTag';
import { TEACHER_STATUS } from '../types/teacher-status-type';
import type { SectionForm } from '@/shared/components/modal/ModalFormCustom';

const TeacherPage = () => {
  const { getAll, create, update, active, paused, remove } = teacherRoleAdminApi;

  const { open, mode, selectedRecord, openCreate, openView, openEdit, close } =
    useFormModal<Teacher>();

  const {
    data: teachers,

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
  } = useTable<Teacher, TeacherFilterParams>({
    fetchApi: getAll,
    removeApi: remove,
    activeApi: active,
    inActiveApi: paused,
  });

  const sectionsTeacherForm: SectionForm[] = [
    {
      key: 'general',
      label: 'Thông tin chung',
      fields: generalInfoFormFields,
    },
    {
      key: 'teacher',
      label: 'Thông tin giáo viên',
      fields: teacherFormFields,
    },
  ];

  const columns = [
    {
      title: 'Mã giáo viên',
      dataIndex: 'teacherCode',
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
      title: 'Vai trò',
      dataIndex: 'teacherRoleText',
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
      title: 'Trạng thái',
      dataIndex: 'status',
      align: 'center' as const,
      render: (_: any, record: any) => {
        return (
          <TeacherStatusTag status={record.teacherStatus} statusText={record.teacherStatusText} />
        );
      },
    },

    {
      title: 'Tác vụ',
      align: 'center' as const,
      render: (_: any, record: Teacher) => {
        return (
          <ActionGroup<Teacher>
            record={record}
            actions={[
              {
                show: () => true,
                icon: <EyeOutlined />,
                tooltip: 'Chi tiết',
                onClick: openView,
              },
              {
                show: (r) => r.teacherStatus !== TEACHER_STATUS.INACTIVE,
                icon: <EditOutlined />,
                tooltip: 'Sửa',
                onClick: openEdit,
              },

              {
                show: (r) => r.teacherStatus === TEACHER_STATUS.ACTIVE,
                icon: <CloseOutlined />,
                tooltip: 'Tạm nghỉ',
                danger: true,
                onClick: () => handleInActive(record.id),
                isPopconfirm: true,
              },

              {
                show: (r) => r.teacherStatus === TEACHER_STATUS.PAUSED,
                icon: <CheckOutlined />,
                tooltip: 'Hoạt động',
                color: '#52c41a',
                onClick: () => handleActive(record.id),
                isPopconfirm: true,
              },

              {
                show: (r) => r.teacherStatus === TEACHER_STATUS.PAUSED,
                icon: <DeleteOutlined />,
                tooltip: 'Ngừng công tác',
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
        title="Quản lý giáo viên"
        subtitle="Danh sách giáo viên"
        extra={
          <Button type="primary" onClick={openCreate}>
            + Thêm giáo viên
          </Button>
        }
      />

      <div className="mb-4">
        <FilterTableCustom
          dataFilters={teacherFilters}
          values={filterValues}
          onChange={handleFilterChange}
          onReset={handleFilterReset}
          onSubmit={handleFilterSubmit}
        />
      </div>

      <TablePaginationCustom<Teacher>
        columns={columns}
        data={teachers}
        loading={loading}
        pagination={pagination}
        onChangePage={handleChangePage}
      />

      <ModalFormCustom<Teacher>
        open={open}
        title="Giáo Viên"
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
        sections={sectionsTeacherForm}
      />
    </div>
  );
};

export default TeacherPage;
