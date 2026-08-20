export const STUDENT_STATUS = {
  ACTIVE: 'ACTIVE', // Đang tham gia

  PAUSED: 'PAUSED', // Đã tạm ngưng

  DROPPED: 'DROPPED', // Đã nghỉ học
} as const;

export type StudentStatusType = (typeof STUDENT_STATUS)[keyof typeof STUDENT_STATUS];
