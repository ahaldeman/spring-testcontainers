package com.vmware.springtestcontainers.book;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.springtestcontainers.Utils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookController.class)
class BookControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookService bookService;

    @Test
    void getFavoriteBooks_returnsAllBooks() throws Exception {
        UUID id = UUID.randomUUID();
        List<Book> books = List.of(Book.builder()
            .id(id)
            .title("Extreme Programming Explained")
            .authorName("Kent Beck")
            .isbn13("9780201616415")
            .isbn10("0134051998")
            .publishDate(Utils.parseDate("1999-10-05"))
            .build());

        when(bookService.getFavoriteBooks()).thenReturn(books);

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/book/favorites"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.books[0].id", is(id.toString())))
            .andExpect(jsonPath("$.books[0].title", is("Extreme Programming Explained")))
            .andExpect(jsonPath("$.books[0].authorName", is("Kent Beck")))
            .andExpect(jsonPath("$.books[0].isbn13", is("9780201616415")))
            .andExpect(jsonPath("$.books[0].isbn10", is("0134051998")))
            .andExpect(jsonPath("$.books[0].publishDate", is("1999-10-05")));
    }

    @Test
    void saveFavoriteBook_savesABook_andReturnsResponseWithSavedBook() throws Exception {
        UUID id = UUID.randomUUID();
        Book book = Book.builder()
            .id(id)
            .title("Extreme Programming Explained")
            .authorName("Kent Beck")
            .isbn13("9780201616415")
            .isbn10("0134051998")
            .publishDate(Utils.parseDate("1999-10-05"))
            .build();

        when(bookService.saveFavoriteBook(any())).thenReturn(Optional.of(book));

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/book/favorite")
                .content(objectMapper.writeValueAsString(book))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.book.id", is(id.toString())))
            .andExpect(jsonPath("$.book.title", is("Extreme Programming Explained")))
            .andExpect(jsonPath("$.book.authorName", is("Kent Beck")))
            .andExpect(jsonPath("$.book.isbn13", is("9780201616415")))
            .andExpect(jsonPath("$.book.isbn10", is("0134051998")))
            .andExpect(jsonPath("$.book.publishDate", is("1999-10-05")));
    }

    @Test
    void saveFavoriteBook_returnsBadRequest_ifBookAlreadyExists() throws Exception {
        UUID id = UUID.randomUUID();
        Book book = Book.builder()
            .id(id)
            .title("Extreme Programming Explained")
            .authorName("Kent Beck")
            .isbn13("9780201616415")
            .isbn10("0134051998")
            .publishDate(Utils.parseDate("1999-10-05"))
            .build();

        when(bookService.saveFavoriteBook(any())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/book/favorite")
                .content(objectMapper.writeValueAsString(book))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error", is("Book already exists")));
    }

    @Test
    void updateFavoriteBook_savesUpdateToBook_andReturnsResponseWithSavedBook() throws Exception {
        UUID id = UUID.randomUUID();
        Book book = Book.builder()
            .id(id)
            .title("Extreme Programming Explained")
            .authorName("Kent Beck")
            .isbn13("9780201616415")
            .isbn10("0134051998")
            .publishDate(Utils.parseDate("1999-10-05"))
            .build();

        when(bookService.updateFavoriteBook(any())).thenReturn(Optional.of(book));

        mockMvc.perform(MockMvcRequestBuilders.put("/v1/book/favorite")
                .content(objectMapper.writeValueAsString(book))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.book.id", is(id.toString())))
            .andExpect(jsonPath("$.book.title", is("Extreme Programming Explained")))
            .andExpect(jsonPath("$.book.authorName", is("Kent Beck")))
            .andExpect(jsonPath("$.book.isbn13", is("9780201616415")))
            .andExpect(jsonPath("$.book.isbn10", is("0134051998")))
            .andExpect(jsonPath("$.book.publishDate", is("1999-10-05")));
    }

    @Test
    void saveFavoriteBook_returnsNotFound_ifBookDoesNotExists() throws Exception {
        UUID id = UUID.randomUUID();
        Book book = Book.builder()
            .id(id)
            .title("Extreme Programming Explained")
            .authorName("Kent Beck")
            .isbn13("9780201616415")
            .isbn10("0134051998")
            .publishDate(Utils.parseDate("1999-10-05"))
            .build();

        when(bookService.updateFavoriteBook(any())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.put("/v1/book/favorite")
                .content(objectMapper.writeValueAsString(book))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error", is("Book does not exist")));
    }
}
