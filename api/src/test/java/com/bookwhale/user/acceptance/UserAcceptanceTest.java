package com.bookwhale.user.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import com.bookwhale.common.acceptance.AcceptanceTest;
import com.bookwhale.common.acceptance.AcceptanceUtils;
import com.bookwhale.common.acceptance.step.AcceptanceStep;
import com.bookwhale.common.domain.Location;
import com.bookwhale.article.acceptance.step.ArticleAcceptanceStep;
import com.bookwhale.article.dto.ArticlesResponse;
import com.bookwhale.article.dto.ArticleRequest;
import com.bookwhale.article.dto.ArticleResponse;
import com.bookwhale.article.dto.BookRequest;
import com.bookwhale.user.acceptance.step.UserAcceptanceStep;
import com.bookwhale.user.dto.LikeRequest;
import com.bookwhale.user.dto.LikeResponse;
import com.bookwhale.user.dto.LoginRequest;
import com.bookwhale.user.dto.PasswordUpdateRequest;
import com.bookwhale.user.dto.ProfileResponse;
import com.bookwhale.user.dto.SignUpRequest;
import com.bookwhale.user.dto.UserResponse;
import com.bookwhale.user.dto.UserUpdateRequest;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.MultiPartSpecification;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.MimeTypeUtils;

@DisplayName("유저 통합 테스트")
public class UserAcceptanceTest extends AcceptanceTest {

    @DisplayName("회원가입을 한다.")
    @Test
    void signUpTest() {
        SignUpRequest signUpRequest = SignUpRequest.builder()
            .identity("gentleDot")
            .password("1234")
            .name("백상일")
            .email("gentleDot@email.com")
            .phoneNumber("010-3456-3456")
            .build();

        ExtractableResponse<Response> response = UserAcceptanceStep.requestToSignUp(signUpRequest);

        AcceptanceStep.assertThatStatusIsCreated(response);
    }

    @DisplayName("내 정보를 조회한다.")
    @Test
    void getMyInfo() {
        String jwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);

        ExtractableResponse<Response> response = UserAcceptanceStep.requestToGetMyInfo(jwt);
        UserResponse userResponse = response.jsonPath().getObject(".", UserResponse.class);

