package com.hotketok;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class HotketokCommonbillServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(HotketokCommonbillServiceApplication.class, args);
	}

}
