import { FormFieldType } from '@/shared/types/form-field-type';
import { studentStatusOptions } from './student-status-options';

export const studentFilters = [
  {
    name: 'keySearch',
    type: FormFieldType.Input,
    placeholder: 'Tìm kiếm theo email, tên...',
  },
  {
    name: 'status',
    type: FormFieldType.Select,
    placeholder: 'Trạng thái',
    options: studentStatusOptions,
  },
];
