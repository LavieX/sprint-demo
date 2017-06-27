package com.ca.atlas.sample;

import java.util.logging.Logger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Application {

	private static Logger LOGGER = Logger.getLogger(Application.class.getName());

	@RequestMapping("/")
	public String home() {
		LOGGER.info("Serving root");
		return "Hello, Big World!";
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
