import { PaymentStatus } from '../types/payment-type';

export const paymentStatusOptions = [
  { label: 'Thành công', value: PaymentStatus.SUCCESS },
  { label: 'Đã hủy', value: PaymentStatus.CANCELLED },
];
