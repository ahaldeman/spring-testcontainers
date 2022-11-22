package com.vmware.springtestcontainers.book;

import net.datafaker.Faker;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BookRandomizer {

    private final Faker faker;

    public BookRandomizer() {
        faker = new Faker();
    }

    private Book randomBook() {
        return Book.builder()
            .id(UUID.randomUUID())
            .title(faker.book().title())
            .authorName(faker.book().author())
            .isbn(faker.number().digits(13))
            .publishDate(faker.date().birthday())
            .build();
    }

    public List<Book> randomBooks(int size) {
        return Stream.generate(this::randomBook)
            .limit(size)
            .collect(Collectors.toList());
    }
}
