import { axiosClient } from '@/shared/lib/axios';
import type { Certificate } from '../types/certificate-type';

export const certificateApi = {
  getMyCertificates: async (): Promise<Certificate[]> => {
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
export const instructorCertificateApi = {
  getTemplate: async (courseId: string | number) => {
    const res = await axiosClient.get(`/api/instructor/courses/${courseId}/certificate-template`);
    return res.data;
  },
  
  uploadTemplate: async (courseId: string | number, file: File) => {
    const formData = new FormData();
    formData.append('file', file);
    const res = await axiosClient.post(`/api/instructor/courses/${courseId}/certificate-template`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });
    return res.data;
  },

  deleteTemplate: async (courseId: string | number) => {
    const res = await axiosClient.delete(`/api/instructor/courses/${courseId}/certificate-template`);
    return res.data;
  }
};