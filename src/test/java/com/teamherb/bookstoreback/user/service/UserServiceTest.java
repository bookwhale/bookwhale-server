package com.teamherb.bookstoreback.user.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.teamherb.bookstoreback.user.domain.User;
import com.teamherb.bookstoreback.user.domain.UserRepository;
import com.teamherb.bookstoreback.user.dto.SignUpRequest;
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

    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, passwordEncoder);

        user = User.builder()
            .id(1L)
            .identity("highright96")
            .password("1234")
            .name("남상우")
            .email("highright96@email.com")
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
}
