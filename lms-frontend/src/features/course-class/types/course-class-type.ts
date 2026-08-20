export type CourseClass = {
  id: string;
  classCode: string;
  name: string;
  courseId: string;
  courseName: string;
  roomId: string;
  roomName: string;
  scheduleSlotIds: string[];
  scheduleInformation: string;
  mainTeacherId: string;
  mainTeacherName: string;
  assistantTeacherId: string | null;
  assistantTeacherName: string | null;
  startDate: string;
  endDate: string;
  maxStudents: number;
  tuitionFee: string;
  status: CourseClassStatusType;
  statusText: string;
  createdAt: string;
  updatedAt: string;
};

export const CourseClassStatus = {
  OPEN: 'OPEN',
  ONGOING: 'ONGOING',
  CLOSED: 'CLOSED',
} as const;

export type CourseClassStatusType = (typeof CourseClassStatus)[keyof typeof CourseClassStatus];
