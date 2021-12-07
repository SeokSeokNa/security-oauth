package com.cos.security1.security1.config.auth;

import com.cos.security1.security1.model.User;
import com.cos.security1.security1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/*
    시큐리티 설정(SecurityConfig)에서 "loginProcessingUrl('/login')" 라고 걸어뒀기에
    /login 요청이 오면 스프링은 "UserDetailsService" 타입으로 IoC 되어있는 클래스를 찾고 그 안에 "loadUserByUsername" 메소드를 자동 실행시킴

    추가로 PrincipalDetailsService 이 클래를 만든 목적은 PrincipalDetails 타입의 객체를 "Authentication" 객체에 넣으려고 하는것이다.
    그래야 컨트롤러에서 OAuth 로그인이던 일반로그인 이던 상관없이 "@AuthenticationPrincipal PrincipalDetails principalDetails" 로 정보를 받아올 수 있기 떄문이다
    이렇게 안하면 OAuth 로그인한 사람 컨트롤러 따로 일반로그인한 사람 컨트롤러 따로 가져가야할 것이다 .. 진짜 최악이다
 */
@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {


    private final UserRepository userRepository;
    /*
        Security session 안에 Authentication 안에 Userdetails를 집어넣기 위한 작업
        Security session(내부에 Authentication(내부에 PrincipalDetails))  <- 이런형태가 됨
        메소드 종료시 @AuthenticationPrincipal 어노테이션이 만들어진다.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //파라미터인 username은 로그인페이지인 loginForm에  "name" 옵션이 username 으로 설정해둬야 받을 수 있다!
        //만약 name 옵션부분에 username이 아니라 username2 이런식으로 바꿔서 보내고 싶다면
        // "SecurityConfig" 에서 ".usernameParameter("/username2")" 로 설정해야한다.

        User userEntity = userRepository.findByUsername(username);
        if (userEntity != null) {
            return new PrincipalDetails(userEntity);
        }
        return null;
    }
}
