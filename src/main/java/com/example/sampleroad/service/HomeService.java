package com.example.sampleroad.service;

import com.example.sampleroad.common.utils.CustomValue;
import com.example.sampleroad.domain.CategoryType;
import com.example.sampleroad.domain.display.DisplayType;
import com.example.sampleroad.domain.home.HomeSectionType;
import com.example.sampleroad.domain.home.MoveCase;
import com.example.sampleroad.domain.member.Member;
import com.example.sampleroad.domain.product.EventProductType;
import com.example.sampleroad.domain.product.HomeProductType;
import com.example.sampleroad.domain.push.NotificationAgree;
import com.example.sampleroad.domain.search.SearchSortType;
import com.example.sampleroad.dto.response.BestSellerResponseDto;
import com.example.sampleroad.dto.response.banner.BannerResponseDto;
import com.example.sampleroad.dto.response.display.DisplaySectionResponseDto;
import com.example.sampleroad.dto.response.grouppurchase.GroupPurchaseResponseDto;
import com.example.sampleroad.dto.response.home.HomeResponseDto;
import com.example.sampleroad.dto.response.product.CustomKitResponseDto;
import com.example.sampleroad.dto.response.product.EventProductQueryDto;
import com.example.sampleroad.dto.response.product.EventProductResponseDto;
import com.example.sampleroad.dto.response.push.NotificationResponseDto;
import com.example.sampleroad.dto.response.push.NotificationResponseQueryDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.example.sampleroad.repository.cart.CartRepository;
import com.example.sampleroad.repository.notification.NotificationAgreeRepository;
import com.example.sampleroad.repository.product.EventProductRepository;
import com.google.gson.Gson;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HomeService {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final CustomKitService customKitService;
    private final BannerService bannerService;
    private final PopUpService popUpService;
    private final DisplayService displayService;
    private final ZeroExperienceReviewService zeroExperienceReviewService;
    private final GroupPurchaseService groupPurchaseService;
    private final ProductShopByService productShopByService;
    private final OrderService orderService;
    private final WishListService wishListService;
    private final EventProductRepository eventProductRepository;
    private final NotificationAgreeRepository notificationAgreeRepository;
    private final CartRepository cartRepository;

    @Value("${shop-by.client-id}")
    String clientId;
    @Value("${shop-by.url}")
    String shopByUrl;
    @Value("${shop-by.check-member-url}")
    String profile;
    @Value("${shop-by.orders-url}")
    String ordersUrl;
    @Value("${shop-by.accept-header}")
    String acceptHeader;
    @Value("${shop-by.version-header}")
    String versionHeader;
    Gson gson = new Gson();
    @Value("${shop-by.platform-header}")
    String platformHeader;
    @Value("${shop-by.products}")
    String products;

    @Value("${shop-by.new-category-no}")
    int newCategoryNo;
    @Value("${shop-by.perfume-category-no}")
    int perfumeCategoryNo;
    @Value("${shop-by.weekly-special-category-no}")
    int weeklySpecialCategoryNo;
    @Value("${shop-by.experience-category-no}")
    int experienceCategoryNo;
    @Value("${shop-by.origin-category-no}")
    int originCategoryNo;
    @Value("${shop-by.beauty-item-category-no}")
    int beautyItemCategoryNo;
    @Value("${shop-by.kit-category-no}")
    String kitCategoryNo;

    @Transactional
    public HomeResponseDto getNewHome(UserDetailsImpl userDetails) throws UnirestException, ParseException {

        log.info("\n");
        log.info("getHome logging -----------------------------------------------");
        Long memberId = userDetails.getMember().getId();
        log.info("getHome logging -----------------------------------------------");
        log.info("\n");

        //홈에서 모든 키트 카테고리 no를 내려주는 일
        HomeResponseDto.HomeCategoryList homeCategoryList = categoryService.getHomeCategory();

        // 시스템 알림 및 광고성 푸시 알림 체크
        NotificationResponseDto notificationResponseDto = checkMemberNotification(userDetails);

        // 배너 조회
        BannerResponseDto.getBannerList bannerList = getGetBannerList();

        // 인기 상품 조회
        BestSellerResponseDto.BestSeller bestSeller = productService.getBestSeller(CustomValue.bestSellerSize);

        // 상품 진열 관리로 오늘의 특가 세팅하기
        Map<DisplayType, List<DisplaySectionResponseDto.SectionItem>> displaySectionItems = displayService.getDisplaySectionItems();

        // 첫구매 고객인지 아닌지 true/false
        boolean isFirstPurchase = orderService.getFirstPurchase(userDetails);
        // 팝업 리스트
        HomeResponseDto.PopUpList popUpList = popUpService.getPopUpList(userDetails);

        String instagramUrl = CustomValue.instagramUrl;
        String blogUrl = CustomValue.blogUrl;
        //String facebookUrl = CustomValue.facebookUrl;

        boolean hasZeroExperience = false;
        if (!isFirstPurchase) {
            hasZeroExperience = zeroExperienceReviewService.getZeroExperienceByIsNecessaryFromHome(memberId);
        }

        /*
         * 팀 구매로 같이 싸게 사자!
         * 첫 구매딜
         * 인기 상품 대규모 입점 -> 매주 입고되는 신상품 [NEW]
         * 7일 한정 주가특가 - 최대 89% 할인 가격으로 만나는 한정 특가
         * 겨울 스킨케어 모음전 -> 품절대란 베스트 셀러 [향수]
         * 베스트셀러[백화점 향수] -> 샘플로드 회원들의 PICK!
         * 실시간 인기
         * 0원 샘플 체험
         * */

        List<HomeResponseDto.ProductSectionInfos> productSectionDtoList = new ArrayList<>();
        // TODO: 3/5/24 팀 구매로 같이 싸게 사자!
        if (displaySectionItems.get(DisplayType.GROUP_PURCHASE) != null) {
            List<GroupPurchaseResponseDto.ProductInfo> groupPurchaseProductList = groupPurchaseService.getGroupPurchaseProductList(
                    displaySectionItems.get(DisplayType.GROUP_PURCHASE).get(0).getDisplayNo()
            );
            if (!groupPurchaseProductList.isEmpty()) {
                productSectionDtoList.add(convertGroupPurchaseToProductSectionInfos(groupPurchaseProductList));
            }
        }
        if (isFirstPurchase) {
            productSectionDtoList.add(convertFirstDealToProductSectionInfos(getEventProductList())); // 첫 구매딜
        }
        if (displaySectionItems.get(DisplayType.HOT_DEAL) != null) {
            productSectionDtoList.add(convertDisplayProductSectionInfos(displaySectionItems.get(DisplayType.HOT_DEAL), DisplayType.HOT_DEAL)); // 주간 특가
        }
        if (!bannerList.getHomeSurveyBannerList().getBannerList().isEmpty() && hasZeroExperience) {
            productSectionDtoList.add(this.getHomeMiddleBannerSection(bannerList.getHomeSurveyBannerList())); // 샘플 서베이 띠배너
        }
        productSectionDtoList.add(this.getHomeNewCategoryProductSection(newCategoryNo)); // 매주 입고되는 신상품 [NEW]
        if (!bannerList.getHomeMiddleBannerList().getBannerList().isEmpty()) {
            productSectionDtoList.add(this.getHomeMiddleBannerSection(bannerList.getHomeMiddleBannerList())); // 홈 middle 배너
        }
        productSectionDtoList.add(this.getHomeProductSection(perfumeCategoryNo, HomeProductType.SECTION_3));// 백화점 향수)
        if (displaySectionItems.get(DisplayType.ZERO_EXPERIENCE) != null) {
            int displayNo = displaySectionItems.get(DisplayType.ZERO_EXPERIENCE).get(0).getDisplayNo();
            productSectionDtoList.add(this.getExperienceHomeProductSection(experienceCategoryNo, HomeProductType.EXPERIENCE, displayNo));// 0원 체험존 1)
        }
        productSectionDtoList.add(convertBestSellerToProductSectionInfos(bestSeller, true)); // 실시간 인기 상품
        productSectionDtoList.add(this.getHomeProductSection(beautyItemCategoryNo, HomeProductType.BEAUTY_ITEM));// 최저가 본품 쇼핑 3)
        if (displaySectionItems.get(DisplayType.NORMAL_ITEM_DISPLAY) != null) {
            productSectionDtoList.add(this.convertDisplayProductSectionInfos(displaySectionItems.get(DisplayType.NORMAL_ITEM_DISPLAY), DisplayType.NORMAL_ITEM_DISPLAY)); // 샘플로드 회원들의 PICK!
        }

        boolean hasCart = cartRepository.existsByMemberId(memberId);

        Set<Integer> allProductNos = new HashSet<>();

        for (HomeResponseDto.ProductSectionInfos sectionInfo : productSectionDtoList) {
            Set<Integer> productNos = sectionInfo.getProducts().stream()
                    .map(HomeResponseDto.ProductSectionDto::getProductNo)
                    .collect(Collectors.toSet());
            allProductNos.addAll(productNos);
        }

        Map<Integer, Boolean> wishListMap = wishListService.isInWishlist(memberId, allProductNos);

        for (HomeResponseDto.ProductSectionInfos sectionInfo : productSectionDtoList) {
            if (sectionInfo.getProducts() != null) {
                for (HomeResponseDto.ProductSectionDto product : sectionInfo.getProducts()) {
                    boolean isWishList = wishListMap.getOrDefault(product.getProductNo(), false);
                    product.setIsWishList(isWishList);
                }
            }
        }


        // TODO: 1/30/24 샘플 서베이 띠배너 들어갈수있느지 체크
        if (!bannerList.getHomeSurveyBannerList().getBannerList().isEmpty()) {
            productSectionDtoList.add(this.getHomeSurveyBannerSection(bannerList.getHomeSurveyBannerList())); // 홈 middle 배너
        }

        return new HomeResponseDto(
                bannerList.getHomeBannerList(),
                isFirstPurchase,
                popUpList,
                homeCategoryList,
                productSectionDtoList,
                notificationResponseDto,
                instagramUrl, blogUrl, null,
                hasCart,
                hasZeroExperience);
    }

    private BannerResponseDto.getBannerList getGetBannerList() {
        BannerResponseDto bannerList = bannerService.getHomeBannerList();
        List<BannerResponseDto.BannerInfoDto> homeBanners = new ArrayList<>();
        List<BannerResponseDto.BannerInfoDto> homeMiddleBanners = new ArrayList<>();
        List<BannerResponseDto.BannerInfoDto> surveyBannerBanners = new ArrayList<>();

        for (BannerResponseDto.BannerInfoDto banner : bannerList.getBannerList()) {
            switch (banner.getBannerSectionType()) {
                case HOME:
                    homeBanners.add(banner);
                    break;
                case HOME_MIDDLE:
                    homeMiddleBanners.add(banner);
                    break;
                case SURVEY_BANNER:
                    surveyBannerBanners.add(banner);
                    break;
            }
        }

        double heightRatio = bannerList.getHeightRatio();
        double widthRatio = bannerList.getWidthRatio();

        BannerResponseDto homeBannerList = new BannerResponseDto(heightRatio, widthRatio, homeBanners);
        BannerResponseDto homeMiddleBannerList = new BannerResponseDto(heightRatio, widthRatio, homeMiddleBanners);
        BannerResponseDto homeSurveyBannerList = new BannerResponseDto(heightRatio, widthRatio, surveyBannerBanners);
        return new BannerResponseDto.getBannerList(homeBannerList, homeMiddleBannerList, homeSurveyBannerList);
    }

    private EventProductResponseDto.EventProductList getEventProductList() throws UnirestException {
        List<EventProductResponseDto.EventProductInfo> eventProductInfoList = new ArrayList<>();
        List<EventProductQueryDto.EventProductInfo> eventProductList = eventProductRepository.findEventProductByIsVisible(EventProductType.FIRST_DEAL);
        int[] productNos = eventProductList.stream()
                .mapToInt(EventProductQueryDto.EventProductInfo::getProductNo)
                .toArray();

        Map<Integer, EventProductQueryDto.EventProductInfo> productNoToEventProductMap = eventProductList.stream()
                .collect(Collectors.toMap(EventProductQueryDto.EventProductInfo::getProductNo, Function.identity()));

        List<HomeResponseDto.ProductSectionDto> productSectionInfoList = productShopByService.shopbyGetProductList(productNos);

        for (HomeResponseDto.ProductSectionDto productSectionDto : productSectionInfoList) {
            EventProductResponseDto.EventProductInfo eventProductInfo = createEventProductInfo(productSectionDto, productNoToEventProductMap);
            eventProductInfoList.add(eventProductInfo);
        }

        return new EventProductResponseDto.EventProductList(eventProductInfoList);
    }

    private EventProductResponseDto.EventProductInfo createEventProductInfo(
            HomeResponseDto.ProductSectionDto productSectionDto,
            Map<Integer, EventProductQueryDto.EventProductInfo> productNoToEventProductMap
    ) {
        return new EventProductResponseDto.EventProductInfo(
                productNoToEventProductMap.get(productSectionDto.getProductNo()).getEventTitle(),
                productNoToEventProductMap.get(productSectionDto.getProductNo()).getEventSubTitle(),
                productNoToEventProductMap.get(productSectionDto.getProductNo()).getEventName(),
                productSectionDto.getImageUrl(), productSectionDto.getSalePrice(),
                productNoToEventProductMap.get(productSectionDto.getProductNo()).getEventPrice(),
                productNoToEventProductMap.get(productSectionDto.getProductNo()).getEventFinishTime(),
                productSectionDto.getProductNo(), productSectionDto.getStockCnt(),
                productSectionDto.getProductName(), productSectionDto.getBrandName(),
                productNoToEventProductMap.get(productSectionDto.getProductNo()).getCategoryType()
        );
    }


    private HomeResponseDto.ProductSectionInfos getHomeProductSection(int categoryNo, HomeProductType homeType) throws UnirestException, ParseException {
        int pageNumber = 1;
        int pageSize = 6;
        if (categoryNo == experienceCategoryNo) {
            pageSize = 8;
        }
        String sectionTitle = getSectionTitleByHomeProductType(homeType);
        String sectionSubTitle = getSectionSubTitleByHomeProductType(homeType);

        CustomKitResponseDto customKitResponseDto = customKitService.shopbyGetProductListByCategoryNo(categoryNo, pageNumber, pageSize, SearchSortType.SALE_YMD);
        List<CustomKitResponseDto.CustomKitItemInfo> items = customKitResponseDto.getItem();
        List<HomeResponseDto.ProductSectionDto> productSectionInfoList = new ArrayList<>();

        CategoryType categoryType = CategoryType.SAMPLE;
        int parsedKitCategoryNo = Integer.parseInt(kitCategoryNo);
        for (CustomKitResponseDto.CustomKitItemInfo item : items) {
            if (item.getCategoryNo() == parsedKitCategoryNo) {
                categoryType = CategoryType.KIT;
            }
            if (homeType.equals(HomeProductType.EXPERIENCE)) {
                categoryType = CategoryType.KIT;
            }
            HomeResponseDto.ProductSectionDto dto = new HomeResponseDto.ProductSectionDto(
                    categoryType,
                    item.getProductNo(), item.getProductName(),
                    item.getBrandName(), item.getImgUrl(),
                    item.getViewRating(), item.getTotalReviewCount(),
                    item.getSalePrice(), item.getImmediateDiscountAmt(),
                    item.getStockCnt()
            );
            productSectionInfoList.add(dto);
        }

        Integer categoryNoInteger = categoryNo;
        HomeSectionType homeSectionType =
                (categoryNo == weeklySpecialCategoryNo) ? HomeSectionType.HOT_DEAL :
                        (categoryNo == originCategoryNo) ? HomeSectionType.HORIZONTAL_ITEM_SECTION : HomeSectionType.SIX_ITEM_SECTION;

        MoveCase moveCase = MoveCase.CATEGORY;

        return new HomeResponseDto.ProductSectionInfos(homeSectionType, moveCase, categoryNoInteger,
                sectionTitle, sectionSubTitle, productSectionInfoList, null);
    }

    private HomeResponseDto.ProductSectionInfos getExperienceHomeProductSection(int experienceCategoryNo, HomeProductType homeType, int displayNo) throws UnirestException, ParseException {
        int pageNumber = 1;
        int pageSize = 8;

        String sectionTitle = getSectionTitleByHomeProductType(homeType);
        String sectionSubTitle = getSectionSubTitleByHomeProductType(homeType);

        // TODO: 3/11/24 0원 샘플을 db에서 가져오는게 맞는지;; 
        CustomKitResponseDto customKitResponseDto = customKitService.shopbyGetProductListByCategoryNo(experienceCategoryNo, pageNumber, pageSize, SearchSortType.SALE_YMD);
        List<CustomKitResponseDto.CustomKitItemInfo> items = customKitResponseDto.getItem();
        List<HomeResponseDto.ProductSectionDto> productSectionInfoList = new ArrayList<>();

        CategoryType categoryType = CategoryType.KIT;
        for (CustomKitResponseDto.CustomKitItemInfo item : items) {
            HomeResponseDto.ProductSectionDto dto = new HomeResponseDto.ProductSectionDto(
                    categoryType,
                    item.getProductNo(), item.getProductName(),
                    item.getBrandName(), item.getImgUrl(),
                    item.getViewRating(), item.getTotalReviewCount(),
                    item.getSalePrice(), item.getImmediateDiscountAmt(),
                    item.getStockCnt()
            );
            productSectionInfoList.add(dto);
        }
        Integer categoryNoInteger = displayNo;

        HomeSectionType homeSectionType = HomeSectionType.FREE_ITEM_SECTION;

        MoveCase moveCase = MoveCase.FREE_SAMPLE;

        return new HomeResponseDto.ProductSectionInfos(homeSectionType, moveCase, categoryNoInteger,
                sectionTitle, sectionSubTitle, productSectionInfoList, null);
    }

    private HomeResponseDto.ProductSectionInfos getHomeNewCategoryProductSection(int categoryNo) throws UnirestException, ParseException {
        int pageNumber = 1;
        int pageSize = 6;
        if (categoryNo == experienceCategoryNo) {
            pageSize = 20;
        }
        String sectionTitle = getSectionTitleByHomeProductType(HomeProductType.SECTION_1);
        String sectionSubTitle = getSectionSubTitleByHomeProductType(HomeProductType.SECTION_1);

        CustomKitResponseDto customKitResponseDto = customKitService.shopbyGetProductListByCategoryNo(categoryNo, pageNumber, pageSize, SearchSortType.SALE_YMD);
        List<CustomKitResponseDto.CustomKitItemInfo> items = customKitResponseDto.getItem();
        List<HomeResponseDto.ProductSectionDto> productSectionInfoList = new ArrayList<>();

        CategoryType categoryType = CategoryType.SAMPLE;
        int parsedKitCategoryNo = Integer.parseInt(kitCategoryNo);
        for (CustomKitResponseDto.CustomKitItemInfo item : items) {
            if (item.getCategoryNo() == parsedKitCategoryNo) {
                categoryType = CategoryType.KIT;
            }
            HomeResponseDto.ProductSectionDto dto = new HomeResponseDto.ProductSectionDto(
                    categoryType,
                    item.getProductNo(), item.getProductName(),
                    item.getBrandName(), item.getImgUrl(),
                    item.getViewRating(), item.getTotalReviewCount(),
                    item.getSalePrice(), item.getImmediateDiscountAmt(),
                    item.getStockCnt()
            );
            productSectionInfoList.add(dto);
        }

        HomeSectionType homeSectionType =
                (categoryNo == experienceCategoryNo) ? HomeSectionType.FREE_ITEM_SECTION :
                        (categoryNo == weeklySpecialCategoryNo) ? HomeSectionType.HOT_DEAL :
                                (categoryNo == originCategoryNo) ? HomeSectionType.HORIZONTAL_ITEM_SECTION : HomeSectionType.SIX_ITEM_SECTION;

        MoveCase moveCase = (categoryNo == experienceCategoryNo) ? MoveCase.FREE_SAMPLE : MoveCase.CATEGORY;
        String moveKeyStr = null;

        if (categoryNo == newCategoryNo) {
            moveKeyStr = SearchSortType.RECENT_PRODUCT.toString();
        }

        return new HomeResponseDto.ProductSectionInfos(homeSectionType, moveCase, 0,
                sectionTitle, sectionSubTitle, productSectionInfoList, moveKeyStr);
    }

    private String getSectionTitleByHomeProductType(HomeProductType productType) {
        switch (productType) {
            case SECTION_1:
                return CustomValue.newItemTitle;
            case SECTION_2:
                return CustomValue.customSectionTitle;
            case SECTION_3:
                return CustomValue.perfumeTitle;
            case MDKIT:
                return CustomValue.mdKitSectionTitle;
            case EXPERIENCE:
                return CustomValue.experienceSectionTitle;
            case HOT_DEAL:
                return CustomValue.weeklySpecialSectionTitle;
            case LIP:
                return CustomValue.lipTitle;
            case ORIGIN_ITEM:
                return CustomValue.originItemTitle;
            case BEAUTY_ITEM:
                return CustomValue.beautyItemTitle;
            default:
                return ""; // Default title or handle other cases
        }
    }

    private String getSectionSubTitleByHomeProductType(HomeProductType productType) {
        switch (productType) {
            case MDKIT:
                return CustomValue.mdKitSectionSubTitle;
            case EXPERIENCE:
                return CustomValue.experienceSectionSubTitle;
            case HOT_DEAL:
                return CustomValue.weeklySpecialSectionSubTitle;
            case LIP:
                return CustomValue.lipSubTitle;
            case ORIGIN_ITEM:
                return CustomValue.originSubTitle;
            case BEAUTY_ITEM:
                return CustomValue.beautyItemSubTitle;
            default:
                return ""; // Default subtitle or handle other cases
        }
    }

    private NotificationResponseDto checkMemberNotification(UserDetailsImpl userDetails) {
        Long memberId = userDetails.getMember().getId();

        if (notificationAgreeRepository.existsByMemberIdAndIsFirst(memberId, false)) {
            return new NotificationResponseDto(false);
        }

        NotificationResponseQueryDto notification = notificationAgreeRepository.findByMemberIdQueryDsl(memberId);
        if (notification == null) {
            notificationAgreeRepository.save(createNotificationAgree(userDetails.getMember()));
            return new NotificationResponseDto(true); // 처음 저장되는 경우 true 반환
        }

        return new NotificationResponseDto(notification.getIsFirst());
    }


    private NotificationAgree createNotificationAgree(Member saveMember) {
        return NotificationAgree.builder()
                .isFirst(true)
                .smsAgreed(false)
                .directMailAgreed(false)
                .member(saveMember)
                .build();
    }

    private HomeResponseDto.ProductSectionInfos convertDisplayProductSectionInfos(List<DisplaySectionResponseDto.SectionItem> displayProductInfo, DisplayType displayType) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<HomeResponseDto.ProductSectionDto> productSectionDtos = displayProductInfo.stream()
                .map(firstDealProducts -> new HomeResponseDto.ProductSectionDto(
                        CategoryType.SAMPLE, // 이 부분은 적절한 카테고리 타입으로 대체해야 할 수 있습니다.
                        firstDealProducts.getProductNo(),
                        firstDealProducts.getProductName(),
                        firstDealProducts.getBrandName(),
                        firstDealProducts.getSalePrice(),
                        firstDealProducts.getImmediateDiscountAmt(),
                        firstDealProducts.getImageUrl(),
                        firstDealProducts.getSaleStartYmdt().format(formatter),
                        firstDealProducts.getSaleEndYmdt().format(formatter)
                ))
                .collect(Collectors.toList());

        String sectionTitle;
        String sectionSubTitle;
        HomeSectionType homeSectionType;

        if (displayType.equals(DisplayType.HOT_DEAL)) {
            sectionTitle = CustomValue.weeklySpecialSectionTitle;
            sectionSubTitle = CustomValue.weeklySpecialSectionSubTitle;
            homeSectionType = HomeSectionType.HOT_DEAL;
        } else {
            // 이 부분은 displayType에 따라 다른 값으로 설정해야 할 수도 있습니다.
            // 여기서는 예시로 SECTION_2와 관련된 값을 설정하고 있습니다.
            sectionTitle = getSectionTitleByHomeProductType(HomeProductType.SECTION_2);
            sectionSubTitle = getSectionSubTitleByHomeProductType(HomeProductType.SECTION_2);
            homeSectionType = HomeSectionType.SIX_ITEM_SECTION;
        }

        return new HomeResponseDto.ProductSectionInfos(
                homeSectionType,
                null, // 이동 케이스는 상황에 따라 다를 수 있습니다.
                null, // 필요한 경우 추가적인 정보를 이곳에 설정할 수 있습니다.
                sectionTitle,
                sectionSubTitle,
                productSectionDtos,
                null // 필요한 경우 추가적인 정보를 이곳에 설정할 수 있습니다.
        );
    }

    private HomeResponseDto.ProductSectionInfos getBannerSection(BannerResponseDto bannerList, HomeSectionType sectionType) {
        List<HomeResponseDto.ProductSectionDto> productSectionInfoList = bannerList.getBannerList().stream()
                .map(banner -> new HomeResponseDto.ProductSectionDto(
                        banner.getBannerKeyNo(),
                        banner.getImageUrl()))
                .collect(Collectors.toList());

        return new HomeResponseDto.ProductSectionInfos(sectionType, null, null, "", "", productSectionInfoList, null);
    }

    private HomeResponseDto.ProductSectionInfos getHomeMiddleBannerSection(BannerResponseDto homeMiddleBannerList) {
        return getBannerSection(homeMiddleBannerList, HomeSectionType.HOME_MIDDLE_BANNER);
    }

    private HomeResponseDto.ProductSectionInfos getHomeSurveyBannerSection(BannerResponseDto homeSurveyBannerList) {
        return getBannerSection(homeSurveyBannerList, HomeSectionType.SURVEY_BANNER);
    }


    public HomeResponseDto.ProductSectionInfos convertBestSellerToProductSectionInfos(BestSellerResponseDto.BestSeller bestSeller, boolean isNewHome) {
        List<HomeResponseDto.ProductSectionDto> productSectionDtos = new ArrayList<>();
        for (BestSellerResponseDto bestSellerProduct : bestSeller.getProducts()) {
            HomeResponseDto.ProductSectionDto productSectionDto = new HomeResponseDto.ProductSectionDto(
                    CategoryType.SAMPLE,
                    bestSellerProduct.getProductNo(), bestSellerProduct.getProductName(),
                    bestSellerProduct.getBrandName(), bestSellerProduct.getImageUrl(), null, null);

            productSectionDtos.add(productSectionDto);
        }
        int bestCategoryNo = bestSeller.getCategoryNo();

        if (isNewHome) {
            bestCategoryNo = 0;
        }

        return new HomeResponseDto.ProductSectionInfos(
                HomeSectionType.BESTSELLER, // 가정: bestSeller는 항상 베스트셀러 섹션에 해당
                MoveCase.CATEGORY,
                bestCategoryNo,
                bestSeller.getSectionTitle(),
                bestSeller.getSectionSubTitle(),
                productSectionDtos,
                SearchSortType.POPULAR.toString()
        );
    }

    public HomeResponseDto.ProductSectionInfos convertFirstDealToProductSectionInfos(EventProductResponseDto.EventProductList firstDeal) {
        List<HomeResponseDto.ProductSectionDto> productSectionDtos = firstDeal.getEventProductInfoList().stream()
                .map(firstDealProducts -> new HomeResponseDto.ProductSectionDto(
                        CategoryType.SAMPLE,
                        firstDealProducts.getProductNo(),
                        firstDealProducts.getProductName(),
                        firstDealProducts.getBrandName(),
                        firstDealProducts.getSalePrice(),
                        // TODO: 2024/01/02 990원딜 = eventPrice는 이제 정가 -x = 990
                        firstDealProducts.getSalePrice() - firstDealProducts.getEventPrice(),
                        firstDealProducts.getEventProductImgUrl(),
                        firstDealProducts.getLocalDateTime().minusDays(7L).format(DateTimeFormatter.ofPattern("yyyy.MM.dd")),
                        firstDealProducts.getLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
                ))
                .collect(Collectors.toList());

        String eventTitle = null;
        String eventSubTitle = null;
        if (!firstDeal.getEventProductInfoList().isEmpty()) {
            EventProductResponseDto.EventProductInfo firstDealProduct = firstDeal.getEventProductInfoList().get(0);
            eventTitle = firstDealProduct.getEventTitle();
            eventSubTitle = firstDealProduct.getEventSubTitle();
        }

        return new HomeResponseDto.ProductSectionInfos(
                HomeSectionType.FIRST_PURCHASE,
                null,
                null,
                eventTitle,
                eventSubTitle,
                productSectionDtos,
                null
        );
    }

    public HomeResponseDto.ProductSectionInfos convertGroupPurchaseToProductSectionInfos(List<GroupPurchaseResponseDto.ProductInfo> groupPurchaseProductList) throws UnirestException, ParseException {
        List<HomeResponseDto.ProductSectionDto> productSectionDtos = groupPurchaseProductList.stream()
                .map(groupPurchseProduct -> new HomeResponseDto.ProductSectionDto(
                        CategoryType.GROUP_PURCHASE,
                        groupPurchseProduct.getProductNo(),
                        groupPurchseProduct.getProductName(),
                        groupPurchseProduct.getBrandName(),
                        groupPurchseProduct.getSalePrice(),
                        groupPurchseProduct.getImmediateDiscountAmt(),
                        groupPurchseProduct.getProductImageUrl(),
                        groupPurchseProduct.getStockCnt(),
                        groupPurchseProduct.getGroupMemberImgUrls()
                ))
                .collect(Collectors.toList());

        String sectionTitle = CustomValue.groupPurchaseHomeSectionTitle;
        String sectionSubTitle = CustomValue.groupPurchaseHomeSectionSubTitle;

        return new HomeResponseDto.ProductSectionInfos(
                HomeSectionType.GROUP_PURCHASE,
                MoveCase.GROUP_PURCHASE,
                CustomValue.groupPurchaseHomeSectionDiscountRate,
                sectionTitle,
                sectionSubTitle,
                productSectionDtos,
                null
        );
    }
}
