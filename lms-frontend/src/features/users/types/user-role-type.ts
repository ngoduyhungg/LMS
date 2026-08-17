export const USER_ROLE = {
  ADMIN: 'ADMIN',
  INSTRUCTOR: 'INSTRUCTOR',
  STUDENT: 'STUDENT',
} as const;

export type UserRole = (typeof USER_ROLE)[keyof typeof USER_ROLE];