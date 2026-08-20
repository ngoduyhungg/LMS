import { useAppSelector } from '@/app/redux/hooks';
import { Col, Form } from 'antd';
import { type FormModalModeType } from '@/shared/types/form-modal-mode-type';
import RowCustom from '../row/RowCustom';
import InputCustom from '../input/InputCustom';
import UploadImageCustom from '../upload/UploadImageCustom';
import InputNumberCustom from '../input/InputNumberCustom';
import InputPasswordCustom from '../input/InputPasswordCustom';
import SelectCustom from '../select/SelectCustom';
import SelectFetchCustom from '../select/SelectFetchCustom';
import TimePickerCustom from '../timepicker/TimePickerCustom';
import DatePickerCustom from '../datepicker/DatePickerCustom';
import InputTextAreaCustom from '../input/InputTextAreaCustom';
import { FormFieldType } from '@/shared/types/form-field-type';
import type { UserRole } from '@/features/users/types/user-role-type';
import type { FormField } from '../modal/ModalFormCustom';
import DateTimePickerCustom from '../datetimepicker/DateTimePickerCustom';

interface DynamicFormProps<T> {
  fields: FormField<T>[];
  disabled?: boolean;
  mode?: FormModalModeType;
}

const DynamicForm = <T,>({ fields, disabled, mode }: DynamicFormProps<T>) => {
  const { user } = useAppSelector((state) => state.auth);
  const form = Form.useFormInstance();

  return (
    <RowCustom>
      {fields
        .filter((field) => {
          const isHidden =
            typeof field.hidden === 'function'
              ? field.hidden({ mode, role: user?.role as UserRole })
              : field.hidden;

          return !isHidden;
        })
        .map((field) => (
          <Col key={field.name as string} span={field.col || 12}>
            <Form.Item name={field.name as string} label={field.label} rules={field.rules}>
              {(() => {
                const isDisabled =
                  typeof field.disabled === 'function'
                    ? field.disabled({ role: user?.role as UserRole, mode })
                    : field.disabled;

                const fieldProps =
                  typeof field.props === 'function' ? field.props(form) : field.props;

                switch (field.type) {
                  case FormFieldType.Input:
                    return (
                      <InputCustom
                        placeholder={field.placeholder}
                        disabled={isDisabled || disabled}
                        prefix={field.icon ? <field.icon /> : null}
                        {...fieldProps}
                      />
                    );
                  case FormFieldType.InputPassword:
                    return (
                      <InputPasswordCustom
                        placeholder={field.placeholder}
                        disabled={isDisabled || disabled}
                        prefix={field.icon ? <field.icon /> : null}
                        {...fieldProps}
                      />
                    );
                  case FormFieldType.ImageUpload:
                    return (
                      <UploadImageCustom
                        value={form.getFieldValue(field.name)}
                        onChange={(value) => form.setFieldsValue({ [field.name]: value })}
                        {...fieldProps}
                      />
                    );
                  case FormFieldType.InputNumber:
                    return (
                      <InputNumberCustom
                        placeholder={field.placeholder}
                        disabled={isDisabled || disabled}
                        {...fieldProps}
                      />
                    );
                  case FormFieldType.Select:
                    return (
                      <SelectCustom
                        placeholder={field.placeholder}
                        options={field.options}
                        disabled={isDisabled || disabled}
                        {...fieldProps}
                      />
                    );
                  case FormFieldType.SelectFetch:
                    return (
                      <SelectFetchCustom
                        placeholder={field.placeholder}
                        fetchOptions={field.fetchOptions}
                        onChange={(value, options) =>
                          field.onChange && field.onChange(value, options, form)
                        }
                        disabled={isDisabled || disabled}
                        {...fieldProps}
                      />
                    );
                  case FormFieldType.DatePicker:
                    return (
                      <DatePickerCustom
                        placeholder={field.placeholder}
                        disabled={isDisabled || disabled}
                        {...fieldProps}
                      />
                    );
                  case FormFieldType.DateTimePicker:
                    return (
                      <DateTimePickerCustom
                        placeholder={field.placeholder}
                        disabled={isDisabled || disabled}
                        {...fieldProps}
                      />
                    );
                  case FormFieldType.TimePicker:
                    return (
                      <TimePickerCustom
                        placeholder={field.placeholder}
                        disabled={isDisabled || disabled}
                        {...fieldProps}
                      />
                    );
                  case FormFieldType.TextArea:
                    return (
                      <InputTextAreaCustom
                        placeholder={field.placeholder}
                        disabled={isDisabled || disabled}
                        {...fieldProps}
                      />
                    );
                  default:
                    return null;
                }
              })()}
            </Form.Item>
          </Col>
        ))}
    </RowCustom>
  );
};

export default DynamicForm;
