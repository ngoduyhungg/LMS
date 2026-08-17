import { axiosClient } from '@/shared/lib/axios';

const API_URL_PREFIX = '/users';

export const userRoleAdminApi = {
  getAll: async (params: any) => {
    const res = await axiosClient.get(`${API_URL_PREFIX}`, { params });

    return res.data;
  },
  
  create: async (payload: any) => {
    const res = await axiosClient.post(`${API_URL_PREFIX}`, payload);

    return res.data;
  },

  update: async (userId: string, payload: any) => {
    const res = await axiosClient.patch(`${API_URL_PREFIX}/${userId}`, payload);

    return res.data;
  },

  active: async (userId: string) => {
    const res = await axiosClient.patch(`${API_URL_PREFIX}/${userId}/active`);

    return res.data;
  },

  inActive: async (userId: string) => {
    const res = await axiosClient.patch(`${API_URL_PREFIX}/${userId}/inactive`);

    return res.data;
  },

  remove: async (userId: string) => {
    const res = await axiosClient.delete(`${API_URL_PREFIX}/${userId}`);

    return res.data;
  },
};

export const userRoleUserApi = {
  get: async () => {
    const res = await axiosClient.get(`${API_URL_PREFIX}/me`);

    return res.data;
  },

  update: async (payload: any) => {
    const res = await axiosClient.patch(`${API_URL_PREFIX}/me`, payload);

    return res.data;
  },
  sync: async () => {
    const res = await axiosClient.post(`/api/users/sync`);
    return res.data;
  },
};
