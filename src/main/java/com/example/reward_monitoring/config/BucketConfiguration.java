package com.example.reward_monitoring.config;

import io.github.bucket4j.Bandwidth;
import org.springframework.context.annotation.Configuration;
import io.github.bucket4j.Refill;
import io.github.bucket4j.Bucket;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class BucketConfiguration {
    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    public Bucket getBucket(String userId) {
        return buckets.computeIfAbsent(userId, id -> {
            Refill refill = Refill.intervally(10, Duration.ofSeconds(10));
            Bandwidth limit = Bandwidth.classic(10000, refill);
            return Bucket.builder()
                    .addLimit(limit)
                    .build();
        });
    }

}
