package com.library.bookmarker.controller;

import com.library.bookmarker.service.LibraryService;
import com.library.bookmarker.vo.LibraryVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/library")
@RequiredArgsConstructor
public class LibraryController {

    private final LibraryService libraryService;

    @GetMapping({"", "/"})
    public String mainPage(Model model) throws IOException{

        List<LibraryVo> librarySidoList = libraryService.selectLibrarySidoList();

        model.addAttribute("sidoList", librarySidoList);
        return "library/library";
    }

    // 도서관 시/도 조회
    @GetMapping("/sigungus")
    @ResponseBody
    public List<String> getLibraryListSigunguList(@RequestParam("libSido") String libSido) throws IOException {
        if (libSido == null || libSido.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return libraryService.selectLibrarySigunguList(libSido);
    }

    // 도서관 상세 리스트 조회
    @GetMapping("/libraries")
    @ResponseBody
    public List<LibraryVo> getLibraryInfoList(@RequestParam("libName") String libName, @RequestParam("address") String address) throws IOException {
        List<LibraryVo> libraryVo = new ArrayList<>();
        if (address == null || address.trim().isEmpty()) {
            return new ArrayList<>();
        } else {
            libraryVo = libraryService.selectLibraryList(libName, address);
        }
        return libraryVo;
    }
}
