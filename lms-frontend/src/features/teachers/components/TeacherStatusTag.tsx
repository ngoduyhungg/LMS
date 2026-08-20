import { Tag } from 'antd';
import { TEACHER_STATUS, type TeacherStatusType } from '../types/teacher-status-type';

interface TeacherStatusTagProps {
  status?: TeacherStatusType;
  statusText?: string;
}

const TeacherStatusTag = ({ status, statusText }: TeacherStatusTagProps) => {
  switch (status) {
    case TEACHER_STATUS.ACTIVE:
      return <Tag color="success">{statusText || 'Hoạt động'}</Tag>;

    case TEACHER_STATUS.PAUSED:
      return <Tag color="error">{statusText || 'Tạm nghỉ'}</Tag>;

    case TEACHER_STATUS.INACTIVE:
      return <Tag>{statusText || 'Ngừng công tác'}</Tag>;

    default:
      return <Tag>{statusText || 'Không xác định'}</Tag>;
  }
};

export default TeacherStatusTag;
