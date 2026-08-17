import { Dayjs } from 'dayjs';

import type { FormInstance } from 'antd';
import type { Promotion } from '../types/promotion-type';
import { FormFieldType } from '@/shared/types/form-field-type';
import type { FormField } from '@/shared/components/modal/ModalFormCustom';
import { disableEndDateNotPast, disableStartDateNotPast } from '@/shared/utils/validate-date';
import { discountTypePromotionOptions } from './promotion-options';

export const promotionFormFields: FormField<Promotion>[] = [
  {
    name: 'promoCode',
    label: 'Mã khuyến mãi',
    type: FormFieldType.Input,
    placeholder: 'Mã khuyến mãi',
    disabled: true,
  },
  {
    name: 'name',
    label: 'Tên khuyến mãi',
    type: FormFieldType.Input,
    placeholder: 'Nhập tên khuyến mãi',
    rules: [
      {
        required: true,
        message: 'Vui lòng nhập tên khuyến mãi',
      },
    ],
  },
  {
    name: 'discountType',
    label: 'Loại khuyến mãi',
    type: FormFieldType.Select,
    options: discountTypePromotionOptions,
    placeholder: 'Chọn loại khuyến mãi',
    rules: [
      {
        required: true,
        message: 'Vui lòng chọn loại khuyến mãi',
      },
    ],
  },
  {
    name: 'discountValue',
    label: 'Giá trị khuyến mãi',
    type: FormFieldType.InputNumber,
    placeholder: 'Nhập giá trị khuyến mãi',
    rules: [
      {
        required: true,
        message: 'Vui lòng nhập giá trị khuyến mãi',
      },
    ],
    props: {
      isCurrency: true,
    },
  },
  {
    name: 'startDate',
    label: 'Ngày bắt đầu',
    type: FormFieldType.DatePicker,
    props: (form: FormInstance) => ({
      disabledDate: (current: Dayjs) =>
        disableStartDateNotPast(current, form.getFieldValue('endDate')),
    }),
    placeholder: 'Chọn ngày bắt đầu',
    rules: [
      {
        required: true,
        message: 'Vui lòng chọn ngày bắt đầu',
      },
    ],
  },
  {
    name: 'endDate',
    label: 'Ngày kết thúc',
    type: FormFieldType.DatePicker,
    props: (form: FormInstance) => ({
      disabledDate: (current: Dayjs) =>
        disableEndDateNotPast(current, form.getFieldValue('startDate')),
    }),
    placeholder: 'Chọn ngày kết thúc',
    rules: [
      {
        required: true,
        message: 'Vui lòng chọn ngày kết thúc',
      },
    ],
  },
  {
    name: 'note',
    label: 'Ghi chú',
    type: FormFieldType.TextArea,
    placeholder: 'Nhập ghi chú',
  },
];
