package com.example.sampleroad.controller;

import com.example.sampleroad.common.utils.ResultInfo;
import com.example.sampleroad.dto.request.CartRequestDto;
import com.example.sampleroad.dto.response.cart.CartResponseDto;
import com.example.sampleroad.dto.response.cart.NewCartResponseDto;
import com.example.sampleroad.dto.response.order.OrderResponseDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.example.sampleroad.service.CartService;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@Slf4j
@Api(tags = {"장바구니 관련 api Controller"})
public class CartController {

    private final CartService cartService;

    /**
     * 장바구니 등록
     * 장바구니 등록은 커스텀키트 등록 or 관리자키트 등록 으로 구분
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/06/28
     **/
    @PostMapping("/api/cart")
    @ApiOperation(value = "장바구니 등록하기 api")
    public ResultInfo addToCart(@RequestBody CartRequestDto.AddToCart dto,
                                @AuthenticationPrincipal UserDetailsImpl userDetails) throws UnirestException, ParseException {
        HashMap<String, Object> resultInfo = cartService.addToCart(dto, userDetails);
        return new ResultInfo(ResultInfo.Code.SUCCESS, "장바구니 담기완료", resultInfo);
    }

    @GetMapping("/api/cart")
    @ApiOperation(value = "장바구니 가져오기 api")
    public CartResponseDto.ResponseDto getCart(@AuthenticationPrincipal UserDetailsImpl userDetails) throws UnirestException, ParseException {
        return cartService.getCart(userDetails);
    }

    /**
     * 장바구니 리뉴얼 버전
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 3/12/24
     **/
    @PostMapping("/api/new-cart")
    @ApiOperation(value = "장바구니 가져오기 api")
    public NewCartResponseDto getCart(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                      @RequestBody CartRequestDto.ProductNosDto dto) throws UnirestException, ParseException {
        return cartService.getCart(userDetails,dto);
    }


    /**
     * 장바구니 수정 - nonCustomKit만 업데이트 대상
     * 재고 초과하여 주문 -> "재고 초과 000 메세지 출력"
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/06/16
     **/
    @PutMapping("/api/cart/{cartId}")
    @ApiOperation(value = "장바구니 수정하기 api")
    public ResultInfo updateCart(@RequestBody CartRequestDto.UpdateCart dto,
                                 @AuthenticationPrincipal UserDetailsImpl userDetails,
                                 @PathVariable Long cartId) {
        cartService.updateCart(dto, userDetails, cartId);
        return new ResultInfo(ResultInfo.Code.SUCCESS, "장바구니 수정완료");
    }

    /**
     * 주문 장바구니에서 고른 제품 삭제
     * 장바구니no 를 리스트로 받는다
     *
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/06
     **/
    @PostMapping("/api/cart-delete")
    @ApiOperation(value = "주문-장바구니에서 선택하여 삭제")
    public ResultInfo deleteCart(@RequestBody CartRequestDto.CartIdsDto cartIdsDto,
                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 장바구니에 담긴 상품하나당 장바구니No를 가진다
        HashMap<String, Object> resultMap = cartService.deleteCart(cartIdsDto, userDetails);
        return new ResultInfo(ResultInfo.Code.SUCCESS, "장바구니 삭제완료", resultMap);
    }

    /**
     * 장바구니에 담긴 제품들을 주문
     *
     * @param userDetails, cartNos
     * @return orderSheetNo
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/06/09
     **/
    @PostMapping("/api/cart/order")
    @ApiOperation(value = "장바구니에서 주문하기")
    public ResultInfo createCartOrder(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                      @RequestBody CartRequestDto.CartIdsDto cartIdsDto,
                                      @RequestParam(defaultValue = "false") boolean isExcludeZeroItem) throws UnirestException, ParseException {
        // 장바구니에 담긴 상품을 바로 주문한다
        OrderResponseDto.CreateOrderSheet cartOrder = cartService.createCartOrder(userDetails, cartIdsDto, isExcludeZeroItem);
        return new ResultInfo(ResultInfo.Code.SUCCESS, "주문서 작성 완료", cartOrder);
    }
}
