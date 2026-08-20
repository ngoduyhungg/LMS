import { axiosClient } from '@/shared/lib/axios';
import type { CourseFilterParams } from '../types/course-filter-params-type';

const API_URL_PREFIX = 'api/courses';

export const getCategories = async () => {
  const res = await axiosClient.get('api/categories');
  return res.data;
};

export const getCourseOptions = async () => {
  const res = await axiosClient.get(`${API_URL_PREFIX}/options`, {});
  return res.data;
};

export const courseApi = {
  // Course CRUD
  getAll: async (params: CourseFilterParams) => {
    const res = await axiosClient.get(`${API_URL_PREFIX}`, { params });
    return res.data;
  },
  getDetail: async (id: string) => {
    const res = await axiosClient.get(`${API_URL_PREFIX}/${id}`);
    return res.data;
  },
  getCurriculum: async (id: string) => {
    const res = await axiosClient.get(`${API_URL_PREFIX}/${id}/curriculum`);
    return res.data;
  },
  create: async (payload: any) => {
    const res = await axiosClient.post(`${API_URL_PREFIX}`, payload);
    return res.data;
  },
  update: async (id: string, payload: any) => {
    // Dùng PUT cho Upsert theo chuẩn Backend Contract
    const res = await axiosClient.put(`${API_URL_PREFIX}/${id}`, payload);
    return res.data;
  },
  remove: async (id: string) => {
    const res = await axiosClient.delete(`${API_URL_PREFIX}/${id}`);
    return res.data;
  },

  // Module CRUD
  createModule: async (courseId: string, payload: any) => {
    const res = await axiosClient.post(`${API_URL_PREFIX}/${courseId}/modules`, payload);
    return res.data;
  },
  updateModule: async (moduleId: string, payload: any) => {
    const res = await axiosClient.put(`${API_URL_PREFIX}/modules/${moduleId}`, payload);
    return res.data;
  },
  deleteModule: async (moduleId: string) => {
    const res = await axiosClient.delete(`${API_URL_PREFIX}/modules/${moduleId}`);
    return res.data;
  },

  // Lesson CRUD
  createLesson: async (moduleId: string, payload: any) => {
    const res = await axiosClient.post(`${API_URL_PREFIX}/modules/${moduleId}/lessons`, payload);
    return res.data;
  },
  updateLesson: async (lessonId: string, payload: any) => {
    const res = await axiosClient.put(`${API_URL_PREFIX}/lessons/${lessonId}`, payload);
    return res.data;
  },
  deleteLesson: async (lessonId: string) => {
    const res = await axiosClient.delete(`${API_URL_PREFIX}/lessons/${lessonId}`);
    return res.data;
  },
};