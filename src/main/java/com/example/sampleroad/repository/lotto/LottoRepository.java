package com.example.sampleroad.repository.lotto;

import com.example.sampleroad.domain.lotto.Lotto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LottoRepository extends JpaRepository<Lotto, Long> {

    Optional<Lotto> findByLottoKeyNo(int keyNo);

}
