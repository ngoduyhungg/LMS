import useTable from '@/shared/hooks/useTable';
import PageHeader from '@/shared/components/page/PageHeader';
import { Button } from 'antd';
import ModalFormCustom from '@/shared/components/modal/ModalFormCustom';
import { useFormModal } from '@/shared/hooks/useFormModal';
import { FormModalMode } from '@/shared/types/form-modal-mode-type';
import FilterTableCustom from '@/shared/components/table/FilterTableCustom';
import TablePaginationCustom from '@/shared/components/table/TablePaginationCustom';
import ActionGroup from '@/shared/components/table/ActionGroup';
import {
  EyeOutlined,
  EditOutlined,
  DeleteOutlined,
  CheckOutlined,
  CloseOutlined,
} from '@ant-design/icons';
import type { SectionForm } from '@/shared/components/modal/ModalFormCustom';
import { LeaveRequestStatus, type LeaveRequest } from '../types/leave-request-type';
import { leaveRequestRoleAdminApi, leaveRequestRoleStudentApi } from '../api/leave-request-api';
import type { LeaveRequestFilterParams } from '../types/leave-request-filter-params-type';
import { leaveRequestFormFields } from '../constants/leave-request-form-fields';
import { useAppSelector } from '@/app/redux/hooks';
import { USER_ROLE } from '@/features/users/types/user-role-type';
import LeaveRequestStatusTag from '../components/LeaveRequestStatusTag';
import { leaveRequestFilters } from '../constants/leave-request-filter-table';
import { mappedTimeInformation } from '@/features/course-class-session/utils';

const LeaveRequestPage = () => {
  const { user } = useAppSelector((state) => state.auth);
  const { getAll: getAllByAdmin, approve, reject } = leaveRequestRoleAdminApi;
  const { getAll, create, update, remove } = leaveRequestRoleStudentApi;

  const { open, mode, selectedRecord, openCreate, openView, openEdit, close } =
    useFormModal<LeaveRequest>();

  const isAdmin = user?.role === USER_ROLE.ADMIN;

  const {
    data: leaveRequests,

    loading,

    pagination,

    filterValues,

    handleFilterChange,

    handleFilterSubmit,

    handleFilterReset,

    handleChangePage,

    handleDelete,

    handleActive,

    handleInActive,

    refetch,
  } = useTable<LeaveRequest, LeaveRequestFilterParams>({
    fetchApi: isAdmin ? getAllByAdmin : getAll,
    removeApi: remove,
    activeApi: approve,
    inActiveApi: reject,
  });

  const sectionsLeaveRequestForm: SectionForm[] = [
    {
      key: 'leaveRequest',
      label: 'Thông tin đơn xin nghỉ',
      fields: leaveRequestFormFields,
    },
  ];

  const columns = [
    {
      title: 'Học viên',
      dataIndex: 'studentName',
    },
    {
      title: 'Lớp học',
      dataIndex: 'courseClassName',
    },
    {
      title: 'Thời gian',
      dataIndex: 'leaveDate',
      render: (_: string, record: LeaveRequest) => (
        <span>
          {mappedTimeInformation({
            scheduleSlotName: record.scheduleSlotName,
            startTime: record.startTime,
            endTime: record.endTime,
          })}
        </span>
      ),
    },
    {
      title: 'Lý do xin nghỉ',
      dataIndex: 'reason',
    },
    {
      title: 'Trạng thái',
      dataIndex: 'status',
      align: 'center' as const,
      render: (_: any, record: LeaveRequest) => {
        return <LeaveRequestStatusTag status={record.status} statusText={record.statusText} />;
      },
    },
    {
      title: 'Tác vụ',
      align: 'center' as const,
      render: (_: any, record: LeaveRequest) => {
        return (
          <ActionGroup<LeaveRequest>
            record={record}
            actions={[
              {
                show: () => true,
                icon: <EyeOutlined />,
                tooltip: 'Chi tiết',
                onClick: openView,
              },
              {
                show: (r) => r.status === LeaveRequestStatus.PENDING,
                icon: <EditOutlined />,
                tooltip: 'Sửa',
                onClick: openEdit,
              },
              {
                show: (r) => !isAdmin && r.status === LeaveRequestStatus.PENDING,
                icon: <DeleteOutlined />,
                tooltip: 'Xóa đơn xin nghỉ',
                danger: true,
                onClick: () => handleDelete(record.id),
                isPopconfirm: true,
              },
              {
                show: (r) => isAdmin && r.status === LeaveRequestStatus.PENDING,
                icon: <CheckOutlined />,
                tooltip: 'Duyệt đơn xin nghỉ',
                onClick: () => handleActive(record.id),
                isPopconfirm: true,
              },
              {
                show: (r) => isAdmin && r.status === LeaveRequestStatus.PENDING,
                icon: <CloseOutlined />,
                tooltip: 'Từ chối đơn xin nghỉ',
                danger: true,
                onClick: () => handleInActive(record.id),
                isPopconfirm: true,
              },
            ]}
          />
        );
      },
    },
  ];

  return (
    <div className="flex flex-col h-full">
      <PageHeader
        title="Quản lý đơn xin nghỉ"
        subtitle="Danh sách đơn xin nghỉ"
        extra={
          !isAdmin && (
            <Button type="primary" onClick={openCreate}>
              + Thêm đơn xin nghỉ
            </Button>
          )
        }
      />

      <div className="mb-4">
        <FilterTableCustom
          dataFilters={leaveRequestFilters}
          values={filterValues}
          onChange={handleFilterChange}
          onReset={handleFilterReset}
          onSubmit={handleFilterSubmit}
        />
      </div>

      <TablePaginationCustom<LeaveRequest>
        columns={columns}
        data={leaveRequests}
        loading={loading}
        pagination={pagination}
        onChangePage={handleChangePage}
      />

      <ModalFormCustom<LeaveRequest>
        open={open}
        title="Đơn xin nghỉ"
        mode={mode}
        initialValues={selectedRecord}
        disabled={mode === FormModalMode.VIEW}
        onCancel={close}
        onSuccess={refetch}
        onSubmit={
          mode === FormModalMode.CREATE ? create : (values) => update(selectedRecord!.id, values)
        }
        sections={sectionsLeaveRequestForm}
      />
    </div>
  );
};

export default LeaveRequestPage;
