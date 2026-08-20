import type { Stat } from '@/features/dashboard/types/start-data-type';
import type { RecentActivityItem } from './recent-activity-type';
import type { TodayClassesItem } from './today-classes-type';

export type Dashboard = {
  statData: Stat[];
  recentActivityData: RecentActivityItem[];
  todayClasses: TodayClassesItem[];
};
