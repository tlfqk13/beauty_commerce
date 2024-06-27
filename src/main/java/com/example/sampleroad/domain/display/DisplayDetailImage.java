package com.example.sampleroad.domain.display;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "DISPLAY_DETAIL_IMAGE")
public class DisplayDetailImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DISPLAY_DEATAIL_IMAGE_ID")
    private Long id;

    @Column(name = "IMAGE_POS")
    private int imagePos;

    @Column(name = "IMAGE_URL")
    @Lob
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DISPLAY_ID")
    private Display display;
}
