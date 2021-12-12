package com.github.upfile.core.persistence.model;

import com.github.upfile.core.persistence.generator.FileUuidGenerator;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;


@Table

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class File {

    @Id
    private String id;

    private String fileName;
    private String originalFileName;
    private String mediaType;

    @CreatedDate
    private LocalDateTime createdDate;

    @Builder
    public File(String fileName, String originalFileName, MediaType mediaType) {
        this.id = FileUuidGenerator.generate();
        this.fileName = fileName;
        this.originalFileName = originalFileName;
        this.mediaType = mediaType.getType();
    }
}
