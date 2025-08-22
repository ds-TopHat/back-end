package com.mathfusion.domain.wrongnote.controller;

import com.mathfusion.domain.wrongnote.dto.WrongNoteResponse;
import com.mathfusion.domain.wrongnote.service.WrongNoteService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0/wrong-notes")
public class WrongNoteController {

    private final WrongNoteService wrongNoteService;

    @Operation(summary = "오답노트 리스트 조회", description = "질문했던 내역이 최신순으로 정렬되어 있습니다.")
    @GetMapping
    public ResponseEntity<List<WrongNoteResponse.WrongNoteListResponse>> getWrongNotes(
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername(); // UserDetails 기본적으로 username이 email
        List<WrongNoteResponse.WrongNoteListResponse> response = wrongNoteService.getWrongNotes(email);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "오답노트 상세 조회")
    @GetMapping("/{questionId}")
    public ResponseEntity<WrongNoteResponse.WrongNoteDetailResponse> getWrongNoteDetail(
            @PathVariable Long questionId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();

        return wrongNoteService.getWrongNoteDetail(questionId, email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
