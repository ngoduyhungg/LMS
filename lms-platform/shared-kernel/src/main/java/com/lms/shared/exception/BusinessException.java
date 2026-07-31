package com.lms.shared.exception;

import com.lms.shared.enums.ErrorCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {

        super(errorCode.getMessage());

        this.errorCode = errorCode;
    }

    public BusinessException(
            ErrorCode errorCode,
            Object... args
    ) {

        super(String.format(
                errorCode.getMessage(),
                args
        ));

        this.errorCode = errorCode;
    }
}
