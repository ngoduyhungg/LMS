import { Skeleton } from 'antd';
import CardCustom from '@/shared/components/card/CardCustom';
import PageHeader from '@/shared/components/page/PageHeader';

const DashboardSkeleton = () => {
  return (
    <div className="flex flex-col gap-6">
      <PageHeader title="Dashboard" subtitle="Tổng quan hệ thống quản lý YOEDU" />

      {/* Stats */}
      <div className="grid grid-cols-4 gap-4">
        {Array.from({ length: 4 }).map((_, index) => (
          <CardCustom key={index}>
            <Skeleton active paragraph={{ rows: 2 }} />
          </CardCustom>
        ))}
      </div>

      {/* Bottom */}
      <div className="grid grid-cols-2 gap-4">
        <CardCustom title="Hoạt động gần đây">
          <Skeleton active paragraph={{ rows: 6 }} />
        </CardCustom>

        <CardCustom title="Lớp học hôm nay">
          <Skeleton active paragraph={{ rows: 6 }} />
        </CardCustom>
      </div>
    </div>
  );
};

export default DashboardSkeleton;
