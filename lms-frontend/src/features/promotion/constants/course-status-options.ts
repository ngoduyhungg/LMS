import { PromotionStatus } from '../types/promotion-type';

export const promotionStatusOptions = [
  { label: 'Đang hoạt động', value: PromotionStatus.ACTIVE },
  { label: 'Ngưng hoạt động', value: PromotionStatus.INACTIVE },
  { label: 'Hết hạn', value: PromotionStatus.EXPIRED },
];
