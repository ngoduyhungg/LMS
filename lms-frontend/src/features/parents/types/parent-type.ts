import type { User } from '@/features/users/types/user-type';

export interface Parent extends User {
  id: string;
  userId: string;
}
