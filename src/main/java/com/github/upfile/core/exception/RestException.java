package com.github.upfile.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class RestException extends ResponseStatusException {

    public RestException(EM400 message) {
        super(HttpStatus.BAD_REQUEST, message.name());
    }

    public RestException(EM500 message) {
        super(HttpStatus.BAD_REQUEST, message.name());
    }

    public static RestException with(EM400 message) {
        return new RestException(message);
    }

    public static RestException with(EM500 message) {
        return new RestException(message);
    }
}
