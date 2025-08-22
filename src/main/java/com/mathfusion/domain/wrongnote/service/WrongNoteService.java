package com.mathfusion.domain.wrongnote.service;

import com.mathfusion.domain.question.entity.Question;
import com.mathfusion.domain.wrongnote.dto.WrongNoteResponse;
import com.mathfusion.domain.wrongnote.repository.WrongNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WrongNoteService {

    private final WrongNoteRepository wrongNoteRepository;

    @Value("${cloud.aws.s3.bucket-name}")
    private String bucketName;

    @Value("${cloud.aws.region.static}")
    private String region;

    private String getS3BaseUrl() {
        return String.format("https://%s.s3.%s.amazonaws.com/", bucketName, region);
    }

    public List<WrongNoteResponse.WrongNoteListResponse> getWrongNotes(String email) {
        return wrongNoteRepository.findWrongNotesByUserEmail(email, getS3BaseUrl());
    }

    public Optional<WrongNoteResponse.WrongNoteDetailResponse> getWrongNoteDetail(Long questionId, String email) {
        Optional<Question> questionOpt = wrongNoteRepository.findByIdAndUserEmail(questionId, email);

        return questionOpt.map(q -> WrongNoteResponse.WrongNoteDetailResponse.builder()
                .questionId(q.getId())
                .createdAt(q.getCreatedAt())
                .problemImageUrl(getS3BaseUrl() + q.getProblemImage())
                .unitId(q.getUnit().getId())
                .unitType(q.getUnit().getType())
                .aiAnswer(q.getAiAnswer())
                .build());
    }
}
