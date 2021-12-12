package com.github.upfile.core.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.springframework.web.multipart.MultipartFile;


public class FileUploadedEvent extends ApplicationEvent {
    @Getter
    private final String fileName;

    public FileUploadedEvent(String fileName, MultipartFile multipartFile) {
        super(multipartFile);
        this.fileName = fileName;
    }
}
