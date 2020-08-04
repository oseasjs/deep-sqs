package com.deep.sqs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class DeepSqsApplication {

	public static void main(String[] args) {
		SpringApplication.run(DeepSqsApplication.class, args);
	}

}
