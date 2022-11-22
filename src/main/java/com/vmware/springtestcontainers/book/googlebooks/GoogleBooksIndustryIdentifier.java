package com.vmware.springtestcontainers.book.googlebooks;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoogleBooksIndustryIdentifier {
    String type;
    String identifier;
}
