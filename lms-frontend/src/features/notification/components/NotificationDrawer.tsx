import { Drawer, List } from 'antd';
import { useNotifications } from '../hooks/useNotifications';
import { useMarkAsRead } from '../hooks/useMarkAsRead';
import { NotificationItem } from './NotificationItem';
import { useAllMarkAsRead } from '../hooks/useAllAsRead';

interface Props {
  open: boolean;
  onClose: () => void;
  userId?: string;
}

const NotificationDrawer = ({ open, onClose, userId }: Props) => {
  const { data, isLoading } = useNotifications(userId);
  const markAsRead = useMarkAsRead();
  const markAllAsRead = useAllMarkAsRead();

  const handleMarkAsRead = (id: string) => {
    markAsRead.mutate(id);
  };

  const handleMarkAllAsRead = () => {
    if (userId) {
      markAllAsRead.mutate(userId);
    }
  };

  return (
    <Drawer
      title="Thông báo"
      open={open}
      onClose={onClose}
      extra={
        <button
          onClick={handleMarkAllAsRead}
          disabled={markAllAsRead.isPending}
          className="text-sm text-blue-600 hover:text-blue-800 disabled:opacity-50"
        >
          {markAllAsRead.isPending ? 'Đang xử lý...' : 'Đánh dấu tất cả đã đọc'}
        </button>
      }
    >
      <List
        dataSource={data}
        renderItem={(item: any) => (
          <List.Item style={{ border: 'none', padding: 0 }}>
            <NotificationItem item={item} handleClick={() => handleMarkAsRead(item.id)} />
          </List.Item>
        )}
        loading={isLoading}
      />
    </Drawer>
  );
};

export default NotificationDrawer;
