package com.vmware.springtestcontainers.book;

import com.vmware.springtestcontainers.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class BookRepositoryTests {

    @Autowired
    BookRepository bookRepository;

    @BeforeEach
    void cleanUp() {
        bookRepository.deleteAll();
    }

    @Test
    void bookRepository_shouldInsertAndRetrieveABook() throws Exception {
        Book book = Book.builder()
            .title("Extreme Programming Explained")
            .authorName("Kent Beck")
            .isbn("9780201616415")
            .publishDate(Utils.parseDate("1999-10-05"))
            .build();

        bookRepository.save(book);

        List<Book> books = bookRepository.findAll();

        assertThat(books).isNotEmpty();

        Book expectedBook = book.toBuilder()
            .id(books.get(0).getId())
            .build();

        assertThat(books.get(0)).isEqualTo(expectedBook);
    }

    @Test
    void findBookByIsbn_returnsABook_ifItExists() throws Exception {
        Book book = Book.builder()
            .title("Extreme Programming Explained")
            .authorName("Kent Beck")
            .isbn("9780201616415")
            .publishDate(Utils.parseDate("1999-10-05"))
            .build();

        bookRepository.save(book);

        Optional<Book> foundBook = bookRepository.findBookByIsbn("9780201616415");

        assertThat(foundBook).isPresent();
        assertThat(foundBook.get()).isEqualTo(book);
    }

    @Test
    void findBookByIsbn_returnsEmpty_ifItDoesNotExist() throws Exception {
        Book book = Book.builder()
            .title("Extreme Programming Explained")
            .authorName("Kent Beck")
            .isbn("9780201616415")
            .publishDate(Utils.parseDate("1999-10-05"))
            .build();

        bookRepository.save(book);

        Optional<Book> foundBook = bookRepository.findBookByIsbn("123564894984");

        assertThat(foundBook).isEmpty();
    }
}
