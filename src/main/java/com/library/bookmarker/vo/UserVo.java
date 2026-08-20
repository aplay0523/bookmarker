package com.library.bookmarker.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserVo {

    private Long id;
    private String email;
    private String name;
    private int age;
    private String provider;
    private String providerId;
    private String role;
    private LocalDate createDt;

}
