package com.example.sampleroad.service;


import com.example.sampleroad.common.utils.ShopBy;
import com.example.sampleroad.domain.search.SearchSortType;
import com.example.sampleroad.dto.response.product.CustomKitQueryDto;
import com.example.sampleroad.dto.response.product.CustomKitResponseDto;
import com.example.sampleroad.dto.response.wishList.WishListQueryDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.example.sampleroad.repository.cart.CartRepository;
import com.example.sampleroad.repository.product.ProductRepository;
import com.example.sampleroad.repository.review.ReviewRepository;
import com.example.sampleroad.repository.wishlist.WishListRepository;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CustomKitService {
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    private final CartRepository cartRepository;
    private final WishListRepository wishListRepository;

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
    @Value("${shop-by.products}")
    String products;

    @Value("${shop-by.best-category-no}")
    int bestCategoryNo;
    @Value("${shop-by.new-category-no}")
    int newCategoryNo;
    @Value("${shop-by.event-category-no}")
    int eventCategoryNo;
    @Value("${shop-by.origin-category-no}")
    int originCategoryNo;
    @Value("${shop-by.group-purchase-category-no}")
    int groupPurchaseCategoryNo;
    @Value("${shop-by.sample-category-no}")
    int sampleCategoryNo;

    Gson gson = new Gson();

    /**
     * 카테고리 별로 상품 조회하기
     * 288411 - 샘플 카테고리 번호 (depth1)
     *
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2024/04/11
     **/

    public CustomKitResponseDto.CustomKitFromCategory getCustomKitItemByCategory(UserDetailsImpl userDetail, int pageNumber, int pageSize, int categoryNumber, SearchSortType condition) throws UnirestException, ParseException {

        if (categoryNumber == 0) {
            categoryNumber = sampleCategoryNo;
        }

        // Initialize customKitResponseDto
        CustomKitResponseDto customKitResponseDto = shopbyGetProductListByCategoryNo(categoryNumber, pageNumber, pageSize, condition);

        // Extract product numbers from items
        Set<Integer> productNos = customKitResponseDto.getItem().stream()
                .map(CustomKitResponseDto.CustomKitItemInfo::getProductNo)
                .collect(Collectors.toSet());

        // Retrieve custom kit items by product numbers
        log.info("커스텀키트 조회하기 _____S");
        List<CustomKitQueryDto> productList = productRepository.findCustomKitItemByProductNos(productNos);
        Map<Integer, Double> reviewRateMap = productList.stream()
                .collect(Collectors.toMap(CustomKitQueryDto::getProductNo, CustomKitQueryDto::getProductReviewRate));
        Map<Integer, Integer> reviewCountMap = reviewRepository.findReviewsInfoCount(productNos);

        Map<Integer, Boolean> wishListMap = setWishList(userDetail.getMember().getId(), productNos);

        setCustomKitItems(customKitResponseDto, reviewCountMap, reviewRateMap, wishListMap);

        boolean hasCart = cartRepository.existsByMemberId(userDetail.getMember().getId());
        return new CustomKitResponseDto.CustomKitFromCategory(hasCart, customKitResponseDto.getTotalCount(), customKitResponseDto.getItem());
    }

    private Map<Integer, Boolean> setWishList(Long memberId, Set<Integer> productNos) {
        List<WishListQueryDto> wishList = wishListRepository.findByProductNosAndMemberId(productNos, memberId);

        // wishList에서 모든 productNo를 추출하여 Set으로 변환
        Set<Integer> wishListProductNos = wishList.stream()
                .map(WishListQueryDto::getProductNo)
                .collect(Collectors.toSet());
        // productNos 리스트를 이용하여 각 productNo가 wishList에 있는지 확인
        return productNos.stream()
                .collect(Collectors.toMap(productNo -> productNo, wishListProductNos::contains));
    }


    private void setCustomKitItems(CustomKitResponseDto customKitResponseDto,
                                   Map<Integer, Integer> reviewCountMap,
                                   Map<Integer, Double> reviewRateMap,
                                   Map<Integer, Boolean> wishListMap) {
        for (CustomKitResponseDto.CustomKitItemInfo kitItem : customKitResponseDto.getItem()) {
            Double reviewRateAvg = reviewRateMap.getOrDefault(kitItem.getProductNo(), 0.0);
            BigDecimal roundedReviewRateAvg = new BigDecimal(reviewRateAvg).setScale(1, RoundingMode.HALF_UP);
            double roundedReviewRate = roundedReviewRateAvg.doubleValue();
            kitItem.setViewRating(roundedReviewRate);
            kitItem.setTotalReviewCount(reviewCountMap.getOrDefault(kitItem.getProductNo(), 0));
            kitItem.setIsWishList(wishListMap.getOrDefault(kitItem.getProductNo(),false));
        }
    }

    public CustomKitResponseDto shopbyGetProductListByCategoryNo(int categoryNumber, int pageNumber, int pageSize, SearchSortType condition) throws UnirestException, ParseException {

        HttpResponse<String> response = getHttpResponseSearchByCategoryNo(categoryNumber, pageNumber, pageSize, condition);

        ShopBy.errorMessage(response);

        List<CustomKitResponseDto.CustomKitItemInfo> customKitItemInfoList = new ArrayList<>();
        JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
        Long totalCount = jsonObject.get("totalCount").getAsLong();
        JsonArray itemsArray = jsonObject.getAsJsonArray("items");
        for (JsonElement element : itemsArray) {
            JsonObject productObject = element.getAsJsonObject();
            int productNo = productObject.get("productNo").getAsInt();
            String productName = productObject.get("productName").getAsString();
            String brandName = productObject.get("brandName").getAsString();
            brandName = processBrandName(brandName);
            String imageUrl = productObject.getAsJsonArray("imageUrls").get(0).getAsString();
            imageUrl = "https:" + imageUrl;
            Double reviewRating = productObject.get("reviewRating").getAsDouble();
            int totalReviewCount = productObject.get("totalReviewCount").getAsInt();
            int salePrice = productObject.get("salePrice").getAsInt();
            int immediateDiscountAmt = productObject.get("immediateDiscountAmt").getAsInt();
            int stockCnt = productObject.get("stockCnt").getAsInt();

            String displayCategoryNos = productObject.get("displayCategoryNos").getAsString();
            if (displayCategoryNos.length() >= 6) {
                displayCategoryNos = displayCategoryNos.substring(0, 6);
            }

            CustomKitResponseDto.CustomKitItemInfo customKitItemInfo
                    = new CustomKitResponseDto.CustomKitItemInfo
                    (Integer.parseInt(displayCategoryNos), productNo,
                            productName, brandName, imageUrl, reviewRating, totalReviewCount, salePrice,
                            immediateDiscountAmt, stockCnt);

            customKitItemInfoList.add(customKitItemInfo);

        }
        return new CustomKitResponseDto(totalCount, customKitItemInfoList);
    }

    private HttpResponse<String> getHttpResponseSearchByCategoryNo(int categoryNumber, int pageNumber, int pageSize, SearchSortType condition) throws UnirestException {
        String orderBy;
        String orderDirection = "DESC"; // Default value

        String excludeCategoryNos = "&excludeCategoryNos=" + eventCategoryNo + "," + groupPurchaseCategoryNo;
        if (categoryNumber == sampleCategoryNo) {
            excludeCategoryNos += "," + originCategoryNo;
        } else if (categoryNumber == newCategoryNo) {
            if (SearchSortType.DEFAULT.equals(condition)) {
                categoryNumber = sampleCategoryNo;
                condition = SearchSortType.SALE_YMD;
            } else {
                categoryNumber = sampleCategoryNo;
            }
        } else if (categoryNumber == bestCategoryNo) {
            categoryNumber = sampleCategoryNo;
            condition = SearchSortType.POPULAR;
        }

        switch (condition) {
            case LOW_PRICE:
                orderBy = SearchSortType.DISCOUNTED_PRICE.toString();
                orderDirection = "ASC";
                break;
            case HIGH_PRICE:
                orderBy = SearchSortType.DISCOUNTED_PRICE.toString();
                break;
            case POPULAR:
                orderBy = SearchSortType.POPULAR.toString();
                break;
            default:
                orderBy = SearchSortType.SALE_YMD.toString(); // 판매일자순
        }

        return Unirest.get("https://shop-api.e-ncp.com/products/search" +
                        "?filter.saleStatus=ALL_CONDITIONS&filter.soldout=true" +
                        "&filter.totalReviewCount=true" +
                        excludeCategoryNos +
                        "&order.by=" + orderBy +
                        "&order.direction=" + orderDirection +
                        "&order.soldoutPlaceEnd=" + true +
                        "&categoryNos=" + categoryNumber +
                        "&pageNumber=" + pageNumber +
                        "&pageSize=" + pageSize +
                        "&hasTotalCount=true&hasOptionValues=false")
                .header("accept", acceptHeader)
                .header("version", versionHeader)
                .header("clientid", clientId)
                .header("platform", platformHeader)
                .header("accesstoken", "")
                .header("content-type", acceptHeader)
                .asString();
    }

    private String processBrandName(String brandName) {
        int slashIndex = brandName.indexOf('/');
        return slashIndex != -1 ? brandName.substring(0, slashIndex).trim() : brandName.trim();
    }
}


