package com.library.bookmarker.oauth2;

import java.util.Map;
import lombok.Getter;

@Getter
public class GoogleOAuth2UserInfo implements OAuth2UserInfo{

    private final Map<String, Object> attributes;
    private final String providerId;
    private final String provider = "GOOGLE";
    private final String name;
    private final String email;

    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.providerId = (String) attributes.get("sub");
        this.name = (String) attributes.get("name");
        this.email = (String) attributes.get("email");
    }
}
