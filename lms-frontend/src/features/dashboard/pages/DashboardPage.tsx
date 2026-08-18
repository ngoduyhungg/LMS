import React from 'react';
import { Typography, Row, Col, Card, Button, Empty } from 'antd';
import { 
  PlayCircleOutlined, SafetyCertificateOutlined, BookOutlined,
  TeamOutlined, ReadOutlined, SolutionOutlined, BarChartOutlined,
  EditOutlined, StarOutlined
} from '@ant-design/icons';
import { useAppSelector } from '@/app/redux/hooks';
import { USER_ROLE } from '@/features/users/types/user-role-type';
import { useNavigate } from 'react-router-dom';

const { Title, Text } = Typography;

const DashboardPage: React.FC = () => {
  const { user } = useAppSelector((state) => state.auth);
  const navigate = useNavigate();
  
  const isAdmin = user?.role === USER_ROLE.ADMIN;
  const isInstructor = user?.role === USER_ROLE.INSTRUCTOR;
  const isStudent = user?.role === USER_ROLE.STUDENT || (!isAdmin && !isInstructor);

  const getSubtitle = () => {
    if (isAdmin) return 'Tổng quan tình hình hoạt động của hệ thống LMS.';
    if (isInstructor) return 'Quản lý khóa học, lớp học và theo dõi tiến độ của học viên.';
    return 'Tiếp tục hành trình học tập của bạn trên hệ thống LMS.';
  };

  return (
    <div className="mx-auto max-w-7xl p-6">
      {/* Welcome Section */}
      <div className="mb-8">
        <Title level={2} className="mb-1 text-gray-800">
          Chào mừng trở lại, {user?.fullName || 'bạn'}!
        </Title>
        <Text className="text-base text-gray-500">
          {getSubtitle()}
        </Text>
      </div>

      {/* Quick Summary Cards */}
      <Row gutter={[24, 24]} className="mb-8">
        {/* ----- ADMIN SUMMARY ----- */}
        {isAdmin && (
          <>
            <Col xs={24} sm={8}>
              <Card className="border-0 bg-gradient-to-br from-blue-50 to-blue-100/50 shadow-sm rounded-xl">
                <div className="flex items-center gap-4">
                  <div className="flex h-12 w-12 items-center justify-center rounded-full bg-blue-600 text-xl text-white shadow-md">
                    <TeamOutlined />
                  </div>
                  <div>
                    <Text className="font-medium text-gray-500">Tổng người dùng</Text>
                    <div className="text-2xl font-bold text-gray-800">--</div>
                  </div>
                </div>
              </Card>
            </Col>
            <Col xs={24} sm={8}>
              <Card className="border-0 bg-gradient-to-br from-indigo-50 to-indigo-100/50 shadow-sm rounded-xl">
                <div className="flex items-center gap-4">
                  <div className="flex h-12 w-12 items-center justify-center rounded-full bg-indigo-600 text-xl text-white shadow-md">
                    <ReadOutlined />
                  </div>
                  <div>
                    <Text className="font-medium text-gray-500">Khóa học hệ thống</Text>
                    <div className="text-2xl font-bold text-gray-800">--</div>
                  </div>
                </div>
              </Card>
            </Col>
            <Col xs={24} sm={8}>
              <Card className="border-0 bg-gradient-to-br from-teal-50 to-teal-100/50 shadow-sm rounded-xl">
                <div className="flex items-center gap-4">
                  <div className="flex h-12 w-12 items-center justify-center rounded-full bg-teal-600 text-xl text-white shadow-md">
                    <SolutionOutlined />
                  </div>
                  <div>
                    <Text className="font-medium text-gray-500">Ghi danh mới</Text>
                    <div className="text-2xl font-bold text-gray-800">--</div>
                  </div>
                </div>
              </Card>
            </Col>
          </>
        )}

        {/* ----- INSTRUCTOR SUMMARY ----- */}
        {isInstructor && (
          <>
            <Col xs={24} sm={8}>
              <Card className="border-0 bg-gradient-to-br from-blue-50 to-blue-100/50 shadow-sm rounded-xl">
                <div className="flex items-center gap-4">
                  <div className="flex h-12 w-12 items-center justify-center rounded-full bg-blue-600 text-xl text-white shadow-md">
                    <BookOutlined />
                  </div>
                  <div>
                    <Text className="font-medium text-gray-500">Đang giảng dạy</Text>
                    <div className="text-2xl font-bold text-gray-800">--</div>
                  </div>
                </div>
              </Card>
            </Col>
            <Col xs={24} sm={8}>
              <Card className="border-0 bg-gradient-to-br from-orange-50 to-orange-100/50 shadow-sm rounded-xl">
                <div className="flex items-center gap-4">
                  <div className="flex h-12 w-12 items-center justify-center rounded-full bg-orange-500 text-xl text-white shadow-md">
                    <TeamOutlined />
                  </div>
                  <div>
                    <Text className="font-medium text-gray-500">Tổng học viên</Text>
                    <div className="text-2xl font-bold text-gray-800">--</div>
                  </div>
                </div>
              </Card>
            </Col>
            <Col xs={24} sm={8}>
              <Card className="border-0 bg-gradient-to-br from-yellow-50 to-yellow-100/50 shadow-sm rounded-xl">
                <div className="flex items-center gap-4">
                  <div className="flex h-12 w-12 items-center justify-center rounded-full bg-yellow-500 text-xl text-white shadow-md">
                    <StarOutlined />
                  </div>
                  <div>
                    <Text className="font-medium text-gray-500">Đánh giá trung bình</Text>
                    <div className="text-2xl font-bold text-gray-800">--</div>
                  </div>
                </div>
              </Card>
            </Col>
          </>
        )}

        {/* ----- STUDENT SUMMARY ----- */}
        {isStudent && (
          <>
            <Col xs={24} sm={8}>
              <Card className="border-0 bg-gradient-to-br from-blue-50 to-blue-100/50 shadow-sm rounded-xl">
                <div className="flex items-center gap-4">
                  <div className="flex h-12 w-12 items-center justify-center rounded-full bg-blue-600 text-xl text-white shadow-md">
                    <BookOutlined />
                  </div>
                  <div>
                    <Text className="font-medium text-gray-500">Khóa học của tôi</Text>
                    <div className="text-2xl font-bold text-gray-800">--</div>
                  </div>
                </div>
              </Card>
            </Col>
            <Col xs={24} sm={8}>
              <Card className="border-0 bg-gradient-to-br from-green-50 to-green-100/50 shadow-sm rounded-xl">
                <div className="flex items-center gap-4">
                  <div className="flex h-12 w-12 items-center justify-center rounded-full bg-green-600 text-xl text-white shadow-md">
                    <PlayCircleOutlined />
                  </div>
                  <div>
                    <Text className="font-medium text-gray-500">Đang học</Text>
                    <div className="text-2xl font-bold text-gray-800">--</div>
                  </div>
                </div>
              </Card>
            </Col>
            <Col xs={24} sm={8}>
              <Card className="border-0 bg-gradient-to-br from-purple-50 to-purple-100/50 shadow-sm rounded-xl">
                <div className="flex items-center gap-4">
                  <div className="flex h-12 w-12 items-center justify-center rounded-full bg-purple-600 text-xl text-white shadow-md">
                    <SafetyCertificateOutlined />
                  </div>
                  <div>
                    <Text className="font-medium text-gray-500">Chứng chỉ</Text>
                    <div className="text-2xl font-bold text-gray-800">--</div>
                  </div>
                </div>
              </Card>
            </Col>
          </>
        )}
      </Row>

      {/* Main Content Sections */}
      <Row gutter={[24, 24]}>
        {/* ----- ADMIN CONTENT ----- */}
        {isAdmin && (
          <>
            <Col xs={24} lg={16}>
              <Card 
                title={<span className="text-lg font-bold">Hoạt động hệ thống gần đây</span>} 
                className="h-full border-gray-100 shadow-sm rounded-xl"
                bodyStyle={{ padding: '2rem' }}
              >
                <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} description={<span className="text-gray-500">Chưa có dữ liệu hoạt động</span>} />
              </Card>
            </Col>
            <Col xs={24} lg={8}>
              <Card 
                title={<span className="text-lg font-bold">Phân tích ghi danh</span>} 
                className="h-full border-gray-100 shadow-sm rounded-xl"
                bodyStyle={{ padding: '2rem' }}
              >
                <Empty image={<BarChartOutlined className="text-4xl text-gray-300" />} description={<span className="text-gray-500">Đang chờ dữ liệu phân tích</span>} />
              </Card>
            </Col>
          </>
        )}

        {/* ----- INSTRUCTOR CONTENT ----- */}
        {isInstructor && (
          <>
            <Col xs={24} lg={16}>
              <Card 
                title={<span className="text-lg font-bold">Lớp học đang phụ trách</span>} 
                className="h-full border-gray-100 shadow-sm rounded-xl"
                bodyStyle={{ padding: '2rem' }}
              >
                <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} description={<span className="text-gray-500">Bạn chưa được phân công lớp học nào</span>}>
                  <Button type="primary" className="mt-2 bg-blue-600" onClick={() => navigate('/course-management')}>Quản lý khóa học</Button>
                </Empty>
              </Card>
            </Col>
            <Col xs={24} lg={8}>
              <Card 
                title={<span className="text-lg font-bold">Cần xử lý</span>} 
                className="h-full border-gray-100 shadow-sm rounded-xl"
                bodyStyle={{ padding: '2rem' }}
              >
                <Empty image={<EditOutlined className="text-4xl text-gray-300" />} description={<span className="text-gray-500">Không có bài tập cần chấm</span>} />
              </Card>
            </Col>
          </>
        )}

        {/* ----- STUDENT CONTENT ----- */}
        {isStudent && (
          <>
            <Col xs={24} lg={16}>
              <Card 
                title={<span className="text-lg font-bold">Khóa học đang học</span>} 
                className="h-full border-gray-100 shadow-sm rounded-xl"
                bodyStyle={{ padding: '2rem' }}
              >
                <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} description={<span className="text-gray-500">Bạn chưa ghi danh vào khóa học nào</span>}>
                  <Button type="primary" className="mt-2 bg-blue-600" onClick={() => navigate('/courses')}>Khám phá khóa học</Button>
                </Empty>
              </Card>
            </Col>
            <Col xs={24} lg={8}>
              <Card 
                title={<span className="text-lg font-bold">Chứng chỉ mới nhất</span>} 
                className="h-full border-gray-100 shadow-sm rounded-xl"
                bodyStyle={{ padding: '2rem' }}
              >
                <Empty image={<SafetyCertificateOutlined className="text-4xl text-gray-300" />} description={<span className="text-gray-500">Chưa có chứng chỉ nào</span>} />
              </Card>
            </Col>
          </>
        )}
      </Row>
    </div>
  );
};

export default DashboardPage;