package com.teamherb.bookstoreback.user.service;

import com.teamherb.bookstoreback.account.domain.Account;
import com.teamherb.bookstoreback.account.domain.AccountRepository;
import com.teamherb.bookstoreback.account.dto.AccountRequest;
import com.teamherb.bookstoreback.common.exception.CustomException;
import com.teamherb.bookstoreback.common.exception.dto.ErrorCode;
import com.teamherb.bookstoreback.user.domain.Role;
import com.teamherb.bookstoreback.user.domain.User;
import com.teamherb.bookstoreback.user.domain.UserRepository;
import com.teamherb.bookstoreback.user.dto.SignUpRequest;
import com.teamherb.bookstoreback.user.dto.UserResponse;
import com.teamherb.bookstoreback.user.dto.UserUpdateRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    private final AccountRepository accountRepository;

    private final PasswordEncoder passwordEncoder;

    public void createUser(SignUpRequest signUpRequest) {
        if (userRepository.existsByIdentity(signUpRequest.getIdentity())) {
            throw new CustomException(ErrorCode.DUPLICATED_USER_IDENTITY);
        }

        User user = User.builder()
            .identity(signUpRequest.getIdentity())
            .password(passwordEncoder.encode(signUpRequest.getPassword()))
            .email(signUpRequest.getEmail())
            .name(signUpRequest.getName())
            .role(Role.ROLE_USER)
            .build();

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getMyInfo(User user) {
        List<Account> accounts = accountRepository.findAllByUser(user);
        return UserResponse.of(user, accounts);
    }

    public void updateMyInfo(User user, UserUpdateRequest userUpdateRequest) {
        checkSizeToUpdateAccount(userUpdateRequest.getAccounts());
        updateAccounts(user, userUpdateRequest);
        user.update(userUpdateRequest);
    }

    private void checkSizeToUpdateAccount(List<AccountRequest> accounts) {
        if (accounts.isEmpty()) {
            throw new CustomException(ErrorCode.MINIMUM_NUMBER_ACCOUNT);
        }
        if (accounts.size() > 3) {
            throw new CustomException(ErrorCode.MAXIMUM_NUMBER_ACCOUNT);
        }
    }

    private void updateAccounts(User user, UserUpdateRequest userUpdateRequest) {
        accountRepository.deleteAllByUser(user);
        accountRepository.saveAll(userUpdateRequest.toAccounts(user));
    }
}
