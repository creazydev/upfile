package com.github.upfile.core.service;

import com.github.upfile.core.persistence.model.File;
import com.github.upfile.core.persistence.repository.FileRepository;
import com.github.upfile.core.util.MediaTypeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service

@RequiredArgsConstructor
@Log4j2
public class FileUploadService {
    private final FileRepository fileRepository;
    private final FileStorageService fileStorageService;

    public Mono<File> upload(Mono<FilePart> filePart) {
        return filePart
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
            .onErrorResume(Mono::error)
            .flatMap(fileRepository::save);
    }
}
