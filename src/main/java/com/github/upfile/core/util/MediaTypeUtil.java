package com.github.upfile.core.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.MediaType;

import java.net.URLConnection;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MediaTypeUtil {

    public static Optional<MediaType> guessMediaType(String fileName) {
        try {
            return Optional.of(MediaType.valueOf(URLConnection.guessContentTypeFromName(fileName)));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
