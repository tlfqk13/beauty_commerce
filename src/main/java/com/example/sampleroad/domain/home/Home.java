package com.example.sampleroad.domain.home;

import com.example.sampleroad.domain.product.HomeProductType;
import com.example.sampleroad.domain.product.Product;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "HOME")
public class Home {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HOME_ID")
    private Long id;

    @Column(name = "HOME_PRODUCT_TYPE")
    @Enumerated(EnumType.STRING)
    private HomeProductType homeProductType;

    @Column(name = "END_DATE")
    private LocalDateTime localDateTime;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;
}
