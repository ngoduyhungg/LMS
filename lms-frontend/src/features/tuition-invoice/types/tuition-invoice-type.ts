export interface TuitionInvoice {
  id: string;

  invoiceCode: string;

  studentId: string;
  studentName: string | null;

  courseClassId: string;
  courseClassName: string;

  courseId: string;
  courseName: string;

  originalAmount: string;
  discountAmount: string;
  finalAmount: string;
  amountPaid: string;
  balanceAmount: string;

  promotionId: string | null;

  dueDate: string | null;

  note: string | null;

  status: TuitionInvoiceStatusType;
  statusText?: string;

  createdAt: string;
  updatedAt: string;
}

export const TuitionInvoiceStatus = {
  UNPAID: 'UNPAID', // chưa trả
  PARTIAL: 'PARTIAL', // trả 1 phần
  PAID: 'PAID', // đủ
} as const;

export type TuitionInvoiceStatusType =
  (typeof TuitionInvoiceStatus)[keyof typeof TuitionInvoiceStatus];
