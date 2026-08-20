import CardCustom from '@/shared/components/card/CardCustom';
import type { Stat } from '@/features/dashboard/types/start-data-type';

const StatCard: React.FC<Stat> = ({ title, value, extra, color }) => {
  return (
    <CardCustom title={title} className="shadow-sm">
      <div className="flex flex-col gap-2">
        <span className="text-gray-500">{title}</span>

        <span className="text-2xl font-semibold" style={{ color }}>
          {value}
        </span>

        {extra && <span className="text-sm text-gray-400">{extra}</span>}
      </div>
    </CardCustom>
  );
};

export default StatCard;
