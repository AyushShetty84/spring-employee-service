package springTesting.SpringTestingPractice;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.containers.PostgreSQLContainer;

@TestConfiguration
public class TestContainerConfiguration {
    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {

        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));
    }
}
