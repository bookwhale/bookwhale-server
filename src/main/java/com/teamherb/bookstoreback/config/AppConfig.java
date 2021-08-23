package com.teamherb.bookstoreback.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.teamherb.bookstoreback.common.utils.upload.FileStoreOnServerUtil;
import com.teamherb.bookstoreback.common.utils.upload.FileStoreUtil;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

    private final EntityManager em;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(em);
    }

    @Bean
    public FileStoreUtil fileStoreUtil() {
        return new FileStoreOnServerUtil();
    }
}
