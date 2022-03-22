package com.bookwhale.config;

import com.bookwhale.push.domain.FireBaseAccess;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.path}")
    private String keyPath;
    @Value("${firebase.url}")
    private String apiUrl;

    @Bean
    public FireBaseAccess generateAccessToken() {
        return new FireBaseAccess(keyPath, apiUrl);
    }
}
