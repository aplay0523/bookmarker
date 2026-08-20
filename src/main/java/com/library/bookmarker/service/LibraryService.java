package com.library.bookmarker.service;

import com.library.bookmarker.mapper.LibraryMapper;
import com.library.bookmarker.vo.LibraryVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LibraryService {

    private final LibraryMapper libraryMapper;

    public List<LibraryVo> selectLibrarySidoList() throws IOException {
        return libraryMapper.selectLibrarySidoList();
    }
    public List<String> selectLibrarySigunguList(String libSido) throws IOException {
        return libraryMapper.selectLibrarySigunguList(libSido);
    }
    public List<LibraryVo> selectLibraryList(String libName, String address) throws IOException {
        return libraryMapper.selectLibraryList(libName, address);
    }
}
