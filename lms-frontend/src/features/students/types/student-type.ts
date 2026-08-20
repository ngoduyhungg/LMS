import type { User } from '@/features/users/types/user-type';
import type { StudentStatusType } from './student-status-type';

export interface Student extends User {
  id: string;

  userId: string;

  studentCode: string;

  parentId?: string | null;

  parentName?: string | null;

  schoolName?: string | null;

  gradeLevel?: string | null;

  entryAcademicLevel?: string | null;

  latestTestScore?: number | null;

  learningGoal?: string | null;

  note?: string | null;

  studentStatus: StudentStatusType;

  studentStatusText: string;
}
