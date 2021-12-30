package com.bookwhale.auth.docs;

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

import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;

public class AuthDocumentation {

    public static RestDocumentationResultHandler redirectProviderLogin() {

        return document("oauth/requestLogin",
            preprocessResponse(prettyPrint()),
            pathParameters(
                parameterWithName("providerType").description("OAuth 로그인 기능 공급자 (GOOGLE, NAVER)")
            )
        );
    }

    public static RestDocumentationResultHandler loginProcess() {
        return document("oauth/loginProcess",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            pathParameters(
                parameterWithName("providerType").description("OAuth 로그인 기능 공급자 (GOOGLE, NAVER)")
            ), requestParameters(
                parameterWithName("code").description("provider 에 로그인이 완료되면 전달받는 요청 키")
            )
        );
    }

    public static RestDocumentationResultHandler refreshLoginProcess() {
        return document("oauth/refresh",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            requestFields(
                getRefreshRequestFieldDescriptor()
            ),
            responseFields(
                getRefreshResponseFieldDescriptor()
            )
        );
    }

    public static RestDocumentationResultHandler logoutProcess() {
        return document("oauth/logout",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            requestFields(
                getRefreshRequestFieldDescriptor()
            ),
            responseFields(
                fieldWithPath("").type(String.class).description("요청 결과 메시지 - 로그아웃 되었습니다.")
            )
        );
    }

    public static RestDocumentationResultHandler withdrawalProcess() {
        return document("oauth/withdrawal",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            requestFields(
                getRefreshRequestFieldDescriptor()
            ),
            responseFields(
                fieldWithPath("").type(String.class).description("요청 결과 메시지 - 회원 탈퇴 완료되었습니다.")
            )
        );
    }

    private static FieldDescriptor[] getRefreshRequestFieldDescriptor() {
        FieldDescriptor[] response = new FieldDescriptor[]{
            fieldWithPath("apiToken").type(JsonFieldType.STRING).description(
                "OAuth Token - API Access Token"),
            fieldWithPath("refreshToken").type(JsonFieldType.STRING).description(
                "OAuth Token - Refresh Token"),
        };
        return response;
    }

    private static FieldDescriptor[] getRefreshResponseFieldDescriptor() {
        FieldDescriptor[] response = new FieldDescriptor[]{
            fieldWithPath("apiToken").type(JsonFieldType.STRING).description(
                "OAuth Token - API Access Token"),
            fieldWithPath("refreshToken").type(JsonFieldType.STRING).description(
                "OAuth Token - Refresh Token"),
        };
        return response;
    }

}
