package com.bookwhale.push.domain;

import com.google.firebase.messaging.Message;
import java.util.HashMap;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FireBaseAccessTest {

    @DisplayName("Json 형태 메시지 생성 확인")
    @Test
    void makeMessageJson() {
        FireBaseAccess fireBaseAccess = new FireBaseAccess(null, null);
        Map<String, String> dataMap = new HashMap<>();
        String messageJson = fireBaseAccess.makeMessageJson("testToken", "테스트 타이틀", "테스트 본문", dataMap);

        Assertions.assertThat(messageJson).isNotNull();
    }

    @DisplayName("com.google.firebase.messaging.Message 객체 생성 확인")
    @Test
    void makeMessage() {
        FireBaseAccess fireBaseAccess = new FireBaseAccess(null, null);
        Map<String, String> dataMap = new HashMap<>();
        Message message = fireBaseAccess.makeMessage("testToken", "테스트 타이틀", "테스트 본문", dataMap);

        Assertions.assertThat(message).isNotNull();
    }
}