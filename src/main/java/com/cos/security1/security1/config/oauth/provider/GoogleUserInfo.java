package com.cos.security1.security1.config.oauth.provider;

import java.util.Map;

/*
    구글 로그인 유저 정보 구현체
 */
public class GoogleUserInfo implements OAuth2UserInfo{

    private Map<String ,Object> attributes; // oAuth2User.getAttributes()

    public GoogleUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getProvider() {
        return "google";
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }
}
