package com.teamherb.bookstoreback.account.docs;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;

public class AccountDocumentation {

    public static RestDocumentationResultHandler createAccount() {
        return document("account/createAccount",
            preprocessRequest(prettyPrint()),
            requestHeaders(
                headerWithName("jwt").description("접속 인증 정보가 담긴 JWT")
            ),
            requestFields(
                fieldWithPath("accountNumber").type(JsonFieldType.STRING).description("계좌 번호"),
                fieldWithPath("accountBank").type(JsonFieldType.STRING).description("은행"),
                fieldWithPath("accountOwner").type(JsonFieldType.STRING).description("이름")
            )
        );
    }

    public static RestDocumentationResultHandler findAccounts() {
        FieldDescriptor[] account = new FieldDescriptor[]{
            fieldWithPath("accountNumber").type(JsonFieldType.STRING).description("계좌 번호"),
            fieldWithPath("accountBank").type(JsonFieldType.STRING).description("은행"),
            fieldWithPath("accountOwner").type(JsonFieldType.STRING).description("이름")
        };

        return document("account/findAccounts",
            Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
            requestHeaders(
                headerWithName("jwt").description("접속 인증 정보가 담긴 JWT")
            ),
            responseFields(fieldWithPath("[]").description("An arrays of accounts"))
                .andWithPrefix("[].", account)
        );
    }

    public static RestDocumentationResultHandler updateAccount() {
        return document("account/updateAccount",
            preprocessRequest(prettyPrint()),
            requestHeaders(
                headerWithName("jwt").description("접속 인증 정보가 담긴 JWT")
            ),
            requestFields(
                fieldWithPath("accountId").type(JsonFieldType.NUMBER).description("수정할 계좌 ID"),
                fieldWithPath("accountNumber").type(JsonFieldType.STRING).description("계좌 번호").optional(),
                fieldWithPath("accountBank").type(JsonFieldType.STRING).description("은행").optional(),
                fieldWithPath("accountOwner").type(JsonFieldType.STRING).description("이름").optional()
            )
        );
    }

    public static RestDocumentationResultHandler deleteAccount() {
        return document("account/deleteAccount",
            requestHeaders(
                headerWithName("jwt").description("접속 인증 정보가 담긴 JWT")
            ),
            pathParameters(
                parameterWithName("accountId").description("삭제할 계좌 ID")
            )
        );
    }
}
