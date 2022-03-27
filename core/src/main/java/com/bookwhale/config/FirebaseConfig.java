package com.bookwhale.config;

import com.bookwhale.push.domain.FireBaseAccess;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.http.HttpTransportOptions.DefaultHttpTransportFactory;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class FirebaseConfig {

    @Value("${external-api.firebase.api-url}")
    private String apiUrl;
    @Value("${external-api.firebase.type}")
    private String type;
    @Value("${external-api.firebase.project-id}")
    private String projectId;
    @Value("${external-api.firebase.private-key-id}")
    private String privateKeyId;
    @Value("${external-api.firebase.private-key}")
    private String privateKey;
    @Value("${external-api.firebase.client-email}")
    private String clientEmail;
    @Value("${external-api.firebase.client-id}")
    private String clientId;
    @Value("${external-api.firebase.auth-url}")
    private String authUri;
    @Value("${external-api.firebase.token-url}")
    private String tokenUri;
    @Value("${external-api.firebase.provider-cert-url}")
    private String providerCertUrl;
    @Value("${external-api.firebase.client-cert-url}")
    private String clientCertUrl;

    @Bean
    public FireBaseAccess generateAccessToken() {
        URI uri = null;
        try {
            uri = new URI(tokenUri);
        } catch (URISyntaxException e) {
            log.error("Token server URI specified in 'token_uri' could not be parsed.", e);
        }

        GoogleCredentials serviceAccountCredentials = null;
        try {
            serviceAccountCredentials = ServiceAccountCredentials.fromPkcs8(
                clientId,
                clientEmail,
                privateKey,
                privateKeyId,
                null,
                new DefaultHttpTransportFactory(),
                uri
                )
            .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
        } catch (IOException e) {
            log.error("Credential can not created.", e);
        }
        return new FireBaseAccess(serviceAccountCredentials, projectId, apiUrl);
    }
}
