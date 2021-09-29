package com.teamherb.bookstoreback.user.controller;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.teamherb.bookstoreback.Interest.dto.InterestRequest;
import com.teamherb.bookstoreback.Interest.dto.InterestResponse;
import com.teamherb.bookstoreback.common.Pagination;
import com.teamherb.bookstoreback.common.controller.CommonApiTest;
import com.teamherb.bookstoreback.common.security.WithMockCustomUser;
import com.teamherb.bookstoreback.post.domain.PostStatus;
import com.teamherb.bookstoreback.post.dto.PostsResponse;
import com.teamherb.bookstoreback.user.docs.UserDocumentation;
import com.teamherb.bookstoreback.user.dto.LoginRequest;
import com.teamherb.bookstoreback.user.dto.PasswordUpdateRequest;
import com.teamherb.bookstoreback.user.dto.ProfileResponse;
import com.teamherb.bookstoreback.user.dto.SignUpRequest;
import com.teamherb.bookstoreback.user.dto.UserUpdateRequest;
import com.teamherb.bookstoreback.user.service.UserService;
import java.time.LocalDateTime;
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


@DisplayName("유저 단위 테스트(Controller)")
@WebMvcTest(controllers = UserController.class)
public class UserControllerTest extends CommonApiTest {

  @MockBean
  UserService userService;

