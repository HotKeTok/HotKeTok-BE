package com.hotketok.externalApi;

import com.hotketok.dto.TestResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/requestform-service")
public class TestController {
    @GetMapping("/test")
    public TestResponse test() {
        return new TestResponse("jerry");
    }
}
