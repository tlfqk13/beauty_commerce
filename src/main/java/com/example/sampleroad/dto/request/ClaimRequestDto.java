package com.example.sampleroad.dto.request;

import com.example.sampleroad.domain.claim.ClaimReasonType;
import com.example.sampleroad.domain.claim.ReturnWayType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class ClaimRequestDto {

    @NoArgsConstructor
    @Getter
    public static class ReturnClaims {
        private String claimType;
        private String[] claimImageUrls;
        private int productCnt;
        private ClaimReasonType claimReasonType;
        private String claimReasonDetail;
        private List<ClaimedProductOption> claimedProductOptions;
        private ClaimRequestDto.BankAccountInfo bankAccountInfo;
        private ReturnWayType returnWayType;
        private ClaimRequestDto.ReturnAddressInfo returnAddress;

        public ReturnClaims(ReturnClaims dto, List<ClaimedProductOption> claimedProductOption) {
            this.claimType = dto.getClaimType();
            this.claimImageUrls = dto.getClaimImageUrls();
            this.productCnt = dto.getProductCnt();
            this.claimReasonType = dto.getClaimReasonType();
            this.claimReasonDetail = dto.getClaimReasonDetail();
            this.claimedProductOptions = claimedProductOption;
            this.bankAccountInfo = dto.getBankAccountInfo();
            this.returnWayType = dto.getReturnWayType();
            this.returnAddress = dto.getReturnAddress();
        }
    }

    @NoArgsConstructor
    @Getter
    public static class ExchangeClaims {
        private String[] claimImageUrls;
        private int productCnt;
        private ClaimReasonType claimReasonType;
        private String claimReasonDetail;
        private ClaimRequestDto.BankAccountInfo bankAccountInfo;
        private ReturnWayType returnWayType;
        private ClaimRequestDto.ReturnAddressInfo returnAddressInfo;
        private ClaimRequestDto.ExchangeAddressInfo exchangeAddressInfo;
        private ClaimRequestDto.ExchangeOption exchangeOption;

        public ExchangeClaims(ExchangeClaims exchangeClaims, ExchangeOption exchangeOption) {
            this.claimImageUrls = exchangeClaims.getClaimImageUrls();
            this.productCnt = exchangeClaims.getProductCnt();
            this.claimReasonType = exchangeClaims.getClaimReasonType();
            this.claimReasonDetail = exchangeClaims.getClaimReasonDetail();
            this.bankAccountInfo = exchangeClaims.getBankAccountInfo();
            this.returnWayType = exchangeClaims.getReturnWayType();
            this.returnAddressInfo = exchangeClaims.getReturnAddressInfo();
            this.exchangeAddressInfo = exchangeClaims.getExchangeAddressInfo();
            this.exchangeOption = exchangeOption;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class ClaimedProductOption {
        private int orderProductOptionNo;
        private int productCnt;

        public ClaimedProductOption(int orderProductOptionNo, int productCnt) {
            this.orderProductOptionNo = orderProductOptionNo;
            this.productCnt = productCnt;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class ExchangeOption {
        private List<ExchangeInputTexts> inputTexts;
        private int orderCnt;
        private int optionNo;
        private int productNo;

        public ExchangeOption(List<ExchangeInputTexts> inputTexts, int orderCnt, int optionNo, int productNo) {
            this.inputTexts = inputTexts;
            this.orderCnt = orderCnt;
            this.optionNo = optionNo;
            this.productNo = productNo;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class UpdateClaimWithdraw {
        private List<ClaimDto> updateClaim;
    }

    @NoArgsConstructor
    @Getter
    public static class ClaimDto {
        private String claimNo;
        private int orderOptionNo;
    }

    @NoArgsConstructor
    @Getter
    public static class ExchangeInputTexts {
        private String inputValue;
        private String inputLabel;
    }

    @NoArgsConstructor
    @Getter
    public static class BankAccountInfo {
        private String bankAccount;
        private String bankDepositorName;
        private String bank;
        private String bankName;
    }

    @NoArgsConstructor
    @Getter
    public static class ReturnAddressInfo {
        private String receiverName;
        private String addressName;
        private String receiverContact1;
        private String receiverZipCd;
        private String receiverAddress;
        private String receiverJibunAddress;
        private String receiverDetailAddress;
        private String deliveryMemo;
    }

    @NoArgsConstructor
    @Getter
    public static class ExchangeAddressInfo {
        private String receiverName;
        private String addressName;
        private String receiverContact1;
        private String receiverZipCd;
        private String receiverAddress;
        private String receiverJibunAddress;
        private String receiverDetailAddress;
        private String deliveryMemo;
    }
}
