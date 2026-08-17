import { axiosClient } from '@/shared/lib/axios';
import type { LeaveRequestFilterParams } from '../types/leave-request-filter-params-type';

const API_URL_PREFIX = '/leave-requests';

export const leaveRequestRoleAdminApi = {
  getAll: async (params: LeaveRequestFilterParams) => {
    const res = await axiosClient.get(API_URL_PREFIX, { params });

    return res.data;
  },

  getDetail: async (id: string) => {
    const res = await axiosClient.get(`${API_URL_PREFIX}/${id}`);

    return res.data;
  },

  approve: async (id: string) => {
    const res = await axiosClient.patch(`${API_URL_PREFIX}/${id}/approve`);

    return res.data;
  },

  reject: async (id: string) => {
    const res = await axiosClient.patch(`${API_URL_PREFIX}/${id}/reject`);

    return res.data;
  },
};

export const leaveRequestRoleStudentApi = {
  getAll: async (params: LeaveRequestFilterParams) => {
    const res = await axiosClient.get(`${API_URL_PREFIX}/me`, { params });

    return res.data;
  },

  create: async (payload: any) => {
    const res = await axiosClient.post(`${API_URL_PREFIX}/me`, payload);

    return res.data;
  },

  update: async (id: string, payload: any) => {
    const res = await axiosClient.patch(`${API_URL_PREFIX}/me/${id}`, payload);

    return res.data;
  },

  remove: async (id: string) => {
    const res = await axiosClient.delete(`${API_URL_PREFIX}/me/${id}`);

    return res.data;
  },
};
