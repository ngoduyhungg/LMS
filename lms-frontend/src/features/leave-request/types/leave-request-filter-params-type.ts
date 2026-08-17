import type { FilterParams } from '@/shared/types/filter-params-type';

export interface LeaveRequestFilterParams extends FilterParams {
  courseClassId?: string;
  studentId?: string;
}
