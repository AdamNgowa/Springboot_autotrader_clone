package com.autotrader.backend;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

// Declares ONE real PostgreSQL container for tests. Same major version as docker-compose.yml.
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    // @ServiceConnection makes Spring Boot point spring.datasource.* at this container automatically,
    // so no URL, username or password is configured by hand.
    // @Bean, @ServiceConnection,PostgreSQLContainer<?> postgresContainer() - This tells springboot this container,
    // provides the database connection for the test application
    @Bean
    @ServiceConnection
     PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:17"));
    }
}