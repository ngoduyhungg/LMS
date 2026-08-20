import { FORMAT_DATE_TIME } from '@/shared/constants/format-date';
import { formatDate } from '@/shared/utils/date';

export const mappedTimeInformation = ({
  scheduleSlotName,
  startTime,
  endTime,
}: {
  scheduleSlotName: string;
  startTime: string;
  endTime: string;
}) => {
  return `${scheduleSlotName} (${formatDate(startTime, FORMAT_DATE_TIME)} -
              ${formatDate(endTime, FORMAT_DATE_TIME)})`;
};
