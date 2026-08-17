import { Tag } from 'antd';
import { EnrollmentStatus, type EnrollmentStatusType } from '../types/enrollment-type';

interface EnrollmentStatusTagProps {
  status?: EnrollmentStatusType;
  statusText?: string;
}

const EnrollmentStatusTag = ({ status, statusText }: EnrollmentStatusTagProps) => {
  switch (status) {
    case EnrollmentStatus.ACTIVE:
      return <Tag color="blue">{statusText || 'Đang học'}</Tag>;

    case EnrollmentStatus.PAUSED:
      return <Tag color="orange">{statusText || 'Bảo lưu'}</Tag>;

    case EnrollmentStatus.DROPPED:
      return <Tag color="purple">{statusText || 'Đã thôi học'}</Tag>;

    case EnrollmentStatus.COMPLETED:
      return <Tag color="green">{statusText || 'Hoàn thành'}</Tag>;

    case EnrollmentStatus.CANCELLED:
      return <Tag color="red">{statusText || 'Hủy đăng ký'}</Tag>;

    default:
      return <Tag>{statusText || 'Không xác định'}</Tag>;
  }
};

export default EnrollmentStatusTag;
