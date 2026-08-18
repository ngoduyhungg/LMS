import React from 'react';
import { Layout, Dropdown, Avatar, Button, Badge } from 'antd';
import { MenuUnfoldOutlined, MenuFoldOutlined, UserOutlined, LogoutOutlined, BellOutlined } from '@ant-design/icons';
import { useAppDispatch, useAppSelector } from '@/app/redux/hooks';
import { logoutThunk } from '@/features/auth/store/auth-thunk';
import { useTheme } from '@/app/providers/theme/hooks/useTheme';

const { Header } = Layout;

interface AppHeaderProps {
  collapsed: boolean;
  setCollapsed: (collapsed: boolean) => void;
  isMobile?: boolean;
  drawerVisible?: boolean;
  setDrawerVisible?: (visible: boolean) => void;
}

const AppHeader: React.FC<AppHeaderProps> = ({ 
  collapsed, setCollapsed, isMobile, drawerVisible, setDrawerVisible 
}) => {
  const dispatch = useAppDispatch();
  const { user } = useAppSelector((state) => state.auth);
  const { theme } = useTheme();

  const handleMenuToggle = () => {
    if (isMobile && setDrawerVisible) {
      setDrawerVisible(!drawerVisible);
    } else {
      setCollapsed(!collapsed);
    }
  };

  const userMenu = {
    items: [
      { key: 'profile', icon: <UserOutlined />, label: 'Hồ sơ cá nhân' },
      { type: 'divider' as const },
      { key: 'logout', icon: <LogoutOutlined />, label: 'Đăng xuất', onClick: () => dispatch(logoutThunk()), danger: true },
    ],
  };

  return (
    <Header className={`px-4 flex items-center justify-between shadow-sm z-10 ${theme === 'dark' ? 'bg-[#141414]' : 'bg-white'}`} style={{ height: 64, paddingInline: 16 }}>
      <Button
        type="text"
        icon={isMobile ? <MenuUnfoldOutlined /> : (collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />)}
        onClick={handleMenuToggle}
        className="text-lg w-10 h-10 flex items-center justify-center"
      />
      <div className="flex items-center gap-4 md:gap-6">
        <Badge count={0} showZero={false} dot>
          <Button type="text" icon={<BellOutlined className="text-xl text-gray-500" />} />
        </Badge>
        <Dropdown menu={userMenu} placement="bottomRight" trigger={['click']}>
          <div className="flex items-center cursor-pointer hover:bg-gray-50 p-1 md:p-1.5 rounded-lg transition-colors">
            <Avatar icon={<UserOutlined />} className="bg-blue-600 flex-shrink-0" />
            <div className="ml-3 hidden md:block text-sm">
              <div className="font-semibold text-gray-800 leading-none">{user?.fullName || 'User'}</div>
              <div className="text-xs text-gray-500 mt-1 capitalize">{user?.role?.toLowerCase() || 'N/A'}</div>
            </div>
          </div>
        </Dropdown>
      </div>
    </Header>
  );
};

export default AppHeader;