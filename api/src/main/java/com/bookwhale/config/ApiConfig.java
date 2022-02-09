package com.bookwhale.config;

import com.amazonaws.services.s3.AmazonS3;
import com.bookwhale.auth.domain.JWT;
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
}
