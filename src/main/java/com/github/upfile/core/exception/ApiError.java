package com.github.upfile.core.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ApiError {
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown error"),
    IO_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "Problem with writing to/reading from storage"),
    FILE_NOT_READABLE(HttpStatus.BAD_REQUEST, "File is not readable"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "Not found");


    private final HttpStatus httpStatus;
    private final String readableMessage;
}
