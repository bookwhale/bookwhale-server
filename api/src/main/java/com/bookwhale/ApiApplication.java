package com.bookwhale;

import static com.bookwhale.config.JasyptConfig.JASYPT_PASSWORD;

import com.bookwhale.config.JasyptConfig;
import com.ulisesbocchio.jasyptspringboot.environment.StandardEncryptableEnvironment;
import java.util.Optional;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class ApiApplication {
    
    public static void main(String[] args) {
        new SpringApplicationBuilder()
            .sources(ApiApplication.class)
            .environment(getEnvironment())
            .run(args);
    }

    private static StandardEncryptableEnvironment getEnvironment() {
        String password = Optional.ofNullable(System.getenv(JASYPT_PASSWORD))
            .orElseThrow(() -> new RuntimeException("프로퍼티 복호화 비밀번호를 찾을 수 없습니다."));

        return new StandardEncryptableEnvironment(
            null,
            null,
            null,
            null,
            JasyptConfig.createStringEncryptor(password),
            null);
    }
}
