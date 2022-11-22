package com.vmware.springtestcontainers.book.googlebooks;

import net.datafaker.Faker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class GoogleBooksItemRandomizer {

    private final Faker faker;

    public GoogleBooksItemRandomizer() {
        faker = new Faker();
    }

    private GoogleBooksItem randomGoogleBooksItem() {
        String publishedDate = new SimpleDateFormat("yyyy-MM-dd")
            .format(faker.date().birthday());

        return GoogleBooksItem.builder()
            .volumeInfo(GoogleBooksVolumeInfo.builder()
                .title(faker.book().title())
                .publishedDate(publishedDate)
                .authors(List.of(
                    faker.book().author(),
                    faker.book().author()
                ))
                .industryIdentifiers(List.of(
                    GoogleBooksIndustryIdentifier.builder()
                        .type("ISBN_13")
                        .identifier(faker.number().digits(13))
                        .build(),
                    GoogleBooksIndustryIdentifier.builder()
                        .type("ISBN_10")
                        .identifier(faker.number().digits(10))
                        .build()
                ))
                .build())
            .build();
    }

    public List<GoogleBooksItem> randomGoogleBooksItemList(long size) {
        return Stream.generate(this::randomGoogleBooksItem)
            .limit(size)
            .collect(Collectors.toList());
    }
}
