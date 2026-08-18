import { useEffect, useState } from 'react';
import { Typography, Row, Col, Button, Empty, Skeleton, Pagination, Segmented } from 'antd';
import { PlusOutlined, ReadOutlined } from '@ant-design/icons';
import { useAppSelector } from '@/app/redux/hooks';
import { USER_ROLE } from '@/features/users/types/user-role-type';
import useTable from '@/shared/hooks/useTable';
import { courseRoleAdminApi, getCategories } from '../api/course-api';
import type { Course, Category } from '../types/course-type';
import type { CourseFilterParams } from '../types/course-filter-params-type';
import CourseCard from '../components/CourseCard';
import ModalFormCustom from '@/shared/components/modal/ModalFormCustom';
import { useFormModal } from '@/shared/hooks/useFormModal';
import { courseFormFields } from '../constants/course-form-fields';
import { FormModalMode } from '@/shared/types/form-modal-mode-type';
import type { SectionForm } from '@/shared/components/modal/ModalFormCustom';
import { useNavigate } from 'react-router-dom';

const { Title, Text } = Typography;

const CoursePage = () => {
  const { user } = useAppSelector((state) => state.auth);
  const navigate = useNavigate();
  const isAdmin = user?.role === USER_ROLE.ADMIN;
  const isInstructor = user?.role === USER_ROLE.INSTRUCTOR;
  const isAdminOrInstructor = isAdmin || isInstructor;

  const [categories, setCategories] = useState<{ label: string; value: string }[]>([]);
  const [selectedCategory, setSelectedCategory] = useState<string>('all');

  const { getAll, create, update, open: openCourse, close: closeCourse, remove } = courseRoleAdminApi;

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
        console.warn('Backend chưa hỗ trợ API Categories hoặc lỗi mạng:', error);
        setCategories([{ label: 'Tất cả', value: 'all' }]);
      }
    };
    fetchCategories();
  }, []);

  const { open, mode, selectedRecord, openCreate, openEdit, close } = useFormModal<Course>();

  const {
    data: courses,
    loading,
    pagination,
    handleChangePage,
    refetch,
  } = useTable<Course, CourseFilterParams>({
    fetchApi: getAll,
    removeApi: remove,
    activeApi: openCourse,
    inActiveApi: closeCourse,
  });

  const sectionsCourseForm: SectionForm[] = [
    { key: 'course', label: 'Thông tin khóa học', fields: courseFormFields },
  ];

  const handleCourseAction = (course: Course) => {
    if (isAdminOrInstructor) {
      openEdit(course);
    } else {
      navigate(`/courses/${course.id}`);
    }
  };

  const handleDetailClick = (course: Course) => {
    navigate(`/courses/${course.id}`);
  };

  return (
    <div className="mx-auto max-w-7xl p-4 md:p-6 lg:p-8">
      <div className="mb-6 flex flex-col gap-4 md:mb-8 md:flex-row md:items-center md:justify-between">
        <div>
          <Title level={2} className="mb-1 text-2xl font-bold text-gray-800 md:text-3xl">
            {isAdminOrInstructor ? 'Quản lý khóa học' : 'Khám phá khóa học'}
          </Title>
          <Text className="text-gray-500">
            {isAdminOrInstructor 
              ? 'Quản trị danh mục và thiết kế chương trình đào tạo.' 
              : 'Tìm kiếm, khám phá và tham gia các khóa học phù hợp với bạn.'}
          </Text>
        </div>
        
        {isAdmin && (
          <Button type="primary" className="bg-blue-600 font-medium" size="large" icon={<PlusOutlined />} onClick={openCreate}>
            Tạo khóa học
          </Button>
        )}
      </div>

      <div className="mb-8 overflow-x-auto pb-2">
        {categories.length > 0 ? (
          <Segmented
            options={categories}
            value={selectedCategory}
            onChange={(val) => setSelectedCategory(val as string)}
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
          <Empty
            image={<ReadOutlined className="text-6xl text-gray-300" />}
            description={<span className="text-lg font-medium text-gray-500">Chưa có khóa học nào</span>}
          />
        </div>
      )}

      {isAdminOrInstructor && (
        <ModalFormCustom<Course>
          open={open}
          title="Khóa học"
          mode={mode}
          initialValues={selectedRecord}
          disabled={mode === FormModalMode.VIEW}
          onCancel={close}
          onSuccess={refetch}
          onSubmit={mode === FormModalMode.CREATE ? create : (values) => update(selectedRecord!.id, values)}
          sections={sectionsCourseForm}
        />
      )}
    </div>
  );
};

export default CoursePage;