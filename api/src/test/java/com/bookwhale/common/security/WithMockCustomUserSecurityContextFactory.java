package com.bookwhale.common.security;

import com.bookwhale.security.UserPrincipal;
import com.bookwhale.user.domain.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockCustomUserSecurityContextFactory implements
    WithSecurityContextFactory<WithMockCustomUser> {

  @Override
  public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
    SecurityContext context = SecurityContextHolder.createEmptyContext();

    User user = User.builder()
        .identity(customUser.identity())
        .password(customUser.password())
        .name(customUser.name())
        .email(customUser.email())
        .role(customUser.roles())
        .profileImage(customUser.profileImage())
        .provider(customUser.provider())
        .build();

    UserPrincipal principal = UserPrincipal.create(user);
    Authentication auth = new UsernamePasswordAuthenticationToken(principal, user.getPassword(),
        principal.getAuthorities());
    context.setAuthentication(auth);
    return context;
  }
}
