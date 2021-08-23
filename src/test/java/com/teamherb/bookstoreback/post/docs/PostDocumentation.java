package com.teamherb.bookstoreback.post.docs;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;

import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;

public class PostDocumentation {

    public static RestDocumentationResultHandler createPost() {
        return document("post/createPost",
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("접속 인증 정보가 담긴 JWT")
            ),
            requestPartFields("postRequest",
                fieldWithPath("accountRequest.accountNumber").description("계좌번호"),
                fieldWithPath("accountRequest.accountOwner").description("예금주 이름"),
                fieldWithPath("accountRequest.accountBank").description("은행"),
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
}
