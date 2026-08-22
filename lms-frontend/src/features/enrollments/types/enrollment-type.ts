export interface LessonProgress {
  lessonId: string | number;
  isCompleted: boolean;
  watchedSeconds: number;
}

export interface EnrollmentResponse {
  id: string | number;
  courseId: string | number;
  status: 'ACTIVE' | 'COMPLETED' | 'CANCELLED' | 'EXPIRED';
  progressPercentage: number;
  lastAccessedLessonId?: string | number | null;
  enrolledAt: string;
  completedAt?: string | null;
  lessonProgresses: LessonProgress[];
}

// Giữ lại Legacy Type để không làm hỏng giao diện Admin/Teacher cũ
export type Enrollment = {
  id: string;
  studentId: string;
  studentName: string | null;
  courseClassId: string;
  courseClassName: string | null;
  courseId: string;
  courseName: string;
  status: EnrollmentStatusType;
  statusText?: string;
  note: string | null;
  enrolledAt: string;
  createdAt: string;
  updatedAt: string;
};

export const EnrollmentStatus = {
  ACTIVE: 'ACTIVE',
  PAUSED: 'PAUSED',
  DROPPED: 'DROPPED',
  COMPLETED: 'COMPLETED',
  CANCELLED: 'CANCELLED',
} as const;

export type EnrollmentStatusType = (typeof EnrollmentStatus)[keyof typeof EnrollmentStatus];