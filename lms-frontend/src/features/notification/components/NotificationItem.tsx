import { formatRelativeTime } from '@/shared/utils/date';
import { Badge, Typography } from 'antd';

const { Text } = Typography;

interface NotificationItemProps {
  item: any;
  handleClick: () => void;
}

export const NotificationItem = ({ item, handleClick }: NotificationItemProps) => {
  return (
    <div
      onClick={handleClick}
      className={`w-full mb-3 p-3 cursor-pointer rounded-lg ${item.isRead ? 'bg-white' : 'bg-blue-50'}`}
    >
      <div className="flex gap-2">
        {!item.isRead && <Badge status="processing" />}

        <div className="flex-1">
          <Text strong={!item.isRead}>{item.title}</Text>

          <div className="text-xs text-gray-500 mt-1">{item.content}</div>

          <div className="text-xs text-gray-400 mt-1">{formatRelativeTime(item.createdAt)}</div>
        </div>
      </div>
    </div>
  );
};
