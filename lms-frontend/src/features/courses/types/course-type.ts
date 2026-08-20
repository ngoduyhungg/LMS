export interface Category {
  id: string;
  name: string;
  slug: string;
  description?: string;
}

export interface Course {
  id: string;
  slug: string;
  title: string;
  summary?: string;
  description?: string;
  price?: number;
  thumbnailUrl?: string;
  level: CourseLevelType;
  categoryId?: string;
  category?: Category;
  status: CourseStatusType;
  statusText?: string;
  createdAt?: string;
  updatedAt?: string;
  instructorName?: string;
  // Các field legacy (sẽ dần bị loại bỏ bởi backend)
  courseCode?: string;
  totalSessions?: number;
}

export const CourseStatus = {
  DRAFT: 'DRAFT',
  PUBLISHED: 'PUBLISHED',
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

export interface LessonResource {
  id?: string;
  title: string;
  fileUrl: string;
  fileType: string;
  fileSizeBytes?: number;
}

export interface Lesson {
  id: string;
  title: string;
  content?: string;
  videoUrl?: string;
  durationSeconds?: number;
  lessonType: string;
  isPreview: boolean;
  sortOrder: number;
  resources?: LessonResource[];
}

export interface Module {
  id: string;
  title: string;
  sortOrder?: number;
  lessons?: Lesson[];
}