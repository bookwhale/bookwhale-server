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
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;

import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
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
            fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
            fieldWithPath("phoneNumber").type(JsonFieldType.STRING).description("전화번호"),
            fieldWithPath("email").type(JsonFieldType.STRING).description("이메일")
        ));
  }

  public static RestDocumentationResultHandler userUpdatePassword() {
    return document("user/updatePassword",
        preprocessRequest(prettyPrint()),
        requestHeaders(
            headerWithName(HttpHeaders.AUTHORIZATION).description("접속 인증 정보가 담긴 JWT")
        ),
        requestFields(
            fieldWithPath("oldPassword").type(JsonFieldType.STRING).description("기존 비밀번호"),
            fieldWithPath("newPassword").type(JsonFieldType.STRING).description("변경할 비밀번호")
        ));
  }

  public static RestDocumentationResultHandler userUploadProfileImage() {
    return document("user/uploadProfileImage",
        preprocessRequest(prettyPrint()),
        preprocessResponse(prettyPrint()),
        requestHeaders(
            headerWithName(HttpHeaders.AUTHORIZATION).description("접속 인증 정보가 담긴 JWT")
        ),
        requestParts(
            partWithName("profileImage").description("업로드할 프로필 사진")
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

  public static RestDocumentationResultHandler userFindInterests() {
    FieldDescriptor[] response = new FieldDescriptor[]{
        fieldWithPath("interestId").type(JsonFieldType.NUMBER).description("관심목록 ID"),
        fieldWithPath("postsResponse.postId").type(JsonFieldType.NUMBER).description("게시글 ID"),
        fieldWithPath("postsResponse.postTitle").type(JsonFieldType.STRING).description("게시글 제목"),
        fieldWithPath("postsResponse.postPrice").type(JsonFieldType.STRING).description("게시글 가격"),
        fieldWithPath("postsResponse.bookTitle").type(JsonFieldType.STRING).description("책 제목"),
        fieldWithPath("postsResponse.createdDate").type(JsonFieldType.STRING).description("게시글 등록일"),
        fieldWithPath("postsResponse.postImage").type(JsonFieldType.STRING).description("판매자가 올린 이미지"),
        fieldWithPath("postsResponse.postStatus").type(JsonFieldType.STRING).description(
            "게시글 상태 [SALE, RESERVED, SOLD_OUT]")
    };

    return document("user/findInterests",
        preprocessRequest(prettyPrint()),
        preprocessResponse(prettyPrint()),
        requestHeaders(
            headerWithName(HttpHeaders.AUTHORIZATION).description("접속 인증 정보가 담긴 JWT")
        ),
        responseFields(fieldWithPath("[]").description("An arrays of interest"))
            .andWithPrefix("[].", response)
    );
  }

  public static RestDocumentationResultHandler userAddInterest() {
    return document("user/addInterest",
        preprocessRequest(prettyPrint()),
        preprocessResponse(prettyPrint()),
        requestHeaders(
            headerWithName(HttpHeaders.AUTHORIZATION).description("접속 인증 정보가 담긴 JWT")
        ), requestFields(
            fieldWithPath("postId").type(JsonFieldType.NUMBER).description("게시글 ID")
        )
    );
  }

  public static RestDocumentationResultHandler userDeleteInterest() {
    return document("user/deleteInterest",
        preprocessResponse(prettyPrint()),
        requestHeaders(
            headerWithName(HttpHeaders.AUTHORIZATION).description("접속 인증 정보가 담긴 JWT")
        ),
        pathParameters(
            parameterWithName("interestId").description("관심목록 ID")
        )
    );
  }

  public static RestDocumentationResultHandler userFindMyPosts() {
    FieldDescriptor[] response = new FieldDescriptor[]{
        fieldWithPath("postId").type(JsonFieldType.NUMBER).description("게시글 ID"),
        fieldWithPath("postTitle").type(JsonFieldType.STRING).description("게시글 제목"),
        fieldWithPath("postPrice").type(JsonFieldType.STRING).description("게시글 가격"),
        fieldWithPath("bookTitle").type(JsonFieldType.STRING).description("책 제목"),
        fieldWithPath("createdDate").type(JsonFieldType.STRING).description("게시글 등록일"),
        fieldWithPath("postImage").type(JsonFieldType.STRING).description("판매자가 올린 이미지"),
        fieldWithPath("postStatus").type(JsonFieldType.STRING).description(
            "게시글 상태 [SALE, RESERVED, SOLD_OUT]")
    };

    return document("user/findMyPosts",
        preprocessRequest(prettyPrint()),
        preprocessResponse(prettyPrint()),
        requestHeaders(
            headerWithName(HttpHeaders.AUTHORIZATION).description("접속 인증 정보가 담긴 JWT")
        ),
        responseFields(fieldWithPath("[]").description("An arrays of postsResponse"))
            .andWithPrefix("[].", response)
    );
  }
}
