package com.bookwhale.config;

import com.bookwhale.auth.domain.JWT;
import com.querydsl.jpa.impl.JPAQueryFactory;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class CoreConfig {

    private final EntityManager em;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(em);
    }

    @Value("${jwt.token.issuer}")
    String issuer;
    @Value("${jwt.token.clientSecret}")
    String clientSecret;
    @Value("${jwt.token.expirySecond}")
    int expirySecond;
    @Value("${jwt.token.expiryRefreshSecond}")
    int expiryRefreshSecond;

    @Bean
    public JWT jwt() {
        return new JWT(issuer, clientSecret, expirySecond, expiryRefreshSecond);
    }
}
