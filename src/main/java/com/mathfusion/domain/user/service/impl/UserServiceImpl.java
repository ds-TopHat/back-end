package com.mathfusion.domain.user.service.impl;

import com.mathfusion.domain.exception.ErrorStatus;
import com.mathfusion.domain.user.dto.UserRequest;
import com.mathfusion.domain.user.entity.EmailVerification;
import com.mathfusion.domain.user.entity.User;
import com.mathfusion.domain.user.exception.EmailErrorCode;
import com.mathfusion.domain.user.exception.EmailException;
import com.mathfusion.domain.user.exception.UserException;
import com.mathfusion.domain.user.repository.EmailVerificationRepository;
import com.mathfusion.domain.user.repository.UserRepository;
import com.mathfusion.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

//회원가입 구현체
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmailVerificationRepository emailVerificationRepository;

    @Override
    public Long signup(UserRequest.SignupRequest dto){
        String email = dto.getEmail();

        //이메일 중복 확인
        if (userRepository.existsByEmail(email)){
            throw new UserException(ErrorStatus.USER_ALREADY_EXISTS);
        }

        //이메일 인증 여부 확인
        EmailVerification emailVerification = emailVerificationRepository.findTopByEmailOrderByExpiredTimeDesc(email)
                .orElseThrow(()-> new EmailException(EmailErrorCode.INVALID_CODE));

        //인증 실패
        if(!emailVerification.isVerified()){
            throw new EmailException(EmailErrorCode.NOT_VERIFIED);
        }

        //인증번호 만료
        if(emailVerification.getExpiredTime().isBefore(LocalDateTime.now())) {
            throw new EmailException(EmailErrorCode.EXPIRED);
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
