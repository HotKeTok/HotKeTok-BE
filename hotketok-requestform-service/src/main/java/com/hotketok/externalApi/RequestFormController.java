package com.hotketok.externalApi;

import com.hotketok.dto.CreateRequestFormRequest;
import com.hotketok.service.RequestFormService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/requestform-service/")
@RequiredArgsConstructor
public class RequestFormController {

    private final RequestFormService requestFormService;

    @PostMapping("/post")
    ResponseEntity<String> postRequestForm(
            @RequestBody CreateRequestFormRequest createRequestFormRequest
            /*List<MultipartFile> multipartFiles*/){

        requestFormService.post(createRequestFormRequest,1);
        return ResponseEntity.ok("요청이 전송되었습니다.");
    }


}
