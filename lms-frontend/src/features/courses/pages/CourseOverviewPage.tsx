import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Typography, Card, Tag, Button, Spin, Row, Col, Collapse, Empty, Skeleton, Divider, message } from 'antd';
import { ArrowLeftOutlined, BookOutlined, ClockCircleOutlined, UserOutlined, PlayCircleOutlined } from '@ant-design/icons';
import { courseApi } from '../api/course-api';
import { type Course, CourseLevel, CourseStatus, type Module, type ApiError } from '../types/course-type';
import { useAppSelector } from '@/app/redux/hooks';
import { USER_ROLE } from '@/features/users/types/user-role-type';
import { studentLearningApi } from '@/features/enrollments/api/enrollment-api';
import type { EnrollmentResponse } from '@/features/enrollments/types/enrollment-type';

const { Title, Text, Paragraph } = Typography;

const CourseOverviewPage: React.FC = () => {
  const { id: routeParam } = useParams<{ id: string }>(); 
  const navigate = useNavigate();
  const { user } = useAppSelector((state) => state.auth);

  // FIX: Khởi tạo messageApi để không bị lỗi Static function context
  const [messageApi, contextHolder] = message.useMessage();

  const [currentParam, setCurrentParam] = useState(routeParam);
  const [course, setCourse] = useState<Course | null>(null);
  const [curriculum, setCurriculum] = useState<Module[]>([]);
  
  const [loadingCourse, setLoadingCourse] = useState(true);
  const [loadingCurriculum, setLoadingCurriculum] = useState(true);
  const [errorCourse, setErrorCourse] = useState(false);

  const [enrollment, setEnrollment] = useState<EnrollmentResponse | null>(null);
  const [loadingEnrollment, setLoadingEnrollment] = useState(false);
  const [isEnrolling, setIsEnrolling] = useState(false);

  if (routeParam !== currentParam) {
    setCurrentParam(routeParam);
    setLoadingCourse(true);
    setLoadingCurriculum(true);
    setCourse(null);
    setCurriculum([]);
    setErrorCourse(false);
  }

  const isAdminOrInstructor = user?.role === USER_ROLE.ADMIN || user?.role === USER_ROLE.INSTRUCTOR;
  const isStudent = user?.role === USER_ROLE.STUDENT;

  useEffect(() => {
    if (!routeParam) return;

    courseApi.getDetail(routeParam)
      .then(res => {
        const courseData = res.data || res;
        setCourse(courseData);
        setErrorCourse(false);

        courseApi.getCurriculum(courseData.id)
          .then(currRes => {
            const modulesList = currRes.data?.modules || currRes.data || currRes || [];
            setCurriculum(Array.isArray(modulesList) ? modulesList : []);
          })
          .catch(err => {
            console.warn('Backend chưa có/lỗi API Curriculum:', err);
            setCurriculum([]);
          })
          .finally(() => setLoadingCurriculum(false));

        if (isStudent) {
          setLoadingEnrollment(true);
          studentLearningApi.getEnrollmentDetail(courseData.id)
            .then(enrollRes => setEnrollment(enrollRes))
            .catch(err => {
              const status = err.response?.status;
              // FIX: Bắt luôn cả lỗi 400 (Bad Request) từ Backend ném ra để Console không bị đỏ
              if (status !== 404 && status !== 400) {
                console.warn('Lỗi check enrollment:', err);
              }
            })
            .finally(() => setLoadingEnrollment(false));
        }
      })
      .catch(err => {
        console.error('Lỗi khi tải chi tiết khóa học:', err);
        setErrorCourse(true);
        setLoadingCurriculum(false);
      })
      .finally(() => setLoadingCourse(false));
  }, [routeParam, isStudent]);

  const handleEnroll = async () => {
    if (!course) return;
    try {
      setIsEnrolling(true);
      await studentLearningApi.enroll(course.id);
      messageApi.success('Đăng ký khóa học thành công!');
      navigate(`/learning/courses/${course.id}`);
    } catch (error: unknown) {
      const apiErr = error as ApiError;
      const status = apiErr.response?.status;
      if (status === 409) messageApi.warning('Bạn đã đăng ký khóa học này.');
      else if (status === 400) messageApi.warning('Khóa học này hiện chưa thể đăng ký.');
      else if (status === 403) messageApi.error('Bạn không có quyền đăng ký khóa học này.');
      else messageApi.error('Lỗi khi đăng ký khóa học. Vui lòng thử lại sau.');
    } finally {
      setIsEnrolling(false);
    }
  };

  const getLevelColor = (level: string) => {
    switch (level) {
      case CourseLevel.BEGINNER: return 'green';
      case CourseLevel.INTERMEDIATE: return 'blue';
      case CourseLevel.ADVANCED: return 'purple';
      default: return 'default';
    }
  };

  if (errorCourse || (!loadingCourse && !course)) {
    return (
      <div className="flex flex-col items-center justify-center p-20 text-center">
        <Empty description={<span className="text-gray-500 text-lg">Không tìm thấy khóa học này hoặc đã xảy ra lỗi.</span>} />
        <Button type="primary" onClick={() => navigate(-1)} className="mt-4">Quay lại danh sách</Button>
      </div>
    );
  }

  const ActionButton = () => {
    if (isAdminOrInstructor) {
      return (
        <Button type="primary" size="large" className="w-full bg-gray-800 font-semibold h-12 text-lg" onClick={() => navigate(`/course-management/${course!.slug || course!.id}/edit`)}>
          Quản lý khóa học
        </Button>
      );
    }
    
    if (isStudent) {
      if (loadingEnrollment) return <Button size="large" className="w-full h-12" loading>Đang tải...</Button>;
      if (enrollment) {
        return (
          <Button type="primary" size="large" className="w-full bg-green-600 font-semibold h-12 text-lg" onClick={() => navigate(`/learning/courses/${course!.id}`)}>
            Tiếp tục học ({enrollment.progressPercentage}%)
          </Button>
        );
      }
      return (
        <Button type="primary" size="large" className="w-full bg-blue-600 font-semibold h-12 text-lg" onClick={handleEnroll} loading={isEnrolling}>
          Đăng ký học ngay
        </Button>
      );
    }
    return null;
  };

  return (
    <div className="mx-auto max-w-6xl p-4 md:p-6 lg:p-8">
      {contextHolder}
      <Button type="text" icon={<ArrowLeftOutlined />} onClick={() => navigate(-1)} className="mb-6 hover:bg-gray-100">
        Quay lại
      </Button>

      <Row gutter={[32, 32]}>
        <Col xs={24} lg={16}>
          {loadingCourse ? (
            <Skeleton active paragraph={{ rows: 6 }} />
          ) : (
            <>
              <div className="mb-8">
                <div className="mb-4 flex flex-wrap gap-2">
                  <Tag color={getLevelColor(course!.level)} className="m-0 px-3 py-1 font-medium border-0 rounded-md text-sm">
                    {course!.level}
                  </Tag>
                  {isAdminOrInstructor && (
                    <Tag color={course!.status === CourseStatus.PUBLISHED ? 'success' : 'default'} className="m-0 px-3 py-1 border-0 rounded-md text-sm">
                      {course!.statusText || course!.status}
                    </Tag>
                  )}
                  {course!.category && (
                    <Tag color="geekblue" className="m-0 px-3 py-1 border-0 rounded-md text-sm">
                      {course!.category.name}
                    </Tag>
                  )}
                </div>

                <Title level={1} className="mb-4 text-3xl font-extrabold text-gray-900 md:text-4xl">
                  {course!.title}
                </Title>

                <Paragraph className="text-lg text-gray-600 leading-relaxed mb-8 whitespace-pre-wrap">
                  {course!.description || 'Khóa học này hiện chưa có mô tả chi tiết.'}
                </Paragraph>

                <div className="block lg:hidden mb-8">
                  <ActionButton />
                </div>
              </div>

              <Divider className="my-8" />

              <div>
                <Title level={3} className="mb-6 font-bold text-gray-800">Nội dung khóa học</Title>
                
                {loadingCurriculum ? (
                  <Skeleton active paragraph={{ rows: 4 }} />
                ) : curriculum.length > 0 ? (
                  <Collapse 
                    expandIconPlacement="end"
                    className="bg-white rounded-lg shadow-sm border-gray-100"
                    items={curriculum.map((mod) => ({
                      key: mod.id,
                      label: <div className="font-semibold text-gray-800 py-1">{mod.title}</div>,
                      children: (
                        <div className="flex flex-col">
                          {(!mod.lessons || mod.lessons.length === 0) ? (
                            <div className="text-gray-400 py-2 text-sm italic">Chưa có bài học nào trong học phần này.</div>
                          ) : (
                            mod.lessons.map(lesson => (
                              <div key={lesson.id} className="flex items-center gap-3 text-gray-600 py-2 border-b border-gray-50 last:border-0 hover:bg-gray-50 px-2 rounded-md transition-colors cursor-pointer">
                                <PlayCircleOutlined className="text-blue-500" />
                                <span>{lesson.title}</span>
                              </div>
                            ))
                          )}
                        </div>
                      )
                    }))}
                  />
                ) : (
                  <div className="bg-gray-50 border border-dashed border-gray-200 rounded-xl p-8 text-center">
                    <Text className="text-gray-500 font-medium">Khóa học chưa có nội dung.</Text>
                  </div>
                )}
              </div>
            </>
          )}
        </Col>

        <Col xs={24} lg={8}>
          <div className="sticky top-24">
            {/* FIX: Thay bodyStyle thành styles={{ body: {...} }} */}
            <Card className="overflow-hidden rounded-xl border-gray-100 shadow-md" styles={{ body: { padding: 0 } }}>
              <div className="flex h-56 items-center justify-center bg-gray-100">
                {loadingCourse ? (
                  <Spin />
                ) : course?.thumbnailUrl ? (
                  <img src={course.thumbnailUrl} alt={course.title} className="h-full w-full object-cover" />
                ) : (
                  <div className="flex flex-col items-center text-gray-400">
                    <BookOutlined className="mb-2 text-5xl" />
                    <Text type="secondary">Chưa có ảnh</Text>
                  </div>
                )}
              </div>

              <div className="p-6">
                <div className="hidden lg:block mb-6">
                  <ActionButton />
                </div>

                <Title level={5} className="mb-4 mt-0 border-b border-gray-100 pb-2 text-gray-800">Thông tin chung</Title>
                {loadingCourse ? (
                  <Skeleton active paragraph={{ rows: 3 }} title={false} />
                ) : (
                  <div className="flex flex-col gap-4 text-gray-600">
                    <div className="flex justify-between items-center">
                      <span className="flex items-center gap-2"><BookOutlined /> Mã khóa học</span>
                      <span className="font-medium text-gray-800">{course!.courseCode || 'Đang cập nhật'}</span>
                    </div>
                    <div className="flex justify-between items-center">
                      <span className="flex items-center gap-2"><ClockCircleOutlined /> Thời lượng</span>
                      <span className="font-medium text-gray-800">{course!.totalSessions || 0} buổi</span>
                    </div>
                    <div className="flex justify-between items-center">
                      <span className="flex items-center gap-2"><UserOutlined /> Giảng viên</span>
                      <span className="font-medium text-gray-800 text-right max-w-[150px] truncate" title={course!.instructorName}>
                        {course!.instructorName || 'Chưa cập nhật'}
                      </span>
                    </div>
                  </div>
                )}
              </div>
            </Card>
          </div>
        </Col>
      </Row>
    </div>
  );
};

export default CourseOverviewPage;