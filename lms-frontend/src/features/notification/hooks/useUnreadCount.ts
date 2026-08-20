import { useQuery } from '@tanstack/react-query';
import { notificationApi } from '../api/notification-api';
import { NOTIFICATION_QUERY_KEY } from '../constants/query-key';

export const useUnreadCount = (userId?: string) => {
  return useQuery({
    queryKey: [NOTIFICATION_QUERY_KEY.NOTIFICATIONS_UNREAD, userId],
    queryFn: () => notificationApi.getUnreadCount(userId!),
    enabled: !!userId,
  });
};
