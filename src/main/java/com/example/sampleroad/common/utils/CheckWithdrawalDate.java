package com.example.sampleroad.common.utils;

import com.example.sampleroad.common.exception.ErrorCode;
import com.example.sampleroad.common.exception.ErrorCustomException;
import com.example.sampleroad.domain.member.Member;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
public class CheckWithdrawalDate {

    public static void checkWithdrawalDate(Member member) {

        if (member != null) {
            log.info("탈퇴 30일 이하 체크");
            if (member.getWithdrawalDate() != null) {
                LocalDateTime memberWithdrawalDate = member.getWithdrawalDate();
                LocalDateTime thirtyDaysAfter = memberWithdrawalDate.plus(30, ChronoUnit.DAYS);
                LocalDateTime todayDate = LocalDateTime.now();

                if (todayDate.isBefore(thirtyDaysAfter)) {
                    throw new ErrorCustomException(ErrorCode.NOT_AFTER_30_DAYS_WITHDRAWAL_DATE);
                }
            }
        }
    }
}
