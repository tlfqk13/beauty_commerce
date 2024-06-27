package com.example.sampleroad.service;

import com.example.sampleroad.common.exception.ErrorCode;
import com.example.sampleroad.common.exception.ErrorCustomException;
import com.example.sampleroad.common.utils.CheckWithdrawalDate;
import com.example.sampleroad.common.utils.ShopBy;
import com.example.sampleroad.domain.AppleLoginUser;
import com.example.sampleroad.domain.RefreshToken;
import com.example.sampleroad.domain.survey.Survey;
import com.example.sampleroad.domain.member.Member;
import com.example.sampleroad.domain.member.MemberBank;
import com.example.sampleroad.domain.push.NotificationAgree;
import com.example.sampleroad.dto.request.AppleLoginRequestDto;
import com.example.sampleroad.jwt.JwtTokenProvider;
import com.example.sampleroad.repository.AppleLoginUserRepository;
import com.example.sampleroad.repository.MemberBankRepository;
import com.example.sampleroad.repository.member.MemberRepository;
import com.example.sampleroad.repository.notification.NotificationAgreeRepository;
import com.example.sampleroad.repository.survey.SurveyRepository;
import com.example.sampleroad.repository.token.RefreshTokenRepository;
import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AppleIdService {

    private final MemberRepository memberRepository;
    private final SurveyRepository surveyRepository;
    private final MemberBankRepository memberBankRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AppleLoginUserRepository appleLoginUserRepository;
    private final NotificationAgreeRepository notificationAgreeRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${shop-by.client-id}")
    String clientId;

    @Value("${shop-by.url}")
    String shopByUrl;

    @Value("${shop-by.accept-header}")
    String acceptHeader;

    @Value("${shop-by.version-header}")
    String versionHeader;

    @Value("${shop-by.platform-header}")
    String platformHeader;

    @Value("${shop-by.login-url}")
    String loginUrl;

    @Value("${shop-by.check-member-url}")
    String checkMemberUrl;

    Gson gson = new Gson();

    @Transactional
    public HashMap<String, Object> addMemberAppleLogin(String userIdentifier) throws UnirestException, ParseException {
        log.info("userIdentifier -> " + userIdentifier);
        Optional<AppleLoginUser> appleLoginUser =
                appleLoginUserRepository.findByUserIdentifier(userIdentifier);

        if (appleLoginUser.isPresent()) {
            Member member = appleLoginUser.get().getMember();
            AppleLoginRequestDto.Login dto =
                    new AppleLoginRequestDto.Login(member.getMemberLoginId(), appleLoginUser.get().getPassword());
            shopbyLoginMember(dto, member);

            return createToken(member);
        } else {
            throw new ErrorCustomException(ErrorCode.APPLE_MEMBER_NOT_FOUND);
        }
    }

    private void shopbyLoginMember(AppleLoginRequestDto.Login dto, Member member) throws UnirestException, ParseException {
        JSONObject json = new JSONObject();
        json.put("memberId", dto.getMemberLoginId());
        json.put("password", dto.getPassword());
        json.put("keepLogin", true);
        HttpResponse<String> response = Unirest.post(shopByUrl + loginUrl)
                .header("accept", acceptHeader)
                .header("version", versionHeader)
                .header("clientid", clientId)
                .header("platform", platformHeader)
                .header("content-type", acceptHeader)
                .body(gson.toJson(json))
                .asString();
        JSONObject jsonObject = ShopBy.errorMessage(response);
        String shopbyAccessToken = String.valueOf(jsonObject.get("accessToken"));

        member.updateMemberShopByAccessToken(shopbyAccessToken);
    }

    @Transactional
    public void addMemberAppleIdJoin(AppleLoginRequestDto.CreateMember createMember) throws UnirestException, ParseException {

        checkWithdrawalDate(createMember);

        appleLoginUserRepository.findByUserIdentifier(createMember.getUserIdentifier())
                .ifPresent(appleLoginUser ->
                {
                    throw new ErrorCustomException(ErrorCode.ALREADY_USER_ERROR);
                });

        Random random = new Random();
        int randomNumber = random.nextInt(10000) + 1;  // 1부터 10000까지의 랜덤 숫자

        String uuid = UUID.randomUUID().toString().replace("-", "");
        log.info("uuid -> " + uuid);
        String appleMemberId = uuid.substring(3, 11);
        String applePassword = uuid.substring(1, 8);

        appleMemberId = "a" + appleMemberId + randomNumber;
        applePassword = "a" + applePassword + randomNumber;

        AppleLoginRequestDto.CreateMember dto =
                new AppleLoginRequestDto.CreateMember(createMember, appleMemberId, applePassword);

        log.info("appleMemberId -> " + appleMemberId);
        log.info("applePassword -> " + applePassword);
        String joinTermsAgreements = String.join(",", createMember.getJoinTermsAgreements());
        log.info("삽뱌이 회원가입 시작 ___S");
        String memberNo = shopbyCheckMemberAdd(dto);
        log.info("삽뱌이 회원가입 시작 ___E");

        Member saveMember = memberRepository.save(createMember
                .toEntity(joinTermsAgreements,
                        String.valueOf(memberNo),
                        appleMemberId,
                        passwordEncoder.encode(applePassword)));

        surveyRepository.save(createSurvey(saveMember));
        memberBankRepository.save(createMemberBank(saveMember));
        notificationAgreeRepository.save(createNotificationAgree(saveMember));
        // 에플 로긴 테이블에도 추가해줘야한다.
        appleLoginUserRepository.save(createAppleLoginUser(saveMember, createMember.getUserIdentifier(), applePassword));
    }

    private NotificationAgree createNotificationAgree(Member saveMember) {
        return NotificationAgree.builder()
                .isFirst(true)
                .smsAgreed(false)
                .directMailAgreed(false)
                .member(saveMember)
                .build();
    }

    private void checkWithdrawalDate(AppleLoginRequestDto.CreateMember createMember) {
        // 탈퇴 30일 회원인지 조회
        Optional<Member> member = memberRepository.findFirstByCiAndWithdrawalOrderByIdDesc(createMember.getCi(), true);
        member.ifPresent(CheckWithdrawalDate::checkWithdrawalDate);
    }

    private AppleLoginUser createAppleLoginUser(Member saveMember, String userIdentifier, String applePassword) {
        return AppleLoginUser.builder()
                .userIdentifier(userIdentifier)
                .password(applePassword)
                .member(saveMember)
                .build();
    }

    private Survey createSurvey(Member saveMember) {
        return Survey.builder()
                .skinType("")
                .skinTrouble("")
                .preference("")
                .member(saveMember)
                .build();
    }

    private MemberBank createMemberBank(Member saveMember) {
        return MemberBank.builder()
                .bankAccount("")
                .bankName("")
                .bankDepositorName("")
                .member(saveMember)
                .build();
    }

    private HashMap<String, Object> createToken(Member member) {

        String accessToken = "Bearer " + jwtTokenProvider.createAccessToken(String.valueOf(member.getId()), member.getMemberNo());
        RefreshToken refreshToken = getOrCreateRefreshToken(member);
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("accessToken", accessToken);
        resultMap.put("refreshToken", refreshToken.getToken());

        return resultMap;
    }

    private RefreshToken getOrCreateRefreshToken(Member member) {
        Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.findByMemberId(member.getId());
        if (refreshTokenOptional.isPresent()) {
            RefreshToken refreshToken = refreshTokenOptional.get();
            refreshToken.updateRefreshToken("Bearer " + jwtTokenProvider.createRefreshToken(String.valueOf(member.getId()), member.getMemberNo()));
            return refreshToken;
        } else {
            return refreshTokenRepository.save(new RefreshToken("Bearer " + jwtTokenProvider.createRefreshToken(String.valueOf(member.getId()), member.getNickname()), member));
        }
    }

    private String shopbyCheckMemberAdd(AppleLoginRequestDto.CreateMember dto) throws UnirestException, ParseException {
        HttpResponse<String> response = Unirest.post(shopByUrl + checkMemberUrl)
                .header("Content-Type", acceptHeader)
                .header("version", versionHeader)
                .header("clientid", clientId)
                .header("platform", platformHeader)
                .body(gson.toJson(dto))
                .asString();

        log.info("회원가입 샵바이 요청 확인 " + response.getBody());
        JSONObject jsonObject = ShopBy.errorMessage(response);

        String memberNo = String.valueOf(jsonObject.get("memberNo"));
        return memberNo;
    }
}
