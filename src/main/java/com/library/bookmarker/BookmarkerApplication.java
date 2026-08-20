package com.library.bookmarker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BookmarkerApplication {

	private static final Logger logger = LoggerFactory.getLogger(BookmarkerApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(BookmarkerApplication.class, args);
	}

	@Bean
	CommandLineRunner init() {
		return (args) -> {
			logger.info("북마커 서버 실행 완료");
		};
	}
}
