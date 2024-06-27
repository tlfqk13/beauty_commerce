package com.example.sampleroad.controller;

import com.example.sampleroad.dto.request.PaymentRequestDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
@RequiredArgsConstructor
public class PaymentController {

    @Value("${shop-by.client-id}")
    String clientId;

    @PostMapping("/api/payment/reserve")
    public String reservePayment(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                 @RequestBody PaymentRequestDto paymentRequestDto,
                                 Model model) throws JsonProcessingException {
        log.info("orderSheetNo: {}", paymentRequestDto.getOrderSheetNo());
        model.addAttribute("accessToken", userDetails.getMember().getShopByAccessToken());
        model.addAttribute("clientId", clientId);
        log.info("결제 페이지 진입 __________________E");

        String receiverAddress = paymentRequestDto.getShippingAddress().getReceiverAddress();
        String receiverZipCd = paymentRequestDto.getShippingAddress().getReceiverZipCd();
        log.info("receiverAddress: {}", receiverAddress);
        log.info("receiverZipCd: {}", receiverZipCd);

        boolean startsWithLetter = receiverAddress.matches("^[a-zA-Z가-힣].*");
        String convertRecevierAddress = "";
        String convertRecevierZipCd = "";

        if (startsWithLetter) {
            // 대문자로 시작하면 true
            System.out.println("Address starts with a letter");
        } else {
            convertRecevierAddress = receiverZipCd;
            convertRecevierZipCd = receiverAddress;
            System.out.println("Address does not start with a letter");
            paymentRequestDto.getShippingAddress().setReceiverAddress(convertRecevierAddress);
            paymentRequestDto.getShippingAddress().setReceiverZipCd(convertRecevierZipCd);
        }

        log.info("convertRecevierAddress: {}", convertRecevierAddress);
        log.info("convertRecevierZipCd: {}", convertRecevierZipCd);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonData = objectMapper.writeValueAsString(paymentRequestDto);
        model.addAttribute("jsonData", jsonData);
        log.info("jsonData: {}", jsonData);

        return "shopbypayment-real";
    }

    @GetMapping("/payment/confirm")
    public String paymentConfirm(@RequestParam(defaultValue = "") String orderSheetNo,
                                 @RequestParam(defaultValue = "") String result,
                                 @RequestParam(defaultValue = "") String message) {

        log.info(orderSheetNo);
        log.info(result);
        log.info(message);

        if (!result.equals("SUCCESS")) {
            log.info("결제 실패");
            return "payment-fail";
        }
        return "payment-success";
    }
}
