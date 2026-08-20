import { FormFieldType } from '@/shared/types/form-field-type';
import { teacherStatusOptions } from './teacher-status-options';

export const teacherFilters = [
  {
    name: 'keySearch',
    type: FormFieldType.Input,
    placeholder: 'Tìm kiếm theo email, tên...',
  },
  {
    name: 'status',
    type: FormFieldType.Select,
    placeholder: 'Trạng thái',
    options: teacherStatusOptions,
  },
];
