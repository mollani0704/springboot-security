package com.study.security_jongseong.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.study.security_jongseong.config.auth.AuthFailureHandler;
import com.study.security_jongseong.service.auth.PrincipalOauth2UserService;

import lombok.RequiredArgsConstructor;

@EnableWebSecurity // 기존의 WebSecurityConfigurerAdapter를 비활성 시키고 현재 Security 설정을 따르겠다. 
@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	
	private final PrincipalOauth2UserService principalOauth2UserService;
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable(); //CSRF 토큰 비활성화.
		http.authorizeRequests() // 요청이 들어오면 인증을 해라
			.antMatchers("/api/v1/grant/test/user/**")
			.access("hasRole('ROLE_USER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
			
			.antMatchers("/api/v1/grant/test/manager/**")
			.access("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
			
			.antMatchers("/api/v1/grant/test/admin/**")
			.access("hasRole('ROLE_ADMIN')")
			
			.antMatchers("/", "/index", "/mypage/**") //우리가 지정한 요청
			.authenticated() 			// 인증을 거쳐라
			
			.anyRequest() // 다른 모든 요청들 -> 다른 모든요청은 모두 접근 권한을 부여하겠다. 
			.permitAll() // 모든 권한을 줘라.
			
			.and()
			
			.formLogin()//로그인 방식은 form login을 해라. 로그인 방식 -> 1.jwt토큰 요청, 2.http 기본 요청, 3.form login 요청
			.loginPage("/auth/signin") // 로그인 페이지는 해당 get요청을 통해 접근. 요청 주소. -> get요청.
			.loginProcessingUrl("/auth/signin") // 로그인 요청(post요청)
			.failureHandler(new AuthFailureHandler()) // 비밀번호나 아이디가 틀렸을 때 예외를 던져주고 이 예외를 낚아 챌수 있는 핸들러를 등록해놓는 것.
			
			
			.and()
			
			.oauth2Login()
			.userInfoEndpoint()
			.userService(principalOauth2UserService)
			
			.and()
			
			.defaultSuccessUrl("/index");
		
//		super.configure(http); -> 기존에 있던 security를 사용하는 것.
	}
}
