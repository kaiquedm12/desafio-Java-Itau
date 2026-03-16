package com.kaique.transacao_api;

import java.time.Clock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = "com.kaique")
public class TransacaoApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransacaoApiApplication.class, args);
	}

	@Bean
	Clock clock() {
		return Clock.systemUTC();
	}

}
