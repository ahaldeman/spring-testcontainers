package com.vmware.springtestcontainers.book.googlebooks;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Builder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoogleBooksSearchResponse {
    List<GoogleBooksItem> items;
}
