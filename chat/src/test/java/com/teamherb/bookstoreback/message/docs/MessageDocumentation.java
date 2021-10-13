package com.teamherb.bookstoreback.message.docs;

/*
public class MessageDocumentation {

  public static RestDocumentationResultHandler findMessages() {
    FieldDescriptor[] response = new FieldDescriptor[]{
        fieldWithPath("senderId").type(JsonFieldType.NUMBER).description("메세지를 보낸 유저 ID"),
        fieldWithPath("senderIdentity").type(JsonFieldType.STRING).description("메세지를 보낸 유저 아이디"),
        fieldWithPath("content").type(JsonFieldType.STRING).description("메세지 내용"),
        fieldWithPath("createdDate").type(JsonFieldType.STRING).description("메세지를 보낸 시간")
    };

    return document("message/findMessages",
        preprocessRequest(prettyPrint()),
        preprocessResponse(prettyPrint()),
        requestHeaders(
            headerWithName(HttpHeaders.AUTHORIZATION).description("접속 인증 정보가 담긴 JWT")
        ),
        pathParameters(
            parameterWithName("roomId").description("채팅방 ID")
        ),
        requestParameters(
            parameterWithName("page").description("페이지(0부터 시작) (필수)"),
            parameterWithName("size").description("한 페이지 내의 사이즈 (필수)")
        ),
        responseFields(fieldWithPath("[]").description("An arrays of message"))
            .andWithPrefix("[].", response)
    );
  }

  public static RestDocumentationResultHandler findLastMessage() {
    return document("message/findLastMessage",
        preprocessRequest(prettyPrint()),
        preprocessResponse(prettyPrint()),
        requestHeaders(
            headerWithName(HttpHeaders.AUTHORIZATION).description("접속 인증 정보가 담긴 JWT")
        ),
        pathParameters(
            parameterWithName("roomId").description("채팅방 ID")
        ),
        responseFields(
            fieldWithPath("senderId").type(JsonFieldType.NUMBER).description("메세지를 보낸 유저 ID"),
            fieldWithPath("senderIdentity").type(JsonFieldType.STRING)
                .description("메세지를 보낸 유저 아이디"),
            fieldWithPath("content").type(JsonFieldType.STRING).description("메세지 내용"),
            fieldWithPath("createdDate").type(JsonFieldType.STRING).description("메세지를 보낸 시간")
        )
    );
  }
}
*/