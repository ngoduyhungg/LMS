import { axiosClient } from '@/shared/lib/axios';
import type { CourseFilterParams } from '../types/course-filter-params-type';
import type { CoursePayload, ModulePayload, LessonPayload } from '../types/course-type';

const API_URL_PREFIX = '/api/courses';

export const getCategories = async () => {
  const res = await axiosClient.get('/api/categories');
  if (res.data && res.data.success && Array.isArray(res.data.data)) {
    return res.data.data;
  }
  return res.data;
};

export const getCourseOptions = async () => {
  const res = await axiosClient.get(`${API_URL_PREFIX}/options`, {});
  return res.data;
};

export const courseApi = {
  getAll: async (params: CourseFilterParams) => {
    const res = await axiosClient.get(`${API_URL_PREFIX}`, { params });
    const rawData = res.data?.data;
    const allCourses = Array.isArray(rawData) ? rawData : [];

    const page = Number(params.page) || 1;
    const limit = Number(params.limit) || 10;
    const startIndex = (page - 1) * limit;
    const paginatedItems = allCourses.slice(startIndex, startIndex + limit);

    return {
      data: {
        items: paginatedItems,
        pagination: {
          total: allCourses.length,
          page: page,
          limit: limit
        }
      }
    };
  },
  
  getDetail: async (id: string) => {
    const res = await axiosClient.get(`${API_URL_PREFIX}/${id}`);
    return res.data;
  },
  getCurriculum: async (id: string) => {
    const res = await axiosClient.get(`${API_URL_PREFIX}/${id}/curriculum`);
    return res.data;
  },
  create: async (payload: CoursePayload) => {
    const res = await axiosClient.post(`${API_URL_PREFIX}`, payload);
    return res.data;
  },
  update: async (id: string, payload: CoursePayload) => {
    const res = await axiosClient.put(`${API_URL_PREFIX}/${id}`, payload);
    return res.data;
  },
  remove: async (id: string) => {
    const res = await axiosClient.delete(`${API_URL_PREFIX}/${id}`);
    return res.data;
  },

  createModule: async (courseId: string, payload: ModulePayload) => {
    const res = await axiosClient.post(`${API_URL_PREFIX}/${courseId}/modules`, payload);
    return res.data;
  },
  updateModule: async (moduleId: string, payload: ModulePayload) => {
    const res = await axiosClient.put(`${API_URL_PREFIX}/modules/${moduleId}`, payload);
    return res.data;
  },
  deleteModule: async (moduleId: string) => {
    const res = await axiosClient.delete(`${API_URL_PREFIX}/modules/${moduleId}`);
    return res.data;
  },

  createLesson: async (moduleId: string, payload: LessonPayload) => {
    const res = await axiosClient.post(`${API_URL_PREFIX}/modules/${moduleId}/lessons`, payload);
    return res.data;
  },
  getLessonDetail: async (lessonId: string | number) => {
    const res = await axiosClient.get(`/api/courses/lessons/${lessonId}`);
    return res.data;
  },
  updateLesson: async (lessonId: string, payload: LessonPayload) => {
    const res = await axiosClient.put(`${API_URL_PREFIX}/lessons/${lessonId}`, payload);
    return res.data;
  },
  deleteLesson: async (lessonId: string) => {
    const res = await axiosClient.delete(`${API_URL_PREFIX}/lessons/${lessonId}`);
    return res.data;
  },
};