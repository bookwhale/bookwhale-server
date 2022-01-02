package com.bookwhale.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.bookwhale.article.domain.ArticleRepository;
import com.bookwhale.common.upload.FileUploader;
import com.bookwhale.favorite.domain.FavoriteRepository;
import com.bookwhale.user.domain.User;
import com.bookwhale.user.domain.UserRepository;
import com.bookwhale.user.dto.ProfileResponse;
import com.bookwhale.user.dto.UserUpdateRequest;
import java.util.Optional;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
@DisplayName("유저 단위 테스트(Service)")
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FileUploader fileUploader;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private FavoriteRepository favoriteRepository;

    UserService userService;

    User user;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, fileUploader);

        user = User.builder()
            .id(1L)
            .nickname("남상우")
            .email("highright96@email.com")
            .build();
    }

    @DisplayName("내 정보를 수정한다.")
    @Test
    void updateMyInfo_success() {
        UserUpdateRequest userUpdateRequest = UserUpdateRequest.builder()
            .nickname("테스터1")
            .build();

        when(userRepository.findByEmail(any(String.class)))
            .thenReturn(Optional.of(user));

        userService.updateMyInfo(user, userUpdateRequest);

        Assertions.assertAll(
            () -> assertThat(user.getNickname()).isEqualTo(userUpdateRequest.getNickname())
        );
    }

    @DisplayName("프로필 사진을 업로드한다.")
    @Test
    void uploadProfileImage_success() {
        MockMultipartFile image = new MockMultipartFile("profileImage", "profileImage.jpg",
            ContentType.IMAGE_JPEG.getMimeType(),
            "프로필 이미지 입니다.".getBytes());
        String uploadedImage = "uploadImage";

        when(userRepository.findByEmail(any(String.class)))
            .thenReturn(Optional.of(user));
        when(fileUploader.uploadFile(any())).thenReturn(uploadedImage);

        ProfileResponse profileResponse = userService.uploadProfileImage(user, image);

        assertThat(profileResponse.getProfileImage()).isEqualTo(uploadedImage);
    }

    @DisplayName("프로필 사진을 삭제한다.")
    @Test
    void deleteProfileImage_success() {
        when(userRepository.findByEmail(any(String.class)))
            .thenReturn(Optional.of(user));

        userService.deleteProfileImage(user);
        assertThat(user.getProfileImage()).isEqualTo(null);
    }
}
