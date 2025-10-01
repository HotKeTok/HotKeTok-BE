package com.hotketok;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class HotketokNoticeServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(HotketokNoticeServiceApplication.class, args);
	}

}
