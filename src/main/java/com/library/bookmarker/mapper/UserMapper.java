package com.library.bookmarker.mapper;

import com.library.bookmarker.vo.UserVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    UserVo selectUser(@Param("provider") String provider, @Param("providerId") String providerId);

    int insertUser(UserVo userVo);
    int updateUser(UserVo userVo);
    int deleteUser(Long id);
}
