package com.example.sampleroad.service;

import com.example.sampleroad.common.exception.ErrorCode;
import com.example.sampleroad.common.exception.ErrorCustomException;
import com.example.sampleroad.common.utils.CheckWithdrawalDate;
import com.example.sampleroad.common.utils.CustomValue;
import com.example.sampleroad.common.utils.ShopBy;
import com.example.sampleroad.domain.AppleLoginUser;
import com.example.sampleroad.domain.DeliveryLocation;
import com.example.sampleroad.domain.push.PushToken;
import com.example.sampleroad.domain.RefreshToken;
import com.example.sampleroad.domain.member.Member;
import com.example.sampleroad.domain.member.MemberBank;
import com.example.sampleroad.domain.member.RegisterType;
import com.example.sampleroad.domain.notify.ProductStockNotify;
import com.example.sampleroad.domain.notify.WeeklyNotify;
import com.example.sampleroad.domain.push.NotificationAgree;
import com.example.sampleroad.domain.survey.Survey;
import com.example.sampleroad.dto.request.MemberRequestDto;
import com.example.sampleroad.dto.response.DeliveryLocationResponseDto;
import com.example.sampleroad.dto.response.member.MemberBankResponseDto;
import com.example.sampleroad.dto.response.member.MemberQueryDto;
import com.example.sampleroad.dto.response.member.MemberResponseDto;
import com.example.sampleroad.dto.response.survey.SurveyResponseDto;
import com.example.sampleroad.jwt.JwtTokenProvider;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.example.sampleroad.repository.AppleLoginUserRepository;
import com.example.sampleroad.repository.MemberBankRepository;
import com.example.sampleroad.repository.delivery.DeliveryLocationRepository;
import com.example.sampleroad.repository.member.MemberRepository;
import com.example.sampleroad.repository.notification.NotificationAgreeRepository;
import com.example.sampleroad.repository.notification.ProductStockNotificationRepository;
import com.example.sampleroad.repository.notification.WeeklyNotificationRepository;
import com.example.sampleroad.repository.push.PushTokenRepository;
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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final SurveyRepository surveyRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final MemberBankRepository memberBankRepository;
    private final DeliveryLocationRepository deliveryLocationRepository;
    private final AppleLoginUserRepository appleLoginUserRepository;
    private final NotificationAgreeRepository notificationAgreeRepository;
    private final WeeklyNotificationRepository weeklyNotificationRepository;
    private final PushTokenRepository pushTokenRepository;
    private final ProductStockNotificationRepository productStockNotificationRepository;

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

    @Value("${shop-by.send-authentication-number-url}")
    String sendAuthenticationNumberUrl;

    @Value("${shop-by.check-member-url}")
    String checkMemberUrl;

    @Value("${shop-by.password-no-authentication-certificated-by-sms}")
    String updatePasswordCertificatedBySms;

    @Value("${shop-by.server-profile}")
    String serverProfile;

    Gson gson = new Gson();


    @Transactional
    public void memberAdd(MemberRequestDto.CreateMember dto) throws UnirestException, ParseException {
        // TODO: 2023/08/17 만14세 미만 막는 로직 추가

        checkWithdrawalDate(dto);
        checkUnder14Years(dto);

        memberRepository.findByCiAndWithdrawal(dto.getCi(), false).ifPresent(member -> {
            log.info("이미 존재하는 회원 에러 발생");
            throw new ErrorCustomException(ErrorCode.ALREADY_USER_ERROR);
        });

        String joinTermsAgreements = String.join(",", dto.getJoinTermsAgreements());
        log.info("삽뱌이 회원가입 시작 ___S");
        String memberNo = shopbyCheckMemberAdd(dto);
        log.info("삽뱌이 회원가입 시작 ___E");
        Member saveMember = memberRepository.save(dto.toEntity(joinTermsAgreements, passwordEncoder.encode(dto.getPassword()), String.valueOf(memberNo)));
        surveyRepository.save(createSurvey(saveMember));
        memberBankRepository.save(createMemberBank(saveMember));
        notificationAgreeRepository.save(createNotificationAgree(saveMember, dto.isSmsAgreed(), dto.isDirectMailAgreed()));
        weeklyNotificationRepository.save(createWeeklyNotificationAgree(saveMember, dto.isPushNotificationAgreed()));
    }

    private void checkUnder14Years(MemberRequestDto.CreateMember dto) {
        if (dto.getBirthday() != null) {
            String birthday = dto.getBirthday();
            LocalDate birthdate = LocalDate.parse(birthday, DateTimeFormatter.ofPattern("yyyyMMdd"));
            LocalDate referenceDate = LocalDate.now();

            boolean isUnder14 = isUnder14Years(birthdate, referenceDate);
            if (isUnder14) {
                log.info("만 14세 미만입니다.");
                throw new ErrorCustomException(ErrorCode.UNDER_14YEARS_MEMBER);
            } else {
                log.info("만 14세 이상입니다.");
            }
        }
    }

    private boolean isUnder14Years(LocalDate birthdate, LocalDate referenceDate) {
        if ((birthdate != null) && (referenceDate != null)) {
            Period period = Period.between(birthdate, referenceDate);
            int years = period.getYears();
            int months = period.getMonths();
            int days = period.getDays();

            if (years < 14 || (years == 14 && months == 0 && days == 0)) {
                return true;
            }
        }
        return false;
    }

    private void checkWithdrawalDate(MemberRequestDto.CreateMember createMember) {
        // 탈퇴 30일 회원인지 조회
        Optional<Member> member = memberRepository.findFirstByCiAndWithdrawalOrderByIdDesc(createMember.getCi(), true);
        member.ifPresent(CheckWithdrawalDate::checkWithdrawalDate);
    }


    @Transactional
    public void memberModify(MemberRequestDto.UpdateMember dto, UserDetailsImpl userDetails) throws UnirestException, ParseException {

        Member member = getMember(userDetails);

        shopbyCheckMemberModify(member.getShopByAccessToken(), dto);
        String profileImageUrl = dto.getProfileImageURL();
        boolean isDefaultImageUrl = isDefaultImage(member.getProfileImageURL(), serverProfile);

        List<String> defaultImgList = getDefaultImgList(serverProfile);

        // Log the profile image update request
        if ("".equals(profileImageUrl)) {
            log.info("프로필 사진 삭제 요청임-> " + profileImageUrl);
            if (!isDefaultImageUrl) {
                String randomDefaultImg = getString(defaultImgList);
                member.updateMemberProfileImg(randomDefaultImg);
            }
        } else {
            member.updateMemberInfo(dto);
            member.updateMemberProfileImg(profileImageUrl);

            surveyRepository.findByMemberId(member.getId())
                    .ifPresent(survey -> survey.updateMemberInfo(
                            new SurveyResponseDto(dto.getSkinTrouble(), dto.getSkinType(), dto.getPreference())));

            memberBankRepository.findByMemberId(member.getId())
                    .ifPresent(memberBank -> memberBank.updateMemberInfo(
                            new MemberBankResponseDto(dto.getRefundBank(), dto.getRefundBankAccount(), dto.getRefundBankDepositorName())));
        }
    }

    private static String getString(List<String> defaultImgList) {
        Random random = new Random();
        int index = random.nextInt(defaultImgList.size());
        return defaultImgList.get(index);
    }

    private List<String> getDefaultImgList(String serverProfile) {
        return ("dev".equals(serverProfile)) ? CustomValue.defaultProfileImgTest : CustomValue.defaultProfileImgProd;
    }

    private boolean isDefaultImage(String profileImageUrl, String serverProfile) {
        List<String> defaultImages = ("dev".equals(serverProfile)) ? CustomValue.defaultProfileImgTest : CustomValue.defaultProfileImgProd;
        return defaultImages.contains(profileImageUrl);
    }

    private Member getMember(UserDetailsImpl userDetails) {
        return memberRepository.findById(userDetails.getMember().getId())
                .orElseThrow(() -> new ErrorCustomException(ErrorCode.NO_USER_ERROR));
    }


    @Transactional
    public void memberRemove(MemberRequestDto.DeleteMember dto, UserDetailsImpl userDetails) throws UnirestException, ParseException {
        Member member = getMember(userDetails);

        String encodedMessage = URLEncoder.encode(dto.getReason(), StandardCharsets.UTF_8);
        log.info("회원탈퇴 이유 -> " + dto.getReason());
        shopbyCheckMemberRemove(member.getShopByAccessToken(), encodedMessage);
        member.updateMemberWithdrawal();
        member.updateMemberMobileNo();
        LocalDateTime withdrawalDate = LocalDateTime.now();
        member.updateMemberWithdrawalDate(withdrawalDate);
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByMemberId(member.getId());
        refreshToken.ifPresent(refreshTokenRepository::delete);

        // TODO: 2023/08/25 탈퇴시 push token 삭제
        Optional<PushToken> pushToken = pushTokenRepository.findFirstByMemberId(member.getId());
        pushToken.ifPresent(pushTokenRepository::delete);

        if (member.getRegisterType().equals(RegisterType.APPLE)) {
            AppleLoginUser appleLoginUser = appleLoginUserRepository.findByMemberId(member.getId())
                    .orElseThrow(() -> new ErrorCustomException(ErrorCode.NO_USER_ERROR));
            appleLoginUserRepository.delete(appleLoginUser);
        }
    }

    public MemberResponseDto.MemberInfo getMemberInfo(UserDetailsImpl userDetails) {
        Long memberId = userDetails.getMember().getId();
        MemberQueryDto.MemberInfo memberInfo = memberRepository.findMemberInfo(memberId);
        // Optional을 활용한 값 설정
        boolean hotDealPushNotificationAgreed = weeklyNotificationRepository.findByMemberId(memberId)
                .map(WeeklyNotify::getWeeklyNotificationAgree)
                .orElse(Optional.ofNullable(memberInfo.getInfoAdPushNotificationAgreed()).orElse(false));

        boolean restockNotificationAgreed = productStockNotificationRepository.findByMemberId(memberId)
                .map(ProductStockNotify::getProductStockNotificationAgree)
                .orElse(false);

        memberInfo.setHotDealPushNotificationAgreed(hotDealPushNotificationAgreed);
        memberInfo.setRestockNotificationAgreed(restockNotificationAgreed);

        boolean isNewMember = memberInfo.getCreateTime().isEqual(memberInfo.getModifyTime());

        // 멤버 응답 DTO 생성
        MemberResponseDto memberResponseDto = new MemberResponseDto(memberInfo, userDetails.getMember());
        SurveyResponseDto surveyResponseDto = new SurveyResponseDto(memberInfo);
        DeliveryLocationResponseDto.DeliveryLocation deliveryLocationDto = getDeliveryLocationDto(userDetails.getMember());

        return new MemberResponseDto.MemberInfo(isNewMember, memberResponseDto, surveyResponseDto, deliveryLocationDto);
    }


    @Transactional
    public void logoutMember(UserDetailsImpl userDetails) throws UnirestException, ParseException {
        Member member = getMember(userDetails);
        shopbyCheckMemberLogout(member.getShopByAccessToken());
        // 로그아웃할때 토큰 삭제
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByMemberId(member.getId());
        refreshToken.ifPresent(refreshTokenRepository::delete);
        // fcm 토큰도 삭제
        Optional<PushToken> pushToken = pushTokenRepository.findFirstByMemberId(member.getId());
        pushToken.ifPresent(token -> pushTokenRepository.deleteById(token.getId()));
    }


    @Transactional
    public HashMap<String, Object> loginMember(MemberRequestDto.Login dto) throws UnirestException, ParseException {
        Member member = memberRepository.findByMemberLoginIdAndWithdrawal(dto.getMemberLoginId(), false)
                .orElseThrow(() -> new ErrorCustomException(ErrorCode.MEMBER_NOT_FOUND));

        shopbyLoginMember(dto, member);
        return createToken(member);
    }

    private void shopbyLoginMember(MemberRequestDto.Login dto, Member member) throws UnirestException, ParseException {
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


    public void memberLoginIdDuplicateChecked(String memberLoginId) {
        Optional<Member> member = memberRepository.findByMemberLoginIdAndWithdrawal(memberLoginId, false);
        if (member.isPresent()) {
            throw new ErrorCustomException(ErrorCode.ALREADY_USE_LOGIN_ID);
        } else {
            throw new ErrorCustomException(ErrorCode.NO_USE_LOGIN_ID_ERROR);
        }
    }


    public void nicknameDuplicateChecked(String nickname) {
        Optional<Member> member = memberRepository.findByNicknameAndWithdrawal(nickname, false);
        if (member.isPresent()) {
            throw new ErrorCustomException(ErrorCode.ALREADY_USE_NICKNAME_ERROR);
        } else {
            throw new ErrorCustomException(ErrorCode.NO_USE_NICKNAME_ERROR);
        }
    }


    public void ciDuplicateChecked(String ci) {
        Optional<Member> member = memberRepository.findByCiAndWithdrawal(ci, false);
        if (member.isPresent()) {
            throw new ErrorCustomException(ErrorCode.ALREADY_CI);
        } else {
            throw new ErrorCustomException(ErrorCode.NO_CI_ERROR);
        }
    }


    public void ciMatchChecked(String ci, UserDetailsImpl userDetails) {

        if (!userDetails.getMember().getCi().equals(ci)) {
            throw new ErrorCustomException(ErrorCode.NOT_MATCHED_CI);
        }
    }


    @Transactional
    public void modifyMemberPassword(MemberRequestDto.ChangeMemberPw dto, UserDetailsImpl userDetails) throws UnirestException, ParseException {

        Member member = memberRepository.findById(userDetails.getMember().getId())
                .orElseThrow(() -> new ErrorCustomException(ErrorCode.MEMBER_NOT_FOUND));

        boolean matches = passwordEncoder.matches(dto.getCurrentPassword(), member.getPassword());

        if (!matches) {
            throw new ErrorCustomException(ErrorCode.INCORRECT_ORIGINAL_PASSWORD);
        }

        String password = dto.getNewPassword() != null ? passwordEncoder.encode(dto.getNewPassword()) : null;
        member.updateMemberPassword(password);

        shopbyModifyMemberPassword(dto, userDetails.getMember().getShopByAccessToken());
    }


    public void emailDuplicateChecked(String email) {
        Optional<Member> member = memberRepository.findByEmailAndWithdrawal(email, false);
        if (member.isPresent()) {
            throw new ErrorCustomException(ErrorCode.ALREADY_USE_EMAIL_ERROR);
        } else {
            throw new ErrorCustomException(ErrorCode.NO_USE_EMAIL_ERROR);
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

    private NotificationAgree createNotificationAgree(Member saveMember, boolean smsAgreed, boolean directMailAgreed) {
        return NotificationAgree.builder()
                .isFirst(true)
                .smsAgreed(smsAgreed)
                .directMailAgreed(directMailAgreed)
                .member(saveMember)
                .build();
    }

    private WeeklyNotify createWeeklyNotificationAgree(Member saveMember, boolean pushNotificationAgreed) {
        return WeeklyNotify.builder()
                .weeklyNotificationAgree(pushNotificationAgreed)
                .member(saveMember)
                .build();
    }

    private void shopbyModifyMemberPassword(MemberRequestDto.ChangeMemberPw dto, String shopByAccessToken) throws UnirestException, ParseException {

        MemberRequestDto.ChangeMemberPw changeMemberPw =
                new MemberRequestDto.ChangeMemberPw(dto.getCurrentPassword(), dto.getNewPassword());

        HttpResponse<String> response = Unirest.put("https://shop-api.e-ncp.com/profile/password")
                .header("Content-Type", acceptHeader)
                .header("version", "1.1")
                .header("clientid", clientId)
                .header("platform", platformHeader)
                .header("accesstoken", shopByAccessToken)
                .body(gson.toJson(changeMemberPw))
                .asString();

        if (response.getStatus() != 204) {
            ShopBy.errorMessage(response);
        }
    }

    private String shopbyCheckMemberAdd(MemberRequestDto.CreateMember dto) throws UnirestException, ParseException {
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

    private void shopbyCheckMemberModify(String shopByAccessToken, MemberRequestDto.UpdateMember dto) throws UnirestException, ParseException {
        HttpResponse<String> response = Unirest.put(shopByUrl + checkMemberUrl)
                .header("Content-Type", acceptHeader)
                .header("version", versionHeader)
                .header("platform", platformHeader)
                .header("clientid", clientId)
                .header("accesstoken", shopByAccessToken)
                .body(gson.toJson(dto))
                .asString();

        if (response.getStatus() != 204) {
            ShopBy.errorMessage(response);
        }
    }

    private void shopbyCheckMemberRemove(String shopByAccessToken, String reason) throws UnirestException, ParseException {
        HttpResponse<String> response = Unirest.delete(shopByUrl + checkMemberUrl + "?reason=" + reason)
                .header("accept", "*/*")
                .header("version", versionHeader)
                .header("platform", platformHeader)
                .header("clientid", clientId)
                .header("accesstoken", shopByAccessToken)
                .asString();

        if (response.getStatus() != 204) {
            ShopBy.errorMessage(response);
        }
    }

    private void shopbyCheckMemberLogout(String shopByAccessToken) throws UnirestException, ParseException {
        log.info("shopbyAT " + shopByAccessToken);
        HttpResponse<String> response = Unirest.delete(shopByUrl + loginUrl)
                .header("Content-Type", acceptHeader)
                .header("version", versionHeader)
                .header("clientid", clientId)
                .header("accesstoken", shopByAccessToken)
                .header("platform", platformHeader)
                .asString();

        if (response.getStatus() != 204) {
            ShopBy.errorMessage(response);
        }
    }

    private DeliveryLocationResponseDto.DeliveryLocation getDeliveryLocationDto(Member member) {
        Optional<DeliveryLocation> deliveryLocationOptional = deliveryLocationRepository.findByMemberAndDefaultAddress(member, true);
        return deliveryLocationOptional.map(DeliveryLocationResponseDto.DeliveryLocation::new)
                .orElse(new DeliveryLocationResponseDto.DeliveryLocation());
    }
}
