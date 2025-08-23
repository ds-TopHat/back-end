package com.mathfusion.domain.wrongnote.repository;

import com.mathfusion.domain.question.entity.Question;
import com.mathfusion.domain.wrongnote.dto.WrongNoteResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.mathfusion.domain.question.entity.QQuestion.question;
import static com.mathfusion.domain.unit.entity.QUnit.unit;
import static com.mathfusion.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class WrongNoteRepository {
    private final JPAQueryFactory queryFactory;

    public List<WrongNoteResponse.WrongNoteListResponse> findWrongNotesByUserEmail(String email, String s3BaseUrl) {

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
                .map(tuple -> WrongNoteResponse.WrongNoteListResponse.builder()
                        .questionId(tuple.get(question.id))
                        .problemImageUrl(s3BaseUrl + tuple.get(question.problemImage))
                        .unitType(tuple.get(unit.type))
                        .build())
                .toList();
    }

    public Optional<Question> findByIdAndUserEmail(Long questionId, String email) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(question)
                        .join(question.user, user)
                        .join(question.unit, unit).fetchJoin()
                        .where(
                                question.id.eq(questionId),
                                user.email.eq(email),
                                question.problemImage.isNotNull()
                        )
                        .fetchOne()
        );
    }
}
