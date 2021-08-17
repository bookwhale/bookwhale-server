package com.teamherb.bookstoreback.common.security;

import com.teamherb.bookstoreback.security.UserPrincipal;
import com.teamherb.bookstoreback.user.domain.User;
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
            .address(customUser.address())
            .phoneNumber(customUser.phoneNumber())
            .role(customUser.roles())
            .build();

        UserPrincipal principal = UserPrincipal.create(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, user.getPassword(),
            principal.getAuthorities());
        context.setAuthentication(auth);
        return context;
    }
}
