package com.teamherb.bookstoreback.common.security;

import com.teamherb.bookstoreback.user.domain.Role;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {

  String identity() default "highright96";

  String password() default "1234";

  String email() default "highright96@email.com";

  String name() default "남상우";

  String phoneNumber() default "010-1234-1234";

  Role roles() default Role.ROLE_USER;
}
