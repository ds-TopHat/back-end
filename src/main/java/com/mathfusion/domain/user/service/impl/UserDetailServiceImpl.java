package com.mathfusion.domain.user.service.impl;


import com.mathfusion.domain.exception.ErrorStatus;
import com.mathfusion.domain.user.entity.User;
import com.mathfusion.domain.user.exception.UserException;
import com.mathfusion.domain.user.repository.UserRepository;
import com.mathfusion.domain.user.service.UserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

//인증 구현체
@RequiredArgsConstructor
@Service
public class UserDetailServiceImpl implements UserDetailService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(ErrorStatus.USER_NOT_FOUND));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles("USER") // 기본 역할
                .build();
    }
}
