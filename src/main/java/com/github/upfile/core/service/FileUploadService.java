package com.github.upfile.core.service;

import com.github.upfile.core.persistence.model.File;
import com.github.upfile.core.persistence.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

@Service

@RequiredArgsConstructor
public class FileUploadService {
    private final FileRepository fileRepository;
    private final FileStorageService fileStorageService;

    public Mono<File> upload(MultipartFile multipartFile) {
        return Mono
            .just(multipartFile)
            .map(mFile -> File
                .builder()
                .build()
            )
            .doOnNext(file -> fileStorageService.storeFile(file.getFileName(), multipartFile))
            .flatMap(fileRepository::save);
    }
}
