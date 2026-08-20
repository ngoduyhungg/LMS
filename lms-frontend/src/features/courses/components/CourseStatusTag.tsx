import { Tag } from 'antd';
import { CourseStatus, type CourseStatusType } from '../types/course-type';

interface CourseStatusTagProps {
  status?: CourseStatusType;
  statusText?: string;
}

const CourseStatusTag = ({ status, statusText }: CourseStatusTagProps) => {
  switch (status) {
    case CourseStatus.DRAFT:
      return <Tag color="blue">{statusText || 'Bản nháp'}</Tag>;

    case CourseStatus.OPEN:
      return <Tag color="success">{statusText || 'Đang mở'}</Tag>;

    case CourseStatus.CLOSED:
      return <Tag color="error">{statusText || 'Đã đóng'}</Tag>;

    case CourseStatus.DELETED:
      return <Tag>{statusText || 'Đã xóa'}</Tag>;

    default:
      return <Tag>{statusText || 'Không xác định'}</Tag>;
  }
};

export default CourseStatusTag;
