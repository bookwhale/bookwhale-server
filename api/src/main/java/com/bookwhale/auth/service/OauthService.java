package com.bookwhale.auth.service;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.bookwhale.auth.domain.OAuthObjectConverter;
import com.bookwhale.auth.domain.info.UserInfoFromGoogle;
import com.bookwhale.auth.domain.info.UserInfoFromNaver;
import com.bookwhale.auth.domain.provider.GoogleOAuthProvider;
import com.bookwhale.auth.domain.provider.NaverOAuthProvider;
import com.bookwhale.auth.domain.provider.OAuthProvider;
import com.bookwhale.auth.domain.provider.OAuthProviderType;
import com.bookwhale.auth.domain.token.GoogleOAuthToken;
import com.bookwhale.auth.domain.token.NaverOAuthToken;
import com.bookwhale.auth.dto.OAuthLoginResponse;
import com.bookwhale.common.exception.CustomException;
import com.bookwhale.common.exception.ErrorCode;
import com.bookwhale.common.token.JWT;
import com.bookwhale.common.token.JWT.Claims;
import com.bookwhale.user.domain.ApiUser;
import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class OauthService {

    private final Map<String, OAuthProvider> oAuthProviders;
    private final HttpServletResponse response;
    private final JWT apiToken;

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
            log.debug("로그인 페이지 redirection 실패", e);
        }
    }

    public OAuthLoginResponse loginProcess(OAuthProviderType providerType, String accessCode) {
        OAuthLoginResponse result = null;
        if (providerType.equals(OAuthProviderType.GOOGLE)) {
            // step 1 : accessToken 요청
            GoogleOAuthProvider oAuthProvider = (GoogleOAuthProvider) oAuthProviders.get(
                "GoogleOAuthProvider");
            ResponseEntity<String> accessTokenResponse = oAuthProvider.requestAccessToken(
                accessCode);

            if (!accessTokenResponse.getStatusCode().equals(HttpStatus.OK)) {
                throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
            }

            GoogleOAuthToken accessToken = (GoogleOAuthToken) OAuthObjectConverter.getTokenFromResponse(
                accessTokenResponse, providerType);

            // step 2 : 로그인된 사용자의 정보 요청
            ResponseEntity<String> userInfoResponse = oAuthProvider.getUserInfoFromProvider(
                accessToken);

            if (!userInfoResponse.getStatusCode().equals(HttpStatus.OK)) {
                throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
            }

            UserInfoFromGoogle userInfo = (UserInfoFromGoogle) OAuthObjectConverter.getUserInfoFromProvider(
                userInfoResponse, providerType);

            // step 3 : 확인된 사용자 정보를 바탕으로 JWT 생성
            Claims userClaim = Claims.of(
                userInfo.getName(),
                userInfo.getEmail(),
                userInfo.getPicture()
            );

            String newToken = apiToken.createNewToken(userClaim);
            result = new OAuthLoginResponse(newToken);


        } else if (providerType.equals(OAuthProviderType.NAVER)) {
            NaverOAuthProvider oAuthProvider = (NaverOAuthProvider) oAuthProviders.get(
                "NaverOAuthProvider");
            ResponseEntity<String> accessTokenResponse = oAuthProvider.requestAccessToken(
                accessCode);

            if (!accessTokenResponse.getStatusCode().equals(HttpStatus.OK)) {
                throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
            }

            NaverOAuthToken accessToken = (NaverOAuthToken) OAuthObjectConverter.getTokenFromResponse(
                accessTokenResponse, providerType);

            // step 2 : 로그인된 사용자의 정보 요청
            ResponseEntity<String> userInfoResponse = oAuthProvider.getUserInfoFromProvider(
                accessToken);

            if (!userInfoResponse.getStatusCode().equals(HttpStatus.OK)) {
                throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
            }

            UserInfoFromNaver userInfo = (UserInfoFromNaver) OAuthObjectConverter.getUserInfoFromProvider(
                userInfoResponse, providerType);

            // step 3 : 확인된 사용자 정보를 바탕으로 JWT 생성
            Claims userClaim = Claims.of(
                userInfo.getName(),
                userInfo.getEmail(),
                userInfo.getPicture()
            );

            String newToken = apiToken.createNewToken(userClaim);
            result = new OAuthLoginResponse(newToken);
        }

        return result;
    }

    public ApiUser getUserFromApiToken(String token) {
        Claims userClaim;
        try {
            userClaim = apiToken.verify(token);
        } catch (JWTVerificationException e) {
            log.error("token verify failed.", e);
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        return ApiUser.builder()
            .name(userClaim.getName())
            .image(userClaim.getImage())
            .email(userClaim.getEmail())
            .build();
    }


}
