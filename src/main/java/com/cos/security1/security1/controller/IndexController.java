package com.cos.security1.security1.controller;

import com.cos.security1.security1.config.auth.PrincipalDetails;
import com.cos.security1.security1.model.User;
import com.cos.security1.security1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class IndexController {


    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    //일반 로그인한 상태에서 정보 받기
    @GetMapping("/test/login")
    public @ResponseBody String testLogin(Authentication authentication //DI(의존성 주입)
            , @AuthenticationPrincipal UserDetails userDetails // UserDetails 타입을 받을수 있는 방법 1
            , @AuthenticationPrincipal PrincipalDetails userDetails2) {  // UserDetails 타입을 받을수 있는 방법 2(PrincipalDetails 가 UserDetails 타입이기 떄문에)
        System.out.println("/test/login ===================");
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        System.out.println("authentication :" + principalDetails.getUser());

        System.out.println("userDetails = " + userDetails.getUsername());
        System.out.println("userDetails2 = " + userDetails2.getUser());

        return "세션 정보 확인하기";
    }


    //소셜 로그인한 상태에서 정보 받기
    @GetMapping("/test/oauth/login")
    public @ResponseBody String testOauthLogin(Authentication authentication, //OAuth2 유저 받는 방법1
                                               @AuthenticationPrincipal OAuth2User oauth //OAuth2 유저 받는 방법2
                                               ){ //DI(의존성 주입))
        System.out.println("/test/oauth/login ===================");
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        System.out.println("authentication :" + oAuth2User.getAttributes());

        System.out.println("oauth2User :" + oauth.getAttributes());

        return "OAuth 세션 정보 확인하기";
    }




    @GetMapping({"","/"})
    public String index() {
        return "index";
    }

    //OAuth 로그인 해도 PrincipalDetails 로 받을 수 있고
    //일반 로그인을 해도 PrincipalDetails 로 받을 수 있다!!!!
    //실제로 이렇게 써야할 듯
    @GetMapping("/user")
    public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        System.out.println("principalDetails = " + principalDetails.getUser());
        return "user";
    }

    @GetMapping("/admin")
    public @ResponseBody String admin() {
        return "admin";
    }

    @GetMapping("/manager")
    public @ResponseBody String manager() {
        return "manager";
    }

    //시큐리티 설정 재설정 안하면 시큐리티가 재공하는 기본 login 페이지로 가게 시큐리티가 요청을 가로채버림
    // config 패키지 밑에 "SecurityConfig" 파일 설정후 이 컨트롤러에 매핑이 정상적으로 이루어짐을 확인함
    @GetMapping("/loginForm")
    public  String loginForm() {
        return "loginForm";
    }

    @GetMapping("/joinForm")
    public  String joinForm() {
        return "joinForm";
    }

    @PostMapping("/join")
    public String join(User user) {
        System.out.println(user);
        user.setRole("ROLE_USER");
        String rawPassword = user.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        user.setPassword(encPassword);
        userRepository.save(user);
        return "redirect:/loginForm";
    }

    //권한 하나만 걸고 싶을때의 예
    @Secured("ROLE_ADMIN") // SecurityConfig에 @EnableGlobalMethodSecurity(securedEnabled = true) 옵션으로 이 어노테이션을 써서 권한 검사를 할 수 있음(메서드별로 간단히 해보고 싶을경우 추천방식)
    @GetMapping("/info")
    public @ResponseBody String info() {
        return "개인정보";
    }

    //권한 여러개 걸고 싶을때의 예
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')") //SecurityConfig에 @EnableGlobalMethodSecurity( prePostEnabled = true) 옵션으로 사용 가능한 어노테이션
                                                                      //해당 메서드가 실행되기 전에 애가 먼저 실행됨
//    @PostAuthorize()  // 메소드 종료휴 권한검사할때 필요 ( 굳이 쓸필요 없어서 잘 안쓰임 )
    @GetMapping("/data")
    public @ResponseBody String data() {
        return "데이터 정보";
    }

}
