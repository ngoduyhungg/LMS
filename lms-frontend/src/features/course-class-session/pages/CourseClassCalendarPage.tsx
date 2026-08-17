import '../styles/index.css';

import { useState } from 'react';

import FullCalendar from '@fullcalendar/react';

import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid';
import interactionPlugin from '@fullcalendar/interaction';
import listPlugin from '@fullcalendar/list';
import { courseClassSessionRoleAdminApi } from '../api/course-class-session-api';
import type { CalendarSession } from '../types/course-class-session-calendar-type';
import { type CourseClassSessionStatusType } from '../types/course-class-session-type';
import { mappedColorByStatus } from '../constants/calendar-color';
import { formatDateTimeQuery } from '@/shared/utils/date';
import DrawerCourseClassInformation from '../components/DrawerCourseClassInformation';

const CourseClassCalendarPage = () => {
  const { calendar: getCalendarSessions } = courseClassSessionRoleAdminApi;
  const [events, setEvents] = useState([]);

  const [open, setOpen] = useState(false);

  const [selectedSession, setSelectedSession] = useState<CalendarSession | null>(null);

  const handleDatesSet = async (arg: any) => {
    try {
      if (!arg.start || !arg.end) {
        throw new Error('Invalid date range');
      }

      const res = await getCalendarSessions({
        startDate: formatDateTimeQuery(arg.start) as string,
        endDate: formatDateTimeQuery(arg.end) as string,
      });

      if (!res.success) {
        throw new Error('Failed to fetch calendar sessions');
      }

      const sessions = res.data || [];

      const calendarEvents = sessions.map((item: CalendarSession) => ({
        id: item.id,

        title: item.title,

        start: item.start,

        end: item.end,

        extendedProps: item,
      }));

      setEvents(calendarEvents);
    } catch (error) {
      console.error('Error fetching calendar sessions:', error);
    }
  };

  const handleCloseDrawer = () => {
    setOpen(false);
    setSelectedSession(null);
  };

  return (
    <div className="bg-white p-4 rounded-lg">
      <FullCalendar
        plugins={[dayGridPlugin, timeGridPlugin, interactionPlugin, listPlugin]}
        initialView="timeGridWeek"
        height="auto"
        events={events}
        eventClick={(info) => {
          setSelectedSession(info.event.extendedProps as CalendarSession);

          setOpen(true);
        }}
        datesSet={(arg) => {
          handleDatesSet(arg);
        }}
        eventClassNames={(arg) => {
          const status = arg.event.extendedProps.status as CourseClassSessionStatusType;

          return [mappedColorByStatus[status]];
        }}
        headerToolbar={{
          left: 'prev,next today',
          center: 'title',
          right: 'timeGridDay,timeGridWeek,dayGridMonth,listWeek',
        }}
      />

      <DrawerCourseClassInformation
        open={open}
        selectedSession={selectedSession}
        onClose={handleCloseDrawer}
      />
    </div>
  );
};

export default CourseClassCalendarPage;
