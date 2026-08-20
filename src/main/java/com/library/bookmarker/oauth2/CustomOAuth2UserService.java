package com.library.bookmarker.oauth2;

import com.library.bookmarker.util.Sha256EncryptUtil;
import com.library.bookmarker.mapper.UserMapper;
import com.library.bookmarker.vo.UserVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserMapper userMapper;

    private final Sha256EncryptUtil sha256EncryptUtil;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // DefaultOAuth2UserService를 통해 소셜(구글,네이버,카카오) 사용자 정보(Attributes) 가져옴
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 소셜 로그인 구분
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 공통으로 사용할 변수
        String providerId = "";
        String email = "";
        String name = "";

        // 각 소셜 맞게 데이터 파싱 분기 처리
        if ("google".equals(registrationId)) {
            providerId = String.valueOf(attributes.get("sub"));
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");

        } else if ("kakao".equals(registrationId)) {
            // 카카오는 id가 Long 타입이며, 프로필 정보가 kakao_account라는 Map 안에 묶여있음
            providerId = String.valueOf(attributes.get("id"));

            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            if (kakaoAccount != null) {
                email = (String) kakaoAccount.get("email");
                Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
                if (profile != null) {
                    name = (String) profile.get("nickname");
                }
            }

        } else if ("naver".equals(registrationId)) {
            // 네이버는 유저 정보는 response Map 안에 담겨옴
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            if (response != null) {
                providerId = (String) response.get("id");
                email = (String) response.get("email");
                name = (String) response.get("name");
            }
        }

        log.info("소셜 로그인 시도 - Provider: {}, ProviderId: {}, Email: {}, Name: {}",
                registrationId.toUpperCase(), providerId, email, name);

        // providerId, provider로 조회하여 기존 유저 구분 없으면 로그인 처리
        UserVo userVo = null;
        try {
            providerId = sha256EncryptUtil.encryptSHA256(providerId);
            String provider = registrationId.toUpperCase();
            userVo = userMapper.selectUser(provider, providerId);

            if (userVo != null) {
                log.info("기존 회원 로그인 성공: {}", email, provider, providerId);
            } else {
                // 가입 여부 파악 및 가입 처리
                UserVo newUserVo = UserVo.builder()
                        .email(email)
                        .name(name)
                        .providerId(providerId)
                        .provider(registrationId.toUpperCase())
                        .age(0)
                        .build();

                userMapper.insertUser(newUserVo);
                userVo = newUserVo;
                log.info("소셜 회원가입 및 정보 저장 성공: {}", email);
            }

        } catch (Exception e) {
            log.error("소셜 유저 DB 저장 중 에러 발생", e);
            throw new OAuth2AuthenticationException("DB 처리 중 에러가 발생했습니다.");
        }

        // 유저 객체 반환
        return new CustomOAuth2User(userVo, attributes);
    }
}
