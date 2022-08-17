package com.study.security_jongseong.service.auth;

import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.study.security_jongseong.domain.user.User;
import com.study.security_jongseong.domain.user.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j // Logger를 자동으로 만들어주는 어노테이션
@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService{
	
	private final UserRepository userRepository;
	
	
	/*
	 * OAuth2User의 정보를 우리 서버 database에 등록.
	 */
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		String provider = null;
		
		
		/*
		 * super.loadUser(userRequest)
		 * 엔드포인트 결과 즉, OAuth2User 정보를 가진 객체를 리턴.
		 */
		OAuth2User oAuth2User = super.loadUser(userRequest);
		
		/*
		 * Provider 정보(클라이언트 아이디, 클라이언트 시크릿, 클라이언트 네임)
		 */
		ClientRegistration clientRegistration = userRequest.getClientRegistration();
		
		/*
		 * 실제 프로필 정보(Map)
		 */
		Map<String, Object> attributes = oAuth2User.getAttributes();
		
		
		log.error(">>>>>> ClientRegistration: {}", clientRegistration);
		log.error(">>>>>> oAuth2User: {}", attributes);
		
		provider = clientRegistration.getClientName();
		
		User user  = getOAuth2User(provider, attributes);
		
		return new PrincipalDetails(user, attributes);
	}
	
	private User getOAuth2User(String provider, Map<String, Object> attributes) throws OAuth2AuthenticationException {
		User user = null;
		String id = null;
		
		String oauth2_id = null;
		
		Map<String, Object> response = null;
		
		if(provider.equalsIgnoreCase("google")) {
			response = attributes;
			id = (String) response.get("sub");
					
		} else if(provider.equalsIgnoreCase("naver")) {
			response = (Map<String, Object>) attributes.get("response");
			id = (String) response.get("id");
			
		} else {
			throw new OAuth2AuthenticationException("provider Error!");
		}
		
		oauth2_id = provider + "_" + id;
		
		try {
			user = userRepository.findOAuth2UserByUsername(oauth2_id);
		} catch (Exception e) {
			e.printStackTrace();
			throw new OAuth2AuthenticationException("DATABASE Error!");
		}
		
		if(user == null) {
			user = User.builder()
				.user_name((String)response.get("name"))
				.user_email((String) response.get("email"))
				.user_id(oauth2_id)
				.oauth2_id(oauth2_id)
				.user_password(new BCryptPasswordEncoder().encode(oauth2_id))
				.user_roles("ROLE_USER")
				.user_provider(provider)
				.build();
				
			try {
				userRepository.save(user);
				user = userRepository.findOAuth2UserByUsername(oauth2_id);
			} catch (Exception e) {
				e.printStackTrace();
				throw new OAuth2AuthenticationException("DATABASE Error!");
			}
		}
		
		return user;
	}
}
