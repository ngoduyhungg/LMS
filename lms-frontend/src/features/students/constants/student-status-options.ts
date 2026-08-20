import { STUDENT_STATUS } from '../types/student-status-type';

export const studentStatusOptions = [
  { label: 'Đang tham gia', value: STUDENT_STATUS.ACTIVE },
  { label: 'Đã tạm ngưng', value: STUDENT_STATUS.PAUSED },
  { label: 'Đã nghỉ học', value: STUDENT_STATUS.DROPPED },
];
