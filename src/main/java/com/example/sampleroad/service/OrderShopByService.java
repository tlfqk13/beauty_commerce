package com.example.sampleroad.service;

import com.example.sampleroad.common.utils.CustomValue;
import com.example.sampleroad.common.utils.ShopBy;
import com.example.sampleroad.domain.claim.ClaimStatus;
import com.example.sampleroad.domain.order.OrderStatus;
import com.example.sampleroad.domain.order.OrderType;
import com.example.sampleroad.dto.request.order.OrderRequestDto;
import com.example.sampleroad.dto.response.order.OrderCancelResponseDto;
import com.example.sampleroad.dto.response.order.OrderDetailResponseDto;
import com.example.sampleroad.dto.response.order.OrderResponseDto;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class OrderShopByService {

    @Value("${shop-by.client-id}")
    String clientId;

    @Value("${shop-by.accept-header}")
    String acceptHeader;

    @Value("${shop-by.version-header}")
    String versionHeader;

    @Value("${shop-by.platform-header}")
    String platformHeader;

    Gson gson = new Gson();

    @Value("${shop-by.url}")
    String shopByUrl;

    public OrderDetailResponseDto.NewOrderDetail shopbyGetOrderDetail(String shopByAccessToken, String orderNo) throws
            UnirestException, ParseException {
        HttpResponse<String> response = Unirest.get(shopByUrl + "/profile/orders/" + orderNo)
                .header("accept", acceptHeader)
                .header("version", versionHeader)
                .header("clientid", clientId)
                .header("accesstoken", shopByAccessToken)
                .header("platform", platformHeader)
                .header("content-type", acceptHeader)
                .asString();

        log.info("주문 상세 조회_____________________________");
        ShopBy.errorMessage(response);
        log.info("주문 상세 조회_____________________________");

        List<OrderDetailResponseDto.NewInPayInfoProduct> inPayInfoProducts = new ArrayList<>();

        JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
        JsonObject shippingAddress = jsonObject.getAsJsonObject("shippingAddress");
        JsonObject firstOrderAmount = jsonObject.getAsJsonObject("firstOrderAmount");
        String orderDate = jsonObject.get("orderYmdt").getAsString();
        String deliveryMemo = jsonObject.get("deliveryMemo").isJsonNull() ? "" : jsonObject.get("deliveryMemo").getAsString();
        String receiverZipCd = shippingAddress.get("receiverZipCd").getAsString();
        String receiverAddress = shippingAddress.get("receiverAddress").getAsString();
        String receiverJibunAddress = shippingAddress.get("receiverJibunAddress").getAsString();
        String receiverDetailAddress = shippingAddress.get("receiverDetailAddress").getAsString();
        String receiverName = shippingAddress.get("receiverName").getAsString();
        String receiverContact1 = shippingAddress.get("receiverContact1").getAsString();
        String addressName = shippingAddress.get("addressName").isJsonNull() ? "" : shippingAddress.get("addressName").getAsString();
        int totalProductAmt = firstOrderAmount.get("totalProductAmt").getAsInt(); // 상품금액 - 즉시할인금액
        int deliveryAmt = firstOrderAmount.get("deliveryAmt").getAsInt(); // 배송비
        int standardAmt = firstOrderAmount.get("standardAmt").getAsInt(); // 기본 상품금액
        int immediateDiscountAmt = firstOrderAmount.get("immediateDiscountAmt").getAsInt(); // 즉시할인금액
        int cartCouponDiscountAmount = firstOrderAmount.get("cartCouponDiscountAmt").getAsInt();
        int productCouponDiscountAmount = firstOrderAmount.get("productCouponDiscountAmt").getAsInt();
        int couponDiscountAmount = cartCouponDiscountAmount + productCouponDiscountAmount;
        int pointDiscountAmount = firstOrderAmount.get("subPayAmt").getAsInt();
        String payTypeLabel = jsonObject.get("payTypeLabel").getAsString();

        OrderResponseDto.ShippingAddress shippingAddressDto =
                new OrderResponseDto.ShippingAddress(receiverZipCd, receiverAddress, receiverJibunAddress, receiverDetailAddress
                        , receiverName, receiverContact1, addressName, deliveryMemo);

        OrderResponseDto.PayInfo payInfo = new OrderResponseDto.PayInfo(totalProductAmt, deliveryAmt, standardAmt, immediateDiscountAmt,
                couponDiscountAmount, pointDiscountAmount, payTypeLabel);

        JsonElement partnerElement = jsonObject.getAsJsonArray("orderOptionsGroupByPartner").get(0);
        JsonArray deliveryArray = partnerElement.getAsJsonObject().getAsJsonArray("orderOptionsGroupByDelivery");
        String retrieveInvoiceUrl = null;
        for (JsonElement deliveryArrayElement : deliveryArray) {
            JsonElement deliveryElement = deliveryArrayElement.getAsJsonObject();
            retrieveInvoiceUrl = deliveryElement.getAsJsonObject().get("retrieveInvoiceUrl").isJsonNull() ? "" :
                    deliveryElement.getAsJsonObject().get("retrieveInvoiceUrl").getAsString(); // 송장 추적
            JsonArray orderOptionsArray = deliveryElement.getAsJsonObject().getAsJsonArray("orderOptions");

            for (JsonElement orderOptionElement : orderOptionsArray) {
                JsonObject orderOptionObject = orderOptionElement.getAsJsonObject();
                int productNo = orderOptionObject.get("productNo").getAsInt();
                int orderOptionNo = orderOptionObject.get("orderOptionNo").getAsInt();
                int productOptionNo = orderOptionObject.get("optionNo").getAsInt();
                String productName = orderOptionObject.get("productName").getAsString();
                String brandName = orderOptionObject.get("brandName").getAsString();
                if (brandName.equals("")) {
                    brandName = orderOptionObject.get("brandNameEn").getAsString();
                }
                String productImgUrl = orderOptionObject.get("imageUrl").getAsString();
                productImgUrl = "https:" + productImgUrl;
                int orderCnt = orderOptionObject.get("orderCnt").getAsInt();
                Integer claimNo = orderOptionObject.get("claimNo").isJsonNull() ? 0 :
                        orderOptionObject.get("claimNo").getAsInt();

                OrderStatus orderStatusType = OrderStatus.valueOf(orderOptionObject.get("orderStatusType").getAsString());
                String claimStatus = orderOptionObject.get("claimStatusType").isJsonNull() ? null : orderOptionObject.get("claimStatusType").getAsString();
                ClaimStatus claimStatusType = null;
                if (claimStatus != null) {
                    claimStatusType = ClaimStatus.valueOf(claimStatus);
                }

                JsonObject priceObject = orderOptionObject.getAsJsonObject("price");
                int productStandardPrice = priceObject.get("standardAmt").getAsInt(); // 상품 정가
                int productImmediateDiscountedAmt = priceObject.get("immediateDiscountedAmt").getAsInt(); // 상품 총 할인금액
                int productImmediateDiscountedPrice = productStandardPrice - productImmediateDiscountedAmt;

                OrderDetailResponseDto.NewInPayInfoProduct product =
                        new OrderDetailResponseDto.NewInPayInfoProduct(productName, productImgUrl, productNo, productOptionNo,
                                orderOptionNo, productStandardPrice, productImmediateDiscountedPrice
                                , orderCnt, claimNo, orderStatusType, claimStatusType, retrieveInvoiceUrl, brandName);

                inPayInfoProducts.add(product);

            }
        }

        OrderStatus orderStatusType = getOrderStatus(inPayInfoProducts);

        OrderResponseDto.OrderInfo orderInfo = new OrderResponseDto.OrderInfo(OrderType.GENERAL_PURCHASE,
                orderNo, orderDate, orderStatusType, retrieveInvoiceUrl);

        return new OrderDetailResponseDto.NewOrderDetail(orderInfo, shippingAddressDto, payInfo, inPayInfoProducts);

    }

    private static OrderStatus getOrderStatus(List<OrderDetailResponseDto.NewInPayInfoProduct> inPayInfoProducts) {
        List<OrderStatus> orderStatusList = inPayInfoProducts
                .stream()
                .map(OrderDetailResponseDto.NewInPayInfoProduct::getOrderStatusType)
                .collect(Collectors.toList());

        OrderStatus orderStatusType = null;

        if (orderStatusList.contains(OrderStatus.DELIVERY_ING)) {
            orderStatusType = OrderStatus.DELIVERY_ING;
            return orderStatusType;
        }

        // Loop through the orderStatusList once
        // TODO: 4/1/24 현재 마지막 orderStatus를 따라가는 거지 구조
        for (OrderStatus status : orderStatusList) {
            if (status == OrderStatus.PAY_DONE) {
                // If PAY_DONE is found, set orderStatusType to PAY_DONE and exit the loop
                orderStatusType = OrderStatus.PAY_DONE;
                break;
            } else if ((status == OrderStatus.DELIVERY_ING || status == OrderStatus.PRODUCT_PREPARE ) && orderStatusType == null) {
                // If DELIVERY_ING, PRODUCT_PREPARE, or DELIVERY_PREPARE is found first, set orderStatusType to DELIVERY_ING
                orderStatusType = OrderStatus.DELIVERY_ING;
            } else if (status == OrderStatus.BUY_CONFIRM && orderStatusType == null) {
                // If BUY_CONFIRM is found and orderStatusType is still null, set it to BUY_CONFIRM
                orderStatusType = OrderStatus.BUY_CONFIRM;
            }
        }

        if (orderStatusType == null) {
            orderStatusType = (!inPayInfoProducts.isEmpty() && inPayInfoProducts.get(0).getOrderStatusType() != null)
                    ? inPayInfoProducts.get(0).getOrderStatusType()
                    : OrderStatus.PAY_DONE; // Default to PAY_DONE if productPayInfos is empty or the first item's status is null
        }
        return orderStatusType;
    }

    public OrderResponseDto.NewOrderListShopby shopbyGetOrderList(String shopByAccessToken, int pageNumber, int pageSize, OrderStatus orderStatusType) throws UnirestException, ParseException {

        String orderRequestTypes = "";

        if (OrderStatus.PAY_DONE.equals(orderStatusType)) {
            orderRequestTypes = "Pay Done";
        }

        HttpResponse<String> response = Unirest.get(shopByUrl + "/profile/orders")
                .queryString("orderRequestTypes", orderRequestTypes)
                .queryString("pageNumber", pageNumber)
                .queryString("pageSize", pageSize)
                .queryString("hasTotalCount", true)
                .queryString("startYmd", CustomValue.defaultStartYmd)
                .queryString("endYmd", "")
                .header("accept", acceptHeader)
                .header("version", versionHeader)
                .header("accesstoken", shopByAccessToken)
                .header("clientid", clientId)
                .header("platform", platformHeader)
                .asString();

        log.info("배송/주문 조회_____________________________");
        ShopBy.errorMessage(response);
        log.info("배송/주문 조회_____________________________");

        JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
        JsonArray productsArray = jsonObject.getAsJsonArray("items");
        int totalCount = jsonObject.get("totalCount").getAsInt();
        List<OrderResponseDto.NewOrderListGroup> orderList = new ArrayList<>();
        int totalProductCount = 0;
        int totalPrice = 0;
        int totalImmediateDiscountAmt = 0;

        for (JsonElement itemElement : productsArray) {
            JsonObject itemObject = itemElement.getAsJsonObject();
            JsonObject firstOrderAmt = itemObject.getAsJsonObject("firstOrderAmt");

            totalPrice = firstOrderAmt.get("standardAmt").getAsInt();
            totalImmediateDiscountAmt = firstOrderAmt.get("immediateDiscountAmt").getAsInt();

            List<OrderResponseDto.InOrderListProduct> productsList = new ArrayList<>();
            String orderNo = itemObject.get("orderNo").getAsString();
            String orderDate = itemObject.get("orderYmdt").getAsString();

            JsonArray orderProducts = itemObject.getAsJsonArray("orderOptions");
            for (JsonElement orderProductElement : orderProducts) {
                JsonObject orderProductJsonObject = orderProductElement.getAsJsonObject();
                //int orderOptionNo = orderProductJsonObject.get("orderOptionNo").getAsInt();
                int productNo = orderProductJsonObject.get("productNo").getAsInt();
                String imageUrl = orderProductJsonObject.get("imageUrl").getAsString();
                imageUrl = "https:" + imageUrl;
                String brandName = orderProductJsonObject.get("brandName").getAsString();
                brandName = processBrandName(brandName);
                int orderCnt = orderProductJsonObject.get("orderCnt").getAsInt();
                String productName = orderProductJsonObject.get("productName").getAsString();
                int price = orderProductJsonObject.getAsJsonObject("price").get("salePrice").getAsInt();
                int immediateDiscountAmt = orderProductJsonObject.getAsJsonObject("price").get("immediateDiscountAmt").getAsInt();
                OrderStatus orderStatus = OrderStatus.valueOf(orderProductJsonObject.get("orderStatusType").getAsString());

                OrderResponseDto.InOrderListProduct inOrderListProduct = new OrderResponseDto.InOrderListProduct(
                        orderStatus, orderNo, orderDate,
                        productName, productNo, imageUrl,
                        brandName, orderCnt,
                        price, immediateDiscountAmt
                );
                productsList.add(inOrderListProduct);
            }

            OrderResponseDto.InOrderListProduct selectedProduct = getInOrderListProduct(productsList);

            if (selectedProduct != null) {
                totalProductCount = productsList.size();

                OrderResponseDto.InOrderListRepresentativeProduct inOrderListRepresentativeProduct =
                        new OrderResponseDto.InOrderListRepresentativeProduct(selectedProduct, totalPrice, totalImmediateDiscountAmt);

                OrderResponseDto.NewOrderListGroup orderListGroup =
                        new OrderResponseDto.NewOrderListGroup(totalProductCount, inOrderListRepresentativeProduct);
                orderList.add(orderListGroup);
            }
        }
        return new OrderResponseDto.NewOrderListShopby(totalCount, orderList);
    }

    private static OrderResponseDto.InOrderListProduct getInOrderListProduct(List<OrderResponseDto.InOrderListProduct> productsList) {
        return productsList.stream()
                // First, try to find a product with price > 0 and not CANCEL_DONE
                .filter(product -> product.getPrice() > 0 && product.getOrderStatus() != OrderStatus.CANCEL_DONE)
                .findFirst()
                // If none found, try to find a product that is not CANCEL_DONE
                .orElseGet(() -> productsList.stream()
                        .filter(product -> product.getOrderStatus() != OrderStatus.CANCEL_DONE)
                        .findFirst()
                        // As a last resort, return the first product if none match the criteria above,
                        // or null if the list is empty.
                        .orElse(productsList.isEmpty() ? null : productsList.get(0)));
    }

    private String processBrandName(String brandName) {
        int slashIndex = brandName.indexOf('/');
        return slashIndex != -1 ? brandName.substring(0, slashIndex).trim() : brandName.trim();
    }

    public OrderCancelResponseDto.CalculateCancelOrder shopbyCalculateCancelOrder(String shopByAccessToken,
                                                                                   List<OrderRequestDto.ClaimedProductOptions> claimedProductOptionsList) throws UnirestException, ParseException {

        JSONObject json = createCancelOrderJson();
        json.put("claimedProductOptions", claimedProductOptionsList);

        HttpResponse<String> response = Unirest.post(shopByUrl + "/profile/claims/estimate")
                .header("accept", acceptHeader)
                .header("version", versionHeader)
                .header("clientid", clientId)
                .header("accesstoken", shopByAccessToken)
                .header("platform", platformHeader)
                .header("content-type", acceptHeader)
                .body(gson.toJson(json))
                .asString();

        ShopBy.errorMessage(response);

        JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
        JsonObject productAmtInfoJson = jsonObject.getAsJsonObject("productAmtInfo");
        JsonObject deliveryAmtInfoJson = jsonObject.getAsJsonObject("deliveryAmtInfo");
        JsonObject subtractionAmtInfoJson = jsonObject.getAsJsonObject("subtractionAmtInfo");

        int totalProductAmt = productAmtInfoJson.get("totalAmt").getAsInt();
        int refundPayAmt = jsonObject.get("refundPayAmt").getAsInt();
        int refundSubPayAmt = jsonObject.get("refundSubPayAmt").getAsInt();
        String refundType = jsonObject.get("refundType").getAsString();
        String refundPayType = jsonObject.get("refundPayType").getAsString();
        String refundTypeLabel = jsonObject.get("refundTypeLabel").getAsString();
        int additionalPayAmt = jsonObject.get("additionalPayAmt").getAsInt();
        int refundMainPayAmt = jsonObject.get("refundMainPayAmt").getAsInt();

        int deliveryTotalAmt = deliveryAmtInfoJson.get("totalAmt").getAsInt();

        int cartCouponAmt = subtractionAmtInfoJson.get("cartCouponAmt").getAsInt();
        int productCouponDiscountAmt = productAmtInfoJson.get("productCouponDiscountAmt").getAsInt();

        return new OrderCancelResponseDto.CalculateCancelOrder(
                totalProductAmt,
                refundPayAmt, refundSubPayAmt, refundType,
                refundPayType, refundTypeLabel,
                additionalPayAmt, refundMainPayAmt,
                cartCouponAmt, productCouponDiscountAmt,
                deliveryTotalAmt
        );

    }

    private JSONObject createCancelOrderJson(OrderRequestDto.CancelOrder cancelOrderDto) {
        JSONObject json = new JSONObject();
        json.put("claimType", "CANCEL");
        json.put("claimReasonType", cancelOrderDto.getClaimReasonType());
        json.put("claimReasonDetail", cancelOrderDto.getClaimReasonDetail());
        json.put("bankAccountInfo", cancelOrderDto.getBankAccountInfo());
        json.put("saveBankAccountInfo", false);
        json.put("responsibleObjectType", null);
        json.put("productCnt", 1);
        json.put("refundsImmediately", true);
        return json;
    }

    private JSONObject createCancelOrderJson() {
        JSONObject json = new JSONObject();
        json.put("claimType", "CANCEL");
        json.put("claimReasonType", "CHANGE_MIND");
        json.put("claimReasonDetail", "");
        json.put("bankAccountInfo", "");
        json.put("saveBankAccountInfo", false);
        json.put("responsibleObjectType", "BUYER");
        json.put("refundsImmediately", true);
        return json;
    }

}
