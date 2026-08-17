import { useAppDispatch, useAppSelector } from '@/app/redux/hooks';
import { Button, Flex, Form } from 'antd';
import { generalInfoFormFields } from '../constants/general-info-form-fields';
import CardCustom from '@/shared/components/card/CardCustom';
import type { User } from '../types/user-type';
import { useEffect } from 'react';
import { getMeThunk } from '@/features/auth/store/auth-thunk';
import { useNotification } from '@/shared/hooks/useNotification';
import { userRoleUserApi } from '../api/user-api';
import { formatDateToPicker } from '@/shared/utils/date';
import DynamicForm from '@/shared/components/form/DynamicForm';

const GeneralInfoTab = () => {
  const { user } = useAppSelector((state) => state.auth);
  const dispatch = useAppDispatch();

  const { showNotification } = useNotification();

  const { update } = userRoleUserApi;

  const [form] = Form.useForm();

  const onFinish = async (values: User) => {
    try {
      const res = await update(values);

      dispatch(getMeThunk());

      showNotification(
        'success',
        'Cập nhật thành công',
        res.message || 'Thông tin cá nhân đã được cập nhật thành công',
      );
    } catch (error: any) {
      showNotification(
        'error',
        'Cập nhật thất bại',
        error?.response?.data?.message || 'Đã có lỗi xảy ra khi cập nhật thông tin cá nhân',
      );
    }
  };

  useEffect(() => {
    if (user) {
      form.setFieldsValue({
        ...user,
        dateOfBirth: user.dateOfBirth ? formatDateToPicker(user.dateOfBirth) : null,
      });
    }
  }, [user, form]);

  return (
    <Flex vertical gap={16}>
      <CardCustom title="Thông tin cá nhân">
        <Form form={form} layout="vertical" onFinish={onFinish}>
          <DynamicForm<User> fields={generalInfoFormFields} />

          <Button type="primary" htmlType="submit">
            Cập nhật
          </Button>
        </Form>
      </CardCustom>
    </Flex>
  );
};

export default GeneralInfoTab;
