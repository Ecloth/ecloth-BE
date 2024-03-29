package com.ecloth.beta.security.memberDetail;

import com.ecloth.beta.domain.member.entity.Member;
import com.ecloth.beta.domain.member.model.MemberStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class MemberDetails implements UserDetails {

    private final Member member;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(member.getMemberRole().name()));
        return authorities;
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return String.valueOf(member.getMemberId());
    }

    @Override
    // 계정이 만료되지 않았는지 리턴
    public boolean isAccountNonExpired() {
        return MemberStatus.UNVERIFIED.equals(member.getMemberStatus()) ||
                MemberStatus.ACTIVE.equals(member.getMemberStatus());
    }

    @Override
    // 계정이 잠겨있지 않은지 리턴
    public boolean isAccountNonLocked() {
        return MemberStatus.SUSPENDED.equals(member.getMemberStatus()) ||
                MemberStatus.INACTIVE.equals(member.getMemberStatus())
                        == false;
    }

    @Override
    // 계정의 패스워드가 만료되지 않았는지 리턴
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    // 사용가능 계정인지 리턴
    public boolean isEnabled() {
        return MemberStatus.ACTIVE.equals(member.getMemberStatus()) ||
                MemberStatus.UNVERIFIED.equals(member.getMemberStatus());
    }

    public Long getMemberId(){
        return Long.valueOf(getUsername());
    }

    public String getRole() {
        return getAuthorities().toString();
    }

}
