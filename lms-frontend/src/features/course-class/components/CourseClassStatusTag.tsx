import { Tag } from 'antd';
import { CourseClassStatus, type CourseClassStatusType } from '../types/course-class-type';

interface CourseClassStatusTagProps {
  status?: CourseClassStatusType;
  statusText?: string;
}

const CourseClassStatusTag = ({ status, statusText }: CourseClassStatusTagProps) => {
  switch (status) {
    case CourseClassStatus.OPEN:
      return <Tag color="blue">{statusText || 'Bản nháp'}</Tag>;

    case CourseClassStatus.ONGOING:
      return <Tag color="success">{statusText || 'Đang mở'}</Tag>;

    case CourseClassStatus.CLOSED:
      return <Tag color="error">{statusText || 'Đã đóng'}</Tag>;

    default:
      return <Tag>{statusText || 'Không xác định'}</Tag>;
  }
};

export default CourseClassStatusTag;
