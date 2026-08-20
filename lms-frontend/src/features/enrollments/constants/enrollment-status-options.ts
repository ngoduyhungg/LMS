import { EnrollmentStatus } from '../types/enrollment-type';

export const enrollmentStatusOptions = [
  { label: 'Đang học', value: EnrollmentStatus.ACTIVE },
  { label: 'Bảo lưu', value: EnrollmentStatus.PAUSED },
  { label: 'Đã thôi học', value: EnrollmentStatus.DROPPED },
  { label: 'Hoàn thành', value: EnrollmentStatus.COMPLETED },
  { label: 'Hủy đăng ký', value: EnrollmentStatus.CANCELLED },
];
