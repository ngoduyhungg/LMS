import { axiosClient } from '@/shared/lib/axios';
import type { TuitionInvoiceFilterParams } from '../types/tuition-invoice-filter-params-type';

const API_URL_PREFIX = '/tuition-invoices';

export const getTuitionInvoiceOptions = async () => {
  const res = await axiosClient.get(`${API_URL_PREFIX}/options`, {});

  return res.data;
};

export const tuitionInvoiceRoleAdminApi = {
  getAll: async (params: TuitionInvoiceFilterParams) => {
    const res = await axiosClient.get(API_URL_PREFIX, { params });

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

  getDetail: async (id: string) => {
    const res = await axiosClient.get(`${API_URL_PREFIX}/${id}`);

    return res.data;
  },

  remove: async (id: string) => {
    const res = await axiosClient.delete(`${API_URL_PREFIX}/${id}`);

    return res.data;
  },
};

export const tuitionInvoiceRoleStudentApi = {
  getAll: async (params: TuitionInvoiceFilterParams) => {
    const res = await axiosClient.get(`${API_URL_PREFIX}/me`, { params });

    return res.data;
  },
};
