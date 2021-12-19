package com.bookwhale.auth.domain.provider;

public interface OAuthProvider {
    String getOAuthRedirectURL();
    String requestAccessToken(String accessCode);
}
