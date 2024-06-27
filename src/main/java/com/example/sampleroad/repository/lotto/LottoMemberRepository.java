package com.example.sampleroad.repository.lotto;

import com.example.sampleroad.domain.lotto.LottoMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LottoMemberRepository extends JpaRepository<LottoMember, Long> {

    Optional<LottoMember> findByLotto_LottoKeyNoAndMemberIdAndLotto_Id(int keyNo,Long memberId,Long lottoId);
}
