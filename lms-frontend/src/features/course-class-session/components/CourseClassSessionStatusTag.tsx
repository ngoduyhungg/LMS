import { Tag } from 'antd';
import {
  CourseClassSessionStatus,
  type CourseClassSessionStatusType,
} from '../types/course-class-session-type';

interface CourseClassSessionStatusTagProps {
  status?: CourseClassSessionStatusType;
  statusText?: string;
}

const CourseClassSessionStatusTag = ({ status, statusText }: CourseClassSessionStatusTagProps) => {
  switch (status) {
    case CourseClassSessionStatus.SCHEDULED:
      return <Tag color="blue">{statusText || 'Đã lên lịch'}</Tag>;

    case CourseClassSessionStatus.DONE:
      return <Tag color="green">{statusText || 'Đã diễn ra'}</Tag>;

    case CourseClassSessionStatus.CANCELLED:
      return <Tag color="red">{statusText || 'Đã hủy'}</Tag>;

    case CourseClassSessionStatus.RESCHEDULED:
      return <Tag color="orange">{statusText || 'Đã dời lịch'}</Tag>;
    default:
      return <Tag>{statusText || 'Không xác định'}</Tag>;
  }
};

export default CourseClassSessionStatusTag;
