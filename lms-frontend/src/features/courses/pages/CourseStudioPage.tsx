import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Tabs, Button, Card, Form, Input, Select, InputNumber, message, Spin, Typography, Space, Popconfirm, Modal, Tag, Row, Col, Empty } from 'antd';
import { ArrowLeftOutlined, SaveOutlined, SendOutlined, PlusOutlined, DeleteOutlined, EditOutlined, HolderOutlined } from '@ant-design/icons';
import { courseApi, getCategories } from '../api/course-api';
import type { Course, Module, Lesson } from '../types/course-type';

const { Title, Text } = Typography;

const CourseStudioPage: React.FC = () => {
  const { id: routeParam } = useParams<{ id: string }>(); 
  const navigate = useNavigate();
  const [messageApi, contextHolder] = message.useMessage(); 

  const [course, setCourse] = useState<Course | null>(null);
  const [curriculum, setCurriculum] = useState<Module[]>([]);
  const [categories, setCategories] = useState<{ label: string; value: string }[]>([]);
  
  const [initialLoading, setInitialLoading] = useState(true);
  const [isRefreshing, setIsRefreshing] = useState(false);
  const [saving, setSaving] = useState(false);
  const [activeTab, setActiveTab] = useState('info');

  const [form] = Form.useForm();
  const [moduleModal, setModuleModal] = useState<{ open: boolean, data?: Module }>({ open: false });
  const [lessonModal, setLessonModal] = useState<{ open: boolean, moduleId?: string, data?: Lesson }>({ open: false });
  const [modForm] = Form.useForm();
  const [lessForm] = Form.useForm();

  const loadCourseInfo = async () => {
    if (!routeParam) return null;
    const courseRes = await courseApi.getDetail(routeParam);
    const courseData = courseRes.data || courseRes;
    setCourse(courseData);
    
    const catId = courseData.category?.id || courseData.categoryId;
    form.setFieldsValue({
      title: courseData.title || undefined,
      summary: courseData.summary || undefined,
      description: courseData.description || undefined,
      price: courseData.price || 0,
      level: courseData.level ? String(courseData.level).toUpperCase() : undefined, 
      categoryId: catId ? String(catId) : undefined, 
      thumbnailUrl: courseData.thumbnailUrl || undefined
    });

    return courseData.id; 
  };

  const reloadCurriculum = async (actualCourseId: string) => {
    setIsRefreshing(true);
    try {
      const currRes = await courseApi.getCurriculum(actualCourseId);
      let modulesList: Module[] = [];
      if (currRes.data && Array.isArray(currRes.data.modules)) {
        modulesList = currRes.data.modules; 
      } else if (currRes.data && Array.isArray(currRes.data)) {
        modulesList = currRes.data;
      } else if (Array.isArray(currRes)) {
        modulesList = currRes;
      }
      setCurriculum(modulesList);
    } catch (e) {
      setCurriculum([]);
    } finally {
      setIsRefreshing(false);
    }
  };

  useEffect(() => {
    const initPage = async () => {
      try {
        setInitialLoading(true);
        const actualId = await loadCourseInfo();
        if (actualId) {
          const catRes = await getCategories().catch(() => ({ data: [] }));
          const catData = catRes.data || catRes.items || catRes;
          if (Array.isArray(catData)) {
            setCategories(catData.map((c: any) => ({ label: c.name, value: String(c.id) }))); 
          }
          await reloadCurriculum(actualId); 
        }
      } catch (error: any) {
        messageApi.error('Lỗi tải dữ liệu Course Studio');
        navigate('/course-management');
      } finally {
        setInitialLoading(false);
      }
    };
    initPage();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [routeParam]);

  const handleSaveInfo = async () => {
    let values;
    try {
      values = await form.validateFields(); 
    } catch (e) {
      return; 
    }

    try {
      setSaving(true);
      const payload = { 
        title: values.title?.trim(),
        summary: values.summary?.trim() || null,
        description: values.description?.trim() || null,
        price: Number(values.price) || 0,
        level: values.level,
        categoryId: values.categoryId ? Number(values.categoryId) : null,
        thumbnailUrl: values.thumbnailUrl?.trim() || null,
        status: course?.status || 'DRAFT' 
      };
      
      await courseApi.update(course!.id, payload);
      messageApi.success('Đã lưu thông tin khóa học');
      await loadCourseInfo(); 
    } catch (error: any) {
      messageApi.error(error.response?.data?.message || 'Lỗi API khi lưu thông tin');
    } finally {
      setSaving(false);
    }
  };

  const handlePublish = async () => {
    let values;
    try {
      values = await form.validateFields();
    } catch (e) {
      messageApi.error('Vui lòng điền đầy đủ các trường bắt buộc có dấu (*) đỏ trên Form!');
      return; 
    }

    if (!values.description || !values.thumbnailUrl || !values.categoryId) {
      messageApi.warning('Để Xuất bản, bạn cần bổ sung: Danh mục, Link Ảnh đại diện và Mô tả chi tiết!');
      return;
    }
    if (curriculum.length === 0) {
      messageApi.warning('Để Xuất bản, khóa học phải có ít nhất 1 Module nội dung!');
      return;
    }
    const hasLesson = curriculum.some(mod => mod.lessons && mod.lessons.length > 0);
    if (!hasLesson) {
      messageApi.warning('Để Xuất bản, mỗi Module phải có ít nhất 1 Bài học bên trong!');
      return;
    }

    try {
      setSaving(true);
      const payload = { 
        title: values.title?.trim(),
        summary: values.summary?.trim() || null,
        description: values.description?.trim() || null,
        price: Number(values.price) || 0,
        level: values.level,
        categoryId: Number(values.categoryId),
        thumbnailUrl: values.thumbnailUrl?.trim(),
        status: 'PUBLISHED' 
      };
      
      await courseApi.update(course!.id, payload);
      messageApi.success('Tuyệt vời! Xuất bản khóa học thành công!');
      await loadCourseInfo();
    } catch (error: any) {
      messageApi.error(error.response?.data?.message || 'Backend từ chối Xuất bản do sai cấu trúc dữ liệu!');
    } finally {
      setSaving(false);
    }
  };

  const submitModule = async () => {
    try {
      const vals = await modForm.validateFields();
      if (moduleModal.data) {
        await courseApi.updateModule(moduleModal.data.id, vals);
        messageApi.success('Đã cập nhật Module');
      } else {
        await courseApi.createModule(course!.id, { ...vals, sortOrder: curriculum.length + 1 });
        messageApi.success('Đã thêm Module');
      }
      setModuleModal({ open: false });
      modForm.resetFields();
      await reloadCurriculum(course!.id); 
    } catch (err: any) {
      if (err.errorFields) return; 
      messageApi.error(err.response?.data?.message || 'Lỗi lưu Module');
    }
  };

  const deleteModule = async (moduleId: string) => {
    try {
      await courseApi.deleteModule(moduleId);
      messageApi.success('Đã xóa Module');
      await reloadCurriculum(course!.id);
    } catch (err: any) {
      messageApi.error(err.response?.data?.message || 'Lỗi xóa Module');
    }
  };

  const submitLesson = async () => {
    try {
      const vals = await lessForm.validateFields();
      const payload = { ...vals, lessonType: vals.lessonType || 'VIDEO', isPreview: vals.isPreview || false };
      if (lessonModal.data) {
        await courseApi.updateLesson(lessonModal.data.id, payload);
        messageApi.success('Đã cập nhật Bài học');
      } else {
        await courseApi.createLesson(lessonModal.moduleId!, { ...payload, sortOrder: 99 });
        messageApi.success('Đã thêm Bài học');
      }
      setLessonModal({ open: false });
      lessForm.resetFields();
      await reloadCurriculum(course!.id);
    } catch (err: any) {
      if (err.errorFields) return;
      messageApi.error(err.response?.data?.message || 'Lỗi lưu Bài học');
    }
  };

  const deleteLesson = async (lessonId: string) => {
    try {
      if (!lessonId) {
        messageApi.error('Lỗi Frontend: Bài học này không có ID do Backend trả về null!');
        return;
      }
      await courseApi.deleteLesson(lessonId);
      messageApi.success('Đã xóa Bài học');
      await reloadCurriculum(course!.id);
    } catch (err: any) {
      messageApi.error(err.response?.data?.message || 'Lỗi xóa Bài học');
    }
  };

  if (initialLoading) return <div className="flex h-screen items-center justify-center"><Spin size="large" /></div>;
  if (!course) return <Empty description="Không tìm thấy khóa học" className="mt-20" />;

  return (
    <div className="mx-auto max-w-5xl p-4 md:p-6 lg:p-8 relative">
      {contextHolder}
      
      {isRefreshing && (
        <div className="absolute inset-0 z-50 flex items-center justify-center bg-white/50 rounded-xl">
          <Spin size="large" />
        </div>
      )}

      <div className="mb-6 flex flex-col md:flex-row md:items-center justify-between gap-4 bg-white p-4 md:p-6 rounded-xl shadow-sm border border-gray-100">
        <div className="flex items-center gap-4">
          <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/course-management')} />
          <div>
            <Title level={4} className="m-0 text-gray-800">{course.title}</Title>
            <Space className="mt-1">
              <Tag color={course.status === 'PUBLISHED' ? 'success' : 'warning'}>{course.statusText || course.status}</Tag>
              <Text type="secondary" className="text-xs">ID: {course.id}</Text>
            </Space>
          </div>
        </div>
        <Space>
          <Button icon={<SaveOutlined />} onClick={handleSaveInfo} loading={saving}>Lưu nháp</Button>
          <Button type="primary" className="bg-blue-600" icon={<SendOutlined />} onClick={handlePublish} loading={saving}>Xuất bản</Button>
        </Space>
      </div>

      <Tabs 
        activeKey={activeTab}
        onChange={setActiveTab}
        className="bg-white p-6 rounded-xl shadow-sm border border-gray-100"
        items={[
          {
            key: 'info',
            label: 'Thông tin khóa học',
            children: (
              <Form form={form} layout="vertical" className="mt-4 max-w-3xl">
                <Form.Item name="title" label="Tiêu đề" rules={[{ required: true }]}>
                  <Input size="large" />
                </Form.Item>
                <Form.Item name="summary" label="Tóm tắt ngắn (Summary)" rules={[{ max: 500 }]}>
                  <Input.TextArea rows={2} />
                </Form.Item>
                <Form.Item name="description" label="Mô tả chi tiết">
                  <Input.TextArea rows={6} />
                </Form.Item>
                <Row gutter={16}>
                  <Col span={12}>
                    <Form.Item name="price" label="Giá bán (VNĐ)">
                      <InputNumber size="large" className="w-full" min={0} />
                    </Form.Item>
                  </Col>
                  <Col span={12}>
                    <Form.Item name="level" label="Cấp độ" rules={[{ required: true }]}>
                      <Select size="large">
                        <Select.Option value="BEGINNER">Cơ bản</Select.Option>
                        <Select.Option value="INTERMEDIATE">Trung cấp</Select.Option>
                        <Select.Option value="ADVANCED">Nâng cao</Select.Option>
                      </Select>
                    </Form.Item>
                  </Col>
                  <Col span={12}>
                    <Form.Item name="categoryId" label="Danh mục">
                      <Select size="large" options={categories} />
                    </Form.Item>
                  </Col>
                  <Col span={12}>
                    <Form.Item name="thumbnailUrl" label="Link Ảnh đại diện (URL)">
                      <Input size="large" placeholder="https://..." />
                    </Form.Item>
                  </Col>
                </Row>
              </Form>
            )
          },
          {
            key: 'curriculum',
            label: 'Chương trình học',
            children: (
              <div className="mt-4">
                <div className="mb-4 flex justify-between items-center">
                  <Text className="text-gray-500">Xây dựng cấu trúc bài giảng cho khóa học của bạn.</Text>
                  <Button type="dashed" icon={<PlusOutlined />} onClick={() => { modForm.resetFields(); setModuleModal({ open: true }); }}>Thêm Module</Button>
                </div>

                {curriculum.length === 0 ? (
                  <Empty description="Chưa có nội dung nào" className="my-10" />
                ) : (
                  <div className="flex flex-col gap-4">
                    {curriculum.map((mod) => (
                      <Card key={mod.id} size="small" className="border-gray-200 bg-gray-50" 
                        title={<span className="font-bold text-gray-700">{mod.title}</span>}
                        extra={
                          <Space>
                            <Button type="text" size="small" icon={<EditOutlined />} onClick={() => { modForm.setFieldsValue(mod); setModuleModal({ open: true, data: mod }); }} />
                            <Popconfirm title="Xóa Module này?" onConfirm={() => deleteModule(mod.id)}>
                              <Button type="text" danger size="small" icon={<DeleteOutlined />} />
                            </Popconfirm>
                          </Space>
                        }
                      >
                        {/* THAY THẾ COMPONENT <List> BỊ DEPRECATED BẰNG THẺ DIV THUẦN TÚY */}
                        <div className="bg-white rounded-md mb-3 border border-gray-100 flex flex-col">
                          {(!mod.lessons || mod.lessons.length === 0) ? (
                            <div className="p-3 text-center text-gray-400 text-sm">Chưa có bài học</div>
                          ) : (
                            mod.lessons.map((lesson, idx) => (
                              <div key={lesson.id || `temp-${idx}`} className="flex items-center justify-between p-3 border-b border-gray-100 last:border-b-0 hover:bg-gray-50 transition-colors">
                                <div className="flex items-center gap-2">
                                  <HolderOutlined className="text-gray-300 cursor-grab" />
                                  <Text>{lesson.title}</Text>
                                  <Tag className="text-[10px] ml-2 text-blue-600 bg-blue-50 border-blue-200">{lesson.lessonType}</Tag>
                                </div>
                                <Space>
                                  <Button type="text" size="small" icon={<EditOutlined />} onClick={() => { lessForm.setFieldsValue(lesson); setLessonModal({ open: true, moduleId: mod.id, data: lesson }); }} />
                                  <Popconfirm title="Xóa bài học?" onConfirm={() => deleteLesson(lesson.id)}>
                                    <Button type="text" danger size="small" icon={<DeleteOutlined />} />
                                  </Popconfirm>
                                </Space>
                              </div>
                            ))
                          )}
                        </div>

                        <Button type="dashed" block icon={<PlusOutlined />} onClick={() => { lessForm.resetFields(); setLessonModal({ open: true, moduleId: mod.id }); }}>
                          Thêm Bài học
                        </Button>
                      </Card>
                    ))}
                  </div>
                )}
              </div>
            )
          }
        ]}
      />

      <Modal title={moduleModal.data ? 'Sửa Module' : 'Thêm Module'} open={moduleModal.open} onCancel={() => setModuleModal({ open: false })} onOk={submitModule} destroyOnClose>
        <Form form={modForm} layout="vertical">
          <Form.Item name="title" label="Tiêu đề Module" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
        </Form>
      </Modal>

      <Modal title={lessonModal.data ? 'Sửa Bài học' : 'Thêm Bài học'} open={lessonModal.open} onCancel={() => setLessonModal({ open: false })} onOk={submitLesson} width={600} destroyOnClose>
        <Form form={lessForm} layout="vertical">
          <Form.Item name="title" label="Tiêu đề bài học" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item name="lessonType" label="Loại bài học" initialValue="VIDEO">
                <Select options={[{ label: 'Video', value: 'VIDEO' }, { label: 'Tài liệu (Text)', value: 'TEXT' }]} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="videoUrl" label="Video URL (Nếu có)">
                <Input placeholder="https://youtube.com/..." />
              </Form.Item>
            </Col>
          </Row>
          <Form.Item name="content" label="Nội dung Text / HTML">
            <Input.TextArea rows={4} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default CourseStudioPage;