export interface LeaveRequest {
  id: string;

  studentId: string;
  studentName: string | null;

  sessionId: string;

  courseClassId: string;
  courseClassName: string;

  scheduleSlotId: string;
  scheduleSlotName: string;

  startTime: string;
  endTime: string;

  courseId: string;
  courseName: string;

  status: LeaveRequestStatusType;
  statusText?: string;

  reason: string;

  leaveDate: string;

  createdAt: string;
  updatedAt: string;
}

export const LeaveRequestStatus = {
  PENDING: 'PENDING',
  APPROVED: 'APPROVED',
  REJECTED: 'REJECTED',
} as const;

export type LeaveRequestStatusType = (typeof LeaveRequestStatus)[keyof typeof LeaveRequestStatus];
