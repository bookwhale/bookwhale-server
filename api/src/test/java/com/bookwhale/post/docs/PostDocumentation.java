package com.bookwhale.post.controller;

import static java.lang.String.format;
import static java.util.List.of;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bookwhale.common.controller.CommonApiTest;
import com.bookwhale.common.domain.Location;
import com.bookwhale.common.security.WithMockCustomUser;
import com.bookwhale.dto.Pagination;
import com.bookwhale.post.docs.PostDocumentation;
import com.bookwhale.post.domain.BookStatus;
import com.bookwhale.post.domain.PostStatus;
import com.bookwhale.post.dto.BookRequest;
import com.bookwhale.post.dto.BookResponse;
import com.bookwhale.post.dto.NaverBookRequest;
import com.bookwhale.post.dto.PostRequest;
import com.bookwhale.post.dto.PostResponse;
import com.bookwhale.post.dto.PostStatusUpdateRequest;
import com.bookwhale.post.dto.PostUpdateRequest;
import com.bookwhale.post.dto.PostsRequest;
import com.bookwhale.post.dto.PostsResponse;
import com.bookwhale.post.service.NaverBookAPIService;
import com.bookwhale.post.service.PostService;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

@DisplayName("게시글 단위 테스트(Controller)")
@WebMvcTest(controllers = PostController.class)
public class PostControllerTest extends CommonApiTest {

    @MockBean
    PostService postService;

    @MockBean
    NaverBookAPIService naverBookAPIService;

    @Test
    @DisplayName("네이버 책 API")
    @WithMockCustomUser
    public void findNaverBooksTest() throws Exception {
        NaverBookRequest request = NaverBookRequest.builder()
            .title("책 제목")
            .display(10)
            .start(1)
            .build();

        BookResponse bookResponse = BookResponse.builder()
            .bookIsbn("12398128745902")
            .bookListPrice("10000")
            .bookThumbnail("썸네일")
            .bookTitle("책 제목")
            .bookPublisher("출판사")
            .bookAuthor("작가")
            .bookSummary("설명")
            .bookPubDate("2021-12-12")
            .build();

        when(naverBookAPIService.getNaverBooks(any())).thenReturn(of(bookResponse));

        mockMvc.perform(get(format("/api/post/naverBookAPI?title=%s&display=%d&start=%d",
                request.getTitle(), request.getDisplay(), request.getStart()))
                .header(HttpHeaders.AUTHORIZATION, "accessToken"))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(PostDocumentation.findNaverBooks());
    }

    @WithMockCustomUser
    @DisplayName("게시글을 등록한다.")
    @Test
    void createPost() throws Exception {
        MockMultipartFile image1 = new MockMultipartFile("images", "bookImage1.jpg",
            ContentType.IMAGE_JPEG.getMimeType(),
            "이미지1 입니다.".getBytes());

        MockMultipartFile image2 = new MockMultipartFile("images", "bookImage2.jpg",
            ContentType.IMAGE_JPEG.getMimeType(),
            "이미지2 입니다.".getBytes());

        BookRequest bookRequest = BookRequest.builder()
            .bookSummary("설명")
            .bookPubDate("2021-12-12")
            .bookIsbn("12398128745902")
            .bookListPrice("10000")
            .bookThumbnail("썸네일")
            .bookTitle("책 제목")
            .bookPublisher("출판사")
            .bookAuthor("작가")
            .build();

        PostRequest postRequest = PostRequest.builder()
            .bookRequest(bookRequest)
            .title("책 팝니다~")
            .description("쿨 거래시 1000원 할인해드려요~")
            .bookStatus("BEST")
            .price("5000")
            .sellingLocation("BUSAN")
            .build();

        String content = objectMapper.writeValueAsString(postRequest);
        MockMultipartFile json = new MockMultipartFile("postRequest", "jsonData",
            "application/json", content.getBytes(StandardCharsets.UTF_8));

        when(postService.createPost(any(), any(), any())).thenReturn(1L);

        mockMvc.perform(multipart("/api/post")
                .file(image1)
                .file(image2)
                .file(json)
                .header(HttpHeaders.AUTHORIZATION, "accessToken")
                .contentType(MediaType.MULTIPART_MIXED))
            .andExpect(header().string("location", "/api/post/1"))
            .andExpect(status().isCreated())
            .andDo(print())
            .andDo(PostDocumentation.createPost());
    }

