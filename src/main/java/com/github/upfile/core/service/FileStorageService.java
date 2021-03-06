package com.github.upfile.core.service;

import com.github.upfile.config.StorageConfiguration;
import com.github.upfile.core.exception.ApiError;
import com.github.upfile.core.exception.RestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Service

@RequiredArgsConstructor
public class FileStorageService {
    private final StorageConfiguration storageConfiguration;

    public Mono<byte[]> getStoredFileAsByteArray(String fileName) {
        return Mono.just(fileName)
            .flatMap(this::getStoredFile)
            .map(f -> {
                try {
                    return Files.readAllBytes(f.toPath());
                } catch (IOException ioException) {
                    Mono.error(() -> RestException.with(ApiError.IO_EXCEPTION));
                    return new byte[0];
                }
            });
    }

    public Mono<File> getStoredFile(String fileName) {
        return Mono.fromCallable(() -> new File(this.storageConfiguration.getStoragePath() + fileName));
    }

    public Mono<Void> deleteStoredFile(String fileName) {
        return this.getStoredFile(fileName)
            .doOnNext(File::delete)
            .then();
    }
}
