export interface Schedule {
  id: string;
  slotCode: string;
  weekday: number;
  weekdayName: string;
  startTime: string;
  endTime: string;
  note: string | null;
  createdAt: string;
  updatedAt: string;
}
