import axios from 'axios';
import type { LoginPayload } from '../types/auth-type';

const KEYCLOAK_URL = 'http://localhost:8180/realms/lms-realm/protocol/openid-connect';

export const loginApi = async (payload: LoginPayload) => {
  const params = new URLSearchParams();
  params.append('grant_type', 'password');
  params.append('client_id', 'lms-frontend');
  params.append('username', payload.email);
  params.append('password', payload.password);
  
  const res = await axios.post(`${KEYCLOAK_URL}/token`, params, {
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
  });
  return res.data;
};

export const refreshTokenApi = async (refreshToken: string) => {
  const params = new URLSearchParams();
  params.append('grant_type', 'refresh_token');
  params.append('client_id', 'lms-frontend');
  params.append('refresh_token', refreshToken);

  const res = await axios.post(`${KEYCLOAK_URL}/token`, params, {
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
  });
  return res.data;
};

export const logoutApi = async (refreshToken: string) => {
  const params = new URLSearchParams();
  params.append('client_id', 'lms-frontend');
  params.append('refresh_token', refreshToken);

  await axios.post(`${KEYCLOAK_URL}/logout`, params, {
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
  });
};