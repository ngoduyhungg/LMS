import type { FilterParams } from '@/shared/types/filter-params-type';

export interface ScheduleFilterParams extends FilterParams {
  weekday?: number;
  startTime?: string;
  endTime?: string;
}
