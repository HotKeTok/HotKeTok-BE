package com.hotketok.hotketokeurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class HotketokEurekaServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(HotketokEurekaServerApplication.class, args);
	}

}
