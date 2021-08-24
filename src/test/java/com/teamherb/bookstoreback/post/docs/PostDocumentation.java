package com.teamherb.bookstoreback.post.docs;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;

public class PostDocumentation {

    public static RestDocumentationResultHandler createPost() {
        return document("post/createPost",
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("접속 인증 정보가 담긴 JWT")
            ),
            requestPartFields("postRequest",
                fieldWithPath("accountRequest.accountNumber").description("계좌번호"),
                fieldWithPath("accountRequest.accountOwner").description("예금주 이름"),
                fieldWithPath("accountRequest.accountBank").description("은행명"),
                fieldWithPath("bookRequest.bookIsbn").description("책 ISBN"),
                fieldWithPath("bookRequest.bookTitle").description("책 이름(네이버 책 API)"),
                fieldWithPath("bookRequest.bookAuthor").description("작가(네이버 책 API)"),
                fieldWithPath("bookRequest.bookPublisher").description("출판사(네이버 책 API)"),
                fieldWithPath("bookRequest.bookThumbnail").description("책 썸네일(네이버 책 API)"),
                fieldWithPath("bookRequest.bookListPrice").description("책 정가(네이버 책 API)"),
                fieldWithPath("bookRequest.bookPubDate").description("책 출판일(네이버 책 API)"),
                fieldWithPath("bookRequest.bookSummary").description("책 설명(네이버 책 API)"),
                fieldWithPath("title").description("게시글 제목"),
                fieldWithPath("price").description("게시글 가격"),
                fieldWithPath("description").description("게시글 설명"),
                fieldWithPath("bookStatus").description("책 상태 [LOWER, MIDDLE, UPPER, BEST]")
            ));
    }

    public static RestDocumentationResultHandler findPost() {
        return document("post/findPost",
            preprocessResponse(prettyPrint()),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("접속 인증 정보가 담긴 JWT")
            ),
            pathParameters(
                parameterWithName("postId").description("게시글 ID")
            ),
            responseFields(
                fieldWithPath("accountResponse.accountNumber").type(JsonFieldType.STRING)
                    .description("계좌번호"),
                fieldWithPath("accountResponse.accountOwner").type(JsonFieldType.STRING)
                    .description("예금주 이름"),
                fieldWithPath("accountResponse.accountBank").type(JsonFieldType.STRING)
                    .description("은행명"),
                fieldWithPath("bookResponse.bookIsbn").type(JsonFieldType.STRING)
                    .description("책 ISBN"),
                fieldWithPath("bookResponse.bookTitle").type(JsonFieldType.STRING)
                    .description("책 이름(네이버 책 API)"),
                fieldWithPath("bookResponse.bookAuthor").type(JsonFieldType.STRING)
                    .description("작가(네이버 책 API)"),
                fieldWithPath("bookResponse.bookPublisher").type(JsonFieldType.STRING)
                    .description("출판사(네이버 책 API)"),
                fieldWithPath("bookResponse.bookThumbnail").type(JsonFieldType.STRING)
                    .description("책 썸네일(네이버 책 API)"),
                fieldWithPath("bookResponse.bookListPrice").type(JsonFieldType.STRING)
                    .description("책 정가(네이버 책 API)"),
                fieldWithPath("bookResponse.bookPubDate").type(JsonFieldType.STRING)
                    .description("책 출판일(네이버 책 API)"),
                fieldWithPath("bookResponse.bookSummary").type(JsonFieldType.STRING)
                    .description("책 설명(네이버 책 API)"),
                fieldWithPath("postId").type(JsonFieldType.NUMBER).description("게시글 ID"),
                fieldWithPath("title").type(JsonFieldType.STRING).description("게시글 제목"),
                fieldWithPath("price").type(JsonFieldType.STRING).description("게시글 가격"),
                fieldWithPath("description").type(JsonFieldType.STRING).description("게시글 설명"),
                fieldWithPath("myPost").type(JsonFieldType.BOOLEAN).description("나의 게시글 여부"),
                fieldWithPath("images").type(JsonFieldType.ARRAY).description("이미지 URL"),
                fieldWithPath("bookStatus").type(JsonFieldType.STRING)
                    .description("책 상태 [LOWER, MIDDLE, UPPER, BEST]"),
                fieldWithPath("postStatus").type(JsonFieldType.STRING)
                    .description("게시글 상태 [SALE, PROCEEDING, COMPLETE]")
            ));
    }
}
