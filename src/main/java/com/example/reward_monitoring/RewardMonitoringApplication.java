package com.example.reward_monitoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class RewardMonitoringApplication {
	public static void main(String[] args) {
		SpringApplication.run(RewardMonitoringApplication.class, args);
	}

}
