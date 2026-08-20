export const AttendanceStatus = {
  PRESENT: 'PRESENT',
  ABSENT: 'ABSENT',
  LATE: 'LATE',
  EXCUSED: 'EXCUSED',
} as const;

export type AttendanceStatusType = (typeof AttendanceStatus)[keyof typeof AttendanceStatus];

export interface AttendancePayload {
  studentId: string;
  status: AttendanceStatusType;
  note?: string;
}

export interface AttendanceStudent {
  studentId: string;
  studentCode: string;
  fullName: string;

  attendanceId: string | null;
  status: AttendanceStatusType | null;
  note: string | null;
}

export interface AttendanceData {
  id: string;
  className: string;

  startTime: string;
  endTime: string;

  status: string;

  students: AttendanceStudent[];
}
