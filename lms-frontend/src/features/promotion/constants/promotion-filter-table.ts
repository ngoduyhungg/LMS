import { FormFieldType } from '@/shared/types/form-field-type';
import { discountTypePromotionOptions } from './promotion-options';
import { promotionStatusOptions } from './course-status-options';

export const promotionFilters = [
  {
    name: 'keySearch',
    type: FormFieldType.Input,
    placeholder: 'Tìm kiếm theo tên lớp học...',
  },
  {
    name: 'discountType',
    label: 'Loại khuyến mãi',
    type: FormFieldType.Select,
    options: discountTypePromotionOptions,
    placeholder: 'Chọn loại khuyến mãi',
  },
  {
    name: 'startDate',
    label: 'Ngày bắt đầu',
    type: FormFieldType.DatePicker,
    placeholder: 'Chọn ngày bắt đầu',
  },
  {
    name: 'endDate',
    label: 'Ngày kết thúc',
    type: FormFieldType.DatePicker,
    placeholder: 'Chọn ngày kết thúc',
  },
  {
    name: 'status',
    type: FormFieldType.Select,
    placeholder: 'Trạng thái',
    options: promotionStatusOptions,
  },
];
