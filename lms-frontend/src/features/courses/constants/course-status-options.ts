import { CourseStatus } from '../types/course-type';

export const courseStatusOptions = [
  { label: 'Bản nháp', value: CourseStatus.DRAFT },
  { label: 'Đang mở', value: CourseStatus.OPEN },
  { label: 'Đã đóng', value: CourseStatus.CLOSED },
  { label: 'Đã xóa', value: CourseStatus.DELETED },
];
