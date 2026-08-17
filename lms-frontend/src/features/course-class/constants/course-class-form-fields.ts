import { FormFieldType } from '@/shared/types/form-field-type';
import type { FormField } from '@/shared/components/modal/ModalFormCustom';
import type { CourseClass } from '../types/course-class-type';
import { getCourseOptions } from '@/features/courses/api/course-api';
import { getTeachersOptions } from '@/features/teachers/api/teacher-api';
import { getRoomsOptions } from '@/features/rooms/api/room-api';
import { getScheduleOptions } from '@/features/schedule/api/schedule-api';
import { disableEndDateNotPast, disableStartDateNotPast } from '@/shared/utils/validate-date';
import type { FormInstance } from 'antd';
import { Dayjs } from 'dayjs';
import { USER_ROLE, type UserRole } from '@/features/users/types/user-role-type';

export const courseClassFormFields: FormField<CourseClass>[] = [
  {
    name: 'classCode',
    label: 'Mã lớp học',
    type: FormFieldType.Input,
    placeholder: 'Mã lớp học',
    disabled: true,
  },
  {
    name: 'name',
    label: 'Tên lớp học',
    type: FormFieldType.Input,
    placeholder: 'Nhập lớp khóa học',
    rules: [
      {
        required: true,
        message: 'Vui lòng nhập tên lớp học',
      },
    ],
  },
  {
    name: 'courseId',
    label: 'Khóa học',
    type: FormFieldType.SelectFetch,
    fetchOptions: getCourseOptions,
    placeholder: 'Chọn khóa học',
    rules: [
      {
        required: true,
        message: 'Vui lòng chọn khóa học',
      },
    ],
    onChange: (value, options, form) => {
      const selectedCourse = options.find((option: any) => option.value === value);

      form.setFieldsValue(selectedCourse);
    },
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
    name: 'roomId',
    label: 'Phòng học',
    type: FormFieldType.SelectFetch,
    fetchOptions: getRoomsOptions,
    placeholder: 'Chọn phòng học',
    rules: [
      {
        required: true,
        message: 'Vui lòng chọn phòng học',
      },
    ],
  },
  {
    name: 'scheduleSlotIds',
    label: 'Ca học',
    type: FormFieldType.SelectFetch,
    fetchOptions: getScheduleOptions,
    placeholder: 'Chọn ca học',
    col: 24,
    props: {
      mode: 'multiple',
    },
    rules: [
      {
        required: true,
        message: 'Vui lòng chọn ca học',
      },
    ],
  },
  {
    name: 'startDate',
    label: 'Ngày bắt đầu',
    type: FormFieldType.DatePicker,
    props: (form: FormInstance) => ({
      disabledDate: (current: Dayjs) =>
        disableStartDateNotPast(current, form.getFieldValue('endDate')),
    }),
    placeholder: 'Chọn ngày bắt đầu',
    rules: [
      {
        required: true,
        message: 'Vui lòng chọn ngày bắt đầu',
      },
    ],
  },
  {
    name: 'endDate',
    label: 'Ngày kết thúc',
    type: FormFieldType.DatePicker,
    props: (form: FormInstance) => ({
      disabledDate: (current: Dayjs) =>
        disableEndDateNotPast(current, form.getFieldValue('startDate')),
    }),
    placeholder: 'Chọn ngày kết thúc',
    rules: [
      {
        required: true,
        message: 'Vui lòng chọn ngày kết thúc',
      },
    ],
  },
  {
    name: 'tuitionFee',
    label: 'Giá lớp học',
    type: FormFieldType.InputNumber,
    placeholder: 'Nhập giá lớp học',
    props: {
      isCurrency: true,
    },
    rules: [
      {
        required: true,
        message: 'Vui lòng nhập giá lớp học',
      },
    ],
  },
  {
    name: 'maxStudents',
    label: 'Số học viên tối đa',
    type: FormFieldType.InputNumber,
    placeholder: 'Nhập số học viên tối đa',
    hidden: ({ role }: { role: UserRole }) => role !== USER_ROLE.ADMIN,
  },
];
