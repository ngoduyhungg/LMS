import { FormFieldType } from '@/shared/types/form-field-type';
import { USER_ROLE } from '@/features/users/types/user-role-type';
import { FormModalMode } from '@/shared/types/form-modal-mode-type';
import type { Teacher } from '../types/teacher-type';
import { teacherRoleOptions } from './teacher-role-options';
import type { FormContext, FormField } from '@/shared/components/modal/ModalFormCustom';

export const teacherFormFields: FormField<Teacher>[] = [
  {
    name: 'teacherCode',
    label: 'Mã giáo viên',
    type: FormFieldType.Input,
    placeholder: 'Mã giáo viên',
    disabled: ({ role, mode }: FormContext) =>
      role !== USER_ROLE.ADMIN || mode === FormModalMode.CREATE,
  },
  {
    name: 'teacherRole',
    type: FormFieldType.Select,
    label: 'Vai trò',
    placeholder: 'Chọn vai trò',
    options: teacherRoleOptions,
  },
  {
    name: 'specialization',
    label: 'Chuyên môn',
    type: FormFieldType.Input,
    placeholder: 'Nhập chuyên môn',
  },
  {
    name: 'qualification',
    label: 'Trình độ',
    type: FormFieldType.Input,
    placeholder: 'Nhập trình độ',
  },
  {
    name: 'yearsOfExperience',
    label: 'Số năm kinh nghiệm',
    type: FormFieldType.InputNumber,
    placeholder: 'Nhập số năm kinh nghiệm',
  },
  {
    name: 'note',
    label: 'Ghi chú',
    type: FormFieldType.TextArea,
    placeholder: 'Nhập ghi chú',
  },
];
