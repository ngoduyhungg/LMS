import { useCallback, useEffect, useState } from 'react';

import { PAGE_DEFAULT, PAGE_LIMIT } from '../constants/pagination';

import { useNotification } from './useNotification';

interface PaginationResponse {
  total: number;

  page: number;

  limit: number;
}

interface useTableProps<P> {
  fetchApi: (params: P) => Promise<any>;

  removeApi?: (id: string) => Promise<any>;

  activeApi?: (id: string) => Promise<any>;

  inActiveApi?: (id: string) => Promise<any>;

  initialFilters?: Partial<P>;
}

const useTable = <T, P>({
  fetchApi,
  removeApi,
  activeApi,
  inActiveApi,
  initialFilters = {},
}: useTableProps<P>) => {
  const { showNotification } = useNotification();

  const [data, setData] = useState<T[]>([]);

  const [loading, setLoading] = useState(false);

  // UI filter state
  const [filterValues, setFilterValues] = useState<Partial<P>>(initialFilters);

  // params thực sự fetch API
  const [params, setParams] = useState<P>({
    page: PAGE_DEFAULT,
    limit: PAGE_LIMIT,
    ...initialFilters,
  } as P);

  const [pagination, setPagination] = useState<PaginationResponse>({
    total: 0,
    page: PAGE_DEFAULT,
    limit: PAGE_LIMIT,
  });

  const fetchData = useCallback(async () => {
    try {
      setLoading(true);

      const response = await fetchApi(params);

      setData(response.data.items || []);

      setPagination({
        total: response.data.pagination?.total || 0,

        page: response.data.pagination?.page || PAGE_DEFAULT,

        limit: response.data.pagination?.limit || PAGE_LIMIT,
      });
    } catch (error) {
      console.log(error);
    } finally {
      setLoading(false);
    }
  }, [fetchApi, params]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  const handleFilterChange = (values: Partial<P>) => {
    setFilterValues(values);
  };

  const handleFilterSubmit = () => {
    setParams((prev) => ({
      ...prev,

      ...filterValues,

      page: PAGE_DEFAULT,
    }));
  };

  const handleFilterReset = () => {
    setFilterValues(initialFilters);

    setParams({
      page: PAGE_DEFAULT,
      limit: PAGE_LIMIT,
      ...initialFilters,
    } as P);
  };

  const handleChangePage = (page: number, limit: number) => {
    setParams((prev) => ({
      ...prev,
      page,
      limit,
    }));
  };

  const handleDelete = async (id: string) => {
    if (!removeApi) return;

    try {
      const res = await removeApi(id);

      showNotification('success', 'Xóa thành công', res.message || 'Xóa thành công');

      fetchData();
    } catch (error: any) {
      showNotification(
        'error',
        'Xóa thất bại',
        error?.response?.data?.message || 'Đã có lỗi xảy ra',
      );
    }
  };

  const handleActive = async (id: string) => {
    if (!activeApi) return;

    try {
      const res = await activeApi(id);

      showNotification('success', 'Chuyển trạng thái', res.message || 'Kích hoạt thành công');

      fetchData();
    } catch (error: any) {
      showNotification(
        'error',
        'Chuyển trạng thái',
        error?.response?.data?.message || 'Đã có lỗi xảy ra',
      );
    }
  };

  const handleInActive = async (id: string) => {
    if (!inActiveApi) return;

    try {
      const res = await inActiveApi(id);

      showNotification('success', 'Chuyển trạng thái', res.message || 'Ngưng hoạt động thành công');

      fetchData();
    } catch (error: any) {
      showNotification(
        'error',
        'Chuyển trạng thái',
        error?.response?.data?.message || 'Đã có lỗi xảy ra',
      );
    }
  };

  return {
    data,

    loading,

    pagination,

    params,

    filterValues,

    setParams,

    setFilterValues,

    handleFilterChange,

    handleFilterSubmit,

    handleFilterReset,

    handleChangePage,

    handleDelete,

    handleActive,

    handleInActive,

    refetch: fetchData,
  };
};

export default useTable;
