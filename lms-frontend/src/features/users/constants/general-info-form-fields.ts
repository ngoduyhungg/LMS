import { FormFieldType } from '@/shared/types/form-field-type';

import { rules } from '@/shared/utils/rules';
import { userGenderOptions } from './user-gender-options';
import type { User } from '../types/user-type';
import type { FormContext, FormField } from '@/shared/components/modal/ModalFormCustom';
import { FormModalMode } from '@/shared/types/form-modal-mode-type';

export const generalInfoFormFields: FormField<User>[] = [
  {
    name: 'avatarUrl',
    label: 'Ảnh đại diện',
    type: FormFieldType.ImageUpload,
    placeholder: '',
    col: 24,
  },
  {
    name: 'email',
    label: 'Email',
    type: FormFieldType.Input,
    placeholder: 'Nhập email',
    rules: [
      {
        required: true,
        message: 'Vui lòng nhập email',
      },
      rules.email,
    ],
  },
  {
    name: 'password',
    label: 'Mật khẩu',
    type: FormFieldType.InputPassword,
    placeholder: 'Nhập mật khẩu',
    rules: [
      {
        required: true,
        message: 'Vui lòng nhập mật khẩu',
      },
      rules.password,
    ],
    hidden: ({ mode }: FormContext) => !mode || mode !== FormModalMode.CREATE,
  },
  {
    name: 'fullName',
    label: 'Họ và tên',
    type: FormFieldType.Input,
    placeholder: 'Nhập họ và tên',
    rules: [
      {
        required: true,
        message: 'Vui lòng nhập họ và tên',
      },
    ],
  },

  {
    name: 'phone',
    label: 'Số điện thoại',
    type: FormFieldType.Input,
    placeholder: 'Nhập số điện thoại',
    rules: [rules.phone],
  },

  {
    name: 'gender',
    label: 'Giới tính',
    type: FormFieldType.Select,
    placeholder: 'Chọn giới tính',
    options: userGenderOptions,
  },

  {
    name: 'dateOfBirth',
    label: 'Ngày sinh',
    type: FormFieldType.DatePicker,
    placeholder: 'Chọn ngày sinh',
  },

  {
    name: 'address',
    label: 'Địa chỉ',
    type: FormFieldType.TextArea,
    placeholder: 'Nhập địa chỉ',
  },
];
