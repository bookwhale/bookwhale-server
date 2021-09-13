package com.teamherb.bookstoreback.post.docs;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;

import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;

public class PostDocumentation {

  public static RestDocumentationResultHandler findNaverBooks() {
    FieldDescriptor[] books = new FieldDescriptor[]{
        fieldWithPath("bookIsbn").type(JsonFieldType.STRING).description("ISBN"),
        fieldWithPath("bookListPrice").type(JsonFieldType.STRING).description("정가"),
        fieldWithPath("bookThumbnail").type(JsonFieldType.STRING).description("썸네일"),
        fieldWithPath("bookTitle").type(JsonFieldType.STRING).description("제목"),
        fieldWithPath("bookPublisher").type(JsonFieldType.STRING).description("출판사"),
        fieldWithPath("bookAuthor").type(JsonFieldType.STRING).description("저자"),
        fieldWithPath("bookSummary").type(JsonFieldType.STRING).description("설명"),
        fieldWithPath("bookPubDate").type(JsonFieldType.STRING).description("출간일")
    };

    return document("post/findNaverBooks",
        preprocessRequest(prettyPrint()),
        preprocessResponse(prettyPrint()),
        requestHeaders(
            headerWithName(HttpHeaders.AUTHORIZATION).description("접속 인증 정보가 담긴 JWT")
        ),
        requestParameters(
            parameterWithName("title").description("제목").optional(),
            parameterWithName("isbn").description("ISBN").optional(),
            parameterWithName("author").description("저자명").optional()
        ),
        responseFields(
            fieldWithPath("[]").description("An array of books"))
            .andWithPrefix("[].", books)
    );
  }

  public static RestDocumentationResultHandler createPost() {
    return document("post/createPost",
        requestHeaders(
            headerWithName(HttpHeaders.AUTHORIZATION).description("접속 인증 정보가 담긴 JWT")
        ),
        requestPartFields("postRequest",
            fieldWithPath("bookRequest.bookIsbn").description("책 ISBN"),
            fieldWithPath("bookRequest.bookTitle").description("책 이름(네이버 책 API)"),
            fieldWithPath("bookRequest.bookAuthor").description("저자(네이버 책 API)"),
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
            fieldWithPath("sellerId").type(JsonFieldType.NUMBER).description("판매자 ID"),
            fieldWithPath("sellerIdentity").type(JsonFieldType.STRING).description("판매자 아이디"),
            fieldWithPath("sellerProfileImage").type(JsonFieldType.STRING)
                .description("판매자 프로필 사진"),
            fieldWithPath("postId").type(JsonFieldType.NUMBER).description("게시글 ID"),
            fieldWithPath("title").type(JsonFieldType.STRING).description("게시글 제목"),
            fieldWithPath("price").type(JsonFieldType.STRING).description("게시글 가격"),
            fieldWithPath("description").type(JsonFieldType.STRING).description("게시글 설명"),
            fieldWithPath("myPost").type(JsonFieldType.BOOLEAN).description("나의 게시글 여부"),
            fieldWithPath("images").type(JsonFieldType.ARRAY).description("이미지 URL"),
            fieldWithPath("bookStatus").type(JsonFieldType.STRING)
                .description("책 상태 [LOWER, MIDDLE, UPPER, BEST]"),
            fieldWithPath("postStatus").type(JsonFieldType.STRING)
                .description("게시글 상태 [SALE, RESERVED, SOLD_OUT]"),
            fieldWithPath("createdDate").type(JsonFieldType.STRING).description("게시글 등록일"),
            fieldWithPath("lastModifiedDate").type(JsonFieldType.STRING).description("게시글 최근 수정일")
        ));
  }

  public static RestDocumentationResultHandler findPosts() {
    FieldDescriptor[] response = new FieldDescriptor[]{
        fieldWithPath("postId").type(JsonFieldType.NUMBER).description("게시글 ID"),
        fieldWithPath("postTitle").type(JsonFieldType.STRING).description("게시글 제목"),
        fieldWithPath("postPrice").type(JsonFieldType.STRING).description("게시글 가격"),
        fieldWithPath("bookTitle").type(JsonFieldType.STRING).description("책 제목"),
        fieldWithPath("createdDate").type(JsonFieldType.STRING).description("게시글 등록일"),
        fieldWithPath("bookThumbnail").type(JsonFieldType.STRING).description(
            "책 썸네일(네이버 책 API)"),
        fieldWithPath("postStatus").type(JsonFieldType.STRING).description(
            "게시글 상태 [SALE, RESERVED, SOLD_OUT]")
    };

    return document("post/findPosts",
        preprocessRequest(prettyPrint()),
        preprocessResponse(prettyPrint()),
        requestHeaders(
            headerWithName(HttpHeaders.AUTHORIZATION).description("접속 인증 정보가 담긴 JWT")
        ),
        requestParameters(
            parameterWithName("title").description("책 제목").optional(),
            parameterWithName("author").description("저자").optional(),
            parameterWithName("publisher").description("출판사").optional(),
            parameterWithName("page").description("페이지(0부터 시작) (필수)"),
            parameterWithName("size").description("한 페이지 내의 사이즈 (필수)")
        ),
        responseFields(fieldWithPath("[]").description("An arrays of fullPostResponse"))
            .andWithPrefix("[].", response)
    );
  }

  public static RestDocumentationResultHandler updatePost() {
    return document("post/updatePost",
        preprocessRequest(prettyPrint()),
        preprocessResponse(prettyPrint()),
        requestHeaders(
            headerWithName(HttpHeaders.AUTHORIZATION).description("접속 인증 정보가 담긴 JWT")
        ),
        requestPartFields("postUpdateRequest",
            fieldWithPath("title").description("게시글 제목 (필수)"),
            fieldWithPath("price").description("게시글 가격 (필수)"),
            fieldWithPath("description").description("게시글 설명 (필수)"),
            fieldWithPath("bookStatus").description("책 상태 [LOWER, MIDDLE, UPPER, BEST] (필수)")
        )
    );
  }

  public static RestDocumentationResultHandler updatePostStatus() {
    return document("post/updatePostStatus",
        preprocessRequest(prettyPrint()),
        requestHeaders(
            headerWithName(HttpHeaders.AUTHORIZATION).description("접속 인증 정보가 담긴 JWT")
        ),
        pathParameters(
            parameterWithName("postId").description("게시글 ID")
        ), requestFields(
            fieldWithPath("postStatus").type(JsonFieldType.STRING)
                .description("게시글 상태 [SALE, RESERVED, SOLD_OUT]")
        )
    );
  }
}
