import { axiosClient } from '@/shared/lib/axios';
import type { StudentFilterParams } from '../types/student-filter-params-type';

const API_URL_PREFIX = '/students';

export const getStudentOptions = async () => {
  const res = await axiosClient.get(`${API_URL_PREFIX}/options`, {});

  return res.data;
};

/* ROLE ADMIN */
export const studentRoleAdminApi = {
  getAll: async (params: StudentFilterParams) => {
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

  active: async (id: string) => {
    const res = await axiosClient.patch(`${API_URL_PREFIX}/${id}/active`);

    return res.data;
  },

  paused: async (id: string) => {
    const res = await axiosClient.patch(`${API_URL_PREFIX}/${id}/paused`);

    return res.data;
  },

  remove: async (id: string) => {
    const res = await axiosClient.delete(`${API_URL_PREFIX}/${id}`);

    return res.data;
  },
};

/* ROLE STUDENT */
export const studentRoleStudentApi = {
  update: async (payload: any) => {
    const res = await axiosClient.patch(`${API_URL_PREFIX}/me`, payload);

    return res.data;
  },
};
