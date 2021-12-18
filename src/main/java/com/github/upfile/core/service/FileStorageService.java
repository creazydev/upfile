package com.github.upfile.core.service;

import com.github.upfile.config.StorageConfiguration;
import com.github.upfile.core.exception.EM400;
import com.github.upfile.core.exception.EM500;
import com.github.upfile.core.exception.RestException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
                    Mono.error(() -> RestException.with(EM500.IO_EXCEPTION));
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

    public Mono<Void> storeFile(String fileName, Mono<FilePart> filePartMono) {
        return Mono
            .just(fileName)
            .flatMap(this::getStoredFile)
            .doOnNext(file -> filePartMono.flatMap(it -> it.transferTo(file)))
            .onErrorResume(Mono::error)
            .then();
    }
}
