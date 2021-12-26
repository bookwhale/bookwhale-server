package com.bookwhale.user.controller;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bookwhale.article.domain.BookStatus;
import com.bookwhale.article.dto.ArticlesResponse;
import com.bookwhale.common.controller.CommonApiTest;
import com.bookwhale.common.domain.Location;
import com.bookwhale.common.security.WithMockCustomUser;
import com.bookwhale.user.docs.UserDocumentation;
import com.bookwhale.user.dto.FavoriteRequest;
import com.bookwhale.user.dto.FavoriteResponse;
import com.bookwhale.user.service.FavoriteService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;


@DisplayName("관심목록 관련 기능 단위 테스트(Controller)")
@WebMvcTest(controllers = FavoriteController.class)
public class FavoriteControllerTest extends CommonApiTest {

    @MockBean
    FavoriteService favoriteService;

    @WithMockCustomUser
    @DisplayName("관심목록을 조회한다.")
    @Test
    void findFavorites() throws Exception {
        List<FavoriteResponse> responses = List.of(
            new FavoriteResponse(1L,
                ArticlesResponse.builder()
                    .articleId(1L)
                    .articleImage("이미지")
                    .articleTitle("책 팝니다~")
                    .articlePrice("20000원")
                    .bookStatus(BookStatus.BEST.getName())
                    .sellingLocation(Location.SEOUL.getName())
                    .chatCount(1L)
                    .favoriteCount(0L)
                    .beforeTime("15분 전")
                    .build()
            )
        );

        when(favoriteService.findAllFavorites(any())).thenReturn(responses);

        mockMvc.perform(get("/api/user/me/favorites")
                .header(HttpHeaders.AUTHORIZATION, "accessToken"))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(UserDocumentation.userFindFavorites());
    }

    @WithMockCustomUser
    @DisplayName("관심목록에 추가한다.")
    @Test
    void addFavorite() throws Exception {
        FavoriteRequest favoriteRequest = new FavoriteRequest(1L);

        doNothing().when(favoriteService).addFavorite(any(), any());

        mockMvc.perform(post("/api/user/me/favorite")
                .header(HttpHeaders.AUTHORIZATION, "accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(favoriteRequest)))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(UserDocumentation.userAddFavorite());
    }

    @WithMockCustomUser
    @DisplayName("관심목록에서 삭제한다.")
    @Test
    void deleteFavorite() throws Exception {
        doNothing().when(favoriteService).deleteFavorite(any(), any());

        mockMvc.perform(
                RestDocumentationRequestBuilders.delete("/api/user/me/favorite/{favoriteId}", 1L)
                    .header(HttpHeaders.AUTHORIZATION, "accessToken"))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(UserDocumentation.userDeleteFavorite());
    }
}
