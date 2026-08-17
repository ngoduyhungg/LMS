import { FormFieldType } from '@/shared/types/form-field-type';
import { paymentStatusOptions } from './payment-status-options';
import { paymentMethodOptions } from './payment-method-options';
import { getTuitionInvoiceOptions } from '@/features/tuition-invoice/api/tuition-invoice-api';

export const paymentFilters = [
  {
    name: 'invoiceId',
    type: FormFieldType.SelectFetch,
    fetchOptions: getTuitionInvoiceOptions,
    placeholder: 'Chọn hóa đơn',
  },
  {
    name: 'paymentMethod',
    type: FormFieldType.Select,
    options: paymentMethodOptions,
    placeholder: 'Chọn phương thức thanh toán',
  },
  {
    name: 'paidAtFrom',
    label: 'Ngày bắt đầu',
    type: FormFieldType.DatePicker,
    placeholder: 'Từ ngày',
  },
  {
    name: 'paidAtTo',
    label: 'Ngày kết thúc',
    type: FormFieldType.DatePicker,
    placeholder: 'Đến ngày',
  },
  {
    name: 'status',
    type: FormFieldType.Select,
    placeholder: 'Trạng thái',
    options: paymentStatusOptions,
  },
];
