package com.example.sampleroad.domain.lotto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "LOTTO")
public class Lotto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LOTTO_ID")
    private Long id;

    @Column(name = "LOTTO_KEY_NO")
    private int lottoKeyNo; // == bannerKeyNo 임시
}