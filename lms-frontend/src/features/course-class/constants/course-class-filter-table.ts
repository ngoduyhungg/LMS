import { FormFieldType } from '@/shared/types/form-field-type';
import { getCourseOptions } from '@/features/courses/api/course-api';
import { getTeachersOptions } from '@/features/teachers/api/teacher-api';
import { getRoomsOptions } from '@/features/rooms/api/room-api';
import { getScheduleOptions } from '@/features/schedule/api/schedule-api';

export const courseClassFilters = [
  {
    name: 'keySearch',
    type: FormFieldType.Input,
    placeholder: 'Tìm kiếm theo tên lớp học...',
  },
  {
    name: 'courseId',
    label: 'Khóa học',
    type: FormFieldType.SelectFetch,
    fetchOptions: getCourseOptions,
    placeholder: 'Chọn khóa học',
  },
  {
    name: 'mainTeacherId',
    label: 'Giáo viên chính',
    type: FormFieldType.SelectFetch,
    fetchOptions: getTeachersOptions,
    placeholder: 'Chọn giáo viên',
  },
  {
    name: 'roomId',
    label: 'Phòng học',
    type: FormFieldType.SelectFetch,
    fetchOptions: getRoomsOptions,
    placeholder: 'Chọn phòng học',
  },
  {
    name: 'scheduleSlotId',
    label: 'Ca học',
    type: FormFieldType.SelectFetch,
    fetchOptions: getScheduleOptions,
    placeholder: 'Chọn ca học',
  },
  {
    name: 'startDate',
    label: 'Ngày bắt đầu',
    type: FormFieldType.DatePicker,
    placeholder: 'Chọn ngày bắt đầu',
  },
  {
    name: 'endDate',
    label: 'Ngày kết thúc',
    type: FormFieldType.DatePicker,
    placeholder: 'Chọn ngày kết thúc',
  },
];
