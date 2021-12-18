package com.github.upfile.core.persistence.converter;

import io.r2dbc.spi.Clob;
import org.apache.logging.log4j.util.Strings;
import org.springframework.core.convert.converter.Converter;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class ClobConverter implements Converter<Clob, String> {

    @Override
    public String convert(Clob source) {
        try {
            Flux<?> sourceStream = (Flux<?>) source.stream();
            return String.join("", (List) sourceStream.collectList().toFuture().get());
        } catch (InterruptedException | ExecutionException e) {
            return Strings.EMPTY;
        }
    }
}
