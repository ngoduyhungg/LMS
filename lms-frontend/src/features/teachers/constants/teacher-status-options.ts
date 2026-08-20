import { TEACHER_STATUS } from '../types/teacher-status-type';

export const teacherStatusOptions = [
  { label: 'Đang giảng dạy', value: TEACHER_STATUS.ACTIVE },
  { label: 'Tạm nghỉ', value: TEACHER_STATUS.PAUSED },
  { label: 'Ngừng công tác', value: TEACHER_STATUS.INACTIVE },
];
