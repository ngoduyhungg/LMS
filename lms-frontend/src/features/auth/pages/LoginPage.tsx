import { Button, Form, Image } from 'antd';

import { Link, useNavigate } from 'react-router-dom';

import { useAppSelector, useAppDispatch } from '@/app/redux/hooks';

import YoeduLogo from '@/assets/images/yoedu-logo.svg';
import CardCustom from '@/shared/components/card/CardCustom';
import { loginFormFields } from '@/features/auth/constants/login-form-fields';
import { loginThunk } from '@/features/auth/store/auth-thunk';
import { useNotification } from '@/shared/hooks/useNotification';
import DynamicForm from '@/shared/components/form/DynamicForm';
import type { LoginPayload } from '../types/auth-type';

const LoginPage = () => {
  const dispatch = useAppDispatch();
  const { loading } = useAppSelector((state) => state.auth);
  const { showNotification } = useNotification();

  const navigate = useNavigate();

  const [form] = Form.useForm<LoginPayload>();

  const onFinish = async (values: LoginPayload) => {
    try {
      await dispatch(
        loginThunk({
          email: values.email,
          password: values.password,
        }),
      ).unwrap();

      showNotification(
        'success',
        'Đăng nhập thành công',
        'Bạn đã đăng nhập thành công. Vui lòng tiếp tục sử dụng hệ thống.',
      );

      navigate('/', { replace: true });
    } catch (error: any) {
      showNotification('error', 'Đăng nhập thất bại', error || 'Đã xảy ra lỗi. Vui lòng thử lại.');
    }
  };

  return (
    <CardCustom className="w-full max-w-md border-0 shadow-2xl">
      <div className="mx-auto flex h-24 w-24 items-center justify-center">
        <Image src={YoeduLogo} preview={false} />
      </div>

      <div className="mb-2 text-center">
        <h1 className="mb-2 font-bold text-2xl">Đăng nhập</h1>

        <span className="text-gray-500">Nhập thông tin tài khoản để tiếp tục</span>
      </div>

      <Form form={form} layout="vertical" autoComplete="off" onFinish={onFinish}>
        <DynamicForm<LoginPayload> fields={loginFormFields} />

        <div className="mb-6 flex items-center justify-end">
          <Link to="/auth/forgot-password" className="text-sm text-blue-600 hover:text-blue-500">
            Quên mật khẩu?
          </Link>
        </div>

        <Form.Item className="mb-4">
          <Button loading={loading} htmlType="submit" type="primary" block>
            Đăng nhập
          </Button>
        </Form.Item>
      </Form>

      <div className="text-center">
        <span className="text-gray-500">Chưa có tài khoản? </span>

        <Link to="/auth/register" className="font-medium text-blue-600 hover:text-blue-500">
          Đăng ký ngay
        </Link>
      </div>

      <div className="mt-8 text-center">
        <span className="text-xs text-gray-400">© 2026 YOEDU. Hệ thống quản lý đào tạo.</span>
      </div>
    </CardCustom>
  );
};

export default LoginPage;
