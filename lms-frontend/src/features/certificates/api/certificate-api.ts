import { axiosClient } from '@/shared/lib/axios';
import type { Certificate } from '../types/certificate-type';

export const certificateApi = {
  getMyCertificates: async (): Promise<Certificate[]> => {
    // Contract: Không có ApiResponse wrapper
    const res = await axiosClient.get('/api/certificates/me');
    return res.data;
  },
};