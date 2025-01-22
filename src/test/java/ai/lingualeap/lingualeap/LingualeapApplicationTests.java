package ai.lingualeap.lingualeap;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootTest
@Testcontainers
class LingualeapApplicationTests {
    private static final Logger LOGGER = LoggerFactory.getLogger(LingualeapApplicationTests.class);
    private static final String TEST_DATABASE_NAME = "lingualeap_test";
    private static final String TEST_CREDENTIALS = "test";

    @Container
    private static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:15-alpine")
                    .withDatabaseName(TEST_DATABASE_NAME)
                    .withUsername(TEST_CREDENTIALS)
                    .withPassword(TEST_CREDENTIALS);

    @Autowired
    private org.springframework.context.ApplicationContext context;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Test
    void contextLoads() {
        assert context != null;
        LOGGER.info("ApplicationContext loaded successfully: {}", context.getDisplayName());
    }
}
