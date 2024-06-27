package com.example.sampleroad.dto.response.member;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MemberBankResponseDto {
    private String refundBank;
    private String refundBankAccount;
    private String refundBankDepositorName;

    public MemberBankResponseDto(String refundBank, String refundBankAccount, String refundBankDepositorName){
        this.refundBank = refundBank;
        this.refundBankAccount = refundBankAccount;
        this.refundBankDepositorName = refundBankDepositorName;
    }
}
