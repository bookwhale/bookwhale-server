package com.bookwhale.auth.controller;

import com.bookwhale.auth.domain.provider.OAuthProviderType;
import com.bookwhale.auth.dto.OAuthLoginResponse;
import com.bookwhale.auth.service.OauthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/auth")
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
        log.info("사용자로부터 OAuth 로그인 요청 : {}", providerType);
        oauthService.sendLoginRequest(providerType);
    }

    @GetMapping("/{providerType}/login")
    public ResponseEntity<OAuthLoginResponse> oAuthLoginProcess(
        @PathVariable OAuthProviderType providerType,
        @RequestParam(name = "code") String accessCode,
        @RequestParam(name = "state", required = false) String state) {
        log.info("전달받은 코드 확인 : {}", accessCode);
        log.info("전달받은 상태 확인(naver) : {}", state);

        OAuthLoginResponse result = oauthService.loginProcess(providerType, accessCode);

        return ResponseEntity.ok(result);
    }
}
