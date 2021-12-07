package com.cos.security1.security1.config;

import com.cos.security1.security1.config.oauth.PrincipalOauth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터체인에 등록이 됨!!
@EnableGlobalMethodSecurity(securedEnabled = true , prePostEnabled = true) // securedEnabled = true -> secured 어노테이션 활성화
                                                                          // prePostEnabled = true -> preAuthorize , postAuthorize어노테이션 활성화
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    //해당 메서드의 리턴되는 오브젝트를 IoC로 등록해준다.

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final PrincipalOauth2UserService principalOauth2UserService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
//        서버가 응답한 html 페이지로 요청했는지 아니면 강제로 포스트맨.같은걸로 요청했는지를 검증하는데 사용됨
//        이걸 disable 안하면  포스트맨 테스트를 할 수가 없음 , 안드로이드나 다른 api 요청을 못한다는 소리
        http.authorizeRequests()
                .antMatchers("/user/**").authenticated() // /user/~ 로 들어오는 모든 요청은 인증이 필요하다.
                .antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")// /manager/~ 로 들어오는 모든 요청은 인증 뿐 아니라 ROLE_ADMIN 또는 ROLE_MANAGER 권한이 필요하다.
                .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')") // /admin/~ 로 들어오는 모든 요청은 인증 뿐 아니라 ROLE_ADMIN 권한 도 필요하다.
                .anyRequest().permitAll() //다른 요청은 모두 허가한다.
                .and()
                .formLogin() //인증이 필요한 페이지에 접근시 로그인 페이지로 이동하게
                //.usernameParameter("/username2") //loginForm에서 name이 username2로 바꿔서 보내고싶다면
                .loginPage("/loginForm") //이동할 로그인 페이지 controller 주소
                .loginProcessingUrl("/login") //  /login 이라는 주소가 호출되면 시큐리티가 낚아채서 대신 로그인을 진행해줌!("/login" 주소에 매핑되는 컨트롤러를 따로 안만들어줘도 된다는 장점이 있네)
                // 시큐리티가 낚아채서 내가만든 "UserDetailsService" 타입의 "PrincipalDetailsService" 클래스 에서 로그인 처리 진행함
                // 로그인 진행하는 html에 form 태그에 해당 주소를 action 부분에 적용해줘야함
                .defaultSuccessUrl("/") // 로그인 완료시 이동될 페이지
                // 만약 특정 url로 이동하다 로그인 페이지로 이동된 상태에서 로그인 하게되면 defaultSuccessUrl에 해당하는 url로 이동하지 않고 원래 가려던 특정 url로 바로 이동한다!!
                // (원래 이런 로직을 만드려면 인터셉터에서 로직 만들어서 처리해야 하는데 그런거 없이 바로되서 엄청 편함!!)


                .and()
                .oauth2Login()
                .loginPage("/loginForm") //인증이 필요할 경우 똑같이 loginForm으로 가게
                .userInfoEndpoint()
                .userService(principalOauth2UserService) //구글 로그인후 code를 받는게 아니라 AccessToken을 받고 그 토큰으로 해당 사용자 정보를 요청해서 받아 후처리 할 곳!
                                                         // (여기에서는 엑세스토큰 + 사용자프로필 정보 까지 받아 올 수 있다)

        ;
    }
}
