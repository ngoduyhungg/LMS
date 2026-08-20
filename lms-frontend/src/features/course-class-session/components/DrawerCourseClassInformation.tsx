import { Descriptions, Divider, Drawer, Tag } from 'antd';
import type { CalendarSession } from '../types/course-class-session-calendar-type';
import { formatDate } from '@/shared/utils/date';
import { FORMAT_DATE_TIME } from '@/shared/constants/format-date';
import { mappedColorByStatus } from '../constants/calendar-color';

interface DrawerCourseClassInformationProps {
  open: boolean;
  selectedSession: CalendarSession | null;
  onClose: () => void;
}

const DrawerCourseClassInformation: React.FC<DrawerCourseClassInformationProps> = ({
  open,
  selectedSession,
  onClose,
}) => {
  return (
    <Drawer title="Chi tiết ca học" size={600} open={open} onClose={onClose}>
      {selectedSession && (
        <>
          <Descriptions column={1} bordered size="small">
            <Descriptions.Item label="Lớp học">{selectedSession.courseClassName}</Descriptions.Item>

            <Descriptions.Item label="Khóa học">{selectedSession.courseName}</Descriptions.Item>

            <Descriptions.Item label="Giáo viên chính">
              {selectedSession.mainTeacherName}
            </Descriptions.Item>

            <Descriptions.Item label="Trợ giảng">
              {selectedSession.assistantTeacherName || '-'}
            </Descriptions.Item>

            <Descriptions.Item label="Thời gian bắt đầu">
              {formatDate(selectedSession.start, FORMAT_DATE_TIME)}
            </Descriptions.Item>

            <Descriptions.Item label="Thời gian kết thúc">
              {formatDate(selectedSession.end, FORMAT_DATE_TIME)}
            </Descriptions.Item>

            <Descriptions.Item label="Trạng thái">
              <Tag className={`${mappedColorByStatus[selectedSession.status]} text-white!`}>
                {selectedSession.statusText}
              </Tag>
            </Descriptions.Item>
          </Descriptions>

          {selectedSession.note && (
            <>
              <Divider />

              <div>
                <div className="mb-2 font-medium">Ghi chú</div>

                <div className="text-gray-600">{selectedSession.note}</div>
              </div>
            </>
          )}
        </>
      )}
    </Drawer>
  );
};

export default DrawerCourseClassInformation;
