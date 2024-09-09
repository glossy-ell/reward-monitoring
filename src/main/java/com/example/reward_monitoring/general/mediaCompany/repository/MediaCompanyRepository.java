package com.example.reward_monitoring.general.mediaCompany.repository;

import com.example.reward_monitoring.general.mediaCompany.entity.MediaCompany;
import com.example.reward_monitoring.general.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MediaCompanyRepository extends JpaRepository<MediaCompany,Integer> {

    public MediaCompany findByIdx(int idx);

}
