package com.example.sampleroad.service;

import com.example.sampleroad.common.exception.ErrorCode;
import com.example.sampleroad.common.exception.ErrorCustomException;
import com.example.sampleroad.domain.experience.Experience;
import com.example.sampleroad.domain.experience.ExperienceDetailImage;
import com.example.sampleroad.domain.experience.ExperienceMember;
import com.example.sampleroad.domain.experience.ExperienceStatus;
import com.example.sampleroad.domain.member.Member;
import com.example.sampleroad.domain.product.Product;
import com.example.sampleroad.dto.request.ExperienceRequestDto;
import com.example.sampleroad.dto.response.experience.ExperienceResponseDto;
import com.example.sampleroad.dto.response.experience.ExperienceResponseQueryDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.example.sampleroad.repository.experience.ExperienceDetailImageRepository;
import com.example.sampleroad.repository.experience.ExperienceMemberRepository;
import com.example.sampleroad.repository.experience.ExperienceRepository;
import com.example.sampleroad.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ExperiencePartyService {

    private final ExperienceRepository experienceRepository;
    private final ExperienceMemberRepository experienceMemberRepository;
    private final ExperienceDetailImageRepository experienceDetailImageRepository;
    private final MemberRepository memberRepository;
    private final ProductService productService;

    public ExperienceResponseDto getExperienceList(UserDetailsImpl userDetails, int pageNumber, int pageSize, String isMyExperience, ExperienceStatus experienceStatus) {

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<ExperienceResponseQueryDto.ExperienceInfo> experiencePage;
        LocalDateTime now = LocalDateTime.now();
        Long memberId = userDetails.getMember().getId();

        if (Boolean.parseBoolean(isMyExperience)) {
            // 내가 신청했으면
            List<ExperienceStatus> experienceStatusList = buildExperienceStatusList(experienceStatus);
            // 내가 신청한 체험단의 내 신청 상태와 체험단 자체의 상태를 같이 파라미터로 넘긴다
            experiencePage = experienceRepository.findExperience(pageable, memberId, experienceStatusList);
        } else {
            // 내가 신청안한 전체 체험단 리스트 조회이니깐 체험단 자체 상태로 조건걸면 된다
            experiencePage = experienceRepository.findExperience(pageable, experienceStatus);
        }

        List<Long> experienceIds = experiencePage.stream().map(ExperienceResponseQueryDto.ExperienceInfo::getExperienceId).collect(Collectors.toList());
        List<ExperienceResponseQueryDto.ExperienceMemberInfo> experienceMembers = experienceMemberRepository.findExperienceMemberByMemberId(memberId, experienceIds);

        // 내가 신청한 체험단 + 그 체험단에서의 나의 신청상태 map
        Map<Long, ExperienceStatus> statusMap = experienceMembers.stream()
                .collect(Collectors.toMap(ExperienceResponseQueryDto.ExperienceMemberInfo::getExperienceId, ExperienceResponseQueryDto.ExperienceMemberInfo::getMemberExperienceStatus));

        List<ExperienceResponseDto.ExperienceInfo> experienceListResponse = experiencePage.getContent().stream()
                .map(experience -> {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
                    String startTime = formatDateTime(experience.getExperienceStartTime(), formatter);
                    String finishTime = formatDateTime(experience.getExperienceFinishTime(), formatter);

                    // Use the status from the experienceMembers if available, otherwise use the default
                    ExperienceStatus adjustedStatus = statusMap.getOrDefault(
                            experience.getExperienceId(),
                            experience.getExperienceStatus()
                    );

                    // true = 내가 신청한 체험단이다
                    boolean isStatusPresent = statusMap.containsKey(experience.getExperienceId());

                    // isStatusPresent = true 면 내가 신청한
                    // 내가 신청했고 조회 필터가 모집 종료로 넘어온 경우
                    if (Boolean.parseBoolean(isMyExperience) && ExperienceStatus.EXPIRE_EXPERIENCE.equals(experience.getExperienceStatus())) {
                        // TODO: 2023/12/08 당첨자 발표 확인일 칼같이 지켜라
                        if (now.isBefore(experience.getWinnerNoticeTime())) {
                            // 내가 신청했고 조회 필터가 모집 종료인데 당첨자 발표일 이전이면
                            adjustedStatus = ExperienceStatus.FINISH_SUBMIT;
                        } else {
                            // 내가 신청했고 조회 필터가 모집 종료인데 당첨자 발표일 이후이면
                            adjustedStatus = ExperienceStatus.CHECK_EXPERIENCE;
                        }
                    }

                    // 내가 신청안했고 조회 필터가 모집 종료로 넘어온 경우
                    if (!isStatusPresent && ExperienceStatus.EXPIRE_EXPERIENCE.equals(experience.getExperienceStatus())) {
                        adjustedStatus = ExperienceStatus.EXPIRE_EXPERIENCE;
                        startTime = "모집 종료";
                        finishTime = null;
                    }

                    if (isStatusPresent) {
                        if (now.isBefore(experience.getWinnerNoticeTime())) {
                            // 내가 신청했고 조회 필터가 모집 종료인데 당첨자 발표일 이전이면
                            adjustedStatus = ExperienceStatus.FINISH_SUBMIT;
                        } else {
                            // 내가 신청했고 조회 필터가 모집 종료인데 당첨자 발표일 이후이면
                            adjustedStatus = ExperienceStatus.CHECK_EXPERIENCE;
                        }
                    }

                    return new ExperienceResponseDto.ExperienceInfo(experience.getExperienceId(), experience.getImageUrl(),
                            startTime, finishTime, adjustedStatus, isStatusPresent);
                })
                .collect(Collectors.toList());

        return new ExperienceResponseDto(experienceListResponse, experiencePage.getTotalElements());
    }

    private List<ExperienceStatus> buildExperienceStatusList(ExperienceStatus experienceStatus) {
        switch (experienceStatus) {
            case ING_EXPERIENCE:
                return List.of(ExperienceStatus.FINISH_SUBMIT, ExperienceStatus.ING_EXPERIENCE);
            case EXPIRE_EXPERIENCE:
                return List.of(ExperienceStatus.CHECK_EXPERIENCE, ExperienceStatus.EXPIRE_EXPERIENCE);
            default:
                return List.of(experienceStatus);
        }
    }

    public ExperienceResponseDto.ExperienceDetail getExperienceDetail(UserDetailsImpl userDetails, Long experienceId) {
        Experience experience = getExperience(experienceId);
        Optional<ExperienceMember> experienceMember = experienceMemberRepository.findByExperienceIdAndMemberId(experienceId, userDetails.getMember().getId());
        boolean isMyExperience = experienceMember.isPresent();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
        String startTime = formatDateTime(experience.getExperienceStartTime(), formatter);
        String finishTime = formatDateTime(experience.getExperienceFinishTime(), formatter);
        String noticeTime = formatDateTime(experience.getWinnerNoticeTime(), formatter);

        List<ExperienceDetailImage> detailImageUrls = experienceDetailImageRepository.findExperienceDetailImageByExperienceId(experience.getId());
        List<String> experienceDetailImgUrls = detailImageUrls.stream()
                .map(ExperienceDetailImage::getImageUrl)
                .collect(Collectors.toList());

        String appProductImageUrl = null;
        if (experience.getProductNo() != null) {
            Product product = productService.getProduct(experience.getProductNo());
            appProductImageUrl = product.getImgUrl();
        }

        // 모집중인지 아닌지만 내려준다
        ExperienceStatus experienceStatus;
        LocalDateTime now = LocalDateTime.now();

        if (isMyExperience) {
            if (now.isBefore(experience.getWinnerNoticeTime())) {
                experienceStatus = ExperienceStatus.ING_EXPERIENCE;
            } else {
                experienceStatus = ExperienceStatus.EXPIRE_EXPERIENCE;
            }
        } else {
            if (now.isBefore(experience.getExperienceFinishTime())) {
                experienceStatus = ExperienceStatus.ING_EXPERIENCE;
            } else {
                experienceStatus = ExperienceStatus.EXPIRE_EXPERIENCE;
            }
        }

        return new ExperienceResponseDto.ExperienceDetail(
                experience.getId(), isMyExperience,
                experience.getImageUrl(), experienceDetailImgUrls,
                experience.getContent(),
                experience.getBrandName(), experience.getProductName(),
                experience.getProductNo(), appProductImageUrl,
                startTime, finishTime, noticeTime,
                experience.getLimitRegisterMember(),
                experienceStatus,
                experience.getOfferTarget(),
                experience.getSnsInfoPlaceHolder()
        );
    }

    private String formatDateTime(LocalDateTime dateTime, DateTimeFormatter formatter) {
        return dateTime != null ? dateTime.format(formatter) : null;
    }

    @Transactional
    public void addExperience(UserDetailsImpl userDetails, ExperienceRequestDto dto) {

        Member member = getMember(userDetails);

        Experience experience = getExperience(dto.getExperienceId());

        boolean isExistsExperience = experienceMemberRepository.existsByMemberIdAndExperienceId(userDetails.getMember().getId(), dto.getExperienceId());

        if (isExistsExperience) {
            throw new ErrorCustomException(ErrorCode.ALREADY_REGISTER_EXPERIENCE);
        }

        // TODO: 2023/12/07 모집인원 초과 에러

        // TODO: 2023/12/07 이미 종료된 체험단 신청할 경우
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(experience.getExperienceFinishTime())) {
            throw new ErrorCustomException(ErrorCode.ALREADY_FINISH_EXPERIENCE);
        }

        ExperienceMember experienceMember = ExperienceMember.builder()
                .member(member)
                .experience(experience)
                .snsAccountInfo(dto.getSnsAccountInfo())
                .receiverName(dto.getReceiverName())
                .receiverContact(dto.getReceiverContact())
                .receiverZipCode(dto.getReceiverZipCode())
                .receiverAddress(dto.getReceiverAddress())
                .receiverDetailAddress(dto.getReceiverDetailAddress())
                .build();

        try {
            experienceMemberRepository.save(experienceMember);
        } catch (Exception e) {
            log.info("체험단 신청 에러 ___________________________________S");
            throw new ErrorCustomException(ErrorCode.CALL_CUSTOMER_INFORMATION);
        }
    }

    public ExperienceResponseDto.ExperienceRegisterCheck getRegisterCheckExperience(UserDetailsImpl userDetails, Long experienceId) {
        Optional<ExperienceMember> experienceMember = experienceMemberRepository.findByExperienceIdAndMemberId(experienceId, userDetails.getMember().getId());
        if (experienceMember.isPresent()) {
            LocalDateTime winnersNoticeTime = experienceMember.get().getExperience().getWinnerNoticeTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
            String winnersNoticeTimeStr = formatDateTime(winnersNoticeTime, formatter);
            return new ExperienceResponseDto.ExperienceRegisterCheck(experienceMember.get(), winnersNoticeTimeStr);
        } else {
            throw new ErrorCustomException(ErrorCode.DO_NOT_REGISTER_EXPERIENCE);
        }
    }

    public HashMap<String, Object> getExperienceWinner(UserDetailsImpl userDetails, Long experienceId) {
        // 신청내역이 없어도 false 라서 이게 가장 좋은 방식인가?
        boolean isWinner = experienceMemberRepository.existsByMemberIdAndExperienceIdAndIsWinner(userDetails.getMember().getId(), experienceId, true);
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("isWinner", isWinner);
        return resultMap;
    }

    private Experience getExperience(Long experienceId) {
        return experienceRepository.findById(experienceId)
                .orElseThrow(() -> new ErrorCustomException(ErrorCode.EXPERIENCE_NOT_FOUND));
    }

    private Member getMember(UserDetailsImpl userDetails) {
        return memberRepository.findById(userDetails.getMember().getId())
                .orElseThrow(() -> new ErrorCustomException(ErrorCode.NO_USER_ERROR));
    }
}
