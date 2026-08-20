import React from 'react';
import { Card, Typography, Tag, Button } from 'antd';
import { BookOutlined, ClockCircleOutlined, UserOutlined, SettingOutlined } from '@ant-design/icons';
import { type Course, CourseLevel, CourseStatus } from '../types/course-type';
import { USER_ROLE, type UserRole } from '@/features/users/types/user-role-type';

const { Title, Text, Paragraph } = Typography;

interface CourseCardProps {
  course: Course;
  userRole?: UserRole;
  onActionClick?: (course: Course) => void;
  onDetailClick?: (course: Course) => void;
}

const CourseCard: React.FC<CourseCardProps> = ({ course, userRole, onActionClick, onDetailClick }) => {
  const isAdminOrInstructor = userRole === USER_ROLE.ADMIN || userRole === USER_ROLE.INSTRUCTOR;

  const getLevelColor = (level: string) => {
    switch (level) {
      case CourseLevel.BEGINNER: return 'green';
      case CourseLevel.INTERMEDIATE: return 'blue';
      case CourseLevel.ADVANCED: return 'purple';
      default: return 'default';
    }
  };

  const handleDetail = () => {
    if (onDetailClick) onDetailClick(course);
  };

  return (
    <Card
      hoverable
      className="flex h-full flex-col overflow-hidden rounded-xl border-gray-100 shadow-sm transition-all hover:shadow-md"
      cover={
        <div 
          className="flex h-44 cursor-pointer items-center justify-center overflow-hidden border-b border-gray-100 bg-gray-50"
          onClick={handleDetail}
        >
          {course.thumbnailUrl ? (
            <img src={course.thumbnailUrl} alt={course.title} className="h-full w-full object-cover transition-transform duration-300 hover:scale-105" />
          ) : (
            <div className="flex flex-col items-center text-gray-300">
              <BookOutlined className="mb-2 text-4xl" />
              <Text type="secondary">Chưa có ảnh</Text>
            </div>
          )}
        </div>
      }
      bodyStyle={{ padding: '1.25rem', flexGrow: 1, display: 'flex', flexDirection: 'column' }}
    >
      <div className="mb-3 flex items-start justify-between gap-2">
        <Tag color={getLevelColor(course.level)} className="m-0 rounded-md font-medium border-0 px-2 py-0.5">
          {course.level}
        </Tag>
        {isAdminOrInstructor && (
          <Tag color={course.status === CourseStatus.PUBLISHED ? 'success' : 'default'} className="m-0 rounded-md border-0">
            {course.statusText || course.status}
          </Tag>
        )}
      </div>

      <Title 
        level={5} 
        className="line-clamp-2 mb-1 min-h-[2.5rem] leading-snug cursor-pointer hover:text-blue-600 transition-colors" 
        title={course.title}
        onClick={handleDetail}
      >
        {course.title}
      </Title>

      <Text className="mb-3 block text-xs text-gray-400">Mã: {course.courseCode}</Text>

      <Paragraph className="line-clamp-2 mb-4 flex-grow text-sm text-gray-500" title={course.description}>
        {course.description || 'Chưa có mô tả chi tiết cho khóa học này.'}
      </Paragraph>

      <div className="mb-5 flex flex-col gap-2 text-xs text-gray-500">
        <span className="flex items-center gap-1.5"><ClockCircleOutlined /> Tổng cộng: {course.totalSessions || 0} buổi học</span>
        <span className="flex items-center gap-1.5"><UserOutlined /> Giảng viên: <span className="italic">Chưa cập nhật</span></span>
      </div>

      <div className="mt-auto border-t border-gray-50 pt-4">
        <Button
          type={isAdminOrInstructor ? 'default' : 'primary'}
          className={`w-full rounded-lg ${!isAdminOrInstructor ? 'bg-blue-600 font-medium' : 'text-gray-600 hover:text-blue-600'}`}
          icon={isAdminOrInstructor ? <SettingOutlined /> : <BookOutlined />}
          onClick={() => onActionClick && onActionClick(course)}
        >
          {isAdminOrInstructor ? 'Quản lý khóa học' : 'Xem khóa học'}
        </Button>
      </div>
    </Card>
  );
};

export default CourseCard;