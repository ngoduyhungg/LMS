import dayjs, { Dayjs } from 'dayjs';
import { FormFieldType } from '@/shared/types/form-field-type';
import {
  formatDateTimeQuery,
  formatDateToPicker,
  formatDateToQuery,
  formatTimeToPicker,
  formatTimeToQuery,
} from './date';
import type { FormField, SectionForm } from '../components/modal/ModalFormCustom';

/* Dùng khi bạn muốn format values dựa trên các mảng Fields của các Section */
export const formatSectionFieldsFormValues = <T>(values: T, sections: SectionForm[]): T => {
  const formattedValues: Record<string, unknown> = {
    ...(values as Record<string, unknown>),
  };

  sections.forEach((section) => {
    section.fields.forEach((field) => {
      const value = formattedValues[field.name as string];

      if (!value) return;

      switch (field.type) {
        case FormFieldType.InputNumber:
          formattedValues[field.name as string] = Number(value);
          break;
        case FormFieldType.DatePicker:
          formattedValues[field.name as string] = dayjs.isDayjs(value)
            ? formatDateToQuery(value as Dayjs)
            : formatDateToPicker(value as string);
          break;
        case FormFieldType.DateTimePicker:
          formattedValues[field.name as string] = dayjs.isDayjs(value)
            ? formatDateTimeQuery(value as Dayjs)
            : formatDateToPicker(value as string);
          break;
        case FormFieldType.TimePicker:
          formattedValues[field.name as string] = dayjs.isDayjs(value)
            ? formatTimeToQuery(value as Dayjs)
            : formatTimeToPicker(value as string);
          break;
        default:
          break;
      }
    });
  });

  return formattedValues as T;
};

/* Dùng khi bạn muốn format values dựa trên các field trong một mảng Fields */
export const formatFieldsFormValues = <T>(values: T, fields: FormField<any>[]): T => {
  const formattedValues: Record<string, unknown> = {
    ...(values as Record<string, unknown>),
  };

  fields.forEach((field) => {
    const value = formattedValues[field.name as string];

    if (!value) return;

    switch (field.type) {
      case FormFieldType.DatePicker:
        formattedValues[field.name as string] = dayjs.isDayjs(value)
          ? formatDateToQuery(value as Dayjs)
          : formatDateToPicker(value as string);
        break;
      case FormFieldType.DateTimePicker:
        formattedValues[field.name as string] = dayjs.isDayjs(value)
          ? formatDateTimeQuery(value as Dayjs)
          : formatDateToPicker(value as string);
        break;
      case FormFieldType.TimePicker:
        formattedValues[field.name as string] = dayjs.isDayjs(value)
          ? formatTimeToQuery(value as Dayjs)
          : formatTimeToPicker(value as string);
        break;
      default:
        break;
    }
  });

  return formattedValues as T;
};
