package com.mathfusion.domain.wrongnote.controller;

import com.mathfusion.domain.wrongnote.dto.PdfRequest;
import com.mathfusion.domain.wrongnote.dto.WrongNoteResponse;
import com.mathfusion.domain.wrongnote.service.PdfGeneratorService;
import com.mathfusion.domain.wrongnote.service.WrongNoteService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0/wrong-notes")
public class WrongNoteController {

    private final WrongNoteService wrongNoteService;
    private final PdfGeneratorService pdfGeneratorService;

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

    @Operation(summary = "오답노트 PDF 생성",
            description = "/api/v0/wrong-notes에서 받은 problemImageUrl들을 모아서 배열 안에 넣고 보내주세요.\n\n" +
            "예시:\n" +
            "```json\n" +
            "{\n" +
            "  \"problemImageUrls\": [\n" +
            "    \"https://tophat2025.s3.ap-northeast-2.amazonaws.com/uploads/d5a02100-9e33-481c-87d8-f488b77f2424.png\",\n" +
            "    \"https://tophat2025.s3.ap-northeast-2.amazonaws.com/uploads/d5a02100-9e33-481c-87d8-f488b77f2424.png\",\n" +
            "    \"https://tophat2025.s3.ap-northeast-2.amazonaws.com/uploads/d5a02100-9e33-481c-87d8-f488b77f2424.png\",\n" +
            "    \"https://tophat2025.s3.ap-northeast-2.amazonaws.com/uploads/d5a02100-9e33-481c-87d8-f488b77f2424.png\",\n" +
            "    \"https://tophat2025.s3.ap-northeast-2.amazonaws.com/uploads/d5a02100-9e33-481c-87d8-f488b77f2424.png\"\n" +
            "}\n" + "```\n\n" )
    @PostMapping("/pdf")
    public ResponseEntity<Resource> generatePdf(@RequestBody PdfRequest pdfRequest) throws Exception {
        List<String> imageUrls = pdfRequest.getProblemImageUrls();

        // PDF를 임시 파일로 생성
        File pdfFile = pdfGeneratorService.generatePdfFromUrls(imageUrls);

        // InputStreamResource로 변환
        InputStreamResource resource = new InputStreamResource(new FileInputStream(pdfFile));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=exam.pdf")
                .contentLength(pdfFile.length())
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
}
