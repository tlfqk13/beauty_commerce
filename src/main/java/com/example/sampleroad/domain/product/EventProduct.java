package com.example.sampleroad.domain.product;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "EVENT_PRODUCT")
public class EventProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EVENT_PRODUCT_ID")
    private Long id;

    @Column(name = "EVENT_FINISH_TIME")
    private LocalDateTime eventFinishTime;

    @Column(name = "EVENT_TITLE")
    private String eventTitle;

    @Column(name = "EVENT_NAME")
    private String eventName;

    @Column(name = "EVENT_SUB_TITLE")
    private String eventSubTitle;

    @Column(name = "EVENT_PRICE")
    private int eventPrice;

    @Column(name = "IS_VISIBLE")
    private boolean isVisible;

    @Column(name = "EVENT_PRODUCT_TYPE")
    @Enumerated(EnumType.STRING)
    private EventProductType eventProductType;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

    public boolean getIsVisible() {
        return isVisible;
    }
}
