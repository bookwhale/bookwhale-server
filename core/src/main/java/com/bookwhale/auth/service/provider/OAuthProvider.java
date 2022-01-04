package com.bookwhale.auth.service.provider;

import org.springframework.http.ResponseEntity;

public interface OAuthProvider {

    String getOAuthRedirectURL();

    ResponseEntity<String> requestAccessToken(String accessCode);
}
