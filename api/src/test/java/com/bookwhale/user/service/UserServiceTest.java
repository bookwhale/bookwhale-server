package com.bookwhale.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bookwhale.common.exception.CustomException;
import com.bookwhale.common.exception.ErrorCode;
import com.bookwhale.common.upload.FileUploader;
import com.bookwhale.favorite.domain.FavoriteRepository;
import com.bookwhale.article.domain.ArticleRepository;
import com.bookwhale.user.domain.User;
import com.bookwhale.user.domain.UserRepository;
import com.bookwhale.user.dto.PasswordUpdateRequest;
import com.bookwhale.user.dto.ProfileResponse;
import com.bookwhale.user.dto.SignUpRequest;
import com.bookwhale.user.dto.UserUpdateRequest;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("유저 단위 테스트(Service)")
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

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
        userService = new UserService(userRepository, passwordEncoder, fileUploader,
            articleRepository);

        user = User.builder()
            .id(1L)
            .identity("highright96")
            .password("1234")
            .name("남상우")
            .email("highright96@email.com")
            .phoneNumber("010-1234-1234")
            .build();
    }

    @DisplayName("회원가입을 한다.")
    @Test
    void createUser_success() {
        SignUpRequest signUpRequest = SignUpRequest.builder()
            .identity("highright96")
            .password("1234")
            .name("남상우")
            .email("highright96@email.com")
            .phoneNumber("010-1234-1234")
            .build();

        when(userRepository.existsByIdentity(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn(signUpRequest.getPassword());
        when(userRepository.save(any())).thenReturn(user);

        userService.createUser(signUpRequest);

        verify(userRepository).existsByIdentity(any());
        verify(passwordEncoder).encode(any());
        verify(userRepository).save(any());
    }

    @DisplayName("회원가입을 할 때 중복된 아이디면 예외가 발생한다.")
    @Test
    void createUser_duplicatedIdentity_failure() {
        SignUpRequest signUpRequest = SignUpRequest.builder()
            .identity("highright96")
            .password("1234")
            .name("남상우")
            .email("highright96@email.com")
            .phoneNumber("010-1234-1234")
            .build();

        when(userRepository.existsByIdentity(any())).thenReturn(true);
        assertThatThrownBy(() -> userService.createUser(signUpRequest)).
            isInstanceOf(CustomException.class)
            .hasMessage(ErrorCode.DUPLICATED_USER_IDENTITY.getMessage());
    }

    @DisplayName("내 정보를 수정한다.")
    @Test
    void updateMyInfo_success() {
        UserUpdateRequest userUpdateRequest = UserUpdateRequest.builder()
            .name("테스터1")
            .phoneNumber("010-0000-0000")
            .email("tester1@email.com")
            .build();

        userService.updateMyInfo(user, userUpdateRequest);

        Assertions.assertAll(
            () -> assertThat(user.getName()).isEqualTo(userUpdateRequest.getName()),
            () -> assertThat(user.getEmail()).isEqualTo(userUpdateRequest.getEmail()),
            () -> assertThat(user.getPhoneNumber()).isEqualTo(userUpdateRequest.getPhoneNumber())
        );
    }

    @DisplayName("비밀번호를 수정한다.")
    @Test
    void updatePassword_success() {
        PasswordUpdateRequest request = new PasswordUpdateRequest("1234", "12345");

        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        userService.updatePassword(user, request);

        assertThat(user.getPassword()).isEqualTo(passwordEncoder.encode(request.getNewPassword()));
    }

    @DisplayName("비밀번호를 수정을 할 때 기존 비밀번호가 틀리면 예외가 발생한다.")
    @Test
    void updatePassword_failure() {
        PasswordUpdateRequest req = new PasswordUpdateRequest("invalidPassword", "12345");

        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        assertThatThrownBy(() -> userService.updatePassword(user, req))
            .isInstanceOf(CustomException.class)
            .hasMessage(ErrorCode.INVALID_USER_PASSWORD.getMessage());
    }

    @DisplayName("프로필 사진을 업로드한다.")
    @Test
    void uploadProfileImage_success() {
        MockMultipartFile image = new MockMultipartFile("profileImage", "profileImage.jpg",
            ContentType.IMAGE_JPEG.getMimeType(),
            "프로필 이미지 입니다.".getBytes());
        String uploadedImage = "uploadImage";

        when(fileUploader.uploadFile(any())).thenReturn(uploadedImage);

        ProfileResponse profileResponse = userService.uploadProfileImage(user, image);

        assertThat(profileResponse.getProfileImage()).isEqualTo(uploadedImage);
    }

    @DisplayName("프로필 사진을 삭제한다.")
    @Test
    void deleteProfileImage_success() {
        userService.deleteProfileImage(user);
        assertThat(user.getProfileImage()).isEqualTo(null);
    }
}
