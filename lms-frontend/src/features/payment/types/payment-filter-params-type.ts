import type { FilterParams } from '@/shared/types/filter-params-type';
import type { PaymentMethodType } from './payment-type';

export interface PaymentFilterParams extends FilterParams {
  invoiceId?: string;
  paymentMethod?: PaymentMethodType;
  paidAtFrom?: string;
  paidAtTo?: string;
}
