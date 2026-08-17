import { LeaveRequestStatus } from '../types/leave-request-type';

export const leaveRequestStatusOptions = [
  { label: 'Chờ duyệt', value: LeaveRequestStatus.PENDING },
  { label: 'Đã phê duyệt', value: LeaveRequestStatus.APPROVED },
  { label: 'Đã từ chối', value: LeaveRequestStatus.REJECTED },
];
