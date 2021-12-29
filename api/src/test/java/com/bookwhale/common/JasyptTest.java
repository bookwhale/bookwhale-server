package com.bookwhale.common;

import static com.bookwhale.config.JasyptConfig.JASYPT_PASSWORD;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class JasyptTest {

    @Test
    void 환경_변수_테스트() {
        String jasyptPassword = System.getenv(JASYPT_PASSWORD);
        assertThat(jasyptPassword).isNotNull();
    }
}
