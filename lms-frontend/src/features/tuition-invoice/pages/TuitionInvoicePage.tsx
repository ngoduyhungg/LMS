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
import { formatDate } from '@/shared/utils/date';
import {
  tuitionInvoiceRoleAdminApi,
  tuitionInvoiceRoleStudentApi,
} from '../api/tuition-invoice-api';
import { TuitionInvoiceStatus, type TuitionInvoice } from '../types/tuition-invoice-type';
import { tuitionInvoiceFormFields } from '../constants/tuition-invoice-form-fields';
import type { TuitionInvoiceFilterParams } from '../types/tuition-invoice-filter-params-type';
import TuitionInvoiceStatusTag from '../components/TuitionInvoiceStatusTag';
import { tuitionInvoiceFilters } from '../constants/tuition-invoice-filter-table';
import { useAppSelector } from '@/app/redux/hooks';
import { USER_ROLE } from '@/features/users/types/user-role-type';
import { formatCurrency } from '@/shared/utils/currecy';

const TuitionInvoicePage = () => {
  const { getAll, create, update, remove } = tuitionInvoiceRoleAdminApi;
  const { getAll: getAllStudent } = tuitionInvoiceRoleStudentApi;

  const { user } = useAppSelector((state) => state.auth);

  const isAdmin = user?.role === USER_ROLE.ADMIN;

  const { open, mode, selectedRecord, openCreate, openView, openEdit, close } =
    useFormModal<TuitionInvoice>();

  const {
    data: tuitionInvoices,

    loading,

    pagination,

    filterValues,

    handleFilterChange,

    handleFilterSubmit,

    handleFilterReset,

    handleChangePage,

    handleDelete,

    refetch,
  } = useTable<TuitionInvoice, TuitionInvoiceFilterParams>({
    fetchApi: isAdmin ? getAll : getAllStudent,
    removeApi: remove,
  });

  const sectionsTuitionInvoiceForm: SectionForm[] = [
    {
      key: 'tuitionInvoice',
      label: 'Thông tin hóa đơn',
      fields: tuitionInvoiceFormFields,
    },
  ];

  const columns = [
    {
      title: 'Mã hóa đơn',
      dataIndex: 'invoiceCode',
    },
    {
      title: 'Tên học sinh',
      dataIndex: 'studentName',
    },
    {
      title: 'Tên lớp học',
      dataIndex: 'courseClassName',
    },
    {
      title: 'Tổng tiền',
      dataIndex: 'finalAmount',
      align: 'right' as const,
      render: (value: string) => formatCurrency(value),
    },
    {
      title: 'Đã thanh toán',
      dataIndex: 'amountPaid',
      align: 'right' as const,
      render: (value: string) => formatCurrency(value),
    },
    {
      title: 'Còn lại',
      dataIndex: 'balanceAmount',
      align: 'right' as const,
      render: (value: string) => formatCurrency(value),
    },
    {
      title: 'Ngày đến hạn',
      dataIndex: 'dueDate',
      align: 'center' as const,
      render: (value: string) => formatDate(value),
    },
    {
      title: 'Trạng thái',
      dataIndex: 'status',
      align: 'center' as const,
      render: (_: any, record: TuitionInvoice) => {
        return <TuitionInvoiceStatusTag status={record.status} statusText={record.statusText} />;
      },
    },
    {
      title: 'Tác vụ',
      align: 'center' as const,
      render: (_: any, record: TuitionInvoice) => {
        return (
          <ActionGroup<TuitionInvoice>
            record={record}
            actions={[
              {
                show: () => true,
                icon: <EyeOutlined />,
                tooltip: 'Chi tiết',
                onClick: openView,
              },
              {
                show: (r) =>
                  user?.role === USER_ROLE.ADMIN && r.status === TuitionInvoiceStatus.UNPAID,
                icon: <EditOutlined />,
                tooltip: 'Sửa',
                onClick: openEdit,
              },
              {
                show: (r) =>
                  user?.role === USER_ROLE.ADMIN && r.status === TuitionInvoiceStatus.UNPAID,
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
        title="Quản lý hóa đơn học phí"
        subtitle="Danh sách hóa đơn học phí"
        extra={
          <Button type="primary" onClick={openCreate}>
            + Thêm hóa đơn học phí
          </Button>
        }
      />

      <div className="mb-4">
        <FilterTableCustom
          dataFilters={tuitionInvoiceFilters}
          values={filterValues}
          onChange={handleFilterChange}
          onReset={handleFilterReset}
          onSubmit={handleFilterSubmit}
        />
      </div>

      <TablePaginationCustom<TuitionInvoice>
        columns={columns}
        data={tuitionInvoices}
        loading={loading}
        pagination={pagination}
        onChangePage={handleChangePage}
      />

      <ModalFormCustom<TuitionInvoice>
        open={open}
        title="Hóa đơn học phí"
        mode={mode}
        initialValues={selectedRecord}
        disabled={mode === FormModalMode.VIEW}
        onCancel={close}
        onSuccess={refetch}
        onSubmit={
          mode === FormModalMode.CREATE ? create : (values) => update(selectedRecord!.id, values)
        }
        sections={sectionsTuitionInvoiceForm}
      />
    </div>
  );
};

export default TuitionInvoicePage;
