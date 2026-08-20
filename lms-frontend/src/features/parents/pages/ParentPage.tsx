import useTable from '@/shared/hooks/useTable';
import PageHeader from '@/shared/components/page/PageHeader';
import { Button } from 'antd';
import ModalFormCustom from '@/shared/components/modal/ModalFormCustom';
import { useFormModal } from '@/shared/hooks/useFormModal';
import { FormModalMode } from '@/shared/types/form-modal-mode-type';
import FilterTableCustom from '@/shared/components/table/FilterTableCustom';
import { generalInfoFormFields } from '@/features/users/constants/general-info-form-fields';
import TablePaginationCustom from '@/shared/components/table/TablePaginationCustom';
import type { SectionForm } from '@/shared/components/modal/ModalFormCustom';
import { parentRoleAdminApi } from '../api/parent-api';
import type { Parent } from '../types/parent-type';
import type { ParentFilterParams } from '../types/parent-filter-params-type';
import { parentFilters } from '../constants/parent-filter-table';
import ActionGroup from '@/shared/components/table/ActionGroup';
import { EyeOutlined, EditOutlined } from '@ant-design/icons';

const ParentPage = () => {
  const { getAll, create, update } = parentRoleAdminApi;

  const { open, mode, selectedRecord, openCreate, openView, openEdit, close } =
    useFormModal<Parent>();

  const {
    data: parents,

    loading,

    pagination,

    filterValues,

    handleFilterChange,

    handleFilterSubmit,

    handleFilterReset,

    handleChangePage,

    refetch,
  } = useTable<Parent, ParentFilterParams>({
    fetchApi: getAll,
  });

  const sectionsParentForm: SectionForm[] = [
    {
      key: 'general',
      label: 'Thông tin chung',
      fields: generalInfoFormFields,
    },
  ];

  const columns = [
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
      title: 'Tác vụ',
      align: 'center' as const,
      render: (_: any, record: Parent) => {
        return (
          <ActionGroup<Parent>
            record={record}
            actions={[
              {
                show: () => true,
                icon: <EyeOutlined />,
                tooltip: 'Chi tiết',
                onClick: openView,
              },
              {
                show: () => true,
                icon: <EditOutlined />,
                tooltip: 'Sửa',
                onClick: openEdit,
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
        title="Quản lý phụ huynh"
        subtitle="Danh sách phụ huynh của học sinh"
        extra={
          <Button type="primary" onClick={openCreate}>
            + Thêm phụ huynh
          </Button>
        }
      />

      <div className="mb-4">
        <FilterTableCustom
          dataFilters={parentFilters}
          values={filterValues}
          onChange={handleFilterChange}
          onReset={handleFilterReset}
          onSubmit={handleFilterSubmit}
        />
      </div>

      <TablePaginationCustom<Parent>
        columns={columns}
        data={parents}
        loading={loading}
        pagination={pagination}
        onChangePage={handleChangePage}
      />

      <ModalFormCustom<Parent>
        open={open}
        title="Phụ Huynh"
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
        sections={sectionsParentForm}
      />
    </div>
  );
};

export default ParentPage;
