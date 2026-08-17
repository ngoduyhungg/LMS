import { axiosClient } from '@/shared/lib/axios';
import type { PromotionFilterParams } from '../types/promotion-filter-params-type';

const API_URL_PREFIX = '/promotions';

export const getPromotionsOptions = async () => {
  const res = await axiosClient.get(`${API_URL_PREFIX}/options`, {});

  return res.data;
};

/* ROLE ADMIN */
export const promotionRoleAdminApi = {
  getAll: async (params: PromotionFilterParams) => {
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

  active: async (id: string) => {
    const res = await axiosClient.patch(`${API_URL_PREFIX}/${id}/active`);

    return res.data;
  },

  inActive: async (id: string) => {
    const res = await axiosClient.patch(`${API_URL_PREFIX}/${id}/inactive`);

    return res.data;
  },

  remove: async (id: string) => {
    const res = await axiosClient.delete(`${API_URL_PREFIX}/${id}`);

    return res.data;
  },
};
