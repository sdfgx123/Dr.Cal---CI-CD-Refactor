package com.fc.mini3server._core.security;

import com.fc.mini3server._core.handler.exception.Exception500;
import com.fc.mini3server.domain.User;
import com.fc.mini3server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrincipalUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new Exception500("이메일과 일치하는 회원 정보가 존재하지 않습니다.");
        }

        return new PrincipalUserDetail(user);
    }
}
