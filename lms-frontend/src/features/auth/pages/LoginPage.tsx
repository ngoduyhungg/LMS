import { Button, Form } from 'antd';
import { useNavigate } from 'react-router-dom';
import { useAppDispatch, useAppSelector } from '@/app/redux/hooks';

import CardCustom from '@/shared/components/card/CardCustom';
import { loginFormFields } from '@/features/auth/constants/login-form-fields';
import DynamicForm from '@/shared/components/form/DynamicForm';
import type { LoginPayload } from '../types/auth-type';
import { loginThunk } from '../store/auth-thunk';
import { useNotification } from '@/shared/hooks/useNotification';

const LoginPage = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const { showNotification } = useNotification();
  const { loading } = useAppSelector((state) => state.auth);
  const [form] = Form.useForm<LoginPayload>();

  const onFinish = async (values: LoginPayload) => {
    try {
      await dispatch(loginThunk(values)).unwrap();
      showNotification('success', 'Đăng nhập thành công', 'Chào mừng bạn quay lại hệ thống!');
      navigate('/');
    } catch (error: unknown) {
      const errorMessage = error instanceof Error ? error.message : String(error);
      showNotification('error', 'Đăng nhập thất bại', errorMessage || 'Thông tin đăng nhập không chính xác');
    }
  };

  return (
    <CardCustom className="w-full max-w-md border-0 shadow-2xl">
      <div className="mx-auto mb-6 flex items-center justify-center">
        {/* LMS Brand Logo thay thế cho YoeduLogo */}
        <div className="flex h-16 w-16 items-center justify-center rounded-xl bg-blue-600 text-2xl font-black text-white shadow-lg">
          LMS
        </div>
      </div>
      
      <div className="mb-6 text-center">
        <h1 className="mb-2 font-bold text-2xl text-gray-800">Đăng nhập</h1>
        <span className="text-gray-500">Đăng nhập để tiếp tục sử dụng hệ thống quản lý học tập</span>
      </div>

      <Form form={form} layout="vertical" autoComplete="off" onFinish={onFinish}>
        <DynamicForm<LoginPayload> fields={loginFormFields} />
        
        <div className="mb-4 text-right">
          <a href="#" className="text-sm font-medium text-blue-600 hover:text-blue-500">
            Quên mật khẩu?
          </a>
        </div>

        <Form.Item className="mb-6!">
          <Button htmlType="submit" type="primary" block loading={loading} size="large">
            Đăng nhập
          </Button>
        </Form.Item>
      </Form>

      <div className="text-center">
        <span className="text-sm text-gray-500">Liên hệ quản trị viên để được cấp tài khoản</span>
      </div>
      
      <div className="mt-8 text-center">
        <span className="text-xs text-gray-400">© 2026 LMS. Hệ thống quản lý học tập.</span>
      </div>
    </CardCustom>
  );
};

export default LoginPage;