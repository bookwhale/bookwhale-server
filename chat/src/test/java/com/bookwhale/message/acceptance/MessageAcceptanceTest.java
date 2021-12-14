package com.bookwhale.message.acceptance;

import com.bookwhale.common.dto.Pagination;
import com.bookwhale.message.acceptance.step.AcceptanceStep;
import com.bookwhale.message.acceptance.step.MessageAcceptanceStep;
import com.bookwhale.message.domain.Message;
import com.bookwhale.message.domain.MessageRepository;
import com.bookwhale.message.dto.MessageResponse;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.time.LocalDateTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("메세지 통합 테스트")
public class MessageAcceptanceTest extends AcceptanceTest {

    @Autowired
    MessageRepository messageRepository;

    Long roomId = 2L;

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();
        Message message1 = Message.builder()
            .roomId(roomId)
            .content("1번 메세지입니다.")
            .createdDate(LocalDateTime.of(2021, 10, 18, 1, 1))
            .build();
        Message message2 = Message.builder()
            .roomId(roomId)
            .content("2번 메세지입니다.")
            .createdDate(LocalDateTime.of(2021, 10, 18, 1, 2))
            .build();

        messageRepository.save(message1);
        messageRepository.save(message2);
    }

    @DisplayName("채팅방의 이전 메세지들을 조회한다.")
    @Test
    void findMessages() {
        Pagination pagination = new Pagination(0, 5);

        ExtractableResponse<Response> response = MessageAcceptanceStep.requestToFindMessages(roomId,
            pagination);
        List<MessageResponse> messageResponses = response.jsonPath()
            .getList("", MessageResponse.class);

        AcceptanceStep.assertThatStatusIsOk(response);
        MessageAcceptanceStep.assertThatFindMessages(messageResponses);
    }

    @DisplayName("채팅방의 마지막 메세지를 조회한다.")
    @Test
    void findLastMessage() {
        ExtractableResponse<Response> res = MessageAcceptanceStep.requestToFindLastMessage(roomId);
        MessageResponse messageResponse = res.jsonPath().getObject("", MessageResponse.class);

        AcceptanceStep.assertThatStatusIsOk(res);
        Assertions.assertThat(messageResponse).isNotNull();
    }
}