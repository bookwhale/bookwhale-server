package com.bookwhale.user.controller;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bookwhale.common.controller.CommonApiTest;
import com.bookwhale.common.domain.Location;
import com.bookwhale.common.security.WithMockCustomUser;
import com.bookwhale.post.domain.PostStatus;
import com.bookwhale.post.dto.PostsResponse;
import com.bookwhale.user.docs.UserDocumentation;
import com.bookwhale.user.dto.LikeRequest;
import com.bookwhale.user.dto.LikeResponse;
import com.bookwhale.user.service.LikeService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;


@DisplayName("관심목록 관련 기능 단위 테스트(Controller)")
@WebMvcTest(controllers = LikeController.class)
public class LikeControllerTest extends CommonApiTest {

  @MockBean
  LikeService likeService;

  @WithMockCustomUser
  @DisplayName("관심목록을 조회한다.")
  @Test
  void findLikes() throws Exception {
    List<LikeResponse> responses = List.of(
        new LikeResponse(1L,
            PostsResponse.builder()
                .postId(1L)
                .postImage("이미지")
                .postTitle("책 팝니다~")
                .postPrice("20000원")
                .postStatus(PostStatus.SALE.getName())
                .description("책 설명 보충합니다.")
                .sellingLocation(Location.SEOUL.getName())
                .viewCount(1L)
                .likeCount(1L)
                .beforeTime("15분 전")
                .build()
        )
    );

    when(likeService.findAllLikes(any())).thenReturn(responses);

    mockMvc.perform(get("/api/user/me/likes")
            .header(HttpHeaders.AUTHORIZATION, "accessToken"))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(UserDocumentation.userFindLikes());
  }

  @WithMockCustomUser
  @DisplayName("관심목록에 추가한다.")
  @Test
  void addLike() throws Exception {
    LikeRequest likeRequest = new LikeRequest(1L);

    doNothing().when(likeService).addLike(any(), any());

    mockMvc.perform(post("/api/user/me/like")
            .header(HttpHeaders.AUTHORIZATION, "accessToken")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(likeRequest)))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(UserDocumentation.userAddLike());
  }

  @WithMockCustomUser
  @DisplayName("관심목록에서 삭제한다.")
  @Test
  void deleteLike() throws Exception {
    doNothing().when(likeService).deleteLike(any(), any());

    mockMvc.perform(
            RestDocumentationRequestBuilders.delete("/api/user/me/like/{likeId}", 1L)
                .header(HttpHeaders.AUTHORIZATION, "accessToken"))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(UserDocumentation.userDeleteLike());
  }
}
