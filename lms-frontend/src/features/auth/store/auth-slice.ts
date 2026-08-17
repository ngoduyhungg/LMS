import { createSlice } from '@reduxjs/toolkit';

import { getMeThunk, loginThunk, registerThunk } from './auth-thunk';
import type { User } from '@/features/users/types/user-type';

type AuthState = {
  user: User | null;
  initialized: boolean; // Dùng để đánh dấu đã kiểm tra token và lấy thông tin user hay chưa
  loading: boolean;
  error: string | null;
};

const initialState: AuthState = {
  user: null,
  initialized: false,
  loading: false,
  error: null,
};

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    logout: (state) => {
      state.user = null;

      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
    },
    clearError: (state) => {
      state.error = null;
    },
    markInitialized: (state) => {
      state.initialized = true;
    },
  },

  extraReducers: (builder) => {
    builder
      .addCase(loginThunk.pending, (state) => {
        state.loading = true;

        state.error = null;
      })
      .addCase(loginThunk.fulfilled, (state, action) => {
        state.loading = false;

        state.user = action.payload.user;

        localStorage.setItem('accessToken', action.payload.accessToken);
        localStorage.setItem('refreshToken', action.payload.refreshToken);
      })
      .addCase(loginThunk.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      })

      .addCase(registerThunk.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(registerThunk.fulfilled, (state) => {
        state.loading = false;
      })
      .addCase(registerThunk.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      })

      .addCase(getMeThunk.pending, (state) => {
        state.loading = true;

        state.error = null;
      })
      .addCase(getMeThunk.fulfilled, (state, action) => {
        state.loading = false;

        state.user = action.payload;

        state.initialized = true;
      })
      .addCase(getMeThunk.rejected, (state, action) => {
        state.loading = false;

        state.user = null;

        state.initialized = true;

        state.error = action.payload as string;
      });
  },
});

export const { logout, clearError, markInitialized } = authSlice.actions;

export default authSlice.reducer;
