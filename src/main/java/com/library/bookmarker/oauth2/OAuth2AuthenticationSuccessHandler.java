package com.library.bookmarker.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private static final Logger log = LogManager.getLogger(OAuth2AuthenticationSuccessHandler.class);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // 로그인에 성공한 유저 객체 꺼내기
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        // 세션 생성 로그 출력
        String providerId = oAuth2User.getName();
        String authorities = oAuth2User.getAuthorities().toString();

        log.info("=========================================================");
        log.info("[Security 세션 생성 완료] 소셜 로그인 최종 성공!");
        log.info("로그인 유저 이메일 및 권한: {}", providerId,authorities);
        log.info("=========================================================");

        // 메인 페이지 "/"로 리다이렉트
        setDefaultTargetUrl("/");
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
