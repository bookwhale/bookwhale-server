package com.teamherb.bookstoreback.user.docs;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;

public class UserDocumentation {

  public static RestDocumentationResultHandler userSignup() {
    return document("user/signup",
        preprocessRequest(prettyPrint()),
        requestFields(
            fieldWithPath("identity").type(JsonFieldType.STRING).description("아이디"),
            fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"),
            fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
            fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
            fieldWithPath("phoneNumber").type(JsonFieldType.STRING).description("전화번호")
        )
    );
  }

  public static RestDocumentationResultHandler userLogin() {
    return document("user/userLogin",
        preprocessRequest(prettyPrint()),
        requestFields(
            fieldWithPath("identity").type(JsonFieldType.STRING).description("아이디"),
            fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
        ));
  }

  public static RestDocumentationResultHandler userMe() {
    return document("user/me",
        preprocessResponse(prettyPrint()),
        requestHeaders(
            headerWithName(HttpHeaders.AUTHORIZATION).description("접속 인증 정보가 담긴 JWT")
        ),
        responseFields(
            fieldWithPath("identity").type(JsonFieldType.STRING).description("아이디"),
            fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
            fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
            fieldWithPath("phoneNumber").type(JsonFieldType.STRING).description("전화번호"),
            fieldWithPath("profileImage").type(JsonFieldType.STRING).description("프로필 이미지")
        ));
  }

  public static RestDocumentationResultHandler userUpdateMe() {
    return document("user/updateMe",
        preprocessRequest(prettyPrint()),
        requestHeaders(
            headerWithName(HttpHeaders.AUTHORIZATION).description("접속 인증 정보가 담긴 JWT")
        ),
        requestFields(
            fieldWithPath("name").type(JsonFieldType.STRING).description("이름").optional(),
            fieldWithPath("phoneNumber").type(JsonFieldType.STRING).description("전화번호").optional(),
            fieldWithPath("email").type(JsonFieldType.STRING).description("이메일").optional()
        ));
  }

  public static RestDocumentationResultHandler userUploadProfileImage() {
    return document("user/uploadProfileImage",
        preprocessRequest(prettyPrint()),
        preprocessResponse(prettyPrint()),
        requestHeaders(
            headerWithName(HttpHeaders.AUTHORIZATION).description("접속 인증 정보가 담긴 JWT")
        ),
        responseFields(
            fieldWithPath("profileImage").type(JsonFieldType.STRING).description("업로드된 유저 이미지")
        )
    );
  }

  public static RestDocumentationResultHandler userDeleteProfileImage() {
    return document("user/deleteProfileImage",
        preprocessRequest(prettyPrint()),
        preprocessResponse(prettyPrint()),
        requestHeaders(
            headerWithName(HttpHeaders.AUTHORIZATION).description("접속 인증 정보가 담긴 JWT")
        )
    );
  }
}
