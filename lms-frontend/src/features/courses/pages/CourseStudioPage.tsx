import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Tabs, Button, Card, Form, Input, Select, InputNumber, message, Spin, Typography, Space, Popconfirm, Modal, Tag, Row, Col, Empty, Upload, Tooltip } from 'antd';
import { ArrowLeftOutlined, SaveOutlined, SendOutlined, PlusOutlined, DeleteOutlined, EditOutlined, HolderOutlined, UploadOutlined, FileImageOutlined } from '@ant-design/icons';
import { courseApi, getCategories } from '../api/course-api';
// HOTFIX: Nhập API Mẫu chứng chỉ vừa tạo
import { instructorCertificateApi } from '../../certificates/api/certificate-api'; 
import type { Course, Module, Lesson, ApiError, Category } from '../types/course-type';
import type { UploadFile } from 'antd/es/upload/interface';

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

  // STATE CHO MẪU CHỨNG CHỈ (UI-6)
  const [templateLoading, setTemplateLoading] = useState(false);
  const [templateData, setTemplateData] = useState<{ id: string, fileUrl: string, createdAt: string } | null>(null);
  const [uploadFileList, setUploadFileList] = useState<UploadFile[]>([]);
  // DTO Admin/Instructor Course Summary có thể trả về completedCount. Giả lập nếu không có sẵn, mặc định là 0.
  const [completedCount, setCompletedCount] = useState<number>(0); 

  const loadCourseInfo = async () => {
    if (!routeParam) return null;
    const courseRes = await courseApi.getDetail(routeParam);
    const courseData = courseRes.data || courseRes;
    setCourse(courseData);
    
    // Nếu API CourseDetail có trả về số lượng hoàn thành, ta lấy ra để xử lý vô hiệu hóa sớm nút Xóa
    setCompletedCount(courseData.completedCount || 0);

    return courseData.id; 
  };

  const loadCertificateTemplate = async (actualCourseId: string) => {
    setTemplateLoading(true);
    try {
      const res = await instructorCertificateApi.getTemplate(actualCourseId);
      setTemplateData(res);
    } catch (error: any) {
      const status = error.response?.status;
      const errorCode = error.response?.data?.code;
      
      if (status !== 404 && errorCode !== 'CERTIFICATE_NOT_FOUND') {
        messageApi.error('Lỗi khi tải mẫu chứng chỉ.');
      }
      setTemplateData(null);
    } finally {
      setTemplateLoading(false);
    }
  };

  const reloadCurriculum = async (actualCourseId: string) => {
    setIsRefreshing(true);
    try {
      const currRes = await courseApi.getCurriculum(actualCourseId);
      const modulesList = currRes.data?.modules || currRes.data || currRes || [];
      setCurriculum(Array.isArray(modulesList) ? modulesList : []);
    } catch (e: unknown) {
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
            setCategories(catData.map((c: Category) => ({ label: c.name, value: String(c.id) }))); 
          }
          await reloadCurriculum(actualId); 
          await loadCertificateTemplate(actualId);
        }
      } catch (error: unknown) {
        const apiError = error as ApiError;
        messageApi.error(apiError.response?.data?.message || 'Lỗi tải dữ liệu Course Studio');
        navigate('/course-management');
      } finally {
        setInitialLoading(false);
      }
    };
    initPage();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [routeParam]);

  useEffect(() => {
    if (course && !initialLoading) {
      const catId = course.category?.id || course.categoryId;
      form.setFieldsValue({
        title: course.title,
        summary: course.summary,
        description: course.description,
        price: course.price || 0,
        level: course.level ? String(course.level).toUpperCase() : undefined, 
        categoryId: catId ? String(catId) : undefined, 
        thumbnailUrl: course.thumbnailUrl
      });
    }
  }, [course, initialLoading, form]);

  const handleSaveInfo = async () => {
    let values;
    try {
      values = await form.validateFields(); 
    } catch (e: unknown) {
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
      messageApi.destroy();
      messageApi.success('Đã lưu thông tin khóa học');
      await loadCourseInfo(); 
    } catch (error: unknown) {
      const apiError = error as ApiError;
      messageApi.destroy();
      messageApi.error(apiError.response?.data?.message || 'Lỗi API khi lưu thông tin');
    } finally {
      setSaving(false);
    }
  };

  const handlePublish = async () => {
    let values;
    try {
      values = await form.validateFields();
    } catch (e: unknown) {
      messageApi.destroy();
      messageApi.error('Vui lòng điền đầy đủ các trường bắt buộc có dấu (*) đỏ trên Form!');
      return; 
    }

    messageApi.destroy();

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
    } catch (error: unknown) {
      const apiError = error as ApiError;
      messageApi.error(apiError.response?.data?.message || 'Backend từ chối Xuất bản do sai cấu trúc dữ liệu!');
    } finally {
      setSaving(false);
    }
  };

  const submitModule = async () => {
    try {
      const vals = await modForm.validateFields();
      if (moduleModal.data) {
        await courseApi.updateModule(moduleModal.data.id, vals);
        messageApi.destroy();
        messageApi.success('Đã cập nhật Module');
      } else {
        await courseApi.createModule(course!.id, { ...vals, sortOrder: curriculum.length + 1 });
        messageApi.destroy();
        messageApi.success('Đã thêm Module');
      }
      setModuleModal({ open: false });
      modForm.resetFields();
      await reloadCurriculum(course!.id); 
    } catch (err: unknown) {
      const apiError = err as ApiError;
      messageApi.destroy();
      messageApi.error(apiError.response?.data?.message || 'Lỗi lưu Module');
    }
  };

  const deleteModule = async (moduleId: string) => {
    try {
      await courseApi.deleteModule(moduleId);
      messageApi.destroy();
      messageApi.success('Đã xóa Module');
      await reloadCurriculum(course!.id);
    } catch (err: unknown) {
      const apiError = err as ApiError;
      messageApi.destroy();
      messageApi.error(apiError.response?.data?.message || 'Lỗi xóa Module');
    }
  };

  const submitLesson = async () => {
    try {
      const vals = await lessForm.validateFields();
      const payload = { ...vals, lessonType: vals.lessonType || 'VIDEO', isPreview: vals.isPreview || false };
      if (lessonModal.data) {
        await courseApi.updateLesson(lessonModal.data.id, payload);
        messageApi.destroy();
        messageApi.success('Đã cập nhật Bài học');
      } else {
        await courseApi.createLesson(lessonModal.moduleId!, { ...payload, sortOrder: 99 });
        messageApi.destroy();
        messageApi.success('Đã thêm Bài học');
      }
      setLessonModal({ open: false });
      lessForm.resetFields();
      await reloadCurriculum(course!.id);
    } catch (err: unknown) {
      const apiError = err as ApiError;
      messageApi.destroy();
      messageApi.error(apiError.response?.data?.message || 'Lỗi lưu Bài học');
    }
  };

  const deleteLesson = async (lessonId: string) => {
    try {
      await courseApi.deleteLesson(lessonId);
      messageApi.destroy();
      messageApi.success('Đã xóa Bài học');
      await reloadCurriculum(course!.id);
    } catch (err: unknown) {
      const apiError = err as ApiError;
      messageApi.destroy();
      messageApi.error(apiError.response?.data?.message || 'Lỗi xóa Bài học');
    }
  };

  // ===============================================
  // UI-6: XỬ LÝ TEMPLATE CHỨNG CHỈ (UPLOAD & DELETE)
  // ===============================================
  const handleUploadTemplate = async () => {
    if (!uploadFileList[0] || !course) return;
    try {
      setTemplateLoading(true);
      const fileToUpload = uploadFileList[0].originFileObj as File;
      await instructorCertificateApi.uploadTemplate(course.id, fileToUpload);
      messageApi.success('Đã tải lên Mẫu chứng chỉ thành công!');
      setUploadFileList([]);
      await loadCertificateTemplate(course.id);
    } catch (error: any) {
      messageApi.error(error.response?.data?.message || 'Lỗi khi tải mẫu chứng chỉ.');
    } finally {
      setTemplateLoading(false);
    }
  };

  const handleDeleteTemplate = async () => {
    if (!course) return;
    try {
      setTemplateLoading(true);
      await instructorCertificateApi.deleteTemplate(course.id);
      messageApi.success('Đã xóa Mẫu chứng chỉ.');
      setTemplateData(null);
    } catch (error: any) {
      messageApi.destroy();
      // LOGIC XỬ LÝ THEO UPDATE NGHIỆP VỤ CỦA BACKEND & PO
      if (error.response?.data?.errorCode === 'COURSE_INVALID_STATUS' || error.response?.data?.code === 'COURSE_INVALID_STATUS') {
        messageApi.warning(
          'Không thể xóa Mẫu chứng chỉ vì đã có học viên được cấp bằng. Bạn chỉ có thể Cập nhật mẫu mới.'
        );
      } else {
        messageApi.error(error.response?.data?.message || 'Lỗi hệ thống khi xóa mẫu chứng chỉ.');
      }
    } finally {
      setTemplateLoading(false);
    }
  };

  if (initialLoading) return <div className="flex h-screen items-center justify-center"><Spin size="large" /></div>;
  if (!course) return <Empty description="Không tìm thấy khóa học" className="mt-20" />;

  const isPublished = course.status === 'PUBLISHED';

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
              <Tag color={isPublished ? 'success' : 'warning'}>{course.statusText || course.status}</Tag>
              <Text type="secondary" className="text-xs">ID: {course.id}</Text>
            </Space>
          </div>
        </div>
        <Space>
          <Button icon={<SaveOutlined />} onClick={handleSaveInfo} loading={saving}>
            {isPublished ? 'Lưu thay đổi' : 'Lưu nháp'}
          </Button>
          <Button 
            type="primary" 
            className={isPublished ? "bg-gray-400" : "bg-blue-600"} 
            icon={<SendOutlined />} 
            onClick={handlePublish} 
            loading={saving}
            disabled={isPublished}
          >
            {isPublished ? 'Đã xuất bản' : 'Xuất bản'}
          </Button>
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
                    <Form.Item name="level" label="Cấp độ" rules={[{ required: true, message: 'Vui lòng chọn cấp độ' }]}>
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
          },
          // ==================================
          // TAB MỚI: MẪU CHỨNG CHỈ (UI-6)
          // ==================================
          {
            key: 'certificate',
            label: 'Mẫu chứng chỉ',
            children: (
              <div className="mt-4 flex flex-col items-start gap-6 relative">
                {templateLoading && (
                  <div className="absolute inset-0 bg-white/70 z-10 flex items-center justify-center rounded-lg">
                    <Spin />
                  </div>
                )}
                
                <div className="flex flex-col">
                  <Title level={5}>Cấu hình Chứng chỉ Khóa học</Title>
                  <Text type="secondary">Tải lên hình ảnh mẫu (JPG/PNG) làm phôi chứng chỉ. Hệ thống sẽ tự động in tên học viên vào giữa ảnh này.</Text>
                </div>

                {templateData ? (
                  <div className="w-full p-4 border border-blue-100 bg-blue-50/30 rounded-xl flex flex-col md:flex-row gap-6 items-start">
                    <div className="w-full max-w-sm rounded-lg overflow-hidden border border-gray-200 shadow-sm bg-white">
                      <img src={templateData.fileUrl} alt="Certificate Template" className="w-full h-auto object-cover" />
                    </div>
                    <div className="flex flex-col items-start gap-3 flex-1">
                      <Tag color="blue" icon={<FileImageOutlined />}>Đã cấu hình mẫu chứng chỉ</Tag>
                      <Text type="secondary" className="text-xs">
                        Ngày tạo: {new Date(templateData.createdAt).toLocaleString('vi-VN')}
                      </Text>
                      
                      <div className="mt-4 flex gap-3">
                        <Upload 
                          beforeUpload={() => false} // Không upload tự động
                          maxCount={1}
                          accept="image/png, image/jpeg"
                          fileList={uploadFileList}
                          onChange={(info) => setUploadFileList(info.fileList)}
                          showUploadList={false}
                        >
                          <Button icon={<UploadOutlined />}>Đổi mẫu khác</Button>
                        </Upload>
                        
                        {/* Logic UX Fail-fast: Disable sẵn nút nếu đã có người học xong */}
                        <Tooltip title={completedCount > 0 ? "Không thể xóa vì đã có người được cấp chứng chỉ này" : ""}>
                          <Popconfirm
                            title="Xóa mẫu chứng chỉ?"
                            description="Bạn có chắc muốn xóa phôi chứng chỉ này không?"
                            onConfirm={handleDeleteTemplate}
                            okText="Đồng ý Xóa"
                            cancelText="Đóng"
                            okButtonProps={{ danger: true }}
                            disabled={completedCount > 0}
                          >
                            <Button danger icon={<DeleteOutlined />} disabled={completedCount > 0}>
                              Xóa mẫu
                            </Button>
                          </Popconfirm>
                        </Tooltip>
                      </div>

                      {uploadFileList.length > 0 && (
                        <div className="mt-2 flex items-center gap-2 p-2 bg-yellow-50 border border-yellow-200 rounded">
                          <Text className="text-sm">Đã chọn ảnh mới: {uploadFileList[0].name}</Text>
                          <Button type="primary" size="small" onClick={handleUploadTemplate}>Lưu ảnh mới</Button>
                        </div>
                      )}
                    </div>
                  </div>
                ) : (
                  <div className="w-full p-8 border-2 border-dashed border-gray-200 rounded-xl flex flex-col items-center justify-center bg-gray-50 text-center">
                    <FileImageOutlined className="text-4xl text-gray-300 mb-3" />
                    <Title level={5} className="text-gray-600 m-0">Chưa có mẫu chứng chỉ</Title>
                    <Text type="secondary" className="mb-4">Học viên sẽ không nhận được chứng chỉ sau khi hoàn thành khóa học này.</Text>
                    
                    <Upload 
                      beforeUpload={() => false}
                      maxCount={1}
                      accept="image/png, image/jpeg"
                      fileList={uploadFileList}
                      onChange={(info) => setUploadFileList(info.fileList)}
                      showUploadList={false}
                    >
                      <Button type="primary" icon={<UploadOutlined />}>Tải ảnh mẫu lên (JPG/PNG)</Button>
                    </Upload>
                    
                    {uploadFileList.length > 0 && (
                      <div className="mt-4 flex items-center gap-2">
                        <Text strong>{uploadFileList[0].name}</Text>
                        <Button type="primary" onClick={handleUploadTemplate}>Xác nhận tải lên</Button>
                      </div>
                    )}
                  </div>
                )}
              </div>
            )
          }
        ]}
      />

      <Modal title={moduleModal.data ? 'Sửa Module' : 'Thêm Module'} open={moduleModal.open} onCancel={() => setModuleModal({ open: false })} onOk={submitModule} destroyOnHidden>
        <Form form={modForm} layout="vertical">
          <Form.Item name="title" label="Tiêu đề Module" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
        </Form>
      </Modal>

      <Modal title={lessonModal.data ? 'Sửa Bài học' : 'Thêm Bài học'} open={lessonModal.open} onCancel={() => setLessonModal({ open: false })} onOk={submitLesson} width={600} destroyOnHidden>
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