import { axiosClient } from '@/shared/lib/axios';

const API_URL_PREFIX = '/notifications';

export const notificationApi = {
  getAll: async (userId: string) => {
    const res = await axiosClient.get(`${API_URL_PREFIX}?userId=${userId}`);
    return res.data.data;
  },

  getUnreadCount: async (userId: string) => {
    const res = await axiosClient.get(`${API_URL_PREFIX}/unread-count?userId=${userId}`);
    return res.data.data;
  },

  markAllAsRead: async (userId: string) => {
    const res = await axiosClient.patch(`${API_URL_PREFIX}/read-all?userId=${userId}`);
    return res.data.data;
  },

  markAsRead: async (id: string) => {
    const res = await axiosClient.patch(`${API_URL_PREFIX}/${id}/read`);
    return res.data.data;
  },
};
