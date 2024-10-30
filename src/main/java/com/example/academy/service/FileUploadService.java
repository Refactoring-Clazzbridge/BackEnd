package com.example.academy.service;

import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.core.sync.RequestBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class FileUploadService {

    private final S3Client s3Client;

    @Value("${ncloud.storage.endPoint}")
    private String endPoint;

    @Value("${ncloud.storage.bucketName}")
    private String bucketName;

    public FileUploadService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String uploadFile(MultipartFile file) throws IOException {
        // UUID + 원래 파일 이름으로 S3에 저장될 이름 생성
        String storageFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        // MIME 타입 가져오기
        String contentType = file.getContentType();

        // S3에 파일 업로드
        PutObjectRequest putRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(storageFileName)
            .contentType(contentType) // MIME 타입 설정
            .acl(ObjectCannedACL.PUBLIC_READ) // 공개 읽기 설정 (필요 시)
            .build();

        PutObjectResponse putResponse = s3Client.putObject(putRequest,
            RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        System.out.println("Uploaded file ETag: " + putResponse.eTag());
        return String.format("%s/%s/%s", endPoint, bucketName, storageFileName);
    }

    public void deleteFile(String fileName) {
        try {
            // S3에 삭제 요청
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

            // 파일 삭제 처리
            DeleteObjectResponse deleteResponse = s3Client.deleteObject(deleteRequest);

            String message = String.format("# 파일 삭제: '%s' 삭제 코드: %s",
                fileName,
                deleteResponse.sdkHttpResponse().statusCode());

            System.out.println(message);

        } catch (SdkClientException e) {
            // SDK 클라이언트 예외 처리
            e.printStackTrace();
        }
    }
}