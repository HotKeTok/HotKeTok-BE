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
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Configuration
@ConditionalOnProperty(name = "cloud.provider", havingValue = "aws")
public class AwsS3Config {

    @Value("${aws.s3.bucket-name}")
    private String bucketName;
    @Value("${aws.s3.region}")
    private String region;
    @Value("${aws.s3.credentials.accessKey}")
    private String accessKey;
    @Value("${aws.s3.credentials.secretKey}")
    private String secretKey;

    @Bean
    public S3Client s3Client() {
        // application.yml에서 읽어온 키로 S3Client를 빌드
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        return S3Client.builder().region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    @Bean
    public ImageStorageService awsImageStorageService(S3Client s3Client) {
        return new ImageStorageService() {
            @Override
            public String uploadImage(MultipartFile file, String folderName) throws IOException {
                String fileName = folderName+UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
                try {
                    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                            .bucket(bucketName).key(fileName).contentType(file.getContentType()).build();
                    s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
                    log.info("upload image success");
                    return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, fileName);
                } catch (IOException e) {
                    throw new CustomException(ImageErrorCode.UPLOAD_FAILED);
                }
            }

            @Override
            public void deleteImage(String fileUrl) {
                String objectKey = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
                DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                        .bucket(bucketName).key(objectKey).build();
                s3Client.deleteObject(deleteObjectRequest);
            }
        };
    }
}
