import type { FormContext, FormField } from '@/shared/components/modal/ModalFormCustom';
import { FormFieldType } from '@/shared/types/form-field-type';
import { getStudentOptions } from '@/features/students/api/student-api';
import { USER_ROLE } from '@/features/users/types/user-role-type';
import type { TuitionInvoice } from '../types/tuition-invoice-type';
import { getCourseClassOptions } from '@/features/course-class/api/course-class-api';
import { FormModalMode } from '@/shared/types/form-modal-mode-type';
import { getPromotionsOptions } from '@/features/promotion/api/promotion-api';
import { calculateInvoice } from '../utils/calculate-invoice';

export const tuitionInvoiceFormFields: FormField<TuitionInvoice>[] = [
  {
    name: 'studentId',
    label: 'Học viên',
    type: FormFieldType.SelectFetch,
    fetchOptions: getStudentOptions,
    placeholder: 'Chọn học viên',
    rules: [{ required: true, message: 'Vui lòng chọn học viên' }],
    disabled: ({ role, mode }: FormContext) =>
      role !== USER_ROLE.ADMIN || mode === FormModalMode.EDIT,
  },
  {
    name: 'courseClassId',
    label: 'Lớp học',
    type: FormFieldType.SelectFetch,
    fetchOptions: getCourseClassOptions,
    placeholder: 'Chọn lớp học',
    rules: [{ required: true, message: 'Vui lòng chọn lớp học' }],
    disabled: ({ mode }: FormContext) => mode === FormModalMode.EDIT,
    onChange: (value, options, form) => {
      const selectedCourseClass = options.find((option: any) => option.value === value);

      form.setFieldsValue({
        originalAmount: !value || !selectedCourseClass ? undefined : selectedCourseClass.tuitionFee,
        promotionId: undefined,
        discountAmount: 0,
        finalAmount: !value || !selectedCourseClass ? undefined : selectedCourseClass.tuitionFee,
      });
    },
  },
  {
    name: 'promotionId',
    label: 'Khuyến mãi',
    type: FormFieldType.SelectFetch,
    fetchOptions: getPromotionsOptions,
    placeholder: 'Chọn khuyến mãi',
    disabled: ({ mode }: FormContext) => mode === FormModalMode.EDIT,
    onChange: (value, options, form) => {
      const getCourseClassTuitionFee = form.getFieldValue('originalAmount') || 0;

      const selectedPromotion = options.find((p: any) => p.value === value);

      if (!value || !selectedPromotion) {
        form.setFieldsValue({
          discountType: undefined,
          discountAmount: 0,
          finalAmount: getCourseClassTuitionFee,
        });

        return;
      }

      const { discountAmount, finalAmount } = calculateInvoice(
        getCourseClassTuitionFee,
        selectedPromotion.discountType,
        selectedPromotion.discountValue,
      );

      form.setFieldsValue({
        discountAmount,
        finalAmount,
      });
    },
  },
  {
    name: 'originalAmount',
    label: 'Học phí gốc',
    type: FormFieldType.InputNumber,
    placeholder: 'Học phí gốc',
    props: {
      isCurrency: true,
    },
    disabled: true,
  },
  {
    name: 'discountAmount',
    label: 'Số tiền / chiết khấu giảm',
    type: FormFieldType.InputNumber,
    placeholder: 'Số tiền / chiết khấu giảm',
    props: {
      isCurrency: true,
    },
    disabled: true,
  },
  {
    name: 'finalAmount',
    label: 'Tổng tiền',
    type: FormFieldType.InputNumber,
    placeholder: 'Tổng tiền',
    props: {
      isCurrency: true,
    },
    disabled: true,
  },
  {
    name: 'dueDate',
    label: 'Hạn thanh toán',
    type: FormFieldType.DatePicker,
    placeholder: 'Chọn hạn thanh toán',
  },
  {
    name: 'note',
    label: 'Ghi chú',
    type: FormFieldType.TextArea,
    placeholder: 'Nhập ghi chú',
  },
];
