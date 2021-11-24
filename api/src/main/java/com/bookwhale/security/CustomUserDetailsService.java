package com.bookwhale.security;

import com.bookwhale.user.domain.User;
import com.bookwhale.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String identity) throws UsernameNotFoundException {
        User findUser = userRepository.findByIdentity(identity)
            .orElseThrow(() ->
                new UsernameNotFoundException("유저를 찾을 수 없습니다. 아이디 : " + identity)
            );
        return UserPrincipal.create(findUser);
    }
}
