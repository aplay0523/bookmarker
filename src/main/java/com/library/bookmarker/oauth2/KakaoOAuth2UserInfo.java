package com.library.bookmarker.oauth2;

import java.util.Map;
import lombok.Getter;

@Getter
public class KakaoOAuth2UserInfo implements OAuth2UserInfo{

    private final Map<String, Object> attributes;
    private final String providerId;
    private final String provider = "KAKAO";
    private final String name;
    private final String email;

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.providerId = String.valueOf(attributes.get("id"));

        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) attributes.get("profile");

        this.name = (String) profile.get("nickname");
        this.email = (String) kakaoAccount.get("email");
    }
}
