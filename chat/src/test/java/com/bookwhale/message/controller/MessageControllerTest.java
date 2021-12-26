package com.bookwhale.message.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bookwhale.common.dto.Pagination;
import com.bookwhale.message.dto.MessageResponse;
import com.bookwhale.message.service.MessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

@DisplayName("메세지 단위 테스트(Controller)")
@WebMvcTest(controllers = MessageController.class)
public class MessageControllerTest {

    MockMvc mockMvc;

    ObjectMapper objectMapper;

    @MockBean
    MessageService messageService;

    @MockBean
    SimpMessagingTemplate messagingTemplate;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext) {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        this.mockMvc = MockMvcBuilders
            .webAppContextSetup(webApplicationContext)
            .addFilters(new CharacterEncodingFilter("UTF-8", true))
            .build();
    }

    @Test
    @DisplayName("채팅방의 이전 메세지들을 조회한다.")
    public void findMessages() throws Exception {
        Long roomId = 1L;
        Pagination pagination = new Pagination(0, 10);
        MessageResponse messageResponse = MessageResponse.builder()
            .senderId(1L)
            .senderIdentity("highright96")
            .content("안녕하세요.")
            .createdDate(LocalDateTime.now())
            .build();

        when(messageService.findMessages(any(), any())).thenReturn(List.of(messageResponse));

        mockMvc.perform(get(
                "/api/message/{roomId}?page={page}&size={size}", roomId,
                pagination.getPage(), pagination.getSize()))
            .andExpect(status().isOk())
            .andDo(print());
    }

    @Test
    @DisplayName("채팅방의 마지막 메세지를 조회한다.")
    public void findLastMessage() throws Exception {
        Long roomId = 1L;
        MessageResponse messageResponse = MessageResponse.builder()
            .senderId(1L)
            .senderIdentity("highright96")
            .content("안녕하세요.")
            .createdDate(LocalDateTime.now())
            .build();

        when(messageService.findLastMessage(any())).thenReturn(messageResponse);

        mockMvc.perform(get("/api/message/{roomId}/last", roomId))
            .andExpect(status().isOk())
            .andDo(print());
    }
}
