import type { FilterParams } from '@/shared/types/filter-params-type';

export interface CourseClassFilterParams extends FilterParams {
  courseId?: string;
  mainTeacherId?: string;
  roomId?: string;
  scheduleSlotId?: string;
  startDate?: string;
  endDate?: string;
}
