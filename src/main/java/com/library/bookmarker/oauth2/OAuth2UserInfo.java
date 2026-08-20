package com.library.bookmarker.oauth2;

import java.util.Map;

public interface OAuth2UserInfo {
    Map<String, Object>  getAttributes();
    String getProviderId();
    String getProvider();
    String getName();
    String getEmail();
}
