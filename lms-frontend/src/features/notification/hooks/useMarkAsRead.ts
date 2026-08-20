import { useMutation, useQueryClient } from '@tanstack/react-query';
import { notificationApi } from '../api/notification-api';
import { NOTIFICATION_QUERY_KEY } from '../constants/query-key';

export const useMarkAsRead = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: string) => notificationApi.markAsRead(id),

    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: [NOTIFICATION_QUERY_KEY.NOTIFICATIONS],
      });

      queryClient.invalidateQueries({
        queryKey: [NOTIFICATION_QUERY_KEY.NOTIFICATIONS_UNREAD],
      });
    },
  });
};
