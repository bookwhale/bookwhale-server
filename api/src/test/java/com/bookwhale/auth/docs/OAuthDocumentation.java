package com.bookwhale.auth.docs;

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
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;

import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;

public class OAuthDocumentation {

    public static RestDocumentationResultHandler redirectProviderLogin() {

        return document("oauth/requestLogin", preprocessResponse(prettyPrint()), pathParameters(
            parameterWithName("providerType").description("OAuth 로그인 기능 공급자 (GOOGLE, NAVER)")));
    }

    public static RestDocumentationResultHandler loginProcessAfterRedirct() {
        return document("oauth/loginProcessAfterRedirct", preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()), pathParameters(
                parameterWithName("providerType").description("OAuth 로그인 기능 공급자 (GOOGLE, NAVER)")),
            requestParameters(
                parameterWithName("code").description("provider 에 로그인이 완료되면 전달받는 요청 키"),
                parameterWithName("state").description("provider 에 로그인이 완료되면 전달받는 요청 키")
                    .optional()), responseFields(getTokenResponseFieldDescriptor()));
    }

    public static RestDocumentationResultHandler loginProcess() {
        return document("oauth/loginProcess", preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()), pathParameters(
                parameterWithName("providerType").description("OAuth 로그인 기능 공급자 (GOOGLE, NAVER)")),
            requestParameters(
                parameterWithName("code").description("provider로 부터 전달받은 accessToken")
                ),
            responseFields(getTokenResponseFieldDescriptor()));
    }

    public static RestDocumentationResultHandler refreshLoginProcess() {
        return document("oauth/refresh", preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()), requestFields(getTokenRequestFieldDescriptor()),
            responseFields(getTokenResponseFieldDescriptor()));
    }

    public static RestDocumentationResultHandler logoutProcess() {
        return document("oauth/logout", preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()), requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("접속 인증 정보가 담긴 JWT")),
            requestFields(getTokenRequestFieldDescriptor()), responseFields(
                fieldWithPath("message").type(JsonFieldType.STRING)
                    .description("요청 결과 메시지 - 로그아웃 되었습니다.")));
    }

    public static RestDocumentationResultHandler withdrawalProcess() {
        return document("oauth/withdrawal", preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()), requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("접속 인증 정보가 담긴 JWT")),
            requestFields(getTokenRequestFieldDescriptor()), responseFields(
                fieldWithPath("message").type(JsonFieldType.STRING)
                    .description("요청 결과 메시지 - 회원 탈퇴 완료되었습니다.")));
    }

    private static FieldDescriptor[] getTokenRequestFieldDescriptor() {
        FieldDescriptor[] request = new FieldDescriptor[]{
            fieldWithPath("apiToken").type(JsonFieldType.STRING).description(
                "OAuth Token - API Access Token (유효 : 30분)"),
            fieldWithPath("refreshToken").type(JsonFieldType.STRING).description(
                "OAuth Token - Refresh Token (유효 : 1주)"),};
        return request;
    }

    private static FieldDescriptor[] getTokenResponseFieldDescriptor() {
        FieldDescriptor[] response = new FieldDescriptor[]{
            fieldWithPath("apiToken").type(JsonFieldType.STRING).description(
                "OAuth Token - API Access Token (유효 : 30분)"),
            fieldWithPath("refreshToken").type(JsonFieldType.STRING).description(
                "OAuth Token - Refresh Token (유효 : 1주)"),};
        return response;
    }

}
