package com.vmware.springtestcontainers.book;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BooksResponse {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<Book> books;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String error;
}
