package com.teamherb.bookstoreback.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.teamherb.bookstoreback.common.exception.CustomException;
import com.teamherb.bookstoreback.common.exception.dto.ErrorCode;
import com.teamherb.bookstoreback.user.domain.User;
import com.teamherb.bookstoreback.user.domain.UserRepository;
import com.teamherb.bookstoreback.user.dto.SignUpRequest;
import com.teamherb.bookstoreback.user.dto.UserUpdateRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("유저 단위 테스트(Service)")
public class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  UserService userService;

  User user;

  @BeforeEach
  void setUp() {
    userService = new UserService(userRepository, passwordEncoder);

    user = User.builder()
        .identity("highright96")
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
        .name("주호세")
        .phoneNumber("010-0000-0000")
        .email("hose@email.com")
        .build();

    userService.updateMyInfo(user, userUpdateRequest);

    Assertions.assertAll(
        () -> assertThat(user.getName()).isEqualTo(userUpdateRequest.getName()),
        () -> assertThat(user.getEmail()).isEqualTo(userUpdateRequest.getEmail()),
        () -> assertThat(user.getPhoneNumber()).isEqualTo(userUpdateRequest.getPhoneNumber())
    );
  }
}
