import { USER_ROLE } from '../types/user-role-type';

export const userRoleOptions = [
  {
    label: 'Quản trị viên',
    value: USER_ROLE.ADMIN,
  },
  {
    label: 'Nhân viên',
    value: USER_ROLE.STAFF,
  },
  {
    label: 'Giáo viên',
    value: USER_ROLE.TEACHER,
  },
  {
    label: 'Phụ huynh',
    value: USER_ROLE.PARENT,
  },
  {
    label: 'Học viên',
    value: USER_ROLE.STUDENT,
  },
];
