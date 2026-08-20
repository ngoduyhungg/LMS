export interface Promotion {
  id: string;
  promoCode: string;
  name: string;
  discountType: DiscountType;
  discountValue: number;
  startDate: string;
  endDate: string;
  status: PromotionStatusType;
  statusText: string;
  note: string;
  createdAt: string;
  updatedAt: string;
}

export const PromotionStatus = {
  ACTIVE: 'ACTIVE',
  INACTIVE: 'INACTIVE',
  EXPIRED: 'EXPIRED',
} as const;

export type PromotionStatusType = (typeof PromotionStatus)[keyof typeof PromotionStatus];

export const DiscountTypePromotion = {
  PERCENT: 'PERCENT',
  AMOUNT: 'AMOUNT',
} as const;

export type DiscountType = (typeof DiscountTypePromotion)[keyof typeof DiscountTypePromotion];
