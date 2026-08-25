import React, { useEffect, useState } from 'react';
import { Typography, Table, Tag, Progress, Button, Popconfirm, message } from 'antd';
import { StopOutlined } from '@ant-design/icons';
import { adminEnrollmentApi } from '../api/enrollment-api';
import type { EnrollmentResponse } from '../types/enrollment-type';
import { useAppSelector } from '@/app/redux/hooks';
import { USER_ROLE } from '@/features/users/types/user-role-type';

const { Title, Text } = Typography;

const EnrollmentPage: React.FC = () => {
  const { user } = useAppSelector((state) => state.auth);
  const [enrollments, setEnrollments] = useState<EnrollmentResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [messageApi, contextHolder] = message.useMessage();

  const isAdmin = user?.role === USER_ROLE.ADMIN;

  const fetchEnrollments = async () => {
    try {
      setLoading(true);
      const data = await adminEnrollmentApi.getAll();
      setEnrollments(Array.isArray(data) ? data : []);
    } catch (error: any) {
      if (error.response?.status === 403) {
        messageApi.error('Bạn không có quyền quản lý ghi danh.');
      } else {
        messageApi.error(error.response?.data?.message || 'Lỗi khi tải danh sách ghi danh.');
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (isAdmin) {
      fetchEnrollments();
    } else {
      setLoading(false);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isAdmin]);

  const handleCancel = async (id: string | number) => {
    try {
      const updatedEnrollment = await adminEnrollmentApi.cancel(id);
      messageApi.success(`Đã hủy ghi danh #${id} thành công.`);
      // Phản ánh dữ liệu từ Backend xuống UI thay vì fetch lại toàn bộ
      setEnrollments((prev) => 
        prev.map((enr) => (enr.id === id ? updatedEnrollment : enr))
      );
    } catch (error: any) {
      messageApi.error(error.response?.data?.message || 'Lỗi khi hủy ghi danh.');
    }
  };

  // Route Guard ngay tại Component
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
      title: 'Mã GD (ID)',
      dataIndex: 'id',
      key: 'id',
      width: 100,
      render: (id: string | number) => <Text strong>#{id}</Text>
    },
    {
      title: 'Course ID',
      dataIndex: 'courseId',
      key: 'courseId',
      width: 120,
      render: (cId: string | number) => <Text type="secondary">Course: {cId}</Text>
    },
    {
      title: 'Trạng thái',
      dataIndex: 'status',
      key: 'status',
      width: 140,
      render: (status: string) => {
        let color = 'default';
        let label = status;
        switch (status) {
          case 'ACTIVE': color = 'processing'; label = 'Đang học'; break;
          case 'COMPLETED': color = 'success'; label = 'Hoàn thành'; break;
          case 'CANCELLED': color = 'error'; label = 'Đã hủy'; break;
          case 'EXPIRED': color = 'warning'; label = 'Hết hạn'; break;
        }
        return <Tag color={color} className="m-0 font-medium border-0">{label}</Tag>;
      },
    },
    {
      title: 'Tiến độ học tập',
      dataIndex: 'progressPercentage',
      key: 'progressPercentage',
      width: 250,
      render: (progress: number, record: EnrollmentResponse) => (
        <div className="flex flex-col">
          <Progress percent={progress} size="small" status={progress === 100 ? 'success' : 'active'} className="m-0" />
          <Text type="secondary" className="text-[11px] mt-1">
            {record.lastAccessedLessonId ? `Lesson truy cập cuối: ${record.lastAccessedLessonId}` : 'Chưa có hoạt động'}
          </Text>
        </div>
      ),
    },
    {
      title: 'Ngày ghi danh',
      dataIndex: 'enrolledAt',
      key: 'enrolledAt',
      render: (date: string) => date ? new Date(date).toLocaleString('vi-VN') : '-',
    },
    {
      title: 'Ngày hoàn thành',
      dataIndex: 'completedAt',
      key: 'completedAt',
      render: (date: string) => date ? <Text className="text-green-600 font-medium">{new Date(date).toLocaleString('vi-VN')}</Text> : '-',
    },
    {
      title: 'Thao tác',
      key: 'action',
      width: 120,
      render: (_: any, record: EnrollmentResponse) => {
        const isCancelled = record.status === 'CANCELLED';
        return (
          <Popconfirm
            title="Xác nhận Hủy ghi danh?"
            description={`Học viên sẽ bị ngừng Khóa học #${record.courseId}. Bạn có chắc chắn?`}
            onConfirm={() => handleCancel(record.id)}
            okText="Hủy ghi danh"
            cancelText="Đóng"
            okButtonProps={{ danger: true }}
            disabled={isCancelled}
            placement="left"
          >
            <Button 
              danger 
              size="small"
              icon={<StopOutlined />} 
              disabled={isCancelled}
            >
              Force Cancel
            </Button>
          </Popconfirm>
        );
      },
    },
  ];

  return (
    <div className="mx-auto max-w-7xl p-4 md:p-6 lg:p-8">
      {contextHolder}
      <div className="mb-6 flex flex-col gap-2">
        <Title level={2} className="mb-0 text-2xl font-bold text-gray-800">Quản lý Ghi danh (Enrollments)</Title>
        <Text className="text-gray-500">Giám sát tiến độ học tập và quản lý quyền truy cập khóa học của học viên.</Text>
      </div>

      <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
        <Table
          dataSource={enrollments}
          columns={columns}
          rowKey="id"
          loading={loading}
          pagination={{
            pageSize: 10,
            showSizeChanger: true,
            showTotal: (total) => <span className="font-medium text-gray-500">Tổng cộng {total} ghi danh</span>
          }}
          scroll={{ x: 1000 }}
          locale={{ emptyText: 'Chưa có dữ liệu ghi danh nào trên hệ thống.' }}
        />
      </div>
    </div>
  );
};

export default EnrollmentPage;