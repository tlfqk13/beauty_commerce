package com.example.sampleroad.service;

import com.example.sampleroad.common.utils.CustomValue;
import com.example.sampleroad.common.utils.ShopBy;
import com.example.sampleroad.dto.request.ReviewRequestDto;
import com.example.sampleroad.dto.response.review.ReviewResponseDto;
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
public class ReviewShopByService {

    @Value("${shop-by.client-id}")
    String clientId;

    @Value("${shop-by.version-header}")
    String versionHeader;

    @Value("${shop-by.platform-header}")
    String platformHeader;

    @Value("${shop-by.url}")
    String shopByUrl;

    Gson gson = new Gson();

    /**
     * 샵바이에서 작성 가능한 리뷰를 조회
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 4/12/24
     **/
    public ReviewResponseDto.NewReviewableAndTotalCount shopByGetNewReviewableProduct(String shopByAccessToken, int pageNumber, int pageSize) throws ParseException, UnirestException {
        HttpResponse<String> response = Unirest.get(shopByUrl + "/profile/order-options/product-reviewable")
                .queryString("pageNumber", pageNumber)
                .queryString("pageSize", pageSize)
                .queryString("hasTotalCount", true)
                .queryString("startDate", CustomValue.defaultStartYmd)
                .queryString("endDate", "")
                .header("version", versionHeader)
                .header("accesstoken", shopByAccessToken)
                .header("clientid", clientId)
                .header("platform", platformHeader)
                .asString();

        log.info("작성 가능한 리뷰조회____________________________");
        ShopBy.errorMessage(response);
        log.info("작성 가능한 리뷰조회____________________________");

        List<ReviewResponseDto.NewReviewable> reviewableList = new ArrayList<>();
        JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
        JsonArray productsArray = jsonObject.getAsJsonArray("items");
        int totalCount = jsonObject.get("totalCount").getAsInt();

        for (JsonElement itemElement : productsArray) {
            JsonObject itemObject = itemElement.getAsJsonObject();
            String productName = itemObject.get("productName").getAsString();
            String brandName = itemObject.get("brandName").getAsString();
            brandName = processBrandName(brandName);
            int orderOptionNo = itemObject.get("orderOptionNo").getAsInt();
            int optionNo = itemObject.get("optionNo").getAsInt();
            String optionTitle = itemObject.get("optionTitle").getAsString();
            String orderNo = itemObject.get("orderNo").getAsString();
            int productNo = itemObject.get("productNo").getAsInt();
            String imageUrl = itemObject.get("imageUrl").getAsString();
            imageUrl = "https:" + imageUrl;

            JsonObject orderDateObject = itemObject.getAsJsonObject("orderStatusDate");
            String orderDate = orderDateObject.get("registerYmdt").getAsString();

            ReviewResponseDto.NewReviewable reviewable =
                    new ReviewResponseDto.NewReviewable(
                            productName, brandName, orderOptionNo, optionNo
                            , optionTitle, orderNo, productNo, imageUrl, orderDate);

            reviewableList.add(reviewable);
        }
        return new ReviewResponseDto.NewReviewableAndTotalCount(totalCount, reviewableList);
    }

    /**
     * 작성한 리뷰 조회
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 4/11/24
     **/
    public ReviewResponseDto.ReviewByMemberAndTotalCount shopbyGetWrittenReview(String shopByAccessToken, int pageNumber, int pageSize, String startYmd) throws UnirestException, ParseException {
        HttpResponse<String> response = Unirest.get(shopByUrl + "/profile/product-reviews")
                .queryString("pageNumber", pageNumber)
                .queryString("pageSize", pageSize)
                .queryString("hasTotalCount", true)
                .queryString("startYmd", startYmd)
                .queryString("endYmd", "")
                .header("version", versionHeader)
                .header("accesstoken", shopByAccessToken)
                .header("clientid", clientId)
                .header("platform", platformHeader)
                .asString();

        log.info("내가 쓴 리뷰조회_____________________________");
        ShopBy.errorMessage(response);
        log.info("내가 쓴 리뷰조회_____________________________");

        List<ReviewResponseDto.ReviewByMember> reviewByMemberList = new ArrayList<>();
        JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
        JsonArray productsArray = jsonObject.getAsJsonArray("items");
        int totalCount = jsonObject.get("totalCount").getAsInt();

        for (JsonElement itemElement : productsArray) {
            JsonObject itemObject = itemElement.getAsJsonObject();
            int reviewNo = itemObject.get("reviewNo").getAsInt();
            int productNo = itemObject.get("productNo").getAsInt();
            String productName = itemObject.get("productName").getAsString();
            String imageUrl = itemObject.get("imageUrl").getAsString();
            imageUrl = "https:" + imageUrl;
            String brandName = itemObject.get("brandName").getAsString();
            brandName = processBrandName(brandName);
            String optionTitle = itemObject.getAsJsonObject("orderedOption").get("optionTitle").getAsString();
            int orderOptionNo = itemObject.getAsJsonObject("orderedOption").get("orderOptionNo").getAsInt();
            Double rate = itemObject.get("rate").getAsDouble();
            String registerDate = itemObject.get("registerYmdt").getAsString();
            JsonArray fileUrlsArray = itemObject.getAsJsonArray("fileUrls");
            String[] reviewImageUrls = getReviewImageUrls(fileUrlsArray);
            String content = itemObject.get("content").getAsString();
            int recommendCnt = itemObject.get("recommendCnt").getAsInt();

            ReviewResponseDto.ReviewByMember reviewByMember =
                    new ReviewResponseDto.ReviewByMember(reviewNo, productNo, productName, imageUrl, brandName
                            , optionTitle, orderOptionNo, rate, registerDate, null
                            , reviewImageUrls, content, recommendCnt);

            reviewByMemberList.add(reviewByMember);
        }
        return new ReviewResponseDto.ReviewByMemberAndTotalCount(totalCount, reviewByMemberList);
    }

