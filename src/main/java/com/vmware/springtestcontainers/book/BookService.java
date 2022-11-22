package com.vmware.springtestcontainers.book;

import com.vmware.springtestcontainers.book.googlebooks.GoogleBooksClient;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BookService {

    BookRepository bookRepository;

    GoogleBooksClient googleBooksClient;

    RedisTemplate<String, List<Book>> redisTemplate;

    public List<Book> getFavoriteBooks() {
        return bookRepository.findAll();
    }

    public Optional<Book> saveFavoriteBook(Book book) {
        Optional<Book> existingBook = bookRepository.findBookByIsbn(book.getIsbn());

        if (existingBook.isPresent()) {
            return Optional.empty();
        }

        return Optional.of(bookRepository.save(book));
    }

    public Optional<Book> updateFavoriteBook(Book book) {
        Optional<Book> existingBook = bookRepository.findBookByIsbn(book.getIsbn());

        if (existingBook.isPresent()) {
            return Optional.of(bookRepository.save(book));
        }

        return Optional.empty();
    }

    public void deleteAll() {
        bookRepository.deleteAll();
    }

    public List<Book> searchBooks(String title) {
        List<Book> cachedBooks = redisTemplate.opsForValue().get(title);

        if (Objects.nonNull(cachedBooks)) {
            return cachedBooks;
        } else {
            List<Book> books = googleBooksClient.searchBooksByTitle(title);
            redisTemplate.opsForValue().set(title, books);
            return books;
        }

    }
}
