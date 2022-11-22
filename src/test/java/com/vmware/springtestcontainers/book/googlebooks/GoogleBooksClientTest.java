package com.vmware.springtestcontainers.book.googlebooks;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.vmware.springtestcontainers.book.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@WireMockTest
class GoogleBooksClientTest {

    GoogleBooksClient googleBooksClient;

    WireMock wireMock;

    GoogleBooksItemRandomizer googleBooksItemRandomizer;

    @BeforeEach
    void setUp(WireMockRuntimeInfo wireMockRuntimeInfo) {
        wireMock = wireMockRuntimeInfo.getWireMock();
        googleBooksClient = new GoogleBooksClient(
            String.format("http://localhost:%s", wireMockRuntimeInfo.getHttpPort())
        );
        googleBooksItemRandomizer = new GoogleBooksItemRandomizer();
    }

    @Test
    void searchBooks_returnsSearchResults() {
        GoogleBooksSearchResponse mockResponse = GoogleBooksSearchResponse.builder()
            .items(googleBooksItemRandomizer.randomGoogleBooksItemList(2))
            .build();

        wireMock.register(stubFor(get(anyUrl())
            .willReturn(ResponseDefinitionBuilder.okForJson(mockResponse))));

        List<Book> response = googleBooksClient.searchBooksByTitle("some title");

        List<Book> expected = mockResponse.getItems().stream()
            .map(GoogleBooksItem::toBook)
            .collect(Collectors.toList());

        assertThat(response).isEqualTo(expected);
    }

    @Test
    void searchBooks_searchesWithTheCorrectSearchParameters() {
        GoogleBooksSearchResponse expectedResponse = GoogleBooksSearchResponse.builder()
            .items(googleBooksItemRandomizer.randomGoogleBooksItemList(2))
            .build();

        wireMock.register(stubFor(get(anyUrl())
            .willReturn(ResponseDefinitionBuilder.okForJson(expectedResponse))));

        googleBooksClient.searchBooksByTitle("some title");

        String expectedPath = "/books/v1/volumes?q=intitle:some%20title&maxResults=40&startIndex=0";
        wireMock
            .verifyThat(getRequestedFor(urlEqualTo(expectedPath)));
    }

    @Test
    void searchBooks_throwsExceptionWhenStatusIsNot200() {
        wireMock.register(stubFor(get(anyUrl())
            .willReturn(aResponse().withStatus(400))));

        assertThatThrownBy(() -> googleBooksClient.searchBooksByTitle("some title"))
            .hasMessage("Failed to call Google Books API");
    }

    @Test
    void searchBooks_searchesThroughPaginatedResults() {
        GoogleBooksSearchResponse firstPageResponse = GoogleBooksSearchResponse.builder()
            .items(googleBooksItemRandomizer.randomGoogleBooksItemList(40))
            .build();

        GoogleBooksSearchResponse secondPageResponse = GoogleBooksSearchResponse.builder()
            .items(googleBooksItemRandomizer.randomGoogleBooksItemList(40))
            .build();

        GoogleBooksSearchResponse thirdPageResponse = GoogleBooksSearchResponse.builder()
            .items(googleBooksItemRandomizer.randomGoogleBooksItemList(39))
            .build();

        wireMock.register(stubFor(get(urlEqualTo("/books/v1/volumes?q=intitle:some%20title&maxResults=40&startIndex=0"))
            .willReturn(ResponseDefinitionBuilder.okForJson(firstPageResponse))));

        wireMock.register(stubFor(get(urlEqualTo("/books/v1/volumes?q=intitle:some%20title&maxResults=40&startIndex=40"))
            .willReturn(ResponseDefinitionBuilder.okForJson(secondPageResponse))));

        wireMock.register(stubFor(get(urlEqualTo("/books/v1/volumes?q=intitle:some%20title&maxResults=40&startIndex=80"))
            .willReturn(ResponseDefinitionBuilder.okForJson(thirdPageResponse))));

        List<Book> response = googleBooksClient.searchBooksByTitle("some title");

        assertThat(response).hasSize(119);
    }

    @Test
    void searchBooks_returnsEmptyItemsWhenNoResults() {
        GoogleBooksSearchResponse emptyResponse = GoogleBooksSearchResponse.builder().build();

        wireMock.register(stubFor(get(anyUrl())
            .willReturn(ResponseDefinitionBuilder.okForJson(emptyResponse))));

        List<Book> response = googleBooksClient.searchBooksByTitle("title not found");

        assertThat(response).isEmpty();
    }

    @Test
    void searchBooks_filtersResultsWithNoAuthor() {
        List<GoogleBooksItem> googleBooksItems = googleBooksItemRandomizer.randomGoogleBooksItemList(3);

        googleBooksItems.get(0).getVolumeInfo().setAuthors(null);
        googleBooksItems.get(1).getVolumeInfo().setAuthors(List.of());

        GoogleBooksSearchResponse mockResponse = GoogleBooksSearchResponse.builder()
            .items(googleBooksItems)
            .build();

        wireMock.register(stubFor(get(anyUrl())
            .willReturn(ResponseDefinitionBuilder.okForJson(mockResponse))));

        List<Book> response = googleBooksClient.searchBooksByTitle("some title");

        assertThat(response).hasSize(1);
    }

    @Test
    void searchBooks_filtersResultsWithNoIsbn() {
        List<GoogleBooksItem> googleBooksItems = googleBooksItemRandomizer.randomGoogleBooksItemList(4);

        googleBooksItems.get(0).getVolumeInfo().setIndustryIdentifiers(null);
        googleBooksItems.get(1).getVolumeInfo().setIndustryIdentifiers(List.of());
        googleBooksItems.get(2).getVolumeInfo().setIndustryIdentifiers(List.of(
            GoogleBooksIndustryIdentifier.builder()
                .identifier("323423423")
                .type("OTHER")
                .build()
        ));

        GoogleBooksSearchResponse mockResponse = GoogleBooksSearchResponse.builder()
            .items(googleBooksItems)
            .build();

        wireMock.register(stubFor(get(anyUrl())
            .willReturn(ResponseDefinitionBuilder.okForJson(mockResponse))));

        List<Book> response = googleBooksClient.searchBooksByTitle("some title");

        assertThat(response).hasSize(1);
    }

    @Test
    void searchBooks_filtersResultsWithInvalidDate() {
        List<GoogleBooksItem> googleBooksItems = googleBooksItemRandomizer.randomGoogleBooksItemList(4);

        googleBooksItems.get(0).getVolumeInfo().setPublishedDate(null);
        googleBooksItems.get(1).getVolumeInfo().setPublishedDate("2022-12");
        googleBooksItems.get(2).getVolumeInfo().setPublishedDate("2022");

        GoogleBooksSearchResponse mockResponse = GoogleBooksSearchResponse.builder()
            .items(googleBooksItems)
            .build();

        wireMock.register(stubFor(get(anyUrl())
            .willReturn(ResponseDefinitionBuilder.okForJson(mockResponse))));

        List<Book> response = googleBooksClient.searchBooksByTitle("some title");

        assertThat(response).hasSize(1);
    }
}