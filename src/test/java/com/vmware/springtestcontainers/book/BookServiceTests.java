package com.vmware.springtestcontainers.book;


import com.vmware.springtestcontainers.Utils;
import com.vmware.springtestcontainers.book.googlebooks.GoogleBooksClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTests {

    @Mock
    BookRepository bookRepository;

    @Mock
    GoogleBooksClient googleBooksClient;

    @Mock
    RedisTemplate<String, List<Book>> redisTemplate;

    @Mock
    ValueOperations<String, List<Book>> valueOperations;

    @InjectMocks
    BookService bookService;

    BookRandomizer bookRandomizer;

    @BeforeEach
    void setUp() {
        bookRandomizer = new BookRandomizer();
    }

    @Test
    void getFavoriteBooks_returnsAllBooksInRepository() {
        UUID id = UUID.randomUUID();
        List<Book> books = List.of(Book.builder()
            .id(id)
            .title("Extreme Programming Explained")
            .authorName("Kent Beck")
            .isbn13("9780201616415")
            .isbn10("0134051998")
            .publishDate(Utils.parseDate("1999-10-05"))
            .build());

        when(bookRepository.findAll()).thenReturn(books);

        assertThat(bookService.getFavoriteBooks()).isEqualTo(books);
    }

    @Test
    void saveFavoriteBook_savesABook_andReturnsSavedBook() {
        UUID id = UUID.randomUUID();
        Book book = Book.builder()
            .id(id)
            .title("Extreme Programming Explained")
            .authorName("Kent Beck")
            .isbn13("9780201616415")
            .isbn10("0134051998")
            .publishDate(Utils.parseDate("1999-10-05"))
            .build();

        when(bookRepository.save(any())).thenReturn(book);

        assertThat(bookService.saveFavoriteBook(book)).isEqualTo(Optional.of(book));
    }

    @Test
    void saveFavoriteBook_returnsAnEmptyOptional_ifSavedBookAlreadyExists() {
        UUID id = UUID.randomUUID();
        Book book = Book.builder()
            .id(id)
            .title("Extreme Programming Explained")
            .authorName("Kent Beck")
            .isbn13("9780201616415")
            .isbn10("0134051998")
            .publishDate(Utils.parseDate("1999-10-05"))
            .build();

        when(bookRepository.findBookByIsbn13(any())).thenReturn(Optional.of(book));

        assertThat(bookService.saveFavoriteBook(book)).isEmpty();
    }

    @Test
    void updateFavoriteBook_savesABook_andReturnsSavedBook() {
        UUID id = UUID.randomUUID();
        Book book = Book.builder()
            .id(id)
            .title("Extreme Programming Explained")
            .authorName("Kent Beck")
            .isbn13("9780201616415")
            .isbn10("0134051998")
            .publishDate(Utils.parseDate("1999-10-05"))
            .build();

        when(bookRepository.findBookByIsbn13(any())).thenReturn(Optional.of(book));

        when(bookRepository.save(any())).thenReturn(book);

        assertThat(bookService.updateFavoriteBook(book)).isEqualTo(Optional.of(book));
    }

    @Test
    void updateFavoriteBook_returnsAnEmptyOptional_ifSavedBookAlreadyExists() {
        UUID id = UUID.randomUUID();
        Book book = Book.builder()
            .id(id)
            .title("Extreme Programming Explained")
            .authorName("Kent Beck")
            .isbn13("9780201616415")
            .isbn10("0134051998")
            .publishDate(Utils.parseDate("1999-10-05"))
            .build();

        when(bookRepository.findBookByIsbn13(any())).thenReturn(Optional.empty());

        assertThat(bookService.updateFavoriteBook(book)).isEmpty();
    }

    @Test
    void searchBook_cachesBooksWhenNotAlreadyCached() {
        List<Book> expected = bookRandomizer.randomBooks(2);

        when(googleBooksClient.searchBooksByTitle(any()))
            .thenReturn(expected);
        when(redisTemplate.opsForValue())
            .thenReturn(valueOperations);
        when(valueOperations.get(any()))
            .thenReturn(null);

        List<Book> books = bookService.searchBooks("some title");

        verify(valueOperations).set("some title", expected);
        assertThat(books).isEqualTo(expected);
    }

    @Test
    void searchBook_returnsCachedResultsWhenTheyAreAvailable() {
        List<Book> expected = bookRandomizer.randomBooks(2);

        when(redisTemplate.opsForValue())
            .thenReturn(valueOperations);
        when(valueOperations.get(any()))
            .thenReturn(expected);

        bookService.searchBooks("some title");

        verify(googleBooksClient, times(0)).searchBooksByTitle(anyString());
        verify(valueOperations, times(0)).set(anyString(), any());
    }
}