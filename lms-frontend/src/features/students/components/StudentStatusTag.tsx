import { Tag } from 'antd';

import { STUDENT_STATUS, type StudentStatusType } from '../types/student-status-type';

interface StudentStatusTagProps {
  status?: StudentStatusType;
  statusText?: string;
}

const StudentStatusTag = ({ status, statusText }: StudentStatusTagProps) => {
  switch (status) {
    case STUDENT_STATUS.ACTIVE:
      return <Tag color="success">{statusText || 'Đang tham gia'}</Tag>;

    case STUDENT_STATUS.PAUSED:
      return <Tag color="error">{statusText || 'Đang tạm ngưng'}</Tag>;

    case STUDENT_STATUS.DROPPED:
      return <Tag>{statusText || 'Đã bỏ học'}</Tag>;

    default:
      return <Tag>{statusText || 'Không xác định'}</Tag>;
  }
};

export default StudentStatusTag;
