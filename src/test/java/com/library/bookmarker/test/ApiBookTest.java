package com.library.bookmarker.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class ApiBookTest {

    private final String libraryKey = "4c4758f05d057576af76b56cc7085a08e008d8b16bbbf407b7aaa39684358801";
    private final RestClient restClient = RestClient.create();

    @Test
    void 도서_상세정보_동기조회() {
        System.out.println("--- TEST START - for 동기 싱글 스레드 조회 ---");
        List<String> isbnList = List.of(
                "9788940908808", "9788905011666", "9788989856344", "9788956000329", "9788953535534",
                "9788956925127", "9788995710487", "9788945120106", "9788976843180", "9788953535602"
        );

        long totalStartTime = System.currentTimeMillis();
        List<Map<String, String>> resultList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        for (String isbn : isbnList) {
            long apiStartTime = System.currentTimeMillis();
            try {
                String targetUrl = "https://data4library.kr/api/srchDtlList?authKey=" + libraryKey + "&isbn13=" + isbn + "&format=json";
                String response = restClient.get().uri(targetUrl).retrieve().body(String.class);

                JsonNode jsonNode = objectMapper.readTree(response);
                JsonNode bookDetail = jsonNode.get("response").get("detail").get(0).path("book");

                Map<String, String> bookMap = new HashMap<>();
                bookMap.put("isbn13", bookDetail.path("isbn13").asText(isbn));
                bookMap.put("bookImageURL", bookDetail.path("bookImageURL").asText(""));
                resultList.add(bookMap);

                long apiEndTime = System.currentTimeMillis();
                System.out.println("-> [싱글] ISBN: " + isbn + " | " + (apiEndTime - apiStartTime) + "ms");
            } catch (Exception e) {

            }
        }
        long totalEndTime = System.currentTimeMillis();
        System.out.println("--- 싱글 스레드 소요 시간 : " + (totalEndTime - totalStartTime) + "ms ---");

        assertNotNull(resultList);
        assertEquals(10, resultList.size());
    }

    @Test
    void 도서_상세정보_비동기조회() {
        System.out.println("--- TEST START - CompletableFuture 비동기 멀티 스레드 조회 ---");

        List<String> isbnList = List.of(
                "9788940908808", "9788905011666", "9788989856344", "9788956000329", "9788953535534",
                "9788956925127", "9788995710487", "9788945120106", "9788976843180", "9788953535602"
        );

        long totalStartTime = System.currentTimeMillis();
        ObjectMapper objectMapper = new ObjectMapper();

        List<CompletableFuture<Map<String, String>>> futures = isbnList.stream()
                .map(isbn -> CompletableFuture.supplyAsync(() -> {
                    long apiStartTime = System.currentTimeMillis();
                    try {
                        String targetUrl = "https://data4library.kr/api/srchDtlList?authKey=" + libraryKey + "&isbn13=" + isbn + "&format=json";
                        String response = restClient.get().uri(targetUrl).retrieve().body(String.class);

                        JsonNode jsonNode = objectMapper.readTree(response);
                        JsonNode bookDetail = jsonNode.get("response").get("detail").get(0).path("book");

                        Map<String, String> bookMap = new HashMap<>();
                        bookMap.put("isbn13", bookDetail.path("isbn13").asText(isbn));
                        bookMap.put("bookImageURL", bookDetail.path("bookImageURL").asText(""));

                        long apiEndTime = System.currentTimeMillis();
                        System.out.println("-> [" + Thread.currentThread().getName() + "] ISBN: " + isbn + " | " + (apiEndTime - apiStartTime) + "ms");
                        return bookMap;
                    } catch (Exception e) {
                        Map<String, String> errorMap = new HashMap<>();
                        return errorMap;
                    }
                }))
                .toList();
        List<Map<String, String>> resultList = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream().map(CompletableFuture::join).toList())
                .join();
        long totalEndTime = System.currentTimeMillis();
        System.out.println("--- 멀티 스레드 소요 시간 : " + (totalEndTime - totalStartTime) + "ms ---");

        assertNotNull(resultList);
        assertEquals(10, resultList.size());
    }
}
