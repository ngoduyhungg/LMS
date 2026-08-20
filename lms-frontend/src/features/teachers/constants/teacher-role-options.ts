import { TEACHER_ROLE } from '../types/teacher-role';

export const teacherRoleOptions = [
  { label: 'Giáo viên', value: TEACHER_ROLE.TEACHER },
  { label: 'Trợ giảng', value: TEACHER_ROLE.ASSISTANT },
  { label: 'Cả giáo viên và trợ giảng', value: TEACHER_ROLE.BOTH },
];
