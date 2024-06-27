package com.example.sampleroad.service;

import com.example.sampleroad.common.exception.ErrorCode;
import com.example.sampleroad.common.exception.ErrorCustomException;
import com.example.sampleroad.common.utils.CustomValue;
import com.example.sampleroad.common.utils.ShopBy;
import com.example.sampleroad.dto.request.order.OrderCalculateCouponRequestDto;
import com.example.sampleroad.dto.response.coupon.CouponResponseDto;
import com.example.sampleroad.dto.response.order.OrderCalculateCouponResponseDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
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

import java.text.NumberFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CouponService {
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

    private final BannerService bannerService;

    /**
     * CouponService의 설명을 여기에 작성한다.
     * 쿠폰함 > 쿠폰받기 - 발급가능한 쿠폰 모두 조회
     *
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/10/13
     **/
    public CouponResponseDto.DownloadAbleCoupon getAllCoupons(UserDetailsImpl userDetails) throws UnirestException, ParseException {
        // TODO: 2023/10/12 발급가능한 = 쿠폰함> 쿠폰받기
        String shopByAccessToken = userDetails.getMember().getShopByAccessToken();
        List<CouponResponseDto.DownloadAbleCouponInfo> downloadAbleCouponInfos = shopbyGetAllCounpons(shopByAccessToken);

        List<CouponResponseDto.DownloadAbleCouponInfo> sortedCouponInfos = downloadAbleCouponInfos.stream()
                .sorted(Comparator.comparing(CouponResponseDto.DownloadAbleCouponInfo::getCouponNo).reversed())
                .collect(Collectors.toList());

        return new CouponResponseDto.DownloadAbleCoupon(downloadAbleCouponInfos.size(), sortedCouponInfos);
    }

    public CouponResponseDto.DownloadAbleCoupon getAllBrandCoupons(UserDetailsImpl userDetail, String brandName) throws UnirestException, ParseException {
        // TODO: 2023/10/12 발급가능한 = 쿠폰함> 쿠폰받기
        String shopByAccessToken = userDetail.getMember().getShopByAccessToken();
        List<CouponResponseDto.DownloadAbleCouponInfo> downloadAbleCouponInfos = shopbyGetAllBrandCounpons(shopByAccessToken, brandName);

        List<CouponResponseDto.DownloadAbleCouponInfo> sortedCouponInfos = downloadAbleCouponInfos.stream()
                .sorted(Comparator.comparing(CouponResponseDto.DownloadAbleCouponInfo::getCouponNo).reversed())
                .collect(Collectors.toList());

        return new CouponResponseDto.DownloadAbleCoupon(downloadAbleCouponInfos.size(), sortedCouponInfos);
    }

    /**
     * CouponService의 설명을 여기에 작성한다.
     * 쿠폰함 > 내쿠폰 - 내가 다운받은 모든 쿠폰 조회 - usable : true
     * 쿠폰함 > 내쿠폰 - 사용완료,기간만료 쿠폰 조회 - usable : false
     *
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/10/13
     **/
    public CouponResponseDto.Coupon getAllMyCoupons(UserDetailsImpl userDetails, String usable) throws UnirestException, ParseException {
        String shopByAccessToken = userDetails.getMember().getShopByAccessToken();
        return shopbyGetAllMyCoupons(shopByAccessToken, usable);
    }

    /**
     * 프로모션 코드 입력으로 쿠폰 다운받기
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/10/13
     **/
    public CouponResponseDto.RegisterCouponInfo issueCouponUsingPromotionCode(UserDetailsImpl userDetails, String promotionCode) throws UnirestException, ParseException {
        String shopByAccessToken = userDetails.getMember().getShopByAccessToken();
        return shopbyIssueCouponUsingPromotionCode(shopByAccessToken, promotionCode);
    }

    /**
     * 쿠폰 다운받기
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/10/13
     **/
    @Transactional(noRollbackFor = ErrorCustomException.class)
    public CouponResponseDto.RegisterCouponInfo downloadCoupon(UserDetailsImpl userDetails, String downloadCouponNo) throws UnirestException, ParseException {
        String shopByAccessToken = userDetails.getMember().getShopByAccessToken();

        // TODO: 2/1/24 응모하기 -> 쿠폰으로 처리중
        if ("10000".equals(downloadCouponNo)) {
            bannerService.processBannerDetailAndLotto(Integer.parseInt(downloadCouponNo), userDetails);
            throw new ErrorCustomException(ErrorCode.LOTTO_APPLY_FINISH);
        } else if ("10001".equals(downloadCouponNo)) {
            bannerService.processBannerDetailAndLotto(Integer.parseInt(downloadCouponNo), userDetails);
            throw new ErrorCustomException(ErrorCode.LOTTO_APPLY_FINISH);
        }

        return shopbyDownloadCoupon(shopByAccessToken, downloadCouponNo);
    }

    /**
     * 해당상품의 가능한 쿠폰 조회
     *
     * @param
     * @param userDetails
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/10/13
     **/
    public CouponResponseDto.Coupon getProductDownloadableCoupon(UserDetailsImpl userDetails, int productNo) throws UnirestException, ParseException {
        List<CouponResponseDto.ProductCouponInfo> productCouponInfoList = shopbyGetProductCoupon(productNo, userDetails.getMember().getShopByAccessToken());
        return new CouponResponseDto.Coupon(productCouponInfoList.size(), productCouponInfoList);
    }

    /**
     * 해당 상품에서 다운로드받을 수 있는 모든 쿠폰을 발급합니다.
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/10/16
     **/
    public CouponResponseDto.ProductAllCoupon downloadProductCouponAll(UserDetailsImpl userDetails, int productNo) throws UnirestException, ParseException {
        String shopByAccessToken = userDetails.getMember().getShopByAccessToken();
        return shopbyDownloadProductCouponAll(shopByAccessToken, productNo);
    }

    /**
     * 해당 주문에 적용할 수 있는 쿠폰을 조회하는 API 입니다.
     * 해당 주문에 적용 가능한 상품쿠폰은 제일 상단에 위치.
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/10/19
     **/
    public OrderCalculateCouponResponseDto getCouponByOrderSheetNo(UserDetailsImpl userDetails, String orderSheetNo) throws ParseException, UnirestException {
        String shopByAccessToken = userDetails.getMember().getShopByAccessToken();

        OrderCalculateCouponResponseDto.UsableCouponInfo maximumCouponInfo = shopbyGetCouponMaximum(shopByAccessToken, orderSheetNo);
        OrderCalculateCouponResponseDto orderCalculateCouponResponseDto = shopbyGetCouponByOrderSheet(shopByAccessToken, orderSheetNo);

        // 중복을 제거한 Set을 생성
        Set<OrderCalculateCouponResponseDto.UsableCouponInfo> uniqueProductCoupons = new LinkedHashSet<>(orderCalculateCouponResponseDto.getProductCouponList());

        // maximumCouponInfo와 동일한 것이 있으면 Set에서 제거하고 리스트의 맨 앞에 추가
        if (uniqueProductCoupons.contains(maximumCouponInfo)) {
            uniqueProductCoupons.remove(maximumCouponInfo);
            uniqueProductCoupons.add(maximumCouponInfo);
        }

        return new OrderCalculateCouponResponseDto(orderCalculateCouponResponseDto.getTotalCount(), orderCalculateCouponResponseDto.getCartCouponList(), new ArrayList<>(uniqueProductCoupons));
    }

    /**
     * 쿠폰 적용 금액 계산하기 API
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/10/24
     **/
    public OrderCalculateCouponResponseDto.CalculateCouponResult calculatePaymentPriceByCoupon(UserDetailsImpl userDetails, OrderCalculateCouponRequestDto dto, String orderSheetNo) throws UnirestException, ParseException {
        String shopByAccessToken = userDetails.getMember().getShopByAccessToken();

        return shopbyGetCalculatePaymentPriceByCoupon(shopByAccessToken, dto, orderSheetNo);
    }

    /**
     * 쿠폰 번호로 적용 가능한 타겟 조회 ( 상품, 카테고리...등)
     *
     * @param
     * @param couponNo,pageNumber,pageSize
     * @param pageNumber
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/11/07
     **/
    public List<CouponResponseDto.CouponTargetDto> getCouponTarget(UserDetailsImpl userDetails, int couponNo, int pageNumber, int pageSize) throws UnirestException, ParseException {
        String shopByAccessToken = userDetails.getMember().getShopByAccessToken();
        return shopbyGetCouponTarget(shopByAccessToken, couponNo, pageNumber, pageSize);
    }

    private List<CouponResponseDto.CouponTargetDto> shopbyGetCouponTarget(String shopByAccessToken, int couponNo, int pageNumber, int pageSize) throws ParseException, UnirestException {
        HttpResponse<String> response = Unirest.get(shopByUrl + "/coupons/" + couponNo + "/targets")
                .queryString("pageNumber", pageNumber)
                .queryString("pageSize", pageSize)
                .header("accept", acceptHeader)
                .header("version", versionHeader)
                .header("clientid", clientId)
                .header("accesstoken", shopByAccessToken)
                .header("platform", platformHeader)
                .header("content-type", acceptHeader)
                .asString();

        log.info("쿠폰 번호로 적용가능한 타겟 조회_____________________________");
        if (response.getStatus() != 200) {
            ShopBy.errorMessage(response);
        }
        log.info("쿠폰 번호로 적용가능한 타겟 조회_____________________________");

        JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
        JsonArray itemsArray = jsonObject.getAsJsonArray("items");
        List<CouponResponseDto.CouponTargetDto> couponTargetDtos = new ArrayList<>();
        for (JsonElement itemElement : itemsArray) {
            JsonObject itemObject = itemElement.getAsJsonObject();
            int targetNo = itemObject.get("targetNo").getAsInt();
            String targetName = itemObject.get("targetName").getAsString();
            String targetType = itemObject.get("targetType").getAsString();
            CouponResponseDto.CouponTargetDto dto = new CouponResponseDto.CouponTargetDto(targetNo, targetName, targetType);
            couponTargetDtos.add(dto);
        }

        return couponTargetDtos;


    }

    /**
     * 장바구니 기준 최대 쿠폰 할인 금액 가져오기
     *
     * @param
     * @param orderSheetNo
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/10/27
     **/
    private OrderCalculateCouponResponseDto.UsableCouponInfo shopbyGetCouponMaximum(String shopByAccessToken, String orderSheetNo) throws UnirestException, ParseException {

        JSONObject json = new JSONObject();
        json.put("channelType", null);

        HttpResponse<String> response = Unirest.post(shopByUrl + "/order-sheets/" + orderSheetNo + "/coupons/maximum")
                .header("accept", acceptHeader)
                .header("version", versionHeader)
                .header("clientid", clientId)
                .header("accesstoken", shopByAccessToken)
                .header("platform", platformHeader)
                .header("content-type", acceptHeader)
                .body(gson.toJson(json))
                .asString();

        log.info("상품 쿠폰 최대 적용 계산 조회_____________________________");
        if (response.getStatus() != 200) {
            ShopBy.errorMessage(response);
        }
        log.info("상품 쿠폰 최대 적용 계산 조회______________________________");

        JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
        String resOrderSheetNo = jsonObject.get("orderSheetNo").getAsString();
        JsonArray productsArray = jsonObject.getAsJsonArray("products");
        OrderCalculateCouponResponseDto.UsableCouponInfo maximumCouponInfo = new OrderCalculateCouponResponseDto.UsableCouponInfo();

        for (JsonElement productElement : productsArray) {
            JsonObject productObject = productElement.getAsJsonObject();
            int productNo = productObject.get("productNo").getAsInt();
            String productName = productObject.get("productName").getAsString();
            JsonArray productCouponsArray = productObject.getAsJsonArray("productCoupons");
            for (JsonElement couponElement : productCouponsArray) {
                maximumCouponInfo = createUsableCouponInfo(couponElement.getAsJsonObject(), "PRODUCT", productNo);
            }
        }

        return maximumCouponInfo;
    }

    private OrderCalculateCouponResponseDto.CalculateCouponResult shopbyGetCalculatePaymentPriceByCoupon(String shopByAccessToken, OrderCalculateCouponRequestDto dto, String orderSheetNo) throws UnirestException, ParseException {

        HttpResponse<String> response = Unirest.post(shopByUrl + "/order-sheets/" + orderSheetNo + "/coupons/calculate")
                .header("accept", acceptHeader)
                .header("version", versionHeader)
                .header("clientid", clientId)
                .header("accesstoken", shopByAccessToken)
                .header("platform", platformHeader)
                .header("content-type", acceptHeader)
                .body(gson.toJson(dto))
                .asString();

        log.info("쿠폰 적용 계산 조회_____________________________");
        if (response.getStatus() != 200) {
            ShopBy.errorMessage(response);
        }
        log.info("쿠폰 적용 계산 조회_____________________________");

        JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
        String resOrderSheetNo = jsonObject.get("orderSheetNo").getAsString();
        int cartCouponDiscountAmt = jsonObject.get("cartCouponDiscountAmt").getAsInt();
        int productCouponDiscountAmt = jsonObject.get("productCouponDiscountAmt").getAsInt();

        return new OrderCalculateCouponResponseDto.CalculateCouponResult(
                cartCouponDiscountAmt, productCouponDiscountAmt
        );

    }

    private OrderCalculateCouponResponseDto shopbyGetCouponByOrderSheet(String shopByAccessToken, String orderSheetNo) throws ParseException, UnirestException {

        HttpResponse<String> response = Unirest.get(shopByUrl + "/order-sheets/" + orderSheetNo + "/coupons")
                .header("accept", acceptHeader)
                .header("version", versionHeader)
                .header("clientid", clientId)
                .header("accesstoken", shopByAccessToken)
                .header("platform", platformHeader)
                .header("content-type", acceptHeader)
                .asString();

        log.info("주문에 적용할 쿠폰 조회_____________________________");
        if (response.getStatus() != 200) {
            ShopBy.errorMessage(response);
        }
        log.info("주문에 적용할 쿠폰 조회_____________________________");

        JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
        String resOrderSheetNo = jsonObject.get("orderSheetNo").getAsString();
        JsonArray productsArray = jsonObject.getAsJsonArray("products");
        JsonArray cartCouponArray = jsonObject.getAsJsonArray("cartCoupons");

        List<OrderCalculateCouponResponseDto.UsableCouponInfo> usableCartCouponInfoList = new ArrayList<>();
        List<OrderCalculateCouponResponseDto.UsableCouponInfo> usableProductCouponInfoList = new ArrayList<>();

        // For product coupons
        for (JsonElement productElement : productsArray) {
            JsonObject productObject = productElement.getAsJsonObject();
            int productNo = productObject.get("productNo").getAsInt();
            JsonArray productCouponsArray = productObject.getAsJsonArray("productCoupons");

            for (JsonElement couponElement : productCouponsArray) {
                usableProductCouponInfoList.add(createUsableCouponInfo(couponElement.getAsJsonObject(), "PRODUCT", productNo));
            }
        }
        // TODO: 2023-10-25 상품 쿠폰 중복 제거
        usableProductCouponInfoList = usableProductCouponInfoList.stream()
                .distinct()
                .collect(Collectors.toList());

        // For cart coupons
        for (JsonElement cartCouponElement : cartCouponArray) {
            usableCartCouponInfoList.add(createUsableCouponInfo(cartCouponElement.getAsJsonObject(), "CART", 0));
        }

        return new OrderCalculateCouponResponseDto(usableCartCouponInfoList.size() + usableProductCouponInfoList.size(), usableCartCouponInfoList, usableProductCouponInfoList);

    }

    private OrderCalculateCouponResponseDto.UsableCouponInfo createUsableCouponInfo(JsonObject couponObject, String type, int productNo) {
        int couponIssueNo = couponObject.get("couponIssueNo").getAsInt();
        int couponNo = couponObject.get("couponNo").getAsInt();
        int discountRate = couponObject.get("discountRate").isJsonNull() ? 0 : couponObject.get("discountRate").getAsInt();
        int discountAmt = couponObject.get("couponDiscountAmt").isJsonNull() ? 0 : couponObject.get("couponDiscountAmt").getAsInt();
        String useEndYmdt = couponObject.get("useEndYmdt").getAsString();
        String couponName = couponObject.get("couponName").getAsString();
        String reason = couponObject.get("reason").getAsString();
        int minSalePrice = couponObject.get("minSalePrice").isJsonNull() ? 0 : couponObject.get("minSalePrice").getAsInt();
        int maxDiscountAmt = couponObject.get("maxDiscountAmt").isJsonNull() ? 0 : couponObject.get("maxDiscountAmt").getAsInt();

        String dday = checkEndDateDifference(useEndYmdt);

        NumberFormat numberFormat = NumberFormat.getInstance();
        String formattedMinSalePrice = numberFormat.format(minSalePrice);
        String minSalePriceStr = formattedMinSalePrice + "원 이상 구매 시 사용가능";
        String discountStr = discountAmt != 0 ? numberFormat.format(discountAmt) : numberFormat.format(maxDiscountAmt);
        String maxDiscountAmtStr = "최대 " + discountStr + "원 할인";

        String couponTitle = discountAmt != 0 ? discountStr + "원" : discountRate + "%";

        return new OrderCalculateCouponResponseDto.UsableCouponInfo(
                couponIssueNo, couponNo, couponTitle, couponName, reason,
                type, productNo, discountRate, discountAmt,
                minSalePriceStr, dday, useEndYmdt, maxDiscountAmtStr
        );
    }

    private CouponResponseDto.ProductAllCoupon shopbyDownloadProductCouponAll(String shopByAccessToken, int productNo) throws UnirestException, ParseException {

        JSONObject json = new JSONObject();
        json.put("includesCartCoupon", false);
        json.put("channelType", null);

        HttpResponse<String> response = Unirest.post(shopByUrl + "/coupons/products/" + productNo + "/download")
                .header("accept", acceptHeader)
                .header("version", versionHeader)
                .header("clientid", clientId)
                .header("platform", platformHeader)
                .header("content-type", acceptHeader)
                .header("accesstoken", shopByAccessToken)
                .body(gson.toJson(json))
                .asString();

        if (response.getStatus() != 200) {
            ShopBy.errorMessage(response);
        }

        JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);

        // 'issuedCoupons' 배열을 JsonArray로 추출
        JsonArray issuedCouponsArray = jsonObject.getAsJsonArray("issuedCoupons");

        // 'issueFailCoupons' 배열을 JsonArray로 추출
        JsonArray issueFailCouponsArray = jsonObject.getAsJsonArray("issueFailCoupons");

        List<CouponResponseDto.IssuedCoupons> issuedCoupons = new ArrayList<>();
        List<CouponResponseDto.IssueFailCoupons> issueFailCoupons = new ArrayList<>();

        // 'issuedCoupons' 배열을 순회하며 데이터 추출
        for (JsonElement element : issuedCouponsArray) {
            JsonObject item = element.getAsJsonObject();
            int couponIssueNo = item.get("couponIssueNo").getAsInt();
            int couponNo = item.get("couponNo").getAsInt();
            String couponName = item.get("couponName").getAsString();
            String useEndYmdt = item.get("useEndYmdt").getAsString();
            // 필요한 처리를 여기에 추가
            CouponResponseDto.IssuedCoupons issuedCoupon = new CouponResponseDto.IssuedCoupons(
                    couponIssueNo, couponNo, couponName, useEndYmdt
            );
            issuedCoupons.add(issuedCoupon);
        }


        // 'issueFailCoupons' 배열을 순회하며 데이터 추출
        for (JsonElement element : issueFailCouponsArray) {
            JsonObject item = element.getAsJsonObject();
            int couponNo = item.get("couponNo").getAsInt();
            String failMessage = item.get("failMessage").getAsString();
            // 필요한 처리를 여기에 추가

            CouponResponseDto.IssueFailCoupons issueFailCoupon = new CouponResponseDto.IssueFailCoupons(
                    couponNo, failMessage
            );

            issueFailCoupons.add(issueFailCoupon);
        }

        return new CouponResponseDto.ProductAllCoupon(issuedCoupons, issueFailCoupons);

    }

    private List<CouponResponseDto.ProductCouponInfo> shopbyGetProductCoupon(int productNo, String shopByAccessToken) throws UnirestException, ParseException {
        HttpResponse<String> response = Unirest.get(shopByUrl + "/coupons/products/" + productNo + "/issuable/coupons")
                .header("accept", acceptHeader)
                .header("version", versionHeader)
                .header("clientid", clientId)
                .header("platform", platformHeader)
                .header("content-type", acceptHeader)
                .header("accesstoken", shopByAccessToken)
                .asString();

        if (response.getStatus() != 200) {
            ShopBy.errorMessage(response);
        }

        JsonElement jsonElement = gson.fromJson(response.getBody(), JsonElement.class);
        JsonArray couponItemsArray = jsonElement.getAsJsonArray();
        List<CouponResponseDto.ProductCouponInfo> productCouponInfoList = new ArrayList<>();
        for (JsonElement element : couponItemsArray) {
            if (element.isJsonObject()) {
                JsonObject item = element.getAsJsonObject();
                // 다운불가한 쿠폰이면 바로 다음 인덱스 수행
                boolean isDownloadable = item.get("downloadable").getAsBoolean();
                if (!isDownloadable) {
                    continue;
                }

                int couponNo = item.get("couponNo").getAsInt();
                String couponName = item.get("couponName").getAsString();
                String couponType = item.get("couponType").getAsString();

                // TODO: 2023/11/06 상품번호로 적용가능한 쿠폰에서 cart 쿠폰도 내려올 수 있어서 제외
                if (CustomValue.cartCoupon.equals(couponType)) {
                    continue;
                }

                JsonObject couponStatusObject = item.getAsJsonObject("couponStatus");
                int totalIssuableCnt = couponStatusObject.get("totalIssuableCnt").getAsInt();
                int myIssuedCnt = couponStatusObject.get("myIssuedCnt").getAsInt();
                int issuableCnt = couponStatusObject.get("issuableCnt").getAsInt();
                boolean isIssued = issuableCnt == 0;

               /*
                   "totalIssuableCnt" // 전체 발급 가능 개수
                   "totalIssuedCnt" // 전체 발급 받은 개수
                   "totalIssuedCntToday" // 오늘 발급 받은 전체 개수
                   "issuableCnt": // 발급 가능 개수
                   "myIssuedCnt": // 내가 발급받은 개수
                   "myIssuedCntToday": // 내가 오늘 발급 받은 개수
               */

                JsonObject discountInfoObject = item.getAsJsonObject("discountInfo");
                JsonObject dateInfoObject = item.getAsJsonObject("dateInfo");
                JsonObject useConstraintObject = item.getAsJsonObject("useConstraint");

                int discountRate = discountInfoObject.get("discountRate").isJsonNull() ? 0 : discountInfoObject.get("discountRate").getAsInt();
                int discountAmt = discountInfoObject.get("discountAmt").isJsonNull() ? 0 : discountInfoObject.get("discountAmt").getAsInt();

                String issueStartYmdt = dateInfoObject.get("issueStartYmdt").getAsString();
                String issueEndYmdt = dateInfoObject.get("issueEndYmdt").getAsString();

                int useDays = useConstraintObject.get("useDays").getAsInt();
                int minSalePrice = useConstraintObject.get("minSalePrice").isJsonNull() ? 0 : useConstraintObject.get("minSalePrice").getAsInt(); // 최소 기준 금액
                int maxSalePrice = useConstraintObject.get("maxSalePrice").isJsonNull() ? 0 : useConstraintObject.get("maxSalePrice").getAsInt(); // 최대 기준 금액
                int maxDiscountAmt = discountInfoObject.get("maxDiscountAmt").isJsonNull() ? 0 : discountInfoObject.get("maxDiscountAmt").getAsInt(); // 할인 금액

                String dday = checkEndDateDifference(issueEndYmdt);

                NumberFormat numberFormat = NumberFormat.getInstance();
                String formattedMinSalePrice = numberFormat.format(minSalePrice);
                String minSalePriceStr = formattedMinSalePrice + "원 이상 구매 시 사용가능";
                String discountStr = discountAmt != 0 ? numberFormat.format(discountAmt) : numberFormat.format(maxDiscountAmt);
                String maxDiscountAmtStr = "최대 " + discountStr + "원 할인";

                String couponTitle = discountAmt != 0 ? discountStr + "원" : discountRate + "%";

                CouponResponseDto.ProductCouponInfo productCouponInfo = new CouponResponseDto.ProductCouponInfo(
                        couponNo, couponTitle, couponName, couponType,
                        issueStartYmdt, issueEndYmdt, discountRate,
                        discountAmt, useDays, minSalePriceStr,
                        dday, maxDiscountAmtStr, isIssued
                );

                productCouponInfoList.add(productCouponInfo);
            }
        }

        return productCouponInfoList;
    }

    /**
     * 샵바이 api의 요청명을 여기에 작성한다.
     * /coupons/{couponNo}/download
     * 샵바이 version 1.0.0
     **/
    private CouponResponseDto.RegisterCouponInfo shopbyDownloadCoupon(String shopByAccessToken, String downloadCouponNo) throws UnirestException, ParseException {
        HttpResponse<String> response = Unirest.post(shopByUrl + "/coupons/" + downloadCouponNo + "/download")
                .header("accept", acceptHeader)
                .header("version", versionHeader)
                .header("clientid", clientId)
                .header("platform", platformHeader)
                .header("content-type", acceptHeader)
                .header("accesstoken", shopByAccessToken)
                .asString();

        ShopBy.errorMessage(response);

        return getDownloadCoupon(response);

    }

    /**
     * 샵바이 api의 요청명을 여기에 작성한다.
     * /coupons/register-code/{promotionCode}
     * 샵바이 version 1.0.0
     **/
    private CouponResponseDto.RegisterCouponInfo shopbyIssueCouponUsingPromotionCode(String shopByAccessToken, String promotionCode) throws UnirestException, ParseException {
        HttpResponse<String> response = Unirest.post(shopByUrl + "/coupons/register-code/" + promotionCode)
                .header("accept", acceptHeader)
                .header("version", versionHeader)
                .header("clientid", clientId)
                .header("platform", platformHeader)
                .header("content-type", acceptHeader)
                .header("accesstoken", shopByAccessToken)
                .asString();

        // TODO: 2023/10/23 C0022, 쿠폰번호 잘못 입력은 최대 {0}회까지만 가능합니다.(30분후입력)

        ShopBy.errorMessage(response);

        return getDownloadCoupon(response);

    }

    private CouponResponseDto.RegisterCouponInfo getDownloadCoupon(HttpResponse<String> response) {
        JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
        int couponIssueNo = jsonObject.get("couponIssueNo").getAsInt();
        int couponNo = jsonObject.get("couponNo").getAsInt();
        String couponName = jsonObject.get("couponName").getAsString();
        String useEndYmdt = jsonObject.get("useEndYmdt").getAsString();

        return new CouponResponseDto.RegisterCouponInfo(
                couponIssueNo, couponNo, couponName, useEndYmdt
        );
    }

    /**
     * 샵바이 api의 요청명을 여기에 작성한다.
     * /coupons/issuable
     * 샵바이 version 1.0.0
     **/
    private List<CouponResponseDto.DownloadAbleCouponInfo> shopbyGetAllCounpons(String shopByAccessToken) throws UnirestException, ParseException {
        HttpResponse<String> response = Unirest.get(shopByUrl + "/coupons/issuable")
                .header("accept", acceptHeader)
                .header("version", versionHeader)
                .header("clientid", clientId)
                .header("platform", platformHeader)
                .header("content-type", acceptHeader)
                .header("accesstoken", shopByAccessToken)
                .asString();

        List<CouponResponseDto.DownloadAbleCouponInfo> downloadAbleCouponInfoList = new ArrayList<>();

        if (response.getStatus() != 200) {
            ShopBy.errorMessage(response);
        }

        JsonElement jsonElement = gson.fromJson(response.getBody(), JsonElement.class);
        JsonArray couponItemsArray = jsonElement.getAsJsonArray();

        for (JsonElement element : couponItemsArray) {
            if (element.isJsonObject()) {
                JsonObject item = element.getAsJsonObject();
                int couponNo = item.get("couponNo").getAsInt();
                String couponName = item.get("couponName").getAsString();
                String couponType = item.get("couponType").getAsString();
                String couponTargetType = item.get("couponTargetType").getAsString();

                JsonObject discountInfoObject = item.getAsJsonObject("discountInfo");
                JsonObject dateInfoObject = item.getAsJsonObject("dateInfo");
                JsonObject useConstraintObject = item.getAsJsonObject("useConstraint");
                JsonObject couponStatusObject = item.getAsJsonObject("couponStatus");
                int issuableCnt = couponStatusObject.get("issuableCnt").isJsonNull() ? 0 : couponStatusObject.get("issuableCnt").getAsInt();

                if (issuableCnt == 0) {
                    continue;
                }

                int discountRate = discountInfoObject.get("discountRate").isJsonNull() ? 0 : discountInfoObject.get("discountRate").getAsInt();
                int discountAmt = discountInfoObject.get("discountAmt").isJsonNull() ? 0 : discountInfoObject.get("discountAmt").getAsInt();

                String issueStartYmdt = dateInfoObject.get("issueStartYmdt").getAsString();
                String issueEndYmdt = dateInfoObject.get("issueEndYmdt").getAsString();

                int useDays = useConstraintObject.get("useDays").getAsInt();
                int minSalePrice = useConstraintObject.get("minSalePrice").isJsonNull() ? 0 : useConstraintObject.get("minSalePrice").getAsInt();
                int maxSalePrice = useConstraintObject.get("maxSalePrice").isJsonNull() ? 0 : useConstraintObject.get("maxSalePrice").getAsInt();
                int maxDiscountAmt = discountInfoObject.get("maxDiscountAmt").isJsonNull() ? 0 : discountInfoObject.get("maxDiscountAmt").getAsInt();

                String dday = checkEndDateDifference(issueEndYmdt);

                NumberFormat numberFormat = NumberFormat.getInstance();
                String formattedMinSalePrice = numberFormat.format(minSalePrice);
                String minSalePriceStr = formattedMinSalePrice + "원 이상 구매 시 사용가능";
                String discountStr = discountAmt != 0 ? numberFormat.format(discountAmt) : numberFormat.format(maxDiscountAmt);
                String maxDiscountAmtStr = "최대 " + discountStr + "원 할인";

                String couponTitle = discountAmt != 0 ? discountStr + "원" : discountRate + "%";


                CouponResponseDto.DownloadAbleCouponInfo downloadAbleCouponInfo = new CouponResponseDto.DownloadAbleCouponInfo(
                        couponNo, couponName, couponType,
                        issueStartYmdt, issueEndYmdt,
                        discountRate, discountAmt, useDays,
                        minSalePriceStr, dday, maxDiscountAmtStr,
                        couponTitle, couponTargetType
                );
                downloadAbleCouponInfoList.add(downloadAbleCouponInfo);
            }
        }

        return downloadAbleCouponInfoList;

    }

    private List<CouponResponseDto.DownloadAbleCouponInfo> shopbyGetAllBrandCounpons(String shopByAccessToken, String brandName) throws UnirestException, ParseException {
        HttpResponse<String> response = Unirest.get(shopByUrl + "/coupons/issuable")
                .header("accept", acceptHeader)
                .header("version", versionHeader)
                .header("clientid", clientId)
                .header("platform", platformHeader)
                .header("content-type", acceptHeader)
                .header("accesstoken", shopByAccessToken)
                .asString();

        List<CouponResponseDto.DownloadAbleCouponInfo> downloadAbleCouponInfoList = new ArrayList<>();

        if (response.getStatus() != 200) {
            ShopBy.errorMessage(response);
        }

        JsonElement jsonElement = gson.fromJson(response.getBody(), JsonElement.class);
        JsonArray couponItemsArray = jsonElement.getAsJsonArray();

        for (JsonElement element : couponItemsArray) {
            if (element.isJsonObject()) {
                JsonObject item = element.getAsJsonObject();
                String couponTargetType = item.get("couponTargetType").getAsString();

                if (!couponTargetType.equals("BRAND")) {
                    continue;
                }

                int couponNo = item.get("couponNo").getAsInt();
                String couponName = item.get("couponName").getAsString();
                String couponType = item.get("couponType").getAsString();

                if (!couponName.contains(brandName)) {
                    continue;
                }

                JsonObject discountInfoObject = item.getAsJsonObject("discountInfo");
                JsonObject dateInfoObject = item.getAsJsonObject("dateInfo");
                JsonObject useConstraintObject = item.getAsJsonObject("useConstraint");
                JsonObject couponStatusObject = item.getAsJsonObject("couponStatus");
                int issuableCnt = couponStatusObject.get("issuableCnt").isJsonNull() ? 0 : couponStatusObject.get("issuableCnt").getAsInt();

                if (issuableCnt == 0) {
                    continue;
                }

                int discountRate = discountInfoObject.get("discountRate").isJsonNull() ? 0 : discountInfoObject.get("discountRate").getAsInt();
                int discountAmt = discountInfoObject.get("discountAmt").isJsonNull() ? 0 : discountInfoObject.get("discountAmt").getAsInt();

                String issueStartYmdt = dateInfoObject.get("issueStartYmdt").getAsString();
                String issueEndYmdt = dateInfoObject.get("issueEndYmdt").getAsString();

                int useDays = useConstraintObject.get("useDays").getAsInt();
                int minSalePrice = useConstraintObject.get("minSalePrice").isJsonNull() ? 0 : useConstraintObject.get("minSalePrice").getAsInt();
                int maxSalePrice = useConstraintObject.get("maxSalePrice").isJsonNull() ? 0 : useConstraintObject.get("maxSalePrice").getAsInt();
                int maxDiscountAmt = discountInfoObject.get("maxDiscountAmt").isJsonNull() ? 0 : discountInfoObject.get("maxDiscountAmt").getAsInt();

                String dday = checkEndDateDifference(issueEndYmdt);

                NumberFormat numberFormat = NumberFormat.getInstance();
                String formattedMinSalePrice = numberFormat.format(minSalePrice);
                String minSalePriceStr = formattedMinSalePrice + "원 이상 구매 시 사용가능";
                String discountStr = discountAmt != 0 ? numberFormat.format(discountAmt) : numberFormat.format(maxDiscountAmt);
                String maxDiscountAmtStr = "최대 " + discountStr + "원 할인";

                String couponTitle = discountAmt != 0 ? discountStr + "원" : discountRate + "%";

                boolean isDownloadable = item.get("downloadable").getAsBoolean();

                CouponResponseDto.DownloadAbleCouponInfo downloadAbleCouponInfo = new CouponResponseDto.DownloadAbleCouponInfo(
                        couponNo, couponName, couponType,
                        issueStartYmdt, issueEndYmdt,
                        discountRate, discountAmt, useDays,
                        minSalePriceStr, dday, maxDiscountAmtStr,
                        couponTitle, couponTargetType,isDownloadable
                );
                downloadAbleCouponInfoList.add(downloadAbleCouponInfo);
            }
        }

        return downloadAbleCouponInfoList;
    }

    public String checkEndDateDifference(String issueEndYmdt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime endDateTime = LocalDateTime.parse(issueEndYmdt, formatter);
        LocalDateTime currentDateTime = LocalDateTime.now();

        Duration duration = Duration.between(currentDateTime, endDateTime);
        long diffInDays = duration.toDays();

        if (diffInDays < 1) {
            return "1일 미만 ";
        } else {
            return "D-" + diffInDays;
        }
    }

    /**
     * 샵바이 api의 요청명을 여기에 작성한다.
     * /coupons
     * 샵바이 version 1.0.0
     **/
    private CouponResponseDto.Coupon shopbyGetAllMyCoupons(String shopByAccessToken, String usable) throws UnirestException, ParseException {
        String startYmd = null;
        if ("false".equals(usable)) {
            LocalDateTime date = LocalDateTime.now().minusMonths(1);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            startYmd = date.format(formatter);
        }
        HttpResponse<String> response = Unirest.get(shopByUrl + "/coupons")
                .queryString("usable", usable)
                .queryString("desc", true)
                .queryString("startYmd", startYmd)
                .header("accept", acceptHeader)
                .header("version", versionHeader)
                .header("clientid", clientId)
                .header("platform", platformHeader)
                .header("content-type", acceptHeader)
                .header("accesstoken", shopByAccessToken)
                .asString();

        ShopBy.errorMessage(response);
        JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
        JsonArray couponItemsArray = jsonObject.getAsJsonObject().getAsJsonArray("items");

        List<CouponResponseDto.DownloadedCouponInfo> downloadedCouponInfoList = new ArrayList<>();

        int totalCount = jsonObject.get("totalCount").getAsInt();

        for (JsonElement element : couponItemsArray) {
            if (element.isJsonObject()) {
                JsonObject item = element.getAsJsonObject();

                int couponIssueNo = item.get("couponIssueNo").getAsInt();
                String couponName = item.get("couponName").getAsString();
                String reason = item.get("reason").isJsonNull() ? null : item.get("reason").getAsString();
                int couponNo = item.get("couponNo").getAsInt();
                String couponType = item.get("couponType").getAsString();
                int discountAmt = item.get("discountAmt").getAsInt();
                int discountRate = item.get("discountRate").getAsInt();
                int minSalePrice = item.get("minSalePrice").isJsonNull() ? 0 : item.get("minSalePrice").getAsInt(); // 최소 기준 금액
                int maxSalePrice = item.get("maxSalePrice").isJsonNull() ? 0 : item.get("maxSalePrice").getAsInt(); // 최대 기준 금액
                int maxDiscountAmt = item.get("maxDiscountAmt").isJsonNull() ? 0 : item.get("maxDiscountAmt").getAsInt(); // 할인 금액
                String useEndYmdt = item.get("useEndYmdt").getAsString();
                String useYmdt = item.get("useYmdt").isJsonNull() ? null : item.get("useYmdt").getAsString();
                String couponTargetType = item.get("couponTargetType").getAsString();
                boolean isUsed = item.get("used").getAsBoolean();
                String dday = "";
                if ("false".equals(usable) && isUsed) {
                    dday = "사용됨";
                } else if ("false".equals(usable)) {
                    dday = "만료됨";
                } else {
                    dday = checkEndDateDifference(useEndYmdt);
                }

                NumberFormat numberFormat = NumberFormat.getInstance();
                String formattedMinSalePrice = numberFormat.format(minSalePrice);
                String minSalePriceStr = formattedMinSalePrice + "원 이상 구매 시 사용가능";
                String discountStr = discountAmt != 0 ? numberFormat.format(discountAmt) : numberFormat.format(maxDiscountAmt);
                String maxDiscountAmtStr = "최대 " + discountStr + "원 할인";

                String couponTitle = discountAmt != 0 ? discountStr + "원" : discountRate + "%";

                CouponResponseDto.DownloadedCouponInfo downloadedCouponInfo = new CouponResponseDto.DownloadedCouponInfo(
                        couponIssueNo, couponName, couponNo,
                        couponType, discountAmt, discountRate,
                        useEndYmdt, couponTargetType,
                        minSalePrice, maxSalePrice, maxDiscountAmt,
                        useYmdt, reason, dday,
                        minSalePriceStr, maxDiscountAmtStr,
                        couponTitle
                );

                downloadedCouponInfoList.add(downloadedCouponInfo);
            }
        }

        List<CouponResponseDto.DownloadedCouponInfo> sortedList = downloadedCouponInfoList.stream()
                .sorted(Comparator.comparing(CouponResponseDto.DownloadedCouponInfo::getCouponIssueNo).reversed())
                .collect(Collectors.toList());

        return new CouponResponseDto.Coupon(totalCount, sortedList);
    }
}
