package com.example.sampleroad.service;

import com.example.sampleroad.common.exception.ErrorCode;
import com.example.sampleroad.common.exception.ErrorCustomException;
import com.example.sampleroad.common.utils.CheckWithdrawalDate;
import com.example.sampleroad.common.utils.ShopBy;
import com.example.sampleroad.domain.RefreshToken;
import com.example.sampleroad.domain.survey.Survey;
import com.example.sampleroad.domain.member.Member;
import com.example.sampleroad.domain.member.MemberBank;
import com.example.sampleroad.domain.member.RegisterType;
import com.example.sampleroad.domain.push.NotificationAgree;
import com.example.sampleroad.dto.request.KakaoLoginRequestDto;
import com.example.sampleroad.dto.request.OpenIdRequestDto;
import com.example.sampleroad.dto.response.openId.OpenIdResponseDto;
import com.example.sampleroad.jwt.JwtTokenProvider;
import com.example.sampleroad.repository.MemberBankRepository;
import com.example.sampleroad.repository.member.MemberRepository;
import com.example.sampleroad.repository.notification.NotificationAgreeRepository;
import com.example.sampleroad.repository.survey.SurveyRepository;
import com.example.sampleroad.repository.token.RefreshTokenRepository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
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
public class OpenIdService {

    @Value("${shop-by.url}")
    String shopByUrl;

    @Value("${shop-by.accept-header}")
    String acceptHeader;

    @Value("${shop-by.version-header}")
    String versionHeader;

    @Value("${shop-by.platform-header}")
    String platformHeader;

    @Value("${shop-by.check-member-url}")
    String checkMemberUrl;

    @Value("${shop-by.client-id}")
    String clientId;

    private final MemberRepository memberRepository;
    private final SurveyRepository surveyRepository;
    private final MemberBankRepository memberBankRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final NotificationAgreeRepository notificationAgreeRepository;

    Gson gson = new Gson();

    @Transactional
    public HashMap<String, Object> getOpenIdAccessToken(String code) throws UnirestException, ParseException {
        // 샵바이에서 해당 유저의 accessToken을 가져온다
        OpenIdResponseDto.OpenIdAccessTokenDto openIdAccessTokenDto = shopbyGetOpenIdAccessToken(code);
        String accessToken = openIdAccessTokenDto.getAccessToken();
        log.info("open Id accessToken -> " + accessToken);

        OpenIdRequestDto.CreateMember createMember = shopbyGetProfileByOpenIdAccessToken(accessToken);

        return getOpenIdResult(accessToken, createMember);
    }

    private HashMap<String, Object> getOpenIdResult(String accessToken, OpenIdRequestDto.CreateMember createMember) throws UnirestException, ParseException {
        Random random = new Random();
        int randomNumber = random.nextInt(10000) + 1;  // 1부터 10000까지의 랜덤 숫자

        String uuid = UUID.randomUUID().toString().replace("-", "");
        log.info("uuid -> " + uuid);
        String nickName = createMember.getNickname() + uuid.substring(3, 9) + randomNumber;
        boolean smsAgreed = Optional.ofNullable(createMember.getSmsAgreed()).orElse(false);
        boolean directMailAgreed = Optional.ofNullable(createMember.getDirectMailAgreed()).orElse(false);
        OpenIdRequestDto.CreateOpenIdMember createOpenIdMember = new OpenIdRequestDto.CreateOpenIdMember(
                createMember.getMobileNo(), createMember.getMemberName(), createMember.getBirthday(), createMember.getSex(),
                createMember.getEmail(), nickName, true, createMember.getJoinTermsAgreements(),
                smsAgreed, directMailAgreed
        );

        // 조건문을 확실히 걸어야...
        // 기존 소셜 로그인 유저인지 파악 if문 조건으로 줘야함
        boolean existMember = memberRepository.existsByMemberNo(createMember.getMemberNo());
        checkWithdrawalDate(createMember);

        if (!existMember) {
            log.info("new social login member__S");
            if (!createMember.getMemberStatus().equals("ACTIVE")) {
                log.info("Not ACTIVE");
                shopbyRegisterOpenId(accessToken, createOpenIdMember);
            }
            log.info("new social login member__E");
            Member saveMember = memberRepository.save(createMember.toEntity(accessToken, nickName));
            surveyRepository.save(createSurvey(saveMember));
            memberBankRepository.save(createMemberBank(saveMember));
            notificationAgreeRepository.save(createNotificationAgree(saveMember,smsAgreed,directMailAgreed));
            return createToken(saveMember, false);
        } else {
            log.info("update shopbyAccessToken memberNo -> " + createMember.getMemberNo());
            Member member = memberRepository.findByMemberNoAndWithdrawalAndRegisterType(createMember.getMemberNo(), false, RegisterType.KAKAO)
                    .orElseThrow(() -> new ErrorCustomException(ErrorCode.NO_USER_ERROR));
            member.updateMemberShopByAccessToken(accessToken);
            Optional<Survey> survey = surveyRepository.findByMemberId(member.getId());
            boolean isSurvey = true;
            if (survey.isPresent()) {
                if (survey.get().getPreference().isBlank()) {
                    isSurvey = false;
                }
            }
            return createToken(member, isSurvey);
        }
    }

