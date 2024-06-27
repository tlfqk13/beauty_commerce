package com.example.sampleroad.service;

import com.example.sampleroad.common.exception.ErrorCode;
import com.example.sampleroad.common.exception.ErrorCustomException;
import com.example.sampleroad.domain.banner.*;
import com.example.sampleroad.domain.lotto.Lotto;
import com.example.sampleroad.domain.lotto.LottoMember;
import com.example.sampleroad.domain.order.OrderStatus;
import com.example.sampleroad.dto.response.banner.BannerDetailResponseDto;
import com.example.sampleroad.dto.response.banner.BannerResponseDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.example.sampleroad.repository.banner.BannerDetailImageRepository;
import com.example.sampleroad.repository.banner.BannerDetailRepository;
import com.example.sampleroad.repository.banner.BannerRepository;
import com.example.sampleroad.repository.lotto.LottoMemberRepository;
import com.example.sampleroad.repository.lotto.LottoRepository;
import com.example.sampleroad.repository.orders.OrdersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class BannerService {

    private final BannerRepository bannerRepository;
    private final BannerDetailRepository bannerDetailRepository;
    private final BannerDetailImageRepository bannerDetailImageRepository;
    private final LottoRepository lottoRepository;
    private final LottoMemberRepository lottoMemberRepository;
    private final OrdersRepository ordersRepository;

    /**
     * 배너 리스트 페이지 조회
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/10/31
     **/

    public BannerResponseDto getBannerList() {
        List<BannerSectionType> bannerSectionTypes = new ArrayList<>();
        bannerSectionTypes.add(BannerSectionType.HOME);
        return getBanners(bannerSectionTypes);
    }

    public BannerResponseDto getHomeBannerList() {
        List<BannerSectionType> bannerSectionTypes = new ArrayList<>();
        bannerSectionTypes.add(BannerSectionType.HOME);
        bannerSectionTypes.add(BannerSectionType.HOME_MIDDLE);
        bannerSectionTypes.add(BannerSectionType.SURVEY_BANNER);

        return getBanners(bannerSectionTypes);
    }


    public BannerResponseDto getHomeBannerList(BannerSectionType bannerSectionType) {
        List<BannerSectionType> bannerSectionTypes = Collections.singletonList(bannerSectionType);
        return getBanners(bannerSectionTypes);
    }

    private BannerResponseDto getBanners(List<BannerSectionType> bannerSectionTypes) {
        List<BannerResponseDto.BannerInfoQueryDto> bannerList = bannerRepository.findByBannerSection(true, bannerSectionTypes);
        List<BannerResponseDto.BannerInfoDto> bannerInfoDtoList = new ArrayList<>();

        double heightRatio = 240.0;
        double widthRatio = 390.0;

        for (BannerResponseDto.BannerInfoQueryDto bannerInfo : bannerList) {
            Long bannerId = bannerInfo.getBannerId();
            String bannerName = bannerInfo.getBannerName();
            String imageUrl = bannerInfo.getImageUrl();
            int bannerKeyNo = bannerInfo.getBannerKeyNo() != 0 ? bannerInfo.getBannerKeyNo() : 0;
            boolean isMoveBannerDetail = bannerInfo.getIsMoveBannerDetail();
            heightRatio = bannerInfo.getHeightRatio();
            widthRatio = bannerInfo.getWidthRatio();
            String bannerKeyStr = bannerInfo.getBannerKeyStr();
            BannerType bannerType = bannerInfo.getBannerType();

            BannerResponseDto.BannerInfoDto bannerInfoDto = new BannerResponseDto.BannerInfoDto(
                    bannerInfo.getBannerSectionType(),
                    bannerId, bannerName, imageUrl,
                    bannerKeyNo, isMoveBannerDetail, bannerType,
                    bannerKeyStr
            );
            bannerInfoDtoList.add(bannerInfoDto);
        }

        return new BannerResponseDto(heightRatio, widthRatio, bannerInfoDtoList);
    }

    /**
     * 배너 상세 API
     * 배너 id로 배너 상세 페이지 img 조회
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/10/24
     **/
    @Transactional
    public BannerDetailResponseDto.BannerDetailResponse getBannerDetail(Long bannerId) {
        Banner banner = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new ErrorCustomException(ErrorCode.BANNER_NOT_FOUND));

        BannerType bannerType = banner.getBannerType();

        List<BannerDetailImage> bannerDetailImageList = bannerDetailImageRepository.findByBanner_Id(bannerId);
        List<String> bannerDetailImages = bannerDetailImageList.stream()
                .map(BannerDetailImage::getBannerDetailImg)
                .collect(Collectors.toList());

        BannerDetailResponseDto.BannerDetailResponse bannerDetailResponse = null;

        if (bannerType == BannerType.COUPON_INSERT || bannerType == BannerType.NOTICE_IN || bannerType == BannerType.COUPON_LIST ||
                bannerType == BannerType.REVIEW || bannerType == BannerType.SETTING || bannerType == BannerType.EVENT_OUT ||
                bannerType == BannerType.GROUP_PURCHASE) {
            bannerDetailResponse = getBannerDetail(bannerType, bannerId);
        }
        banner.updateBannerViewCount();

        if (bannerDetailResponse != null) {
            return new BannerDetailResponseDto.BannerDetailResponse(bannerDetailResponse, bannerDetailImages);
        } else {
            // 기본 생성자를 반환
            return new BannerDetailResponseDto.BannerDetailResponse();
        }
    }

    private BannerDetailResponseDto.BannerDetailResponse getBannerDetail(BannerType bannerType, Long bannerId) {
        BannerDetailResponseDto.BannerDetailResponse bannerDetailResponse = null;

        switch (bannerType) {
            case COUPON_INSERT:
                bannerDetailResponse = bannerDetailRepository.findBannerCouponDetailByBannerId(bannerType, bannerId);
                break;
            case PRODUCT_DETAIL:
                bannerDetailResponse = bannerDetailRepository.findBannerProductDetailByBannerId(bannerType, bannerId);
                break;
            case NOTICE_IN:
                bannerDetailResponse = bannerDetailRepository.findBannerNoticeDetailByBannerId(bannerType, bannerId);
                break;
            case CATEGORY_DETAIL:
                bannerDetailResponse = bannerDetailRepository.findBannerCategoryDetailByBannerId(bannerType, bannerId);
                break;
            case COUPON_LIST:
            case REVIEW:
            case SETTING:
            case EVENT_OUT:
            case GROUP_PURCHASE:
                bannerDetailResponse = bannerDetailRepository.findBannerOtherDetailByBannerId(bannerType, bannerId);
            default:
                // 기본 처리
                break;
        }

        return bannerDetailResponse;
    }

    @Transactional(noRollbackFor = ErrorCustomException.class)
    public void processBannerDetailAndLotto(int bannerKeyNo, UserDetailsImpl userDetails) {

        Optional<BannerDetail> bannerDetail = bannerDetailRepository.findByBannerKeyNo(bannerKeyNo);
        if (bannerDetail.isEmpty()) {
            return;
        }

        Optional<Lotto> lotto = lottoRepository.findByLottoKeyNo(bannerKeyNo);
        if (lotto.isEmpty()) {
            return;
        }

        if (bannerKeyNo == 10001) {
            processLunaYearEventLottoMember(lotto.get(), userDetails);
        } else {
            processLottoMember(lotto.get(), userDetails);
        }

    }

    private void processLottoMember(Lotto lotto, UserDetailsImpl userDetails) {
        Optional<LottoMember> lottoMember = lottoMemberRepository
                .findByLotto_LottoKeyNoAndMemberIdAndLotto_Id(lotto.getLottoKeyNo(), userDetails.getMember().getId(),lotto.getId());
        if (lottoMember.isEmpty()) {
            createNewLottoMember(lotto, userDetails);
        } else {
            LocalDateTime modifiedTime = lottoMember.get().getModifiedAt();
            LocalDate modifiedAt = modifiedTime.toLocalDate();
            LocalDate today = LocalDate.now(ZoneId.systemDefault());
            if (modifiedAt.isEqual(today)) {
                throw new ErrorCustomException(ErrorCode.TODAY_ALREADY_REGISTER_LOTTO);
            }
            lottoMember.get().updateApplyCount();
            lottoMemberRepository.save(lottoMember.get());
        }
    }

    private void processLunaYearEventLottoMember(Lotto lotto, UserDetailsImpl userDetails) {

        Optional<LottoMember> lottoMember = lottoMemberRepository
                .findByLotto_LottoKeyNoAndMemberIdAndLotto_Id(lotto.getLottoKeyNo(), userDetails.getMember().getId(),lotto.getId());

        // TODO: 2/1/24 이벤트 기간안에 구매이력있는지
        LocalDateTime eventStartDate = LocalDateTime.of(2024, 2, 2, 10, 0);
        LocalDateTime eventEndDate = LocalDateTime.of(2024, 2, 12, 0, 0);
        boolean firstPurchase = getFirstPurchase(userDetails, eventStartDate, eventEndDate);
        if (firstPurchase) {
            // TODO: 2/1/24 구매내역이 있으면
            if (lottoMember.isEmpty()) {
                // TODO: 2/1/24 참여이력 없으면 응모가 완료되었습니다
                createNewLottoMember(lotto, userDetails);
            } else {
                // TODO: 2/1/24 참여이력 있으면 -> 이미 참여하신 이벤트입니다
                throw new ErrorCustomException(ErrorCode.ALREADY_REGISTER_LOTTO);
            }
        } else {
            // TODO: 2/1/24 이벤트 응모 대상자가 아닙니다
            throw new ErrorCustomException(ErrorCode.DO_NOT_MEMBER_REGISTER_LOTTO);
        }
    }

    private void createNewLottoMember(Lotto lotto, UserDetailsImpl userDetails) {
        LottoMember newLottoMember = LottoMember.builder()
                .member(userDetails.getMember())
                .lotto(lotto)
                .build();
        lottoMemberRepository.save(newLottoMember);
    }

    /**
     * 첫 구매 대상인지 조회
     *
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/12/18
     */

    public boolean getFirstPurchase(UserDetailsImpl userDetails, LocalDateTime eventStartDate, LocalDateTime eventEndDate) {
        List<OrderStatus> orderStatusList = Arrays.asList(OrderStatus.PAY_DONE,OrderStatus.BUY_CONFIRM);
        return ordersRepository.existsByMemberIdAndOrderStatus(userDetails.getMember().getId(), orderStatusList, eventStartDate, eventEndDate);
    }

}
