import React, { useEffect, useState } from 'react';
import { Typography, Row, Col, Card, Skeleton, Button, message } from 'antd';
import { DownloadOutlined, SafetyCertificateOutlined } from '@ant-design/icons';
import { certificateApi } from '../api/certificate-api';
import type { Certificate } from '../types/certificate-type';

const { Title, Text } = Typography;

const MyCertificatesPage: React.FC = () => {
  const [certificates, setCertificates] = useState<Certificate[]>([]);
  const [loading, setLoading] = useState(true);
  const [downloadingId, setDownloadingId] = useState<string | null>(null);
  const [messageApi, contextHolder] = message.useMessage();

  useEffect(() => {
    const fetchCertificates = async () => {
    try {
      setLoading(true);
      const data = await certificateApi.getMyCertificates();
      setCertificates(Array.isArray(data) ? data : []);
    } catch (error: any) {
      // HOTFIX UX: Nếu là lỗi 403 (Không có quyền), chỉ đơn giản là hiện danh sách rỗng, không báo lỗi đỏ
      if (error.response?.status !== 403) {
        console.error('Lỗi tải chứng chỉ:', error);
        // Có thể gọi messageApi.error() ở đây nếu cần
      }
      setCertificates([]); // Gán mảng rỗng để hiện component Empty
    } finally {
      setLoading(false);
    }
  };
    fetchCertificates();
  }, []);

  const handleDownload = async (pdfUrl: string, code: string) => {
    try {
      setDownloadingId(code);
      // Ép tải file PDF về máy tính với tên file là Mã Chứng chỉ
      await certificateApi.downloadPdf(pdfUrl, `${code}.pdf`);
      messageApi.success('Tải chứng chỉ thành công!');
    } catch (error) {
      console.error('Lỗi khi tải PDF:', error);
      messageApi.error('Không thể tải chứng chỉ lúc này. Vui lòng thử lại.');
    } finally {
      setDownloadingId(null);
    }
  };

  return (
    <div className="mx-auto max-w-7xl p-4 md:p-6 lg:p-8">
      {contextHolder}
      <div className="mb-8 flex items-center gap-3">
        <SafetyCertificateOutlined className="text-3xl text-yellow-500" />
        <div>
          <Title level={2} className="mb-1 text-2xl font-bold text-gray-800">Chứng chỉ của tôi</Title>
          <Text className="text-gray-500">Các thành tựu bạn đã đạt được sau khi hoàn thành khóa học.</Text>
        </div>
      </div>

      {loading ? (
        <Row gutter={[24, 24]}>
          {[1, 2, 3].map(i => (
            <Col xs={24} sm={12} lg={8} key={i}>
              <Card className="rounded-xl shadow-sm border-gray-100">
                <Skeleton active paragraph={{ rows: 2 }} />
              </Card>
            </Col>
          ))}
        </Row>
      ) : certificates.length > 0 ? (
        <Row gutter={[24, 24]}>
          {certificates.map((cert) => (
            <Col xs={24} sm={12} lg={8} key={cert.certificateCode}>
              <Card className="rounded-xl shadow-sm hover:shadow-md transition-shadow border-gray-200">
                <div className="flex flex-col items-center text-center p-4">
                  <div className="w-20 h-20 bg-yellow-50 rounded-full flex items-center justify-center mb-4 border-4 border-yellow-100">
                    <SafetyCertificateOutlined className="text-4xl text-yellow-600" />
                  </div>
                  <Title level={5} className="mb-1">Chứng nhận Hoàn thành Khóa học</Title>
                  <Text className="text-gray-500 text-xs mb-4">Mã: {cert.certificateCode}</Text>
                  
                  <div className="w-full bg-gray-50 p-3 rounded-lg mb-4">
                    <Text className="text-sm font-medium text-gray-700">Khóa học ID: {cert.enrollmentId}</Text>
                    <br/>
                    <Text className="text-xs text-gray-500">Ngày cấp: {new Date(cert.issuedAt).toLocaleDateString('vi-VN')}</Text>
                  </div>

                  <Button 
                    type="primary" 
                    icon={<DownloadOutlined />} 
                    disabled={!cert.pdfUrl} 
                    loading={downloadingId === cert.certificateCode}
                    onClick={() => handleDownload(cert.pdfUrl!, cert.certificateCode)}
                    className="w-full bg-gray-800 hover:bg-gray-700"
                  >
                    Tải PDF
                  </Button>
                </div>
              </Card>
            </Col>
          ))}
        </Row>
      ) : (
        <div className="flex flex-col items-center justify-center py-20 bg-white rounded-2xl border border-dashed border-gray-200 shadow-sm">
          <div className="w-20 h-20 bg-gray-50 rounded-full flex items-center justify-center mb-4">
            <SafetyCertificateOutlined className="text-4xl text-gray-300" />
          </div>
          <Text className="text-lg font-medium text-gray-600 mb-1">Chưa có chứng chỉ nào</Text>
          <Text className="text-gray-400 text-sm">Hãy hoàn thành 100% tiến độ khóa học để nhận chứng chỉ nhé.</Text>
        </div>
      )}
    </div>
  );
};

export default MyCertificatesPage;