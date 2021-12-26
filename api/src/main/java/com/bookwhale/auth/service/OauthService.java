package com.bookwhale.auth.service;

import com.bookwhale.auth.domain.OAuthObjectConverter;
import com.bookwhale.auth.domain.Token;
import com.bookwhale.auth.domain.TokenRepository;
import com.bookwhale.auth.domain.info.UserInfo;
import com.bookwhale.auth.domain.provider.GoogleOAuthProvider;
import com.bookwhale.auth.domain.provider.NaverOAuthProvider;
import com.bookwhale.auth.domain.provider.OAuthProvider;
import com.bookwhale.auth.domain.provider.OAuthProviderType;
import com.bookwhale.auth.dto.OAuthLoginResponse;
import com.bookwhale.auth.dto.OAuthRefreshLoginRequest;
import com.bookwhale.common.exception.CustomException;
import com.bookwhale.common.exception.ErrorCode;
import com.bookwhale.common.token.JWT;
import com.bookwhale.common.token.JWT.Claims;
import com.bookwhale.common.token.JWT.ClaimsForRefresh;
import com.bookwhale.common.utils.RandomUtils;
import com.bookwhale.user.domain.ApiUser;
import com.bookwhale.user.domain.UserRepository;
import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class OauthService {

    private final Map<String, OAuthProvider> oAuthProviders;
    private final HttpServletResponse response;
    private final JWT apiToken;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    public void sendLoginRequest(OAuthProviderType providerType) {
        try {
            String redirectUrl = null;
            if (providerType.equals(OAuthProviderType.GOOGLE)) {
                GoogleOAuthProvider oAuthProvider = (GoogleOAuthProvider) oAuthProviders.get(
                    "GoogleOAuthProvider");
                redirectUrl = oAuthProvider.getOAuthRedirectURL();
            } else if (providerType.equals(OAuthProviderType.NAVER)) {
                NaverOAuthProvider oAuthProvider = (NaverOAuthProvider) oAuthProviders.get(
                    "NaverOAuthProvider");
                redirectUrl = oAuthProvider.getOAuthRedirectURL();
            }

            response.sendRedirect(redirectUrl);
        } catch (IOException e) {
            log.error("로그인 페이지 redirection 실패", e);
        }
    }

    @Transactional
    public OAuthLoginResponse loginProcess(OAuthProviderType providerType, String accessCode) {
        // step1 : provider로부터 사용자 정보 확인
        UserInfo loginUserInfo = OAuthObjectConverter.getOAuthLoginResponse(oAuthProviders,
                providerType, accessCode)
            .orElseThrow(() -> new CustomException(ErrorCode.INFORMATION_NOT_FOUND));

        // step2 : 확인된 사용자 정보로 apiToken 생성
        String createdApiToken = OAuthObjectConverter.createApiToken(apiToken, loginUserInfo);

        // step3 : 랜덤 문자열로 RefreshToken 생성
        String randomString = RandomUtils.createRandomString();
        String userEmail = loginUserInfo.getEmail();
        String refreshToken = OAuthObjectConverter.createRefreshToken(apiToken, randomString,
            userEmail);

        // step4 : 토큰 생성에 성공하면 랜덤 문자열을 서버에 저장 (RefreshToken)
        Token refreshTokenInfo = Token.create(userEmail, randomString);
        tokenRepository.save(refreshTokenInfo);

        return new OAuthLoginResponse(createdApiToken, refreshToken);
    }

    public ApiUser getUserFromApiToken(String token) {
        Claims userClaim = apiToken.verify(token);

        return ApiUser.builder().name(userClaim.getName()).image(userClaim.getImage())
            .email(userClaim.getEmail()).build();
    }

    public OAuthLoginResponse apiTokenRefresh(OAuthRefreshLoginRequest refreshRequest) {
        // step1 : refresh token 확인
        String refreshToken = refreshRequest.getRefreshToken();
        ClaimsForRefresh refreshClaim = apiToken.verifyForRefresh(refreshToken);
        String rid = refreshClaim.getRid();
        String email = refreshClaim.getEmail();

        Token userRid = tokenRepository.findTokenByEmail(email);

        if (!rid.equals(userRid.getTokenValue())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        // step2 : refresh token 으로 새로운 api token 생성
        String createdApiToken = apiToken.refreshApiToken(refreshRequest.getApiToken());

        return new OAuthLoginResponse(createdApiToken, refreshToken);
    }
}
