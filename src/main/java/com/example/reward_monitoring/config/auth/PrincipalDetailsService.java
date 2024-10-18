package com.example.reward_monitoring.config.auth;



import com.example.reward_monitoring.general.member.entity.Member;
import com.example.reward_monitoring.general.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;



@Service
@Slf4j
public class PrincipalDetailsService implements UserDetailsService {

    @Autowired
    private MemberRepository memberRepository;



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Member memberEntity = memberRepository.findById(username);
        if (memberEntity != null) {
            if (!memberEntity.getId().equals(username)) {//  대소문자 구분
                throw new UsernameNotFoundException("Invalid username");
            }
            return new PrincipalDetails(memberEntity);
        }
        return null;
    }

}
