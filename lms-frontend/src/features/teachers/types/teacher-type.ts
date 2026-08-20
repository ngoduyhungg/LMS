import type { User } from '@/features/users/types/user-type';
import type { TeacherRoleType } from './teacher-role';
import type { TeacherStatusType } from './teacher-status-type';

export interface Teacher extends User {
  id: string;

  userId: string;

  teacherCode: string;

  teacherRole: TeacherRoleType;

  teacherRoleText: string;

  specialization?: string | null;

  qualification?: string | null;

  yearsOfExperience?: number | null;

  note?: string | null;

  teacherStatus: TeacherStatusType;

  teacherStatusText: string;
}
