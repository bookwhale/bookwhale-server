package com.bookwhale.user.docs;

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
                fieldWithPath("nickname").type(JsonFieldType.STRING).description("이름"),
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
                fieldWithPath("userId").type(JsonFieldType.NUMBER).description("유저 id"),
                fieldWithPath("nickName").type(JsonFieldType.STRING).description("닉네임"),
                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                fieldWithPath("profileImage").type(JsonFieldType.STRING).description("프로필 이미지").optional(),
                fieldWithPath("pushActivate").type(JsonFieldType.STRING).description("push 알림 on/off 상태")
            ));
    }

    public static RestDocumentationResultHandler userUpdateMe() {
        return document("user/updateMe",
            preprocessRequest(prettyPrint()),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("접속 인증 정보가 담긴 JWT")
            ),
            requestFields(
                fieldWithPath("nickname").type(JsonFieldType.STRING).description("이름")
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

    public static RestDocumentationResultHandler userFindFavorites() {
        FieldDescriptor[] response = new FieldDescriptor[]{
            fieldWithPath("favoriteId").type(JsonFieldType.NUMBER).description("관심목록 ID"),
            fieldWithPath("articlesResponse.articleId").type(JsonFieldType.NUMBER).description(
                "게시글 ID"),
            fieldWithPath("articlesResponse.articleTitle").type(JsonFieldType.STRING).description(
                "게시글 제목"),
            fieldWithPath("articlesResponse.articlePrice").type(JsonFieldType.STRING).description(
                "게시글 가격"),
            fieldWithPath("articlesResponse.sellingLocation").type(
                JsonFieldType.STRING).description(
                "게시글에 등록한 판매지역"),
            fieldWithPath("articlesResponse.chatCount").type(JsonFieldType.NUMBER).description(
                "게시글 채팅수"),
            fieldWithPath("articlesResponse.favoriteCount").type(JsonFieldType.NUMBER).description(
                "게시글 관심수"),
            fieldWithPath("articlesResponse.beforeTime").type(JsonFieldType.STRING).description(
                "등록한 시간 - 현재 시간"),
            fieldWithPath("articlesResponse.articleImage").type(JsonFieldType.STRING).description(
                "판매자가 올린 이미지"),
            fieldWithPath("articlesResponse.bookStatus").type(JsonFieldType.STRING).description(
                "책 상태 [LOWER, MIDDLE, UPPER, BEST]")
        };

        return document("user/findFavorites",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("접속 인증 정보가 담긴 JWT")
            ),
            responseFields(fieldWithPath("[]").description("현재 사용자의 관심목록"))
                .andWithPrefix("[].", response)
        );
    }

    public static RestDocumentationResultHandler userAddFavorite() {
        return document("user/addFavorite",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("접속 인증 정보가 담긴 JWT")
            ), requestFields(
                fieldWithPath("articleId").type(JsonFieldType.NUMBER).description("게시글 ID")
            ), responseFields(
                fieldWithPath("favoriteId").type(JsonFieldType.NUMBER).description("관심목록(좋아요) 생성된 ID")
            )
        );
    }

    public static RestDocumentationResultHandler userDeleteFavorite() {
        return document("user/deleteFavorite",
            preprocessResponse(prettyPrint()),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("접속 인증 정보가 담긴 JWT")
            ),
            pathParameters(
                parameterWithName("favoriteId").description("관심목록 ID")
            )
        );
    }

    public static RestDocumentationResultHandler updateUserPushSetting() {
        return document("user/updatePushSetting",
            preprocessResponse(prettyPrint()),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("접속 인증 정보가 담긴 JWT")
            ),
            responseFields(
                fieldWithPath("userId").type(JsonFieldType.NUMBER).description("유저 id"),
                fieldWithPath("pushActivate").type(JsonFieldType.STRING).description("push 알림 on/off 상태")
            ));
    }

    public static RestDocumentationResultHandler getUserPushSetting() {
        return document("user/getPushSetting",
            preprocessResponse(prettyPrint()),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("접속 인증 정보가 담긴 JWT")
            ),
            responseFields(
                fieldWithPath("userId").type(JsonFieldType.NUMBER).description("유저 id"),
                fieldWithPath("pushActivate").type(JsonFieldType.STRING).description("push 알림 on/off 상태")
            ));
    }
}
