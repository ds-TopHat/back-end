package com.mathfusion.domain.qwen;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashscope")
public class QwenController {

    private final QwenService qwenService;

    public QwenController(QwenService qwenService) {
        this.qwenService = qwenService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeImage(@RequestBody Map<String, String> request) {
        String imageUrl = request.get("imageUrl");
        try {
            String json = qwenService.runMultiModalExample(imageUrl);
            return ResponseEntity.ok().body(json);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
