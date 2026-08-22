package com.lms.enrollmentservice.application.port.out;

public interface FileStoragePort {
    String uploadFile(String fileName, byte[] content, String contentType);
    byte[] downloadFile(String fileName);
}