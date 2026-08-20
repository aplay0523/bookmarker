package com.library.bookmarker.service;

import com.library.bookmarker.mapper.CommunityMapper;
import com.library.bookmarker.vo.CommunityVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityMapper communityMapper;

    // 페이징용 건수 조회
    public int selectCommunityCount(String title, String name) throws IOException {
        return communityMapper.selectCommunityCount(title, name);
    }

    // 커뮤니티 게시물 리스트 조회
    public List<CommunityVo> selectCommunityList(String title, String name, int size, int offset) throws IOException {
        return communityMapper.selectCommunityList(title, name, size, offset);
    }

    // 커뮤니티 게시물 단건 조회
    public CommunityVo selectCommunity(Long id) throws IOException {
        return communityMapper.selectCommunity(id);
    }

    // 커뮤니티 게시물 등록
    public int insertCommunity(CommunityVo communityVo) throws IOException {
        return communityMapper.insertCommunity(communityVo);
    }

    // 커뮤니티 게시물 삭제 (본인 확인)
    public int deleteCommunity(Long id, Long userId) throws IOException {
        return communityMapper.deleteCommunity(id, userId);
    }
}
