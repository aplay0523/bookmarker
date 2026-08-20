package com.library.bookmarker.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.bookmarker.dto.ApiResponseDto;
import com.library.bookmarker.dto.BookExistDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class LibraryApiClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${library.api.key}")
    private String libraryKey;

    // 도서관별 도서 책 보유 정보 조회
    @Cacheable(value = "bookExist", key = "#p0 + ':' + #p1", condition = "#p0 != null && #p1 != null")
    public BookExistDto bookExist(String libCode, String isbn) {
        log.info("[LibraryApiClient.bookExist] 도서관 API 조회 시작 - libCode : {}, isbn : {}", libCode, isbn);
        try {
            String response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("data4library.kr")
                            .path("/api/bookExist")
                            .queryParam("authKey", libraryKey)
                            .queryParam("libCode", libCode)
                            .queryParam("isbn13", isbn)
                            .queryParam("format", "json")
                            .build())
                    .retrieve()
                    .body(String.class);
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode responseNode = rootNode.path("response");

            if (responseNode.has("errCode") || responseNode.has("error")) {
                String errCode = responseNode.path("errCode").asText("UNKNOWN_ERR");
                String errorMsg = responseNode.path("error").asText("API Error");
                log.warn("[LibraryApiClient.bookExist] API 응답 에러 - libCode : {}, isbn : {}, error : {} {}",libCode, isbn, errCode, errorMsg);
                return BookExistDto.notFound();
            }
            JsonNode resultNode = responseNode.path("result");
            String hasBook = resultNode.path("hasBook").asText("N");
            String loanAvailable = resultNode.path("loanAvailable").asText("N");

            return new BookExistDto(hasBook, loanAvailable);
        } catch (Exception e) {
            log.warn("[LibraryApiClient.bookExist] 도서관 조회 실패 - libCode : {}, isbn : {}, error : {}", libCode, isbn, e);
            throw new RuntimeException(e);
        }
    }
}
