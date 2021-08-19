package com.teamherb.bookstoreback.user.docs;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

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
                fieldWithPath("accountRequest.accountNumber").type(JsonFieldType.STRING)
                    .description("계좌번호"),
                fieldWithPath("accountRequest.accountBank").type(JsonFieldType.STRING)
                    .description("은행"),
                fieldWithPath("accountRequest.accountOwner").type(JsonFieldType.STRING)
                    .description("이름")
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
                headerWithName("jwt").description("접속 인증 정보가 담긴 JWT")
            ),
            responseFields(
                fieldWithPath("identity").type(JsonFieldType.STRING).description("아이디"),
                fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                fieldWithPath("phoneNumber").type(JsonFieldType.STRING).description("전화번호"),
                fieldWithPath("address").type(JsonFieldType.STRING).description("주소")
            ));
    }

    public static RestDocumentationResultHandler userUpdateMe() {
        return document("user/updateMe",
            preprocessRequest(prettyPrint()),
            requestHeaders(
                headerWithName("jwt").description("접속 인증 정보가 담긴 JWT")
            ),
            requestFields(
                fieldWithPath("name").type(JsonFieldType.STRING).description("이름").optional(),
                fieldWithPath("phoneNumber").type(JsonFieldType.STRING).description("전화번호")
                    .optional(),
                fieldWithPath("address").type(JsonFieldType.STRING).description("주소").optional()
            ));
    }

    public static RestDocumentationResultHandler findPurchaseHistories() {
        FieldDescriptor[] response = new FieldDescriptor[]{
            fieldWithPath("sellerIdentity").type(JsonFieldType.STRING).description("판매자 아이디"),
            fieldWithPath("sellerName").type(JsonFieldType.STRING).description("판매자 이름"),
            fieldWithPath("postTitle").type(JsonFieldType.STRING).description("판매글 제목"),
            fieldWithPath("postPrice").type(JsonFieldType.STRING).description("판매글 가격"),
            fieldWithPath("bookTitle").type(JsonFieldType.STRING).description("책 제목"),
            fieldWithPath("bookThumbnail").type(JsonFieldType.STRING).description("책 썸네일"),
            fieldWithPath("createdDate").type(JsonFieldType.STRING).description("구매 날짜"),
        };

        return document("user/purchaseHistory",
            preprocessResponse(prettyPrint()),
            requestHeaders(
                headerWithName("jwt").description("접속 인증 정보가 담긴 JWT")
            ),
            responseFields(
                fieldWithPath("[]").description("An array of books"))
                .andWithPrefix("[].", response)
        );
    }

    public static RestDocumentationResultHandler findSaleHistories() {
        FieldDescriptor[] response = new FieldDescriptor[]{
                fieldWithPath("purchaserIdentity").type(JsonFieldType.STRING).description("구매자 아이디"),
                fieldWithPath("purchaserName").type(JsonFieldType.STRING).description("구매자 이름"),
                fieldWithPath("postTitle").type(JsonFieldType.STRING).description("판매글 제목"),
                fieldWithPath("postPrice").type(JsonFieldType.STRING).description("판매글 가격"),
                fieldWithPath("bookTitle").type(JsonFieldType.STRING).description("책 제목"),
                fieldWithPath("bookThumbnail").type(JsonFieldType.STRING).description("책 썸네일"),
                fieldWithPath("createdDate").type(JsonFieldType.STRING).description("구매 날짜"),
        };

        return document("user/saleHistory",
                preprocessResponse(prettyPrint()),
                requestHeaders(
                        headerWithName("jwt").description("접속 인증 정보가 담긴 JWT")
                ),
                responseFields(
                        fieldWithPath("[]").description("An array of books"))
                        .andWithPrefix("[].", response)
        );
    }

}
