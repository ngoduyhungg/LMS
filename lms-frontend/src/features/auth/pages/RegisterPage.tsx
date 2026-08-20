import { Button, Form, Image } from 'antd';
import { Link } from 'react-router-dom';

import YoeduLogo from '@/assets/images/yoedu-logo.svg';
import CardCustom from '@/shared/components/card/CardCustom';

import { registerFormFields } from '@/features/auth/constants/register-form-fields';
import { useNotification } from '@/shared/hooks/useNotification';
import DynamicForm from '@/shared/components/form/DynamicForm';
import type { RegisterPayload } from '../types/auth-type';

const RegisterPage = () => {
  const { showNotification } = useNotification();
  const [form] = Form.useForm<RegisterPayload>();

  const onFinish = () => {
    showNotification(
      'warning',
      'Không hỗ trợ',
      'Chức năng đăng ký tài khoản mới hiện đang bị vô hiệu hóa trên hệ thống.'
    );
  };

  return (
    <CardCustom className="w-full max-w-md border-0 shadow-2xl">
      <div className="mx-auto flex h-24 w-24 items-center justify-center">
        <Image src={YoeduLogo} preview={false} />
      </div>
      <div className="mb-2 text-center">
        <h1 className="mb-2 font-bold text-2xl">Đăng ký</h1>
        <span className="text-gray-500">Tạo tài khoản để bắt đầu sử dụng hệ thống</span>
      </div>
      <Form form={form} layout="vertical" autoComplete="off" onFinish={onFinish}>
        <DynamicForm<RegisterPayload> fields={registerFormFields} />
        <Form.Item className="mb-4!">
          <Button htmlType="submit" type="primary" block>
            Đăng ký
          </Button>
        </Form.Item>
      </Form>
      <div className="text-center">
        <span className="text-gray-500">Đã có tài khoản? </span>
        <Link to="/auth/login" className="font-medium text-blue-600 hover:text-blue-500">
          Đăng nhập
        </Link>
      </div>
      <div className="mt-8 text-center">
        <span className="text-xs text-gray-400">© 2026 YOEDU. Hệ thống quản lý đào tạo.</span>
      </div>
    </CardCustom>
  );
};

export default RegisterPage;