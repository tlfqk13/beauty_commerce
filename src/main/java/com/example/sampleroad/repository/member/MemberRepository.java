package com.example.sampleroad.repository.member;

import com.example.sampleroad.domain.member.Member;
import com.example.sampleroad.domain.member.RegisterType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
    Optional<Member> findByMemberLoginIdAndWithdrawal(String memberLoginId, boolean withdrawal);

    Optional<Member> findByCiAndWithdrawal(String ci, boolean withdrawal);

    Optional<Member> findByMobileNoAndMemberNameAndWithdrawal(String memberNo, String memberName, boolean withdrawal);

    Optional<Member> findByMobileNoAndMemberNameAndRegisterTypeAndWithdrawal(String mobileNo, String memberName, RegisterType registerType, boolean withdrawal);

    Optional<Member> findByMobileNoAndMemberLoginIdAndWithdrawal(String memberNo, String memberLoginId, boolean withdrawal);

    Optional<Member> findByNicknameAndWithdrawal(String nickname, boolean withdrawal);

    Optional<Member> findByEmailAndWithdrawal(String email, boolean withdrawal);
    Optional<Member> findFirstByEmailAndWithdrawalOrderByIdDesc(String email, boolean withdrawal);
    Optional<Member> findFirstByCiAndWithdrawalOrderByIdDesc(String ci, boolean withdrawal);
    Optional<Member> findByMemberNoAndWithdrawalAndRegisterType(String memberNo, boolean withdrawal, RegisterType registerType);
}
