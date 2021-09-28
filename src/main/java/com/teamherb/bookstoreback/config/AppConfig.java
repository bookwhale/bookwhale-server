package com.teamherb.bookstoreback.config;

import com.amazonaws.services.s3.AmazonS3;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.teamherb.bookstoreback.common.utils.upload.AwsS3Uploader;
import com.teamherb.bookstoreback.common.utils.upload.FileUploader;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CharacterEncodingFilter;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

  private final EntityManager em;

  private final AmazonS3 amazonS3;

  @Bean
  public JPAQueryFactory jpaQueryFactory() {
    return new JPAQueryFactory(em);
  }

  @Bean
  public FileUploader fileUploader() {
    return new AwsS3Uploader(amazonS3);
  }

  @Bean
  public CharacterEncodingFilter characterEncodingFilter() {
    CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
    characterEncodingFilter.setEncoding("UTF-8");
    characterEncodingFilter.setForceEncoding(true);
    return characterEncodingFilter;
  }
}
