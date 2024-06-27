package com.example.sampleroad.domain.product;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "MD_PICK")
public class MdPick {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MD_PICK_ID")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;
}
