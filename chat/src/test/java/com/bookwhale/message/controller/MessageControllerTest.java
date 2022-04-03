package com.bookwhale.message.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bookwhale.chatroom.service.ChatRoomService;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

@DisplayName("메세지 단위 테스트(Controller)")
@ActiveProfiles("test")
@WebMvcTest(controllers = MessageController.class)
@AutoConfigureRestDocs(uriPort = 8081)
@ExtendWith(RestDocumentationExtension.class)
public class MessageControllerTest {

    protected MockMvc mockMvc;

    protected ObjectMapper objectMapper;

    @MockBean
    protected MessageService messageService;

    @MockBean
    protected ChatRoomService chatRoomService;

    @MockBean
    protected SimpMessagingTemplate messagingTemplate;

    private final ParameterDescriptor[] requestMessageParams = new ParameterDescriptor[]{
        parameterWithName("page").description("이전 채팅 내용 DB 조회 시 offset"),
        parameterWithName("size").description("이전 채팅 내용 DB 조회 시 limit")
    };

    private final FieldDescriptor[] responseMessagesFields = new FieldDescriptor[]{
        fieldWithPath("[].senderId").description("메시지를 전송한 사용자 ID"),
        fieldWithPath("[].senderIdentity").description("메시지를 전송한 사용자 이름"),
        fieldWithPath("[].content").description("메시지 내용"),
        fieldWithPath("[].createdDate").description("메시지 생성일")
    };

    private final FieldDescriptor[] responseMessageFields = new FieldDescriptor[]{
        fieldWithPath("senderId").description("메시지를 전송한 사용자 ID"),
        fieldWithPath("senderIdentity").description("메시지를 전송한 사용자 이름"),
        fieldWithPath("content").description("메시지 내용"),
        fieldWithPath("createdDate").description("메시지 생성일")
    };

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext,
        RestDocumentationContextProvider restDocumentation) {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        this.mockMvc = MockMvcBuilders
            .webAppContextSetup(webApplicationContext)
            .addFilters(new CharacterEncodingFilter("UTF-8", true))
            .apply(documentationConfiguration(restDocumentation))
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

        mockMvc.perform(RestDocumentationRequestBuilders.get(
                "/api/message/{roomId}?page={page}&size={size}", roomId,
                pagination.getPage(), pagination.getSize()))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("chats/existMessages",
                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                pathParameters(
                    parameterWithName("roomId").description("채팅방 ID")
                ), requestParameters(
                    requestMessageParams
                ), responseFields(
                    responseMessagesFields
                )
            ));
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

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/message/{roomId}/last", roomId))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("chats/lastMessage",
                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                pathParameters(
                    parameterWithName("roomId").description("채팅방 ID")
                ), responseFields(
                    responseMessageFields
                )
            ));
    }
}
