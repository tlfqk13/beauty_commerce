package com.example.sampleroad.domain.sample;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "SAMPLE_KIT")
public class SampleKit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "KIT_ID")
    private Long id;

    @Column(name = "KIT_NAME")
    private String kitName;

    @Column(name = "KIT_PRODUCT_NO")
    private int kitProductNo;

    @OneToMany(mappedBy = "sampleKit", cascade = CascadeType.ALL)
    private List<SampleKitItem> sampleKitItems = new ArrayList<>();

}
