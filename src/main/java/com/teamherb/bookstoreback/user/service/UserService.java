package com.teamherb.bookstoreback.user.service;

import com.teamherb.bookstoreback.user.domain.Role;
import com.teamherb.bookstoreback.user.domain.User;
import com.teamherb.bookstoreback.user.domain.UserRepository;
import com.teamherb.bookstoreback.user.dto.SignUpRequest;
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

    public Long createUser(SignUpRequest signUpRequest) {
        if (userRepository.existsByIdentity(signUpRequest.getIdentity())) {
            throw new IllegalArgumentException("동일한 아이디가 존재합니다.");
        }

        User user = User.builder()
            .identity(signUpRequest.getIdentity())
            .password(passwordEncoder.encode(signUpRequest.getPassword()))
            .email(signUpRequest.getEmail())
            .name(signUpRequest.getName())
            .role(Role.ROLE_USER)
            .build();

        User savedUser = userRepository.save(user);
        return savedUser.getId();
    }
}