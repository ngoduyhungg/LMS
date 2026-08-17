import useTable from '@/shared/hooks/useTable';
import PageHeader from '@/shared/components/page/PageHeader';
import { Button } from 'antd';
import ModalFormCustom from '@/shared/components/modal/ModalFormCustom';
import { useFormModal } from '@/shared/hooks/useFormModal';
import { FormModalMode } from '@/shared/types/form-modal-mode-type';
import FilterTableCustom from '@/shared/components/table/FilterTableCustom';
import TablePaginationCustom from '@/shared/components/table/TablePaginationCustom';
import ActionGroup from '@/shared/components/table/ActionGroup';
import {
  EyeOutlined,
  EditOutlined,
  DeleteOutlined,
  CloseOutlined,
  CheckOutlined,
} from '@ant-design/icons';
import type { SectionForm } from '@/shared/components/modal/ModalFormCustom';
import { DiscountTypePromotion, PromotionStatus, type Promotion } from '../types/promotion-type';
import { promotionRoleAdminApi } from '../api/promotion-api';
import type { PromotionFilterParams } from '../types/promotion-filter-params-type';
import PromotionStatusTag from '../components/PromotionStatusTag';
import { promotionFormFields } from '../constants/promotion-form-fields';
import { promotionFilters } from '../constants/promotion-filter-table';
import { formatCurrency } from '@/shared/utils/currecy';
import { formatDate } from '@/shared/utils/date';

const PromotionPage = () => {
  const { getAll, create, update, active, inActive, remove } = promotionRoleAdminApi;

  const { open, mode, selectedRecord, openCreate, openView, openEdit, close } =
    useFormModal<Promotion>();

  const {
    data: promotions,

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
  } = useTable<Promotion, PromotionFilterParams>({
    fetchApi: getAll,
    removeApi: remove,
    activeApi: active,
    inActiveApi: inActive,
  });

  const sectionsPromotionForm: SectionForm[] = [
    {
      key: 'promotion',
      label: 'Thông tin khuyến mãi',
      fields: promotionFormFields,
    },
  ];

  const columns = [
    {
      title: 'Mã khuyến mãi',
      dataIndex: 'promoCode',
    },
    {
      title: 'Tên khuyến mãi',
      dataIndex: 'name',
    },
    {
      title: 'Loại khuyến mãi',
      dataIndex: 'discountType',
      render: (value: string) => {
        return value === DiscountTypePromotion.PERCENT ? 'Chiết khấu' : 'Tiền';
      },
    },
    {
      title: 'Giá trị khuyến mãi',
      dataIndex: 'discountValue',
      align: 'right' as const,
      render: (value: number, record: Promotion) => {
        return record.discountType === DiscountTypePromotion.PERCENT
          ? `${value}%`
          : `${formatCurrency(value)}`;
      },
    },
    {
      title: 'Thời gian áp dụng',
      align: 'center' as const,
      render: (_: any, record: Promotion) => {
        return `${formatDate(record.startDate)} - ${formatDate(record.endDate)}`;
      },
    },
    {
      title: 'Trạng thái',
      dataIndex: 'status',
      align: 'center' as const,
      render: (_: any, record: Promotion) => {
        return <PromotionStatusTag status={record.status} statusText={record.statusText} />;
      },
    },
    {
      title: 'Tác vụ',
      align: 'center' as const,
      render: (_: any, record: Promotion) => {
        return (
          <ActionGroup<Promotion>
            record={record}
            actions={[
              {
                show: () => true,
                icon: <EyeOutlined />,
                tooltip: 'Chi tiết',
                onClick: openView,
              },
              {
                show: (r) => r.status !== PromotionStatus.EXPIRED,
                icon: <EditOutlined />,
                tooltip: 'Sửa',
                onClick: openEdit,
              },
              {
                show: (r) => r.status === PromotionStatus.ACTIVE,
                icon: <CloseOutlined />,
                tooltip: 'Ngưng khuyến mãi',
                danger: true,
                onClick: () => handleInActive(record.id),
                isPopconfirm: true,
              },
              {
                show: (r) => r.status === PromotionStatus.INACTIVE,
                icon: <CheckOutlined />,
                tooltip: 'Kích hoạt khuyến mãi',
                color: '#52c41a',
                onClick: () => handleActive(record.id),
                isPopconfirm: true,
              },
              {
                show: (r) => r.status === PromotionStatus.INACTIVE,
                icon: <DeleteOutlined />,
                tooltip: 'Xóa khuyến mãi',
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
        title="Quản lý khuyến mãi"
        subtitle="Danh sách khuyến mãi"
        extra={
          <Button type="primary" onClick={openCreate}>
            + Thêm khuyến mãi
          </Button>
        }
      />

      <div className="mb-4">
        <FilterTableCustom
          dataFilters={promotionFilters}
          values={filterValues}
          onChange={handleFilterChange}
          onReset={handleFilterReset}
          onSubmit={handleFilterSubmit}
        />
      </div>

      <TablePaginationCustom<Promotion>
        columns={columns}
        data={promotions}
        loading={loading}
        pagination={pagination}
        onChangePage={handleChangePage}
      />

      <ModalFormCustom<Promotion>
        open={open}
        title="Khuyến mãi"
        mode={mode}
        initialValues={selectedRecord}
        disabled={mode === FormModalMode.VIEW}
        onCancel={close}
        onSuccess={refetch}
        onSubmit={
          mode === FormModalMode.CREATE ? create : (values) => update(selectedRecord!.id, values)
        }
        sections={sectionsPromotionForm}
      />
    </div>
  );
};

export default PromotionPage;
