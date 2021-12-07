package com.cos.security1.security1.config.oauth.provider;
/*
    각 OAuth 주체들로 부터 얻어오 User정보들을 담아 회원가입할때 각 정보들을 내보내주기 위한 인터페이스
 */
public interface OAuth2UserInfo {
    String getProviderId();
    String getProvider();
    String getEmail();
    String getName();
}
