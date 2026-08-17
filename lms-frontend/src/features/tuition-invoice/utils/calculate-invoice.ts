import {
  DiscountTypePromotion,
  type DiscountType,
} from '@/features/promotion/types/promotion-type';

export function calculateInvoice(
  originalAmount: number,
  discountType?: DiscountType,
  discountValue?: number,
  amountPaid = 0,
) {
  let discountAmount = 0;

  if (discountType === DiscountTypePromotion.AMOUNT) {
    discountAmount = discountValue ?? 0;
  }

  if (discountType === DiscountTypePromotion.PERCENT) {
    discountAmount = (originalAmount * (discountValue ?? 0)) / 100;
  }

  const finalAmount = Math.max(originalAmount - discountAmount, 0);

  const balanceAmount = Math.max(finalAmount - amountPaid, 0);

  return {
    discountAmount,
    finalAmount,
    balanceAmount,
  };
}
