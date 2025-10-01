package com.hotketok.externalApi;

import com.hotketok.dto.AddCommonBillDetailRequest;
import com.hotketok.dto.GetCommonBillResponse;
import com.hotketok.service.CommonBillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/commonbill-service")
@RequiredArgsConstructor
public class CommonBillController {

    private final CommonBillService commonBillService;

    // 집주인 - 내역 추가
    @PostMapping("/create")
    public ResponseEntity<Void> write(@RequestHeader("userId") Long ownerId,
                                      @RequestBody AddCommonBillDetailRequest request) {
        commonBillService.addCommonBillDetail(ownerId,request);
        return ResponseEntity.ok().build();
    }

    // 입주민/집주인 - 내역 조회
    @GetMapping("/view")
    public GetCommonBillResponse view(@RequestHeader("userId") Long ownerId,
                                      @RequestParam int year,
                                      @RequestParam int month) {
        return commonBillService.getCommonBills(ownerId, year, month);
    }
}

