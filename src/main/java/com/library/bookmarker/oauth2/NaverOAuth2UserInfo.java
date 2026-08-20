package com.library.bookmarker.oauth2;

import java.util.Map;
import lombok.Getter;

@Getter
public class NaverOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;
    private final String providerId;
    private final String provider = "NAVER";
    private final String name;
    private final String email;

    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;

        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        this.providerId = (String) response.get("id");
        this.name = (String) response.get("name");
        this.email = (String) response.get("email");
    }
}
