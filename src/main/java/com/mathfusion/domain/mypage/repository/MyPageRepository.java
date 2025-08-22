package com.mathfusion.domain.mypage.repository;

import com.mathfusion.domain.mypage.dto.MyPageResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.mathfusion.domain.unit.entity.QUnit.unit;
import static com.mathfusion.domain.question.entity.QQuestion.question;

@Repository
@RequiredArgsConstructor
public class MyPageRepository {
    private final JPAQueryFactory queryFactory;

    public List<MyPageResponse.Units> findUnitsByUserEmail(String email) {
        return queryFactory
                .select(Projections.fields(MyPageResponse.Units.class,
                        unit.id.as("id"),
                        unit.type.as("type")
                ))
                .from(question)
                .join(question.unit, unit)
                .where(question.user.email.eq(email))
                .groupBy(unit.id, unit.type)
                .orderBy(question.count().desc())
                .fetch();
    }
}
