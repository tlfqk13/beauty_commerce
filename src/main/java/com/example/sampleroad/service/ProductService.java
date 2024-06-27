package com.example.sampleroad.service;

import com.example.sampleroad.common.exception.ErrorCode;
import com.example.sampleroad.common.exception.ErrorCustomException;
import com.example.sampleroad.common.utils.CustomValue;
import com.example.sampleroad.domain.CategoryType;
import com.example.sampleroad.domain.grouppurchase.GroupPurchaseRoom;
import com.example.sampleroad.domain.grouppurchase.GroupPurchaseRoomProduct;
import com.example.sampleroad.domain.product.Product;
import com.example.sampleroad.domain.product.ProductType;
import com.example.sampleroad.dto.request.ProductRequestDto;
import com.example.sampleroad.dto.response.BestSellerResponseDto;
import com.example.sampleroad.dto.response.PaymentResponseDto;
import com.example.sampleroad.dto.response.grouppurchase.GroupPurchaseQueryDto;
import com.example.sampleroad.dto.response.home.HomeResponseDto;
import com.example.sampleroad.dto.response.product.ProductDetailResponseDto;
import com.example.sampleroad.dto.response.product.ProductQueryDto;
import com.example.sampleroad.dto.response.product.ProductResponseDto;
import com.example.sampleroad.dto.response.zeroExperienceReview.ZeroExperienceRecommendSurveyQueryDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.example.sampleroad.repository.cart.CartRepository;
import com.example.sampleroad.repository.product.ProductRepository;
import com.example.sampleroad.repository.review.ReviewRepository;
import com.example.sampleroad.repository.wishlist.WishListRepository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final WishListRepository wishListRepository;
    private final CartRepository cartRepository;
    private final ReviewRepository reviewRepository;
    private final ZeroExperienceReviewService zeroExperienceReviewService;
    private final GroupPurchaseService groupPurchaseService;
    private final NotificationAgreeService notificationAgreeService;
    private final ProductShopByService productShopByService;
    private final ReviewService reviewService;

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

    @Value("${shop-by.best-category-no}")
    int bestCategoryNo;
    @Value("${shop-by.weekly-special-category-no}")
    int weeklySpecialCategoryNo;
    @Value("${shop-by.new-category-no}")
    int newCategoryNo;
    @Value("${shop-by.event-category-no}")
    int eventCategoryNo;
    @Value("${shop-by.today-price-category-no}")
    int todayPriceCategoryNo;
    @Value("${shop-by.origin-category-no}")
    int originCategoryNo;
    @Value("${shop-by.experience-category-no}")
    int experienceCategoryNo;
    @Value("${shop-by.sample-category-no}")
    int sampleCategoryNo;
    @Value("${shop-by.kit-category-no}")
    String kitCategoryNo;

    Gson gson = new Gson();

    /**
     * 인기 상품 TOP3 조회
     * 1주일전 ~ 전일까지의 판매수로 정렬된 상품을 조회합니다
     * 판매가 없는 경우 상품 3개를 조회한다
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/06/14
     **/

    public BestSellerResponseDto.BestSeller getBestSeller(int limitSize) throws UnirestException, ParseException {

        // TODO: 2024-01-17 첫구매딜 item 조회
        List<BestSellerResponseDto> bestSellerResponseDtos = productShopByService.shopByGetBestSeller();
        List<BestSellerResponseDto> resultList = new ArrayList<>();

        for (BestSellerResponseDto sellerResponseDto : bestSellerResponseDtos) {
            BestSellerResponseDto bestSellerResponseDto =
                    new BestSellerResponseDto(sellerResponseDto.getProductType(),
                            sellerResponseDto.getProductNo(),
                            sellerResponseDto.getStockCnt(),
                            sellerResponseDto.getProductName(),
                            sellerResponseDto.getBrandName(),
                            sellerResponseDto.getImageUrl(),
                            sellerResponseDto.getSalePrice(),
                            sellerResponseDto.getImmediateDiscountAmt());
            resultList.add(bestSellerResponseDto);
        }
        String sectionTitle = CustomValue.bestSellerSectionTitle;
        String sectionSubTitle = CustomValue.bestSellerSectionSubTitle;

        return new BestSellerResponseDto.BestSeller(sectionTitle, sectionSubTitle, bestCategoryNo, resultList);
    }

    public List<BestSellerResponseDto> getBestSellerByShoppBy() throws UnirestException, ParseException {
        return productShopByService.shopByGetBestSeller().stream().limit(8).collect(Collectors.toList());
    }

    /**
     * 상품 상세 조회
     * 3개의 샵바이 api 요청을 보낸다 ( 옵션 조회 + 리뷰 정보 + 상품 정보 )
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/06/16
     **/
    @Transactional
    public ProductDetailResponseDto.ProductInfo getProductInfo(UserDetailsImpl userDetail, int productNo) throws UnirestException, ParseException {

        Long memberId = userDetail.getMember().getId();
        Product product = getProduct(productNo);

        ProductDetailResponseDto productInfo = productShopByService.getProductInfo(userDetail, productNo);
        JsonObject optionJsonObject = productShopByService.shopByGetProductOptions(productNo);
        if (optionJsonObject == null) {
            return new ProductDetailResponseDto.ProductInfo();
        }

        boolean hasCart = cartRepository.existsByMemberId(memberId);
        boolean isWishList = wishListRepository.existsByProductNoAndMemberId(productNo, memberId);

        // TODO: 4/11/24 KIT 타입의 경우 설문에서 가져와서
        Double recommendPercentage = null;
        ProductType productType = determineProductType(product.getCategory().getCategoryDepth2());
        if (productType.equals(ProductType.KIT)) {
            recommendPercentage = calculateRecommendPercentage(productNo, productType);
        }

        ProductDetailResponseDto responseDto = productShopByService.shopByGetProductDetailInfo(optionJsonObject, productInfo);

        // TODO: 4/15/24 자체리뷰 있는 애들만 세팅
        boolean existsSampleRoadReview = reviewService.existsSampleRoadReviewByProductNo(productNo);
        if (existsSampleRoadReview) {
            Tuple reviewCountAndRateAvg = reviewService.getReviewCountAndRateAvg(productNo);
            responseDto.setReviewRating(reviewCountAndRateAvg.get(1, Double.class));
            responseDto.setReviewCnt(reviewCountAndRateAvg.get(0, Long.class));
        }

        responseDto.setCategoryNo(calculateCategoryNo(product));

        List<ProductDetailResponseDto.SampleList> finalSampleList = getFinalSampleList(product, productNo);
        String relatedSectionName = getRelatedSectionName(finalSampleList, product.getProductName());

        product.updateProductViewCount(product.getProductViewCount());

        String deliveryInfo = CustomValue.deliveryInfo;
        if (responseDto.getDeliveryFee() == 0) {
            deliveryInfo = CustomValue.freeDeliveryInfo;
        } else if (responseDto.getDeliveryFee() == 1000) {
            deliveryInfo = CustomValue.deliveryPriceInfo;
        }

        boolean isMultiPurchase = Optional.ofNullable(product.getIsMultiPurchase()).orElse(false);
        boolean isRestockNotification = false;
        int stock = responseDto.getStock();
        if (stock == 0) {
            isRestockNotification = notificationAgreeService.getProductStockNotification(userDetail.getMember().getId(), productNo);
        }

        if (ProductType.GROUP_PURCHASE.equals(productType)) {
            ProductDetailResponseDto.GroupPurchaseSection groupPurchaseSection = getGroupPurchaseSection(productNo, memberId);
            responseDto.setMaxCnt(groupPurchaseSection.getMaxCnt());
            String deliveryDate = "친구에게 공유해 " + groupPurchaseSection.getRoomCapacity() + "인 채우면 배송 출발!";
            return new ProductDetailResponseDto.ProductInfo(hasCart, isWishList, true, isRestockNotification, deliveryInfo, deliveryDate,
                    productType, finalSampleList, responseDto, recommendPercentage, relatedSectionName, groupPurchaseSection);
        }
        return new ProductDetailResponseDto.ProductInfo(hasCart, isWishList, isMultiPurchase, isRestockNotification, deliveryInfo, calculateDepartureDay(),
                productType, finalSampleList, responseDto, recommendPercentage, relatedSectionName, null);
    }

    private String getRelatedSectionName(List<ProductDetailResponseDto.SampleList> finalSampleList, String productName) {
        return !finalSampleList.isEmpty() && productName.contains("본품")
                ? "샘플로 먼저 사용해 보세요!"
                : finalSampleList.isEmpty() ? null : "본품도 사용해 보세요!";
    }

    private List<ProductDetailResponseDto.SampleList> getFinalSampleList(Product product, int productNo) throws UnirestException, ParseException {
        List<ProductDetailResponseDto.SampleList> relatedSampleList = productShopByService.shopbyGetRelatedProducts(productNo, product.getBrandName());
        return relatedSampleList.isEmpty() ? Collections.emptyList() : relatedSampleList;
    }

    private Double calculateRecommendPercentage(int productNo, ProductType productType) {
        Double recommendPercentage = null;
        if (productType.equals(ProductType.KIT)) {
            List<ZeroExperienceRecommendSurveyQueryDto> zeroExperienceRecommend = zeroExperienceReviewService.getZeroExperienceRecommend(productNo);
            long countIsRecommendTrue = zeroExperienceRecommend.stream()
                    .filter(ZeroExperienceRecommendSurveyQueryDto::getIsRecommend)
                    .count();
            if (!zeroExperienceRecommend.isEmpty()) {
                recommendPercentage = (double) countIsRecommendTrue / zeroExperienceRecommend.size() * 100;
            }
        }

        return recommendPercentage;
    }

    private Double getReviewRateAvg(int productNo) {
        return Optional.ofNullable(reviewRepository.findReviewRateAvg(productNo)).orElse(0.0);
    }

    private ProductType determineProductType(CategoryType categoryType) {
        switch (categoryType) {
            case EXPERIENCE:
            case KIT:
                return ProductType.KIT;
            case GROUP_PURCHASE:
                return ProductType.GROUP_PURCHASE;
            default:
                return ProductType.SAMPLE;
        }
    }

    private int calculateCategoryNo(Product product) {
        int categoryDepthNumber3 = product.getCategory().getCategoryDepthNumber3();
        if (categoryDepthNumber3 != 0) {
            return categoryDepthNumber3;
        } else {
            int categoryDepthNumber2 = product.getCategory().getCategoryDepthNumber2();
            return (categoryDepthNumber2 == 0) ? product.getCategory().getCategoryDepthNumber1() : categoryDepthNumber2;
        }
    }

    public static String calculateDepartureDay() {
        LocalDateTime now = LocalDateTime.now();
        // TODO: 2/22/24 물류센터 3시기준으로 모두
        LocalDateTime cutoff = now.withHour(15).withMinute(0).withSecond(0).withNano(0);
        boolean afterCutoff = now.isEqual(cutoff) || now.isAfter(cutoff);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M월 d일");
        String formattedDate;
        String departureDayStatement;

        if (afterCutoff) {
            // If order is placed after 15:00
            formattedDate = now.plusDays(1).format(formatter);
            departureDayStatement = now.plusDays(1).getDayOfWeek().toString();
        } else {
            // If order is placed before 15:00
            formattedDate = now.format(formatter);
            departureDayStatement = now.getDayOfWeek().toString();
        }

        switch (departureDayStatement) {
            case "MONDAY":
                departureDayStatement = "월요일";
                break;
            case "TUESDAY":
                departureDayStatement = "화요일";
                break;
            case "WEDNESDAY":
                departureDayStatement = "수요일";
                break;
            case "THURSDAY":
                departureDayStatement = "목요일";
                break;
            case "FRIDAY":
                departureDayStatement = "금요일";
                break;
            case "SATURDAY":
                departureDayStatement = "토요일";
                break;
            case "SUNDAY":
                if (afterCutoff) {
                    formattedDate = now.plusDays(2L).format(formatter);
                } else {
                    formattedDate = now.plusDays(1L).format(formatter);
                }
                departureDayStatement = "월요일";
                break;
        }

        return "지금 주문 시 " + departureDayStatement + "(" + formattedDate + ")출발 예정";
    }

    private ProductDetailResponseDto.GroupPurchaseSection getGroupPurchaseSection(int productNo, Long memberId) {
        List<GroupPurchaseRoom> groupPurchaseRooms = groupPurchaseService.getGroupPurchaseRooms(productNo);

        List<GroupPurchaseQueryDto.MemberProfileQueryDto> groupPurchaseRoomMembers =
                groupPurchaseService.getGroupPurchaseRoomMember(productNo);

        Optional<GroupPurchaseRoomProduct> groupPurchaseProduct = groupPurchaseService.getGroupPurchaseProductMaxCnt(productNo);
        int groupPurchaseProductMaxCnt = 1;
        int groupPurchaseRoomCapacity = 2;
        if (groupPurchaseProduct.isPresent()) {
            groupPurchaseProductMaxCnt = groupPurchaseProduct.get().getRoomProductMaxCount();
            groupPurchaseRoomCapacity = groupPurchaseProduct.get().getRoomCapacity();
        }

        Map<Long, List<GroupPurchaseQueryDto.MemberProfileQueryDto>> roomIdToMembersMap = groupPurchaseRoomMembers.stream()
                .collect(Collectors.groupingBy(GroupPurchaseQueryDto.MemberProfileQueryDto::getRoomId));
        List<ProductDetailResponseDto.GroupPurchaseRoom> groupPurchaseRoomList = groupPurchaseRooms.stream()
                .filter(groupPurchaseRoom -> groupPurchaseRoom.getDeadLine().isAfter(LocalDateTime.now())) // Filter based on deadline
                .map(groupPurchaseRoom -> {
                    Long roomId = groupPurchaseRoom.getId();
                    List<GroupPurchaseQueryDto.MemberProfileQueryDto> groupPurchaseRoomMember = roomIdToMembersMap.getOrDefault(roomId, Collections.emptyList());

                    String memberProfileImgUrl = groupPurchaseRoomMember.isEmpty() ? "" : groupPurchaseRoomMember.get(0).getMemberProfileImgUrl();
                    String memberNickName = groupPurchaseRoomMember.isEmpty() ? "" : Optional.ofNullable(groupPurchaseRoomMember.get(0).getMemberNickName()).orElse("");

                    int remainingCapacity = groupPurchaseRoom.getRoomCapacity() - groupPurchaseRoomMember.size();
                    LocalDateTime deadLine = groupPurchaseRoom.getDeadLine();
                    String localDateStr = String.valueOf(deadLine.toLocalDate());
                    String localTimeStr = String.valueOf(deadLine.toLocalTime());
                    String deadLineStr = localDateStr + " " + localTimeStr;
                    List<Long> memberIds = groupPurchaseRoomMember.stream().map(GroupPurchaseQueryDto.MemberProfileQueryDto::getMemberId).collect(Collectors.toList());
                    boolean isTeamMember = memberIds.contains(memberId);
                    return new ProductDetailResponseDto.GroupPurchaseRoom(roomId, memberProfileImgUrl, memberNickName, remainingCapacity, deadLineStr, isTeamMember);

                })
                .collect(Collectors.toList());

        int roomCapacity = groupPurchaseRooms.isEmpty() ? groupPurchaseRoomCapacity : groupPurchaseRooms.get(0).getRoomCapacity();

        return new ProductDetailResponseDto.GroupPurchaseSection(roomCapacity, groupPurchaseProductMaxCnt, groupPurchaseRoomList);
    }

    /**
     * 비회원용 최근 본 상품 조회
     * 샵바이 이용
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/11/13
     **/

    public ProductDetailResponseDto.RecentProducts getRecentProduct(ProductRequestDto.RecentProducts productRequestDto) throws UnirestException, ParseException {
        if (productRequestDto.getRecentProductNos().isEmpty()) {
            List<ProductDetailResponseDto.RecentProductInfo> recentProductInfoList = new ArrayList<>();
            return new ProductDetailResponseDto.RecentProducts(0, recentProductInfoList, bestCategoryNo);
        }
        List<ProductDetailResponseDto.RecentProductInfo> recentProductInfoList = productShopByService.shopByGetNotMemberRecentProduct(productRequestDto);
        return new ProductDetailResponseDto.RecentProducts(recentProductInfoList.size(), recentProductInfoList, bestCategoryNo);
    }

    public Page<ProductQueryDto.SearchProductQueryDto> getProductListInfo(Pageable pageable, String searchKeyword) {
        return productRepository.findBySearchKeywordPaging(pageable, searchKeyword);
    }

    public Page<ProductQueryDto.SearchProductQueryDto> getProductListInfo(Pageable pageable, int brandNo) {
        return productRepository.findBySearchKeywordPaging(pageable, brandNo);
    }

    public Product getProduct(int productNo) {
        return productRepository.findByProductNoAndProductInvisible(productNo, false)
                .orElseThrow(() -> new ErrorCustomException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    public List<ProductQueryDto> getProductCategory(List<Integer> productNos) {
        return productRepository.findProductCategoryByProductNos(productNos);
    }

    public PaymentResponseDto.RecommendProductList getRecommendProductList() throws UnirestException, ParseException {
        List<ProductDetailResponseDto.RecommendProductResponseDto> products = getRecommendProductInfos();
        String title = CustomValue.generalProductRecommendTitle;
        String subTitle = CustomValue.generalProductRecommendSubTitle;
        return new PaymentResponseDto.RecommendProductList(title, subTitle, products);
    }

    private List<ProductDetailResponseDto.RecommendProductResponseDto> getRecommendProductInfos() throws UnirestException, ParseException {
        BestSellerResponseDto.BestSeller bestSeller = getBestSeller(10);
        List<ProductDetailResponseDto.RecommendProductResponseDto> r = new ArrayList<>();
        for (int i = 0; i < bestSeller.getProducts().size(); i++) {
            ProductDetailResponseDto.RecommendProductResponseDto productResponseDto =
                    new ProductDetailResponseDto.RecommendProductResponseDto(
                            bestSeller.getProducts().get(i).getProductNo(),
                            bestSeller.getProducts().get(i).getProductName(),
                            bestSeller.getProducts().get(i).getImageUrl());
            r.add(productResponseDto);
        }
        return r;
    }

    /**
     * productNos로 샵바이에서 상품정보 가져오는
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2/27/24
     **/
    public Map<Integer, Integer> getProductListInfo(Set<Integer> productNos) throws UnirestException {
        if (productNos.isEmpty()) {
            return Collections.emptyMap(); // Return an empty map if productNos is empty
        }

        int[] productNoArray = productNos.stream().mapToInt(Integer::intValue).toArray();
        List<HomeResponseDto.ProductSectionDto> productSectionDtoList = productShopByService.shopbyGetProductList(productNoArray);
        return productSectionDtoList.stream().collect(Collectors.toMap(HomeResponseDto.ProductSectionDto::getProductNo, HomeResponseDto.ProductSectionDto::getImmediateDiscountAmt));
    }

    public List<ProductResponseDto> getProductListInfo(List<Integer> productNos) throws UnirestException {
        if (productNos.isEmpty()) {
            return Collections.emptyList(); // Return an empty map if productNos is empty
        }
        List<ProductResponseDto> responseDtoList = new ArrayList<>();
        int[] productNoArray = productNos.stream().mapToInt(Integer::intValue).toArray();
        List<HomeResponseDto.ProductSectionDto> productSectionDtoList = productShopByService.shopbyGetProductList(productNoArray);
        for (HomeResponseDto.ProductSectionDto productSectionDto : productSectionDtoList) {
            ProductResponseDto productResponseDto = new ProductResponseDto(productSectionDto);
            responseDtoList.add(productResponseDto);
        }

        return responseDtoList;

    }

    public List<BestSellerResponseDto> getProductForDeliveryPrice(int additionalPriceNeeded, List<Integer> excludeProductNos) throws UnirestException, ParseException {
        return productShopByService.getProductForDeliveryPrice(additionalPriceNeeded, excludeProductNos);
    }
}
