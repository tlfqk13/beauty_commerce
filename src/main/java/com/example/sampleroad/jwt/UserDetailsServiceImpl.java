package com.example.sampleroad.jwt;

import com.example.sampleroad.common.exception.ErrorCode;
import com.example.sampleroad.common.exception.ErrorCustomException;
import com.example.sampleroad.domain.member.Member;
import com.example.sampleroad.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String userPk) throws ErrorCustomException {
        Member member = memberRepository.findById(Long.parseLong(userPk))
                .orElseThrow(() -> new ErrorCustomException(ErrorCode.NO_USER_ERROR));
        return new UserDetailsImpl(member);
    }
}