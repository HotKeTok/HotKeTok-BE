package com.hotketok.externalApi;

import com.hotketok.dto.EstimateResponse;
import com.hotketok.dto.PostEstimateRequest;
import com.hotketok.dto.PostEstimateResponse;
import com.hotketok.service.EstimateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    // 받은 견적서 조회
    @GetMapping("/list")
    public List<EstimateResponse> getEstimatesByRequestFormId(@RequestParam Long requestFormId) {
        return estimateService.getEstimatesByRequestFormId(requestFormId);
    }
}
