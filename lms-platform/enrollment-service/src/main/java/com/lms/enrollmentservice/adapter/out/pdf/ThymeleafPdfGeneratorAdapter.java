package com.lms.enrollmentservice.adapter.out.pdf;

import com.lms.enrollmentservice.application.port.out.PdfGeneratorPort;
import com.lms.enrollmentservice.application.port.out.dto.CertificateDocumentModel;
import com.lms.shared.enums.ErrorCode;
import com.lms.shared.exception.BusinessException;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class ThymeleafPdfGeneratorAdapter implements PdfGeneratorPort {

    private final TemplateEngine templateEngine;

    @Override
    public byte[] generateCertificate(CertificateDocumentModel model) {
        try {
            Context context = new Context();
            context.setVariable("model", model);
            context.setVariable("templateUrl", model.templateUrl());
            context.setVariable("templateTitle", model.templateTitle());
            String htmlContent = templateEngine.process("certificate-template", context);

            try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                PdfRendererBuilder builder = new PdfRendererBuilder();
                builder.useFastMode();
                builder.useFont(
                        () -> getClass().getResourceAsStream("/fonts/arial.ttf"),
                        "Arial"
                );
                builder.withHtmlContent(htmlContent, "/");
                builder.toStream(os);
                builder.run();
                return os.toByteArray();
            }
        } catch (Exception e) {
            log.error("Failed to generate PDF for certificate code: {}", model.certificateCode(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}