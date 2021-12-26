package com.bookwhale.auth.controller;

import com.bookwhale.auth.domain.provider.OAuthProviderType;
import com.bookwhale.auth.dto.OAuthLoginResponse;
import com.bookwhale.auth.dto.OAuthRefreshLoginRequest;
import com.bookwhale.auth.service.OauthService;
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
     * @param OAuthProviderType (GOOGLE, NAVER)
     */
    @GetMapping(value = "/{providerType}")
    public void oAuthLoginRequest(
        @PathVariable(name = "providerType") OAuthProviderType providerType) {
        oauthService.sendLoginRequest(providerType);
    }

    @GetMapping("/{providerType}/login")
    public ResponseEntity<OAuthLoginResponse> oAuthLoginProcess(
        @PathVariable OAuthProviderType providerType,
        @RequestParam(name = "code") String accessCode,
        @RequestParam(name = "state", required = false) String state) {

        OAuthLoginResponse result = oauthService.loginProcess(providerType, accessCode);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/refresh")
    public ResponseEntity<OAuthLoginResponse> refreshLogin(
        @Valid @RequestBody OAuthRefreshLoginRequest refreshRequest) {
        OAuthLoginResponse response = oauthService.apiTokenRefresh(refreshRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> oAuthLogoutRequest(
        @Valid @RequestBody OAuthRefreshLoginRequest refreshRequest) {
        String result = oauthService.expireToken(refreshRequest);

        return ResponseEntity.ok(result);
    }

}