    public int shopByAddReview(ReviewRequestDto dto, String shopByAccessToken) throws UnirestException, ParseException {
        HttpResponse<String> response = Unirest.post(shopByUrl + "/products" + "/" + dto.getProductNo() + "/product-reviews")
                .header("accept", "application/json")
                .header("version", "1.0")
                .header("accesstoken", shopByAccessToken)
                .header("clientid", clientId)
                .header("platform", "PC")
                .header("content-type", "application/json")
                .body(gson.toJson(dto))
                .asString();
        log.info("샵바이 리뷰추가_____________________________");
        ShopBy.errorMessage(response);
        log.info("샵바이 리뷰추가_____________________________");
        JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
        return jsonObject.get("reviewNo").getAsInt();
    }

    public void shopByDeleteReview(String shopByAccessToken, int productNo, int reviewNo) throws UnirestException, ParseException {
        HttpResponse<String> response = Unirest.delete(
                        shopByUrl + "/products"
                                + "/" + productNo
                                + "/product-reviews"
                                + "/" + reviewNo)
                .header("accept", "application/json")
                .header("version", "1.0")
                .header("accesstoken", shopByAccessToken)
                .header("clientid", clientId)
                .header("platform", "PC")
                .header("content-type", "application/json")
                .asString();

        if (response.getStatus() != 204) {
            ShopBy.errorMessage(response);
        }
    }

    public void shopByReviewRecommend(String shopByAccessToken, int productNo, int reviewNo) throws UnirestException, ParseException {

        HttpResponse<String> response = Unirest.post(
                        shopByUrl + "/products"
                                + "/" + productNo
                                + "/product-reviews"
                                + "/" + reviewNo
                                + "/recommend")
                .header("accept", "application/json")
                .header("version", "1.0")
                .header("accesstoken", shopByAccessToken)
                .header("clientid", clientId)
                .header("platform", "PC")
                .header("content-type", "application/json")
                .asString();

        if (response.getStatus() != 204) {
            ShopBy.errorMessage(response);
        }
    }

    public void shopByReviewUnRecommend(String shopByAccessToken, int productNo, int reviewNo) throws ParseException, UnirestException {
        HttpResponse<String> response = Unirest.delete(
                        shopByUrl + "/products"
                                + "/" + productNo
                                + "/product-reviews"
                                + "/" + reviewNo
                                + "/recommend")
                .header("accept", "application/json")
                .header("version", "1.0")
                .header("accesstoken", shopByAccessToken)
                .header("clientid", clientId)
                .header("platform", "PC")
                .header("content-type", "application/json")
                .asString();

        if (response.getStatus() != 204) {
            ShopBy.errorMessage(response);
        }
    }

    public void shopByReviewUpdate(ReviewRequestDto.Update dto, String shopByAccessToken, int productNo, int reviewNo) throws UnirestException, ParseException {
        HttpResponse<String> response = Unirest.put(
                        shopByUrl + "/products/" + productNo + "/product-reviews/" + reviewNo)
                .header("accept", "application/json")
                .header("version", "1.0")
                .header("accesstoken", shopByAccessToken)
                .header("clientid", clientId)
                .header("platform", "PC")
                .header("content-type", "application/json")
                .body(gson.toJson(dto))
                .asString();

        if (response.getStatus() != 204) {
            ShopBy.errorMessage(response);
        }
    }

    public void shopByReviewReport(ReviewRequestDto.Report dto, String shopByAccessToken, int productNo) throws UnirestException, ParseException {

        JSONObject json = new JSONObject();
        json.put("reviewNo", dto.getReviewNo());
        json.put("reportReasonCd", "COPYRIGHT"); // 신고사유
        json.put("content", dto.getContent());

        HttpResponse<String> response = Unirest.post(
                        shopByUrl + "/products"
                                + "/" + productNo
                                + "/product-reviews/"
                                + dto.getReviewNo()
                                + "/report")
                .header("accept", "application/json")
                .header("version", "1.0")
                .header("accesstoken", shopByAccessToken)
                .header("clientid", clientId)
                .header("platform", "PC")
                .header("content-type", "application/json")
                .body(gson.toJson(json))
                .asString();

        if (response.getStatus() != 204) {
            ShopBy.errorMessage(response);
        }
    }

    private String processBrandName(String brandName) {
        int slashIndex = brandName.indexOf('/');
        return slashIndex != -1 ? brandName.substring(0, slashIndex).trim() : brandName.trim();
    }

    private static String[] getReviewImageUrls(JsonArray fileUrlsArray) {
        String[] reviewImageUrls = new String[fileUrlsArray.size()];
        for (int i = 0; i < fileUrlsArray.size(); i++) {
            String imgUrl = fileUrlsArray.get(i).getAsString();
            reviewImageUrls[i] = imgUrl;
        }
        return reviewImageUrls;
    }
}
