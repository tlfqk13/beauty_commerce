package com.example.sampleroad.domain.experience;

import com.example.sampleroad.common.utils.TimeStamped;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "EXPERIENCE_DETAIL_IMAGE")
public class ExperienceDetailImage{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EXPERIENCE_DEATAIL_IMAGE_ID")
    private Long id;

    @Column(name = "IMAGE_URL")
    @Lob
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EXPERIENCE_ID")
    private Experience experience;
}
