import { TuitionInvoiceStatus } from '../types/tuition-invoice-type';

export const tuitionInvoiceStatusOptions = [
  { label: 'Chưa thanh toán', value: TuitionInvoiceStatus.UNPAID },
  { label: 'Đã thanh toán một phần', value: TuitionInvoiceStatus.PARTIAL },
  { label: 'Đã thanh toán', value: TuitionInvoiceStatus.PAID },
  { label: 'Đã thanh toán vượt mức', value: TuitionInvoiceStatus.OVERPAID },
];
