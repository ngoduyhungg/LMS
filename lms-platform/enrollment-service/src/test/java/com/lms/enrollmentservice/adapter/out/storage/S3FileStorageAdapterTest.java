package com.lms.enrollmentservice.adapter.out.storage;

import com.lms.shared.enums.ErrorCode;
import com.lms.shared.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3FileStorageAdapterTest {

    @Mock
    private S3Client s3Client;

    @InjectMocks
    private S3FileStorageAdapter s3FileStorageAdapter;

    private final String bucketName = "lms-certificates";
    private final String endpoint = "http://localhost:9000";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(s3FileStorageAdapter, "bucketName", bucketName);
        ReflectionTestUtils.setField(s3FileStorageAdapter, "endpoint", endpoint);
    }

    @Test
    @DisplayName("Nên upload file thành công, xác thực bucket, tên, type và trả về URL hợp lệ")
    void shouldUploadFileSuccessfully() {
        String fileName = "certificate_1_user_CERT-123.pdf";
        byte[] content = "dummy-pdf-content".getBytes(); // Mảng byte hợp lệ
        String contentType = "application/pdf";

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        String resultUrl = s3FileStorageAdapter.uploadFile(fileName, content, contentType);

        assertThat(resultUrl).isEqualTo("http://localhost:9000/lms-certificates/certificate_1_user_CERT-123.pdf");

        // Verify request gửi lên AWS SDK đúng thông số
        ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(requestCaptor.capture(), any(RequestBody.class));

        PutObjectRequest capturedRequest = requestCaptor.getValue();
        assertThat(capturedRequest.bucket()).isEqualTo(bucketName);
        assertThat(capturedRequest.key()).isEqualTo(fileName);
        assertThat(capturedRequest.contentType()).isEqualTo(contentType);
    }

    @Test
    @DisplayName("Mảng byte rỗng vẫn được đưa lên SDK chuẩn xác (Trừ khi S3 từ chối)")
    void shouldHandleEmptyByteArrayUpload() {
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        String result = s3FileStorageAdapter.uploadFile("empty.pdf", new byte[0], "application/pdf");
        assertThat(result).isNotNull();
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    @DisplayName("Nên ném BusinessException khi S3 SDK gặp lỗi kết nối hoặc quyền (S3Exception)")
    void shouldThrowExceptionWhenS3Fails() {
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(S3Exception.builder().message("Access Denied").build());

        assertThatThrownBy(() -> s3FileStorageAdapter.uploadFile("test.pdf", new byte[0], "application/pdf"))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Nên ném BusinessException(INTERNAL_SERVER_ERROR) khi gặp RuntimeException bất kỳ, không bị leak")
    void shouldThrowExceptionOnUnexpectedError() {
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(new RuntimeException("Connection timeout"));

        assertThatThrownBy(() -> s3FileStorageAdapter.uploadFile("test.pdf", new byte[0], "application/pdf"))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INTERNAL_SERVER_ERROR);
    }
}