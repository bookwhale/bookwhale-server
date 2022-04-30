package com.bookwhale.chatroom.docs;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;

public class ChatRoomDocumentations {

    public static RestDocumentationResultHandler createChatRoom() {
        return document("chatRoom/createChatRoom",
            preprocessRequest(prettyPrint()),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("접속 인증 정보가 담긴 JWT")
            ),
            requestFields(
                fieldWithPath("articleId").type(JsonFieldType.NUMBER).description("게시글 ID"),
                fieldWithPath("sellerId").type(JsonFieldType.NUMBER).description("판매자 ID")
            )
        );
    }

    public static RestDocumentationResultHandler findChatRooms() {
        FieldDescriptor[] response = new FieldDescriptor[]{
            fieldWithPath("roomId").type(JsonFieldType.NUMBER).description("채팅방 ID"),
            fieldWithPath("articleId").type(JsonFieldType.NUMBER).description("게시글 ID"),
            fieldWithPath("articleTitle").type(JsonFieldType.STRING).description("게시글 제목"),
            fieldWithPath("articleImage").type(JsonFieldType.STRING).description("유저가 올린 이미지"),
            fieldWithPath("opponentIdentity").type(JsonFieldType.STRING).description("상대방 아이디"),
            fieldWithPath("opponentProfile").type(JsonFieldType.STRING).description("상대방 프로필 사진"),
            fieldWithPath("opponentDelete").type(JsonFieldType.BOOLEAN).description(
                "상대방이 나간 채팅방 여부"),
            fieldWithPath("roomCreateAt").type(JsonFieldType.STRING).description(
                "채팅방 생성 일시"),
            fieldWithPath("lastContent").type(JsonFieldType.STRING).optional().description(
                "채팅방의 마지막 메시지 (* 없는 경우에는 null)"),
            fieldWithPath("lastContentCreateAt").type(JsonFieldType.STRING).optional().description(
                "마지막 메시지 생성 일시 (* 없는 경우에는 null)")
        };

        return document("chatRoom/findChatRooms",
            preprocessResponse(prettyPrint()),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("접속 인증 정보가 담긴 JWT")
            ),
            responseFields(fieldWithPath("[]").description("An arrays of chatRoom"))
                .andWithPrefix("[].", response)
        );
    }

    public static RestDocumentationResultHandler findChatRoom() {
        return document("chatRoom/findChatRoom",
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("접속 인증 정보가 담긴 JWT")
            ),
            responseFields(
                fieldWithPath("roomId").type(JsonFieldType.NUMBER).description("채팅방 ID"),
                fieldWithPath("articleId").type(JsonFieldType.NUMBER).description("게시글 ID"),
                fieldWithPath("articleTitle").type(JsonFieldType.STRING).description("게시글 제목"),
                fieldWithPath("articleImage").type(JsonFieldType.STRING).description("유저가 올린 이미지"),
                fieldWithPath("opponentIdentity").type(JsonFieldType.STRING).description("상대방 아이디"),
                fieldWithPath("opponentProfile").type(JsonFieldType.STRING).description("상대방 프로필 사진"),
                fieldWithPath("opponentDelete").type(JsonFieldType.BOOLEAN).description(
                    "상대방이 나간 채팅방 여부"),
                fieldWithPath("roomCreateAt").type(JsonFieldType.STRING).description(
                    "채팅방 생성 일시"),
                fieldWithPath("lastContent").type(JsonFieldType.STRING).optional().description(
                    "채팅방의 마지막 메시지 (* 없는 경우에는 null)"),
                fieldWithPath("lastContentCreateAt").type(JsonFieldType.STRING).optional().description(
                    "마지막 메시지 생성 일시 (* 없는 경우에는 null)")
            )
        );
    }

    public static RestDocumentationResultHandler deleteChatRoom() {
        return document("chatRoom/deleteChatRoom",
            preprocessRequest(prettyPrint()),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("접속 인증 정보가 담긴 JWT")
            ),
            pathParameters(
                parameterWithName("roomId").description("채팅방 ID")
            )
        );
    }
}
