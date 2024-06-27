package com.example.sampleroad.common.utils;

import com.example.sampleroad.common.exception.ErrorCode;
import com.example.sampleroad.common.exception.ErrorCustomException;
import com.example.sampleroad.common.exception.ErrorShopByException;
import com.mashape.unirest.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

@Slf4j
public class ShopBy {

    public static JSONObject errorMessage(HttpResponse<String> response) throws ParseException {
        String body = response.getBody();
        JSONObject jsonObject = StringConvert.StringToJson(body);
        int status = response.getStatus();
        if (status != 200) {
            String message = String.valueOf(jsonObject.get("message"));
            String code = String.valueOf(jsonObject.get("code"));
            if (message.contains("구매불가한 옵션이 포함되어 있습니다")) {
                message = "선택하신 상품의 재고가 부족합니다. 수량을 조절 해주세요.";
            } else if ("PPVE0011".equals(code)) {
                throw new ErrorCustomException(ErrorCode.SOLD_OUT_PRODUCT_REMOVED);
            } else if ("M0013".equals(code)) {
                message = "로그인을 다시해주세요. 감사합니다.";
                throw new ErrorCustomException(ErrorCode.REFRESH_TOKEN_EXPIRE);
            } /*else if ("O8002".equals(jsonObject.get("code"))) {
                // TODO: 3/5/24 팀구매 구매 제한
                throw new ErrorCustomException(ErrorCode.EXPERIENCE_PRODUCT_MAX_COUNT_ZERO);
            }*/ else if (status == 503 || code .equals("99999")) {
                throw new ErrorCustomException(ErrorCode.EMERGENCY_REPAIR);
            }
            throw new ErrorShopByException(status, code, message);
        }
        return jsonObject;
    }

    public static JSONObject errorMessage(HttpResponse<String> response, String shopByError) throws ParseException {
        String body = response.getBody();
        JSONObject jsonObject = StringConvert.StringToJson(body);
        if (response.getStatus() != 200) {
            String message = String.valueOf(jsonObject.get("message"));
            if (shopByError.equals("STOCK_ISSUE")) {
                if (message.contains("구매불가한 옵션이 포함되어 있습니다")) {
                    message = "선택하신 상품의 재고가 부족합니다. 수량을 조절 해주세요.";
                }
            } else if (shopByError.equals("ADD_TO_CART")) {
                if (message.contains("구매불가한 옵션이 포함되어 있습니다")) {
                    message = "품절된 상품입니다. 해당 상품을 제외하고 주문 해주세요.";
                }
            }
            throw new ErrorShopByException(response.getStatus(), "", message);
        }
        return jsonObject;
    }

    public static JSONObject errorMessage(HttpResponse<String> response, boolean isProductDetail) throws ParseException {
        String body = response.getBody();
        JSONObject jsonObject = StringConvert.StringToJson(body);
        if (response.getStatus() == 404 && isProductDetail) {
            String message = "판매가 종료된 상품입니다";
            throw new ErrorShopByException(response.getStatus(), "", message);
        }
        return jsonObject;
    }
}
