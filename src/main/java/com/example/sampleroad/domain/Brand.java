package com.example.sampleroad.domain;


import com.example.sampleroad.domain.product.Product;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "BRAND")
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BRAND_ID")
    private Long id;

    @Column(name = "BRAND_IMAGE_URL")
    private String brandImageUrl;

    @Column(name = "BRAND_DESCRIPTION")
    private String brandDescription;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

}