        AcceptanceStep.assertThatStatusIsOk(response);
        UserAcceptanceStep.assertThatGetMyInfo(userResponse, user);
    }

    @DisplayName("내 정보를 수정한다.")
    @Test
    void updateMyInfo() {
        UserUpdateRequest userUpdateRequest = UserUpdateRequest.builder()
            .name("주호세")
            .phoneNumber("010-5678-5678")
            .email("hose@email.com")
            .build();

        String jwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);

        ExtractableResponse<Response> response = UserAcceptanceStep.requestToUpdateMyInfo(jwt,
            userUpdateRequest);
        UserResponse userResponse = UserAcceptanceStep.requestToGetMyInfo(jwt).jsonPath()
            .getObject(".", UserResponse.class);

        AcceptanceStep.assertThatStatusIsOk(response);
        UserAcceptanceStep.assertThatUpdateMyInfo(userResponse, userUpdateRequest);
    }

    @DisplayName("비밀번호를 수정한다.")
    @Test
    void updatePassword() {
        PasswordUpdateRequest req = new PasswordUpdateRequest(loginRequest.getPassword(), "12345");
        LoginRequest newLoginReq = new LoginRequest(loginRequest.getIdentity(),
            req.getNewPassword());

        String jwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);

        ExtractableResponse<Response> response = UserAcceptanceStep.requestToUpdatePassword(jwt,
            req);
        ExtractableResponse<Response> newLoginResponse = UserAcceptanceStep.requestToLogin(
            newLoginReq);

        AcceptanceStep.assertThatStatusIsOk(response);
        AcceptanceStep.assertThatStatusIsOk(newLoginResponse);
    }

    @DisplayName("비밀번호를 수정할 때 기존 비밀번호를 틀리게 입력하면 예외가 발생한다.")
    @Test
    void updatePassword_invalidPassword_failure() {
        PasswordUpdateRequest req = new PasswordUpdateRequest("invalidPassword", "12345");

        String jwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);

        ExtractableResponse<Response> response = UserAcceptanceStep.requestToUpdatePassword(jwt,
            req);

        AcceptanceStep.assertThatStatusIsBadRequest(response);
    }

    @DisplayName("프로필 사진을 업로드한다.")
    @Test
    void uploadProfileImage() {
        MultiPartSpecification image = new MultiPartSpecBuilder(
            "profileImage".getBytes())
            .mimeType(MimeTypeUtils.IMAGE_JPEG.toString())
            .controlName("profileImage")
            .fileName("profileImage.jpg")
            .build();

        String jwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);

        ExtractableResponse<Response> response = UserAcceptanceStep.uploadProfileImage(jwt, image);
        ProfileResponse profileResponse = response.jsonPath().getObject(".", ProfileResponse.class);
        UserResponse userResponse = UserAcceptanceStep.requestToGetMyInfo(jwt).jsonPath()
            .getObject(".", UserResponse.class);

        AcceptanceStep.assertThatStatusIsOk(response);
        UserAcceptanceStep.assertThatUploadProfileImage(profileResponse, userResponse);
    }

    @DisplayName("프로필 사진을 삭제한다.")
    @Test
    void deleteProfileImage() {
        MultiPartSpecification image = new MultiPartSpecBuilder(
            "profileImage".getBytes())
            .mimeType(MimeTypeUtils.IMAGE_JPEG.toString())
            .controlName("profileImage")
            .fileName("profileImage.jpg")
            .build();

        String jwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);

        UserAcceptanceStep.uploadProfileImage(jwt, image);
        ExtractableResponse<Response> response = UserAcceptanceStep.deleteProfileImage(jwt);
        UserResponse userResponse = UserAcceptanceStep.requestToGetMyInfo(jwt).jsonPath()
            .getObject(".", UserResponse.class);

        AcceptanceStep.assertThatStatusIsOk(response);
        UserAcceptanceStep.assertThatDeleteProfileImage(userResponse);
    }

    @DisplayName("관심목록에 추가한다.")
    @Test
    void addLike() {
        ArticleRequest articleRequest = ArticleRequest.builder()
            .bookRequest(BookRequest.builder()
                .bookSummary("책 설명")
                .bookPubDate("2021-12-12")
                .bookIsbn("12345678910")
                .bookListPrice("10000")
                .bookThumbnail("썸네일")
                .bookTitle("토비의 스프링")
                .bookPublisher("허브출판사")
                .bookAuthor("이일민")
                .build())
            .title("토비의 스프링 팝니다~")
            .description("책 설명")
            .sellingLocation("DAEGU")
            .bookStatus("BEST")
            .price("5000")
            .build();

        String jwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);
        Long articleId = AcceptanceUtils.getIdFromResponse(
            ArticleAcceptanceStep.requestToCreateArticle(jwt, articleRequest));

        ExtractableResponse<Response> response = UserAcceptanceStep.addLike(jwt,
            new LikeRequest(articleId));
        List<LikeResponse> likeResponses = UserAcceptanceStep.findLikes(jwt).jsonPath()
            .getList(".", LikeResponse.class);

        AcceptanceStep.assertThatStatusIsOk(response);
        UserAcceptanceStep.assertThatAddLike(likeResponses, articleRequest);
    }

    @DisplayName("관심목록에서 삭제한다.")
    @Test
    void deleteLike() {
        ArticleRequest articleRequest = ArticleRequest.builder()
            .bookRequest(BookRequest.builder()
                .bookSummary("책 설명")
                .bookPubDate("2021-12-12")
                .bookIsbn("12345678910")
                .bookListPrice("10000")
                .bookThumbnail("썸네일")
                .bookTitle("토비의 스프링")
                .bookPublisher("허브출판사")
                .bookAuthor("이일민")
                .build())
            .title("토비의 스프링 팝니다~")
            .description("책 설명")
            .sellingLocation("DAEGU")
            .bookStatus("BEST")
            .price("5000")
            .build();

        String jwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);
        Long articleId = AcceptanceUtils.getIdFromResponse(
            ArticleAcceptanceStep.requestToCreateArticle(jwt, articleRequest));
        UserAcceptanceStep.addLike(jwt, new LikeRequest(articleId));
        List<LikeResponse> likeResponseAfterAddLike = UserAcceptanceStep.findLikes(jwt).jsonPath()
            .getList(".", LikeResponse.class);
        Long likeCount = likeResponseAfterAddLike.get(0).getArticlesResponse().getLikeCount();
        Long likeId = likeResponseAfterAddLike.get(0).getLikeId();

        ExtractableResponse<Response> response = UserAcceptanceStep.deleteLike(
            jwt, likeId);
        List<LikeResponse> likeResponses = UserAcceptanceStep.findLikes(jwt).jsonPath()
            .getList(".", LikeResponse.class);

        ExtractableResponse<Response> responseAfterDeleteLike = ArticleAcceptanceStep.requestToFindArticle(
            jwt, articleId);
        ArticleResponse articleResponse = responseAfterDeleteLike.jsonPath()
            .getObject(".", ArticleResponse.class);

        AcceptanceStep.assertThatStatusIsOk(response);
        assertThat(likeResponses.size()).isEqualTo(0);
        assertThat(articleResponse.getLikeCount()).isEqualTo(likeCount - 1L);
    }

    @DisplayName("나의 게시글들을 조회한다.")
    @Test
    void findArticles_one() {
        ArticleRequest articleRequest = ArticleRequest.builder()
            .bookRequest(BookRequest.builder()
                .bookSummary("책 설명")
                .bookPubDate("2021-12-12")
                .bookIsbn("12345678910")
                .bookListPrice("10000")
                .bookThumbnail("썸네일")
                .bookTitle("토비의 스프링")
                .bookPublisher("허브출판사")
                .bookAuthor("이일민")
                .build())
            .title("토비의 스프링 팝니다~")
            .description("책 설명")
            .sellingLocation("DAEGU")
            .bookStatus("BEST")
            .price("5000")
            .build();

        String jwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);
        ArticleAcceptanceStep.requestToCreateArticle(jwt, articleRequest);

        ExtractableResponse<Response> response = UserAcceptanceStep.requestToFindMyArticles(jwt);
        List<ArticlesResponse> articlesRespons = response.jsonPath().getList(".", ArticlesResponse.class);

        AcceptanceStep.assertThatStatusIsOk(response);
        UserAcceptanceStep.assertThatFindMyArticles(articlesRespons, articleRequest);
    }

    @DisplayName("나의 게시글들을 조회할 때 다른 유저의 게시글을 조회되지 않는다.")
    @Test
    void findArticles_empty() {
        ArticleRequest articleRequest = ArticleRequest.builder()
            .bookRequest(BookRequest.builder()
                .bookSummary("책 설명")
                .bookPubDate("2021-12-12")
                .bookIsbn("12345678910")
                .bookListPrice("10000")
                .bookThumbnail("썸네일")
                .bookTitle("토비의 스프링")
                .bookPublisher("허브출판사")
                .bookAuthor("이일민")
                .build())
            .title("토비의 스프링 팝니다~")
            .description("책 설명")
            .bookStatus("BEST")
            .price("5000")
            .build();

        String jwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(loginRequest);
        String anotherJwt = UserAcceptanceStep.requestToLoginAndGetAccessToken(anotherLoginRequest);
        ArticleAcceptanceStep.requestToCreateArticle(anotherJwt, articleRequest);

        ExtractableResponse<Response> response = UserAcceptanceStep.requestToFindMyArticles(jwt);
        List<ArticlesResponse> articlesRespons = response.jsonPath().getList(".", ArticlesResponse.class);

        AcceptanceStep.assertThatStatusIsOk(response);
        assertThat(articlesRespons.size()).isEqualTo(0);
    }
}