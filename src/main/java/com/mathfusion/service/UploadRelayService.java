package com.mathfusion.service;

import com.amazonaws.util.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.net.URL;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@Service
public class UploadRelayService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.upload-preset}")
    private String uploadPreset; // 미리 생성한 unsigned preset

    public String uploadToCloudinary(String presignedUrl) {
        try {
            // 1. S3에서 이미지 다운로드 → base64로 변환
            InputStream in = new URL(presignedUrl).openStream();
            byte[] imageBytes = IOUtils.toByteArray(in);
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            in.close();

            //Cloudinary 업로드용 랜덤 파일 이름 생성
            String filename = "qwen_" + UUID.randomUUID().toString().replace("-", "");

            // 3. HTTP 요청 구성
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", "data:image/png;base64," + base64Image);
            body.add("upload_preset", uploadPreset);
            body.add("public_id", filename);  // 슬래시 없는 이름 강제 지정

            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "https://api.cloudinary.com/v1_1/" + cloudName + "/image/upload",
                    request,
                    Map.class
            );

            Map<String, Object> responseBody = response.getBody();
            if (responseBody == null || !responseBody.containsKey("secure_url")) {
                throw new RuntimeException("Cloudinary 응답 오류");
            }

            return (String) responseBody.get("secure_url");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Cloudinary 업로드 실패", e);
        }
    }
}

