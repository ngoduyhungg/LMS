import { axiosClient } from '@/shared/lib/axios';
import type { CourseClassSessionFilterParams } from '../types/course-class-session-filter-params-type';
import type { CourseClassSessionCalendarFilterParams } from '../types/course-class-session-calendar-filter-params-type';
import type { AttendancePayload } from '../types/attendance-type';

const API_URL_PREFIX = '/course-class-sessions';

export const getCourseClassSessionOptions = async () => {
  const res = await axiosClient.get(`${API_URL_PREFIX}/options`, {});

  return res.data;
};

export const courseClassSessionRoleAdminApi = {
  getAll: async (params: CourseClassSessionFilterParams) => {
    const res = await axiosClient.get(`${API_URL_PREFIX}`, { params });

    return res.data;
  },

  getDetail: async (id: string) => {
    const res = await axiosClient.get(`${API_URL_PREFIX}/${id}`);

    return res.data;
  },

  calendar: async (params: CourseClassSessionCalendarFilterParams) => {
    const res = await axiosClient.get(`${API_URL_PREFIX}/calendar`, { params });

    return res.data;
  },

  done: async (id: string) => {
    const res = await axiosClient.patch(`${API_URL_PREFIX}/${id}/done`);

    return res.data;
  },

  cancel: async (id: string) => {
    const res = await axiosClient.patch(`${API_URL_PREFIX}/${id}/cancel`);

    return res.data;
  },

  attendance: async (id: string) => {
    const res = await axiosClient.get(`${API_URL_PREFIX}/${id}/attendance`);

    return res.data;
  },

  takeAttendance: async (id: string, data: { attendances: AttendancePayload[] }) => {
    const res = await axiosClient.post(`${API_URL_PREFIX}/${id}/attendance`, data);

    return res.data;
  },
};
