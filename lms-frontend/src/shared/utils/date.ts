import dayjs, { Dayjs } from 'dayjs';
import relativeTime from 'dayjs/plugin/relativeTime';
import 'dayjs/locale/vi';
import { FORMAT_DATE, FORMAT_TIME } from '../constants/format-date';

dayjs.extend(relativeTime);
dayjs.locale('vi');

/*
 * Format hiển thị thời gian tương đối
 * vd: 5 phút trước, 2 giờ trước, 1 ngày trước
 */
export const formatRelativeTime = (date: string | Date): string => {
  return dayjs(date).fromNow();
};

/**
 * Convert dayjs object -> ISO string
 * Gửi lên BE
 */
export const formatDateTimeQuery = (value?: Dayjs | null): string | null => {
  if (!value) return null;

  return value.toISOString();
};

/**
 * Convert ISO string -> dayjs object
 * dùng cho DatePicker setFieldsValue
 */
export const formatDateToPicker = (value?: string | Date | null): Dayjs | null => {
  if (!value) return null;

  return dayjs(value);
};

/**
 * Convert dayjs -> YYYY-MM-DD
 * dùng cho query params
 */
export const formatDateToQuery = (value?: Dayjs | null): string | null => {
  if (!value) return null;

  return value.format('YYYY-MM-DD');
};

/**
 * Format hiển thị UI
 * vd: 12/05/2026
 */
export const formatDate = (value?: string | Date | null, format = FORMAT_DATE): string => {
  if (!value) return '';

  return dayjs(value).format(format);
};

/**
 * Convert string -> dayjs object
 * dùng cho TimePicker setFieldsValue
 */
export const formatTimeToPicker = (
  value?: string | Date | null,
  format = FORMAT_TIME,
): Dayjs | string => {
  if (!value) return '';

  return dayjs(value, format);
};

/**
 * Convert dayjs -> HH:mm
 * dùng cho query params
 */
export const formatTimeToQuery = (value?: Dayjs | null): string | null => {
  if (!value) return null;

  return value.format(FORMAT_TIME);
};
