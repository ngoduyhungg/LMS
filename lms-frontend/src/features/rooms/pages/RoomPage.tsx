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
import { roomsRoleAdminApi } from '../api/room-api';
import type { Room } from '../types/room-type';
import type { RoomFilterParams } from '../types/room-filter-params-type';
import { roomFormFields } from '../constants/room-form-fields';
import { roomFilters } from '../constants/room-filter-table';

const RoomPage = () => {
  const { getAll, create, update, remove } = roomsRoleAdminApi;

  const { open, mode, selectedRecord, openCreate, openView, openEdit, close } =
    useFormModal<Room>();

  const {
    data: rooms,

    loading,

    pagination,

    filterValues,

    handleFilterChange,

    handleFilterSubmit,

    handleFilterReset,

    handleChangePage,

    handleDelete,

    refetch,
  } = useTable<Room, RoomFilterParams>({
    fetchApi: getAll,
    removeApi: remove,
  });

  const sectionsRoomForm: SectionForm[] = [
    {
      key: 'room',
      label: 'Thông tin phòng học',
      fields: roomFormFields,
    },
  ];

  const columns = [
    {
      title: 'Mã phòng',
      dataIndex: 'roomCode',
    },
    {
      title: 'Tên phòng',
      dataIndex: 'name',
    },
    {
      title: 'Sức chứa',
      dataIndex: 'capacity',
      align: 'center' as const,
    },
    {
      title: 'Tác vụ',
      align: 'center' as const,
      render: (_: any, record: Room) => {
        return (
          <ActionGroup<Room>
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
        title="Quản lý phòng học"
        subtitle="Danh sách phòng học"
        extra={
          <Button type="primary" onClick={openCreate}>
            + Thêm phòng học
          </Button>
        }
      />

      <div className="mb-4">
        <FilterTableCustom
          dataFilters={roomFilters}
          values={filterValues}
          onChange={handleFilterChange}
          onReset={handleFilterReset}
          onSubmit={handleFilterSubmit}
        />
      </div>

      <TablePaginationCustom<Room>
        columns={columns}
        data={rooms}
        loading={loading}
        pagination={pagination}
        onChangePage={handleChangePage}
      />

      <ModalFormCustom<Room>
        open={open}
        title="Phòng học"
        mode={mode}
        initialValues={selectedRecord}
        disabled={mode === FormModalMode.VIEW}
        onCancel={close}
        onSuccess={refetch}
        onSubmit={
          mode === FormModalMode.CREATE ? create : (values) => update(selectedRecord!.id, values)
        }
        sections={sectionsRoomForm}
      />
    </div>
  );
};

export default RoomPage;
