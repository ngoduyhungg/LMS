import { FormFieldType } from '@/shared/types/form-field-type';
import { scheduleWeekOptions } from './schedule-week-options';

export const scheduleFilters = [
  {
    name: 'weekday',
    label: 'Thứ',
    placeholder: 'Chọn thứ',
    type: FormFieldType.Select,
    options: scheduleWeekOptions,
    rules: [{ required: true, message: 'Vui lòng chọn thứ' }],
  },
  {
    name: 'startTime',
    label: 'Thời gian bắt đầu',
    type: FormFieldType.TimePicker,
    placeholder: 'Chọn thời gian bắt đầu',
    rules: [{ required: true, message: 'Vui lòng chọn thời gian bắt đầu' }],
  },
  {
    name: 'endTime',
    label: 'Thời gian kết thúc',
    type: FormFieldType.TimePicker,
    placeholder: 'Chọn thời gian kết thúc',
    rules: [{ required: true, message: 'Vui lòng chọn thời gian kết thúc' }],
  },
];
