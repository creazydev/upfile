package com.github.upfile.config;

import com.github.upfile.core.persistence.converter.ClobConverter;
import io.r2dbc.spi.ConnectionFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.data.relational.core.conversion.BasicRelationalConverter;
import org.springframework.r2dbc.connection.init.CompositeDatabasePopulator;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;

import javax.annotation.PostConstruct;

@Configuration
@EnableR2dbcRepositories
@EnableR2dbcAuditing

@RequiredArgsConstructor
public class PersistenceConfiguration {
    private final BasicRelationalConverter basicRelationalConverter;

    @Bean
    public ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {
        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);

        CompositeDatabasePopulator populator = new CompositeDatabasePopulator();
        populator.addPopulators(new ResourceDatabasePopulator(new ClassPathResource("db/schema.sql")));
        initializer.setDatabasePopulator(populator);
        return initializer;
    }

    @PostConstruct
    public void addConverters() {
        ConfigurableConversionService conversionService = (ConfigurableConversionService) basicRelationalConverter.getConversionService();
        conversionService.addConverter(new ClobConverter());
    }
}
