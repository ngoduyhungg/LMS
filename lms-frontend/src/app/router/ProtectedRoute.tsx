import { Navigate, Outlet } from 'react-router-dom';
import { useAppSelector } from '@/app/redux/hooks';
import { Spin } from 'antd';
import type { UserRole } from '@/features/users/types/user-role-type';

interface ProtectedRouteProps {
  requireAuth?: boolean;
  allowedRoles?: UserRole[];
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ 
  requireAuth = true, 
  allowedRoles 
}) => {
  const { user, initialized } = useAppSelector((state) => state.auth);

  if (!initialized) {
    return <Spin size="large" className="flex! items-center justify-center h-screen" />;
  }

  if (requireAuth && !user) {
    return <Navigate to="/auth/login" replace />;
  }

  if (!requireAuth && user) {
    return <Navigate to="/" replace />;
  }

  if (requireAuth && user && allowedRoles && allowedRoles.length > 0) {
    const hasRole = user.roles?.some((role: UserRole) => allowedRoles.includes(role));
    
    if (!hasRole) {
      return (
        <div className="flex h-screen items-center justify-center bg-gray-50">
          <h1 className="text-2xl font-bold text-red-500">403 - Forbidden</h1>
        </div>
      );
    }
  }

  return <Outlet />;
};

export default ProtectedRoute;