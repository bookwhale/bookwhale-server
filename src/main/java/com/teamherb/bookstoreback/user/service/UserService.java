package com.teamherb.bookstoreback.user.service;

import com.teamherb.bookstoreback.common.exception.CustomException;
import com.teamherb.bookstoreback.common.exception.dto.ErrorCode;
import com.teamherb.bookstoreback.user.domain.Role;
import com.teamherb.bookstoreback.user.domain.User;
import com.teamherb.bookstoreback.user.domain.UserRepository;
import com.teamherb.bookstoreback.user.dto.SignUpRequest;
import com.teamherb.bookstoreback.user.dto.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

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

    public void updateMyInfo(User user, UserUpdateRequest userUpdateRequest) {
        user.update(userUpdateRequest);
        userRepository.save(user);
    }
}
