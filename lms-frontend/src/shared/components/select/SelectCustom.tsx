import React from 'react';
import { Select, Spin, type SelectProps } from 'antd';
import EmptyCustom from '../empty/EmptyCustom';

export interface SelectCustomProps extends SelectProps {}

const SelectCustom: React.FC<SelectCustomProps> = ({ loading, ...props }) => {
  return (
    <Select
      showSearch
      allowClear
      placeholder=" "
      className="w-full min-h-10"
      notFoundContent={loading ? <Spin size="small" /> : <EmptyCustom />}
      {...props}
    />
  );
};

export default SelectCustom;
