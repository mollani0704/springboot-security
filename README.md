# NoticeProject
>SpringBoot와 SpringSecurity에 대해 배운 것들로 게시판을 만들어 보았습니다.
<br>

<img alt="main" src="https://user-images.githubusercontent.com/72548305/236737347-60d845cf-f613-4f8e-9315-7217ebe7eddd.JPG" width="550">

## 사용 기술스택
<p>
  <img alt="html5" src="https://img.shields.io/badge/-html5-F44336?style=flat-square&logo=html5&logoColor=white" />
  <img alt="css" src="https://img.shields.io/badge/-css-03A9F4?style=flat-square&logo=css3&logoColor=white" />
  <img alt="javascript" src="https://img.shields.io/badge/-javascript-FFEB3B?style=flat-square&logo=javascript&logoColor=white" />
  <img alt="springboot" src="https://img.shields.io/badge/-springboot-13aa52?style=flat-square&logo=springboot&logoColor=white" />
  <img alt="thymeleaf" src="https://img.shields.io/badge/-thymeleaf-13aa52?style=flat-square&logo=thymeleaf&logoColor=white" />
  <img alt="Java" src="https://img.shields.io/badge/-java-007396?style=flat-square&logo=java&logoColor=white" />
  <img alt="MariaDB" src="https://img.shields.io/badge/-mariadb-42A5F5?style=flat-square&logo=mariadb&logoColor=white" />
</p>

## 구현기능
### Oauth2를 이용해 구글 로그인 구현
<img alt="main" src="https://user-images.githubusercontent.com/72548305/236738796-99d44406-61a6-44c4-be5d-7c4f7f639e3b.JPG" width="550">
<br>

```java
@Slf4j
@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService{
	
	private final UserRepository userRepository;
	
	public OAuth2User loadUser(OAuth2UserRequest auth2UserRequest) {
		
		String provider = null;
		
		OAuth2User oAuth2User = super.loadUser(auth2UserRequest);

		//ClientRegistration - OAuth2 제공자에 등록된 Client의 정보를 나타내는 클래스이다. 
		ClientRegistration clientRegistration = auth2UserRequest.getClientRegistration();
		
		Map<String, Object> attributes = oAuth2User.getAttributes();
		
		log.error(">>>>>>>>> ClientRegistration : {} ", clientRegistration);
		log.error(">>>>>>> oAuth2User : {}", attributes);
		
		provider = clientRegistration.getClientName();
		
		User user = getOauth2User(provider, attributes);
		
		return new PrincipalDetail(user, attributes);
		
	}
	
	private User getOauth2User(String provider, Map<String, Object> attributes) throws OAuth2AuthenticationException {
		
		User user = null;
		String id = null;
		
		String oauth2_id = null;
		
		Map<String, Object> response = null;
		
		if(provider.equalsIgnoreCase("google")) {
			response = attributes;
			id = (String) response.get("sub");
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
					.user_name((String) response.get("name"))
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

```

### 글쓰기, 글수정

<img alt="notice" src="https://user-images.githubusercontent.com/72548305/236760651-1818da2a-27ed-4ecc-97ae-acc0194f475c.JPG" width="550">
<img alt="notice_write" src="https://user-images.githubusercontent.com/72548305/236760662-e4ba7ca3-cdfd-41d2-bee1-af797682045d.JPG" width="550">

### 페이징 기능
```javascript
function getPageNumbers(totalNoticeCount) {
	const totalPageCount = totalNoticeCount % 10 == 0 ? totalNoticeCount / 10 : (totalNoticeCount / 10) + 1;
	let startIndex = nowPage % 5 == 0 ? nowPage - 4 : nowPage - (nowPage % 5) + 1;
	let endIndex = startIndex + 4 <= totalPageCount ? startIndex + 4 : totalPageCount;
	
	const pageButtons = document.querySelector(".page__buttons");
	
	pageButtons.innerHTML = ``;
	
	if(startIndex != 1) {
		pageButtons.innerHTML += `
			<button type="button" class="page__button pre">&lt;</button>
		`
	}
	
	for(let i = startIndex; i <= endIndex; i++) {
		pageButtons.innerHTML += `
			<button type="button" class="page__button">${i}</button>
		`
	}
	
	if(startIndex != totalPageCount) {
		pageButtons.innerHTML += `
			<button type="button" class="page__button next">&gt;</button>
		`
	}
	
	if(startIndex != 1) {
		const prePageButton = document.querySelector(".pre");
		prePageButton.addEventListener('click', () => {
			nowPage = startIndex - 1;
			load(nowPage);
		})
	}
	
	if(startIndex != totalPageCount) {
		const nextPageButton = document.querySelector('.next');
		nextPageButton.addEventListener('click', () => {
			nowPage = endIndex + 1;
			load(nowPage);
		})
	}
	
	const pageNumberButtons = document.querySelectorAll(".page__button");
	pageNumberButtons.forEach(button => {
		if(button.textContent != "<" && button.textContent != ">") {
			button.addEventListener('click', () => {
				nowPage = button.textContent;
				load(nowPage);
			})
		}
	})
}
```

## 배운 점 & 아쉬운 점
### 배운점
- SpringBoot의 흐름에 대해서 공부할 수 있는 계기가 되어서 좋았다.
- 페이징 기능을 순수 자바스크립트로 구현하는 것이 상당히 어렵게 느껴졌는데 이번 기회로 잘 공부할 수 있었다.
- SrpingSecurity에 대한 이해가 명확히 됐고 구글 Oauth 로그인에 대한 개념과 방법도 익힐 수 있었다. 

### 아쉬운 점
- URI를 설계하는 것이 생각보다 어려웠다.RESTful 하게 작성한다고 했는데 작성하다보니 좀 헷갈리게 작성된 거 같아 좀 더 생각을 해보고 설계를 할 필요성을 느꼈다.
- 너무 무분별하게 DTO를 만든 게 아닌가 하는 생각이 들었다. 객체 그대로 데이터를 주고받는 것 보다 DTO로 변환해서 데이터를 주고받는 것이 더 좋다고 했지만
굳이 안그래도 될 곳에 DTO를 만들어 불필요한 DTO 객체를 만들어 코드에 혼선만 더 준게 아닌가 하는 아쉬움이 든다.
