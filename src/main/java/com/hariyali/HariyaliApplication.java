package com.hariyali;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class HariyaliApplication {

	public static void main(String[] args) {
		SpringApplication.run(HariyaliApplication.class, args);
	}

	
	@Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
