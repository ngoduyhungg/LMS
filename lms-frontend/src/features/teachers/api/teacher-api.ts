import { axiosClient } from '@/shared/lib/axios';
import type { TeacherFilterParams } from '../types/teacher-filter-params-type';

const API_URL_PREFIX = '/teachers';

export const getTeachersOptions = async () => {
  const res = await axiosClient.get(`${API_URL_PREFIX}/options`, {});

  return res.data;
};

/* ROLE ADMIN */
export const teacherRoleAdminApi = {
  getAll: async (params: TeacherFilterParams) => {
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

/* ROLE TEACHER */
export const teacherRoleTeacherApi = {
  update: async (payload: any) => {
    const res = await axiosClient.patch(`${API_URL_PREFIX}/me`, payload);

    return res.data;
  },
};
