import React, { useEffect, useState } from 'react';
import type { SelectProps } from 'antd';
import SelectCustom from './SelectCustom';

export interface SelectFetchCustomProps extends SelectProps {
  fetchOptions?: () => Promise<any>;
  onChange?: (value: any, options: any) => void;
}

const SelectFetchCustom: React.FC<SelectFetchCustomProps> = ({
  fetchOptions,
  onChange,
  ...props
}) => {
  const [options, setOptions] = useState<SelectProps['options']>([]);
  const [loading, setLoading] = useState(false);

  const fetchData = async () => {
    if (!fetchOptions) return;
    setLoading(true);

    try {
      const data = await fetchOptions();
      setOptions(data);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  return (
    <SelectCustom
      loading={loading}
      options={options}
      onChange={(value) => onChange && onChange(value, options)}
      {...props}
    />
  );
};

export default SelectFetchCustom;
