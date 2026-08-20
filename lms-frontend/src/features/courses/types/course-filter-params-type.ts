import type { FilterParams } from '@/shared/types/filter-params-type';

export interface CourseFilterParams extends FilterParams {
  title?: string;
  level?: string;
  categoryId?: string;
  status?: string;
}
