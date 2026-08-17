import type { CourseClassSessionStatusType } from './course-class-session-type';

export interface CalendarSession {
  id: string;

  title: string;

  start: string;

  end: string;

  status: CourseClassSessionStatusType;
  statusText: string;

  courseClassId: string;
  courseClassName: string;

  mainTeacherName: string;

  assistantTeacherName: string;

  courseName: string;

  roomName: string;

  note: string;
}
