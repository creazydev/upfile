package com.github.upfile.core.controller;

import com.github.upfile.core.exception.ApiError;
import com.github.upfile.core.exception.EM404;
import com.github.upfile.core.exception.RestException;
import com.github.upfile.core.persistence.model.File;
import com.github.upfile.core.persistence.repository.FileRepository;
import com.github.upfile.core.service.FileStorageService;
import com.github.upfile.core.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/files")

@RequiredArgsConstructor
@Log4j2
public class FileController {
    private final FileUploadService fileUploadService;
    private final FileRepository fileRepository;
    private final FileStorageService fileStorageService;

    @GetMapping
    public Flux<File> listFiles() {
        return fileRepository.findAll();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<File> uploadFile(@RequestPart(name = "file", value = "file") Mono<FilePart> file) {
        return fileUploadService.upload(file);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<byte[]>> getFile(@PathVariable String id) {
        return fileRepository.findById(id)
            .switchIfEmpty(Mono.error(() -> RestException.with(ApiError.NOT_FOUND)))
            .flatMap(file -> fileStorageService.getStoredFileAsByteArray(file.getId())
                    .map(byteArray -> ResponseEntity
                        .ok()
                        .contentType(MediaType.valueOf(file.getMediaType()))
                        .body(byteArray))
            );
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteFile(@PathVariable String id) {
        return fileRepository.findById(id)
            .switchIfEmpty(Mono.error(() -> RestException.with(ApiError.NOT_FOUND)))
            .doOnNext(file -> this.fileStorageService.deleteStoredFile(file.getId()))
            .doOnNext(fileRepository::delete)
            .then();
    }
}
