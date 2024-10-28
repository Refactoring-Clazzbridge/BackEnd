package com.example.academy.config;

import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

@Configuration
public class S3Config {

    @Value("${ncloud.storage.accessKey}")
    private String accessKey;

    @Value("${ncloud.storage.secretKey}")
    private String secretKey;

    @Value("${ncloud.storage.endPoint}")
    private String endPoint;

    @Value("${ncloud.storage.regionName}")
    private String regionName;

    @Bean
    public S3Client s3Client() {
        // S3Client 구성
        return S3Client.builder()
            .region(Region.of(regionName))
            .credentialsProvider(StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)))
            .endpointOverride(URI.create(endPoint))  // endPoint를 설정 파일에서 주입
            .serviceConfiguration(S3Configuration.builder().build())
            .build();
    }
}