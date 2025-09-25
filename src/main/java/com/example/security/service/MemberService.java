package com.example.security.service;

import com.example.security.entity.Member;
import com.example.security.entity.Role;
import com.example.security.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private  final RoleService roleService;

    public Member register(Member member){
        // 1. 사용자가 입력한 패스워드를 암호화
        String pwd=passwordEncoder.encode(member.getPassword());
        member.setPassword(pwd);
        // 2. 회원의 기본 권한(USER, MANAGER, ADMIN)을 저장한다.
        Role userRole=roleService.findByName("USER");
        Set<Role> roles=new HashSet<>();
        roles.add(userRole);
        member.setRoles(roles);
        return memberRepository.save(member);
    }

    public Member findByUsername(String username){
        Optional<Member> optional =memberRepository.findByUsername(username);
        if(!optional.isPresent()){
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return optional.get();
    }
}
