package com.hotketok.externalApi;

import com.hotketok.dto.*;
import com.hotketok.service.RequestFormService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/requestform-service")
@RequiredArgsConstructor
public class RequestFormController {

    private final RequestFormService requestFormService;
    // 요청서 작성
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    CreateRequestFormResponse createRequestForm(
            @RequestHeader("userId") Long userId,
            @RequestPart(value = "data") CreateRequestFormRequest createRequestFormRequest,
            @RequestPart(value = "images") List<MultipartFile> images){

        return requestFormService.createRequestForm(createRequestFormRequest,images,userId);
    }
    // 요청서 도우미 (사진 인식으로 설명 작성 도우미)
    @PostMapping(value = "/gpt-service", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ChatGPTResponse helpDescriptionByGPT(
            @RequestPart(value = "images") List<MultipartFile> images
    ) throws Exception {
       return requestFormService.helpDescriptionByGPT(images);
    }
    // 수리 요청서 정보 확인
    @GetMapping(value = "/requestform-info/{requestformId}")
    RequestFormInfoResponse getRequestFormInfo(@PathVariable("requestformId") Long requestformId){
        return requestFormService.getRequestFormInfo(requestformId);
    }


}
