package com.library.bookmarker.controller;

import com.library.bookmarker.oauth2.CustomOAuth2User;
import com.library.bookmarker.service.UserService;
import com.library.bookmarker.vo.UserVo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class MyPageController {

    private final UserService userService;

    @GetMapping("/myPage")
    public String myPage(Model model, @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        UserVo userVo = new UserVo();

        if (oAuth2User != null) {
            userVo = oAuth2User.getUserVo();
        } else {
            return "redirect:/login";
        }

        model.addAttribute("data", userVo);
        return "myPage/main";
    }

    // 사용자 계정 삭제
    @DeleteMapping("/myPage/api/user")
    public ResponseEntity<Void> delUser (@AuthenticationPrincipal CustomOAuth2User oAuth2User, HttpServletRequest request) throws IOException {

        if (oAuth2User == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = oAuth2User.getUserVo().getId();

        userService.deleteUser(userId);

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        return ResponseEntity.ok().build();
    }
}
