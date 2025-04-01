package com.mathfusion.domain.user.service;

import com.mathfusion.domain.user.dto.AddUserRequest;
import com.mathfusion.domain.user.entity.User;
import com.mathfusion.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
// 정보암호화하는 등 로직처리
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public Long save(AddUserRequest dto){
        return userRepository.save(User.builder()
                .email(dto.getEmail())
                // 패스워드 암호화
                .password(bCryptPasswordEncoder.encode(dto.getPassword()))
                .build()).getId();

    }
}
