import { FormFieldType } from '@/shared/types/form-field-type';
import { courseStatusOptions } from './course-status-options';
import { courseLevelOptions } from './course-level-options';

export const courseFilters = [
  {
    name: 'keySearch',
    type: FormFieldType.Input,
    placeholder: 'Tìm kiếm theo tên khóa học...',
  },
  {
    name: 'level',
    type: FormFieldType.Select,
    placeholder: 'Cấp độ',
    options: courseLevelOptions,
  },
  {
    name: 'status',
    type: FormFieldType.Select,
    placeholder: 'Trạng thái',
    options: courseStatusOptions,
  },
];
