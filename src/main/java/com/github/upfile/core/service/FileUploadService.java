package com.github.upfile.core.service;

import com.github.upfile.core.exception.ApiError;
import com.github.upfile.core.exception.RestException;
import com.github.upfile.core.persistence.model.File;
import com.github.upfile.core.persistence.repository.FileRepository;
import com.github.upfile.core.util.MediaTypeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service

@RequiredArgsConstructor
@Log4j2
public class FileUploadService {
    private final FileRepository fileRepository;
    private final FileStorageService fileStorageService;

    public Mono<File> upload(Mono<FilePart> filePart) {
        return filePart
            .filter(this::validateFilePart)
            .switchIfEmpty(Mono.error(RestException.with(ApiError.FILE_NOT_READABLE)))
            .onErrorStop()
            .flatMap(filePart_ -> {
                    File file = File.builder()
                        .mediaType(
                            MediaTypeUtil
                                .guessMediaType(filePart_.filename())
                                .orElse(MediaType.APPLICATION_OCTET_STREAM)
                        )
                        .originalFileName(filePart_.filename())
                        .build();

                    return fileStorageService.getStoredFile(file.getId())
                        .doOnSuccess(dest -> filePart_.transferTo(dest).subscribe())
                        .onErrorResume(Mono::error)
                        .thenReturn(file);
                }
            )
            .flatMap(fileRepository::save)
            .onErrorResume(Mono::error);
    }

    private boolean validateFilePart(FilePart filePart) {
        return Objects.nonNull(filePart)
            && Strings.isNotBlank(filePart.filename());
    }
}
