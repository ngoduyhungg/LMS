import { Tag } from 'antd';
import { LeaveRequestStatus, type LeaveRequestStatusType } from '../types/leave-request-type';

interface LeaveRequestStatusTagProps {
  status?: LeaveRequestStatusType;
  statusText?: string;
}

const LeaveRequestStatusTag = ({ status, statusText }: LeaveRequestStatusTagProps) => {
  switch (status) {
    case LeaveRequestStatus.PENDING:
      return <Tag color="blue">{statusText || 'Chờ duyệt'}</Tag>;

    case LeaveRequestStatus.APPROVED:
      return <Tag color="green">{statusText || 'Đã phê duyệt'}</Tag>;

    case LeaveRequestStatus.REJECTED:
      return <Tag color="red">{statusText || 'Đã từ chối'}</Tag>;

    default:
      return <Tag>{statusText || 'Không xác định'}</Tag>;
  }
};

export default LeaveRequestStatusTag;
