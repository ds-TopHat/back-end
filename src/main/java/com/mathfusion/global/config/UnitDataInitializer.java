package com.mathfusion.global.config;

import com.mathfusion.domain.unit.entity.Unit;
import com.mathfusion.domain.unit.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UnitDataInitializer implements CommandLineRunner {

    private final UnitRepository unitRepository;

    @Override
    public void run(String... args) throws Exception {
        // 이미 데이터가 존재하면 초기화하지 않음
        if (unitRepository.count() > 0) {
            return;
        }

        List<Unit> units = List.of(
                // 수와 연산
                new Unit(null, "수와 연산", "자연수의 성질"),
                new Unit(null, "수와 연산", "정수와 유리수"),
                new Unit(null, "수와 연산", "유리수와 소수"),
                new Unit(null, "수와 연산", "유리수와 순환소수"),
                new Unit(null, "수와 연산", "실수와 제곱근"),

                // 문자와 식
                new Unit(null, "문자와 식", "문자와 식"),
                new Unit(null, "문자와 식", "식의 계산"),
                new Unit(null, "문자와 식", "인수분해"),

                // 함수와 방정식
                new Unit(null, "방정식과 함수", "일차방정식"),
                new Unit(null, "방정식과 함수", "좌표평면과 그래프"),
                new Unit(null, "방정식과 함수", "일차함수"),
                new Unit(null, "방정식과 함수", "연립방정식"),
                new Unit(null, "방정식과 함수", "이차방정식"),
                new Unit(null, "방정식과 함수", "이차함수"),

                // 도형
                new Unit(null, "도형", "기본 도형"),
                new Unit(null, "도형", "도형의 이동"),
                new Unit(null, "도형", "도형의 성질"),
                new Unit(null, "도형", "도형의 작도와 합동"),
                new Unit(null, "도형", "삼각형과 사각형"),
                new Unit(null, "도형", "피타고라스 정리"),
                new Unit(null, "도형", "원의 방정식"),

                // 자료와 통계
                new Unit(null, "자료와 통계", "자료의 수집과 정리"),
                new Unit(null, "자료와 통계", "평균과 중앙값"),
                new Unit(null, "자료와 통계", "가능성"),
                new Unit(null, "자료와 통계", "자료의 표현과 해석"),
                new Unit(null, "자료와 통계", "확률"),
                new Unit(null, "자료와 통계", "통계의 이해와 활용")
        );

        unitRepository.saveAll(units);
        System.out.println("Unit 초기 데이터를 DB에 저장했습니다.");
    }
}
