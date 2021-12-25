package com.bookwhale.config;

import com.amazonaws.services.s3.AmazonS3;
import com.bookwhale.common.token.JWT;
import com.bookwhale.common.upload.AwsS3Uploader;
import com.bookwhale.common.upload.FileUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CharacterEncodingFilter;

@Configuration
@RequiredArgsConstructor
public class ApiConfig {

    private final AmazonS3 amazonS3;

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
