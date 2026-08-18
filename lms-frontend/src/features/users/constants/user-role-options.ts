import { USER_ROLE } from '../types/user-role-type';

export const userRoleOptions = [
  {
    label: 'Quản trị viên',
    value: USER_ROLE.ADMIN,
  },
  {
    label: 'Nhân viên',
    value: USER_ROLE.INSTRUCTOR,
  },
  {
    label: 'Giáo viên',
    value: USER_ROLE.INSTRUCTOR,
  },
  {
    label: 'Phụ huynh',
    value: USER_ROLE.STUDENT,
  },
  {
    label: 'Học viên',
    value: USER_ROLE.STUDENT,
  },
];
