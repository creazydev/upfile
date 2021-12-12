package com.github.upfile.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration

@Getter
public class StorageConfiguration {

    @Value("${storage.path}")
    private String storagePath;
}
