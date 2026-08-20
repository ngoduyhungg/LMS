import type { FormField } from '@/shared/components/modal/ModalFormCustom';
import { FormFieldType } from '@/shared/types/form-field-type';
import type { Payment } from '../types/payment-type';
import { getTuitionInvoiceOptions } from '@/features/tuition-invoice/api/tuition-invoice-api';
import { paymentMethodOptions } from './payment-method-options';

export const paymentFormFields: FormField<Payment>[] = [
  {
    name: 'invoiceId',
    label: 'Hóa đơn',
    type: FormFieldType.SelectFetch,
    fetchOptions: getTuitionInvoiceOptions,
    placeholder: 'Chọn hóa đơn',
    rules: [{ required: true, message: 'Vui lòng chọn hóa đơn' }],
    onChange: (value, options, form) => {
      const selectedInvoice = options.find((option: any) => option.value === value);

      if (!selectedInvoice) {
        form.setFieldsValue({
          amountPaid: undefined,
          balanceAmount: undefined,
        });

        return;
      }

      if (selectedInvoice) {
        form.setFieldsValue({
          amountPaid: selectedInvoice.amountPaid,
          balanceAmount: selectedInvoice.balanceAmount,
        });
      }
    },
  },
  {
    name: 'amountPaid',
    label: 'Số tiền đã thanh toán',
    type: FormFieldType.InputNumber,
    props: {
      isCurrency: true,
    },
    disabled: true,
    placeholder: 'Số tiền đã thanh toán',
  },
  {
    name: 'balanceAmount',
    label: 'Số tiền thanh toán còn lại',
    type: FormFieldType.InputNumber,
    props: {
      isCurrency: true,
    },
    disabled: true,
    placeholder: 'Số tiền thanh toán còn lại',
  },
  {
    name: 'paymentMethod',
    label: 'Phương thức thanh toán',
    type: FormFieldType.Select,
    options: paymentMethodOptions,
    placeholder: 'Chọn phương thức thanh toán',
  },
  {
    name: 'paidAmount',
    label: 'Số tiền thanh toán',
    type: FormFieldType.InputNumber,
    props: {
      isCurrency: true,
    },
    placeholder: 'Nhập số tiền thanh toán',
    rules: [
      {
        required: true,
        message: 'Vui lòng nhập số tiền thanh toán',
      },
      ({ getFieldValue }: any) => ({
        validator(_: any, value: string) {
          if (!value || value <= getFieldValue('balanceAmount')) {
            return Promise.resolve();
          }

          return Promise.reject(
            new Error('Số tiền thanh toán không được lớn hơn số tiền thanh toán còn lại'),
          );
        },
      }),
    ],
  },
  {
    name: 'note',
    label: 'Ghi chú',
    type: FormFieldType.TextArea,
    placeholder: 'Nhập ghi chú',
  },
];
