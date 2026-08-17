import { FormFieldType } from '@/shared/types/form-field-type';
import { getStudentOptions } from '@/features/students/api/student-api';
import { leaveRequestStatusOptions } from './leave-request-status-options';
import { USER_ROLE, type UserRole } from '@/features/users/types/user-role-type';
import { getCourseClassSessionOptions } from '@/features/course-class-session/api/course-class-session-api';

export const leaveRequestFilters = [
  {
    name: 'studentId',
    type: FormFieldType.SelectFetch,
    fetchOptions: getStudentOptions,
    placeholder: 'Chọn học viên',
    hidden: ({ role }: { role: UserRole }) => role !== USER_ROLE.ADMIN,
  },
  {
    name: 'sessionId',
    type: FormFieldType.SelectFetch,
    fetchOptions: getCourseClassSessionOptions,
    placeholder: 'Chọn buổi học',
  },
  {
    name: 'status',
    type: FormFieldType.Select,
    placeholder: 'Trạng thái',
    options: leaveRequestStatusOptions,
  },
];
