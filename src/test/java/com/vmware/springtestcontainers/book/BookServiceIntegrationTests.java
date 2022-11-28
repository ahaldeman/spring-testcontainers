package com.vmware.springtestcontainers.book;

import com.vmware.springtestcontainers.SQLServerTestBase;
import com.vmware.springtestcontainers.book.googlebooks.GoogleBooksClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@ContextConfiguration(initializers = {BookServiceIntegrationTests.Initializer.class})
public class BookServiceIntegrationTests extends SQLServerTestBase {

    BookRandomizer bookRandomizer;

    @MockBean
    GoogleBooksClient googleBooksClient;

    @Autowired
    BookService bookService;

    @Container
    private static final GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:alpine"))
        .withExposedPorts(6379);

    static class Initializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                "spring.redis.port=" + redis.getMappedPort(6379)
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    @BeforeEach
    void setUp() {
        bookRandomizer = new BookRandomizer();
    }

    @Test
    void searchBooks_byTitle_returnsResults() {
        when(googleBooksClient.searchBooksByTitle(anyString()))
            .thenReturn(bookRandomizer.randomBooks(2));

        List<Book> results = bookService.searchBooks("extreme programming explained");
        assertThat(results).hasSize(2);
    }
}