  @DisplayName("유저 회원가입을 한다.")
  @Test
  void createUser() throws Exception {
    SignUpRequest signUpRequest = SignUpRequest.builder()
        .identity("highright96")
        .password("1234")
        .name("남상우")
        .phoneNumber("010-1234-1234")
        .email("highright96@email.com")
        .build();

    doNothing().when(userService).createUser(any());

    mockMvc.perform(post("/api/user/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(signUpRequest)))
        .andExpect(status().isCreated())
        .andDo(print())
        .andDo(UserDocumentation.userSignup());
  }

  @DisplayName("유저 로그인을 한다.")
  @Test
  void login() throws Exception {
    LoginRequest loginRequest = new LoginRequest("highright96", "1234");

    mockMvc.perform(post("/api/user/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isUnauthorized())
        .andDo(print())
        .andDo(UserDocumentation.userLogin());
  }

  @WithMockCustomUser
  @DisplayName("내 정보를 조회한다.")
  @Test
  void getMyInfo() throws Exception {
    mockMvc.perform(get("/api/user/me")
            .header(HttpHeaders.AUTHORIZATION, "accessToken"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("identity").value("highright96"))
        .andExpect(jsonPath("name").value("남상우"))
        .andExpect(jsonPath("email").value("highright96@email.com"))
        .andExpect(jsonPath("phoneNumber").value("010-1234-1234"))
        .andExpect(jsonPath("profileImage").value("profileImage"))
        .andDo(print())
        .andDo(UserDocumentation.userMe());
  }

  @WithMockCustomUser
  @DisplayName("내 정보를 수정한다.")
  @Test
  void updateMyInfo() throws Exception {
    UserUpdateRequest userUpdateRequest = UserUpdateRequest.builder()
        .name("주호세")
        .email("hose@email.com")
        .phoneNumber("010-5678-5678")
        .build();

    doNothing().when(userService).updateMyInfo(any(), any());

    mockMvc.perform(patch("/api/user/me")
            .header(HttpHeaders.AUTHORIZATION, "accessToken")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userUpdateRequest)))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(UserDocumentation.userUpdateMe());
  }

  @WithMockCustomUser
  @DisplayName("비밀번호를 수정한다.")
  @Test
  void updatePassword() throws Exception {
    PasswordUpdateRequest request = new PasswordUpdateRequest("old password", "new password");

    doNothing().when(userService).updatePassword(any(), any());

    mockMvc.perform(patch("/api/user/password")
            .header(HttpHeaders.AUTHORIZATION, "accessToken")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(UserDocumentation.userUpdatePassword());
  }

  @WithMockCustomUser
  @DisplayName("프로필 사진을 업로드한다.")
  @Test
  void uploadProfileImage() throws Exception {
    MockMultipartFile image = new MockMultipartFile("profileImage", "profileImage.jpg",
        ContentType.IMAGE_JPEG.getMimeType(),
        "프로필 이미지 입니다.".getBytes());
    String path = "uploadFileUrl";

    when(userService.uploadProfileImage(any(), any())).thenReturn(new ProfileResponse(path));

    mockMvc.perform(MockMultipartPatchBuilder("/api/user/profile")
            .file(image)
            .header(HttpHeaders.AUTHORIZATION, "accessToken")
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isOk())
        .andExpect(jsonPath("profileImage").value(path))
        .andDo(print())
        .andDo(UserDocumentation.userUploadProfileImage());
  }

  @WithMockCustomUser
  @DisplayName("프로필 사진을 업로드시 잘못된 RequestParam 을 보내면 예외가 발생한다.")
  @Test
  void uploadProfileImage_failure() throws Exception {

    when(userService.uploadProfileImage(any(), any())).thenReturn(new ProfileResponse());

    mockMvc.perform(MockMultipartPatchBuilder("/api/user/profile")
            .header(HttpHeaders.AUTHORIZATION, "accessToken")
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isBadRequest())
        .andDo(print());
  }

  @WithMockCustomUser
  @DisplayName("프로필 사진을 삭제한다.")
  @Test
  void deleteProfileImage() throws Exception {
    doNothing().when(userService).deleteProfileImage(any());

    mockMvc.perform(delete("/api/user/profile")
            .header(HttpHeaders.AUTHORIZATION, "accessToken"))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(UserDocumentation.userDeleteProfileImage());
  }

  @WithMockCustomUser
  @DisplayName("관심목록을 조회한다.")
  @Test
  void findInterests() throws Exception {
    List<InterestResponse> responses = List.of(
        InterestResponse.builder()
            .interestId(1L)
            .postId(1L)
            .bookTitle("토비의 스프링")
            .postTitle("토비의 스프링 팝니다.")
            .bookThumbnail("이미지")
            .postPrice("10000")
            .postStatus(PostStatus.SALE)
            .build()
    );

    when(userService.findInterests(any())).thenReturn(responses);

    mockMvc.perform(get("/api/user/me/interests")
            .header(HttpHeaders.AUTHORIZATION, "accessToken"))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(UserDocumentation.userFindInterests());
  }

  @WithMockCustomUser
  @DisplayName("관심목록에 추가한다.")
  @Test
  void addInterest() throws Exception {
    InterestRequest interestRequest = new InterestRequest(1L);

    doNothing().when(userService).addInterest(any(), any());

    mockMvc.perform(post("/api/user/me/interest")
            .header(HttpHeaders.AUTHORIZATION, "accessToken")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(interestRequest)))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(UserDocumentation.userAddInterest());
  }

  @WithMockCustomUser
  @DisplayName("관심목록에서 삭제한다.")
  @Test
  void deleteInterest() throws Exception {
    doNothing().when(userService).deleteInterest(any(), any());

    mockMvc.perform(
            RestDocumentationRequestBuilders.delete("/api/user/me/interest/{interestId}", 1L)
                .header(HttpHeaders.AUTHORIZATION, "accessToken"))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(UserDocumentation.userDeleteInterest());
  }

  @WithMockCustomUser
  @DisplayName("내 판매글들을 조회한다.")
  @Test
  void findMyPosts() throws Exception {
    PostsResponse postsResponse = PostsResponse.builder()
        .postId(1L)
        .postImage("이미지")
        .postTitle("책 팝니다~")
        .bookTitle("토비의 스프링")
        .postPrice("20000원")
        .postStatus(PostStatus.SALE)
        .createdDate(LocalDateTime.now())
        .build();
    Pagination page = new Pagination(0, 10);

    when(userService.findMyPost(any(), any())).thenReturn(List.of(postsResponse));

    mockMvc.perform(
            get(String.format("/api/user/me/post?page=%d&size=%d", page.getPage(), page.getSize()))
                .header(HttpHeaders.AUTHORIZATION, "accessToken"))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(UserDocumentation.userFindMyPosts());
  }
}
