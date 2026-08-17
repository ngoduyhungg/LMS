import useTable from '@/shared/hooks/useTable';
import PageHeader from '@/shared/components/page/PageHeader';
import { Button } from 'antd';
import ModalFormCustom from '@/shared/components/modal/ModalFormCustom';
import { useFormModal } from '@/shared/hooks/useFormModal';
import { FormModalMode } from '@/shared/types/form-modal-mode-type';
import FilterTableCustom from '@/shared/components/table/FilterTableCustom';
import TablePaginationCustom from '@/shared/components/table/TablePaginationCustom';
import ActionGroup from '@/shared/components/table/ActionGroup';
import { EyeOutlined } from '@ant-design/icons';
import type { SectionForm } from '@/shared/components/modal/ModalFormCustom';
import { formatDate } from '@/shared/utils/date';
import type { Payment } from '../types/payment-type';
import { paymentRoleAdminApi } from '../api/payment-api';
import type { PaymentFilterParams } from '../types/payment-filter-params-type';
import PaymentStatusTag from '../components/PaymentStatusTag';
import { paymentFormFields } from '../constants/payment-form-fields';
import { formatCurrency } from '@/shared/utils/currecy';
import { paymentFilters } from '../constants/payment-filter-table';

const PaymentPage = () => {
  const { getAll, create } = paymentRoleAdminApi;

  const { open, mode, selectedRecord, openCreate, openView, close } = useFormModal<Payment>();

  const {
    data: payments,

    loading,

    pagination,

    filterValues,

    handleFilterChange,

    handleFilterSubmit,

    handleFilterReset,

    handleChangePage,

    refetch,
  } = useTable<Payment, PaymentFilterParams>({
    fetchApi: getAll,
  });

  const sectionsPaymentForm: SectionForm[] = [
    {
      key: 'payment',
      label: 'Thông tin thanh toán',
      fields: paymentFormFields,
    },
  ];

  const columns = [
    {
      title: 'Mã thanh toán',
      dataIndex: 'paymentCode',
    },
    {
      title: 'Số tiền thanh toán',
      dataIndex: 'paidAmount',
      align: 'right' as const,
      render: (value: number) => formatCurrency(value),
    },
    {
      title: 'Phương thức thanh toán',
      dataIndex: 'paymentMethod',
    },
    {
      title: 'Mã hóa đơn',
      dataIndex: 'invoiceCode',
    },
    {
      title: 'Tên học sinh',
      dataIndex: 'studentName',
    },
    {
      title: 'Người thu',
      dataIndex: 'cashierUserName',
    },
    {
      title: 'Ngày thanh toán',
      dataIndex: 'paidAt',
      align: 'center' as const,
      render: (value: string) => formatDate(value),
    },
    {
      title: 'Trạng thái',
      dataIndex: 'status',
      align: 'center' as const,
      render: (_: any, record: Payment) => {
        return <PaymentStatusTag status={record.status} statusText={record.statusText} />;
      },
    },
    {
      title: 'Tác vụ',
      align: 'center' as const,
      render: (_: any, record: Payment) => {
        return (
          <ActionGroup<Payment>
            record={record}
            actions={[
              {
                show: () => true,
                icon: <EyeOutlined />,
                tooltip: 'Chi tiết',
                onClick: openView,
              },
              // {
              //   show: (r) =>
              //     user?.role === USER_ROLE.ADMIN && r.status === TuitionInvoiceStatus.UNPAID,
              //   icon: <EditOutlined />,
              //   tooltip: 'Sửa',
              //   onClick: openEdit,
              // },
              // {
              //   show: (r) =>
              //     user?.role === USER_ROLE.ADMIN && r.status === TuitionInvoiceStatus.UNPAID,
              //   icon: <DeleteOutlined />,
              //   tooltip: 'Xóa',
              //   danger: true,
              //   onClick: () => handleDelete(record.id),
              //   isPopconfirm: true,
              // },
            ]}
          />
        );
      },
    },
  ];

  return (
    <div className="flex flex-col h-full">
      <PageHeader
        title="Quản lý thanh toán"
        subtitle="Danh sách thanh toán"
        extra={
          <Button type="primary" onClick={openCreate}>
            + Thanh toán
          </Button>
        }
      />

      <div className="mb-4">
        <FilterTableCustom
          dataFilters={paymentFilters}
          values={filterValues}
          onChange={handleFilterChange}
          onReset={handleFilterReset}
          onSubmit={handleFilterSubmit}
        />
      </div>

      <TablePaginationCustom<Payment>
        columns={columns}
        data={payments}
        loading={loading}
        pagination={pagination}
        onChangePage={handleChangePage}
      />

      <ModalFormCustom<Payment>
        open={open}
        title="Thanh toán"
        mode={mode}
        initialValues={selectedRecord}
        disabled={mode === FormModalMode.VIEW}
        onCancel={close}
        onSuccess={refetch}
        onSubmit={mode === FormModalMode.CREATE ? create : undefined}
        sections={sectionsPaymentForm}
      />
    </div>
  );
};

export default PaymentPage;