    @WithMockCustomUser
    @DisplayName("게시글을 등록할 때 이미지를 보내지 않으면 예외가 발생한다.")
    @Test
    void createPost_notExistRequestPart_failure() throws Exception {
        BookRequest bookRequest = BookRequest.builder()
            .bookSummary("설명")
            .bookPubDate("2021-12-12")
            .bookIsbn("12398128745902")
            .bookListPrice("10000")
            .bookThumbnail("썸네일")
            .bookTitle("책 제목")
            .bookPublisher("출판사")
            .bookAuthor("작가")
            .build();

        PostRequest postRequest = PostRequest.builder()
            .bookRequest(bookRequest)
            .title("책 팝니다~")
            .description("쿨 거래시 1000원 할인해드려요~")
            .bookStatus("BEST")
            .price("5000")
            .build();

        String content = objectMapper.writeValueAsString(postRequest);
        MockMultipartFile json = new MockMultipartFile("postRequest", "jsonData",
            "application/json", content.getBytes(StandardCharsets.UTF_8));

        when(postService.createPost(any(), any(), any())).thenReturn(1L);

        mockMvc.perform(multipart("/api/post")
                .file(json)
                .header(HttpHeaders.AUTHORIZATION, "accessToken")
                .contentType(MediaType.MULTIPART_MIXED))
            .andExpect(status().isBadRequest())
            .andDo(print());
    }

