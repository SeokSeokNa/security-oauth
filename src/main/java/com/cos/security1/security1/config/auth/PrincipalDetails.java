package com.cos.security1.security1.config.auth;

/*
 시큐리티가 "/login" 주소 요청이 오면 낚아채서 로그인을 진행시킨다.
 로그인 진행이 완료가 되면 시큐리티 session을 만들어줌 (Sercutiry ContextHolder  라는 Key값을 가지고 value로 session을 담음)
   (시큐리티가 자신만의 세션공간을 가짐)
 이때 value 부분에 저장되는 오브젝트는 꼭 "Authentication" 타입의 객체여야만 한다.
 Authentication 안에 User정보가 있어야함
 User오브젝트 타입은 UserDetails 타입 객체 여야한다.

 정리하자면
 1. 로그인시 Security Session을 생성함
 2. 이 session에는 "Authentication" 객체 정보만 value로 담을 수 있음(시큐리티가 그렇게 정해둬서 이렇게 따라야함)
 3. Authentication 객체 안에 User 정보를 담기 위해 해당 User오브젝트는 UserDetails 타입 객체여야 한다.

    Session -> Authentication객체 -> UserDetails(PrincipalDetails)
 */


import com.cos.security1.security1.model.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;


@Data
public class PrincipalDetails implements UserDetails , OAuth2User { //소셜로그인 할때와 일반로그인 할때 타입이 달라서 Controller 같은곳에서 DI받아 처리하기가 너무 까다롭다 .. 같은 객체여야 일반 session처럼 꺼내 쓸텐데
                                                                    //그래서 두개의 interface 모두 상속을 받아 인증객체는 소셜로그인이나 일반로그인 상관없이 "PrincipalDetails" 로 처리하기로 하자!

    private User user;//콤포지션션
    private Map<String ,Object> attributes;

    //일반 로그인시 사용하는 객체
    public PrincipalDetails(User user) {
        this.user = user;
    }

    //OAuth 로그인시 사용하는 객체
    public PrincipalDetails(User user , Map<String ,Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }



    //해당 User의 권한을 리턴하는곳!!
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole();
            }
        });
        return collection;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    //계정이 만료가 안됐나  물어보는것
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    //계정 안잠겼니?
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    //비밀번호가 너무 오래사용하고있나?
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    //계정 활성화 여부
    @Override
    public boolean isEnabled() {

        //만약 로그인할때마다 User객체에 로그인 시간을 담는 필드가 있다쳐보자!
        //현재시간 - 로그인시간 = 1년 초과시 return false;   하면 휴면계정 같은 처리를 할 수 있다.

        return true;
    }


    /*
        OAuth2User 인터페이스를 상속받아 구현해야하는 메소드들
     */
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return null;
    }
}
