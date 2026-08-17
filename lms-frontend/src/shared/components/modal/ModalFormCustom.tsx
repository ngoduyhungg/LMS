import { useEffect, useState } from 'react';
import { FormModalMode, type FormModalModeType } from '../../types/form-modal-mode-type';
import ModalCustom from './ModalCustom';
import { useNotification } from '@/shared/hooks/useNotification';
import { Button, Form, Tabs } from 'antd';
import { formatSectionFieldsFormValues } from '@/shared/utils/form';
import DynamicForm from '../form/DynamicForm';
import type { UserRole } from '@/features/users/types/user-role-type';
import type { FormFieldTypeKey } from '@/shared/types/form-field-type';

export interface FormContext {
  role: UserRole;
  mode: FormModalModeType;
}

export interface FormField<T> {
  name: keyof T;

  label: string;

  type: FormFieldTypeKey;

  placeholder?: string;

  disabled?: any;

  hidden?: any;

  rules?: any[];

  options?: {
    label: string;
    value: string | number;
  }[];

  fetchOptions?: () => Promise<any>;

  col?: number;

  props?: any;

  icon?: any;

  onChange?: (value: any, options: any, form: any) => void;
}

export interface SectionForm {
  key: string;
  label: string;
  isDisabled?: boolean; // Chỉ dùng để disable toàn bộ section, không phụ thuộc vào từng field (Dùng cho trường hợp muốn sử dụng section này ở Pages khác)
  isHidden?: boolean | ((dataForm: { dataForm: any }) => boolean);
  fields: FormField<any>[];
}

interface ModalFormCustomProps<T> {
  open: boolean;

  title: string;

  mode: FormModalModeType;

  initialValues?: Partial<T> | null;

  loading?: boolean;

  sections: SectionForm[];

  disabled?: boolean;

  onCancel: () => void;

  onSuccess: () => void;

  onSubmit?: (values: T) => Promise<void>;
}

const ModalFormCustom = <T,>({
  open,
  title,
  mode,
  initialValues,
  sections,
  onCancel,
  onSuccess,
  onSubmit,
  disabled,
}: ModalFormCustomProps<T>) => {
  const [form] = Form.useForm();
  const dataForm = Form.useWatch([], form);

  const [loading, setLoading] = useState(false);
  const { showNotification } = useNotification();

  useEffect(() => {
    if (!open) {
      form.resetFields();
      return;
    }

    if (initialValues) {
      const formattedValues = formatSectionFieldsFormValues(initialValues as T, sections);

      form.setFieldsValue(formattedValues);
    }
  }, [open, initialValues, sections, form]);

  const handleSubmit = async (values: T) => {
    try {
      setLoading(true);

      const formattedValues = formatSectionFieldsFormValues(values, sections);

      if (onSubmit) {
        await onSubmit(formattedValues);
      }

      showNotification('success', 'Thành công', 'Dữ liệu đã được lưu thành công');

      form.resetFields();

      onCancel();

      onSuccess();
    } catch (error: any) {
      showNotification(
        'error',
        'Lỗi',
        error?.response?.data?.message || 'Không thể lưu dữ liệu. Vui lòng thử lại',
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <ModalCustom
      open={open}
      title={
        mode === FormModalMode.CREATE
          ? `Thêm ${title.toLowerCase()}`
          : mode === FormModalMode.EDIT
            ? `Cập nhật ${title.toLowerCase()}`
            : `Thông tin ${title.toLowerCase()}`
      }
      onCancel={onCancel}
      footer={null}
    >
      <Form form={form} layout="vertical" onFinish={handleSubmit}>
        <Tabs
          items={sections
            .filter((section) => {
              const isHidden =
                typeof section.isHidden === 'function'
                  ? section.isHidden({ dataForm })
                  : section.isHidden;

              return !isHidden;
            })
            .map((section) => ({
              key: section.key,
              label: section.label,
              children: (
                <DynamicForm<T>
                  fields={section.fields}
                  disabled={section.isDisabled || disabled}
                  mode={mode}
                />
              ),
            }))}
        />

        <div className="flex justify-end gap-2">
          <Button onClick={onCancel}>Hủy</Button>

          <Button type="primary" htmlType="submit" loading={loading} disabled={disabled}>
            Lưu
          </Button>
        </div>
      </Form>
    </ModalCustom>
  );
};

export default ModalFormCustom;
