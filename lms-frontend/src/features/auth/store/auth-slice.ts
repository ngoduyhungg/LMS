import { createSlice } from '@reduxjs/toolkit';
import { initializeAuthThunk, loginThunk } from './auth-thunk';
import type { User } from '@/features/users/types/user-type';

type AuthState = {
  user: User | null;
  initialized: boolean;
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
      // --- LOGIN THUNK ---
      .addCase(loginThunk.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(loginThunk.fulfilled, (state, action) => {
        state.loading = false;
        state.user = action.payload.user;
      })
      .addCase(loginThunk.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      })

      .addCase(initializeAuthThunk.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(initializeAuthThunk.fulfilled, (state, action) => {
        state.loading = false;
        state.user = action.payload as User;
        state.initialized = true;
      })
      .addCase(initializeAuthThunk.rejected, (state, action) => {
        state.loading = false;
        state.user = null;
        state.initialized = true;
        state.error = action.payload as string;
      });
  },
});

export const { logout, clearError, markInitialized } = authSlice.actions;

export default authSlice.reducer;