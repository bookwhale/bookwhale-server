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
import com.bookwhale.auth.dto.provider.GoogleOAuthProvider;
import com.bookwhale.auth.dto.provider.NaverOAuthProvider;
import com.bookwhale.auth.dto.provider.OAuthProvider;
import com.bookwhale.auth.dto.provider.OAuthProviderType;
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
        // step2-1 : 확인된 사용자 정보 저장
        boolean isCreatedUser = userService.checkUserExists(loginUserInfo);
        if (!isCreatedUser) {
            userService.createUser(loginUserInfo);
        }

        // step2-2 : api token 생성
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
        // step1 : refresh token 확인
        String refreshToken = refreshRequest.getRefreshToken();
        ClaimsForRefresh refreshClaim = apiToken.verifyForRefresh(refreshToken);
        String rid = refreshClaim.getRid();
        String email = refreshClaim.getEmail();

        Token userRid = tokenRepository.findTokenByEmail(email)
            .orElseThrow(() -> new CustomException(ErrorCode.FORBIDDEN));

        if (!rid.equals(userRid.getTokenValue())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        // step2 : 새로운 api token 생성
        String createdApiToken = apiToken.refreshApiToken(refreshRequest.getApiToken());

        return new OAuthLoginResponse(createdApiToken, refreshToken);
    }

    @Transactional
    public String expireToken(OAuthRefreshLoginRequest refreshRequest) {
        // step1 : refresh token 확인 후 삭제
        String refreshToken = refreshRequest.getRefreshToken();
        ClaimsForRefresh refreshClaim = apiToken.verifyForRefresh(refreshToken);
        String email = refreshClaim.getEmail();

        Token userRid = tokenRepository.findTokenByEmail(email)
            .orElseThrow(() -> new CustomException(ErrorCode.FORBIDDEN));

        tokenRepository.delete(userRid);

        // step2 : apiToken 불용화
        // TODO 해당 로직에 대해서는 cache형 db를 사용하여 가용한 토큰인지 정보를 보관할 수 있다면 추가작업이 가능할 것으로 판단됨.

        return "로그아웃 되었습니다.";
    }

    @Transactional
    public String withdrawal(OAuthRefreshLoginRequest refreshRequest, User user) {
        // step1 : refresh token 확인 후 삭제
        String refreshToken = refreshRequest.getRefreshToken();
        ClaimsForRefresh refreshClaim = apiToken.verifyForRefresh(refreshToken);
        String email = refreshClaim.getEmail();

        Optional<Token> userRid = tokenRepository.findTokenByEmail(email);
        userRid.ifPresent(tokenRepository::delete);

        // step2 : 사용자 정보 삭제
        userService.withdrawalUser(user);

        return "회원 탈퇴 완료되었습니다.";
    }
}
