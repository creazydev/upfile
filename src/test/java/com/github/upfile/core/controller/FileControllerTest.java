package com.github.upfile.core.controller;

import com.github.upfile.core.persistence.model.File;
import com.github.upfile.core.persistence.repository.FileRepository;
import com.github.upfile.core.service.FileStorageService;
import com.github.upfile.core.service.FileUploadService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = FileController.class)
class FileControllerTest {

    @MockBean
    FileUploadService fileUploadService;

    @MockBean
    FileStorageService fileStorageService;

    @MockBean
    FileRepository repository;

    @Autowired
    private WebTestClient webClient;

    private File getSampleFile() {
        return File.builder()
                .mediaType(MediaType.APPLICATION_PDF)
                .originalFileName("sample.pdf")
                .build();
    }

    @Test
    void listFiles() {
        File sampleFile = this.getSampleFile();
        Mockito.when(repository.findAll()).thenReturn(Flux.just(sampleFile));
        webClient.get()
                .uri("/api/files")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(File.class)
                .contains(sampleFile)
                .hasSize(1);

        Mockito.verify(repository, times(1)).findAll();
    }

    @Test
    void uploadFile() {
        File sampleFile = this.getSampleFile();
        FilePart filePart = mock(FilePart.class);
        given(filePart.filename()).willReturn(sampleFile.getOriginalFileName());

        Mockito
                .when(fileUploadService.upload(Mono.just(filePart)))
                .thenReturn(Mono.just(sampleFile));

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.asyncPart("file", Mono.just(filePart), FilePart.class);

        webClient.post()
                .uri("/api/files")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(File.class);
    }

    @Test
    void getFile() {
        File file = this.getSampleFile();
        Mockito
                .when(fileStorageService.getStoredFile(file.getId())
                        .thenReturn(Mono.just(new byte[64])));
        Mockito
                .when(repository.findById(file.getId())
                        .thenReturn(Mono.just(file)));

        webClient.get()
                .uri("/api/files/{id}", file.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(file.getMediaType())
                .expectHeader().contentLength(64)
                .expectBody(byte[].class);

        Mockito.verify(repository, times(1)).findById(file.getId());
        Mockito.verify(fileStorageService, times(1)).getStoredFile(file.getId());
    }

    @Test
    void deleteFile() {
        Mono<Void> voidReturn = Mono.empty();
        File file = this.getSampleFile();
        Mockito.when(repository.findById(Objects.requireNonNull(file.getId()))).thenReturn(Mono.just(file));
        Mockito.when(repository.delete(file)).thenReturn(voidReturn);
        Mockito.when(fileStorageService.deleteStoredFile(file.getId())).thenReturn(voidReturn);
        webClient.delete().uri("/api/files/{id}", file.getId())
                .exchange()
                .expectStatus().isOk();
    }
}