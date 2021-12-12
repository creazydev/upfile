package com.github.upfile.core.persistence.generator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUuidGenerator {

    public static String generate() {
        return System.currentTimeMillis()
            + "_"
            + UUID.randomUUID().toString().replace("_", "").substring(0, 128);
    }
}
