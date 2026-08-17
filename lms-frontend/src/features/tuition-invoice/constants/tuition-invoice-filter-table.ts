import { FormFieldType } from '@/shared/types/form-field-type';
import { getStudentOptions } from '@/features/students/api/student-api';
import { USER_ROLE, type UserRole } from '@/features/users/types/user-role-type';
import { tuitionInvoiceStatusOptions } from './tuition-invoice-status-options';
import { getCourseClassOptions } from '@/features/course-class/api/course-class-api';
import { getPromotionsOptions } from '@/features/promotion/api/promotion-api';

export const tuitionInvoiceFilters = [
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
    name: 'promotionId',
    type: FormFieldType.SelectFetch,
    fetchOptions: getPromotionsOptions,
    placeholder: 'Chọn khuyến mãi',
  },
  {
    name: 'dueDateFrom',
    label: 'Ngày bắt đầu',
    type: FormFieldType.DatePicker,
    placeholder: 'Từ ngày',
  },
  {
    name: 'dueDateTo',
    label: 'Ngày kết thúc',
    type: FormFieldType.DatePicker,
    placeholder: 'Đến ngày',
  },
  {
    name: 'status',
    type: FormFieldType.Select,
    placeholder: 'Trạng thái',
    options: tuitionInvoiceStatusOptions,
  },
];
