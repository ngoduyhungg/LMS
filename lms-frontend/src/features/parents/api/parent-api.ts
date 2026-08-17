import { axiosClient } from '@/shared/lib/axios';
import type { ParentFilterParams } from '../types/parent-filter-params-type';

const API_URL_PREFIX = '/parents';

export const getParentOptions = async () => {
  const res = await axiosClient.get(`${API_URL_PREFIX}/options`, {});

  return res.data;
};

/* ROLE ADMIN */
export const parentRoleAdminApi = {
  getAll: async (params: ParentFilterParams) => {
    const res = await axiosClient.get(`${API_URL_PREFIX}`, { params });

    return res.data;
  },

  create: async (payload: any) => {
    const res = await axiosClient.post(`${API_URL_PREFIX}`, payload);

    return res.data;
  },

  update: async (id: string, payload: any) => {
    const res = await axiosClient.patch(`${API_URL_PREFIX}/${id}`, payload);

    return res.data;
  },
};

/* ROLE PARENT */
export const parentRoleParentApi = {
  update: async (payload: any) => {
    const res = await axiosClient.patch(`${API_URL_PREFIX}/me`, payload);

    return res.data;
  },
};
