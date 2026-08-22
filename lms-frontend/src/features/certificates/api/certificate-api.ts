import { axiosClient } from '@/shared/lib/axios';
import type { Certificate } from '../types/certificate-type';

export const certificateApi = {
  getMyCertificates: async (): Promise<Certificate[]> => {
    // Contract: Không có ApiResponse wrapper
    const res = await axiosClient.get('/api/certificates/me');
    return res.data;
  },
  downloadPdf: async (pdfUrl: string, fileName: string) => {
    const res = await axiosClient.get(pdfUrl, { responseType: 'blob' });
    const fileURL = window.URL.createObjectURL(new Blob([res.data]));
    const link = document.createElement('a');
    link.href = fileURL;
    link.setAttribute('download', fileName);
    document.body.appendChild(link);
    link.click();
    link.remove();
  }
};