import { ActivityType } from '@/features/dashboard/constants/activity';

export type RecentActivityItem = {
  type: (typeof ActivityType)[keyof typeof ActivityType];
  title: string;
  message: string;
  date: string;
};
