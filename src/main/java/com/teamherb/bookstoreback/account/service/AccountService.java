package com.teamherb.bookstoreback.account.service;

import com.teamherb.bookstoreback.account.domain.Account;
import com.teamherb.bookstoreback.account.domain.AccountRepository;
import com.teamherb.bookstoreback.account.dto.AccountRequest;
import com.teamherb.bookstoreback.account.dto.AccountResponse;
import com.teamherb.bookstoreback.account.dto.AccountUpdateRequest;
import com.teamherb.bookstoreback.common.exception.CustomException;
import com.teamherb.bookstoreback.common.exception.dto.ErrorCode;
import com.teamherb.bookstoreback.user.domain.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;

    public void createAccount(User user, AccountRequest accountRequest) {
        if (accountRepository.countByUser(user) >= 3) {
            throw new CustomException(ErrorCode.MAXIMUM_NUMBER_ACCOUNT);
        }
        Account account = Account.create(user, accountRequest);
        accountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> findAccounts(User user) {
        List<Account> accounts = accountRepository.findAllByUser(user);
        return AccountResponse.listOf(accounts);
    }

    public void updateAccount(User user, AccountUpdateRequest req) {
        Account findAccount = accountRepository.findAccountByIdAndUser(req.getAccountId(), user)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_ACCESS_DENIED));
        findAccount.update(req);
    }

    public void deleteAccount(User user, Long accountId) {
        Account findAccount = accountRepository.findAccountByIdAndUser(accountId, user)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_ACCESS_DENIED));
        accountRepository.delete(findAccount);
    }
}
