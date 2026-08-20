import { FormFieldType } from '@/shared/types/form-field-type';
import { rules } from '@/shared/utils/rules';
import { LockOutlined, MailOutlined } from '@ant-design/icons';
import type { RegisterPayload } from '../types/auth-type';
import type { FormField } from '@/shared/components/modal/ModalFormCustom';

export const registerFormFields: FormField<RegisterPayload>[] = [
  {
    name: 'fullName',
    label: 'Họ và tên',
    type: FormFieldType.Input,
    placeholder: 'Nhập họ và tên',
    icon: MailOutlined,
    rules: [
      {
        required: true,
        message: 'Vui lòng nhập họ và tên',
      },
    ],
    col: 24,
  },
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

  {
    name: 'confirmPassword',
    label: 'Xác nhận mật khẩu',
    type: FormFieldType.InputPassword,
    placeholder: 'Nhập lại mật khẩu',
    icon: LockOutlined,
    rules: [
      {
        required: true,
        message: 'Vui lòng xác nhận mật khẩu',
      },
      rules.password,
      ({ getFieldValue }: any) => ({
        validator(_: any, value: string) {
          if (!value || getFieldValue('password') === value) {
            return Promise.resolve();
          }

          return Promise.reject(new Error('Mật khẩu xác nhận không khớp'));
        },
      }),
    ],
    col: 24,
  },
];
