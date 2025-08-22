package com.mathfusion.domain.wrongnote.repository;

import com.mathfusion.domain.wrongnote.dto.WrongNoteResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.mathfusion.domain.question.entity.QQuestion.question;
import static com.mathfusion.domain.unit.entity.QUnit.unit;
import static com.mathfusion.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class WrongNoteRepository {
    private final JPAQueryFactory queryFactory;

    @Value("${cloud.aws.s3.bucket-name}")
    private String bucketName;

    @Value("${cloud.aws.region.static}")
    private String region;

    public List<WrongNoteResponse> findWrongNotesByUserEmail(String email) {

        String s3BaseUrl = String.format("https://%s.s3.%s.amazonaws.com/", bucketName, region);

        return queryFactory
                .select(question.id, question.problemImage, unit.type)
                .from(question)
                .join(question.user, user)
                .join(question.unit, unit)
                .where(user.email.eq(email),
                        question.problemImage.isNotNull()
                                .and(question.problemImage.ne("")))
                .orderBy(question.createdAt.desc())
                .fetch()
                .stream()
                .map(tuple -> WrongNoteResponse.builder()
                        .questionId(tuple.get(question.id))
                        .problemImageUrl(s3BaseUrl + tuple.get(question.problemImage))
                        .unitType(tuple.get(unit.type))
                        .build())
                .toList();
    }
}
