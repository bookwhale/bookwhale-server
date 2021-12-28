package com.bookwhale.user.controller;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bookwhale.common.controller.CommonApiTest;
import com.bookwhale.user.docs.UserDocumentation;
import com.bookwhale.user.dto.ProfileResponse;
import com.bookwhale.user.dto.UserUpdateRequest;
import com.bookwhale.user.service.FavoriteService;
import com.bookwhale.user.service.UserService;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;


@DisplayName("유저 단위 테스트(Controller)")
@WebMvcTest(controllers = UserController.class)
public class UserControllerTest extends CommonApiTest {

    @MockBean
    UserService userService;

    @MockBean
    FavoriteService favoriteService;

    @DisplayName("내 정보를 수정한다.")
    @Test
    void updateMyInfo() throws Exception {
        UserUpdateRequest userUpdateRequest = UserUpdateRequest.builder()
            .nickname("hose12")
            .build();

        doNothing().when(userService).updateMyInfo(any(), any());

        mockMvc.perform(patch("/api/user/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userUpdateRequest)))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(UserDocumentation.userUpdateMe());
    }

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
                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isOk())
            .andExpect(jsonPath("profileImage").value(path))
            .andDo(print())
            .andDo(UserDocumentation.userUploadProfileImage());
    }


    @DisplayName("프로필 사진을 업로드시 잘못된 RequestParam 을 보내면 예외가 발생한다.")
    @Test
    void uploadProfileImage_failure() throws Exception {

        when(userService.uploadProfileImage(any(), any())).thenReturn(new ProfileResponse());

        mockMvc.perform(MockMultipartPatchBuilder("/api/user/profile")
                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isBadRequest())
            .andDo(print());
    }


    @DisplayName("프로필 사진을 삭제한다.")
    @Test
    void deleteProfileImage() throws Exception {
        doNothing().when(userService).deleteProfileImage(any());

        mockMvc.perform(delete("/api/user/profile")
                .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken"))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(UserDocumentation.userDeleteProfileImage());
    }
}
