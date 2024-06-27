package com.example.sampleroad.domain;

import com.example.sampleroad.common.utils.TimeStamped;
import com.example.sampleroad.domain.member.Member;
import com.example.sampleroad.dto.request.DeliveryLocationRequestDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "DELIVERY_LOCATION")
public class DeliveryLocation extends TimeStamped {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DELIVERY_LOCATION_ID")
    private Long id;

    @Column(name = "RECEIVER_NAME")
    private String receiverName;

    @Column(name = "ADDRESS_NAME")
    private String addressName;

    @Column(name ="RECEIVER_CONTACT")
    private String receiverContact;

    @Column(name ="RECEIVER_ZIPCODE")
    private String receiverZipCode;

    @Column(name ="RECEIVER_ADDRESS")
    private String receiverAddress;

    @Column(name ="RECEIVER_JIBUN_ADDRESS")
    private String receiverJibunAddress;

    @Column(name = "RECEIVER_DETAIL_ADDRESS")
    private String receiverDetailAddress;

    @Column(name = "is_default")
    private Boolean defaultAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @Builder
    public DeliveryLocation(Boolean defaultAddress, String receiverName, String addressName, String receiverContact, String receiverZipCode,
                            String receiverAddress, String receiverJibunAddress, String receiverDetailAddress, Member member){
        this.defaultAddress = defaultAddress;
        this.receiverName = receiverName;
        this.addressName = addressName;
        this.receiverContact = receiverContact;
        this.receiverZipCode = receiverZipCode;
        this.receiverAddress = receiverAddress;
        this.receiverJibunAddress = receiverJibunAddress;
        this.receiverDetailAddress = receiverDetailAddress;
        this.member = member;
    }
    
    public void updateDeliveryLocation(DeliveryLocationRequestDto.Update dto) {
        this.defaultAddress = dto.getDefaultAddress();
        this.receiverName = dto.getReceiverName();
        this.addressName = dto.getAddressName();
        this.receiverContact = dto.getReceiverContact();
        this.receiverZipCode = dto.getReceiverZipCode();
        this.receiverAddress = dto.getReceiverAddress();
        this.receiverJibunAddress = dto.getReceiverJibunAddress();
        this.receiverDetailAddress = dto.getReceiverDetailAddress();
    }

    public void updateDefaultAddress(Boolean defaultAddress){
        this.defaultAddress = defaultAddress;
    }
}
