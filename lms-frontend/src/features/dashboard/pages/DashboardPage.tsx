import StatCard from '@/features/dashboard/components/StatCard';
import { useEffect, useState } from 'react';
import type { Dashboard } from '@/features/dashboard/types/dashboard-type';
import RecentActivity from '@/features/dashboard/components/RecentActivity';
import TodayClasses from '@/features/dashboard/components/TodayClasses';
import { dashboardRoleAdminApi } from '../api/dashboard-api';
import PageHeader from '@/shared/components/page/PageHeader';
import DashboardSkeleton from '../components/DashboardSkeleton';

const mapColor = ['green', 'blue', 'purple', 'red'];

const DashboardPage = () => {
  const { getDashboard } = dashboardRoleAdminApi;
  const [data, setData] = useState<Dashboard | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const res = await getDashboard();
        setData(res.data);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  if (loading) {
    return <DashboardSkeleton />;
  }

  return (
    <div className="flex flex-col gap-6">
      {/* Title */}
      <PageHeader title="Dashboard" subtitle="Tổng quan hệ thống quản lý YOEDU" />

      {/* Stats */}
      <div className="grid grid-cols-4 gap-4">
        {data?.statData.map((item, index) => (
          <StatCard
            key={index}
            title={item.title}
            value={item.value}
            extra={item.extra}
            color={mapColor[index % mapColor.length]}
          />
        ))}
      </div>

      {/* Bottom */}
      <div className="grid grid-cols-2 gap-4">
        <RecentActivity data={data?.recentActivityData || []} />
        <TodayClasses data={data?.todayClasses || []} />
      </div>
    </div>
  );
};

export default DashboardPage;
