package com.example.sampleroad.domain;

import com.example.sampleroad.domain.member.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "IMAGE")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IMAGE_ID")
    private Long id;

    @Column(name = "IMAGE_URL")
    @Lob
    private String imageUrl;

    @Column(name = "ORIGIN_NAME")
    @Lob
    private String originName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @Builder
    public Image(String imageUrl, String originName, Member member) {
        this.imageUrl = imageUrl;
        this.originName = originName;
        this.member = member;
    }
}
