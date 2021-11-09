package com.bookwhale.security.oauth;

import com.bookwhale.security.UserPrincipal;
import com.bookwhale.security.oauth.user.OAuth2UserInfo;
import com.bookwhale.security.oauth.user.OAuth2UserInfoFactory;
import com.bookwhale.user.domain.AuthProvider;
import com.bookwhale.user.domain.Role;
import com.bookwhale.user.domain.User;
import com.bookwhale.user.domain.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

  private final UserRepository userRepository;

  /**
   * Authorization 서버에서 받아온 Access Token 으로 Resource Server 에게 유저 정보를 요청한다.
   */
  @Override
  public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) {
    OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
    try {
      return processOAuth2User(oAuth2UserRequest, oAuth2User);
    } catch (AuthenticationException e) {
      throw e;
    } catch (Exception e) {
      throw new InternalAuthenticationServiceException(e.getMessage(), e.getCause());
    }
  }

  private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
    OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
        oAuth2UserRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());
    if (StringUtils.isEmpty(oAuth2UserInfo.getEmail())) {
      throw new OAuth2AuthenticationProcessingException("OAuth2 공급자에서 이메일을 찾을 수 없습니다.");
    }
    User user = findUserBySocialEmail(oAuth2UserInfo,
        oAuth2UserRequest.getClientRegistration().getRegistrationId());
    return UserPrincipal.create(user, oAuth2User.getAttributes());
  }

  public User findUserBySocialEmail(OAuth2UserInfo userInfo, String provider) {
    Optional<User> userOptional = userRepository.findByIdentity(userInfo.getEmail());
    User user;
    if (userOptional.isPresent()) {
      user = userOptional.get();
      validateProvider(user, provider);
      user = updateUser(user, userInfo);
    } else {
      user = createUser(userInfo, provider);
    }
    return user;
  }

  public void validateProvider(User user, String provider) {
    if (!user.isSameProvider(provider)) {
      throw new OAuth2AuthenticationProcessingException("이미 존재하는 아이디입니다.");
    }
  }

  public User updateUser(User user, OAuth2UserInfo userInfo) {
    user.updateSocialUser(userInfo.getName());
    return userRepository.save(user);
  }

  public User createUser(OAuth2UserInfo userInfo, String provider) {
    return userRepository.save(
        User.builder()
            .identity(userInfo.getEmail())
            .name(userInfo.getName())
            .email(userInfo.getEmail())
            .role(Role.ROLE_USER)
            .provider(AuthProvider.valueOf(provider.toUpperCase()))
            .build()
    );
  }
}
