import { axiosClient } from '@/shared/lib/axios';
import type { CourseFilterParams } from '../types/course-filter-params-type';

const API_URL_PREFIX = '/courses';

// ----- MỚI THÊM: API lấy danh mục -----
export const getCategories = async () => {
  const res = await axiosClient.get('/categories');
  return res.data;
};

export const getCourseOptions = async () => {
  const res = await axiosClient.get(`${API_URL_PREFIX}/options`, {});
  return res.data;
};

export const courseRoleAdminApi = {
  getAll: async (params: CourseFilterParams) => {
    const res = await axiosClient.get(`${API_URL_PREFIX}`, { params });
    return res.data;
  },

  getDetail: async (id: string) => {
    const res = await axiosClient.get(`${API_URL_PREFIX}/${id}`);
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

  open: async (id: string) => {
    const res = await axiosClient.patch(`${API_URL_PREFIX}/${id}/open`);
    return res.data;
  },

  close: async (id: string) => {
    const res = await axiosClient.patch(`${API_URL_PREFIX}/${id}/close`);
    return res.data;
  },

  remove: async (id: string) => {
    const res = await axiosClient.delete(`${API_URL_PREFIX}/${id}`);
    return res.data;
  },
};