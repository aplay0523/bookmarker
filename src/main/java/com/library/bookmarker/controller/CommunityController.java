package com.library.bookmarker.controller;

import com.library.bookmarker.oauth2.CustomOAuth2User;
import com.library.bookmarker.service.CommunityService;
import com.library.bookmarker.vo.CommunityVo;
import com.library.bookmarker.vo.PageResponseVo;
import com.library.bookmarker.vo.UserVo;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/community")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;

    @GetMapping({"/", ""})
    public String mainPage(Model model, @AuthenticationPrincipal CustomOAuth2User customOAuth2User) throws IOException {

        String name = "";
        String title = "";
        int page = 1;
        int size = 10;
        int offset = 0;

        int totalCount = communityService.selectCommunityCount("", "");
        List<CommunityVo> communityList = communityService.selectCommunityList(title, name, size, offset);

        PageResponseVo<CommunityVo> pageInfo = new PageResponseVo<>(communityList, totalCount, page, size);

        // 사용자 로그인 체크 true/false
        boolean isLogin = (customOAuth2User != null);

        model.addAttribute("isLogin", isLogin);
        model.addAttribute("pageInfo", pageInfo);
        return "community/list";
    }

    // 커뮤니티 게시물 상세 조회
    @GetMapping({"/api/board/{id}"})
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCommunity(@PathVariable("id") Long id, @AuthenticationPrincipal CustomOAuth2User customOAuth2User) throws IOException {
        CommunityVo communityVo = communityService.selectCommunity(id);

        boolean isOwner = false;
        if (customOAuth2User != null && communityVo != null) {
            Long userId = customOAuth2User.getUserVo().getId();
            isOwner = userId.equals(communityVo.getUserId());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("board", communityVo);
        response.put("isOwner", isOwner);

        return ResponseEntity.ok(response);
    }

    // 커뮤니티 게시물 리스트 조회
    @GetMapping({"/api/board"})
    public String getCommunityList(
            @RequestParam(name="page", defaultValue = "1") int page,
            @RequestParam(name="size", defaultValue = "10") int size,
            @RequestParam("title") String title,
            @RequestParam("name") String name,
            Model model) throws IOException {

        int offset = (page - 1) * size;

        int totalCount = communityService.selectCommunityCount(title, name);
        List<CommunityVo> communityVoList = communityService.selectCommunityList(title, name, size, offset);

        PageResponseVo<CommunityVo> pageInfo = new PageResponseVo<>(communityVoList, totalCount, page, size);

        model.addAttribute("pageInfo", pageInfo);
        return "community/list :: boardTableArea";
    }

    // 커뮤니티 게시물 등록
    @PostMapping({"/api/board"})
    @ResponseBody
    public ResponseEntity<Integer> insertCommunity(@RequestBody CommunityVo communityVo, @AuthenticationPrincipal CustomOAuth2User customOAuth2User) throws IOException {

        if (customOAuth2User == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        Long userId = customOAuth2User.getUserVo().getId();

        CommunityVo newCommunityVo = communityVo.toBuilder()
                .userId(userId)
                .build();

        return ResponseEntity.ok(communityService.insertCommunity(newCommunityVo));
    }

    // 커뮤니티 게시물 삭제
    @DeleteMapping("api/board/{id}")
    @ResponseBody
    public ResponseEntity<Integer> deleteCommunity(@PathVariable("id") Long id, @AuthenticationPrincipal CustomOAuth2User customOAuth2User) throws IOException{
        if (customOAuth2User == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = customOAuth2User.getUserVo().getId();

        return ResponseEntity.ok(communityService.deleteCommunity(id, userId));
    }
}
