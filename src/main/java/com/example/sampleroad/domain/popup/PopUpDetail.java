package com.example.sampleroad.domain.popup;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "POPUP_DETAIL")
public class PopUpDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POPUP_DETAIL_ID")
    private Long id;


    @Column(name = "POPUP_DETAIL_IMAGE")
    @Lob
    private String detailImageUrl;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POPUP_ID")
    private PopUp popUp;

}
