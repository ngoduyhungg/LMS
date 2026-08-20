export const TEACHER_STATUS = {
  ACTIVE: 'ACTIVE', // Đang dạy bình thường

  PAUSED: 'PAUSED', // Tạm nghỉ (có thể quay lại)

  INACTIVE: 'INACTIVE', // Nghỉ việc / dừng hợp tác
} as const;

export type TeacherStatusType = (typeof TEACHER_STATUS)[keyof typeof TEACHER_STATUS];
