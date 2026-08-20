package com.library.bookmarker.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LibraryVo {
    private String libCode;
    private String libName;
    private String latitude;
    private String longitude;
    private String libData;
    private String libSido;
    private String libSigungu;
    private LocalDateTime updateDt;
    private String libAddress;
    private String libTel;

    // 도서 보유 상태
    private String loanAvailable = "N";

}
