package com.example.reward_monitoring.general.advertiser.repository;

import com.example.reward_monitoring.general.advertiser.entity.Advertiser;

import com.example.reward_monitoring.general.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdvertiserRepository extends JpaRepository<Advertiser,Integer> {

    public Advertiser findByIdx(int idx);

}
