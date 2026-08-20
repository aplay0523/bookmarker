package com.library.bookmarker.mapper;

import com.library.bookmarker.vo.LibraryVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LibraryMapper {
    int insertLibraryList(List<LibraryVo> list);

    List<LibraryVo> selectLibrarySidoList();
    List<String> selectLibrarySigunguList(@Param("libSido") String libSido);
    List<LibraryVo> selectLibraryList(@Param("libName") String libName, @Param("address") String address);

}
