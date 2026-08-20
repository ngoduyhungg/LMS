import { Tag } from 'antd';
import { PromotionStatus, type PromotionStatusType } from '../types/promotion-type';

interface PromotionStatusTagProps {
  status?: PromotionStatusType;
  statusText?: string;
}

const PromotionStatusTag = ({ status, statusText }: PromotionStatusTagProps) => {
  switch (status) {
    case PromotionStatus.ACTIVE:
      return <Tag color="blue">{statusText || 'Đang hoạt động'}</Tag>;

    case PromotionStatus.INACTIVE:
      return <Tag color="success">{statusText || 'Ngưng hoạt động'}</Tag>;

    case PromotionStatus.EXPIRED:
      return <Tag color="error">{statusText || 'Đã hết hạn'}</Tag>;

    default:
      return <Tag>{statusText || 'Không xác định'}</Tag>;
  }
};

export default PromotionStatusTag;
