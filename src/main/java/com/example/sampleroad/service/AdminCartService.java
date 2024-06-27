package com.example.sampleroad.service;

import com.example.sampleroad.common.exception.ErrorCode;
import com.example.sampleroad.common.exception.ErrorCustomException;
import com.example.sampleroad.common.utils.ShopBy;
import com.example.sampleroad.domain.Category;
import com.example.sampleroad.domain.member.Member;
import com.example.sampleroad.domain.product.Product;
import com.example.sampleroad.dto.request.AdminProductRequestDto;
import com.example.sampleroad.dto.response.AdminProductResponseDto;
import com.example.sampleroad.dto.response.product.ProductResponseDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.example.sampleroad.repository.category.CategoryRepository;
import com.example.sampleroad.repository.member.MemberRepository;
import com.example.sampleroad.repository.product.ProductRepository;
import com.example.sampleroad.repository.review.ReviewRepository;
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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AdminCartService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final MemberRepository memberRepository;
    private final ProductService productService;
    private final ReviewRepository reviewRepository;

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

    @Value("${shop-by.server-profile}")
    String serverProfile;
    Gson gson = new Gson();

    @Transactional
    public HashMap<String, Object> updateProductOptionNo(AdminProductRequestDto dto) {

        // productOptionNo null인 애들 찾아서 한방에 update
        Product product = productService.getProduct(dto.getProductNo());
        int optionNo = shopByGetProductOptions(dto.getProductNo(), null);
        product.updateProductOptionNo(optionNo);
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("productId", product.getId());
        resultMap.put("optionNo", optionNo);
        return resultMap;

    }

    @Transactional
    public void updateProductOptionNo(int productNo, String shopByAccessToken) {

        Product product = productService.getProduct(productNo);
        int optionNo = shopByGetProductOptions(productNo, shopByAccessToken);
        product.updateProductOptionNo(optionNo);

    }

    @Transactional
    public HashMap<String, Object> addProductByAdmin(AdminProductRequestDto dto) {

        Long adminMemberId = 1L;
        if ("dev".equals(serverProfile)) {
            adminMemberId = 194L;
        }

        Member member = memberRepository.findById(adminMemberId)
                .orElseThrow(() -> new ErrorCustomException(ErrorCode.NO_USER_ERROR));

        AdminProductResponseDto adminProductResponseDto = shopbyGetProductListByProductNo(dto.getProductNo(), member.getShopByAccessToken());
        Optional<Category> category;
        if (dto.getIsSample()) {
            category = categoryRepository.findByCategoryDepthNumber3(adminProductResponseDto.getCategoryNo());
        } else {
            category = categoryRepository.findByCategoryDepthNumber1(adminProductResponseDto.getCategoryNo());
        }

        boolean existsByProductNo = productRepository.existsByProductNo(dto.getProductNo());

        if (existsByProductNo) {
            throw new ErrorCustomException(ErrorCode.ALREADY_REGISTER_PRODUCT);
        }

        if (category.isPresent()) {
            Product product = Product.builder()
                    .productNo(adminProductResponseDto.getProductNo())
                    .brandName(adminProductResponseDto.getBrandName())
                    .brandNo(adminProductResponseDto.getBrandNo())
                    .productName(adminProductResponseDto.getProductName())
                    .imgUrl(adminProductResponseDto.getImageUrl())
                    .category(category.get())
                    .build();

            productRepository.save(product);

            updateProductOptionNo(product.getProductNo(), member.getShopByAccessToken());

            HashMap<String, Object> resultMap = new HashMap<>();
            resultMap.put("productId", product.getId());
            resultMap.put("productNo", product.getProductNo());
            return resultMap;
        } else {
            throw new ErrorCustomException(ErrorCode.NOT_REGISTER_CATEGORY);
        }
    }

    @Transactional
    public HashMap<String, Object> updateProductInfo(UserDetailsImpl userDetails) {
        Member member = memberRepository.findById(userDetails.getMember().getId())
                .orElseThrow(() -> new ErrorCustomException(ErrorCode.NO_USER_ERROR));

/*
        if (!member.getMemberLoginId().equals("tlfqk13")) {
            return null;
        }
*/

        List<Product> product = productRepository.findByProductInvisible(false);

        int[] productNoArray = product.stream()
                .mapToInt(Product::getProductNo)
                .toArray();

        List<ProductResponseDto> productResponseDtos = shopbyGetMdPickProductListByProductNo(productNoArray);

        // 변수에 저장
        for (ProductResponseDto currentDto : productResponseDtos) {
            int salePrice = currentDto.getSalePrice();
            int immediateDiscountAmt = currentDto.getImmediateDiscountAmt();
            if (salePrice == 0) {
                salePrice = 1;
            }

            double discountRate = ((((double) immediateDiscountAmt / salePrice))) * 100; // 부동소수점 나눗셈 사용
            Product product1 = productService.getProduct(currentDto.getProductNo());

            product1.updateProductDiscountRate(discountRate);
            product1.updateProductImgUrl(currentDto.getImageUrl());
            product1.updateProductBrandNo(currentDto.getBrandNo());
            Double reviewRateAvg = Optional.ofNullable(reviewRepository.findReviewRateAvg(currentDto.getProductNo())).orElse(0.0);
            product1.updateProductReviewRate(reviewRateAvg);
        }
        return new HashMap<>();

    }

    private AdminProductResponseDto shopbyGetProductListByProductNo(int productNoByDto, String shopByAccessToken) {
        AdminProductResponseDto adminProductResponseDto = new AdminProductResponseDto();
        try {
            HttpResponse<String> response = Unirest.get(shopByUrl + products + "/" + productNoByDto)
                    .header("accept", acceptHeader)
                    .header("version", versionHeader)
                    .header("clientid", clientId)
                    .header("platform", platformHeader)
                    .header("accesstoken", shopByAccessToken)
                    .header("content-type", acceptHeader)
                    .asString();

            ShopBy.errorMessage(response);
            log.info("상품 상세 조회_____________________________E");

            // HttpResponse에서 JSON 응답 추출
            JsonObject productJsonObject = gson.fromJson(response.getBody(), JsonObject.class);
            JsonObject baseInfoObject = productJsonObject.getAsJsonObject("baseInfo");
            JsonObject brandObject = productJsonObject.getAsJsonObject("brand");
            JsonArray categoriesArray = productJsonObject.getAsJsonArray("categories");

            int productNo = baseInfoObject.get("productNo").getAsInt();
            String productName = baseInfoObject.get("productName").getAsString();
            String brandName = brandObject.get("name").getAsString();
            int brandNo = brandObject.get("brandNo").getAsInt();
            String imageUrl = baseInfoObject.getAsJsonArray("imageUrls").get(0).getAsString();
            imageUrl = "https:" + imageUrl;
            int categoryNo = 0;
            for (int i = 0; i < categoriesArray.size(); i++) {
                JsonObject categoryObject = categoriesArray.get(i).getAsJsonObject();
                JsonArray innerCategoriesArray = categoryObject.getAsJsonArray("categories");
                for (int j = 0; j < innerCategoriesArray.size(); j++) {
                    JsonObject innerCategoryObject = innerCategoriesArray.get(j).getAsJsonObject();
                    categoryNo = innerCategoryObject.get("categoryNo").getAsInt();
                    log.info("admin product add categoryNo -> " + categoryNo);
                }
            }

            adminProductResponseDto = new AdminProductResponseDto(productNo, productName, brandName, brandNo, imageUrl, categoryNo);

        } catch (UnirestException | ParseException e) {
            e.printStackTrace();
            return null;
        }
        return adminProductResponseDto;
    }

    /**
     * 옵션 조회하기
     * 샵바이 version 1.0.0
     **/
    private int shopByGetProductOptions(int productNo, String shopByAccessToken) {
        try {
            HttpResponse<String> response = Unirest.get(shopByUrl + products + "/options")
                    .queryString("productNos", productNo)
                    .header("accept", acceptHeader)
                    .header("version", versionHeader)
                    .header("clientid", clientId)
                    .header("platform", platformHeader)
                    .header("accesstoken", shopByAccessToken)
                    .header("content-type", acceptHeader)
                    .asString();

            ShopBy.errorMessage(response);
            log.info("옵션 조회_____________________________E");

            JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
            JsonArray optionInfos = jsonObject.getAsJsonArray("optionInfos");
            JsonObject firstOptionInfo = optionInfos.get(0).getAsJsonObject();
            JsonArray optionsArray = firstOptionInfo.getAsJsonArray("options");
            int optionNo = 0;
            for (int i = 0; i < optionsArray.size(); i++) {
                JsonObject asJsonObject = optionsArray.get(i).getAsJsonObject();
                optionNo = asJsonObject.get("optionNo").getAsInt();
            }

            // JSON 데이터를 JsonObject로 파싱
            return optionNo;
        } catch (UnirestException | ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private List<ProductResponseDto> shopbyGetMdPickProductListByProductNo(int[] productNoArray) {
        try {
            HttpResponse<String> response = shopbyGetProductListRequest(productNoArray);
            ShopBy.errorMessage(response);
            List<ProductResponseDto> productList = new ArrayList<>();

            // HttpResponse에서 JSON 응답 추출
            JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
            JsonArray productsArray = jsonObject.getAsJsonArray("products");

            for (JsonElement element : productsArray) {
                JsonObject productObject = element.getAsJsonObject();
                JsonObject baseInfoObject = productObject.getAsJsonObject("baseInfo");
                JsonObject priceInfoObject = productObject.getAsJsonObject("price");
                // JSON 객체에서 필요한 값을 추출
                int productNo = baseInfoObject.get("productNo").getAsInt();
                int stockCnt = baseInfoObject.get("stockCnt").getAsInt();
                String productName = baseInfoObject.get("productName").getAsString();
                String brandName = baseInfoObject.get("brandName").getAsString();
                brandName = processBrandName(brandName);
                int brandNo = baseInfoObject.get("brandNo").getAsInt();
                String imageUrl = baseInfoObject.getAsJsonArray("listImageUrls").get(0).getAsString();
                imageUrl = "https:" + imageUrl;
                int salePrice = priceInfoObject.get("salePrice").getAsInt();
                int immediateDiscountAmt = priceInfoObject.get("immediateDiscountAmt").getAsInt();

                ProductResponseDto product =
                        new ProductResponseDto(null, productNo, stockCnt, productName,
                                brandName, brandNo, imageUrl, "",
                                salePrice, immediateDiscountAmt);
                productList.add(product);
            }

            return productList;

        } catch (UnirestException | ParseException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private String processBrandName(String brandName) {
        int slashIndex = brandName.indexOf('/');
        return slashIndex != -1 ? brandName.substring(0, slashIndex).trim() : brandName.trim();
    }


    private HttpResponse<String> shopbyGetProductListRequest(int[] productNos) throws UnirestException {

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

}
