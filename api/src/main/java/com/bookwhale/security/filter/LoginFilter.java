package com.bookwhale.security.filter;

import com.bookwhale.user.dto.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

  private final AuthenticationManager authenticationManager;

  @Override
  public Authentication attemptAuthentication(
      HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

    if (!request.getMethod().equals("POST")) {
      throw new AuthenticationServiceException(
          "Authentication method not supported: " + request.getMethod());
    }

    LoginRequest loginRequest = null;
    try {
      loginRequest = new ObjectMapper()
          .readValue(request.getInputStream(), LoginRequest.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
    String identity = loginRequest.getIdentity();
    identity = (identity != null) ? identity : "";
    identity = identity.trim();
    String password = loginRequest.getPassword();
    password = (password != null) ? password : "";

    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
        identity, password);
    return authenticationManager.authenticate(authenticationToken);
  }
}
