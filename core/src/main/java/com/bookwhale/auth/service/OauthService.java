package com.bookwhale.auth.service;

import com.bookwhale.auth.domain.JWT;
import com.bookwhale.auth.domain.JWT.Claims;
import com.bookwhale.auth.domain.JWT.ClaimsForRefresh;
import com.bookwhale.auth.domain.Token;
import com.bookwhale.auth.domain.TokenRepository;
import com.bookwhale.auth.domain.info.UserInfo;
import com.bookwhale.auth.dto.OAuthLoginResponse;
import com.bookwhale.auth.dto.OAuthObjectConverter;
import com.bookwhale.auth.dto.OAuthRefreshLoginRequest;
import com.bookwhale.auth.dto.OAuthResultResponse;
import com.bookwhale.auth.service.provider.GoogleOAuthProvider;
import com.bookwhale.auth.service.provider.KakaoOAuthProvider;
import com.bookwhale.auth.service.provider.NaverOAuthProvider;
import com.bookwhale.auth.service.provider.OAuthProvider;
import com.bookwhale.auth.service.provider.OAuthProviderType;
import com.bookwhale.common.exception.CustomException;
import com.bookwhale.common.exception.ErrorCode;
import com.bookwhale.common.utils.RandomUtils;
import com.bookwhale.user.domain.User;
import com.bookwhale.user.service.UserService;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class OauthService {

    private final Map<String, OAuthProvider> oAuthProviders;
    private final HttpServletResponse response;
    private final JWT apiToken;
    private final TokenRepository tokenRepository;
    private final UserService userService;

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
            } else if (providerType.equals(OAuthProviderType.KAKAO)) {
                KakaoOAuthProvider oAuthProvider = (KakaoOAuthProvider) oAuthProviders.get(
                    "KakaoOAuthProvider");
                redirectUrl = oAuthProvider.getOAuthRedirectURL();
            }

            response.sendRedirect(redirectUrl);
        } catch (IOException e) {
            log.error("????????? ????????? redirection ??????", e);
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public OAuthLoginResponse loginProcess(OAuthProviderType providerType, String accessCode, String deviceToken) {
        // step1 : provider????????? ????????? ?????? ??????
        UserInfo loginUserInfo = OAuthObjectConverter.getUserInfoResponseFromProvider(
                oAuthProviders,
                providerType, accessCode)
            .orElseThrow(() -> new CustomException(ErrorCode.INFORMATION_NOT_FOUND));

        // step2 : ????????? ????????? ????????? apiToken ??????
        // step2-1 : ????????? ????????? ?????? ??????
        boolean isCreatedUser = userService.checkUserExists(loginUserInfo);
        if (!isCreatedUser) {
            userService.createUser(loginUserInfo);
        }

        String userEmail = loginUserInfo.getEmail();
        userService.updateUserDeviceToken(userEmail, deviceToken);

        // step2-2 : api token ??????
        String createdApiToken = OAuthObjectConverter.createApiToken(apiToken, loginUserInfo);

        // step3 : ?????? ???????????? RefreshToken ??????
        String randomString = RandomUtils.createRandomString();
        String refreshToken = OAuthObjectConverter.createRefreshToken(apiToken, randomString,
            userEmail);

        // step4 : ?????? ????????? ???????????? ?????? ???????????? ????????? ?????? (RefreshToken)
        Token refreshTokenInfo = Token.create(userEmail, randomString);
        tokenRepository.save(refreshTokenInfo);

        return new OAuthLoginResponse(createdApiToken, refreshToken);
    }

    @Transactional
    public OAuthLoginResponse requestAccessTokenAndIssueApiToken(OAuthProviderType providerType,
        String accessCode) {
        // step1 : provider????????? ????????? ?????? ?????? (accessCode??? AccessToken ?????? + ????????? ?????? ??????)
        UserInfo loginUserInfo = OAuthObjectConverter.requestAccessTokenAndGetLoginUserInfo(
                oAuthProviders,
                providerType, accessCode)
            .orElseThrow(() -> new CustomException(ErrorCode.INFORMATION_NOT_FOUND));

        // step2 : ????????? ????????? ?????? ??????
        boolean isCreatedUser = userService.checkUserExists(loginUserInfo);
        if (!isCreatedUser) {
            userService.createUser(loginUserInfo);
        }
        String userEmail = loginUserInfo.getEmail();


        // step3 : ????????? ????????? ????????? apiToken ??????
        // step3-1 : api token ??????
        String createdApiToken = OAuthObjectConverter.createApiToken(apiToken, loginUserInfo);

        // step3-2 : ?????? ???????????? RefreshToken ??????
        String randomString = RandomUtils.createRandomString();
        String refreshToken = OAuthObjectConverter.createRefreshToken(apiToken, randomString,
            userEmail);

        // step4 : ?????? ????????? ???????????? ?????? ???????????? ????????? ?????? (RefreshToken)
        Token refreshTokenInfo = Token.create(userEmail, randomString);
        tokenRepository.save(refreshTokenInfo);

        return new OAuthLoginResponse(createdApiToken, refreshToken);
    }

    public User getUserFromApiToken(String token) {
        Claims userClaim = apiToken.verify(token);

        return User.builder()
            .email(userClaim.getEmail())
            .nickname(userClaim.getName())
            .profileImage(userClaim.getImage())
            .build();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public OAuthLoginResponse apiTokenRefresh(OAuthRefreshLoginRequest refreshRequest) {
        // step1 : refresh token ??????
        String refreshToken = refreshRequest.getRefreshToken();
        ClaimsForRefresh refreshClaim = apiToken.verifyForRefresh(refreshToken);
        String rid = refreshClaim.getRid();
        String email = refreshClaim.getEmail();

        Token userRid = tokenRepository.findTokenByEmail(email)
            .orElseThrow(() -> new CustomException(ErrorCode.FORBIDDEN));

        if (!rid.equals(userRid.getTokenValue())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        // step2 : ????????? api token ??????
        String createdApiToken = apiToken.refreshApiToken(refreshRequest.getApiToken());

        return new OAuthLoginResponse(createdApiToken, refreshToken);
    }

    @Transactional
    public OAuthResultResponse expireToken(OAuthRefreshLoginRequest refreshRequest) {
        // step1 : refresh token ?????? ??? ??????
        String refreshToken = refreshRequest.getRefreshToken();
        ClaimsForRefresh refreshClaim = apiToken.verifyForRefresh(refreshToken);
        String email = refreshClaim.getEmail();

        Token userRid = tokenRepository.findTokenByEmail(email)
            .orElseThrow(() -> new CustomException(ErrorCode.FORBIDDEN));

        tokenRepository.delete(userRid);

        // step2 : apiToken ?????????
        // TODO ?????? ????????? ???????????? cache??? db??? ???????????? ????????? ???????????? ????????? ????????? ??? ????????? ??????????????? ????????? ????????? ?????????.

        return new OAuthResultResponse("???????????? ???????????????.");
    }

    @Transactional
    public OAuthResultResponse withdrawalUser(OAuthRefreshLoginRequest refreshRequest, User user) {
        // step1 : refresh token ?????? ??? ??????
        String refreshToken = refreshRequest.getRefreshToken();
        ClaimsForRefresh refreshClaim = apiToken.verifyForRefresh(refreshToken);
        String email = refreshClaim.getEmail();

        Optional<Token> userRid = tokenRepository.findTokenByEmail(email);
        userRid.ifPresent(tokenRepository::delete);

        // step2 : ????????? ?????? ??????
        userService.withdrawalUser(user);

        return new OAuthResultResponse("?????? ?????? ?????????????????????.");
    }
}
