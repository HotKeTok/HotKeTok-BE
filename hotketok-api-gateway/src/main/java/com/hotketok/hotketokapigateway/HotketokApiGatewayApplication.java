package com.hotketok.hotketokapigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class HotketokApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(HotketokApiGatewayApplication.class, args);
	}

}
