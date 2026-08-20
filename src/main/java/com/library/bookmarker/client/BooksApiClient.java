package com.library.bookmarker.client;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.bookmarker.dto.ApiResponseDto;
import com.library.bookmarker.dto.BookExistDto;
import com.library.bookmarker.dto.BookManiaDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class BooksApiClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${library.api.key}")
    private String libraryKey;

    // 도서 책 전체 리스트 api 조회
    public ApiResponseDto searchBooks(String keyword, int pageNo) {
        try {
            String response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("http")
                            .host("data4library.kr")
                            .path("/api/srchBooks")
                            .queryParam("authKey", libraryKey)
                            .queryParam("keyword", keyword)
                            .queryParam("pageNo", pageNo)
                            .queryParam("pageSize", 10)
                            .queryParam("format", "json")
                            .build())
                    .retrieve()
                    .body(String.class);
            return new ApiResponseDto(response);
        } catch (Exception e) {
            log.warn("[BooksApiClient.searchBooks] 도서 조회 실패 - keyword : {}, pageNo : {}, error : {}", keyword, pageNo, e);
            return new ApiResponseDto(null);
        }
    }

    // 도서 책 상세정보 및 이용분석 조회
    @Cacheable(value = "bookDetail", key = "#p0", condition = "#p0 != null")
    public Map<String, Object> searchBookDetail(String isbn) {
        Map<String, Object> responseMap = new HashMap<>();
        try {
            String response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("http")
                            .host("data4library.kr")
                            .path("/api/usageAnalysisList")
                            .queryParam("authKey", libraryKey)
                            .queryParam("isbn13", isbn)
                            .queryParam("format", "json")
                            .build())
                    .retrieve()
                    .body(String.class);

            JsonNode jsonNode = objectMapper.readTree(response);
            JsonNode responseNode = jsonNode.get("response");

            if (responseNode.has("errCode") || responseNode.has("error")) {
                String errCode = responseNode.path("errCode").asText("UNKNOWN_ERR");
                String errorMsg = responseNode.path("error").asText("API Error");
                log.warn("[BooksApiClient.getBookDetail] API 응답 에러 - isbn : {}, error : {} {}", isbn, errCode, errorMsg);
                return null;
            }
            // JsonNode 객체 Map<s,o> 치환 (JsonNode는 타임리프와 규격이 달라 넘기면 인식불가)
            responseMap = objectMapper.convertValue(responseNode, new TypeReference<Map<String, Object>>() {});

            return responseMap;
        } catch (Exception e) {
            log.warn("[BooksApiClient.getBookDetail] 도서 상세 조회 실패 - isbn : {}, error : {}", isbn, e);
            throw new RuntimeException(e);
        }
    }

    // 특정 도서 책 연관(마니아) 조회
    @Cacheable(value = "BookImg", key = "#p0", condition = "#p0 != null", unless = "#result == null || #result.bookImageURL == ''")
    public BookManiaDto searchBookImg(String isbn) {
        try {
            String response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("data4library.kr")
                            .path("/api/srchDtlList")
                            .queryParam("authKey", libraryKey)
                            .queryParam("isbn13", isbn)
                            .queryParam("format", "json")
                            .build())
                    .retrieve()
                    .body(String.class);

            JsonNode jsonNode = objectMapper.readTree(response);
            JsonNode responseNode = jsonNode.get("response");

            if (responseNode.has("errCode") || responseNode.has("error")) {
                String errCode = responseNode.path("errCode").asText("UNKNOWN_ERR");
                String errorMsg = responseNode.path("error").asText("API Error");
                log.warn("[BooksApiClient.searchBookImg] API 응답 에러 - isbn : {}, error : {} {}", isbn, errCode, errorMsg);
                return new BookManiaDto(isbn, "");
            }

            JsonNode detailNode = responseNode.get("detail");
            JsonNode bookDetail = detailNode.get(0).path("book");

            String isbn13 = bookDetail.path("isbn13").asText(isbn);
            String bookImageURL = bookDetail.path("bookImageURL").asText("");
            return new BookManiaDto(isbn13, bookImageURL);
        } catch (Exception e) {
            log.warn("[BooksApiClient.searchBookImg] 도서 상세 조회 실패 - isbn : {}, error : {}", isbn, e);
            throw new RuntimeException(e);
        }
    }
}
