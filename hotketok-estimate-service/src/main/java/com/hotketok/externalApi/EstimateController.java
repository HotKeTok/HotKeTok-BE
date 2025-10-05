package com.hotketok.externalApi;

import com.hotketok.dto.PostEstimateRequest;
import com.hotketok.dto.PostEstimateResponse;
import com.hotketok.service.EstimateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/estimate-service")
@RequiredArgsConstructor
public class EstimateController {

    private final EstimateService estimateService;

    // 견적서 작성
    @PostMapping
    public PostEstimateResponse postEstimate(@RequestBody PostEstimateRequest request) {
        Long userId = 101L;
        return estimateService.postEstimate(userId, request);
    }
}
