import type { FilterParams } from '@/shared/types/filter-params-type';

export interface EnrollmentFilterParams extends FilterParams {
  courseClassId?: string;
  studentId?: string;
}
