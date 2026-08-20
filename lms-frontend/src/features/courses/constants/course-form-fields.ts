import { FormFieldType } from '@/shared/types/form-field-type';
import type { Course } from '../types/course-type';
import { courseLevelOptions } from './course-level-options';
import type { FormField } from '@/shared/components/modal/ModalFormCustom';

export const courseFormFields: FormField<Course>[] = [
  {
    name: 'thumbnailUrl',
    label: 'Banner khóa học',
    type: FormFieldType.ImageUpload,
    placeholder: '',
    col: 24,
    props: {
      type: 'banner',
    },
  },
  {
    name: 'courseCode',
    label: 'Mã khóa học',
    type: FormFieldType.Input,
    placeholder: 'Mã khóa học',
    disabled: true,
  },
  {
    name: 'name',
    label: 'Tên khóa học',
    type: FormFieldType.Input,
    placeholder: 'Nhập tên khóa học',
    rules: [
      {
        required: true,
        message: 'Vui lòng nhập tên khóa học',
      },
    ],
  },
  {
    name: 'totalSessions',
    label: 'Tổng số buổi học',
    type: FormFieldType.InputNumber,
    placeholder: 'Nhập tổng số buổi học',
    rules: [
      {
        required: true,
        message: 'Vui lòng nhập tổng số buổi học',
      },
    ],
  },
  {
    name: 'level',
    label: 'Cấp độ',
    type: FormFieldType.Select,
    options: courseLevelOptions,
    placeholder: 'Chọn cấp độ',
  },
  {
    name: 'description',
    label: 'Mô tả khóa học',
    type: FormFieldType.TextArea,
    placeholder: 'Nhập mô tả khóa học',
  },
];
