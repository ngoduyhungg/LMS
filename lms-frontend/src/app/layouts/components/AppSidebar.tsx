import React from 'react';
import { Layout, Menu, type MenuProps } from 'antd';
import { useNavigate, useLocation } from 'react-router-dom';
import {
  DashboardOutlined, BookOutlined, ReadOutlined, TeamOutlined,
  SafetyCertificateOutlined, UserOutlined, AppstoreOutlined,
} from '@ant-design/icons';
import { useTheme } from '@/app/providers/theme/hooks/useTheme';
import { USER_ROLE, type UserRole } from '@/features/users/types/user-role-type';
import { useAppSelector } from '@/app/redux/hooks';

const { Sider } = Layout;

type MenuItem = Required<MenuProps>['items'][number] & {
  roles?: UserRole[];
  children?: MenuItem[];
};

interface AppSidebarProps {
  collapsed: boolean;
  isMobile?: boolean;
  onClose?: () => void;
}

const AppSidebar: React.FC<AppSidebarProps> = ({ collapsed, isMobile, onClose }) => {
  const { user } = useAppSelector((state) => state.auth);
  const { theme } = useTheme();
  const navigate = useNavigate();
  const location = useLocation();

  const menuItems: MenuItem[] = [
    { key: '/', icon: <DashboardOutlined />, label: 'Dashboard' },
    { key: '/courses', icon: <AppstoreOutlined />, label: 'Khóa học', roles: [USER_ROLE.ADMIN] },
    { key: '/categories', icon: <ReadOutlined />, label: 'Danh mục', roles: [USER_ROLE.ADMIN] },
    { key: '/my-courses', icon: <BookOutlined />, label: 'Khóa học của tôi', roles: [USER_ROLE.STUDENT, USER_ROLE.INSTRUCTOR] },
    { key: '/course-management', icon: <ReadOutlined />, label: 'Quản lý khóa học', roles: [USER_ROLE.INSTRUCTOR] },
    { key: '/enrollments', icon: <TeamOutlined />, label: 'Ghi danh', roles: [USER_ROLE.ADMIN] },
    { key: '/users', icon: <UserOutlined />, label: 'Người dùng', roles: [USER_ROLE.ADMIN] },
    { key: '/certificates', icon: <SafetyCertificateOutlined />, label: 'Chứng chỉ' },
  ];

  const filterMenuByRole = (items: MenuItem[], role?: UserRole): MenuItem[] => {
    return items
      .filter((item) => !item.roles || (role && item.roles.includes(role)))
      .map((item) => ({
        ...item,
        children: item.children ? filterMenuByRole(item.children, role) : undefined,
      }))
      .filter((item) => !item.children || item.children.length > 0) as MenuItem[];
  };

  const siderContent = (
    <>
      <div className={`h-16 flex items-center justify-center border-b ${theme === 'dark' ? 'border-gray-800' : 'border-gray-100'}`}>
        <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-blue-600 text-lg font-black text-white shadow-md">
          LMS
        </div>
        {!collapsed && <span className="ml-3 font-bold text-lg tracking-wide text-blue-600">Platform</span>}
      </div>
      <Menu
        theme={theme}
        mode="inline"
        items={filterMenuByRole(menuItems, user?.role)}
        selectedKeys={[location.pathname]}
        onClick={({ key }) => {
          navigate(key);
          if (isMobile && onClose) onClose();
        }}
        className="mt-4 border-r-0"
      />
    </>
  );

  if (isMobile) {
    return <div className={`h-full w-full ${theme === 'dark' ? 'bg-[#141414]' : 'bg-white'}`}>{siderContent}</div>;
  }

  return (
    <Sider width={260} collapsed={collapsed} theme={theme} className="shadow-md z-10 hidden md:block">
      {siderContent}
    </Sider>
  );
};

export default AppSidebar;