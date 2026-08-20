package com.library.bookmarker.mapper;

import com.library.bookmarker.vo.CommunityVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommunityMapper {

    int selectCommunityCount(@Param("title") String title, @Param("name") String name);

    List<CommunityVo> selectCommunityList(@Param("title") String title, @Param("name") String name, @Param("size") int size, @Param("offset") int offset);

    CommunityVo selectCommunity(@Param("id") Long id);

    int insertCommunity(CommunityVo communityVo);
    int deleteCommunity(@Param("id") Long id, @Param("userId") Long userId);
}
