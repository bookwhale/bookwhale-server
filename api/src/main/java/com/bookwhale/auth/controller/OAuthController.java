package com.bookwhale.auth.controller;

import com.bookwhale.auth.domain.CurrentUser;
import com.bookwhale.auth.dto.OAuthLoginResponse;
import com.bookwhale.auth.dto.OAuthRefreshLoginRequest;
import com.bookwhale.auth.dto.OAuthResultResponse;
import com.bookwhale.auth.service.OauthService;
import com.bookwhale.auth.service.provider.OAuthProviderType;
import com.bookwhale.user.domain.User;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/oauth")
@RestController
public class OAuthController {

    private final OauthService oauthService;

    /**
     * client에서 OAuth 로그인 요청을 providerType을 받아 처리
     *
     * @param providerType OAuth 로그인 기능 공급자 (provider : GOOGLE, NAVER)
     */
    @GetMapping(value = "/{providerType}")
    public void oAuthLoginRequest(
        @PathVariable(name = "providerType") OAuthProviderType providerType) {
        oauthService.sendLoginRequest(providerType);
    }

    /**
     * provider 에 로그인이 완료되면 전달받는 요청 키로 로그인 절차를 진행한다.
     *
     * @param providerType OAuth 로그인 기능 공급자(provider : GOOGLE, NAVER)
     * @param accessCode   요청 키
     * @param state        (optional) 네이버 상태값
     * @return
     */
    @GetMapping("/{providerType}/login")
    public ResponseEntity<OAuthLoginResponse> oAuthLoginProcess(
        @PathVariable OAuthProviderType providerType,
        @RequestParam(name = "code") String accessCode,
        @RequestParam(name = "state", required = false) String state) {

        OAuthLoginResponse result = oauthService.loginProcess(providerType, accessCode);

        return ResponseEntity.ok(result);
    }

    /**
     * 발급한 refreshToken을 확인하여 새로운 apiToken을 생성한다.
     *
     * @param refreshRequest apiToken, refreshToken 문자열
     * @return
     */
    @PostMapping("/refresh")
    public ResponseEntity<OAuthLoginResponse> refreshLogin(
        @Valid @RequestBody OAuthRefreshLoginRequest refreshRequest) {
        OAuthLoginResponse response = oauthService.apiTokenRefresh(refreshRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * 발급 시 저장했던 refreshToken 정보를 제거한다.
     *
     * @param refreshRequest apiToken, refreshToken 문자열
     * @return
     */
    @PostMapping("/logout")
    public ResponseEntity<OAuthResultResponse> oAuthLogoutRequest(
        @Valid @RequestBody OAuthRefreshLoginRequest refreshRequest) {
        OAuthResultResponse result = oauthService.expireToken(refreshRequest);

        return ResponseEntity.ok(result);
    }

    /**
     * 로그인 사용자의 정보를 제거하고 발급 시 저장했던 refreshToken 정보를 제거한다.
     *
     * @param user           로그인 한 사용자 (token)
     * @param refreshRequest apiToken, refreshToken 문자열
     * @return
     */
    @PostMapping("/withdrawal")
    public ResponseEntity<OAuthResultResponse> withdrawalUser(
        @CurrentUser User user,
        @Valid @RequestBody OAuthRefreshLoginRequest refreshRequest) {
        OAuthResultResponse result = oauthService.withdrawal(refreshRequest, user);

        return ResponseEntity.ok(result);
    }

}
