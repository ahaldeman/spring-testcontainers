package com.vmware.springtestcontainers.book.googlebooks;

import com.vmware.springtestcontainers.Utils;
import com.vmware.springtestcontainers.book.Book;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Slf4j
public class GoogleBooksClient {

    private final WebClient webClient;

    private static final Integer PAGE_SIZE = 40;

    public GoogleBooksClient(
        @Value("${google-books-base-url}") String googleBooksBaseUrl
    ) {
        this.webClient = WebClient.builder()
            .baseUrl(googleBooksBaseUrl)
            .build();
    }

    private GoogleBooksSearchResponse getBooksByTitleAndIndex(String title, Integer startIndex) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/books/v1/volumes")
                .queryParam("q", "intitle:" + title)
                .queryParam("maxResults", PAGE_SIZE)
                .queryParam("startIndex", startIndex)
                .build()
            )
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .onStatus(HttpStatus::isError,
                error -> Mono.error(new RuntimeException("Failed to call Google Books API")))
            .bodyToMono(GoogleBooksSearchResponse.class)
            .block();
    }

    private boolean bookIsValid(GoogleBooksItem googleBooksItem) {
        GoogleBooksVolumeInfo volumeInfo = googleBooksItem.getVolumeInfo();

        boolean bookHasAuthor = Objects.nonNull(volumeInfo.getAuthors()) && volumeInfo.getAuthors().size() > 0;
        boolean bookHasIsbn = Objects.nonNull(volumeInfo.getIndustryIdentifiers())
            && volumeInfo.getIndustryIdentifiers().stream().anyMatch(identifier -> "ISBN_10".equals(identifier.getType()) || "ISBN_13".equals(identifier.getType()));
        boolean bookHasValidDate = Objects.nonNull(volumeInfo.getPublishedDate())
            && Utils.isDateInISOFormat(googleBooksItem.getVolumeInfo().getPublishedDate());

        return bookHasAuthor && bookHasIsbn && bookHasValidDate;
    }

    public List<Book> searchBooksByTitle(String title) {
        boolean endOfResultsNotMet = true;
        int startIndex = 0;
        GoogleBooksSearchResponse aggregateResponse = GoogleBooksSearchResponse.builder()
            .items(new ArrayList<>())
            .build();

        while (endOfResultsNotMet) {
            GoogleBooksSearchResponse response = getBooksByTitleAndIndex(title, startIndex);
            if (Objects.nonNull(response.getItems())) {
                aggregateResponse.getItems().addAll(response.getItems());
                endOfResultsNotMet = PAGE_SIZE.equals(response.getItems().size());
                startIndex += PAGE_SIZE;
            } else {
                endOfResultsNotMet = false;
            }
        }

        return aggregateResponse.getItems().stream()
            .filter(this::bookIsValid)
            .map(GoogleBooksItem::toBook)
            .collect(Collectors.toList());
    }
}