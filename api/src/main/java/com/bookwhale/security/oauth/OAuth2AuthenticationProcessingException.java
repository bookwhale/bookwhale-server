package com.bookwhale.security.oauth;

import org.springframework.security.core.AuthenticationException;

public class OAuth2AuthenticationProcessingException extends AuthenticationException {

  public OAuth2AuthenticationProcessingException(String msg) {
    super(msg);
  }
}
