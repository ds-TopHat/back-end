package com.mathfusion.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket-name}")
    private String bucketName;

    public Map<String, String> generateUploadAndDownloadUrls(String objectKey) {
        Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 50);

        // 업로드용 Presigned URL (PUT)
        GeneratePresignedUrlRequest uploadRequest = new GeneratePresignedUrlRequest(bucketName, objectKey)
                .withMethod(HttpMethod.PUT)
                .withExpiration(expiration);
        String uploadUrl = amazonS3.generatePresignedUrl(uploadRequest).toString();

        // 다운로드용 Presigned URL (GET)
        GeneratePresignedUrlRequest downloadRequest = new GeneratePresignedUrlRequest(bucketName, objectKey)
                .withMethod(HttpMethod.GET)
                .withExpiration(expiration);
        String downloadUrl = amazonS3.generatePresignedUrl(downloadRequest).toString();

        return Map.of(
                "uploadUrl", uploadUrl,
                "downloadUrl", downloadUrl
        );
    }

}