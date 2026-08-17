import { Flex, Space, Tag, Typography } from 'antd';

import CardCustom from '@/shared/components/card/CardCustom';
import UserAvatar from '@/shared/components/avatar/UserAvatar';
import { useAppSelector } from '@/app/redux/hooks';

const { Title, Text } = Typography;

const ProfileHeader = () => {
  const { user } = useAppSelector((state) => state.auth);

  return (
    <CardCustom>
      <Flex align="center" gap={20}>
        <UserAvatar />

        <Flex vertical gap={4}>
          <Title level={3} className="mb-0!">
            {user?.fullName}
          </Title>

          <Space>
            <Tag color="blue">{user?.role}</Tag>

            <Tag color="green">{user?.status}</Tag>
          </Space>

          <Text type="secondary">{user?.email}</Text>
        </Flex>
      </Flex>
    </CardCustom>
  );
};

export default ProfileHeader;
