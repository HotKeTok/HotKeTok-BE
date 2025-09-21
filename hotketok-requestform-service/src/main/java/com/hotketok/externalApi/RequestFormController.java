package com.hotketok.externalApi;

import com.hotketok.dto.CreateRequestFormRequest;
import com.hotketok.service.RequestFormService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/requestform-service/")
@RequiredArgsConstructor
public class RequestFormController {

    private final RequestFormService requestFormService;

    @PostMapping(value = "/post", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<String> postRequestForm(
            @RequestPart(value = "data") CreateRequestFormRequest createRequestFormRequest,
            @RequestPart(value = "images") List<MultipartFile> images){

        requestFormService.post(createRequestFormRequest,images,1);
        return ResponseEntity.ok("요청이 전송되었습니다.");
    }


}
