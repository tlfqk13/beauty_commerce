package com.example.sampleroad.service;

import com.example.sampleroad.common.utils.CustomValue;
import com.example.sampleroad.common.utils.ShopBy;
import com.example.sampleroad.domain.Brand;
import com.example.sampleroad.domain.CategoryType;
import com.example.sampleroad.domain.DisplaySectionType;
import com.example.sampleroad.domain.product.EventProductType;
import com.example.sampleroad.domain.search.SearchKeyword;
import com.example.sampleroad.domain.search.SearchSortType;
import com.example.sampleroad.dto.response.BestSellerResponseDto;
import com.example.sampleroad.dto.response.BrandResponseDto;
import com.example.sampleroad.dto.response.coupon.CouponResponseDto;
import com.example.sampleroad.dto.response.home.HomeResponseDto;
import com.example.sampleroad.dto.response.product.EventProductQueryDto;
import com.example.sampleroad.dto.response.product.ProductQueryDto;
import com.example.sampleroad.dto.response.search.SearchKeywordQueryDto;
import com.example.sampleroad.dto.response.search.SearchResponseDto;
import com.example.sampleroad.dto.response.wishList.WishListQueryDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.example.sampleroad.repository.BrandRepository;
import com.example.sampleroad.repository.product.EventProductRepository;
import com.example.sampleroad.repository.search.SearchKeywordRepository;
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
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {
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
    @Value("${shop-by.kit-category-no}")
    String kitCategoryNo;
    @Value("${shop-by.products}")
    String products;
    @Value("${shop-by.event-category-no}")
    int eventCategoryNo;
    @Value("${shop-by.best-category-no}")
    int bestCategoryNo;
    @Value("${shop-by.new-category-no}")
    int newCategoryNo;
    @Value("${shop-by.today-price-category-no}")
    String todayPriceCategoryNo;
    @Value("${shop-by.sample-category-no}")
    int sampleCategoryNo;
    Gson gson = new Gson();

    private final SearchKeywordRepository searchKeywordRepository;
    private final WishListRepository wishListRepository;
    private final EventProductRepository eventProductRepository;
    private final BrandRepository brandRepository;
    private final CategoryService categoryService;
    private final ProductService productService;
    private final CouponService couponService;

    /**
     * 검색 메인
     * 인기 검색어 조회 + 카테고리 + 인기 상품(검색결과 없을때 사용)
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/09/12
     **/
    public SearchResponseDto.SearchMain getSearchMain(UserDetailsImpl userDetail) throws UnirestException, ParseException {
        List<String> favoriteKeywords = getFavoriteKeywords();
        HomeResponseDto.HomeCategoryList homeCategory = categoryService.getHomeCategory();
        SearchResponseDto.BestSeller searchBestSeller = getSearchBestSeller(userDetail);

        return new SearchResponseDto.SearchMain(favoriteKeywords, homeCategory, searchBestSeller);
    }

    public SearchResponseDto.SearchMain getNewSearchMain(UserDetailsImpl userDetail) throws UnirestException, ParseException {
        List<String> favoriteKeywords = getFavoriteKeywords();
        HomeResponseDto.HomeCategoryList homeCategory = categoryService.getHomeCategory();
        SearchResponseDto.BestSeller searchBestSeller = getSearchBestSeller(userDetail);

        for (int i = 0; i < homeCategory.getCategoryInfo().size(); i++) {
            HomeResponseDto.CategoryInfo categoryInfo = homeCategory.getCategoryInfo().get(i);
            if (categoryInfo.getCategoryNo() == newCategoryNo || categoryInfo.getCategoryNo() == bestCategoryNo) {
                categoryInfo.setCategoryNo(0);
            }
        }

        return new SearchResponseDto.SearchMain(favoriteKeywords, homeCategory, searchBestSeller);
    }

    private List<String> getFavoriteKeywords() {
        List<SearchKeywordQueryDto> searchKeywordQueryDtos = searchKeywordRepository.findSearchKeywordByIsVisbile();
        return searchKeywordQueryDtos.stream()
                .map(SearchKeywordQueryDto::getSearchKeyword)
                .collect(Collectors.toList());
    }

    /**
     * 비회원 검색 메인
     * 인기 상품 - 찜하기 기능이 없어야함
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/11/09
     **/
    public SearchResponseDto.SearchMain getSearchMainForNotMember() throws UnirestException, ParseException {
        List<String> favoriteKeywords = getFavoriteKeywords();
        HomeResponseDto.HomeCategoryList homeCategory = categoryService.getHomeCategory();
        SearchResponseDto.BestSeller searchBestSeller = getSearchBestSeller();
        return new SearchResponseDto.SearchMain(favoriteKeywords, homeCategory, searchBestSeller);
    }

    /**
     * 인기 상품을 샵바이 에서 조회
     * 인기 상품 최대 14개
     * 인기 상품이 14개가 안되면 db에서 조회순으로 가져와서 14개 맞춤
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/09/12
     **/
    private SearchResponseDto.BestSeller getSearchBestSeller(UserDetailsImpl userDetail) throws UnirestException, ParseException {
        BestSellerResponseDto.BestSeller bestSellerData = productService.getBestSeller(CustomValue.searchBestSellerSize);

        List<Integer> productNumbers = bestSellerData.getProducts().stream()
                .map(BestSellerResponseDto::getProductNo)
                .collect(Collectors.toList());

        Map<Integer, Boolean> wishlistStatusMap = Optional.ofNullable(userDetail)
                .map(detail -> getWishlistStatusMap(productNumbers, detail.getMember().getId()))
                .orElseGet(Collections::emptyMap);

        List<SearchResponseDto.SearchBestSellerResponseDto> searchResult = bestSellerData.getProducts().stream()
                .map(product -> new SearchResponseDto.SearchBestSellerResponseDto(
                        wishlistStatusMap.getOrDefault(product.getProductNo(), false),
                        product))
                .collect(Collectors.toList());

        return new SearchResponseDto.BestSeller(bestSellerData.getCategoryNo(), searchResult);
    }

    private Map<Integer, Boolean> getWishlistStatusMap(List<Integer> productNos, Long memberId) {
        return wishListRepository.findByProductNosAndMemberId(productNos, memberId).stream()
                .collect(Collectors.toMap(WishListQueryDto::getProductNo, wishList -> Boolean.TRUE));
    }


    /**
     * 비회원 배스트 상품 조회 - 찜하기 X
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/11/09
     **/

    private SearchResponseDto.BestSeller getSearchBestSeller() throws UnirestException, ParseException {
        BestSellerResponseDto.BestSeller bestSeller = productService.getBestSeller(CustomValue.searchBestSellerSize);

        List<SearchResponseDto.SearchBestSellerResponseDto> searchResult = new ArrayList<>();
        for (BestSellerResponseDto product : bestSeller.getProducts()) {
            SearchResponseDto.SearchBestSellerResponseDto dto
                    = new SearchResponseDto.SearchBestSellerResponseDto(false, product);
            searchResult.add(dto);
        }

        return new SearchResponseDto.BestSeller(bestSeller.getCategoryNo(), searchResult);
    }

    /**
     * 샵바이 검색 엔진 이용
     * 검색어는 db에 저장
     * 찜목록 여부와, 키트|샘플 여부 setter로 설정
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/09/12
     **/
    @Transactional
    public SearchResponseDto getSearchProduct(int pageNumber, int pageSize, UserDetailsImpl userDetail, String searchKeyword, SearchSortType searchSortType) throws UnirestException, ParseException {
        Long memberId = userDetail.getMember().getId();
        searchKeyword = searchKeyword.trim();

        String pattern = "^[가-힣a-zA-Z0-9\\s]+$";
        if (!searchKeyword.matches(pattern)) {
            searchKeyword = "";
        }

        SearchSortType searchQueryType = determineSearchQueryType(searchSortType, searchKeyword);
        String sortDirection = determineSortDirection(searchSortType);

        List<EventProductQueryDto.EventProductInfo> eventProducts = eventProductRepository.findEventProductByIsVisible(EventProductType.FIRST_DEAL);
        List<Integer> eventProductNos = eventProducts.stream().map(EventProductQueryDto.EventProductInfo::getProductNo).collect(Collectors.toList());

        SearchResponseDto.SearchResult searchResult;
        if (searchSortType == SearchSortType.DISCOUNT_RATE) {
            searchResult = processDiscountRateSearch(pageNumber, pageSize, userDetail, searchKeyword, eventProductNos);
        } else {
            searchResult = processStandardSearch(searchKeyword, pageNumber, pageSize, searchQueryType, sortDirection, memberId, eventProductNos);
        }
        return new SearchResponseDto(searchResult);
    }

    /**
     * 샵바이 검색 엔진 이용
     * 검색어는 db에 저장
     * 찜목록 여부와, 키트|샘플 여부 setter로 설정
     *
     * @param
     * @param categoryNumber
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/09/12
     **/
    @Transactional
    public SearchResponseDto getSearchBrand(int pageNumber, int pageSize, UserDetailsImpl userDetail, int brandNo, SearchSortType searchSortType, int categoryNumber) throws UnirestException, ParseException {
        SearchSortType searchQueryType = determineSearchQueryType(searchSortType, "");
        String sortDirection = determineSortDirection(searchSortType);

        List<EventProductQueryDto.EventProductInfo> eventProducts = eventProductRepository.findEventProductByIsVisible(EventProductType.FIRST_DEAL);
        List<Integer> eventProductNos = eventProducts.stream().map(EventProductQueryDto.EventProductInfo::getProductNo).collect(Collectors.toList());

        SearchResponseDto.SearchResult searchResult;
        if (searchSortType == SearchSortType.DISCOUNT_RATE) {
            searchResult = processBrandDiscountRateSearch(pageNumber, pageSize, userDetail, brandNo, categoryNumber, eventProductNos);
        } else {
            searchResult = processBrandStandardSearch(brandNo, pageNumber, pageSize, searchQueryType, sortDirection, userDetail.getMember().getId(), eventProductNos, categoryNumber);
        }
        return new SearchResponseDto(searchResult);
    }

    /**
     * 브랜드모아보기
     *
     * @param
     * @param categoryNumber
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 4/18/24
     **/
    public BrandResponseDto getSearchBrandProduct(UserDetailsImpl userDetail, int brandNo, SearchSortType searchSortType, int pageNumber, int pageSize, int categoryNumber) throws UnirestException, ParseException {
        SearchResponseDto searchProduct = getSearchBrand(pageNumber, pageSize, userDetail, brandNo, searchSortType, categoryNumber);

        List<SearchResponseDto.SearchResultItemInfo> searchResultItemInfoList = searchProduct.getSearchResult().getItem();
        List<BrandResponseDto.BrandProductInfo> brandItemInfoList = new ArrayList<>();
        List<BrandResponseDto.SectionInfo> sectionInfoList = new ArrayList<>();

        // Collect product numbers directly from the search results
        brandItemInfoList = getBrandProductInfoList(searchResultItemInfoList, userDetail, brandItemInfoList);

        if (brandItemInfoList.isEmpty()) {
            return new BrandResponseDto(searchProduct.getSearchResult().getTotalCount());

        }

        // TODO: 4/18/24 브랜드 이미지 db 생성?
        String displayMainImgUrl = "";
        String mainTitle = "브랜드";
        Optional<Brand> brand = brandRepository.findByProduct_ProductNo(searchProduct.getSearchResult().getItem().get(0).getProductNo());
        String brandDescription = mainTitle;
        if (brand.isPresent()) {
            displayMainImgUrl = brand.get().getBrandImageUrl();
            brandDescription = brand.get().getBrandDescription();
        }

        List<String> categoryNosList = new ArrayList<>();
        categoryNosList.add(String.valueOf(sampleCategoryNo));
        // TODO: 4/18/24 본인이 속한 카테고리 no가 조회됨, 상위 계층 카테고리 no로 해야함
        if (!searchProduct.getSearchResult().getItem().isEmpty()) {
            mainTitle = searchProduct.getSearchResult().getItem().get(0).getBrandName();
            for (int i = 0; i < searchProduct.getSearchResult().getItem().size(); i++) {
                String displayCategoryNo = searchProduct.getSearchResult().getItem().get(i).getDisplayCategoryNo();
                categoryNosList.add(displayCategoryNo);
            }
        }
        List<BrandResponseDto.BrandCoupon> brandCouponInfoList = new ArrayList<>();

        CouponResponseDto.DownloadAbleCoupon allBrandCoupons = couponService.getAllBrandCoupons(userDetail, searchProduct.getSearchResult().getItem().get(0).getBrandName());
        if (!allBrandCoupons.getCouponInfoList().isEmpty()) {
            addCouponSection(brandCouponInfoList, sectionInfoList, mainTitle);

            for (int i = 0; i < allBrandCoupons.getCouponInfoList().size(); i++) {
                BrandResponseDto.BrandCoupon brandCoupon = new BrandResponseDto.BrandCoupon(
                        allBrandCoupons.getCouponInfoList().get(i).getCouponNo(),
                        allBrandCoupons.getCouponInfoList().get(i).getCouponName(),
                        allBrandCoupons.getCouponInfoList().get(i).getCouponType(),
                        allBrandCoupons.getCouponInfoList().get(i).getDiscountRate(),
                        allBrandCoupons.getCouponInfoList().get(i).getDiscountAmt(),
                        allBrandCoupons.getCouponInfoList().get(i).getMinSalePriceStr(),
                        allBrandCoupons.getCouponInfoList().get(i).getIsDownloadable(),
                        allBrandCoupons.getCouponInfoList().get(i).getMaxDiscountAmtStr(),
                        allBrandCoupons.getCouponInfoList().get(i).getCouponTitle()
                );

                brandCouponInfoList.add(brandCoupon);
            }
        }

        if (brand.isPresent()) {
            addMainImageSections(displayMainImgUrl, sectionInfoList);
        }


        addProductSections(brandItemInfoList, mainTitle, sectionInfoList); // Adding product section

        // TODO: 2024-04-24 카테고리 no 중복제거
        Set<String> categoryNos = convertListToSetWithOrder(categoryNosList);

        return new BrandResponseDto(searchProduct.getSearchResult().getTotalCount(), mainTitle, brandDescription, sectionInfoList, categoryNos);

    }

    public BrandResponseDto.BrandProductInfoDto getSearchBrandProductInfo(UserDetailsImpl userDetail, int brandNo,
                                                                          SearchSortType searchSortType, int pageNumber,
                                                                          int pageSize, int categoryNumber) throws UnirestException, ParseException {

        SearchResponseDto searchProduct = getSearchBrand(pageNumber, pageSize, userDetail, brandNo, searchSortType, categoryNumber);

        List<SearchResponseDto.SearchResultItemInfo> searchResultItemInfoList = searchProduct.getSearchResult().getItem();
        List<BrandResponseDto.BrandProductInfo> brandItemInfoList = new ArrayList<>();

        brandItemInfoList = getBrandProductInfoList(searchResultItemInfoList, userDetail, brandItemInfoList);

        return new BrandResponseDto.BrandProductInfoDto(searchProduct.getSearchResult().getTotalCount(), brandItemInfoList);

    }

    private List<BrandResponseDto.BrandProductInfo> getBrandProductInfoList(List<SearchResponseDto.SearchResultItemInfo> searchResultItemInfoList, UserDetailsImpl userDetail, List<BrandResponseDto.BrandProductInfo> brandItemInfoList) {
        // Collect product numbers directly from the search results
        List<Integer> productNos = searchResultItemInfoList.stream()
                .map(SearchResponseDto.SearchResultItemInfo::getProductNo)
                .collect(Collectors.toList());

        // Retrieve wish list entries based on product numbers
        List<WishListQueryDto> wishList = wishListRepository.findByProductNosAndMemberId(productNos, userDetail.getMember().getId());
        Map<Integer, Long> productNoMap = wishList.stream()
                .collect(Collectors.toMap(WishListQueryDto::getProductNo, WishListQueryDto::getProductId, (existing, replacement) -> existing));

        // Convert search results into brand product info, marking items as wished or not
        brandItemInfoList = searchResultItemInfoList.stream()
                .map(item -> new BrandResponseDto.BrandProductInfo(item, productNoMap.containsKey(item.getProductNo())))
                .collect(Collectors.toList());

        return brandItemInfoList;
    }

    private Set<String> convertListToSetWithOrder(List<String> categoryNosList) {
        // Using LinkedHashSet to maintain the order of elements
        LinkedHashSet<String> categoryNosSet = new LinkedHashSet<>();


        // Add the first element that you always want to be first if it's not already in the list
        categoryNosSet.add(String.valueOf(sampleCategoryNo));  // This ensures sampleCategoryNo is the first in the set

        List<String> categoriesByDepthNumbers = categoryService.findCategoriesByDepthNumbers(categoryNosList);
        // Add all elements from the list to the set; duplicates will not be added
        categoryNosSet.addAll(categoriesByDepthNumbers);

        return categoryNosSet;
    }

    private void addMainImageSections(String mainImageUrl, List<BrandResponseDto.SectionInfo> sectionInfoList) {
        BrandResponseDto.SectionInfo sectionInfo = new BrandResponseDto.SectionInfo(DisplaySectionType.IMAGE_SECTION, mainImageUrl);
        sectionInfoList.add(sectionInfo);
    }

    private void addCouponSection(List<BrandResponseDto.BrandCoupon> brandCouponInfoList, List<BrandResponseDto.SectionInfo> sectionInfoList, String brandName) {
        sectionInfoList.add(new BrandResponseDto.SectionInfo(brandName + " 쿠폰", DisplaySectionType.COUPON_SECTION, brandCouponInfoList));
    }

    private static void addProductSections(List<BrandResponseDto.BrandProductInfo> brandProductInfo,
                                           String mainTitle, List<BrandResponseDto.SectionInfo> sectionInfoList) {
        sectionInfoList.add(new BrandResponseDto.SectionInfo(DisplaySectionType.ITEM_SECTION, mainTitle + " 전체 상품 모아보기", brandProductInfo, null));
    }

    /**
     * 비회원 검색
     * 샵바이 검색 엔진 이용
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/11/09
     **/
    public SearchResponseDto getSearchProductForNotMember(int pageNumber, int pageSize, String searchKeyword, SearchSortType searchSortType) throws UnirestException, ParseException {
        searchKeyword = searchKeyword.trim();

        String pattern = "^[가-힣a-zA-Z0-9\\s]+$";
        if (!searchKeyword.matches(pattern)) {
            searchKeyword = "";
        }
        SearchSortType searchQueryType = determineSearchQueryType(searchSortType, searchKeyword);
        String sortDirection = determineSortDirection(searchSortType);

        List<EventProductQueryDto.EventProductInfo> eventProducts = eventProductRepository.findEventProductByIsVisible(EventProductType.FIRST_DEAL);
        List<Integer> eventProductNos = eventProducts.stream().map(EventProductQueryDto.EventProductInfo::getProductNo).collect(Collectors.toList());

        SearchResponseDto.SearchResult searchResult;
        if (searchSortType == SearchSortType.DISCOUNT_RATE) {
            searchResult = processDiscountRateSearch(pageNumber, pageSize, null, searchKeyword, eventProductNos);
        } else {
            searchResult = processStandardSearch(searchKeyword, pageNumber, pageSize, searchQueryType,
                    sortDirection, null, eventProductNos);
        }
        return new SearchResponseDto(searchResult);
    }

    private SearchResponseDto.SearchResult processStandardSearch(String searchKeyword, int pageNumber, int pageSize,
                                                                 SearchSortType searchQueryType, String sortDirection, Long memberId,
                                                                 List<Integer> eventProductNos) throws UnirestException, ParseException {
        // 상품 검색
        SearchResponseDto.SearchResult searchResult = shopbyGetSearchProductBySearchKeyword(searchKeyword, pageNumber, pageSize,
                searchQueryType, sortDirection, eventProductNos);

        List<SearchResponseDto.SearchResultItemInfo> item = searchResult.getItem();
        List<Integer> productNos = new ArrayList<>();
        for (SearchResponseDto.SearchResultItemInfo i : item) {
            productNos.add(i.getProductNo());
        }

        setWishList(memberId, productNos, searchResult);
        // 검색 키워드 저장
        saveSearchKeyword(searchKeyword);

        return searchResult;
    }

    private SearchResponseDto.SearchResult processBrandStandardSearch(int brandNo, int pageNumber, int pageSize,
                                                                      SearchSortType searchQueryType, String sortDirection, Long memberId,
                                                                      List<Integer> eventProductNos, int categoryNumber) throws UnirestException, ParseException {
        // 상품 검색
        SearchResponseDto.SearchResult searchResult = shopbyGetSearchBrandProductByBrandNo(brandNo, pageNumber, pageSize,
                searchQueryType, sortDirection, eventProductNos, categoryNumber);

        List<SearchResponseDto.SearchResultItemInfo> item = searchResult.getItem();
        List<Integer> productNos = new ArrayList<>();
        for (SearchResponseDto.SearchResultItemInfo i : item) {
            productNos.add(i.getProductNo());
        }

        setWishList(memberId, productNos, searchResult);
        return searchResult;
    }

    private SearchResponseDto.SearchResult processDiscountRateSearch(int pageNumber, int pageSize, UserDetailsImpl userDetail,
                                                                     String searchKeyword, List<Integer> eventProductNos) throws UnirestException, ParseException {

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<ProductQueryDto.SearchProductQueryDto> productListInfo = productService.getProductListInfo(pageable, searchKeyword);

        // Handle the case where no products are found early
        if (productListInfo.isEmpty()) {
            return new SearchResponseDto.SearchResult(0L, new ArrayList<>());
        }

        List<Integer> productNos = productListInfo.stream()
                .map(ProductQueryDto.SearchProductQueryDto::getProductNo)
                .collect(Collectors.toList());

        String discountRateProductNos = productNos.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        SearchResponseDto.SearchResult searchResult =
                shopbyGetSearchProductBySearchKeyword(searchKeyword, pageNumber, pageSize, eventProductNos, discountRateProductNos);

        setWishList(userDetail.getMember().getId(), productNos, searchResult);

        return new SearchResponseDto.SearchResult(searchResult.getTotalCount(), searchResult.getItem());
    }


    private SearchResponseDto.SearchResult processBrandDiscountRateSearch(int pageNumber, int pageSize, UserDetailsImpl userDetail, int brandNo, int categoryNumber, List<Integer> eventProductNos) throws UnirestException, ParseException {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<ProductQueryDto.SearchProductQueryDto> productListInfo = productService.getProductListInfo(pageable, brandNo);

        if (productListInfo.getTotalElements() == 0) {
            List<SearchResponseDto.SearchResultItemInfo> searchResultItemInfos = new ArrayList<>();
            return new SearchResponseDto.SearchResult(0L, searchResultItemInfos);
        }

        List<Integer> productNos = productListInfo.stream()
                .map(ProductQueryDto.SearchProductQueryDto::getProductNo)
                .collect(Collectors.toList());

        String discountRateProductNos = productNos.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        SearchResponseDto.SearchResult searchResult =
                shopbyGetSearchBrandProductByBrandNo(brandNo, pageNumber, pageSize, eventProductNos, categoryNumber, discountRateProductNos);

        setWishList(userDetail.getMember().getId(), productNos, searchResult);

        return new SearchResponseDto.SearchResult(searchResult.getTotalCount(), searchResult.getItem());
    }

    private void setWishList(Long userDetail, List<Integer> productNos, SearchResponseDto.SearchResult searchResult) {
        if (userDetail != null) {
            List<WishListQueryDto> wishList = wishListRepository.findByProductNosAndMemberId(productNos, userDetail);

            Map<Integer, Long> productNoMap = wishList.stream()
                    .collect(Collectors.toMap(WishListQueryDto::getProductNo, WishListQueryDto::getProductId));

            // 찜하기, 상품타입 setter
            setProductDetails(searchResult.getItem(), productNoMap);
        }
    }

    private SearchSortType determineSearchQueryType(SearchSortType searchSortType, String searchKeyword) {
        switch (searchSortType) {
            case LOW_PRICE:
            case HIGH_PRICE:
                return SearchSortType.DISCOUNTED_PRICE;
            default:
                return searchSortType;
        }
    }

    private String determineSortDirection(SearchSortType searchSortType) {
        return (searchSortType == SearchSortType.LOW_PRICE) ? CustomValue.ASC : CustomValue.DESC;
    }

    private HttpResponse<String> searchByNosHttpResponse(int[] productNos) throws UnirestException {
        JSONObject json = new JSONObject();
        json.put("productNos", productNos);
        json.put("hasOptionValues", "false");

        HttpResponse<String> response = Unirest.post(shopByUrl + products + "/search-by-nos")
                .header("accept", acceptHeader)
                .header("version", versionHeader)
                .header("clientid", clientId)
                .header("platform", platformHeader)
                .header("content-type", acceptHeader)
                .body(gson.toJson(json))
                .asString();
        return response;
    }

    private List<SearchResponseDto.SearchResultItemInfo> shopbyGetMdPickProductListByProductNo(int[] productNoArray, List<Integer> eventProductNos) {
        try {

            HttpResponse<String> response = searchByNosHttpResponse(productNoArray);
            ShopBy.errorMessage(response);
            log.info("상품 검색 조회_____________________________E");

            // HttpResponse에서 JSON 응답 추출
            JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
            Long totalCount = jsonObject.get("totalCount").getAsLong();
            JsonArray productsArray = jsonObject.getAsJsonArray("products");
            List<SearchResponseDto.SearchResultItemInfo> searchResultItemInfoItemInfoList = new ArrayList<>();
            for (JsonElement element : productsArray) {
                JsonObject productObject = element.getAsJsonObject();
                JsonObject baseInfoObject = productObject.getAsJsonObject("baseInfo");
                JsonObject priceInfoObject = productObject.getAsJsonObject("price");
                // JSON 객체에서 필요한 값을 추출
                int productNo = baseInfoObject.get("productNo").getAsInt();

                if (eventProductNos.contains(productNo)) {
                    if (totalCount > 0) {
                        totalCount -= 1;
                    }
                    continue;
                }

                String productName = baseInfoObject.get("productName").getAsString();
                String brandName = baseInfoObject.get("brandName").getAsString();
                brandName = processBrandName(brandName);
                String imageUrl = baseInfoObject.getAsJsonArray("listImageUrls").get(0).getAsString();
                imageUrl = "https:" + imageUrl;
                int salePrice = priceInfoObject.get("salePrice").getAsInt();
                int immediateDiscountAmt = priceInfoObject.get("immediateDiscountAmt").getAsInt();
                int stockCnt = baseInfoObject.get("stockCnt").getAsInt();

                String displayCategoryNo = productObject.get("displayCategoryNos").getAsString();
                if (displayCategoryNo.length() >= 6) {
                    displayCategoryNo = displayCategoryNo.substring(0, 6);
                }

                if (todayPriceCategoryNo.equals(displayCategoryNo)) {
                    if (totalCount > 0) {
                        totalCount -= 1;
                    }
                    continue;
                }

                CategoryType productType = CategoryType.SAMPLE;
                if (displayCategoryNo.equals(kitCategoryNo)) {
                    productType = CategoryType.KIT;
                }

                SearchResponseDto.SearchResultItemInfo searchResultItemInfo
                        = new SearchResponseDto.SearchResultItemInfo
                        (productType, productNo, productName, brandName,
                                imageUrl, salePrice, immediateDiscountAmt, stockCnt,
                                displayCategoryNo);

                searchResultItemInfoItemInfoList.add(searchResultItemInfo);
            }

            return searchResultItemInfoItemInfoList;

        } catch (UnirestException | ParseException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private String processBrandName(String brandName) {
        int slashIndex = brandName.indexOf('/');
        return slashIndex != -1 ? brandName.substring(0, slashIndex).trim() : brandName.trim();
    }

    private void saveSearchKeyword(String searchKeyword) {
        // 검색어가 비어있는지 확인
        if (searchKeyword.isBlank()) {
            return;
        }

        // 검색어가 기존에 있는지 확인
        Optional<SearchKeyword> existingSearchKeywordOpt = searchKeywordRepository.findBySearchWord(searchKeyword);

        if (existingSearchKeywordOpt.isPresent()) {
            // 검색어가 이미 있다면 카운트를 올린다
            SearchKeyword existingSearchKeyword = existingSearchKeywordOpt.get();
            existingSearchKeyword.updateSearchCount();
        } else {
            // 새 검색어를 저장
            SearchKeyword newSearchKeyword = SearchKeyword.builder()
                    .searchWord(searchKeyword)
                    .build();
            searchKeywordRepository.save(newSearchKeyword);
        }
    }

    private void setProductDetails(List<SearchResponseDto.SearchResultItemInfo> items, Map<Integer, Long> productNoMap) {
        items.forEach(item -> {
            int productNo = item.getProductNo();
            item.setIsWishList(productNoMap.containsKey(productNo));
        });
    }


    /**
     * 샵바이 api의 요청명을 여기에 작성한다.
     * 인기 검색어 조회하기 : /products/favoriteKeywords
     * 샵바이 version 1.0.0
     **/
    private List<String> shopbyGetFavoriteKeywords() {
        try {
            HttpResponse<String> response = Unirest.get(shopByUrl + "/products/favoriteKeywords?size=10")
                    .header("accept", acceptHeader)
                    .header("version", versionHeader)
                    .header("clientid", clientId)
                    .header("platform", platformHeader)
                    .header("content-type", acceptHeader)
                    .asString();

            log.info("인기 검색어 조회_____________________________E");
            JsonArray itemsArray = gson.fromJson(response.getBody(), JsonArray.class);

            // JsonArray를 Java List로 변환
            List<String> keywordList = new ArrayList<>();
            for (int i = 0; i < itemsArray.size(); i++) {
                keywordList.add(itemsArray.get(i).getAsString());
            }

            return keywordList;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * 샵바이 api의 요청명을 여기에 작성한다.
     * 상품 검색 : /products/search
     * 샵바이 version 1.0.0
     **/
    private SearchResponseDto.SearchResult shopbyGetSearchProductBySearchKeyword(String searchKeyword, int pageNumber, int pageSize,
                                                                                 SearchSortType searchSortType, String sortDirection, List<Integer> eventProductNos) throws UnirestException, ParseException {

        try {
            searchKeyword = URLEncoder.encode(searchKeyword, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }

        HttpResponse<String> response = Unirest.get("https://shop-api.e-ncp.com/products/search" +
                        "?filter.saleStatus=ALL_CONDITIONS" +
                        "&filter.soldout=true" +
                        "&filter.includeNonDisplayableCategory=true" +
                        "&filter.totalReviewCount=true" +
                        "&excludeCategoryNos=" + eventCategoryNo +
                        "&order.by=" + searchSortType +
                        "&order.direction=" + sortDirection +
                        "&pageNumber=" + pageNumber +
                        "&pageSize=" + pageSize +
                        "&hasTotalCount=true&hasOptionValues=false" +
                        "&filter.keywords=" + searchKeyword)
                .header("accept", acceptHeader)
                .header("version", versionHeader)
                .header("clientid", clientId)
                .header("platform", platformHeader)
                .header("content-type", acceptHeader)
                .asString();

        ShopBy.errorMessage(response);

        List<SearchResponseDto.SearchResultItemInfo> searchResultItemInfoItemInfoList = new ArrayList<>();
        Long totalCount = getaLong(eventProductNos, response, searchResultItemInfoItemInfoList);

        return new SearchResponseDto.SearchResult(totalCount, searchResultItemInfoItemInfoList);
    }

    private Long getaLong(List<Integer> eventProductNos, HttpResponse<String> response, List<SearchResponseDto.SearchResultItemInfo> searchResultItemInfoItemInfoList) {
        JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
        Long totalCount = jsonObject.get("totalCount").getAsLong();
        JsonArray itemsArray = jsonObject.getAsJsonArray("items");
        for (JsonElement element : itemsArray) {
            JsonObject productObject = element.getAsJsonObject();
            int productNo = productObject.get("productNo").getAsInt();

            if (eventProductNos.contains(productNo)) {
                if (totalCount > 0) {
                    totalCount -= 1;
                }
                continue;
            }

            String productName = productObject.get("productName").getAsString();
            String brandName = productObject.get("brandName").getAsString();
            brandName = processBrandName(brandName);
            String imageUrl = productObject.getAsJsonArray("imageUrls").get(0).getAsString();
            imageUrl = "https:" + imageUrl;

            String displayCategoryNo = productObject.get("displayCategoryNos").getAsString();
            if (displayCategoryNo.length() >= 6) {
                displayCategoryNo = displayCategoryNo.substring(0, 6);
            }

            if (todayPriceCategoryNo.equals(displayCategoryNo)) {
                if (totalCount > 0) {
                    totalCount -= 1;
                }
                continue;
            }
            int salePrice = productObject.get("salePrice").getAsInt();
            int immediateDiscountAmt = productObject.get("immediateDiscountAmt").getAsInt();
            int stockCnt = productObject.get("stockCnt").getAsInt();

            CategoryType productType = CategoryType.SAMPLE;
            if (displayCategoryNo.equals(kitCategoryNo)) {
                productType = CategoryType.KIT;
            }

            SearchResponseDto.SearchResultItemInfo searchResultItemInfo
                    = new SearchResponseDto.SearchResultItemInfo
                    (productType, productNo, productName, brandName,
                            imageUrl, salePrice, immediateDiscountAmt, stockCnt,
                            displayCategoryNo);

            searchResultItemInfoItemInfoList.add(searchResultItemInfo);

        }

        if (searchResultItemInfoItemInfoList.size() == 0) {
            totalCount = 0L;
        }
        return totalCount;
    }

    private SearchResponseDto.SearchResult shopbyGetSearchProductBySearchKeyword(String searchKeyword, int pageNumber, int pageSize,
                                                                                 List<Integer> eventProductNos,
                                                                                 String discountRateProductNos) throws UnirestException, ParseException {

        try {
            searchKeyword = URLEncoder.encode(searchKeyword, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }

        HttpResponse<String> response = Unirest.get("https://shop-api.e-ncp.com/products/search" +
                        "?filter.saleStatus=ALL_CONDITIONS" +
                        "&filter.soldout=true" +
                        "&filter.includeNonDisplayableCategory=true" +
                        "&filter.totalReviewCount=true" +
                        "&pageNumber=" + pageNumber +
                        "&pageSize=" + pageSize +
                        "&hasTotalCount=true&hasOptionValues=false" +
                        "&filter.keywords=" + searchKeyword +
                        "&filter.customProperties.propNos" + discountRateProductNos)
                .header("accept", acceptHeader)
                .header("version", versionHeader)
                .header("clientid", clientId)
                .header("platform", platformHeader)
                .header("content-type", acceptHeader)
                .asString();

        ShopBy.errorMessage(response);

        List<SearchResponseDto.SearchResultItemInfo> searchResultItemInfoItemInfoList = new ArrayList<>();
        Long totalCount = getaLong(eventProductNos, response, searchResultItemInfoItemInfoList);

        return new SearchResponseDto.SearchResult(totalCount, searchResultItemInfoItemInfoList);
    }

    /**
     * 샵바이 api의 요청명을 여기에 작성한다.
     * 상품 검색 : /products/search
     * 샵바이 version 1.0.0
     **/
    private SearchResponseDto.SearchResult shopbyGetSearchBrandProductByBrandNo(int brandNo, int pageNumber, int pageSize,
                                                                                SearchSortType searchSortType, String sortDirection,
                                                                                List<Integer> eventProductNos, int categoryNumber) throws UnirestException, ParseException {

        if (categoryNumber == 0) {
            categoryNumber = sampleCategoryNo;
        }

        HttpResponse<String> response = Unirest.get("https://shop-api.e-ncp.com/products/search" +
                        "?filter.saleStatus=ALL_CONDITIONS" +
                        "&filter.soldout=true" +
                        "&filter.totalReviewCount=true" +
                        "&excludeCategoryNos=" + eventCategoryNo +
                        "&brandNos=" + brandNo +
                        "&order.by=" + searchSortType +
                        "&order.direction=" + sortDirection +
                        "&order.soldoutPlaceEnd=" + true +
                        "&categoryNos=" + categoryNumber +
                        "&pageNumber=" + pageNumber +
                        "&pageSize=" + pageSize +
                        "&hasTotalCount=true&hasOptionValues=false")
                .header("accept", acceptHeader)
                .header("version", versionHeader)
                .header("clientid", clientId)
                .header("platform", platformHeader)
                .header("content-type", acceptHeader)
                .asString();

        ShopBy.errorMessage(response);

        List<SearchResponseDto.SearchResultItemInfo> searchResultItemInfoItemInfoList = new ArrayList<>();
        Long totalCount = getaLong(eventProductNos, response, searchResultItemInfoItemInfoList);

        return new SearchResponseDto.SearchResult(totalCount, searchResultItemInfoItemInfoList);
    }

    private SearchResponseDto.SearchResult shopbyGetSearchBrandProductByBrandNo(int brandNo, int pageNumber, int pageSize,
                                                                                List<Integer> eventProductNos,
                                                                                int categoryNumber, String discountRateProductNos) throws UnirestException, ParseException {
        if (categoryNumber == 0) {
            categoryNumber = sampleCategoryNo;
        }

        HttpResponse<String> response = Unirest.get("https://shop-api.e-ncp.com/products/search" +
                        "?filter.saleStatus=ALL_CONDITIONS" +
                        "&filter.soldout=true" +
                        "&filter.totalReviewCount=true" +
                        "&excludeCategoryNos=" + eventCategoryNo +
                        "&brandNos=" + brandNo +
                        "&order.soldoutPlaceEnd=" + true +
                        "&pageNumber=" + pageNumber +
                        "&pageSize=" + pageSize +
                        "&hasTotalCount=true&hasOptionValues=false" +
                        "&filter.customProperties.propNos" + discountRateProductNos)
                .header("accept", acceptHeader)
                .header("version", versionHeader)
                .header("clientid", clientId)
                .header("platform", platformHeader)
                .header("content-type", acceptHeader)
                .asString();

        ShopBy.errorMessage(response);

        List<SearchResponseDto.SearchResultItemInfo> searchResultItemInfoItemInfoList = new ArrayList<>();
        Long totalCount = getaLong(eventProductNos, response, searchResultItemInfoItemInfoList);

        return new SearchResponseDto.SearchResult(totalCount, searchResultItemInfoItemInfoList);
    }
}
