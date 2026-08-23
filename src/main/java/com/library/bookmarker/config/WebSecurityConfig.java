package com.library.bookmarker.config;

import com.library.bookmarker.oauth2.CustomOAuth2UserService;
import com.library.bookmarker.oauth2.OAuth2AuthenticationSuccessHandler;
import com.library.bookmarker.util.Sha256EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableWebSecurity
@EnableRedisHttpSession
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/main",
                                "/library",
                                "/library/**",
                                "/books",
                                "/books/**",
                                "/books/api/**",
                                "/community",
                                "/community/**",
                                "/login",
                                "/login/**",
                                "/bootstrap/**",
                                "/favicon.ico",
                                "/error"
                        ).permitAll()
                        .requestMatchers(
                                "/board/**",
                                "/mypage/**"
                        ).authenticated()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.disable())
                .oauth2Login(oauth2 -> oauth2
                                .loginPage("/login")
                         .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .authorizationEndpoint(authorization -> authorization
                                .baseUri("/login/oauth2"))
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                );
        return http.build();
    }
}
