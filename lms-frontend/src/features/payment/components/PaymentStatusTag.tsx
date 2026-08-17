import { Tag } from 'antd';
import { PaymentStatus, type PaymentStatusType } from '../types/payment-type';

interface PaymentStatusTagProps {
  status?: PaymentStatusType;
  statusText?: string;
}

const PaymentStatusTag = ({ status, statusText }: PaymentStatusTagProps) => {
  switch (status) {
    case PaymentStatus.SUCCESS:
      return <Tag color="green">{statusText || 'Thành công'}</Tag>;

    case PaymentStatus.CANCELLED:
      return <Tag color="red">{statusText || 'Đã hủy'}</Tag>;

    default:
      return <Tag>{statusText || 'Không xác định'}</Tag>;
  }
};

export default PaymentStatusTag;
