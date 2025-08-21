package com.mathfusion.domain.user.service.impl;

import com.mathfusion.global.apiPayload.code.status.ErrorStatus;
import com.mathfusion.domain.user.dto.UserRequest;
import com.mathfusion.domain.user.entity.User;
import com.mathfusion.domain.user.exception.UserException;
import com.mathfusion.domain.user.repository.UserRepository;
import com.mathfusion.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

//회원가입 구현체
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public Long signup(UserRequest.SignupRequest dto){
        String email = dto.getEmail();

        //이메일 중복 확인
        if (userRepository.existsByEmail(email)){
            throw new UserException(ErrorStatus.USER_ALREADY_EXISTS);
        }

        //회원가입 처리
        User user = User.builder()
                .email(email)
                .password(bCryptPasswordEncoder.encode(dto.getPassword()))
                .build();

        return userRepository.save(user).getId();
    }

    //회원탈퇴
    @Override
    public void deleteByEmail(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new UserException(ErrorStatus.USER_NOT_FOUND));

        userRepository.delete(user);
    }

}
