package com.teamherb.bookstoreback.user.service;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.teamherb.bookstoreback.account.domain.Account;
import com.teamherb.bookstoreback.account.domain.AccountRepository;
import com.teamherb.bookstoreback.account.dto.AccountRequest;
import com.teamherb.bookstoreback.common.exception.CustomException;
import com.teamherb.bookstoreback.common.exception.dto.ErrorCode;
import com.teamherb.bookstoreback.user.domain.User;
import com.teamherb.bookstoreback.user.domain.UserRepository;
import com.teamherb.bookstoreback.user.dto.SignUpRequest;
import com.teamherb.bookstoreback.user.dto.UserResponse;
import com.teamherb.bookstoreback.user.dto.UserUpdateRequest;
import java.util.Collections;
import java.util.List;
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
    void createUser_success() {
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
    void getMyInfo_success() {
        when(accountRepository.findAllByUser(any())).thenReturn(singletonList(account));

        UserResponse userResponse = userService.getMyInfo(user);

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

    @DisplayName("내 정보를 수정한다.")
    @Test
    void updateMyInfo_success() {
        AccountRequest kbAccountRequest = AccountRequest.builder()
            .accountBank("국민은행")
            .accountNumber("12-12345-12345")
            .accountOwner("주호세")
            .build();

        AccountRequest hanaAccountRequest = AccountRequest.builder()
            .accountBank("하나은행")
            .accountNumber("12-12345-12345")
            .accountOwner("주호세")
            .build();

        UserUpdateRequest userUpdateRequest = UserUpdateRequest.builder()
            .name("주호세")
            .phoneNumber("010-0000-0000")
            .address("경기")
            .accounts(List.of(kbAccountRequest, hanaAccountRequest))
            .build();

        doNothing().when(accountRepository).deleteAllByUser(any());
        when(accountRepository.saveAll(any())).thenReturn(any());

        userService.updateMyInfo(user, userUpdateRequest);

        verify(accountRepository).deleteAllByUser(any());
        verify(accountRepository).saveAll(any());

        Assertions.assertAll(
            () -> assertThat(user.getName()).isEqualTo(userUpdateRequest.getName()),
            () -> assertThat(user.getAddress()).isEqualTo(userUpdateRequest.getAddress()),
            () -> assertThat(user.getPhoneNumber()).isEqualTo(userUpdateRequest.getPhoneNumber())
        );
    }

    @DisplayName("내 정보를 수정할 때 빈 계좌 리스트를 받으면 예외가 발생한다.")
    @Test
    void updateMyInfo_emptyAccounts_failure() {
        UserUpdateRequest userUpdateRequest = UserUpdateRequest.builder()
            .accounts(Collections.emptyList())
            .build();

        assertThatThrownBy(() -> userService.updateMyInfo(user, userUpdateRequest)).
            isInstanceOf(CustomException.class)
            .hasMessage(ErrorCode.MINIMUM_NUMBER_ACCOUNT.getMessage());
    }

    @DisplayName("내 정보를 수정할 때 세 개를 넘은 계좌 리스트를 받으면 예외가 발생한다.")
    @Test
    void updateMyInfo_overAccounts_failure() {
        UserUpdateRequest userUpdateRequest = UserUpdateRequest.builder()
            .accounts(List.of(new AccountRequest(), new AccountRequest(), new AccountRequest(),
                new AccountRequest()))
            .build();

        assertThatThrownBy(() -> userService.updateMyInfo(user, userUpdateRequest)).
            isInstanceOf(CustomException.class)
            .hasMessage(ErrorCode.MAXIMUM_NUMBER_ACCOUNT.getMessage());
    }
}
