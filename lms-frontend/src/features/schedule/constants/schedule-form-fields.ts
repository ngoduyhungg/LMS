import { FormFieldType } from '@/shared/types/form-field-type';
import type { FormField } from '@/shared/components/modal/ModalFormCustom';
import type { Schedule } from '../types/schedule-type';
import { scheduleWeekOptions } from './schedule-week-options';

export const scheduleFormFields: FormField<Schedule>[] = [
  {
    name: 'slotCode',
    label: 'Mã ca học',
    type: FormFieldType.Input,
    placeholder: 'Mã ca học',
    disabled: true,
  },
  {
    name: 'weekday',
    label: 'Thứ',
    type: FormFieldType.Select,
    options: scheduleWeekOptions,
    placeholder: 'Chọn thứ',
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
  {
    name: 'note',
    label: 'Ghi chú',
    type: FormFieldType.TextArea,
    placeholder: 'Nhập ghi chú',
  },
];
