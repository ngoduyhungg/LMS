import { axiosClient } from '@/shared/lib/axios';
import type { EnrollmentFilterParams } from '../types/enrollment-filter-params-type';

const API_URL_PREFIX = '/enrollments';

export const enrollmentRoleAdminApi = {
  getAll: async (params: EnrollmentFilterParams) => {
    const res = await axiosClient.get(API_URL_PREFIX, { params });

    return res.data;
  },

  getDetail: async (id: string) => {
    const res = await axiosClient.get(`${API_URL_PREFIX}/${id}`);

    return res.data;
  },

  create: async (payload: any) => {
    const res = await axiosClient.post(API_URL_PREFIX, payload);

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

  remove: async (id: string) => {
    const res = await axiosClient.delete(`${API_URL_PREFIX}/${id}`);

    return res.data;
  },
};

export const enrollmentRoleStudentApi = {
  pause: async (id: string) => {
    const res = await axiosClient.patch(`${API_URL_PREFIX}/${id}/pause`);

    return res.data;
  },

  remove: async (id: string) => {
    const res = await axiosClient.delete(`${API_URL_PREFIX}/${id}`);

    return res.data;
  },
};
