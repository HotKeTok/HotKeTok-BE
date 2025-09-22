package com.hotketok.internalApi;

import com.hotketok.config.OpenAiFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(
        name = "openAiClient",
        url = "${openai.api.url}",
        configuration = OpenAiFeignConfig.class
)
public interface OpenAiClient {

    @PostMapping(value = "/v1/responses", consumes = MediaType.APPLICATION_JSON_VALUE)
    String createResponse(@RequestBody Map<String, Object> payload);
}
