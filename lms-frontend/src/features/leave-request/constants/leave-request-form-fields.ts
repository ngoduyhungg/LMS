import type { FormContext, FormField } from '@/shared/components/modal/ModalFormCustom';
import { FormFieldType } from '@/shared/types/form-field-type';
import { getStudentOptions } from '@/features/students/api/student-api';
import type { LeaveRequest } from '../types/leave-request-type';
import { USER_ROLE } from '@/features/users/types/user-role-type';
import { getCourseClassSessionOptions } from '@/features/course-class-session/api/course-class-session-api';

export const leaveRequestFormFields: FormField<LeaveRequest>[] = [
  {
    name: 'sessionId',
    label: 'Buổi học',
    type: FormFieldType.SelectFetch,
    fetchOptions: getCourseClassSessionOptions,
    placeholder: 'Chọn buổi học',
    rules: [{ required: true, message: 'Vui lòng chọn buổi học' }],
    col: 24,
  },
  {
    name: 'studentId',
    label: 'Học viên',
    type: FormFieldType.SelectFetch,
    fetchOptions: getStudentOptions,
    placeholder: 'Chọn học viên',
    rules: [{ required: true, message: 'Vui lòng chọn học viên' }],
    hidden: ({ role }: FormContext) => role !== USER_ROLE.ADMIN,
    disabled: true,
    col: 24,
  },
  {
    name: 'reason',
    label: 'Lý do',
    type: FormFieldType.TextArea,
    placeholder: 'Nhập lý do xin nghỉ',
    col: 24,
  },
];
