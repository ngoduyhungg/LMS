import React, { useEffect, useState } from 'react';
import { Typography, Row, Col, Card, Progress, Empty, Skeleton, Button } from 'antd';
import { useNavigate } from 'react-router-dom';
import { studentLearningApi } from '../api/enrollment-api';
import { courseApi } from '@/features/courses/api/course-api';
import type { EnrollmentResponse } from '../types/enrollment-type';
import type { Course } from '@/features/courses/types/course-type';

const { Title, Text } = Typography;

interface EnrolledCourseData extends EnrollmentResponse {
  courseDetails?: Course;
}

const MyLearningPage: React.FC = () => {
  const navigate = useNavigate();
  const [enrollments, setEnrollments] = useState<EnrolledCourseData[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchMyLearning = async () => {
      try {
        setLoading(true);
        const enrolls = await studentLearningApi.getMyEnrollments();
        
        const augmentedEnrolls = await Promise.all(
          enrolls.map(async (e) => {
            try {
              const res = await courseApi.getDetail(String(e.courseId));
              return { ...e, courseDetails: res.data || res };
            } catch (err) {
              return e; 
            }
          })
        );
        setEnrollments(augmentedEnrolls);
      } catch (error) {
        console.error('Lỗi tải My Learning:', error);
      } finally {
        setLoading(false);
      }
    };
    fetchMyLearning();
  }, []);

  return (
    <div className="mx-auto max-w-7xl p-4 md:p-6 lg:p-8">
      <div className="mb-8">
        <Title level={2} className="mb-2 text-2xl font-bold text-gray-800">Khóa học của tôi</Title>
        <Text className="text-gray-500">Tiếp tục hành trình học tập của bạn.</Text>
      </div>

      {loading ? (
        <Row gutter={[24, 24]}>
          {[1, 2, 3].map(i => (
            <Col xs={24} sm={12} lg={8} key={i}>
              <Card className="rounded-xl overflow-hidden shadow-sm" styles={{ body: { padding: 16 } }}>
                <Skeleton.Image active className="!w-full !h-40 mb-4 rounded-md" />
                <Skeleton active paragraph={{ rows: 2 }} />
              </Card>
            </Col>
          ))}
        </Row>
      ) : enrollments.length > 0 ? (
        <Row gutter={[24, 24]}>
          {enrollments.map(enr => (
            <Col xs={24} sm={12} lg={8} key={enr.id}>
              <Card 
                hoverable 
                className="h-full rounded-xl overflow-hidden shadow-sm border-gray-200 flex flex-col"
                styles={{ body: { padding: 16, display: 'flex', flexDirection: 'column', flexGrow: 1 } }}
                cover={
                  <div className="h-40 bg-gray-100 overflow-hidden">
                    {enr.courseDetails?.thumbnailUrl ? (
                      <img src={enr.courseDetails.thumbnailUrl} alt="cover" className="w-full h-full object-cover" />
                    ) : (
                      <div className="w-full h-full flex items-center justify-center text-gray-400">Không có ảnh</div>
                    )}
                  </div>
                }
              >
                <div className="flex-grow">
                  <Title level={5} className="mb-2 mt-0 line-clamp-2" title={enr.courseDetails?.title || `Khóa học #${enr.courseId}`}>
                    {enr.courseDetails?.title || `Khóa học #${enr.courseId}`}
                  </Title>
                  <Progress percent={enr.progressPercentage || 0} size="small" status={enr.status === 'COMPLETED' ? 'success' : 'active'} />
                </div>
                <div className="mt-4">
                  <Button 
                    type="primary" 
                    block 
                    className={enr.status === 'COMPLETED' ? 'bg-green-600' : 'bg-blue-600'}
                    onClick={() => navigate(`/learning/courses/${enr.courseId}`)}
                  >
                    {enr.status === 'COMPLETED' ? 'Xem lại khóa học' : 'Tiếp tục học'}
                  </Button>
                </div>
              </Card>
            </Col>
          ))}
        </Row>
      ) : (
        <div className="flex flex-col items-center justify-center py-20 bg-gray-50 rounded-2xl border border-dashed border-gray-200">
          <Empty description={<span className="text-gray-500">Bạn chưa đăng ký khóa học nào.</span>} />
          <Button type="primary" className="mt-4" onClick={() => navigate('/courses')}>Khám phá khóa học</Button>
        </div>
      )}
    </div>
  );
};

export default MyLearningPage;