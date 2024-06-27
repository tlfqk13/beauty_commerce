package com.example.sampleroad.service;

import com.example.sampleroad.common.utils.CartCount;
import com.example.sampleroad.domain.banner.BannerSectionType;
import com.example.sampleroad.dto.response.banner.BannerResponseDto;
import com.example.sampleroad.dto.response.coupon.CouponResponseDto;
import com.example.sampleroad.dto.response.member.MyPageResponseDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.example.sampleroad.repository.banner.BannerRepository;
import com.example.sampleroad.repository.cart.CartRepository;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MyPageServiceImpl implements MyPageService {

    private final CartRepository cartRepository;
    private final CartCount cartCount;
    private final BannerRepository bannerRepository;
    private final CouponService couponService;

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


    @Override
    public MyPageResponseDto getMyPagePointAndCoupon(UserDetailsImpl userDetails) throws UnirestException, ParseException {
        String accessToken = userDetails.getMember().getShopByAccessToken();
        MyPageResponseDto.Point shopByPoint = getShopByPoint(accessToken);
        Boolean isHaveCoupon = getShopByCoupon(userDetails);
        BannerResponseDto bannerResponseDto = getBannerResponseDto();

        boolean hasCart = cartRepository.existsByMemberId(userDetails.getMember().getId());

        return new MyPageResponseDto(hasCart, shopByPoint, isHaveCoupon, bannerResponseDto, "");
    }

    /**
     * 마이페이지에서 쿠폰 있는지 없는지 조회
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/10/20
     **/
    private Boolean getShopByCoupon(UserDetailsImpl userDetails) throws UnirestException, ParseException {
        CouponResponseDto.Coupon coupon = couponService.getAllMyCoupons(userDetails, "true");
        int totalCount = coupon.getTotalCount();
        return totalCount > 0;
    }

    private MyPageResponseDto.Point getShopByPoint(String accessToken) throws UnirestException {
        JSONObject responseJson = sendShopByGetRequest(accessToken).getObject();
        //Integer availablePointAmt = responseJson.getInt("totalAvailableAmt"); // 사용 가능 포인트
        Integer availablePointAmt = 0; // 사용 가능 포인트
        return new MyPageResponseDto.Point(availablePointAmt);
    }

    private BannerResponseDto getBannerResponseDto() {
        List<BannerSectionType> bannerSectionTypes = new ArrayList<>();
        bannerSectionTypes.add(BannerSectionType.MYPAGE);
        List<BannerResponseDto.BannerInfoQueryDto> banners = bannerRepository.findByBannerSection(true, bannerSectionTypes);

        double heightRatio = 60.0;
        double widthRatio = 340.0;

        List<BannerResponseDto.BannerInfoDto> bannerList = banners.stream()
                .map(banner -> new BannerResponseDto.BannerInfoDto(
                        banner.getBannerSectionType(),
                        banner.getBannerId(), banner.getBannerName(),
                        banner.getImageUrl(), 0,
                        false,
                        banner.getBannerType(),
                        banner.getBannerKeyStr()))
                .collect(Collectors.toList());

        return new BannerResponseDto(heightRatio, widthRatio, bannerList);
    }

    private JsonNode sendShopByGetRequest(String accessToken) throws UnirestException {
        return Unirest.get(shopByUrl + "/profile/accumulations/summary")
                .header("accept", acceptHeader)
                .header("version", versionHeader)
                .header("accesstoken", accessToken)
                .header("clientid", clientId)
                .header("platform", platformHeader)
                .asJson()
                .getBody();
    }
}