    @WithMockCustomUser
    @DisplayName("게시글을 상세 조회한다.")
    @Test
    void findPost() throws Exception {
        BookResponse bookResponse = BookResponse.builder()
            .bookSummary("설명")
            .bookPubDate("2021-12-12")
            .bookIsbn("12398128745902")
            .bookListPrice("10000")
            .bookThumbnail("썸네일")
            .bookTitle("토비의 스프링")
            .bookPublisher("출판사")
            .bookAuthor("이일민")
            .build();

        PostResponse postResponse = PostResponse.builder()
            .sellerId(1L)
            .sellerIdentity("sellerIdentity")
            .sellerProfileImage("sellerProfileImage")
            .postId(1L)
            .title("책 팝니다~")
            .price("5000원")
            .description("쿨 거래시 1000원 할인해드려요~")
            .bookStatus(BookStatus.BEST.getName())
            .postStatus(PostStatus.SALE.getName())
            .images(of("image1", "image2"))
            .bookResponse(bookResponse)
            .isMyPost(true)
            .isMyLike(true)
            .sellingLocation("서울")
            .viewCount(1L)
            .likeCount(0L)
            .beforeTime("15분 전")
            .build();

        when(postService.findPost(any(), any())).thenReturn(postResponse);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/post/{postId}", 1L)
                .header(HttpHeaders.AUTHORIZATION, "accessToken"))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(PostDocumentation.findPost());
    }

    @WithMockCustomUser
    @DisplayName("로그인한 유저가 게시글을 전체 조회한다.")
    @Test
    void findPosts_loginUser() throws Exception {
        Pagination pagination = new Pagination(0, 10);

        PostsRequest postsRequest = PostsRequest.builder()
            .title("책 제목")
            .build();

        PostsResponse postsResponse = PostsResponse.builder()
            .postId(1L)
            .postImage("이미지")
            .postTitle("책 팝니다~")
            .postPrice("20000원")
            .postStatus(PostStatus.SALE.getName())
            .description("판매자가 작성한 게시글 설₩")
            .sellingLocation(Location.SEOUL.getName())
            .viewCount(1L)
            .likeCount(0L)
            .beforeTime("15분 전")
            .build();

        when(postService.findPosts(any(), any())).thenReturn(of(postsResponse));

        mockMvc.perform(get(format("/api/post?title=%s&page=%d&size=%d", postsRequest.getTitle(),
                pagination.getPage(), pagination.getSize()))
                .header(HttpHeaders.AUTHORIZATION, "accessToken"))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(PostDocumentation.findPosts());
    }

    @DisplayName("로그인하지 않은 유저가 게시글을 전체 조회한다.")
    @Test
    void findPosts_anonymousUser() throws Exception {
        Pagination pagination = new Pagination(0, 10);

        PostsRequest postsRequest = PostsRequest.builder()
            .title("책 제목")
            .build();

        PostsResponse postsResponse = PostsResponse.builder()
            .postId(1L)
            .postImage("이미지")
            .postTitle("책 팝니다~")
            .postPrice("20000원")
            .postStatus(PostStatus.SALE.getName())
            .beforeTime("15분 전")
            .build();

        when(postService.findPosts(any(), any())).thenReturn(of(postsResponse));

        mockMvc.perform(get(format("/api/post?title=%s&page=%d&size=%d", postsRequest.getTitle(),
                pagination.getPage(), pagination.getSize())))
            .andExpect(status().isOk())
            .andDo(print());
    }

    @WithMockCustomUser
    @DisplayName("게시글을 수정한다.")
    @Test
    void updatePost_success() throws Exception {
        MockMultipartFile updateImage = new MockMultipartFile("images", "updateImage.jpg",
            ContentType.IMAGE_JPEG.getMimeType(),
            "수정된 이미지입니다.".getBytes());

        PostUpdateRequest request = PostUpdateRequest.builder()
            .title("책 팝니다~ (가격 내림)")
            .description("쿨 거래시 1000원 할인해드려요~ (가격 내림)")
            .bookStatus("LOWER")
            .price("2000")
            .sellingLocation("SEOUL")
            .deleteImgUrls(List.of("image1", "image2"))
            .build();

        String content = objectMapper.writeValueAsString(request);
        MockMultipartFile json = new MockMultipartFile("postUpdateRequest", "jsonData",
            "application/json", content.getBytes(StandardCharsets.UTF_8));

        doNothing().when(postService).updatePost(any(), any(), any(), any());

        mockMvc.perform(MockMultipartPatchBuilder("/api/post/1")
                .file(updateImage)
                .file(json)
                .header(HttpHeaders.AUTHORIZATION, "accessToken")
                .contentType(MediaType.MULTIPART_MIXED))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(PostDocumentation.updatePost());
    }

    @WithMockCustomUser
    @DisplayName("게시글 상태를 변경한다.")
    @Test
    void updatePostStatus() throws Exception {
        PostStatusUpdateRequest request = new PostStatusUpdateRequest(
            PostStatus.SOLD_OUT.toString());

        mockMvc.perform(RestDocumentationRequestBuilders.patch("/api/post/postStatus/{postId}", 1L)
                .header(HttpHeaders.AUTHORIZATION, "accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(PostDocumentation.updatePostStatus());
    }

    @DisplayName("검색조건에 사용될 판매글 상태 목록을 조회한다.")
    @Test
    void getSearchConditionsOfAllBookStatus() throws Exception {
        String kindOfCondition = "bookStatus";
        mockMvc.perform(get("/api/post/conditions/" + kindOfCondition))
            .andExpect(status().isOk())
            .andDo(PostDocumentation.getSearchConditions(kindOfCondition))
            .andDo(print());
    }

    @DisplayName("검색조건에 사용될 판매지역 목록을 조회한다.")
    @Test
    void getSearchConditionsOfAllSellingLocation() throws Exception {
        String kindOfCondition = "locations";
        mockMvc.perform(get("/api/post/conditions/" + kindOfCondition))
            .andExpect(status().isOk())
            .andDo(PostDocumentation.getSearchConditions(kindOfCondition))
            .andDo(print());
    }
}package com.bookwhale.post.docs;

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
                parameterWithName("author").description("저자명").optional(),
                parameterWithName("display").description("검색 결과 출력 건수 지정 / 10(기본값) ~ 100(최대값)"),
                parameterWithName("start").description("검색 시작 위치 / 1(기본값) ~ 1000(최대값)")
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
                fieldWithPath("bookStatus").description("책 상태 [LOWER, MIDDLE, UPPER, BEST]"),
                fieldWithPath("sellingLocation").description("판매(거래)지역")
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
                fieldWithPath("myLike").type(JsonFieldType.BOOLEAN).description("나의 관심목록 여부"),
                fieldWithPath("images").type(JsonFieldType.ARRAY).description("이미지 URL"),
                fieldWithPath("bookStatus").type(JsonFieldType.STRING)
                    .description("책 상태 [LOWER, MIDDLE, UPPER, BEST]"),
                fieldWithPath("postStatus").type(JsonFieldType.STRING)
                    .description("게시글 상태 [SALE, RESERVED, SOLD_OUT]"),
                fieldWithPath("sellingLocation").type(JsonFieldType.STRING)
                    .description("게시글에 등록한 판매지역"),
                fieldWithPath("viewCount").type(JsonFieldType.NUMBER).description("게시글 조회수"),
                fieldWithPath("likeCount").type(JsonFieldType.NUMBER).description("게시글 관심수"),
                fieldWithPath("beforeTime").type(JsonFieldType.STRING).description("등록한 시간 - 현재 시간")
            ));
    }

    public static RestDocumentationResultHandler findPosts() {
        FieldDescriptor[] response = new FieldDescriptor[]{
            fieldWithPath("postId").type(JsonFieldType.NUMBER).description("게시글 ID"),
            fieldWithPath("postTitle").type(JsonFieldType.STRING).description("게시글 제목"),
            fieldWithPath("postPrice").type(JsonFieldType.STRING).description("게시글 가격"),
            fieldWithPath("beforeTime").type(JsonFieldType.STRING).description("등록한 시간 - 현재 시간"),
            fieldWithPath("sellingLocation").type(JsonFieldType.STRING).description(
                "게시글에 등록한 판매지역"),
            fieldWithPath("viewCount").type(JsonFieldType.NUMBER).description("게시글 조회수"),
            fieldWithPath("likeCount").type(JsonFieldType.NUMBER).description("게시글 관심수"),
            fieldWithPath("postImage").type(JsonFieldType.STRING).description("판매자가 올린 이미지"),
            fieldWithPath("postStatus").type(JsonFieldType.STRING).description(
                "게시글 상태 [SALE, RESERVED, SOLD_OUT]"),
            fieldWithPath("description").type(JsonFieldType.STRING).description(
                "판매자가 작성한 게시글 설명")
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
            responseFields(fieldWithPath("[]").description("An arrays of postsResponse"))
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
                fieldWithPath("bookStatus").description("책 상태 [LOWER, MIDDLE, UPPER, BEST] (필수)"),
                fieldWithPath("sellingLocation").description("판매(거래)지역 (필수)"),
                fieldWithPath("deleteImgUrls").description("삭제할 이미지 URL")
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

    public static RestDocumentationResultHandler getSearchConditions(String kindOfCondition) {
        String conditionFlag = StringUtils.isEmpty(kindOfCondition) ? "" : kindOfCondition;
        FieldDescriptor[] response = new FieldDescriptor[]{
            fieldWithPath("code").type(JsonFieldType.STRING).description("검색조건 - 코드값"),
            fieldWithPath("name").type(JsonFieldType.STRING).description("검색조건 - 표기명"),
        };

        return document(String.format("post/conditions/%s", conditionFlag),
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            responseFields(fieldWithPath("[]").description("An arrays of postsResponse"))
                .andWithPrefix("[].", response)
        );
    }

}