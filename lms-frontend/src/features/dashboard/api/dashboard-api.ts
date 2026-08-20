import { axiosClient } from '@/shared/lib/axios';

export const dashboardRoleAdminApi = {
  getDashboard: async () => {
    const res = await axiosClient.get('/dashboard');

    return res.data;
  },
};
