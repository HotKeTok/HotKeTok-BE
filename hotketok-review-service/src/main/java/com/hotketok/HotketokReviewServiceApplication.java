package com.hotketok;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.hotketok.internalApi")
public class HotketokReviewServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(HotketokReviewServiceApplication.class, args);
	}

}
