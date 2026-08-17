import useTable from '@/shared/hooks/useTable';
import PageHeader from '@/shared/components/page/PageHeader';
import { Button } from 'antd';
import ModalFormCustom from '@/shared/components/modal/ModalFormCustom';
import { useFormModal } from '@/shared/hooks/useFormModal';
import { FormModalMode } from '@/shared/types/form-modal-mode-type';
import FilterTableCustom from '@/shared/components/table/FilterTableCustom';
import TablePaginationCustom from '@/shared/components/table/TablePaginationCustom';
import ActionGroup from '@/shared/components/table/ActionGroup';
import { EyeOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import type { SectionForm } from '@/shared/components/modal/ModalFormCustom';
import { scheduleRoleAdminApi } from '../api/schedule-api';
import type { Schedule } from '../types/schedule-type';
import { scheduleFilters } from '../constants/schedule-filter-table';
import type { ScheduleFilterParams } from '../types/schedule-filter-params-type';
import { scheduleFormFields } from '../constants/schedule-form-fields';

const SchedulePage = () => {
  const { getAll, create, update, remove } = scheduleRoleAdminApi;

  const { open, mode, selectedRecord, openCreate, openView, openEdit, close } =
    useFormModal<Schedule>();

  const {
    data: schedules,

    loading,

    pagination,

    filterValues,

    handleFilterChange,

    handleFilterSubmit,

    handleFilterReset,

    handleChangePage,

    handleDelete,

    refetch,
  } = useTable<Schedule, ScheduleFilterParams>({
    fetchApi: getAll,
    removeApi: remove,
  });

  const sectionsScheduleForm: SectionForm[] = [
    {
      key: 'schedule',
      label: 'Thông tin ca học',
      fields: scheduleFormFields,
    },
  ];

  const columns = [
    {
      title: 'Mã ca học',
      dataIndex: 'slotCode',
    },
    {
      title: 'Thứ',
      dataIndex: 'weekdayName',
    },
    {
      title: 'Thời gian bắt đầu',
      dataIndex: 'startTime',
      align: 'center' as const,
    },
    {
      title: 'Thời gian kết thúc',
      dataIndex: 'endTime',
      align: 'center' as const,
    },
    {
      title: 'Tác vụ',
      align: 'center' as const,
      render: (_: any, record: Schedule) => {
        return (
          <ActionGroup<Schedule>
            record={record}
            actions={[
              {
                show: () => true,
                icon: <EyeOutlined />,
                tooltip: 'Chi tiết',
                onClick: openView,
              },
              {
                show: () => true,
                icon: <EditOutlined />,
                tooltip: 'Sửa',
                onClick: openEdit,
              },
              {
                show: () => true,
                icon: <DeleteOutlined />,
                tooltip: 'Xóa',
                danger: true,
                onClick: () => handleDelete(record.id),
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
        title="Quản lý ca học"
        subtitle="Danh sách ca học"
        extra={
          <Button type="primary" onClick={openCreate}>
            + Thêm ca học
          </Button>
        }
      />

      <div className="mb-4">
        <FilterTableCustom
          dataFilters={scheduleFilters}
          values={filterValues}
          onChange={handleFilterChange}
          onReset={handleFilterReset}
          onSubmit={handleFilterSubmit}
        />
      </div>

      <TablePaginationCustom<Schedule>
        columns={columns}
        data={schedules}
        loading={loading}
        pagination={pagination}
        onChangePage={handleChangePage}
      />

      <ModalFormCustom<Schedule>
        open={open}
        title="Ca học"
        mode={mode}
        initialValues={selectedRecord}
        disabled={mode === FormModalMode.VIEW}
        onCancel={close}
        onSuccess={refetch}
        onSubmit={
          mode === FormModalMode.CREATE ? create : (values) => update(selectedRecord!.id, values)
        }
        sections={sectionsScheduleForm}
      />
    </div>
  );
};

export default SchedulePage;
