
package com.shop.config;

import com.shop.service.MemberService;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {


   // @Autowired
  //  MemberService memberService;
   private final MemberService memberService;

    @Bean  // 시큐리트 6에서 config(HttpSecurity http) 변경됨
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // 170  AuthenticationmanagerBuiler 사용안됨 아래코드로 변경
        http
                .formLogin(form -> {
                    form
                            .loginPage("/members/login")    // 로그인 페이지
                            .defaultSuccessUrl("/")         // 로그인 성공시 기본 경로
                            .usernameParameter("email")     // 로그인시 인증 키값
                            .failureUrl("/members/login/error");  // 로그인 실패시 갈 경로
                })
                .logout(logout -> {                         // 로그아웃용
                    logout.logoutRequestMatcher(new AntPathRequestMatcher("/members/logout")) // 로그아웃 처리용 경로
                            .logoutSuccessUrl("/");     // 로그아웃 성공시 갈 경로
                });
        http.authorizeHttpRequests(authorizeHttpRequests -> {
            authorizeHttpRequests
                    .requestMatchers("/favicon.ico", "/error").permitAll()
                    .requestMatchers("/css/**", "/js/**", "/img/**").permitAll()
                    // requestMatchers Http 요청매처를 적용
                    // .permitAll 모든 요청을 인가(인증된 사용자 권한에 상관 없음)
                    .requestMatchers("/", "/members/**", "/item/**", "/images/**").permitAll()
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    // /admin/하위 메서드는 ADMIN 룰에 적용됨.
                    .anyRequest().authenticated();
        })  ;

        http.exceptionHandling(exceptionHandling -> {
            exceptionHandling
                    .authenticationEntryPoint(new CustomAuthenticationEntryPoint());
        })  ;

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    }