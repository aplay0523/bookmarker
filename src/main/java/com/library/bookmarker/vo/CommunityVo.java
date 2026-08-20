package com.library.bookmarker.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CommunityVo {

    private Long id;
    private Long userId;
    private String title;
    private String content;
    private Integer isbn;
    private Integer viewCount;
    private LocalDate createDt;
    private LocalDate updateDt;

    private String email;
    private String name;
}
