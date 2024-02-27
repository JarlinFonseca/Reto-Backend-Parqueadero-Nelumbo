package com.nelumbo.parqueadero;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class ParqueaderoServiceApplication{
	public static void main(String[] args) {
		SpringApplication.run(ParqueaderoServiceApplication.class, args);
	}
}
