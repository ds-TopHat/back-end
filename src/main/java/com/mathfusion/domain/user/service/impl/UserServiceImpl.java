package com.mathfusion.domain.user.service.impl;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.mathfusion.domain.user.dto.UserRequest;
import com.mathfusion.domain.user.entity.User;
import com.mathfusion.domain.user.exception.UserException;
import com.mathfusion.domain.user.repository.EmailVerificationRepository;
import com.mathfusion.domain.user.repository.UserRepository;
import com.mathfusion.domain.user.service.UserService;
import com.mathfusion.global.apiPayload.code.status.ErrorStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

//회원가입 구현체
@RequiredArgsConstructor
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmailVerificationRepository emailVerificationRepository;

    @Override
    public Long signup(UserRequest.SignupRequest dto){
        String email = dto.getEmail();
        log.info("회원가입 시도: {}", email);

        // 입력값 검증
        if (email == null || email.trim().isEmpty()) {
            log.error("이메일이 비어있습니다.");
            throw new UserException(ErrorStatus.INVALID_INPUT);
        }
        if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
            log.error("비밀번호가 비어있습니다.");
            throw new UserException(ErrorStatus.INVALID_INPUT);
        }

        try {
            //이메일 중복 확인
            if (userRepository.existsByEmail(email)){
                log.error("이미 존재하는 이메일: {}", email);
                throw new UserException(ErrorStatus.USER_ALREADY_EXISTS);
            }
            log.info("이메일 중복 확인 완료: {}", email);

            // TODO: 개발/테스트 환경에서는 이메일 인증을 건너뛰고 있습니다.
            // 운영 환경에서는 아래 주석을 해제하고 이메일 인증을 활성화하세요.
            
            /*
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
            */

            //회원가입 처리
            User user = User.builder()
                    .email(email)
                    .password(bCryptPasswordEncoder.encode(dto.getPassword()))
                    .build();

            User savedUser = userRepository.save(user);
            log.info("회원가입 완료: {}", savedUser.getEmail());
            return savedUser.getId();

        } catch (UserException e) {
            log.error("회원가입 중 사용자 관련 오류: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("회원가입 중 예상치 못한 오류: {}", e.getMessage(), e);
            throw new UserException(ErrorStatus.INVALID_INPUT);
        }
    }

    //회원탈퇴
    @Override
    public void deleteByEmail(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new UserException(ErrorStatus.USER_NOT_FOUND));

        userRepository.delete(user);
    }

}
