package com.bookwhale.auth.domain.provider;

import org.springframework.http.ResponseEntity;

public interface OAuthProvider {

    String getOAuthRedirectURL();

    ResponseEntity<String> requestAccessToken(String accessCode);
}
