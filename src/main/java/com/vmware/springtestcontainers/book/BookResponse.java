package com.vmware.springtestcontainers.book;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookResponse {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Book book;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String error;
}
