import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Typography, Card, Tag, Button, Spin, Row, Col } from 'antd';
import { ArrowLeftOutlined, BookOutlined } from '@ant-design/icons';
import { courseRoleAdminApi } from '../api/course-api';
import { type Course, CourseLevel, CourseStatus } from '../types/course-type';
import { useAppSelector } from '@/app/redux/hooks';
import { USER_ROLE } from '@/features/users/types/user-role-type';

const { Title, Text, Paragraph } = Typography;

const CourseOverviewPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [course, setCourse] = useState<Course | null>(null);
  const [loading, setLoading] = useState(true);
  const { user } = useAppSelector((state) => state.auth);

  const isAdminOrInstructor = user?.role === USER_ROLE.ADMIN || user?.role === USER_ROLE.INSTRUCTOR;

  useEffect(() => {
    if (id) {
      courseRoleAdminApi.getDetail(id)
        .then(res => {
          setCourse(res.data || res);
        })
        .catch(err => {
          console.error('Lỗi khi tải chi tiết khóa học:', err);
        })
        .finally(() => setLoading(false));
    }
  }, [id]);

  const getLevelColor = (level: string) => {
    switch (level) {
      case CourseLevel.BEGINNER: return 'green';
      case CourseLevel.INTERMEDIATE: return 'blue';
      case CourseLevel.ADVANCED: return 'purple';
      default: return 'default';
    }
  };

  if (loading) {
    return (
      <div className="flex h-64 items-center justify-center">
        <Spin size="large" />
      </div>
    );
  }

  if (!course) {
    return (
      <div className="p-8 text-center">
        <Text>Không tìm thấy khóa học.</Text>
        <div className="mt-4">
          <Button onClick={() => navigate(-1)}>Quay lại</Button>
        </div>
      </div>
    );
  }

  return (
    <div className="mx-auto max-w-5xl p-4 md:p-6 lg:p-8">
      <Button type="text" icon={<ArrowLeftOutlined />} onClick={() => navigate(-1)} className="mb-6">
        Quay lại
      </Button>

      <Card className="overflow-hidden rounded-xl border-gray-100 shadow-sm" bodyStyle={{ padding: 0 }}>
        <Row>
          <Col xs={24} md={10} className="bg-gray-50 border-r border-gray-100">
            <div className="flex h-64 md:h-full min-h-[300px] items-center justify-center p-6">
              {course.thumbnailUrl ? (
                <img src={course.thumbnailUrl} alt={course.name} className="h-full w-full object-cover rounded-lg shadow-sm" />
              ) : (
                <div className="flex flex-col items-center text-gray-300">
                  <BookOutlined className="mb-3 text-6xl" />
                  <Text type="secondary">Chưa có ảnh mô tả</Text>
                </div>
              )}
            </div>
          </Col>
          
          <Col xs={24} md={14} className="flex flex-col p-6 md:p-8">
            <div className="mb-4 flex flex-wrap gap-2">
              <Tag color={getLevelColor(course.level)} className="m-0 rounded-md border-0 px-2 py-0.5 font-medium">
                {course.level}
              </Tag>
              <Tag color={course.status === CourseStatus.OPEN ? 'success' : 'default'} className="m-0 rounded-md border-0">
                {course.statusText || course.status}
              </Tag>
            </div>
            
            <Title level={2} className="mb-2 mt-0 font-bold text-gray-800">
              {course.name}
            </Title>
            
            <Text className="mb-6 block text-sm text-gray-400">
              Mã khóa học: {course.courseCode}
            </Text>
            
            <Title level={5} className="mb-2 text-gray-800">Mô tả khóa học</Title>
            <Paragraph className="flex-grow whitespace-pre-wrap text-gray-500 leading-relaxed">
              {course.description || 'Chưa có mô tả chi tiết cho khóa học này.'}
            </Paragraph>

            <div className="mt-8 flex gap-3 border-t border-gray-100 pt-6">
              {!isAdminOrInstructor ? (
                <Button type="primary" size="large" className="w-full bg-blue-600 font-medium md:w-auto">
                  Đăng ký học
                </Button>
              ) : (
                <Button type="primary" size="large" className="w-full bg-gray-800 font-medium md:w-auto">
                  Chỉnh sửa khóa học
                </Button>
              )}
            </div>
          </Col>
        </Row>
      </Card>
    </div>
  );
};

export default CourseOverviewPage;