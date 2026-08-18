export interface Course {
  id: string;
  courseCode: string;
  name: string;
  description: string;
  thumbnailUrl: string;
  level: CourseLevelType;
  totalSessions: number;
  status: CourseStatusType;
  statusText: string;
  createdAt: string;
  updatedAt: string;
}

export const CourseStatus = {
  DRAFT: 'DRAFT',
  OPEN: 'OPEN',
  CLOSED: 'CLOSED',
  DELETED: 'DELETED',
} as const;

export type CourseStatusType = (typeof CourseStatus)[keyof typeof CourseStatus];

export const CourseLevel = {
  BEGINNER: 'BEGINNER',
  INTERMEDIATE: 'INTERMEDIATE',
  ADVANCED: 'ADVANCED',
} as const;

export type CourseLevelType = (typeof CourseLevel)[keyof typeof CourseLevel];

export interface Category {
  id: string;
  name: string;
  slug: string;
  description?: string;
}