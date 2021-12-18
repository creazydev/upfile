package com.github.upfile.core.exception;

import org.springframework.web.server.ResponseStatusException;

public class RestException extends ResponseStatusException {

    public RestException(ApiError apiError) {
        super(apiError.getHttpStatus(), apiError.getReadableMessage());
    }

    public static RestException with(ApiError apiError) {
        return new RestException(apiError);
    }
}
