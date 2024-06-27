package com.example.sampleroad.service;

import com.example.sampleroad.common.exception.ErrorCode;
import com.example.sampleroad.common.exception.ErrorCustomException;
import com.example.sampleroad.common.utils.ShopBy;
import com.example.sampleroad.domain.authentication.Authentication;
import com.example.sampleroad.domain.member.Member;
import com.example.sampleroad.domain.member.RegisterType;
import com.example.sampleroad.domain.push.NotificationAgree;
import com.example.sampleroad.domain.survey.Survey;
import com.example.sampleroad.dto.request.MemberRequestDto;
import com.example.sampleroad.dto.response.AuthenticationResponseDto;
import com.example.sampleroad.dto.response.member.MemberResponseDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.example.sampleroad.repository.authentication.AuthenticationRepository;
import com.example.sampleroad.repository.member.MemberRepository;
import com.example.sampleroad.repository.notification.NotificationAgreeRepository;
import com.example.sampleroad.repository.survey.SurveyRepository;
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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AuthenticationsServiceImpl implements AuthenticationsService {

    private final MemberRepository memberRepository;
    private final AuthenticationRepository authenticationRepository;
    private final SurveyRepository surveyRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationAgreeRepository notificationAgreeRepository;

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

    Gson gson = new Gson();

    @Override
    @Transactional
    public void sendAuthenticationNumberForId(MemberRequestDto.SendAuthenticationNumberById dto) throws UnirestException, ParseException {
        log.info("아이디 찾기 인증번호 시작____________________");
        // TODO: 2023/07/20 1일 5회 제한
        int sendCount = 1;
        // 탈퇴회원 조회 못하게
        // TODO: 2/2/24 이름으로 하면 중복이라 망한다!!!!!
        Member member = memberRepository.findByMobileNoAndMemberNameAndRegisterTypeAndWithdrawal(dto.getNotiAccount(), dto.getMemberName(), RegisterType.APP, false)
                .orElseThrow(() -> new ErrorCustomException(ErrorCode.NO_USER_ERROR));

        if (RegisterType.APPLE.equals(member.getRegisterType())) {
            throw new ErrorCustomException(ErrorCode.APPLE_LOGIN_USER);
        } else if (RegisterType.KAKAO.equals(member.getRegisterType())) {
            throw new ErrorCustomException(ErrorCode.KAKAO_LOGIN_USER);
        }

        boolean existAuthentication = authenticationRepository.existsByMobileNoAndMemberName(dto.getNotiAccount(), dto.getMemberName());

        if (!existAuthentication) {
            Authentication authentication = Authentication.builder()
                    .member(member)
                    .sendCount(sendCount)
                    .build();
            authenticationRepository.save(authentication);
            shopbySendAuthenticationNumberForId(dto);
        } else {
            AuthenticationResponseDto authenticationResponseDto = authenticationRepository.findByMobileNoAndMebmerName(dto.getNotiAccount(), dto.getMemberName());
            Optional<Authentication> memberAuthentication = authenticationRepository.findById(authenticationResponseDto.getId());
            if (memberAuthentication.isPresent()) {
                if (authenticationResponseDto.getSendCount() >= 5) {
                    throw new ErrorCustomException(ErrorCode.OVER_SEND_AUTHENTICATION);
                } else {
                    shopbySendAuthenticationNumberForId(dto);
                    sendCount = authenticationResponseDto.getSendCount() + 1;
                    memberAuthentication.get().updateSendCount(sendCount);
                }
            } else {
                throw new ErrorCustomException(ErrorCode.CALL_CUSTOMER_INFORMATION);
            }
        }
        log.info("아이디 찾기 인증번호 종료____________________");
    }

    private void shopbySendAuthenticationNumberForId(MemberRequestDto.SendAuthenticationNumberById dto) throws UnirestException, ParseException {
        JSONObject json = new JSONObject();
        json.put("type", "SMS");
        json.put("usage", "FIND_ID");
        json.put("notiAccount", dto.getNotiAccount());
        json.put("memberName", dto.getMemberName());

        HttpResponse<String> response = Unirest.post(shopByUrl + sendAuthenticationNumberUrl)
                .header("accept", acceptHeader)
                .header("version", versionHeader)
                .header("clientid", clientId)
                .header("platform", platformHeader)
                .header("content-type", acceptHeader)
                .body(gson.toJson(json))
                .asString();

        ShopBy.errorMessage(response);
    }

    @Override
    @Transactional
    public void sendAuthenticationNumberForPw(MemberRequestDto.SendAuthenticationNumberByPw dto) throws UnirestException, ParseException {
        log.info("비밀번호 찾기 인증번호 시작____________________" + dto.getUserLoginId());

        Member member = getMember(dto.getUserLoginId());
        int sendCount = 1;
        boolean existAuthentication = authenticationRepository.existsByMobileNoAndMemberName(member.getMobileNo(), member.getMemberName());

        if (!existAuthentication) {
            Authentication authentication = Authentication.builder()
                    .member(member)
                    .sendCount(sendCount)
                    .build();
            authenticationRepository.save(authentication);
        } else {
            AuthenticationResponseDto authenticationResponseDto = authenticationRepository.findByMobileNoAndMebmerName(member.getMobileNo(), member.getMemberName());
            Optional<Authentication> memberAuthentication = authenticationRepository.findById(authenticationResponseDto.getId());
            if (memberAuthentication.isPresent()) {
                if (authenticationResponseDto.getSendCount() >= 5) {
                    throw new ErrorCustomException(ErrorCode.OVER_SEND_AUTHENTICATION);
                } else {
                    log.info("인증번호 보내기 샵바이 S______");
                    shopbySendAuthenticationNumberForPw(member.getMobileNo(), member.getMemberName());
                    log.info("인증번호 보내기 샵바이 E______");
                    sendCount = authenticationResponseDto.getSendCount() + 1;
                    memberAuthentication.get().updateSendCount(sendCount);
                }
            } else {
                throw new ErrorCustomException(ErrorCode.CALL_CUSTOMER_INFORMATION);
            }
        }
        log.info("비밀번호 찾기 인증번호 종료____________________");

    }

    private void shopbySendAuthenticationNumberForPw(String mobileNo, String memberName) throws UnirestException, ParseException {
        JSONObject json = new JSONObject();
        json.put("type", "SMS");
        json.put("usage", "FIND_PASSWORD");
        json.put("notiAccount", mobileNo);
        json.put("memberName", memberName);

        HttpResponse<String> response = Unirest.post(shopByUrl + sendAuthenticationNumberUrl)
                .header("accept", acceptHeader)
                .header("version", versionHeader)
                .header("clientid", clientId)
                .header("platform", platformHeader)
                .header("content-type", acceptHeader)
                .body(gson.toJson(json))
                .asString();

        ShopBy.errorMessage(response);
    }

    @Override
    @Transactional
    public MemberResponseDto.MemberFindId findMemberIdAfterAuthenticationNumber(MemberRequestDto.FindMemberId dto) throws UnirestException, ParseException {

        log.info("인증번호 검증 샵바이 시작 ___________S");
        authenticationsCheckById(dto);
        log.info("인증번호 검증 샵바이 시작 ___________E");

        String userLoginId = memberRepository.findByMobileNoAndMemberNameAndWithdrawal(dto.getPhoneNumber(), dto.getMemberName(), false)
                .map(Member::getMemberLoginId)
                .orElseThrow(() -> new ErrorCustomException(ErrorCode.MEMBER_NOT_FOUND));

        AuthenticationResponseDto authenticationResponseDto = authenticationRepository.findByMobileNoAndMebmerName(dto.getPhoneNumber(), dto.getMemberName());
        authenticationRepository.deleteById(authenticationResponseDto.getId());

        return new MemberResponseDto.MemberFindId(userLoginId);
    }

    @Override
    @Transactional
    public void updateMemberPw(MemberRequestDto.UpdateMemberPw dto) throws UnirestException, ParseException {

        Member member = memberRepository.findByMemberLoginIdAndWithdrawal(dto.getUserLoginId(), false)
                .orElseThrow(() -> new ErrorCustomException(ErrorCode.NO_USER_ERROR));

        shopbyCheckMemberPwUpdate(dto);

        member.updateMemberPassword(passwordEncoder.encode(dto.getNewPassword()));
    }

    @Override
    @Transactional
    public HashMap<String, Object> checkedMemberToken(UserDetailsImpl userDetails) throws UnirestException, ParseException {
        Member member = getMember(userDetails);
        Optional<Survey> survey = surveyRepository.findByMemberId(userDetails.getMember().getId());
        boolean isSurvey = true;
        if (survey.isPresent()) {
            if (survey.get().getPreference().isBlank()) {
                isSurvey = false;
            }
        }
        log.info("token check______s");
        authenticationsCheckByToken(member.getShopByAccessToken());
        log.info("token check______e");

        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("isSurvey", isSurvey);
        // TODO: 2023/11/14 memberNo response 추가

        if (!notificationAgreeRepository.existsByMemberId(member.getId())) {
            notificationAgreeRepository.save(createNotificationAgree(member));
        }
        return resultMap;
    }

    private NotificationAgree createNotificationAgree(Member saveMember) {
        return NotificationAgree.builder()
                .isFirst(true)
                .smsAgreed(false)
                .directMailAgreed(false)
                .member(saveMember)
                .build();
    }

    @Override
    @Transactional
    public void findMemberPw(MemberRequestDto.FindMemberPw dto) throws UnirestException, ParseException {

        Member member = memberRepository.findByMobileNoAndMemberLoginIdAndWithdrawal(dto.getPhoneNumber(), dto.getUserLoginId(), false)
                .orElseThrow(() -> new ErrorCustomException(ErrorCode.MEMBER_NOT_FOUND));

        authenticationsCheckByPw(dto.getPhoneNumber(), dto.getCertificatedNumber());

        AuthenticationResponseDto authenticationResponseDto = authenticationRepository.findByMobileNoAndMebmerName(member.getMobileNo(), member.getMemberName());
        authenticationRepository.deleteById(authenticationResponseDto.getId());
    }

    @Override
    @Transactional
    public void deleteAuthenticationSendCount() {
        // 1일 5회 기준이라 매일 자정 인증관련 저장된 데이터 삭제
        authenticationRepository.deleteAll();
    }

    private Member getMember(UserDetailsImpl userDetails) {
        return memberRepository.findById(userDetails.getMember().getId())
                .orElseThrow(() -> new ErrorCustomException(ErrorCode.NO_USER_ERROR));
    }

    private Member getMember(String memberId) {
        return memberRepository.findByMemberLoginIdAndWithdrawal(memberId, false)
                .orElseThrow(() -> new ErrorCustomException(ErrorCode.NO_USER_ERROR));
    }

    private void authenticationsCheckByToken(String shopByAccessToken) throws UnirestException, ParseException {

        HttpResponse<String> response = Unirest.get(shopByUrl + "/openid/token")
                .header("version", versionHeader)
                .header("clientid", clientId)
                .header("accesstoken", shopByAccessToken)
                .header("platform", platformHeader)
                .asString();

        ShopBy.errorMessage(response);
    }

    private void authenticationsCheckById(MemberRequestDto.FindMemberId dto) throws UnirestException, ParseException {

        HttpResponse<String> response = Unirest.get(shopByUrl + sendAuthenticationNumberUrl
                        + "?type=SMS"
                        + "&usage=FIND_ID"
                        + "&certificatedNumber=" + dto.getCertificatedNumber()
                        + "&notiAccount=" + dto.getPhoneNumber())
                .header("accept", acceptHeader)
                .header("version", versionHeader)
                .header("clientid", clientId)
                .header("platform", platformHeader)
                .header("content-type", acceptHeader)
                .asString();
        if (response.getStatus() != 200) {
            ShopBy.errorMessage(response);
        }
    }

    private void authenticationsCheckByPw(String phoneNumber, String certificatedNumber) throws UnirestException, ParseException {
        HttpResponse<String> response = Unirest.get(shopByUrl + sendAuthenticationNumberUrl
                        + "?type=SMS"
                        + "&usage=FIND_PASSWORD"
                        + "&certificatedNumber=" + certificatedNumber
                        + "&notiAccount=" + phoneNumber)
                .header("accept", acceptHeader)
                .header("version", versionHeader)
                .header("clientid", clientId)
                .header("platform", platformHeader)
                .header("content-type", acceptHeader)
                .asString();

        if (response.getStatus() != 200) {
            ShopBy.errorMessage(response);
        }
    }

    private void shopbyCheckMemberPwUpdate(MemberRequestDto.UpdateMemberPw dto) throws UnirestException, ParseException {
        JSONObject json = new JSONObject();
        json.put("type", "SMS");
        json.put("usage", "FIND_PASSWORD");
        json.put("certificationNumber", dto.getCertificatedNumber());
        json.put("memberId", dto.getUserLoginId());
        json.put("newPassword", dto.getNewPassword());

        HttpResponse<String> response = Unirest.put(shopByUrl + checkMemberUrl + updatePasswordCertificatedBySms)
                .header("accept", acceptHeader)
                .header("version", versionHeader)
                .header("clientid", clientId)
                .header("platform", platformHeader)
                .header("content-type", acceptHeader)
                .body(gson.toJson(json))
                .asString();

        if (response.getStatus() != 204) {
            ShopBy.errorMessage(response);
        }
    }
}
