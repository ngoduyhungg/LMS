import { Modal } from 'antd';

interface ModalCustomProps {
  open: boolean;
  title?: string;
  width?: number;

  children: React.ReactNode;

  onCancel: () => void;

  footer?: React.ReactNode;

  destroyOnHidden?: boolean;

  isCentered?: boolean;
}

const ModalCustom = ({
  open,
  title,
  width = 700,
  children,
  onCancel,
  footer = null,
  destroyOnHidden = true,
  isCentered = true,
}: ModalCustomProps) => {
  return (
    <Modal
      open={open}
      title={title}
      width={width}
      footer={footer}
      onCancel={onCancel}
      destroyOnHidden={destroyOnHidden}
      centered={isCentered}
    >
      {children}
    </Modal>
  );
};

export default ModalCustom;
