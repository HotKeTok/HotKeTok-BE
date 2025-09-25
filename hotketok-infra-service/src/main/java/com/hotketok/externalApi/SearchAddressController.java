package com.hotketok.externalApi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/infra-service")
public class SearchAddressController {
    @Value("${searchApi.secretKey}")
    private String confmKey;

    @GetMapping("/getAddress")
    public ResponseEntity<List<Map<String, String>>> getAddrApi(
            @RequestParam String currentPage,
            @RequestParam String countPerPage,
            @RequestParam String resultType,
            @RequestParam String keyword) {

        try {
            String apiUrl = UriComponentsBuilder
                    .fromHttpUrl("https://business.juso.go.kr/addrlink/addrLinkApi.do")
                    .queryParam("currentPage", currentPage)
                    .queryParam("countPerPage", countPerPage)
                    .queryParam("confmKey", confmKey)
                    .queryParam("keyword", keyword)
                    .queryParam("resultType", resultType)
                    .build()
                    .toUriString();

            RestTemplate restTemplate = new RestTemplate();
            String responseBody = restTemplate.getForObject(apiUrl, String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode jusoArray = root.path("results").path("juso");

            List<Map<String, String>> resultList = new ArrayList<>();

            if (jusoArray.isArray()) {
                for (JsonNode juso : jusoArray) {
                    Map<String, String> addr = new HashMap<>();
                    addr.put("roadAddr", juso.path("roadAddr").asText());
                    addr.put("jibunAddr", juso.path("jibunAddr").asText());
                    resultList.add(addr);
                }
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(resultList);
        } catch (Exception e) {
            throw new RuntimeException("외부 API 호출 실패");
        }
    }

}