    private NotificationAgree createNotificationAgree(Member saveMember, boolean smsAgreed, boolean directMailAgreed) {
        return NotificationAgree.builder()
                .isFirst(true)
                .smsAgreed(smsAgreed)
                .directMailAgreed(directMailAgreed)
                .member(saveMember)
                .build();
    }

    private void checkWithdrawalDate(OpenIdRequestDto.CreateMember createMember) {
        // 탈퇴 30일 회원인지 조회
        Optional<Member> member = memberRepository.findFirstByEmailAndWithdrawalOrderByIdDesc(createMember.getEmail(), true);
        member.ifPresent(CheckWithdrawalDate::checkWithdrawalDate);
    }

    @Transactional
    public HashMap<String, Object> getOpenIdAccessToken(KakaoLoginRequestDto dto) throws UnirestException, ParseException {
        OpenIdResponseDto.OpenIdAccessTokenDto openIdAccessTokenDto = shopbyGetOpenIdAccessToken(dto);
        String accessToken = openIdAccessTokenDto.getAccessToken();
        log.info("open Id accessToken -> " + accessToken);

        OpenIdRequestDto.CreateMember createMember = shopbyGetProfileByOpenIdAccessToken(accessToken);

        return getOpenIdResult(accessToken, createMember);
    }

