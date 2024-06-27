package com.example.sampleroad.domain.member;

import com.example.sampleroad.dto.response.member.MemberBankResponseDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "MEMBER_BANK")
public class MemberBank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_BANK_ID")
    private Long id;

    @Column(name = "BANK_ACCOUNT")
    private String bankAccount;

    @Column(name = "BANK_NAME")
    private String bankName;

    @Column(name = "BANK_DEPOSITOR_NAME ")
    private String bankDepositorName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @Builder
    public MemberBank(String bankAccount, String bankName, String bankDepositorName, Member member) {
        this.bankAccount = bankAccount;
        this.bankName = bankName;
        this.bankDepositorName = bankDepositorName;
        this.member = member;
    }

    public void updateMemberInfo(MemberBankResponseDto memberBank) {
        if (memberBank.getRefundBank() != null) {
            this.bankName = memberBank.getRefundBank();
        }
        if (memberBank.getRefundBankAccount() != null) {
            this.bankAccount = memberBank.getRefundBankAccount();
        }
        if (memberBank.getRefundBankDepositorName() != null) {
            this.bankDepositorName = memberBank.getRefundBankDepositorName();
        }
    }

}
