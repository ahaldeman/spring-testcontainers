package com.vmware.springtestcontainers.book.googlebooks;

import com.vmware.springtestcontainers.Utils;
import com.vmware.springtestcontainers.book.Book;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Builder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoogleBooksItem {
    GoogleBooksVolumeInfo volumeInfo;

    public Book toBook() {
        return Book.builder()
            .title(volumeInfo.getTitle())
            .authorName(extractAuthor())
            .isbn13(extractIsbn("ISBN_13"))
            .isbn10(extractIsbn("ISBN_10"))
            .publishDate(Utils.parseDate(volumeInfo.getPublishedDate()))
            .build();
    }

    private String extractAuthor() {
        if (Objects.nonNull(volumeInfo.getAuthors())) {
            return volumeInfo.getAuthors().size() > 0 ? volumeInfo.getAuthors().get(0) : "No author found";
        } else {
            return "No author found";
        }
    }

    private String extractIsbn(String type) {
        return volumeInfo.getIndustryIdentifiers()
            .stream()
            .filter(identifier -> identifier.getType().equals(type))
            .map(GoogleBooksIndustryIdentifier::getIdentifier)
            .reduce("", (empty, isbn) -> empty + isbn);
    }
}
