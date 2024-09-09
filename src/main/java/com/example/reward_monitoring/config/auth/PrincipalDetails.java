package com.example.reward_monitoring.config.auth;

import com.example.reward_monitoring.general.member.entity.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class PrincipalDetails implements UserDetails {
    private Member member;
    private Map<String,Object> attributes;

    public PrincipalDetails(Member member){this.member = member;}
    public PrincipalDetails(Member member, Map<String,Object> attributes){
        this.member=member;
        this.attributes = attributes;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PrincipalDetails) {
            return this.member.getId().equals(((PrincipalDetails) obj).getUsername());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.member.getId().hashCode();
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collect = new ArrayList<>();
        collect.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return member.getRole();
            }
        });

        return collect;
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getId();
    }
    public String getName() {
        return member.getName();
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
