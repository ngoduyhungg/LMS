import { useQuery } from '@tanstack/react-query';
import { notificationApi } from '../api/notification-api';
import { NOTIFICATION_QUERY_KEY } from '../constants/query-key';

export const useNotifications = (userId?: string) => {
  return useQuery({
    queryKey: [NOTIFICATION_QUERY_KEY.NOTIFICATIONS, userId],
    queryFn: () => notificationApi.getAll(userId!),
    enabled: !!userId,
  });
};
