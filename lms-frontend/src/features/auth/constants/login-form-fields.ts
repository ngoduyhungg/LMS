import type { FormField } from '@/shared/components/modal/ModalFormCustom';
import { FormFieldType } from '@/shared/types/form-field-type';
import { rules } from '@/shared/utils/rules';
import { LockOutlined, MailOutlined } from '@ant-design/icons';
import type { LoginPayload } from '../types/auth-type';

export const loginFormFields: FormField<LoginPayload>[] = [
  {
    name: 'email',
    label: 'Email',
    type: FormFieldType.Input,
    placeholder: 'Nhập email',
    icon: MailOutlined,
    rules: [
      {
        required: true,
        message: 'Vui lòng nhập email',
      },
      rules.email,
    ],
    col: 24,
  },
  {
    name: 'password',
    label: 'Mật khẩu',
    type: FormFieldType.InputPassword,
    placeholder: 'Nhập mật khẩu',
    icon: LockOutlined,
    rules: [
      {
        required: true,
        message: 'Vui lòng nhập mật khẩu',
      },
      rules.password,
    ],
    col: 24,
  },
];
