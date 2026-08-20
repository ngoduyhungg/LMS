package com.lms.enrollmentservice.adapter.out.storage;

import com.lms.enrollmentservice.application.port.out.FileStoragePort;
import com.lms.shared.enums.ErrorCode;
import com.lms.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3FileStorageAdapter implements FileStoragePort {

    private final S3Client s3Client;

    @Value("${app.storage.s3.bucket}")
    private String bucketName;

    @Value("${app.storage.s3.endpoint}")
    private String endpoint;

    @Override
    public String uploadFile(String fileName, byte[] content, String contentType) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(contentType)
                    .build();

            // Thực thi upload mảng byte lên S3/MinIO
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(content));

            // Trả về Absolute URL theo chiến lược Path-Style Access (MinIO Local).
            // VD: http://localhost:9000/lms-certificates/certificate_8_user1_CERT-XXXX.pdf
            return endpoint.endsWith("/") ?
                    endpoint + bucketName + "/" + fileName :
                    endpoint + "/" + bucketName + "/" + fileName;

        } catch (S3Exception e) {
            String errorMessage = e.awsErrorDetails() != null ? e.awsErrorDetails().errorMessage() : e.getMessage();
            log.error("S3 error occurred while uploading file: {}. Error: {}", fileName, errorMessage, e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error("Unexpected/Network error occurred while uploading file: {}", fileName, e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}