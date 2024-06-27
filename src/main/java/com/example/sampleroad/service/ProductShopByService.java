package com.example.sampleroad.service;

import com.example.sampleroad.common.exception.ErrorCode;
import com.example.sampleroad.common.exception.ErrorCustomException;
import com.example.sampleroad.common.utils.ShopBy;
import com.example.sampleroad.domain.CategoryType;
import com.example.sampleroad.domain.product.Product;
import com.example.sampleroad.domain.product.ProductType;
import com.example.sampleroad.domain.search.SearchSortType;
import com.example.sampleroad.dto.request.ProductRequestDto;
import com.example.sampleroad.dto.response.BestSellerResponseDto;
import com.example.sampleroad.dto.response.home.HomeResponseDto;
import com.example.sampleroad.dto.response.product.ProductDetailResponseDto;
import com.example.sampleroad.dto.response.product.ProductInfoDto;
import com.example.sampleroad.dto.response.wishList.WishListResponseDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.example.sampleroad.repository.product.ProductRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductShopByService {

    private final ProductRepository productRepository;

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
    @Value("${shop-by.sample-category-no}")
    int sampleCategoryNo;
    @Value("${shop-by.experience-category-no}")
    String experienceCategoryNo;
    @Value("${shop-by.group-purchase-category-no}")
    String groupPurchaseCategoryNo;
    @Value("${shop-by.event-category-no}")
    int eventCategoryNo;
    @Value("${shop-by.today-price-category-no}")
    String todayPriceCategoryNo;
    Gson gson = new Gson();

    private JsonObject shopbyGetProductListByProductNo(int productNo) {
        try {
            HttpResponse<String> response = shopbyGetProductListRequest(productNo);

            if (response.getStatus() == 404) {
                ShopBy.errorMessage(response, true);
            } else {
                ShopBy.errorMessage(response);
            }

            // HttpResponse에서 JSON 응답 추출
            return gson.fromJson(response.getBody(), JsonObject.class);

        } catch (UnirestException | ParseException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 상품 상세 조회하기
     * 샵바이 version 1.0.0
     **/
    private HttpResponse<String> shopbyGetProductListRequest(int productNo) throws UnirestException {

        return Unirest.get(shopByUrl + "/products/" + productNo)
                .header("accept", acceptHeader)
                .header("version", versionHeader)
                .header("clientid", clientId)
                .header("platform", platformHeader)
                .header("accesstoken", "")
                .header("content-type", acceptHeader)
                .asString();
    }

    /**
     * search-by-nos -> productNos로 상품조회
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 3/25/24
     **/
    public HttpResponse<String> shopbyGetProductListRequest(int[] productNos) throws UnirestException {

        JSONObject json = new JSONObject();
        json.put("productNos", productNos);
        json.put("hasOptionValues", "false");

        HttpResponse<String> response = Unirest.post(shopByUrl + "/products/search-by-nos")
                .header("accept", acceptHeader)
                .header("version", versionHeader)
                .header("clientid", clientId)
                .header("platform", platformHeader)
                .header("content-type", acceptHeader)
                .body(gson.toJson(json))
                .asString();
        return response;
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
    public ProductDetailResponseDto getProductInfo(UserDetailsImpl userDetail, int productNo) {
        JsonObject productJsonObject = shopbyGetProductListByProductNo(productNo);

        if (productJsonObject == null) {
            return new ProductDetailResponseDto();
        }

        return shopByGetProductDetailInfo(productJsonObject);
    }

    public Product getProduct(int productNo) {
        return productRepository.findByProductNoAndProductInvisible(productNo, false)
                .orElseThrow(() -> new ErrorCustomException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    private ProductDetailResponseDto shopByGetProductDetailInfo(JsonObject productJsonObject) {

        JsonObject baseInfoObject = productJsonObject.getAsJsonObject("baseInfo");
        JsonObject priceInfoObject = productJsonObject.getAsJsonObject("price");
        JsonObject stockInfoObject = productJsonObject.getAsJsonObject("stock");
        JsonObject brandObject = productJsonObject.getAsJsonObject("brand");
        JsonObject counterObject = productJsonObject.getAsJsonObject("counter");
        JsonObject deliveryInfoObject = productJsonObject.getAsJsonObject("deliveryFee");

        // JSON 객체에서 필요한 값을 추출
        int productNo = baseInfoObject.get("productNo").getAsInt();
        String productName = baseInfoObject.get("productName").getAsString();
        String brandName = brandObject.get("name").getAsString();
        int brandNo = brandObject.get("brandNo").getAsInt();

        JsonArray imageUrlJson = baseInfoObject.getAsJsonArray("imageUrls");
        List<String> productMainImgUrlList = new ArrayList<>();
        for (JsonElement element : imageUrlJson) {
            String imageUrl = element.getAsString();
            imageUrl = "https:" + imageUrl;
            productMainImgUrlList.add(imageUrl);
        }
        String[] imageUrls = productMainImgUrlList.toArray(new String[0]);

        int stock = stockInfoObject.get("stockCnt").getAsInt();
        int salePrice = priceInfoObject.get("salePrice").getAsInt();
        int immediateDiscountAmt = priceInfoObject.get("immediateDiscountAmt").getAsInt();
        int couponDiscountAmt = priceInfoObject.get("couponDiscountAmt").getAsInt();
        int likeCnt = counterObject.get("likeCnt").getAsInt();

        boolean hasProductCoupon = couponDiscountAmt != 0;

        int deliveryFee = deliveryInfoObject.get("deliveryAmt").getAsInt();
        int returnDeliveryAmt = deliveryInfoObject.get("returnDeliveryAmt").getAsInt();

        String dutyInfo = baseInfoObject.get("dutyInfo").getAsString();
        List<String> dutyInfos = dutyInfoParser(dutyInfo);

        String content = baseInfoObject.get("content").getAsString();
        List<String> productDetailImgUrlList = getContentImageUrl(content);
        String[] contentImageUrl = productDetailImgUrlList.toArray(new String[0]);

        Long totalReviewCount = counterObject.get("reviewCnt").getAsLong();
        Double reviewRating = productJsonObject.get("reviewRate").getAsDouble();

        return new ProductDetailResponseDto(
                hasProductCoupon, productNo, productName, brandName, brandNo, imageUrls
                , stock, salePrice, immediateDiscountAmt, likeCnt, deliveryFee, returnDeliveryAmt
                , contentImageUrl, dutyInfos, totalReviewCount, reviewRating);
    }

    /**
     * search-by-nos
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 4/15/24
     **/
    public List<HomeResponseDto.ProductSectionDto> shopbyGetProductList(int[] productNos) throws UnirestException {

        HttpResponse<String> response = this.shopbyGetProductListRequest(productNos);

        JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
        JsonArray productsArray = jsonObject.getAsJsonArray("products");
        List<HomeResponseDto.ProductSectionDto> productSectionDtoList = new ArrayList<>();
        for (JsonElement element : productsArray) {
            ProductInfoDto productInfo = parseProductInfo(element.getAsJsonObject());
            HomeResponseDto.ProductSectionDto productSectionDto
                    = new HomeResponseDto.ProductSectionDto(CategoryType.SAMPLE, productInfo);

            productSectionDtoList.add(productSectionDto);
        }

        return productSectionDtoList;
    }

    /**
     * search-by-nos
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 4/15/24
     **/
    public List<WishListResponseDto.WishListProducts> shopbyGetProductListByProductNo(int[] productNos) {
        try {
            HttpResponse<String> response = shopbyGetProductListRequest(productNos);
            log.info("찜목록 상품조회_____________________________");
            ShopBy.errorMessage(response);
            log.info("찜목록 상품조회_____________________________");

            List<WishListResponseDto.WishListProducts> productList = new ArrayList<>();

            // HttpResponse에서 JSON 응답 추출
            JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
            JsonArray productsArray = jsonObject.getAsJsonArray("products");

            for (JsonElement element : productsArray) {
                ProductInfoDto productInfo = parseProductInfo(element.getAsJsonObject());
                // ProductType 매핑을 위한 Map 초기화
                Map<String, ProductType> categoryToProductType = new HashMap<>();
                categoryToProductType.put(kitCategoryNo, ProductType.KIT);
                categoryToProductType.put(experienceCategoryNo, ProductType.ZERO_SAMPLE);
                categoryToProductType.put(groupPurchaseCategoryNo, ProductType.GROUP_PURCHASE);
                // 추가적인 ProductType과 카테고리 번호 매핑이 필요하면 여기에 추가

                // displayCategoryNos를 기반으로 ProductType 결정
                ProductType productType = categoryToProductType.getOrDefault(productInfo.getDisplayCategoryNo(), ProductType.SAMPLE);

                WishListResponseDto.WishListProducts product =
                        new WishListResponseDto.WishListProducts(productType, productInfo);

                productList.add(product);
            }
            return productList;
        } catch (UnirestException | ParseException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private ProductInfoDto parseProductInfo(JsonObject productObject) {
        JsonObject baseInfoObject = productObject.getAsJsonObject("baseInfo");

        if (baseInfoObject == null) {
            baseInfoObject = productObject;
        }

        int productNo = baseInfoObject.get("productNo").getAsInt();
        int stockCnt = baseInfoObject.get("stockCnt").getAsInt();
        String productName = baseInfoObject.get("productName").getAsString();
        Double reviewRating = baseInfoObject.get("reviewRating").getAsDouble();
        int totalReviewCount = baseInfoObject.get("totalReviewCount").getAsInt();
        String brandName = baseInfoObject.get("brandName").getAsString(); // 'brandNameKo' 또는 'brandName' 필드에 따라 조정
        brandName = processBrandName(brandName); // 이 부분은 필요에 따라 메서드를 구현하시거나 제외하십시오.
        String imageUrl = "https:" + baseInfoObject.getAsJsonArray("imageUrls").get(0).getAsString(); // 'imageUrls' 또는 'listImageUrls' 필드에 따라 조정

        String displayCategoryNo = baseInfoObject.get("displayCategoryNos").getAsString();
        if (displayCategoryNo.length() >= 6) {
            displayCategoryNo = displayCategoryNo.substring(0, 6);
        }

        JsonObject priceObject = productObject.getAsJsonObject("price");
        if (priceObject == null) {
            priceObject = productObject;
        }
        int salePrice = priceObject.get("salePrice").getAsInt();
        int immediateDiscountAmt = priceObject.get("immediateDiscountAmt").getAsInt();

        // 여기서 ProductInfo는 공통 필드를 가진 DTO 클래스입니다.
        // 해당 클래스의 정의가 필요합니다. 예시는 아래에 제공되어 있습니다.
        return new ProductInfoDto(productNo, productName,
                brandName, imageUrl,
                salePrice, immediateDiscountAmt,
                stockCnt, reviewRating,
                totalReviewCount, displayCategoryNo);
    }


    /**
     * 샵바이 상품 상세 요청
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 4/11/24
     **/
    public ProductDetailResponseDto shopByGetProductDetailInfo(JsonObject optionJsonObject, ProductDetailResponseDto productDetailResponseDto) {

        JsonArray optionInfos = optionJsonObject.getAsJsonArray("optionInfos");
        JsonObject firstOptionInfo = optionInfos.get(0).getAsJsonObject();
        JsonArray optionsArray = firstOptionInfo.getAsJsonArray("options");

        int[] optionNos = new int[optionsArray.size()];
        String[] labels = new String[optionsArray.size()];
        String[] values = new String[optionsArray.size()];
        Integer[] addPrices = new Integer[optionsArray.size()];
        Integer[] stockCnts = new Integer[optionsArray.size()];
        for (int i = 0; i < optionsArray.size(); i++) {
            JsonObject asJsonObject = optionsArray.get(i).getAsJsonObject();
            int optionNo = asJsonObject.get("optionNo").getAsInt();
            String label = asJsonObject.get("label").getAsString();
            String value = asJsonObject.get("value").getAsString();
            int addPrice = asJsonObject.get("addPrice").getAsInt();
            int stockCnt = asJsonObject.get("stockCnt").getAsInt();
            optionNos[i] = optionNo;
            labels[i] = label;
            values[i] = value;
            addPrices[i] = addPrice;
            stockCnts[i] = stockCnt;
        }

        return new ProductDetailResponseDto(productDetailResponseDto, optionNos, labels, values, addPrices, stockCnts);
    }

    /**
     * 옵션 조회하기
     * 샵바이 version 1.0.0
     **/
    public JsonObject shopByGetProductOptions(int productNo) {
        try {
            HttpResponse<String> response = Unirest.get(shopByUrl + "/products/options")
                    .queryString("productNos", productNo)
                    .header("accept", acceptHeader)
                    .header("version", versionHeader)
                    .header("clientid", clientId)
                    .header("platform", platformHeader)
                    .header("accesstoken", "")
                    .header("content-type", acceptHeader)
                    .asString();

            ShopBy.errorMessage(response);
            log.info("옵션 조회_____________________________E");

            // JSON 데이터를 JsonObject로 파싱
            return gson.fromJson(response.getBody(), JsonObject.class);
        } catch (UnirestException | ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 인기 상품 조회하기
     * search
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 4/12/24
     **/
    public List<BestSellerResponseDto> shopByGetBestSeller() throws ParseException, UnirestException {
        int categoryNumber = sampleCategoryNo;
        String orderBy = SearchSortType.POPULAR.toString();

        HttpResponse<String> response = getProductSearchHttpResponse(categoryNumber, orderBy);

        List<BestSellerResponseDto> bestSellerList = new ArrayList<>();
        JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
        JsonArray itemsArray = jsonObject.getAsJsonArray("items");
        for (JsonElement element : itemsArray) {
            ProductInfoDto productInfoDto = parseProductInfo(element.getAsJsonObject());
            CategoryType productType = CategoryType.SAMPLE;
            if (productInfoDto.getDisplayCategoryNo().equals(kitCategoryNo)) {
                productType = CategoryType.KIT;
            }

            BestSellerResponseDto bestSellerResponseDto = new BestSellerResponseDto(productType, productInfoDto);

            bestSellerList.add(bestSellerResponseDto);
        }
        return bestSellerList;
    }

    private HttpResponse<String> getProductSearchHttpResponse(int categoryNumber, String orderBy) throws UnirestException, ParseException {
        HttpResponse<String> response = Unirest.get("https://shop-api.e-ncp.com/products/search" +
                        "?filter.saleStatus=ALL_CONDITIONS&filter.soldout=true" +
                        "&filter.totalReviewCount=true" +
                        "&excludeCategoryNos=" + eventCategoryNo + "," + experienceCategoryNo +
                        "&order.by=" + orderBy +
                        "&order.direction=" + "DESC" +
                        "&order.soldoutPlaceEnd=" + true +
                        "&categoryNos=" + categoryNumber +
                        "&pageNumber=" + 1 +
                        "&pageSize=" + 20 +
                        "&hasTotalCount=true&hasOptionValues=false")
                .header("accept", acceptHeader)
                .header("version", versionHeader)
                .header("clientid", clientId)
                .header("platform", platformHeader)
                .header("accesstoken", "")
                .header("content-type", acceptHeader)
                .asString();
        ShopBy.errorMessage(response);
        log.info("배스트 상품조회_____________________________E");
        return response;
    }

    private List<String> dutyInfoParser(String dutyInfo) {
        List<String> contentValues = new ArrayList<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(dutyInfo);
            try {
                JsonNode contentsNode = jsonNode.get("contents");
                for (JsonNode contentNode : contentsNode) {
                    String contentValue = contentNode.get(contentNode.fields().next().getKey()).asText();
                    contentValues.add(contentValue);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return contentValues;
    }

    public List<ProductDetailResponseDto.SampleList> shopbyGetRelatedProducts(int productNo, String brandName) throws UnirestException, ParseException {
        HttpResponse<String> response = Unirest.get(shopByUrl + "/products/" + productNo + "/related-products")
                .header("accept", acceptHeader)
                .header("version", versionHeader)
                .header("clientid", clientId)
                .header("platform", platformHeader)
                .header("accesstoken", "")
                .header("content-type", acceptHeader)
                .asString();

        if (response.getStatus() != 200) {
            ShopBy.errorMessage(response);
        }

        JsonElement jsonElement = gson.fromJson(response.getBody(), JsonElement.class);
        JsonArray relatedItemsArray = jsonElement.getAsJsonArray();
        List<ProductDetailResponseDto.SampleList> sampleList = new ArrayList<>();
        for (JsonElement element : relatedItemsArray) {
            if (element.isJsonObject()) {
                JsonObject item = element.getAsJsonObject();
                int relatedProductNo = item.get("productNo").getAsInt();
                String productName = item.get("productName").getAsString();
                String imageUrl = item.get("imageUrl").getAsString();
                imageUrl = "https:" + imageUrl;

                ProductDetailResponseDto.SampleList sampleItem = new ProductDetailResponseDto.SampleList(
                        relatedProductNo, productName, brandName, imageUrl, productNo
                );
                sampleList.add(sampleItem);
            }
        }

        return sampleList;
    }

    /**
     * 장바구니에서 배송비 넘기기 위한 가격 저렴한 상품 조회
     *
     * @param
     * @param
     * @param excludeProductNos
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 3/13/24
     **/
    public List<BestSellerResponseDto> getProductForDeliveryPrice(int addPriceForFreeDelivery, List<Integer> excludeProductNos) throws UnirestException, ParseException {
        // TODO: 2024-03-13 배송비를 위한 저렴이 상품 조회
        HttpResponse<String> response = getProductSearchHttpResponse(addPriceForFreeDelivery);

        List<BestSellerResponseDto> bestSellerList = new ArrayList<>();
        JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
        JsonArray itemsArray = jsonObject.getAsJsonArray("items");
        for (JsonElement element : itemsArray) {
            ProductInfoDto productInfoDto = parseProductInfo(element.getAsJsonObject());
            CategoryType productType = CategoryType.SAMPLE;
            if (productInfoDto.getDisplayCategoryNo().equals(kitCategoryNo)) {
                productType = CategoryType.KIT;
            }

            if (excludeProductNos.contains(productInfoDto.getProductNo())
                    || productInfoDto.getDisplayCategoryNo().equals(todayPriceCategoryNo)
                    || (productInfoDto.getSalePrice() - productInfoDto.getImmediateDiscountAmt()) <= 1000) {
                continue;
            }

            BestSellerResponseDto bestSellerResponseDto =
                    new BestSellerResponseDto(productType, productInfoDto);

            bestSellerList.add(bestSellerResponseDto);
        }
        return bestSellerList.stream().limit(10).collect(Collectors.toList());
    }

    private HttpResponse<String> getProductSearchHttpResponse(int addPriceForFreeDelivery) throws UnirestException, ParseException {
        HttpResponse<String> response = Unirest.get("https://shop-api.e-ncp.com/products/search" +
                        "?filter.saleStatus=ALL_CONDITIONS&filter.soldout=false" +
                        "&filter.discountedPrices=" + addPriceForFreeDelivery +
                        "&filter.discountedComparison=GTE" +
                        "&excludeCategoryNos=" + eventCategoryNo + "," + experienceCategoryNo +
                        "&order.by=" + "DISCOUNTED_PRICE" +
                        "&order.direction=" + "ASC" +
                        "&order.soldoutPlaceEnd=" + true +
                        "&pageNumber=" + 1 +
                        "&pageSize=" + 40 +
                        "&hasTotalCount=true&hasOptionValues=false")
                .header("accept", acceptHeader)
                .header("version", versionHeader)
                .header("clientid", clientId)
                .header("platform", platformHeader)
                .header("accesstoken", "")
                .header("content-type", acceptHeader)
                .asString();

        log.info("배송비를 위한 저렴이 상품조회_____________________________E");
        ShopBy.errorMessage(response);
        log.info("배송비를 위한 저렴이 상품조회_____________________________E");
        return response;
    }

    public List<ProductDetailResponseDto.RecentProductInfo> shopByGetNotMemberRecentProduct(ProductRequestDto.RecentProducts productRequestDto) throws UnirestException, ParseException {

        List<Integer> recentProducts = productRequestDto.getRecentProductNos();
        String recentProductStr = recentProducts.stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));

        HttpResponse<String> response = Unirest.get(shopByUrl + "/guest/recent-products")
                .queryString("mallProductNos", recentProductStr)
                .header("accept", acceptHeader)
                .header("version", versionHeader)
                .header("clientid", clientId)
                .header("platform", platformHeader)
                .header("content-type", acceptHeader)
                .asString();

        if (response.getStatus() != 200) {
            ShopBy.errorMessage(response);
        }

        List<ProductDetailResponseDto.RecentProductInfo> recentProductInfoList = new ArrayList<>();

        JsonElement jsonElement = gson.fromJson(response.getBody(), JsonElement.class);
        JsonArray recentItemsArray = jsonElement.getAsJsonArray();

        for (int i = 0; i < recentItemsArray.size(); i++) {
            JsonObject item = recentItemsArray.get(i).getAsJsonObject();
            int productNo = item.get("productNo").getAsInt();
            int salePrice = item.get("salePrice").getAsInt();
            int immediateDiscountAmt = item.get("immediateDiscountAmt").getAsInt();
            String productName = item.get("productName").getAsString();
            String brandName = item.get("brandName").getAsString();
            String imageUrl = item.getAsJsonArray("imageUrls").get(0).getAsString();
            imageUrl = "https:" + imageUrl;
            int stockCnt = item.get("stockCnt").getAsInt();
            String displayCategoryNos = item.get("displayCategoryNos").getAsString();

            CategoryType productType = CategoryType.SAMPLE;
            if (displayCategoryNos.equals(kitCategoryNo)) {
                productType = CategoryType.KIT;
            }


            ProductDetailResponseDto.RecentProductInfo recentProductInfo
                    = new ProductDetailResponseDto.RecentProductInfo
                    (productType, productNo, productName, brandName,
                            imageUrl, salePrice, immediateDiscountAmt, stockCnt);

            recentProductInfoList.add(recentProductInfo);
        }
        return recentProductInfoList;

    }


    private String processBrandName(String brandName) {
        int slashIndex = brandName.indexOf('/');
        return slashIndex != -1 ? brandName.substring(0, slashIndex).trim() : brandName.trim();
    }

    private static List<String> getContentImageUrl(String content) {
        String regex = "<img src=\"(.*?)\\.(jpe?g|png|gif)\"";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        List<String> productDetailImgUrlList = new ArrayList<>();
        while (matcher.find()) {
            String url = matcher.group(1) + "." + matcher.group(2);
            url = "https:" + url;
            productDetailImgUrlList.add(url);

        }
        return productDetailImgUrlList;
    }
}