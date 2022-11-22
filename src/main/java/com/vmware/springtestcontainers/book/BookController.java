package com.vmware.springtestcontainers.book;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
@AllArgsConstructor
public class BookController {

    BookService bookService;

    @GetMapping("/book/favorites")
    public ResponseEntity<BooksResponse> getFavoriteBooks() {
        return ResponseEntity.ok(BooksResponse.builder()
            .books(bookService.getFavoriteBooks())
            .build());
    }

    @PostMapping("/book/favorite")
    public ResponseEntity<BookResponse> saveFavoriteBook(@RequestBody Book book) {
        return bookService.saveFavoriteBook(book)
            .map(savedBook -> new ResponseEntity<>(BookResponse.builder()
                .book(savedBook)
                .build(), HttpStatus.CREATED))
            .orElse(ResponseEntity.badRequest().body(BookResponse.builder()
                .error("Book already exists")
                .build()));
    }

    @PutMapping("/book/favorite")
    public ResponseEntity<BookResponse> updateFavoriteBook(@RequestBody Book book) {
        return bookService.updateFavoriteBook(book)
            .map(updatedBook -> new ResponseEntity<>(BookResponse.builder()
                .book(updatedBook)
                .build(), HttpStatus.OK))
            .orElse(ResponseEntity.badRequest().body(BookResponse.builder()
                .error("Book does not exist")
                .build()));
    }

    @DeleteMapping("/book")
    public void deleteAll() {
        bookService.deleteAll();
    }

    @GetMapping("/book/search")
    public ResponseEntity<BooksResponse> searchBooks(@RequestParam String title) {
        return ResponseEntity.ok(BooksResponse.builder()
            .books(bookService.searchBooks(title))
            .build());
    }
}
