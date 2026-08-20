package com.library.bookmarker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginPage() {
        // templates/content/login.html 파일을 리턴
        return "login";
    }
}
