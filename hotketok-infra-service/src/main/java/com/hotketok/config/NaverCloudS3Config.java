package com.hotketok.config;

import com.hotketok.exception.ImageErrorCode;
import com.hotketok.hotketokcommonservice.error.exception.CustomException;
import com.hotketok.service.ImageStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.S3Configuration;
import java.io.IOException;
import java.net.URI;
import java.util.UUID;

@Slf4j
@Configuration
@ConditionalOnProperty(name = "cloud.provider", havingValue = "ncloud")
public class NaverCloudS3Config {

    @Value("${ncloud.s3.bucket-name}")
    private String bucketName;

    @Value("${ncloud.s3.region}")
    private String region;

    @Value("${ncloud.s3.credentials.accessKey}")
    private String accessKey;

    @Value("${ncloud.s3.credentials.secretKey}")
    private String secretKey;

    @Value("${ncloud.s3.endpoint}")
    private String endpoint; // 예: https://kr.object.ncloudstorage.com

    @Bean
    public S3Client naverS3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        // AWS SDK를 그대로 사용하되, Naver Cloud 전용 endpoint 설정
        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true) // Naver Cloud는 path-style 권장
                        .build())
                .build();
    }

    @Bean
    public ImageStorageService naverImageStorageService(S3Client naverS3Client) {
        return new ImageStorageService() {
            @Override
            public String uploadImage(MultipartFile file, String folderName) throws IOException {
                String fileName = folderName + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

                try {
                    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(fileName)
                            .contentType(file.getContentType())
                            .acl("public-read")
                            .build();

                    naverS3Client.putObject(
                            putObjectRequest,
                            software.amazon.awssdk.core.sync.RequestBody.fromInputStream(
                                    file.getInputStream(),
                                    file.getSize()
                            )
                    );

                    // 혹시 이미 업로드된 객체의 ACL을 명시적으로 PublicRead로 변경
                    setPublicReadAcl(naverS3Client, bucketName, fileName);

                    log.info("[NaverCloud] upload success: {}", fileName);
                    return String.format("%s/%s/%s", endpoint, bucketName, fileName);
                } catch (IOException e) {
                    log.error("[NaverCloud] upload failed", e);
                    throw new CustomException(ImageErrorCode.UPLOAD_FAILED);
                }
            }

            @Override
            public void deleteImage(String fileUrl) {
                try {
                    // URL → Object Key 추출
                    String objectKey = fileUrl.substring(fileUrl.indexOf(bucketName) + bucketName.length() + 1);
                    DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                            .bucket(bucketName)
                            .key(objectKey)
                            .build();
                    naverS3Client.deleteObject(deleteObjectRequest);
                    log.info("[NaverCloud] delete success: {}", objectKey);
                } catch (Exception e) {
                    log.error("[NaverCloud] delete failed", e);
                    throw new CustomException(ImageErrorCode.DELETE_FAILED);
                }
            }

            // 객체에 Public Read 권한 부여
            private void setPublicReadAcl(S3Client s3Client, String bucket, String key) {
                try {
                    s3Client.putObjectAcl(b -> b
                            .bucket(bucket)
                            .key(key)
                            .acl("public-read")
                    );
                    log.info("[NaverCloud] setPublicReadAcl success for {}", key);
                } catch (Exception e) {
                    log.error("[NaverCloud] setPublicReadAcl failed", e);
                }
            }
        };
    }
}
