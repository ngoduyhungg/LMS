import { axiosClient } from '@/shared/lib/axios';
import type { PaymentFilterParams } from '../types/payment-filter-params-type';

const API_URL_PREFIX = '/payments';

export const paymentRoleAdminApi = {
  getAll: async (params: PaymentFilterParams) => {
    const res = await axiosClient.get(API_URL_PREFIX, { params });

    return res.data;
  },

  create: async (payload: any) => {
    const res = await axiosClient.post(`${API_URL_PREFIX}`, payload);

    return res.data;
  },

  getDetail: async (id: string) => {
    const res = await axiosClient.get(`${API_URL_PREFIX}/${id}`);

    return res.data;
  },
};
