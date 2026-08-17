import { Tag } from 'antd';
import { TuitionInvoiceStatus, type TuitionInvoiceStatusType } from '../types/tuition-invoice-type';

interface TuitionInvoiceStatusTagProps {
  status?: TuitionInvoiceStatusType;
  statusText?: string;
}

const TuitionInvoiceStatusTag = ({ status, statusText }: TuitionInvoiceStatusTagProps) => {
  switch (status) {
    case TuitionInvoiceStatus.UNPAID:
      return <Tag color="blue">{statusText || 'Chưa thanh toán'}</Tag>;

    case TuitionInvoiceStatus.PARTIAL:
      return <Tag color="orange">{statusText || 'Đã thanh toán một phần'}</Tag>;

    case TuitionInvoiceStatus.PAID:
      return <Tag color="green">{statusText || 'Đã thanh toán'}</Tag>;

    default:
      return <Tag>{statusText || 'Không xác định'}</Tag>;
  }
};

export default TuitionInvoiceStatusTag;
