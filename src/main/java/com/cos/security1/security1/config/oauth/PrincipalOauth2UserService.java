package com.cos.security1.security1.config.oauth;

import com.cos.security1.security1.config.auth.PrincipalDetails;
import com.cos.security1.security1.config.oauth.provider.FacebookUserInfo;
import com.cos.security1.security1.config.oauth.provider.GoogleUserInfo;
import com.cos.security1.security1.config.oauth.provider.NaverUserInfo;
import com.cos.security1.security1.config.oauth.provider.OAuth2UserInfo;
import com.cos.security1.security1.model.User;
import com.cos.security1.security1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;


/*
    PrincipalOauth2UserService 이 클래스를 만든 목적은 PrincipalDetails 타입의 객체를 "Authentication" 객체에 넣으려고 하는것이다.
    그래야 컨트롤러에서 OAuth 로그인이던 일반로그인 이던 상관없이 "@AuthenticationPrincipal PrincipalDetails principalDetails" 로 정보를 받아올 수 있기 떄문이다
    이렇게 안하면 OAuth 로그인한 사람 컨트롤러 따로 일반로그인한 사람 컨트롤러 따로 가져가야할 것이다 .. 진짜 최악이다
*/
@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {


    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;


    /*
        구글로 부터 받은 userRequest 데이터에 대한 후처리를 위한 함수
        메소드 종료시 @AuthenticationPrincipal 어노테이션이 만들어진다.
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("getClientRegistration: " + userRequest.getClientRegistration()); // "registrationId" 로 어떤 OAuth로 로그인 했는지가 확인 가능
        System.out.println("getAccessToken: " + userRequest.getAccessToken().getTokenValue());
        // 구글 로그인 버튼 클릭 -> 구글 로그인창 -> 로그인을 완료 -> code를 리턴받음(OAuth-Client 라이브러리가 받아줌) -> 받은 Code로 AccessToken 요청
        // AccessToken 까지 받은 상태가 파라미터인 "userRequest" 정보이다.

        //userRequest 정보 -> 회원프로필을 받아야함함(이때 loadUser메소드가 필요) -> loadUser 메소드 호출 -> 회원프로필 받기(구글로 부터 받아옴)
        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println("oAuth2User: " + oAuth2User.getAttributes());


        //각 소셜로그인 부분에서 요청 정보들이 다르니 인터페이스를 만들어서 요청에 맞는 클래스들을 구현체로 담아 처리
        //소셜마다 이 회원의 고유의 pk값을 가지고 있는 필드들이 달라가지고 이렇게 처리해서 유지보수 편하게 처리했다!!
        OAuth2UserInfo oAuth2UserInfo = null;
        if (userRequest.getClientRegistration().getRegistrationId().equals("google")) {
            System.out.println("구글 로그인 요청");
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        } else if (userRequest.getClientRegistration().getRegistrationId().equals("facebook")) {
            System.out.println("페이스북 로그인 요청");
            oAuth2UserInfo = new FacebookUserInfo(oAuth2User.getAttributes());
        } else if (userRequest.getClientRegistration().getRegistrationId().equals("naver")) {
            System.out.println("네이버 로그인 요청");
            oAuth2UserInfo = new NaverUserInfo((Map)oAuth2User.getAttributes().get("response")); //attribues 안에 response 안에 정보가 있기에 이렇게 해야함
        } else {
            System.out.println("우리는 구글과 페이스북만 지원해용");
        }

        /*회원가입 시키기*/
        String provider = oAuth2UserInfo.getProvider(); //OAuth의 주체가 어디인지(구글인지 페이스북인지 네이버인지 카카오인지 ...)
        String providerId = oAuth2UserInfo.getProviderId(); //해당 OAuth 주체의 회원의 Pk값
        String username = provider + "_" + providerId; // 주체_pk 형태로 만들어짐
        String password = bCryptPasswordEncoder.encode("겟인데어"); //소셜 로그인시 비밀번호는 따로 받지 않으니 임의로 아무값이나 넣는거임
        String email = oAuth2UserInfo.getEmail();
        String role = "ROLE_USER";

        User userEntity = userRepository.findByUsername(username);

        if (userEntity == null) {
            System.out.println(provider + " 로그인이 최초입니다.");
            userEntity = User.builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .build();

            userRepository.save(userEntity);
        } else {
            System.out.println(provider+" 로그인을 이미 한적이 있습니다. 당신은 자동회원가입 되어 있습니다.");
        }

        return new PrincipalDetails(userEntity , oAuth2User.getAttributes()); //결과로 만든 PrincipalDetails객체가 Authentication 객체 안에 자동으로 들어갈것임
    }

}
