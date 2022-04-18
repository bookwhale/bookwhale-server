package com.bookwhale.user.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import com.bookwhale.article.acceptance.step.ArticleAcceptanceStep;
import com.bookwhale.article.dto.ArticleRequest;
import com.bookwhale.article.dto.ArticleResponse;
import com.bookwhale.article.dto.BookRequest;
import com.bookwhale.auth.domain.info.UserInfoFromToken;
import com.bookwhale.common.acceptance.AcceptanceTest;
import com.bookwhale.common.acceptance.AcceptanceUtils;
import com.bookwhale.common.acceptance.step.AcceptanceStep;
import com.bookwhale.user.acceptance.step.UserAcceptanceStep;
import com.bookwhale.user.dto.FavoriteRequest;
import com.bookwhale.user.dto.FavoriteResponse;
import com.bookwhale.user.dto.ProfileResponse;
import com.bookwhale.user.dto.UserPushSettingResponse;
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

    @DisplayName("내 정보를 조회한다.")
    @Test
    void getMyInfo() {
        UserInfoFromToken userInfo = UserInfoFromToken.of(user);
        String token = UserAcceptanceStep.requestToLoginAndGetAccessToken(userInfo, jwt);

        ExtractableResponse<Response> response = UserAcceptanceStep.requestToGetMyInfo(token);
        UserResponse userResponse = response.jsonPath().getObject(".", UserResponse.class);

        AcceptanceStep.assertThatStatusIsOk(response);
        UserAcceptanceStep.assertThatGetMyInfo(userResponse, user);
    }

    @DisplayName("내 정보를 수정한다.")
    @Test
    void updateMyInfo() {
        UserUpdateRequest userUpdateRequest = UserUpdateRequest.builder()
            .nickname("hose12")
            .build();

        String apiToken = UserAcceptanceStep.requestToLoginAndGetAccessToken(
            UserInfoFromToken.of(user), jwt);

        ExtractableResponse<Response> response = UserAcceptanceStep.requestToUpdateMyInfo(apiToken,
            userUpdateRequest);
        UserResponse userResponse = UserAcceptanceStep.requestToGetMyInfo(apiToken).jsonPath()
            .getObject(".", UserResponse.class);

        AcceptanceStep.assertThatStatusIsOk(response);
        UserAcceptanceStep.assertThatUpdateMyInfo(userResponse, userUpdateRequest);
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

        String apiToken = UserAcceptanceStep.requestToLoginAndGetAccessToken(
            UserInfoFromToken.of(user), jwt);

        ExtractableResponse<Response> response = UserAcceptanceStep.uploadProfileImage(apiToken,
            image);
        ProfileResponse profileResponse = response.jsonPath().getObject(".", ProfileResponse.class);
        UserResponse userResponse = UserAcceptanceStep.requestToGetMyInfo(apiToken).jsonPath()
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

        String apiToken = UserAcceptanceStep.requestToLoginAndGetAccessToken(
            UserInfoFromToken.of(user), jwt);

        UserAcceptanceStep.uploadProfileImage(apiToken, image);
        ExtractableResponse<Response> response = UserAcceptanceStep.deleteProfileImage(apiToken);
        UserResponse userResponse = UserAcceptanceStep.requestToGetMyInfo(apiToken).jsonPath()
            .getObject(".", UserResponse.class);

        AcceptanceStep.assertThatStatusIsOk(response);
        UserAcceptanceStep.assertThatDeleteProfileImage(userResponse);
    }

    @DisplayName("관심목록에 추가한다.")
    @Test
    void addFavorite() {
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

        String apiToken = UserAcceptanceStep.requestToLoginAndGetAccessToken(
            UserInfoFromToken.of(user), jwt);
        Long articleId = AcceptanceUtils.getIdFromResponse(
            ArticleAcceptanceStep.requestToCreateArticle(apiToken, articleRequest));

        ExtractableResponse<Response> response = UserAcceptanceStep.addFavorite(apiToken,
            new FavoriteRequest(articleId));
        List<FavoriteResponse> favoriteRespons = UserAcceptanceStep.findFavorites(apiToken)
            .jsonPath()
            .getList(".", FavoriteResponse.class);

        AcceptanceStep.assertThatStatusIsOk(response);
        UserAcceptanceStep.assertThatAddFavorite(favoriteRespons, articleRequest);
    }

    @DisplayName("관심목록에서 삭제한다.")
    @Test
    void deleteFavorite() {
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

        String apiToken = UserAcceptanceStep.requestToLoginAndGetAccessToken(
            UserInfoFromToken.of(user), jwt);
        Long articleId = AcceptanceUtils.getIdFromResponse(
            ArticleAcceptanceStep.requestToCreateArticle(apiToken, articleRequest));
        UserAcceptanceStep.addFavorite(apiToken, new FavoriteRequest(articleId));
        List<FavoriteResponse> favoriteResponseAfterAddFavorite = UserAcceptanceStep.findFavorites(
                apiToken).jsonPath()
            .getList(".", FavoriteResponse.class);
        Long favoriteCount = favoriteResponseAfterAddFavorite.get(0).getArticlesResponse()
            .getFavoriteCount();
        Long favoriteId = favoriteResponseAfterAddFavorite.get(0).getFavoriteId();

        ExtractableResponse<Response> response = UserAcceptanceStep.deleteFavorite(
            apiToken, favoriteId);
        List<FavoriteResponse> favoriteRespons = UserAcceptanceStep.findFavorites(apiToken)
            .jsonPath()
            .getList(".", FavoriteResponse.class);

        ExtractableResponse<Response> responseAfterDeleteFavorite = ArticleAcceptanceStep.requestToFindArticle(
            apiToken, articleId);
        ArticleResponse articleResponse = responseAfterDeleteFavorite.jsonPath()
            .getObject(".", ArticleResponse.class);

        AcceptanceStep.assertThatStatusIsOk(response);
        assertThat(favoriteRespons.size()).isEqualTo(0);
        assertThat(articleResponse.getFavoriteCount()).isEqualTo(favoriteCount - 1L);
    }

    @DisplayName("push 상태를 수정한다.")
    @Test
    void updateMyPushSettingStatus() {
        String apiToken = UserAcceptanceStep.requestToLoginAndGetAccessToken(
            UserInfoFromToken.of(user), jwt);

        UserPushSettingResponse beforePushSettingResponse = UserAcceptanceStep.requestToGetPushSetting(
                apiToken).jsonPath()
            .getObject(".", UserPushSettingResponse.class);

        ExtractableResponse<Response> response = UserAcceptanceStep.requestToTogglePushSetting(
            apiToken);
        UserPushSettingResponse afterPushSettingResponse = response.jsonPath()
            .getObject(".", UserPushSettingResponse.class);

        AcceptanceStep.assertThatStatusIsOk(response);
        assertThat(beforePushSettingResponse.getPushActivate()).isNotEqualTo(
            afterPushSettingResponse.getPushActivate());
    }
}
