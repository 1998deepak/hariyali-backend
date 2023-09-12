package com.hariyali.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class ApplicationConfig {

    @Value("${request.rate.limit}")
    Integer noOfRequestAllowed;

    @Value("${request.rate.noOfMinute}")
    Integer noOfMinute;

    @Bean
    public Bucket createBucket() {
        Bandwidth limit = Bandwidth.classic(noOfRequestAllowed, Refill.greedy(noOfRequestAllowed, Duration.ofMinutes(noOfMinute)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

}
