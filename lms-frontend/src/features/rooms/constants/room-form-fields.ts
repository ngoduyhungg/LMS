import { FormFieldType } from '@/shared/types/form-field-type';
import type { FormField } from '@/shared/components/modal/ModalFormCustom';
import type { Room } from '../types/room-type';

export const roomFormFields: FormField<Room>[] = [
  {
    name: 'roomCode',
    label: 'Mã phòng',
    type: FormFieldType.Input,
    placeholder: 'Mã phòng',
    disabled: true,
  },
  {
    name: 'name',
    label: 'Tên phòng học',
    type: FormFieldType.Input,
    placeholder: 'Nhập tên phòng học',
    rules: [
      {
        required: true,
        message: 'Vui lòng nhập tên phòng học',
      },
    ],
  },
  {
    name: 'capacity',
    label: 'Sức chứa',
    type: FormFieldType.InputNumber,
    placeholder: 'Nhập sức chứa của phòng học',
    rules: [
      {
        required: true,
        message: 'Vui lòng nhập sức chứa của phòng học',
      },
    ],
  },
  {
    name: 'description',
    label: 'Mô tả phòng học',
    type: FormFieldType.TextArea,
    placeholder: 'Nhập mô tả phòng học',
  },
];