    private HashMap<String, Object> createToken(Member member, boolean isSurvey) {
        String accessToken = "Bearer " + jwtTokenProvider.createAccessToken(String.valueOf(member.getId()), member.getMemberNo());

        RefreshToken refreshToken = getOrCreateRefreshToken(member);

        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("accessToken", accessToken);
        resultMap.put("refreshToken", refreshToken.getToken());
        resultMap.put("isSurvey", isSurvey);

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

    /**
     * 회원정보 조회하기 API 확인하기
     * 샵바이 version 1.0.0
     **/
    private OpenIdRequestDto.CreateMember shopbyGetProfileByOpenIdAccessToken(String accessToken) throws UnirestException, ParseException {
        HttpResponse<String> response = Unirest.get("https://shop-api.e-ncp.com/profile")
                .header("version", "1.0")
                .header("platform", "MOBILE_WEB")
                .header("clientid", clientId)
                .header("accesstoken", accessToken)
                .asString();

        log.info("response.getBody()->>>>>>>>> " + response.getBody());

        JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
        String memberNo = jsonObject.get("memberNo").isJsonNull() ? null : jsonObject.get("memberNo").getAsString();
        String memberName = jsonObject.get("memberName").isJsonNull() ? null : jsonObject.get("memberName").getAsString();
        String memberId = jsonObject.get("memberId").isJsonNull() ? null : jsonObject.get("memberId").getAsString();
        String mobileNo = jsonObject.get("mobileNo").isJsonNull() ? null : jsonObject.get("mobileNo").getAsString();
        String birthday = jsonObject.get("birthday").isJsonNull() ? null : jsonObject.get("birthday").getAsString();
        String sex = jsonObject.get("sex").isJsonNull() ? null : jsonObject.get("sex").getAsString();
        String email = jsonObject.get("email").isJsonNull() ? null : jsonObject.get("email").getAsString();
        String nickname = jsonObject.get("nickname").isJsonNull() ? null : jsonObject.get("nickname").getAsString();
        String memberStatus = jsonObject.get("memberStatus").isJsonNull() ? null : jsonObject.get("memberStatus").getAsString();
        boolean smsAgreed = false;
        if (!jsonObject.get("smsAgreed").isJsonNull()) {
            smsAgreed = jsonObject.get("smsAgreed").getAsBoolean();
        }
        boolean directMailAgreed = false;
        if (!jsonObject.get("directMailAgreed").isJsonNull()) {
            directMailAgreed = jsonObject.get("directMailAgreed").getAsBoolean();
        }

        return new OpenIdRequestDto.CreateMember(nickname, true
                , null, mobileNo
                , memberName, null, birthday, null, null, sex
                , memberNo, email, smsAgreed, directMailAgreed, memberStatus);

    }

    private void shopbyRegisterOpenId(String accessToken, OpenIdRequestDto.CreateOpenIdMember dto) throws UnirestException, ParseException {
        // TODO: 2023-07-19 오픈 아이디를 사용한 회원 가입 처리입니다.
        HttpResponse<String> response = Unirest.post("https://shop-api.e-ncp.com/profile/openid")
                .header("accept", acceptHeader)
                .header("version", versionHeader)
                .header("clientid", clientId)
                .header("accesstoken", accessToken)
                .header("platform", "MOBILE_WEB")
                .header("content-type", acceptHeader)
                .body(gson.toJson(dto))
                .asString();

        log.info("오픈 ID 회원가입 ->>>>>>>>>>>>>>>>>>>>>>>>");
        if (response.getStatus() != 200) {
            log.info("오픈 ID 회원가입 errorMessage ->>>>>>>>>>>>>>>>>>>>>>>>");
            ShopBy.errorMessage(response);
        }
        log.info(response.getBody());
        log.info("오픈 ID 회원가입 ->>>>>>>>>>>>>>>>>>>>>>>>");
    }

    // TODO: 2023-07-19 OpenId 회원의 AccessToken 발급하기 위한 API 입니다.
    private OpenIdResponseDto.OpenIdAccessTokenDto shopbyGetOpenIdAccessToken(String code) throws UnirestException, ParseException {
        String provider = "ncp_kakao-sync";
        log.info("code ->>>>>>>>>>>>>>>>>>> " + code);
        HttpResponse<String> response =
                Unirest.get("https://shop-api.e-ncp.com/oauth/openid"
                                + "?provider=" + provider
                                + "&code=" + code
                                + "&keepLogin=true")
                        .header("version", "1.0")
                        .header("clientid", clientId)
                        .header("platform", "MOBILE_WEB")
                        .asString();

        return getOpenIdAccessTokenDto(response);
    }

    // TODO: 2023/08/14 카카오 앱 by 앱에서 Accesstoken 받아서 샵바이에 토큰요청 위한 API
    private OpenIdResponseDto.OpenIdAccessTokenDto shopbyGetOpenIdAccessToken(KakaoLoginRequestDto dto) throws UnirestException, ParseException {
        String provider = "ncp_kakao-sync";
        String openAccessToken = dto.getAccessToken();
        HttpResponse<String> response =
                Unirest.get("https://shop-api.e-ncp.com/oauth/openid"
                                + "?provider=" + provider
                                + "&openAccessToken=" + openAccessToken
                                + "&keepLogin=true")
                        .header("version", "1.0")
                        .header("clientid", clientId)
                        .header("platform", "MOBILE_WEB")
                        .asString();

        return getOpenIdAccessTokenDto(response);
    }

    private OpenIdResponseDto.OpenIdAccessTokenDto getOpenIdAccessTokenDto(HttpResponse<String> response) {
        log.info("response.getBody() " + response.getBody());
        if (response.getStatus() != 200) {
            throw new ErrorCustomException(ErrorCode.NO_CERTIFICATION_ERROR);
        }

        log.info("Open Access Token 발급하기_____________________");
        log.info(response.getBody());
        log.info("Open Access Token 발급하기_____________________");

        JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
        String accessToken = jsonObject.get("accessToken").getAsString();
        Long expireIn = jsonObject.get("expireIn").getAsLong();
        String email = "";
        String signUpDateTime = "";

        // 카카오 싱크 회원으로 가입하면 null로 내려와서 막아줘야함
        if (jsonObject.has("ordinaryMemberResponse") && !jsonObject.get("ordinaryMemberResponse").isJsonNull()) {
            JsonObject ordinaryMemberResponseJsonObject = jsonObject.getAsJsonObject("ordinaryMemberResponse");
            email = ordinaryMemberResponseJsonObject.get("email").getAsString();
            signUpDateTime = ordinaryMemberResponseJsonObject.get("signUpDateTime").getAsString();
        } else {
            log.info("ordinaryMemberResponse null입니다 ");
            email = ""; // 또는 다른 기본값으로 설정
            signUpDateTime = "";
        }

        OpenIdResponseDto.OrdinaryMemberResponse ordinaryMemberResponse
                = new OpenIdResponseDto.OrdinaryMemberResponse(email, signUpDateTime);

        return new OpenIdResponseDto.OpenIdAccessTokenDto(accessToken, expireIn, ordinaryMemberResponse);
    }

    public OpenIdResponseDto getOpenIdLoginUrl() throws UnirestException {
        String loginUrl = shopbyGetOpenIdLoginUrl();
        loginUrl = loginUrl.replace("\"", "");
        return new OpenIdResponseDto(loginUrl);
    }

    private String shopbyGetOpenIdLoginUrl() throws UnirestException {
        HttpResponse<String> response =
                Unirest.get("https://shop-api.e-ncp.com/oauth/login-url" +
                                "?provider=ncp_kakao-sync" +
                                /*"&redirectUri=http://15.164.224.27:8888/oauth/callback")*/
                                "&redirectUri=http://127.0.0.1:8888/oauth/callback")
                        .header("version", "1.0")
                        .header("clientid", clientId)
                        .header("platform", "MOBILE_WEB")
                        .asString();

        log.info("Open Id URL 주소가져오기_____________________");
        log.info(response.getBody());
        log.info("Open Id URL 주소가져오기_____________________");

        if (response.getStatus() != 200) {
            throw new ErrorCustomException(ErrorCode.NO_CERTIFICATION_ERROR);
        }

        JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
        String loginUrl = jsonObject.get("loginUrl").toString();
        log.info("loginUrl ->>>>>>>>> " + loginUrl);

        return loginUrl;
    }
}
