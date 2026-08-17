import { Image, Layout, Menu, type MenuProps } from 'antd';
import { useNavigate, useLocation } from 'react-router-dom';
import {
  DashboardOutlined,
  TeamOutlined,
  SolutionOutlined,
  UserOutlined,
  BookOutlined,
  AuditOutlined,
  ReadOutlined,
  WalletOutlined,
  ScheduleOutlined,
  CalendarOutlined,
} from '@ant-design/icons';
import YoeduLogo from '@/assets/images/yoedu-logo.svg';
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
}

const AppSidebar: React.FC<AppSidebarProps> = ({ collapsed }) => {
  const { user } = useAppSelector((state) => state.auth);
  const { theme } = useTheme();

  const navigate = useNavigate();
  const location = useLocation();

  const menuItems: MenuItem[] = [
    {
      key: '/',
      icon: <DashboardOutlined />,
      label: 'Dashboard',
    },
    {
      key: 'user-management',
      label: 'Quản lý người dùng',
      icon: <TeamOutlined />,
      roles: [USER_ROLE.ADMIN, USER_ROLE.STAFF], // Chỉ admin và manager mới thấy menu này
      children: [
        {
          key: '/accounts',
          icon: <AuditOutlined />,
          label: 'Tài khoản',
        },
        {
          key: '/students',
          icon: <UserOutlined />,
          label: 'Học viên',
        },
        {
          key: '/parents',
          icon: <TeamOutlined />,
          label: 'Phụ huynh',
        },
        {
          key: '/teachers',
          icon: <SolutionOutlined />,
          label: 'Giáo viên',
        },
      ],
    },
    {
      key: 'academic-management',
      label: 'Quản lý đào tạo',
      icon: <BookOutlined />,
      children: [
        {
          key: '/rooms',
          icon: <SolutionOutlined />,
          label: 'Phòng học',
          roles: [USER_ROLE.ADMIN, USER_ROLE.STAFF],
        },
        {
          key: '/schedules',
          icon: <ScheduleOutlined />,
          label: 'Ca học',
          roles: [USER_ROLE.ADMIN, USER_ROLE.STAFF],
        },
        {
          key: '/courses',
          icon: <ReadOutlined />,
          label: 'Khóa đào tạo',
          roles: [USER_ROLE.ADMIN, USER_ROLE.STAFF],
        },
        {
          key: '/course-classes',
          icon: <ReadOutlined />,
          label: 'Lớp học',
        },
        {
          key: '/enrollments',
          icon: <SolutionOutlined />,
          label: 'Tuyển sinh',
        },
        {
          key: '/course-class-sessions',
          icon: <CalendarOutlined />,
          label: 'Lịch học',
        },
        {
          key: '/calendar',
          icon: <CalendarOutlined />,
          label: 'Calendars',
        },
        {
          key: '/leave-requests',
          icon: <AuditOutlined />,
          label: 'Đơn xin nghỉ',
        },
      ],
    },
    {
      key: 'finance',
      label: 'Quản lý học phí',
      icon: <WalletOutlined />,
      roles: [USER_ROLE.ADMIN, USER_ROLE.STAFF], // Chỉ admin và manager mới thấy menu này
      children: [
        {
          key: '/promotions',
          icon: <WalletOutlined />,
          label: 'Chương trình khuyến mãi',
        },
        {
          key: '/tuition-invoices',
          icon: <WalletOutlined />,
          label: 'Hóa đơn học phí',
        },
        {
          key: '/payments',
          icon: <WalletOutlined />,
          label: 'Thanh toán',
        },
      ],
    },
  ];

  const filterMenuByRole = (items: MenuItem[], role?: UserRole): MenuItem[] => {
    return (
      items
        // Nếu item không có trường roles hoặc trường roles có chứa role của user thì giữ lại
        .filter((item) => !item.roles || item.roles.includes(role!))
        // Với các item có children, tiếp tục lọc children theo cùng logic
        .map((item) => ({
          ...item,
          children: item.children ? filterMenuByRole(item.children, role) : undefined,
        }))
        // Sau khi lọc, loại bỏ các item có children nhưng không còn children nào sau khi lọc
        .filter((item) => {
          const isLeaf = !item.children;
          const hasChildren = item.children?.length;

          return isLeaf || hasChildren;
        }) as MenuItem[]
    );
  };

  return (
    <Sider width={240} collapsed={collapsed}>
      <div
        className={`h-16 flex items-center justify-center border-b ${theme === 'dark' ? 'border-gray-700' : 'border-gray-200'}`}
      >
        <Image src={YoeduLogo} preview={false} width={collapsed ? 48 : 64} />
      </div>

      <Menu
        theme={theme}
        mode="inline"
        items={filterMenuByRole(menuItems, user?.role)}
        selectedKeys={[location.pathname]}
        onClick={({ key }) => navigate(key)}
      />
    </Sider>
  );
};

export default AppSidebar;
