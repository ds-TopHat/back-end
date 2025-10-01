package com.mathfusion.domain.user.service;

import com.mathfusion.domain.user.dto.UserRequest;
import org.springframework.stereotype.Service;
// 정보암호화하는 등 로직처리

@Service
public interface UserService {
   Long signup(UserRequest.SignupRequest dto);

   //회원 탈퇴
   void deleteByIdentifier(String identifier);
}
