import React, { useEffect, useState } from 'react';
import { Typography, Table, Tag, Button, message } from 'antd';
import { StopOutlined, EyeOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { adminEnrollmentApi } from '../api/enrollment-api';
import type { CourseEnrollmentSummary } from '../types/enrollment-type';
import { useAppSelector } from '@/app/redux/hooks';
import { USER_ROLE } from '@/features/users/types/user-role-type';

const { Title, Text } = Typography;

const EnrollmentPage: React.FC = () => {
  const { user } = useAppSelector((state) => state.auth);
  const navigate = useNavigate();
  const [summaries, setSummaries] = useState<CourseEnrollmentSummary[]>([]);
  const [loading, setLoading] = useState(true);
  const [messageApi, contextHolder] = message.useMessage();

  const isAdmin = user?.role === USER_ROLE.ADMIN;

  const fetchSummaries = async () => {
    try {
      setLoading(true);
      // Gọi API LEVEL 1: Lấy danh sách thống kê
      const data = await adminEnrollmentApi.getCourseSummaries();
      setSummaries(Array.isArray(data) ? data : []);
    } catch (error: any) {
      if (error.response?.status === 403) {
        messageApi.error('Bạn không có quyền quản lý ghi danh.');
      } else {
        messageApi.error(error.response?.data?.message || 'Lỗi khi tải thống kê ghi danh.');
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (isAdmin) {
      fetchSummaries();
    } else {
      setLoading(false);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isAdmin]);

  if (!loading && !isAdmin) {
    return (
      <div className="flex flex-col items-center justify-center p-20 text-center bg-gray-50 rounded-2xl border border-dashed border-gray-200 m-8">
        <StopOutlined className="text-4xl text-red-500 mb-4" />
        <Title level={3} className="text-gray-800 m-0">Truy cập bị từ chối</Title>
        <Text className="text-gray-500 mt-2">Trang này chỉ dành cho Ban Quản Trị (ADMIN).</Text>
      </div>
    );
  }

  const columns = [
    {
      title: 'Mã KH',
      dataIndex: 'courseId',
      key: 'courseId',
      width: 100,
      render: (id: string | number) => <Text strong>#{id}</Text>
    },
    {
      title: 'Tên Khóa học',
      dataIndex: 'courseTitle',
      key: 'courseTitle',
      render: (title: string) => <Text className="font-medium text-gray-800">{title}</Text>
    },
    {
      title: 'Trạng thái KH',
      dataIndex: 'courseStatus',
      key: 'courseStatus',
      width: 150,
      render: (status: string) => (
        <Tag color={status === 'PUBLISHED' ? 'success' : 'default'} className="border-0">
          {status}
        </Tag>
      )
    },
    {
      title: 'Tổng Ghi danh',
      dataIndex: 'enrollmentCount',
      key: 'enrollmentCount',
      align: 'center' as const,
      width: 150,
      render: (count: number) => <Tag color="blue" className="text-sm px-3 py-1 font-bold rounded-full">{count}</Tag>
    },
    {
      title: 'Đang học',
      dataIndex: 'activeCount',
      key: 'activeCount',
      align: 'center' as const,
      width: 120,
      render: (count: number) => <Text className="text-blue-600 font-medium">{count}</Text>
    },
    {
      title: 'Đã hoàn thành',
      dataIndex: 'completedCount',
      key: 'completedCount',
      align: 'center' as const,
      width: 150,
      render: (count: number) => <Text className="text-green-600 font-medium">{count}</Text>
    },
    {
      title: 'Thao tác',
      key: 'action',
      width: 150,
      align: 'center' as const,
      render: (_: any, record: CourseEnrollmentSummary) => (
        <Button 
          type="primary" 
          icon={<EyeOutlined />} 
          onClick={() => navigate(`/enrollments/courses/${record.courseId}`)}
          className="bg-gray-800 hover:bg-gray-700"
        >
          Xem Học viên
        </Button>
      ),
    },
  ];

  return (
    <div className="mx-auto max-w-7xl p-4 md:p-6 lg:p-8">
      {contextHolder}
      <div className="mb-6 flex flex-col gap-2">
        <Title level={2} className="mb-0 text-2xl font-bold text-gray-800">Quản lý Ghi danh (Tổng quan)</Title>
        <Text className="text-gray-500">Thống kê số lượng học viên ghi danh theo từng khóa học.</Text>
      </div>

      <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
        <Table
          dataSource={summaries}
          columns={columns}
          rowKey="courseId"
          loading={loading}
          pagination={{ pageSize: 10 }}
          scroll={{ x: 1000 }}
          locale={{ emptyText: 'Chưa có dữ liệu thống kê ghi danh.' }}
        />
      </div>
    </div>
  );
};

export default EnrollmentPage;