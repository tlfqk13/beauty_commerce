package com.example.sampleroad.service;

import com.example.sampleroad.common.exception.ErrorCode;
import com.example.sampleroad.common.exception.ErrorCustomException;
import com.example.sampleroad.common.exception.detail.PurchaseConditionProductException;
import com.example.sampleroad.common.utils.ShopBy;
import com.example.sampleroad.domain.product.EventProductType;
import com.example.sampleroad.dto.response.order.OrderResponseDto;
import com.example.sampleroad.dto.response.product.EventProductQueryDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.example.sampleroad.repository.product.EventProductRepository;
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

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class EventService {

    @Value("${shop-by.client-id}")
    String clientId;

    @Value("${shop-by.url}")
    String shopByUrl;

    @Value("${shop-by.products}")
    String products;

    @Value("${shop-by.accept-header}")
    String acceptHeader;

    @Value("${shop-by.version-header}")
    String versionHeader;

    @Value("${shop-by.platform-header}")
    String platformHeader;
    Gson gson = new Gson();

    private final EventProductRepository eventProductRepository;

    /**
     * 이벤트 상품 포함의 경우 가격을 조회에서 최소금액을 넘었는지 검사
     *
     * @param userDetails, productNos
     * @return 예외처리
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/10/12
     **/
    public void checkedFirstDealProductMinimumAmt(UserDetailsImpl userDetails, List<Integer> productNos) throws UnirestException, ParseException {

        int[] productNosArray = productNos.stream()
                .mapToInt(Integer::intValue)
                .toArray();

        List<OrderResponseDto.OrderProductPriceInfo> orderProductPriceInfos = this.shopbyGetProductPriceList(productNosArray, userDetails.getMember().getShopByAccessToken());

        int totalResultPrice = orderProductPriceInfos.stream()
                .mapToInt(OrderResponseDto.OrderProductPriceInfo::getResultPrice)
                .sum();

        // TODO: 2024/01/04 FIRST_DEAL , PURCHASE_CONDITION 구분해야함
        if (totalResultPrice < 10000) {
            throw new ErrorCustomException(ErrorCode.MINIMUM_AMOUNT_NOT_MET);
        }
    }

    public void checkedZeroPerfumeProductMinimumAmt(UserDetailsImpl userDetails, int[] productNosArray) throws UnirestException, ParseException {

        List<OrderResponseDto.OrderProductPriceInfo> orderProductPriceInfos = this.shopbyGetProductPriceList(productNosArray, userDetails.getMember().getShopByAccessToken());

        int totalResultPrice = orderProductPriceInfos.stream()
                .mapToInt(OrderResponseDto.OrderProductPriceInfo::getResultPrice)
                .sum();

        // TODO: 2024/01/04 FIRST_DEAL , PURCHASE_CONDITION 구분해야함
        if (totalResultPrice < 10000) {
            OrderResponseDto.OrderProductPriceInfo orderProductPriceInfo = orderProductPriceInfos.get(0);
            String productName = orderProductPriceInfo.getProductName();
            if (productName.contains(orderProductPriceInfo.getBrandName())) {
                productName = productName.replace(orderProductPriceInfo.getBrandName(), "");
            }
            throw new ErrorCustomException(ErrorCode.PURCHASE_ZERO_PERFUME_PRODUCT_CONDITION);
        }
    }

    public void checkedPurchaseConditionProductMinimumAmt(UserDetailsImpl userDetails, List<Integer> productNos) throws UnirestException, ParseException {

        int[] productNosArray = productNos.stream()
                .mapToInt(Integer::intValue)
                .toArray();

        List<OrderResponseDto.OrderProductPriceInfo> orderProductPriceInfos = this.shopbyGetProductPriceList(productNosArray, userDetails.getMember().getShopByAccessToken());

        int totalResultPrice = orderProductPriceInfos.stream()
                .mapToInt(OrderResponseDto.OrderProductPriceInfo::getResultPrice)
                .sum();

        // TODO: 2024/01/04 FIRST_DEAL , PURCHASE_CONDITION 구분해야함
        if (totalResultPrice < 10000) {
            OrderResponseDto.OrderProductPriceInfo orderProductPriceInfo = orderProductPriceInfos.get(0);
            String productName = orderProductPriceInfo.getProductName();
            if (productName.contains(orderProductPriceInfo.getBrandName())) {
                productName = productName.replace(orderProductPriceInfo.getBrandName(), "");
            }
            throw new PurchaseConditionProductException(ErrorCode.PURCHASE_CONDITION_PRODUCT, productName);
        }
    }


    /**
     * 기획전, 이벤트 상품 조회
     *
     * @param
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/11/14
     **/
    public List<EventProductQueryDto.EventProductInfo> getEventProduct(EventProductType eventProductType) {
        return eventProductRepository.findEventProductByIsVisible(eventProductType);
    }

    /**
     * 해당하는 0원 아이템 조회해서 카운트 갯수 포함안되게 하려고
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/10/23
     **/
    public List<EventProductQueryDto.EventProductInfo> getPriceFreeItems(List<Integer> customKitProductOptionNos) {
        return eventProductRepository.findPriceZeroEventProduct(customKitProductOptionNos);
    }

    private List<OrderResponseDto.OrderProductPriceInfo> shopbyGetProductPriceList(int[] productNos, String shopByAccessToken) throws UnirestException, ParseException {

        HttpResponse<String> response = getSearchByNosHttpResponse(productNos, shopByAccessToken);

        JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
        JsonArray productsArray = jsonObject.getAsJsonArray("products");
        List<OrderResponseDto.OrderProductPriceInfo> responseDtos = new ArrayList<>();
        for (JsonElement element : productsArray) {
            JsonObject productObject = element.getAsJsonObject();
            JsonObject priceInfoObject = productObject.getAsJsonObject("price");
            JsonObject baseInfoObject = productObject.getAsJsonObject("baseInfo");
            // JSON 객체에서 필요한 값을 추출
            int productNo = baseInfoObject.get("productNo").getAsInt();
            String productName = baseInfoObject.get("productName").getAsString();
            String brandName = baseInfoObject.get("brandName").getAsString();
            brandName = processBrandName(brandName);
            // JSON 객체에서 필요한 값을 추출
            int salePrice = priceInfoObject.get("salePrice").getAsInt();
            int immediateDiscountAmt = priceInfoObject.get("immediateDiscountAmt").getAsInt();
            int resultPrice = salePrice - immediateDiscountAmt;
            OrderResponseDto.OrderProductPriceInfo responseDto =
                    new OrderResponseDto.OrderProductPriceInfo(productName, brandName, salePrice, immediateDiscountAmt, resultPrice);
            responseDtos.add(responseDto);
        }

        return responseDtos;
    }

    private String processBrandName(String brandName) {
        int slashIndex = brandName.indexOf('/');
        return slashIndex != -1 ? brandName.substring(0, slashIndex).trim() : brandName.trim();
    }

    private HttpResponse<String> getSearchByNosHttpResponse(int[] productNos, String shopByAccessToken) throws UnirestException, ParseException {
        JSONObject json = new JSONObject();
        json.put("productNos", productNos);
        json.put("hasOptionValues", "true");

        HttpResponse<String> response = Unirest.post(shopByUrl + products + "/search-by-nos")
                .header("accept", acceptHeader)
                .header("version", versionHeader)
                .header("clientid", clientId)
                .header("platform", platformHeader)
                .header("accesstoken", shopByAccessToken)
                .header("content-type", acceptHeader)
                .body(gson.toJson(json))
                .asString();

        ShopBy.errorMessage(response);
        return response;
    }
}
