package com.vmware.springtestcontainers.book.googlebooks;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Builder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoogleBooksVolumeInfo {
    String title;
    List<String> authors;
    String publishedDate;
    List<GoogleBooksIndustryIdentifier> industryIdentifiers;
}
