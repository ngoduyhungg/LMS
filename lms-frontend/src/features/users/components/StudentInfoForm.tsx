import { useEffect } from 'react';

import { Button, Form } from 'antd';
import CardCustom from '@/shared/components/card/CardCustom';
import { useAppDispatch } from '@/app/redux/hooks';
import { initializeAuthThunk } from '@/features/auth/store/auth-thunk';
import { useNotification } from '@/shared/hooks/useNotification';
import type { Student } from '@/features/students/types/student-type';
import { studentRoleStudentApi } from '@/features/students/api/student-api';
import { studentFormFields } from '@/features/students/constants/student-form-fields';
import DynamicForm from '@/shared/components/form/DynamicForm';

interface Props {
  student: Student;
}

const StudentInfoForm = ({ student }: Props) => {
  const dispatch = useAppDispatch();
  const { showNotification } = useNotification();

  const [form] = Form.useForm();

  const onFinish = async (values: Student) => {
    try {
      const res = await studentRoleStudentApi.update(values);

      if (!res.success) {
        showNotification(
          'error',
          'Cập nhật thất bại',
          res.message || 'Đã có lỗi xảy ra khi cập nhật thông tin học viên',
        );
        return;
      }

      dispatch(initializeAuthThunk());

      showNotification(
        'success',
        'Cập nhật thành công',
        res.message || 'Thông tin học viên đã được cập nhật thành công',
      );
    } catch (error) {
      showNotification(
        'error',
        'Cập nhật thất bại',
        'Đã có lỗi xảy ra khi cập nhật thông tin học viên',
      );
    }
  };

  useEffect(() => {
    if (student) {
      form.setFieldsValue({
        ...student,
      });
    }
  }, [student, form]);

  return (
    <CardCustom title="Thông tin học viên">
      <Form form={form} layout="vertical" onFinish={onFinish}>
        <DynamicForm<Student> fields={studentFormFields} />

        <Button type="primary" htmlType="submit">
          Cập nhật
        </Button>
      </Form>
    </CardCustom>
  );
};

export default StudentInfoForm;
