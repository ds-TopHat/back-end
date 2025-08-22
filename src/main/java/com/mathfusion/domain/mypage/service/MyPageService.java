package com.mathfusion.domain.mypage.service;

import com.mathfusion.domain.mypage.dto.MyPageResponse;
import com.mathfusion.domain.mypage.repository.MyPageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MyPageService {
    private final MyPageRepository myPageRepository;

    public MyPageResponse getMypage(String email) {
        String username = email.split("@")[0];

        List<MyPageResponse.Units> units = myPageRepository.findUnitsByUserEmail(email);

        return MyPageResponse.builder()
                .name(username)
                .unitsList(units)
                .build();
    }
}
