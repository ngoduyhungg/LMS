export const TEACHER_ROLE = {
  TEACHER: 'TEACHER', // Giảng dạy

  ASSISTANT: 'ASSISTANT', // Trợ giảng

  BOTH: 'BOTH', // Cả giảng dạy và trợ giảng
} as const;

export type TeacherRoleType = (typeof TEACHER_ROLE)[keyof typeof TEACHER_ROLE];
