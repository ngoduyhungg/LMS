import { useEffect, useState } from 'react';
import { Typography, Row, Col, Button, Empty, Skeleton, Pagination, Segmented, Modal, Form, Input, Select, InputNumber, message } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { useAppSelector } from '@/app/redux/hooks';
import { USER_ROLE } from '@/features/users/types/user-role-type';
import useTable from '@/shared/hooks/useTable';
import { courseApi, getCategories } from '../api/course-api';
import { type Course, type Category, CourseLevel } from '../types/course-type';
import type { CourseFilterParams } from '../types/course-filter-params-type';
import CourseCard from '../components/CourseCard';
import { useNavigate } from 'react-router-dom';

const { Title, Text } = Typography;

const CoursePage = () => {
  const { user } = useAppSelector((state) => state.auth);
  const navigate = useNavigate();
  const isAdmin = user?.role === USER_ROLE.ADMIN;
  const isInstructor = user?.role === USER_ROLE.INSTRUCTOR;
  const isAdminOrInstructor = isAdmin || isInstructor;

  // Sử dụng Hook messageApi để triệt tiêu lỗi "Static function can not consume context"
  const [messageApi, contextHolder] = message.useMessage();

  const [categories, setCategories] = useState<{ label: string; value: string }[]>([]);
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [createForm] = Form.useForm();
  const [isCreating, setIsCreating] = useState(false);

  const { getAll, remove } = courseApi;

  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const res = await getCategories();
        const catData = res.data || res.items || res;
        if (Array.isArray(catData)) {
          setCategories([
            { label: 'Tất cả', value: 'all' }, 
            ...catData.map((c: Category) => ({ label: c.name, value: c.id }))
          ]);
        }
      } catch (error) {
        setCategories([{ label: 'Tất cả', value: 'all' }]);
      }
    };
    fetchCategories();
  }, []);

  const { data: courses, loading, pagination, params, handleChangePage, setParams } = useTable<Course, CourseFilterParams>({
    fetchApi: getAll,
    removeApi: remove,
  });

  const handleCourseAction = (course: Course) => {
    const identifier = course.slug || course.id;
    if (isAdminOrInstructor) {
      navigate(`/course-management/${identifier}/edit`);
    } else {
      navigate(`/courses/${identifier}`);
    }
  };

  const handleDetailClick = (course: Course) => {
    const identifier = course.slug || course.id;
    navigate(`/courses/${identifier}`);
  };

  const handleCreateCourse = async () => {
    try {
      const values = await createForm.validateFields();
      setIsCreating(true);
      const payload = { ...values, status: 'DRAFT', price: values.price || 0 }; 
      const res = await courseApi.create(payload);
      
      messageApi.success('Khởi tạo khóa học thành công!');
      setIsCreateModalOpen(false);
      createForm.resetFields();
      
      const courseData = res.data || res;
      const newCourseIdentifier = courseData.slug || courseData.id;
      if (newCourseIdentifier) navigate(`/course-management/${newCourseIdentifier}/edit`);
    } catch (error: any) {
      messageApi.error(error.response?.data?.message || 'Lỗi khi tạo khóa học');
    } finally {
      setIsCreating(false);
    }
  };

  return (
    <div className="mx-auto max-w-7xl p-4 md:p-6 lg:p-8">
      {contextHolder}
      <div className="mb-6 flex flex-col gap-4 md:mb-8 md:flex-row md:items-center md:justify-between">
        <div>
          <Title level={2} className="mb-1 text-2xl font-bold text-gray-800 md:text-3xl">
            {isAdminOrInstructor ? 'Quản lý khóa học' : 'Khám phá khóa học'}
          </Title>
          <Text className="text-gray-500">
            {isAdminOrInstructor ? 'Quản trị danh mục và thiết kế chương trình đào tạo.' : 'Tìm kiếm, khám phá và tham gia các khóa học phù hợp với bạn.'}
          </Text>
        </div>
        {isAdminOrInstructor && (
          <Button type="primary" className="bg-blue-600 font-medium" size="large" icon={<PlusOutlined />} onClick={() => setIsCreateModalOpen(true)}>
            Tạo khóa học
          </Button>
        )}
      </div>

      <div className="mb-8 overflow-x-auto pb-2">
        {categories.length > 0 ? (
          <Segmented
            options={categories}
            value={params.categoryId || 'all'}
            onChange={(val) => {
              const categoryId = val === 'all' ? undefined : String(val);
              setParams((prev) => ({ ...prev, categoryId, page: 1 }));
            }}
            size="large"
            className="rounded-lg shadow-sm"
          />
        ) : (
          <Skeleton.Button active size="large" className="w-48" />
        )}
      </div>

      {loading ? (
        <Row gutter={[24, 24]}>
          {[1, 2, 3, 4].map((skeletonKey) => (
            <Col xs={24} sm={12} lg={8} xl={6} key={`skeleton-${skeletonKey}`}>
              <div className="h-96 overflow-hidden rounded-xl border border-gray-100 bg-white p-4 shadow-sm">
                <Skeleton.Image active className="!h-40 !w-full mb-4 rounded-lg" />
                <Skeleton active paragraph={{ rows: 3 }} />
              </div>
            </Col>
          ))}
        </Row>
      ) : courses && courses.length > 0 ? (
        <>
          <Row gutter={[24, 24]}>
            {courses.map((course) => (
              <Col xs={24} sm={12} lg={8} xl={6} key={course.id}>
                <CourseCard course={course} userRole={user?.role} onActionClick={handleCourseAction} onDetailClick={handleDetailClick} />
              </Col>
            ))}
          </Row>
          <div className="mt-10 flex justify-end">
            <Pagination
              current={pagination.page}
              pageSize={pagination.limit}
              total={pagination.total}
              onChange={(page, pageSize) => handleChangePage(page, pageSize)}
              showSizeChanger
              className="font-medium"
            />
          </div>
        </>
      ) : (
        <div className="flex flex-col items-center justify-center rounded-2xl border border-dashed border-gray-200 bg-gray-50 py-20">
          <Empty description={<span className="text-lg font-medium text-gray-500">Chưa có khóa học nào</span>} />
        </div>
      )}

      <Modal forceRender title={<span className="text-xl font-bold">Tạo khóa học</span>} open={isCreateModalOpen} onCancel={() => !isCreating && setIsCreateModalOpen(false)} onOk={handleCreateCourse} okText="Lưu nháp" cancelText="Hủy" confirmLoading={isCreating} width={700}>
        <Form form={createForm} layout="vertical" className="mt-6">
          <Form.Item name="thumbnailUrl" label={<span className="font-medium">Banner khóa học (URL)</span>}>
            <Input placeholder="https://example.com/banner.jpg" size="large" />
          </Form.Item>
          <Row gutter={16}>
            <Col span={24}>
              <Form.Item name="title" label={<span className="font-medium">Tên khóa học</span>} rules={[{ required: true, message: 'Vui lòng nhập tên khóa học' }]}>
                <Input placeholder="Nhập tên khóa học..." size="large" />
              </Form.Item>
            </Col>
            <Col xs={24} md={12}>
              <Form.Item name="categoryId" label={<span className="font-medium">Danh mục</span>}>
                <Select size="large" placeholder="Chọn danh mục" options={categories.filter(c => c.value !== 'all')} />
              </Form.Item>
            </Col>
            <Col xs={24} md={12}>
              <Form.Item name="level" label={<span className="font-medium">Cấp độ</span>} rules={[{ required: true, message: 'Vui lòng chọn cấp độ' }]} initialValue={CourseLevel.BEGINNER}>
                <Select size="large">
                  <Select.Option value={CourseLevel.BEGINNER}>Người mới bắt đầu</Select.Option>
                  <Select.Option value={CourseLevel.INTERMEDIATE}>Trung cấp</Select.Option>
                  <Select.Option value={CourseLevel.ADVANCED}>Nâng cao</Select.Option>
                </Select>
              </Form.Item>
            </Col>
            <Col span={24}>
              <Form.Item name="price" label={<span className="font-medium">Giá khóa học (VNĐ)</span>}>
                <InputNumber className="w-full" size="large" min={0} placeholder="0" />
              </Form.Item>
            </Col>
            <Col span={24}>
              <Form.Item name="summary" label={<span className="font-medium">Tóm tắt</span>}>
                <Input.TextArea rows={2} placeholder="Nhập tóm tắt ngắn gọn..." />
              </Form.Item>
            </Col>
            <Col span={24}>
              <Form.Item name="description" label={<span className="font-medium">Mô tả chi tiết</span>}>
                <Input.TextArea rows={4} placeholder="Nhập mô tả chi tiết nội dung khóa học..." />
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </Modal>
    </div>
  );
};

export default CoursePage;