package com.nelumbo.parqueadero;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.security.crypto.password.PasswordEncoder;


@EnableFeignClients
@SpringBootApplication
public class ParqueaderoServiceApplication implements CommandLineRunner {

	@Autowired
	private PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(ParqueaderoServiceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		String password = "admin";
		System.out.println(passwordEncoder.encode(password));
	}
}
