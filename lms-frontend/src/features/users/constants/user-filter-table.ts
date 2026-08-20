import { FormFieldType } from '@/shared/types/form-field-type';
import { userStatusOptions } from './user-status-options';
import { userRoleOptions } from './user-role-options';

export const userFilters = [
  {
    name: 'keySearch',
    type: FormFieldType.Input,
    placeholder: 'Tìm kiếm theo email, tên...',
  },
  {
    name: 'role',
    type: FormFieldType.Select,
    placeholder: 'Vai trò',
    options: userRoleOptions,
  },
  {
    name: 'status',
    type: FormFieldType.Select,
    placeholder: 'Trạng thái',
    options: userStatusOptions,
  },
];
