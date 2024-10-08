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
            Refill refill = Refill.intervally(1, Duration.ofSeconds(100));
            Bandwidth limit = Bandwidth.classic(2, refill);
            return Bucket.builder()
                    .addLimit(limit)
                    .build();
        });
    }

}
