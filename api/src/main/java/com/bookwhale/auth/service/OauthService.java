package com.bookwhale.auth.service;

import com.bookwhale.auth.domain.provider.GoogleOAuthProvider;
import com.bookwhale.auth.domain.provider.NaverOAuthProvider;
import com.bookwhale.auth.domain.provider.OAuthProvider;
import com.bookwhale.auth.domain.provider.OAuthProviderType;
import com.bookwhale.auth.dto.OAuthLoginRequest;
import com.bookwhale.auth.dto.OAuthLoginResponse;
import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class OauthService {

    private final Map<String, OAuthProvider> oAuthProviders;
    private final HttpServletResponse response;

    public void sendLoginRequest(OAuthProviderType providerType) {
        try {
            String redirectUrl = null;
            if (providerType.equals(OAuthProviderType.GOOGLE)) {
                GoogleOAuthProvider oAuthProvider = (GoogleOAuthProvider) oAuthProviders.get("GoogleOAuthProvider");
                redirectUrl = oAuthProvider.getOAuthRedirectURL();
            } else if (providerType.equals(OAuthProviderType.NAVER)) {
                NaverOAuthProvider oAuthProvider = (NaverOAuthProvider) oAuthProviders.get("NaverOAuthProvider");
                redirectUrl = oAuthProvider.getOAuthRedirectURL();
            }

            response.sendRedirect(redirectUrl);
        } catch (IOException e) {
            log.debug("로그인 페이지 redirection 실패", e);
        }
    }

    public OAuthLoginResponse processLogin(OAuthProviderType providerType, String accessCode) {
        return null;
    }

}
