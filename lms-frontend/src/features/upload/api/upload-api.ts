import { axiosClient } from '@/shared/lib/axios';

export const uploadApi = {
  uploadImage: async (formData: FormData) => {
    const res = await axiosClient.post('/upload/image', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });

    return res.data;
  },
};
