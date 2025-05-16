package com.mathfusion.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class UploadRelayService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${postimg.api-key}")
    private String apiKey;

    public String uploadToPostimg(String presignedUrl) {
        try {
            System.out.println("UploadRelayService 진입: presignedUrl = " + presignedUrl);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("key", apiKey);  // Postimages에서 발급받은 public API key
            body.add("upload_url", presignedUrl);  // S3 Presigned URL 직접 업로드

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "https://api.postimages.org/1/upload",
                    request,
                    Map.class
            );

            System.out.println("postimages 응답 수신: " + response.getBody());

            Map<String, Object> responseBody = response.getBody();
            if (responseBody == null || !responseBody.containsKey("url")) {
                throw new RuntimeException("postimages 응답 없음 또는 형식 오류");
            }

            String url = (String) responseBody.get("url");
            if (url == null || url.isBlank()) {
                throw new RuntimeException("postimages URL이 비어 있음");
            }

            System.out.println("최종 반환할 URL = " + url);
            return url;

        } catch (Exception e) {
            System.out.println("UploadRelayService 예외 발생: " + e.getMessage());
            e.printStackTrace();  // 전체 예외 로그 출력
            throw new RuntimeException("postimages 업로드 실패", e);
        }
    }
}

