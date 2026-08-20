package com.library.bookmarker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookExistDto {
    private String hasBook;
    private String loanAvailable;

    public static BookExistDto notFound() {
        return new BookExistDto("N", "N");
    }

}
