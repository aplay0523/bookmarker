package com.library.bookmarker.service;

import com.library.bookmarker.client.BooksApiClient;
import com.library.bookmarker.client.LibraryApiClient;
import com.library.bookmarker.dto.ApiResponseDto;
import com.library.bookmarker.dto.BookExistDto;
import com.library.bookmarker.dto.BookManiaDto;
import com.library.bookmarker.mapper.LibraryMapper;
import com.library.bookmarker.vo.LibraryVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Service
@RequiredArgsConstructor
    public class BooksService {

    private final LibraryMapper libraryMapper;
    private final BooksApiClient booksApiClient;
    private final LibraryApiClient libraryApiClient;

    @Qualifier("bookApiExecutor")
    private final Executor bookApiExecutor;

    // 도서 책 전체 리스트 api 조회
    public ApiResponseDto getSearchBooks(String keyword, int pageNo) {
        return booksApiClient.searchBooks(keyword, pageNo);
    }

    // 도서 책 상세정보 및 이용분석 조회
    public Map<String, Object> getBookDetail(String isbn) {
        return booksApiClient.searchBookDetail(isbn);
    }

    // 도서 연관(마니아) 이미지 조회
    public List<BookManiaDto> getBookImg(List<String> isbnList) {
        long totalStartTime = System.currentTimeMillis();

        if (isbnList == null || isbnList.isEmpty()) {
            return Collections.emptyList();
        }

        List<CompletableFuture<BookManiaDto>> futures = isbnList.stream()
                .map(isbn -> CompletableFuture.supplyAsync(() -> {
                    long apiStartTime = System.currentTimeMillis();

                    BookManiaDto dto = booksApiClient.searchBookImg(isbn);

                    long apiEndTime = System.currentTimeMillis();

                    log.debug("[{}] isbn: {} 조회 완료 -> {}ms",
                            Thread.currentThread().getName(), isbn, (apiEndTime - apiStartTime));
                    return dto;
                }, bookApiExecutor
                ).exceptionally(throwable -> {
                    log.warn("[BooksService.getBookImg] 도서 이미지 조회 실패 - isbn : {} {}", isbn, throwable.getMessage());
                    return new BookManiaDto(isbn, "");
                })).toList();

        List<BookManiaDto> resultList = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .toList())
                .join();
        long totalEndTime = System.currentTimeMillis();
        log.info("[도서관 보유 정보 조회] 도서관: {}개 - 총 소요 시간: {}ms",
                isbnList.size(), resultList.size(), (totalEndTime - totalStartTime));
        return resultList;
    }

    // 도서 보유 정보 조회 (도서관)
    public List<LibraryVo> getBookExistToLibraries(String address, String isbn) {
        long totalStartTime = System.currentTimeMillis();

        // 해당 주소에 맞는 도서관 조회
        List<LibraryVo> libraryList = libraryMapper.selectLibraryList("", address);

        if (libraryList.isEmpty()) {
            return Collections.emptyList();
        }

        // CompletableFuture 기반 병렬 API 호출 작업 생성
        List<CompletableFuture<LibraryVo>> futures = libraryList.stream()
                .map(library -> CompletableFuture.supplyAsync(() -> {

                    long apiStartTime = System.currentTimeMillis();

                    BookExistDto result = libraryApiClient.bookExist(library.getLibCode(), isbn);

                    long apiEndTime = System.currentTimeMillis();
                    log.debug("[{}] LibCode: {} 조회 완료 -> {}ms",
                            Thread.currentThread().getName(), library.getLibCode(), (apiEndTime - apiStartTime));

                    if ("Y".equals(result.getHasBook())) {
                        return library.toBuilder()
                                .loanAvailable(result.getLoanAvailable())
                                .build();
                    }
                    return null;
                }, bookApiExecutor))
                .toList();

        // 비동기 작업 null 제외 결과값 합치기
        List<LibraryVo> resultList = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .filter(Objects::nonNull)
                        .toList())
                .join();

        long totalEndTime = System.currentTimeMillis();
        log.info("[도서관 보유 정보 조회] 도서관: {}개 - 총 소요 시간: {}ms",
                libraryList.size(), resultList.size(), (totalEndTime - totalStartTime));

        return resultList;


    }
}
