package com.vmware.springtestcontainers.book;


import com.vmware.springtestcontainers.Utils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTests {

    @Mock
    BookRepository bookRepository;

    @InjectMocks
    BookService bookService;

    @Test
    void getFavoriteBooks_returnsAllBooksInRepository() throws Exception {
        UUID id = UUID.randomUUID();
        List<Book> books = List.of(Book.builder()
            .id(id)
            .title("Extreme Programming Explained")
            .authorName("Kent Beck")
            .isbn("9780201616415")
            .publishDate(Utils.parseDate("1999-10-05"))
            .build());

        when(bookRepository.findAll()).thenReturn(books);

        assertThat(bookService.getFavoriteBooks()).isEqualTo(books);
    }

    @Test
    void saveFavoriteBook_savesABook_andReturnsSavedBook() throws Exception {
        UUID id = UUID.randomUUID();
        Book book = Book.builder()
            .id(id)
            .title("Extreme Programming Explained")
            .authorName("Kent Beck")
            .isbn("9780201616415")
            .publishDate(Utils.parseDate("1999-10-05"))
            .build();

        when(bookRepository.save(any())).thenReturn(book);

        assertThat(bookService.saveFavoriteBook(book)).isEqualTo(Optional.of(book));
    }

    @Test
    void saveFavoriteBook_returnsAnEmptyOptional_ifSavedBookAlreadyExists() throws Exception {
        UUID id = UUID.randomUUID();
        Book book = Book.builder()
            .id(id)
            .title("Extreme Programming Explained")
            .authorName("Kent Beck")
            .isbn("9780201616415")
            .publishDate(Utils.parseDate("1999-10-05"))
            .build();

        when(bookRepository.findBookByIsbn(any())).thenReturn(Optional.of(book));

        assertThat(bookService.saveFavoriteBook(book)).isEmpty();
    }

    @Test
    void updateFavoriteBook_savesABook_andReturnsSavedBook() throws Exception {
        UUID id = UUID.randomUUID();
        Book book = Book.builder()
            .id(id)
            .title("Extreme Programming Explained")
            .authorName("Kent Beck")
            .isbn("9780201616415")
            .publishDate(Utils.parseDate("1999-10-05"))
            .build();

        when(bookRepository.findBookByIsbn(any())).thenReturn(Optional.of(book));

        when(bookRepository.save(any())).thenReturn(book);

        assertThat(bookService.updateFavoriteBook(book)).isEqualTo(Optional.of(book));
    }

    @Test
    void updateFavoriteBook_returnsAnEmptyOptional_ifSavedBookAlreadyExists() throws Exception {
        UUID id = UUID.randomUUID();
        Book book = Book.builder()
            .id(id)
            .title("Extreme Programming Explained")
            .authorName("Kent Beck")
            .isbn("9780201616415")
            .publishDate(Utils.parseDate("1999-10-05"))
            .build();

        when(bookRepository.findBookByIsbn(any())).thenReturn(Optional.empty());

        assertThat(bookService.updateFavoriteBook(book)).isEmpty();
    }
}