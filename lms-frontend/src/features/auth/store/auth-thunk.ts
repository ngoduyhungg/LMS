import { createAsyncThunk } from '@reduxjs/toolkit';
import { loginApi, logoutApi } from '../api/auth-api';
import type { LoginPayload } from '../types/auth-type';
import { userRoleUserApi } from '@/features/users/api/user-api';
import { extractUserFromToken } from '../utils/jwt';
import { logout } from './auth-slice';

export const loginThunk = createAsyncThunk(
  'auth/login',
  async (payload: LoginPayload, thunkAPI) => {
    try {
      const tokenData = await loginApi(payload);
      const user = extractUserFromToken(tokenData.access_token);
      
      if (!user) throw new Error('Cấu trúc token không hợp lệ hoặc không có quyền truy cập');

      localStorage.setItem('accessToken', tokenData.access_token);
      localStorage.setItem('refreshToken', tokenData.refresh_token);

      await userRoleUserApi.sync();

      return { user, accessToken: tokenData.access_token, refreshToken: tokenData.refresh_token };
    } catch (error: any) {
      return thunkAPI.rejectWithValue(error.response?.data?.error_description || 'Đăng nhập thất bại');
    }
  }
);

export const initializeAuthThunk = createAsyncThunk(
  'auth/initialize', 
  async (_, thunkAPI) => {
    const token = localStorage.getItem('accessToken');
    if (!token) return thunkAPI.rejectWithValue('Không tìm thấy token');
    
    const user = extractUserFromToken(token);
    if (!user) {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      return thunkAPI.rejectWithValue('Token không hợp lệ hoặc thiếu quyền LMS');
    }

    try {
      await userRoleUserApi.sync();
      return user;
    } catch (error: any) {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      return thunkAPI.rejectWithValue('Không thể đồng bộ hồ sơ người dùng');
    }
  }
);

export const logoutThunk = createAsyncThunk('auth/logout', async (_, thunkAPI) => {
  try {
    const refreshToken = localStorage.getItem('refreshToken');
    if (refreshToken) {
      await logoutApi(refreshToken);
    }
  } catch (error) {
    console.error('Lỗi khi đăng xuất Keycloak:', error);
  } finally {
    thunkAPI.dispatch(logout());
    window.location.href = '/auth/login';
  }
});