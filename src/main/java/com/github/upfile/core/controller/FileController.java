package com.github.upfile.core.controller;

import com.github.upfile.core.exception.EM400;
import com.github.upfile.core.exception.RestException;
import com.github.upfile.core.persistence.model.File;
import com.github.upfile.core.persistence.repository.FileRepository;
import com.github.upfile.core.service.FileStorageService;
import com.github.upfile.core.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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
        return fileRepository
            .findAll();
    }

    @PostMapping
    public Mono<File> uploadFile(@RequestParam MultipartFile file) {
        return fileUploadService.upload(file);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<byte[]>> getFile(@PathVariable String id) {
        return fileRepository.findById(id)
            .switchIfEmpty(Mono.error(() -> RestException.with(EM400.NOT_FOUND)))
            .flatMap(file -> fileStorageService.getStoredFileAsByteArray(file.getFileName())
                    .map(byteArray -> ResponseEntity
                        .ok()
                        .contentType(MediaType.valueOf(file.getMediaType()))
                        .body(byteArray))
            );
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteFile(@PathVariable String id) {
        return fileRepository.findById(id)
            .switchIfEmpty(Mono.error(() -> RestException.with(EM400.NOT_FOUND)))
            .doOnNext(file -> this.fileStorageService.deleteStoredFile(file.getFileName()))
            .then();
    }
}
