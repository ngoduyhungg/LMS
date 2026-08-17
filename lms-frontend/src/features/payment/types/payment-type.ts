import type { TuitionInvoice } from '@/features/tuition-invoice/types/tuition-invoice-type';

export interface Payment extends Pick<TuitionInvoice, 'amountPaid' | 'balanceAmount'> {
  id: string;

  paymentCode: string;

  paidAmount: number;

  invoiceId: string;
  invoiceCode: string;

  studentId: string;
  studentName: string;

  paymentMethod: PaymentMethodType;

  cashierUserId: string;
  cashierUserName: string;

  paidAt: string | null;

  note: string | null;

  status: PaymentStatusType;
  statusText?: string;

  createdAt: string;
  updatedAt: string;
}

export const PaymentStatus = {
  SUCCESS: 'SUCCESS', // thành công
  CANCELLED: 'CANCELLED', // hủy
} as const;

export type PaymentStatusType = (typeof PaymentStatus)[keyof typeof PaymentStatus];

export const PaymentMethod = {
  CASH: 'CASH', // tiền mặt
  BANK_TRANSFER: 'BANK_TRANSFER', // chuyển khoản
} as const;

export type PaymentMethodType = (typeof PaymentMethod)[keyof typeof PaymentMethod];
