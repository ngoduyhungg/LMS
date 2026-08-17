import type { FilterParams } from '@/shared/types/filter-params-type';

export interface CourseFilterParams extends FilterParams {
  level?: string;
}
