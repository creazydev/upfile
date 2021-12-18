package com.github.upfile.core.persistence.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.upfile.core.persistence.generator.FileUuidGenerator;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.http.MediaType;
import reactor.util.annotation.NonNull;

import java.time.*;
import java.util.Objects;


@Table

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class File implements Persistable<String> {

    @Id
    private String id;

    private String originalFileName;
    private String mediaType;

    @CreatedDate
    @Getter(AccessLevel.NONE)
    private LocalDateTime createdDate;

    @Builder
    public File(String originalFileName, MediaType mediaType) {
        this.id = FileUuidGenerator.generate();
        this.originalFileName = originalFileName;
        this.mediaType = mediaType.getType();
    }

    @Override
    @JsonIgnore
    public boolean isNew() {
        return Objects.isNull(createdDate);
    }

    public ZonedDateTime getCreatedDate() {
        return createdDate.atZone(ZoneId.systemDefault());
    }
}
