import { DatePicker, type DatePickerProps } from 'antd';
import { FORMAT_DATE_TIME } from '../../constants/format-date';

interface DateTimePickerCustomProps extends DatePickerProps {}

const DateTimePickerCustom = ({ ...props }: DateTimePickerCustomProps) => {
  return <DatePicker showTime format={FORMAT_DATE_TIME} className="w-full min-h-10" {...props} />;
};

export default DateTimePickerCustom;
