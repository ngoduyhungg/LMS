export interface CourseClassSession {
  id: string;
  courseId: string;
  courseName: string;
  courseClassId: string;
  courseClassName: string;
  scheduleSlotId: string;
  scheduleSlotName: string;
  mainTeacherId: string;
  mainTeacherName: string;
  assistantTeacherId: string | null;
  assistantTeacherName: string | null;
  startTime: string;
  endTime: string;
  status: CourseClassSessionStatusType;
  statusText: string;
  note: string | null;
  createdAt: string;
  updatedAt: string;
}

export const CourseClassSessionStatus = {
  SCHEDULED: 'SCHEDULED', // Lớp học đã được lên lịch nhưng chưa bắt đầu
  DONE: 'DONE', // Lớp học đã kết thúc
  CANCELLED: 'CANCELLED', // Lớp học đã bị hủy
  RESCHEDULED: 'RESCHEDULED', // Lớp học đã được lên lịch lại sau khi bị hủy hoặc thay đổi lịch
} as const;

export type CourseClassSessionStatusType =
  (typeof CourseClassSessionStatus)[keyof typeof CourseClassSessionStatus];
