import {
  CourseClassSessionStatus,
  type CourseClassSessionStatusType,
} from '../types/course-class-session-type';

export const mappedColorByStatus: Record<CourseClassSessionStatusType, string> = {
  [CourseClassSessionStatus.SCHEDULED]: 'event-scheduled',
  [CourseClassSessionStatus.DONE]: 'event-done',
  [CourseClassSessionStatus.CANCELLED]: 'event-cancelled',
  [CourseClassSessionStatus.RESCHEDULED]: 'event-rescheduled',
};
