package com.github.upfile.core.persistence.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.upfile.core.persistence.generator.FileUuidGenerator;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;


@Table

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode(doNotUseGetters = true)
public class File implements Persistable<String> {

    @Id
    private String id;

    private String originalFileName;
    private String mediaType;

    @CreatedDate
    @Getter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    private LocalDateTime createdDate;

    @Builder
    public File(String originalFileName, MediaType mediaType) {
        this.id = FileUuidGenerator.generate();
        this.originalFileName = originalFileName;
        this.mediaType = mediaType.getType() + "/" + mediaType.getSubtype();
    }

    @Override
    @JsonIgnore
    public boolean isNew() {
        return Objects.isNull(createdDate);
    }

    public ZonedDateTime getCreatedDate() {
        return Objects.nonNull(createdDate) ? createdDate.atZone(ZoneId.systemDefault()) : null;
    }
}
