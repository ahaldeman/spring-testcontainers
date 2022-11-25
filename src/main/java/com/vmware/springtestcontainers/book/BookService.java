package com.vmware.springtestcontainers.book;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BookService {

    BookRepository bookRepository;

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
}
