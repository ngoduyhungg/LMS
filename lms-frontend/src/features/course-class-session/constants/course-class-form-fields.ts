import { FormFieldType } from '@/shared/types/form-field-type';
import type { FormField } from '@/shared/components/modal/ModalFormCustom';
import { getCourseOptions } from '@/features/courses/api/course-api';
import { getTeachersOptions } from '@/features/teachers/api/teacher-api';
import type { CourseClassSession } from '../types/course-class-session-type';

export const courseClassSessionFormFields: FormField<CourseClassSession>[] = [
  {
    name: 'courseId',
    label: 'Khóa học',
    type: FormFieldType.SelectFetch,
    fetchOptions: getCourseOptions,
    placeholder: 'Chọn khóa học',
    disabled: true,
  },
  {
    name: 'mainTeacherId',
    label: 'Giáo viên chính',
    type: FormFieldType.SelectFetch,
    fetchOptions: getTeachersOptions,
    placeholder: 'Chọn giáo viên chính',
    rules: [
      {
        required: true,
        message: 'Vui lòng chọn giáo viên chính',
      },
    ],
  },
  {
    name: 'assistantTeacherId',
    label: 'Giáo viên phụ',
    type: FormFieldType.SelectFetch,
    fetchOptions: getTeachersOptions,
    placeholder: 'Chọn giáo viên phụ',
  },
  {
    name: 'startTime',
    label: 'Ngày / Giờ bắt đầu',
    type: FormFieldType.DateTimePicker,
    placeholder: 'Chọn ngày / giờ bắt đầu',
    rules: [
      {
        required: true,
        message: 'Vui lòng chọn ngày / giờ bắt đầu',
      },
    ],
  },
  {
    name: 'endTime',
    label: 'Ngày / Giờ kết thúc',
    type: FormFieldType.DateTimePicker,
    placeholder: 'Chọn ngày / giờ kết thúc',
    rules: [
      {
        required: true,
        message: 'Vui lòng chọn ngày / giờ kết thúc',
      },
    ],
  },
  {
    name: 'note',
    label: 'Ghi chú',
    type: FormFieldType.TextArea,
    placeholder: 'Nhập ghi chú',
  },
];
