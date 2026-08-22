package com.lms.enrollmentservice.adapter.in.rest;

import com.lms.enrollmentservice.adapter.in.rest.dto.UserCertificateResponse;
import com.lms.enrollmentservice.adapter.in.rest.mapper.CertificateRestMapper;
import com.lms.enrollmentservice.application.port.in.DownloadCertificateUseCase;
import com.lms.enrollmentservice.application.port.in.GetCertificateUseCase;
import com.lms.enrollmentservice.domain.model.UserCertificate;
import com.lms.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final GetCertificateUseCase getCertificateUseCase;
    private final CertificateRestMapper restMapper;
    private final DownloadCertificateUseCase downloadCertificateUseCase;

    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<UserCertificateResponse>> getMyCertificates() {
        String currentUserId = SecurityUtils.getCurrentUserId();
        List<UserCertificate> certificates = getCertificateUseCase.getMyCertificates(currentUserId);
        return ResponseEntity.ok(restMapper.toResponseList(certificates));
    }
    @GetMapping("/{certificateCode}/download")
    public ResponseEntity<byte[]> downloadCertificate(@PathVariable String certificateCode) {
        byte[] pdfBytes = downloadCertificateUseCase.downloadCertificatePdf(certificateCode);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        // Dùng "inline" để xem trực tiếp trên trình duyệt, dùng "attachment" nếu muốn ép tải file về máy
        headers.setContentDispositionFormData("inline", certificateCode + ".pdf");
        headers.setCacheControl("max-age=3600"); // Tối ưu cache trình duyệt

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}