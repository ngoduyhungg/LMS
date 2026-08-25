import { axiosClient } from '@/shared/lib/axios';
import type { EnrollmentFilterParams } from '../types/enrollment-filter-params-type';
import type { EnrollmentResponse } from '../types/enrollment-type';

const API_URL_PREFIX = '/api/enrollments'; // Fix legacy URL prefix if needed, ensuring it targets /api/enrollments

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

// ==========================================
// UI-4 STUDENT LEARNING API CONTRACT
// ==========================================
export const studentLearningApi = {
  enroll: async (courseId: string | number): Promise<EnrollmentResponse> => {
    const res = await axiosClient.post('/api/enrollments', { courseId: Number(courseId) });
    return res.data; // Raw DTO, no wrapper
  },

  getMyEnrollments: async (): Promise<EnrollmentResponse[]> => {
    const res = await axiosClient.get('/api/enrollments');
    return res.data; // List<EnrollmentResponse>
  },

  getEnrollmentDetail: async (courseId: string | number): Promise<EnrollmentResponse> => {
    const res = await axiosClient.get(`/api/enrollments/courses/${courseId}`);
    return res.data;
  },

  updateProgress: async (
    courseId: string | number, 
    payload: { lessonId: string | number; watchedSeconds: number; isCompleted: boolean }
  ): Promise<EnrollmentResponse> => {
    const res = await axiosClient.put(`/api/enrollments/courses/${courseId}/progress`, payload);
    return res.data;
  }
  
};
// ==========================================
// UI-5 ADMIN ENROLLMENT MANAGEMENT CONTRACT
// ==========================================
export const adminEnrollmentApi = {
  getAll: async (): Promise<EnrollmentResponse[]> => {
    const res = await axiosClient.get('/api/admin/enrollments');
    return res.data;
  },
  cancel: async (id: string | number): Promise<EnrollmentResponse> => {
    // Contract đã verify: Payload body không cần thiết, Backend tự chuyển status = CANCELLED
    const res = await axiosClient.put(`/api/admin/enrollments/${id}/cancel`);
    return res.data;
  }
};