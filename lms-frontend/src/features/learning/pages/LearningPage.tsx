import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Layout, Typography, Spin, Progress, Button, Empty, message, Collapse, Tag } from 'antd';
import { LeftOutlined, CheckCircleFilled, CheckOutlined, PlayCircleOutlined, FileTextOutlined } from '@ant-design/icons';
import { studentLearningApi } from '@/features/enrollments/api/enrollment-api';
import { courseApi } from '@/features/courses/api/course-api';
import type { EnrollmentResponse } from '@/features/enrollments/types/enrollment-type';
import type { Module, Lesson } from '@/features/courses/types/course-type';

const { Header, Sider, Content } = Layout;
const { Title, Text } = Typography;

const LearningPage: React.FC = () => {
  const { courseId } = useParams<{ courseId: string }>();
  const navigate = useNavigate();
  const [messageApi, contextHolder] = message.useMessage();

  const [enrollment, setEnrollment] = useState<EnrollmentResponse | null>(null);
  const [courseTitle, setCourseTitle] = useState<string>('Đang tải...');
  const [curriculum, setCurriculum] = useState<Module[]>([]);
  
  const [activeLessonId, setActiveLessonId] = useState<string | null>(null);
  const [activeLessonData, setActiveLessonData] = useState<Lesson | null>(null);
  
  const [loading, setLoading] = useState(true);
  const [contentLoading, setContentLoading] = useState(false);
  const [updatingProgress, setUpdatingProgress] = useState(false);

  useEffect(() => {
    if (!courseId) return;
    const initLearning = async () => {
      try {
        setLoading(true);
        // 1. Fetch Enrollment check
        const enr = await studentLearningApi.getEnrollmentDetail(courseId);
        setEnrollment(enr);

        // 2. Fetch Curriculum (KHÔNG gọi getDetail bằng ID nữa để tránh lỗi 404 từ Backend)
        const currRes = await courseApi.getCurriculum(courseId);
        
        // Trích xuất Title từ response của Curriculum
        const title = currRes.data?.title || currRes.title || `Khóa học #${courseId}`;
        setCourseTitle(title);
        
        const modulesList = currRes.data?.modules || currRes.data || currRes || [];
        const flatModules = Array.isArray(modulesList) ? modulesList : [];
        setCurriculum(flatModules);

        // 3. Set Active Lesson
        if (enr.lastAccessedLessonId) {
          setActiveLessonId(String(enr.lastAccessedLessonId));
        } else {
          const firstMod = flatModules.find((m: Module) => m.lessons && m.lessons.length > 0);
          if (firstMod && firstMod.lessons) {
            setActiveLessonId(String(firstMod.lessons[0].id));
          }
        }
      } catch (error: any) {
        messageApi.destroy();
        if (error.response?.status === 404) {
          messageApi.error('Bạn chưa đăng ký khóa học này hoặc khóa học không tồn tại.');
          // Đẩy về my-learning thay vì courses/:id để tránh lỗi ID/Slug Loop
          navigate('/my-learning');
        } else {
          messageApi.error('Lỗi tải dữ liệu học tập.');
        }
      } finally {
        setLoading(false);
      }
    };
    initLearning();
  }, [courseId, navigate]);

  // Load Content when active lesson changes
  useEffect(() => {
    if (!activeLessonId) return;
    const fetchLesson = async () => {
      try {
        setContentLoading(true);
        const res = await courseApi.getLessonDetail(activeLessonId);
        setActiveLessonData(res.data || res);
      } catch (err) {
        console.error('Lỗi lấy nội dung bài học', err);
        messageApi.destroy();
        messageApi.error('Không thể tải bài học.');
      } finally {
        setContentLoading(false);
      }
    };
    fetchLesson();
  }, [activeLessonId]);

  const handleMarkComplete = async () => {
    if (!courseId || !activeLessonId) return;
    try {
      setUpdatingProgress(true);
      // PUT Progress, backend tự tính %
      const newEnr = await studentLearningApi.updateProgress(courseId, {
        lessonId: activeLessonId,
        watchedSeconds: 0,
        isCompleted: true
      });
      setEnrollment(newEnr);
      
      messageApi.destroy();
      if (newEnr.status === 'COMPLETED') {
        messageApi.success('Chúc mừng! Bạn đã hoàn thành khóa học.');
      } else {
        messageApi.success('Đã lưu tiến độ!');
      }
    } catch (err) {
      messageApi.destroy();
      messageApi.error('Không thể lưu tiến độ.');
    } finally {
      setUpdatingProgress(false);
    }
  };

  const isLessonCompleted = (id: string | number) => {
    return enrollment?.lessonProgresses?.some(p => String(p.lessonId) === String(id) && p.isCompleted);
  };

  if (loading) return <div className="flex h-screen items-center justify-center bg-gray-50"><Spin size="large" /></div>;
  if (!enrollment) return null;

  return (
    <Layout className="h-screen overflow-hidden bg-white">
      {contextHolder}
      <Header className="bg-gray-900 px-4 flex items-center justify-between border-b border-gray-800 h-16">
        <div className="flex items-center gap-4 text-white">
          <Button type="text" className="text-gray-300 hover:text-white" icon={<LeftOutlined />} onClick={() => navigate('/my-learning')} />
          <Title level={5} className="!text-white !m-0 !font-medium hidden md:block truncate max-w-lg">{courseTitle}</Title>
        </div>
        <div className="flex items-center gap-4 text-white min-w-[200px] justify-end">
          <div className="flex flex-col items-end w-full max-w-[150px]">
            <Text className="text-gray-300 text-xs mb-1">
              Tiến độ: {enrollment.progressPercentage}%
            </Text>
            <Progress percent={enrollment.progressPercentage} showInfo={false} size="small" strokeColor="#52c41a" trailColor="#374151" className="m-0" />
          </div>
          {enrollment.status === 'COMPLETED' && <Tag color="success" className="m-0 border-0">HOÀN THÀNH</Tag>}
        </div>
      </Header>

      <Layout className="h-[calc(100vh-64px)]">
        <Sider width={320} theme="light" className="border-r border-gray-200 overflow-y-auto bg-gray-50 hidden lg:block">
          <div className="p-4 font-bold text-gray-800 border-b border-gray-200 bg-white sticky top-0 z-10">Nội dung học tập</div>
          <Collapse 
            ghost 
            defaultActiveKey={curriculum.map(m => m.id)}
            expandIconPlacement="end"
            className="rounded-none bg-gray-50"
            items={curriculum.map((mod, index) => ({
              key: mod.id,
              label: <div className="font-semibold text-gray-800">Phần {index + 1}: {mod.title}</div>,
              children: (
                <div className="flex flex-col gap-1 -mx-4 -mb-4">
                  {mod.lessons?.map((lesson, lIdx) => {
                    const isActive = activeLessonId === String(lesson.id);
                    const isDone = isLessonCompleted(lesson.id);
                    return (
                      <div 
                        key={lesson.id} 
                        className={`flex items-start gap-3 py-3 px-4 cursor-pointer transition-colors ${isActive ? 'bg-blue-50 border-l-4 border-blue-500' : 'hover:bg-gray-100 border-l-4 border-transparent'}`}
                        onClick={() => setActiveLessonId(String(lesson.id))}
                      >
                        <div className="mt-0.5">
                          {isDone ? (
                            <CheckCircleFilled className="text-green-500 text-base" />
                          ) : (
                            <div className="w-4 h-4 rounded-full border-2 border-gray-300 flex items-center justify-center" />
                          )}
                        </div>
                        <div className="flex flex-col">
                          <Text className={`${isActive ? 'text-blue-700 font-medium' : 'text-gray-700'} text-sm`}>
                            {lIdx + 1}. {lesson.title}
                          </Text>
                          <Text type="secondary" className="text-xs mt-0.5 flex items-center gap-1">
                            {lesson.lessonType === 'VIDEO' ? <PlayCircleOutlined /> : <FileTextOutlined />} 
                            {lesson.lessonType}
                          </Text>
                        </div>
                      </div>
                    );
                  })}
                </div>
              )
            }))}
          />
        </Sider>

        <Content className="overflow-y-auto bg-white p-4 md:p-8">
          <div className="max-w-4xl mx-auto">
            {contentLoading ? (
              <div className="py-20 flex justify-center"><Spin size="large" /></div>
            ) : !activeLessonData ? (
              <Empty description="Vui lòng chọn bài học ở danh sách bên cạnh" className="py-20" />
            ) : (
              <div className="animate-fade-in">
                <Title level={2} className="mb-6 text-gray-900">{activeLessonData.title}</Title>
                
                {activeLessonData.videoUrl && (
                  <div className="mb-8 rounded-xl overflow-hidden bg-black aspect-video flex items-center justify-center shadow-lg">
                    <Text className="text-gray-400">Video Player: {activeLessonData.videoUrl}</Text>
                  </div>
                )}

                {activeLessonData.content && (
                  <div 
                    className="prose prose-blue max-w-none text-gray-700 mb-10" 
                    dangerouslySetInnerHTML={{ __html: activeLessonData.content }} 
                  />
                )}

                <div className="flex justify-end pt-6 border-t border-gray-100 mt-10">
                  <Button 
                    type={isLessonCompleted(activeLessonData.id) ? 'default' : 'primary'} 
                    size="large" 
                    className={isLessonCompleted(activeLessonData.id) ? 'bg-gray-100 text-gray-600' : 'bg-blue-600'}
                    icon={<CheckOutlined />}
                    onClick={handleMarkComplete}
                    loading={updatingProgress}
                    disabled={isLessonCompleted(activeLessonData.id)}
                  >
                    {isLessonCompleted(activeLessonData.id) ? 'Đã hoàn thành' : 'Đánh dấu hoàn thành'}
                  </Button>
                </div>
              </div>
            )}
          </div>
        </Content>
      </Layout>
    </Layout>
  );
};

export default LearningPage;