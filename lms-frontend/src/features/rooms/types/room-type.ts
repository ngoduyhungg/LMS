export interface Room {
  id: string;
  roomCode: string;
  name: string;
  description: string | null;
  capacity: number;
  createdAt: string;
  updatedAt: string;
}
