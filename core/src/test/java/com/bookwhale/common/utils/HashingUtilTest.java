package com.bookwhale.common.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class HashingUtilTest {

    @Test
    void hashingString() {
        String testStr = "test@test.com";
        String hashedStr = HashingUtil.sha256(testStr);

        assertThat(testStr).isNotEqualTo(hashedStr);
        assertThat(hashedStr.getBytes(StandardCharsets.UTF_8).length).isEqualTo(64);
    }
}