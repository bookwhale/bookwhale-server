package com.teamherb.bookstoreback.account.service;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.teamherb.bookstoreback.account.domain.Account;
import com.teamherb.bookstoreback.account.domain.AccountRepository;
import com.teamherb.bookstoreback.account.dto.AccountRequest;
import com.teamherb.bookstoreback.account.dto.AccountUpdateRequest;
import com.teamherb.bookstoreback.common.exception.CustomException;
import com.teamherb.bookstoreback.common.exception.dto.ErrorCode;
import com.teamherb.bookstoreback.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("계좌 단위 테스트(Service)")
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    AccountService accountService;

    User user;

    @BeforeEach
    void setUp() {
        accountService = new AccountService(accountRepository);

        user = User.builder()
            .identity("highright96")
            .name("남상우")
            .email("highright96@email.com")
            .phoneNumber("010-1234-1234")
            .address("서울")
            .build();
    }

    @DisplayName("계좌를 생성한다.")
    @Test
    void createAccount_success() {
        AccountRequest accountRequest = AccountRequest.builder()
            .accountNumber("123-1234-12345")
            .accountOwner("남상우")
            .accountBank("국민은행")
            .build();

        when(accountRepository.countByUser(any())).thenReturn(1L);
        when(accountRepository.save(any())).thenReturn(any());

        accountService.createAccount(user, accountRequest);

        verify(accountRepository).countByUser(any());
        verify(accountRepository).save(any());
    }

    @DisplayName("이미 3개의 계좌가 등록된 경우 예외가 발생한다.")
    @Test
    void createAccount_maximum_failure() {
        AccountRequest accountRequest = AccountRequest.builder()
            .accountNumber("123-1234-12345")
            .accountOwner("남상우")
            .accountBank("국민은행")
            .build();

        when(accountRepository.countByUser(any())).thenReturn(3L);

        assertThatThrownBy(() -> accountService.createAccount(user, accountRequest))
            .isInstanceOf(CustomException.class)
            .hasMessage(ErrorCode.MAXIMUM_NUMBER_ACCOUNT.getMessage());
    }

    @DisplayName("계좌를 수정한다.")
    @Test
    void updateAccount_success() {
        AccountUpdateRequest req = AccountUpdateRequest.builder()
            .accountId(1L)
            .accountNumber("123-1234-12345")
            .accountOwner("남상우")
            .accountBank("국민은행")
            .build();

        Account account = Account.builder().build();

        when(accountRepository.findAccountByIdAndUser(any(), any())).thenReturn(
            ofNullable(account));

        accountService.updateAccount(user, req);

        verify(accountRepository).findAccountByIdAndUser(any(), any());
        assertAll(
            () -> assertThat(account.getAccountBank()).isEqualTo(req.getAccountBank()),
            () -> assertThat(account.getAccountNumber()).isEqualTo(req.getAccountNumber()),
            () -> assertThat(account.getAccountOwner()).isEqualTo(req.getAccountOwner())
        );
    }

    @DisplayName("권한이 없는 유저가 계좌를 수정하면 예외가 발생한다.")
    @Test
    void updateAccount_accessDenied_failure() {
        when(accountRepository.findAccountByIdAndUser(any(), any())).thenReturn(empty());

        assertThatThrownBy(
            () -> accountService.updateAccount(user, AccountUpdateRequest.builder().build()))
            .isInstanceOf(CustomException.class)
            .hasMessage(ErrorCode.USER_ACCESS_DENIED.getMessage());
    }
}