package com.github.upfile.core.service;

import com.github.upfile.core.persistence.model.File;
import com.github.upfile.core.persistence.repository.FileRepository;
import com.github.upfile.core.util.MediaTypeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service

@RequiredArgsConstructor
public class FileUploadService {
    private final FileRepository fileRepository;
    private final FileStorageService fileStorageService;

    public Mono<File> upload(Mono<FilePart> filePart) {
        return filePart
            .map(filePart_ -> File
                .builder()
                .mediaType(MediaTypeUtil.guessMediaType(filePart_.filename()).orElse(MediaType.APPLICATION_OCTET_STREAM))
                .originalFileName(filePart_.filename())
                .build()
            )
            .doOnNext(file -> fileStorageService.storeFile(file.getId(), filePart))
            .flatMap(fileRepository::save);
    }
}
