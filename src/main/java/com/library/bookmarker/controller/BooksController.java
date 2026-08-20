package com.library.bookmarker.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.library.bookmarker.dto.ApiResponseDto;
import com.library.bookmarker.dto.BookManiaDto;
import com.library.bookmarker.service.BooksService;
import com.library.bookmarker.service.LibraryService;
import com.library.bookmarker.vo.LibraryVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BooksController {

    private final LibraryService libraryService;
    private final BooksService booksService;

    @Value("${library.api.key}")
    private String libraryKey;

    @GetMapping({"", "/"})
    public String main() {
        return "books/books";
    }

    // 도서 책 전체 리스트 조회
    @GetMapping("/api/search")
    public ResponseEntity<String> getSearchBooks (
            @RequestParam("keyword") String keyword,
            @RequestParam(value = "pageNo", defaultValue = "1") int pageNo) {

        if (keyword == null) {
            keyword = "";
        }

        ApiResponseDto apiResponseDto = booksService.getSearchBooks(keyword, pageNo);
        String response = apiResponseDto.getResponse();
        return ResponseEntity.ok(response);
    }

    // 외부 api 도서관 도서 보유 정보 조회
    @ResponseBody
    @GetMapping("/api/libraries")
    public List<LibraryVo> getSearchLibrary(@RequestParam("address") String address, @RequestParam("isbn") String isbn) throws IOException {

        List<LibraryVo> returnList = booksService.getBookExistToLibraries(address, isbn);

        return returnList;
    }

    // 외부 api 책 이용 분석
    @GetMapping("/detail/{isbn}")
    public String getSearchBookInfo(@PathVariable("isbn") String isbn, Model model) throws IOException {

        List<LibraryVo> librarySidoList = libraryService.selectLibrarySidoList();

        Map<String, Object> dataMap = booksService.getBookDetail(isbn);

        model.addAttribute("data", dataMap);
        model.addAttribute("sidoList", librarySidoList);
        return "books/detail";
    }

    // 특정 도서의 연관(마니아) 책 이미지 조회 (비동기)
    @PostMapping("api/maniaBook")
    public ResponseEntity<List<BookManiaDto>> getSearchBookImg(@RequestBody Map<String, List<String>> isbnMap) {

        List<String> isbnList = isbnMap.get("isbns");
        if (isbnList == null || isbnList.isEmpty()) {
            return ResponseEntity.badRequest().body(new ArrayList<>());
        }
        List<BookManiaDto> resultList = booksService.getBookImg(isbnList);

        return ResponseEntity.ok(resultList);
    }
}
