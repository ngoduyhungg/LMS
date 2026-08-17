import { USER_ROLE, type UserRole } from '@/features/users/types/user-role-type';

// Hàm decode Base64Url cơ bản
const parseJwt = (token: string) => {
  try {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(
      window.atob(base64).split('').map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)).join('')
    );
    return JSON.parse(jsonPayload);
  } catch (e) {
    return null;
  }
};

export const extractUserFromToken = (token: string) => {
  const payload = parseJwt(token);
  if (!payload) return null;

  const rawRoles: string[] = payload.realm_access?.roles || [];
  const validLmsRoles = [USER_ROLE.ADMIN, USER_ROLE.INSTRUCTOR, USER_ROLE.STUDENT];

  // Chỉ lấy những role chính xác thuộc LMS
  const normalizedRoles = rawRoles.filter((role) => 
    validLmsRoles.includes(role as UserRole)
  );

  if (normalizedRoles.length === 0) {
    return null; // Reject nếu không có role LMS hợp lệ
  }

  return {
    id: payload.sub,
    email: payload.email,
    fullName: payload.name || payload.preferred_username || payload.email,
    roles: normalizedRoles as UserRole[],
    role: normalizedRoles[0] as UserRole, // Hỗ trợ tương thích ngược với sidebar
  };
};