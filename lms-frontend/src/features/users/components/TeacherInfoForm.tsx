import { useEffect } from 'react';

import { Button, Form } from 'antd';
import CardCustom from '@/shared/components/card/CardCustom';
import { teacherFormFields } from '@/features/teachers/constants/teacher-form-fields';
import { teacherRoleTeacherApi } from '@/features/teachers/api/teacher-api';
import { initializeAuthThunk } from '@/features/auth/store/auth-thunk';
import { useNotification } from '@/shared/hooks/useNotification';
import { useAppDispatch } from '@/app/redux/hooks';
import type { Teacher } from '@/features/teachers/types/teacher-type';
import DynamicForm from '@/shared/components/form/DynamicForm';

interface Props {
  teacher: Teacher;
}

const TeacherInfoForm = ({ teacher }: Props) => {
  const dispatch = useAppDispatch();
  const { showNotification } = useNotification();

  const [form] = Form.useForm();

  const onFinish = async (values: Teacher) => {
    try {
      const res = await teacherRoleTeacherApi.update(values);

      if (!res.success) {
        showNotification(
          'error',
          'Cập nhật thất bại',
          res.message || 'Đã có lỗi xảy ra khi cập nhật thông tin giáo viên',
        );
        return;
      }

      dispatch(initializeAuthThunk());

      showNotification(
        'success',
        'Cập nhật thành công',
        res.message || 'Thông tin giáo viên đã được cập nhật thành công',
      );
    } catch (error) {
      showNotification(
        'error',
        'Cập nhật thất bại',
        'Đã có lỗi xảy ra khi cập nhật thông tin giáo viên',
      );
    }
  };

  useEffect(() => {
    if (teacher) {
      form.setFieldsValue({
        ...teacher,
      });
    }
  }, [teacher, form]);

  return (
    <CardCustom title="Thông tin giáo viên">
      <Form form={form} layout="vertical" onFinish={onFinish}>
        <DynamicForm<Teacher> fields={teacherFormFields} />

        <Button type="primary" htmlType="submit">
          Cập nhật
        </Button>
      </Form>
    </CardCustom>
  );
};

export default TeacherInfoForm;
