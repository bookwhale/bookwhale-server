package com.teamherb.bookstoreback.post.docs;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

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
            requestFields(
                fieldWithPath("title").type(JsonFieldType.STRING).description("제목").optional(),
                fieldWithPath("isbn").type(JsonFieldType.STRING).description("ISBN").optional(),
                fieldWithPath("author").type(JsonFieldType.STRING).description("저자명").optional()
            ),
            responseFields(
                fieldWithPath("[]").description("An array of books"))
                .andWithPrefix("[].", books)
        );
    }
}
