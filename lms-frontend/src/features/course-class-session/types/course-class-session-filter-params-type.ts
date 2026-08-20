import type { FilterParams } from '@/shared/types/filter-params-type';

export interface CourseClassSessionFilterParams extends FilterParams {
  courseId?: string;
  courseClassId?: string;
  mainTeacherId?: string;
  scheduleSlotId?: string;
  startDate?: string;
  endDate?: string;
}
