package com.hotketok.service;

import com.hotketok.constant.GPTPrompt;
import com.hotketok.dto.CreateRequestFormResponse;
import com.hotketok.parser.OpenAIResponseParser;
import com.hotketok.domain.RequestForm;
import com.hotketok.domain.RequestFormImage;
import com.hotketok.domain.enums.Status;
import com.hotketok.dto.ChatGPTResponse;
import com.hotketok.dto.CreateRequestFormRequest;
import com.hotketok.dto.GetUserInfoResponse;
import com.hotketok.dto.internalApi.UploadImageResponse;
import com.hotketok.internalApi.InfraServiceClient;
import com.hotketok.internalApi.OpenAiClient;
import com.hotketok.repository.RequestFormImageRepository;
import com.hotketok.repository.RequestFormRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestFormService {
    private final RequestFormRepository requestFormRepository;
    private final RequestFormImageRepository requestFormImageRepository;
    private final InfraServiceClient infraServiceClient;
    private final OpenAiClient openAiClient;

    @Value("${openai.model}")
    private String model;

    @Transactional
    public CreateRequestFormResponse createRequestForm(
            CreateRequestFormRequest createRequestFormRequest,
            List<MultipartFile> images,
            Long userId) {

        // 사용자 서비스에서 주택주소, authorId, payerId 가져오는 서비스 로직
        // GetUserInfoResponse getUserInfoResponse = memberClient.getUserInfoByPayType(userId,createRequestFormRequest.payType());

        GetUserInfoResponse getUserInfoResponse = new GetUserInfoResponse(userId, 2L, "동작 핫케톡 스테이 304호");

        RequestForm requestForm = RequestForm.createRequestForm(
                getUserInfoResponse.userId(),
                getUserInfoResponse.proprietorId(),
                createRequestFormRequest.payType(),
                createRequestFormRequest.description(),
                createRequestFormRequest.requestSchedule(),
                createRequestFormRequest.category(),
                Status.CHOOSING
        );

        boolean isImageSaved = false;
        List<Long> imageIds = new ArrayList<>();
        // List<String> imageUrls = new ArrayList<>(); saga 패턴으로 인해 추후 이미지 전체 삭제 API 개발 시 추가

        try {
            UploadImageResponse uploadImageResponse = infraServiceClient.uploadImages(images, "requestform-image/");

            List<RequestFormImage> imageList = uploadImageResponse.imageList().stream().map(image ->
                    RequestFormImage.createRequestFormImage(requestForm, image)
            ).collect(Collectors.toList());

            requestFormImageRepository.saveAll(imageList);
            imageIds = imageList.stream().map(image -> image.getId()).collect(Collectors.toList());
            // imageUrls = imageList.stream().map(image ->image.getImageUrl()).collect(Collectors.toList());
            isImageSaved = true;

            requestFormRepository.save(requestForm);
            return new CreateRequestFormResponse(requestForm.getId());
        } catch (Exception e) {
            if (isImageSaved) { // 이미지가 저장된 경우 보상 트랜잭션으로 DB 이미지 데이터 삭제
                this.requestFormImageRepository.deleteAllByIdIn(imageIds);
                // this.infraServiceClient.deleteAll(imageUrls);
            }
            return null;
        }
    }

    public ChatGPTResponse helpDescriptionByGPT(List<MultipartFile> images) throws Exception {
        UploadImageResponse uploadImageResponse = infraServiceClient.uploadImages(images, "requestform-ai/");

        List<String> imageList = uploadImageResponse.imageList();
        String prompt = GPTPrompt.CONTENT;


        List<Map<String, Object>> contents = new ArrayList<>();
        // OpenAI Responses payload를 Map으로  구성
        Map<String, Object> textPart = Map.of(
                "type", "input_text",
                "text", prompt
        );
        contents.add(textPart);

        // 이미지 URL 여러 개 추가
        for (String image : imageList) {
            contents.add(Map.of(
                    "type", "input_image",
                    "image_url", image
            ));
        }

        Map<String, Object> inputItem = new HashMap<>();
        inputItem.put("role", "user");
        inputItem.put("content", contents);

        Map<String, Object> payload = new HashMap<>();
        payload.put("model", model);
        payload.put("input", List.of(inputItem));

        // 호출 (에러바디 꼭 로그)
        String json = null;
        try {
            json = openAiClient.createResponse(payload);
        } catch (FeignException e) {
            System.err.println("OpenAI Status: " + e.status());
            System.err.println("OpenAI Body  : " + e.contentUTF8());
            throw e;
        }

        // 응답에서 text만 뽑아서 반환
        String textOnly = OpenAIResponseParser.parse(json).text();

        return new ChatGPTResponse(textOnly);
    }
}
