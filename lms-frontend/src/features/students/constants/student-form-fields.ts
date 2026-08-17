import { FormFieldType } from '@/shared/types/form-field-type';
import type { Student } from '../types/student-type';
import { USER_ROLE } from '@/features/users/types/user-role-type';
import { FormModalMode } from '@/shared/types/form-modal-mode-type';
import type { FormContext, FormField } from '@/shared/components/modal/ModalFormCustom';
import { getParentOptions } from '@/features/parents/api/parent-api';

export const studentFormFields: FormField<Student>[] = [
  {
    name: 'studentCode',
    label: 'Mã sinh viên',
    type: FormFieldType.Input,
    placeholder: 'Mã sinh viên',
    disabled: ({ role, mode }: FormContext) =>
      role !== USER_ROLE.ADMIN || mode === FormModalMode.CREATE,
  },

  {
    name: 'parentId',
    label: 'Phụ huynh',
    type: FormFieldType.SelectFetch,
    fetchOptions: getParentOptions,
    placeholder: 'Chọn phụ huynh',
  },

  {
    name: 'schoolName',
    label: 'Trường học',
    type: FormFieldType.Input,
    placeholder: 'Nhập tên trường học',
  },

  {
    name: 'gradeLevel',
    label: 'Lớp',
    type: FormFieldType.Input,
    placeholder: 'Vui lòng nhập lớp',
  },

  {
    name: 'entryAcademicLevel',
    label: 'Học lực đầu vào',
    type: FormFieldType.Input,
    placeholder: 'Nhập học lực đầu vào',
  },
  {
    name: 'latestTestScore',
    label: 'Điểm test gần nhất',
    type: FormFieldType.InputNumber,
    placeholder: 'Nhập điểm test gần nhất',
    disabled: ({ role }: FormContext) => role !== USER_ROLE.ADMIN,
  },

  // ===== Learning =====
  {
    name: 'learningGoal',
    label: 'Mục tiêu học tập',
    type: FormFieldType.TextArea,
    placeholder: 'Nhập mục tiêu học tập',
  },

  // ===== Internal =====
  {
    name: 'note',
    label: 'Ghi chú',
    type: FormFieldType.TextArea,
    placeholder: 'Nhập ghi chú',
  },
];
