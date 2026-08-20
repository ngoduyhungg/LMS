import { AttendanceStatus } from '../types/attendance-type';

export const attendanceOptions = [
  {
    label: 'Có mặt',
    value: AttendanceStatus.PRESENT,
  },
  {
    label: 'Vắng',
    value: AttendanceStatus.ABSENT,
  },
  {
    label: 'Đi trễ',
    value: AttendanceStatus.LATE,
  },
  {
    label: 'Có phép',
    value: AttendanceStatus.EXCUSED,
  },
];
