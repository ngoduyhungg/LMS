import { FormFieldType } from '@/shared/types/form-field-type';
import { enrollmentStatusOptions } from './enrollment-status-options';
import { getCourseOptions } from '@/features/courses/api/course-api';
import { getStudentOptions } from '@/features/students/api/student-api';
import { getCourseClassOptions } from '@/features/course-class/api/course-class-api';
import { USER_ROLE, type UserRole } from '@/features/users/types/user-role-type';

export const enrollmentFilters = [
  {
    name: 'keySearch',
    type: FormFieldType.Input,
    placeholder: 'Tìm kiếm theo tên học sinh, lớp học...',
  },
  {
    name: 'studentId',
    type: FormFieldType.SelectFetch,
    fetchOptions: getStudentOptions,
    placeholder: 'Chọn học viên',
    hidden: ({ role }: { role: UserRole }) => role !== USER_ROLE.ADMIN,
  },
  {
    name: 'courseClassId',
    type: FormFieldType.SelectFetch,
    fetchOptions: getCourseClassOptions,
    placeholder: 'Chọn lớp học',
  },
  {
    name: 'courseId',
    type: FormFieldType.SelectFetch,
    fetchOptions: getCourseOptions,
    placeholder: 'Chọn khóa học',
  },
  {
    name: 'status',
    type: FormFieldType.Select,
    placeholder: 'Trạng thái',
    options: enrollmentStatusOptions,
  },
];
