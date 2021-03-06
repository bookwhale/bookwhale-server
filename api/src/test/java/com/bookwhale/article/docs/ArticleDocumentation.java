package com.bookwhale.article.docs;

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

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;

public class ArticleDocumentation {

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

        return document("article/findNaverBooks",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("접속 인증 정보가 담긴 JWT")
            ),
            requestParameters(
                parameterWithName("title").description("제목").optional(),
                parameterWithName("isbn").description("ISBN").optional(),
                parameterWithName("author").description("저자명").optional(),
                parameterWithName("display").description("검색 결과 출력 건수 지정 / 10(기본값) ~ 100(최대값)"),
                parameterWithName("start").description("검색 시작 위치 / 1(기본값) ~ 1000(최대값)")
            ),
            responseFields(
                fieldWithPath("[]").description("An array of books"))
                .andWithPrefix("[].", books)
        );
    }

    public static RestDocumentationResultHandler createArticle() {
        return document("article/createArticle",
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("접속 인증 정보가 담긴 JWT")
            ),
            requestPartFields("articleRequest",
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
                fieldWithPath("bookStatus").description("책 상태 [LOWER, MIDDLE, UPPER, BEST]"),
                fieldWithPath("sellingLocation").description("판매(거래)지역")
            ));
    }

    public static RestDocumentationResultHandler findMyArticles() {
        FieldDescriptor[] response = new FieldDescriptor[]{
            fieldWithPath("articleId").type(JsonFieldType.NUMBER).description("게시글 ID"),
            fieldWithPath("articleTitle").type(JsonFieldType.STRING).description("게시글 제목"),
            fieldWithPath("articlePrice").type(JsonFieldType.STRING).description("게시글 가격"),
            fieldWithPath("sellingLocation").type(JsonFieldType.STRING).description(
                "게시글에 등록한 판매지역"),
            fieldWithPath("chatCount").type(JsonFieldType.NUMBER).description("게시글 채팅수"),
            fieldWithPath("favoriteCount").type(JsonFieldType.NUMBER).description("게시글 관심수"),
            fieldWithPath("beforeTime").type(JsonFieldType.STRING).description("등록한 시간 - 현재 시간"),
            fieldWithPath("articleImage").type(JsonFieldType.STRING).description("판매자가 올린 이미지"),
            fieldWithPath("bookStatus").type(JsonFieldType.STRING).description(
                "책 상태 [LOWER, MIDDLE, UPPER, BEST]")
        };

        return document("user/findMyArticles",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("접속 인증 정보가 담긴 JWT")
            ),
            responseFields(fieldWithPath("[]").description("An arrays of articlesResponse"))
                .andWithPrefix("[].", response)
        );
    }

    public static RestDocumentationResultHandler findArticle() {
        return document("article/findArticle",
            preprocessResponse(prettyPrint()),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("접속 인증 정보가 담긴 JWT")
            ),
            pathParameters(
                parameterWithName("articleId").description("게시글 ID")
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
                fieldWithPath("articleId").type(JsonFieldType.NUMBER).description("게시글 ID"),
                fieldWithPath("title").type(JsonFieldType.STRING).description("게시글 제목"),
                fieldWithPath("price").type(JsonFieldType.STRING).description("게시글 가격"),
                fieldWithPath("description").type(JsonFieldType.STRING).description("게시글 설명"),
                fieldWithPath("myArticle").type(JsonFieldType.BOOLEAN).description("나의 게시글 여부"),
                fieldWithPath("myFavorite").type(JsonFieldType.BOOLEAN).description("나의 관심목록 여부"),
                fieldWithPath("myFavoriteId").type(JsonFieldType.NUMBER).description("(관심목록에 있는 판매글인 경우) 관심목록 (좋아요) ID")
                    .optional(),
                fieldWithPath("images").type(JsonFieldType.ARRAY).description("이미지 URL"),
                fieldWithPath("bookStatus").type(JsonFieldType.STRING)
                    .description("책 상태 [LOWER, MIDDLE, UPPER, BEST]"),
                fieldWithPath("articleStatus").type(JsonFieldType.STRING)
                    .description("게시글 상태 [SALE, RESERVED, SOLD_OUT]"),
                fieldWithPath("sellingLocation").type(JsonFieldType.STRING)
                    .description("게시글에 등록한 판매지역"),
                fieldWithPath("viewCount").type(JsonFieldType.NUMBER).description("게시글 조회수"),
                fieldWithPath("favoriteCount").type(JsonFieldType.NUMBER).description("게시글 관심수"),
                fieldWithPath("beforeTime").type(JsonFieldType.STRING).description("등록한 시간 - 현재 시간")
            ));
    }

    public static RestDocumentationResultHandler findArticles() {
        FieldDescriptor[] response = new FieldDescriptor[]{
            fieldWithPath("articleId").type(JsonFieldType.NUMBER).description("게시글 ID"),
            fieldWithPath("articleTitle").type(JsonFieldType.STRING).description("게시글 제목"),
            fieldWithPath("articlePrice").type(JsonFieldType.STRING).description("게시글 가격"),
            fieldWithPath("beforeTime").type(JsonFieldType.STRING).description("등록한 시간 - 현재 시간"),
            fieldWithPath("sellingLocation").type(JsonFieldType.STRING).description(
                "게시글에 등록한 판매지역"),
            fieldWithPath("chatCount").type(JsonFieldType.NUMBER).description("게시글 채팅수"),
            fieldWithPath("favoriteCount").type(JsonFieldType.NUMBER).description("게시글 관심수"),
            fieldWithPath("articleImage").type(JsonFieldType.STRING).description("판매자가 올린 이미지"),
            fieldWithPath("bookStatus").type(JsonFieldType.STRING).description(
                "책 상태 [LOWER, MIDDLE, UPPER, BEST]"),
        };

        return document("article/findArticles",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            requestParameters(
                parameterWithName("search").description("통합 검색 (책 제목, 판매글 제목, 작가)"),
                parameterWithName("page").description("페이지(0부터 시작) (필수)"),
                parameterWithName("size").description("한 페이지 내의 사이즈 (필수)")
            ),
            responseFields(fieldWithPath("[]").description("An arrays of articlesResponse"))
                .andWithPrefix("[].", response)
        );
    }

    public static RestDocumentationResultHandler updateArticle() {
        return document("article/updateArticle",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("접속 인증 정보가 담긴 JWT")
            ),
            requestPartFields("articleUpdateRequest",
                fieldWithPath("title").description("게시글 제목 (필수)"),
                fieldWithPath("price").description("게시글 가격 (필수)"),
                fieldWithPath("description").description("게시글 설명 (필수)"),
                fieldWithPath("bookStatus").description("책 상태 [LOWER, MIDDLE, UPPER, BEST] (필수)"),
                fieldWithPath("sellingLocation").description("판매(거래)지역 (필수)"),
                fieldWithPath("deleteImgUrls").description("삭제할 이미지 URL")
            )
        );
    }

    public static RestDocumentationResultHandler updateArticleStatus() {
        return document("article/updateArticleStatus",
            preprocessRequest(prettyPrint()),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("접속 인증 정보가 담긴 JWT")
            ),
            pathParameters(
                parameterWithName("articleId").description("게시글 ID")
            ), requestFields(
                fieldWithPath("articleStatus").type(JsonFieldType.STRING)
                    .description("게시글 상태 [SALE, RESERVED, SOLD_OUT]")
            )
        );
    }

    public static RestDocumentationResultHandler deleteArticle() {
        return document("article/deleteArticle",
            preprocessRequest(prettyPrint()),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("접속 인증 정보가 담긴 JWT")
            ),
            pathParameters(
                parameterWithName("articleId").description("게시글 ID")
            )
        );
    }

    public static RestDocumentationResultHandler getSearchConditions(String kindOfCondition) {
        String conditionFlag = StringUtils.isEmpty(kindOfCondition) ? "" : kindOfCondition;
        FieldDescriptor[] response = new FieldDescriptor[]{
            fieldWithPath("code").type(JsonFieldType.STRING).description("검색조건 - 코드값"),
            fieldWithPath("name").type(JsonFieldType.STRING).description("검색조건 - 표기명"),
        };

        return document(String.format("article/conditions/%s", conditionFlag),
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            responseFields(fieldWithPath("[]").description("An arrays of articlesResponse"))
                .andWithPrefix("[].", response)
        );
    }

}
