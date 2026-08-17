import type { FilterParams } from '@/shared/types/filter-params-type';

export interface TuitionInvoiceFilterParams extends FilterParams {
  courseClassId?: string;
  studentId?: string;
  promotionId?: string;
  dueDateFrom?: string;
  dueDateTo?: string;
}
