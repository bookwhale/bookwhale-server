package com.teamherb.bookstoreback.user.service;

import static java.util.Collections.singletonList;
import static java.util.Optional.ofNullable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.teamherb.bookstoreback.account.domain.Account;
import com.teamherb.bookstoreback.account.domain.AccountRepository;
import com.teamherb.bookstoreback.common.exception.CustomException;
import com.teamherb.bookstoreback.common.exception.dto.ErrorCode;
import com.teamherb.bookstoreback.user.domain.User;
import com.teamherb.bookstoreback.user.domain.UserRepository;
import com.teamherb.bookstoreback.user.dto.SignUpRequest;
import com.teamherb.bookstoreback.user.dto.UserResponse;
import java.util.Optional;
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
    private AccountRepository accountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    User user;

    Account account;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, accountRepository, passwordEncoder);

        user = User.builder()
            .identity("highright96")
            .name("남상우")
            .email("highright96@email.com")
            .phoneNumber("010-1234-1234")
            .address("서울")
            .build();

        account = Account.builder()
            .user(user)
            .accountBank("국민은행")
            .accountNumber("123-1234-12345")
            .accountOwner("남상우")
            .build();
    }

    @DisplayName("회원가입을 한다.")
    @Test
    void createUser() {
        when(userRepository.existsByIdentity(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("1234");
        when(userRepository.save(any())).thenReturn(user);

        userService.createUser(new SignUpRequest());

        verify(userRepository).existsByIdentity(any());
        verify(passwordEncoder).encode(any());
        verify(userRepository).save(any());
    }

    @DisplayName("회원가입을 할 때 중복된 아이디면 예외가 발생한다.")
    @Test
    void createUser_duplicatedIdentity_failure() {
        when(userRepository.existsByIdentity(any())).thenReturn(true);
        assertThatThrownBy(() -> userService.createUser(new SignUpRequest())).
            isInstanceOf(CustomException.class)
            .hasMessage(ErrorCode.DUPLICATED_USER_IDENTITY.getMessage());
    }

    @DisplayName("내 정보를 조회한다.")
    @Test
    void getMyInfo() {
        when(userRepository.findById(any())).thenReturn(ofNullable(user));
        when(accountRepository.findAllByUser(any())).thenReturn(singletonList(account));

        UserResponse userResponse = userService.getMyInfo(user);

        verify(userRepository).findById(any());
        verify(accountRepository).findAllByUser(any());

        Assertions.assertAll(
            () -> assertThat(userResponse.getIdentity()).isEqualTo(user.getIdentity()),
            () -> assertThat(userResponse.getAccountResponse().get(0).getAccountBank())
                .isEqualTo(account.getAccountBank()),
            () -> assertThat(userResponse.getAccountResponse().get(0).getAccountNumber())
                .isEqualTo(account.getAccountNumber()),
            () -> assertThat(userResponse.getAccountResponse().get(0).getAccountOwner())
                .isEqualTo(account.getAccountOwner())
        );
    }

    @DisplayName("내 정보를 조회할 때 접근 권한이 없으면 예외가 발생한다.")
    @Test
    void getMyInfo_accessDenied_failure() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getMyInfo(user)).
            isInstanceOf(CustomException.class)
            .hasMessage(ErrorCode.USER_ACCESS_DENIED.getMessage());
    }
}
