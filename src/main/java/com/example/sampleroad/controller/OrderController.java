package com.example.sampleroad.controller;

import com.example.sampleroad.common.exception.ErrorCode;
import com.example.sampleroad.common.exception.ErrorCustomException;
import com.example.sampleroad.common.utils.CustomValue;
import com.example.sampleroad.common.utils.ResultInfo;
import com.example.sampleroad.dto.request.order.OrderRequestDto;
import com.example.sampleroad.dto.response.AddressSearchResponseDto;
import com.example.sampleroad.dto.response.PaymentResponseDto;
import com.example.sampleroad.dto.response.order.OrderCancelResponseDto;
import com.example.sampleroad.dto.response.order.OrderDetailResponseDto;
import com.example.sampleroad.dto.response.order.OrderNewResponseDto;
import com.example.sampleroad.dto.response.order.OrderResponseDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.example.sampleroad.service.OrderService;
import com.example.sampleroad.service.PaymentService;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@Api(tags = {"주문 관련 api Controller"})
public class OrderController {

    private final OrderService orderService;
    private final PaymentService paymentService;

    @GetMapping("/api/order-list")
    @ApiOperation(value = "주문/배송 목록 조회 api")
    public OrderResponseDto.OrderListShopby getOrderListShopby(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(defaultValue = CustomValue.pageNumber) int pageNumber,
            @RequestParam(defaultValue = CustomValue.pageSize) int pageSize) throws UnirestException, ParseException {

        return orderService.getOrderListShopby(userDetails, pageNumber, pageSize);
    }

    @GetMapping("/api/new/order-list")
    @ApiOperation(value = "주문/배송 목록 조회 api")
    public OrderResponseDto.NewOrderListShopby getNewOrderListShopby(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(defaultValue = CustomValue.pageNumber) int pageNumber,
            @RequestParam(defaultValue = CustomValue.pageSize) int pageSize) throws UnirestException, ParseException {

        return orderService.getNewOrderListShopby(userDetails, pageNumber, pageSize);
    }

    @GetMapping("/api/order-detail/{orderNo}")
    @ApiOperation(value = "주문 상세 조회하기 api")
    public OrderDetailResponseDto.OrderDetail getOrderDetail(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                             @PathVariable String orderNo) throws UnirestException, ParseException {
        return orderService.getOrderDetail(userDetails, orderNo);
    }

    @GetMapping("/api/new/order-detail/{orderNo}")
    @ApiOperation(value = "주문 상세 조회하기 api")
    public OrderDetailResponseDto.OrderDetail getNewOrderDetail(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                @PathVariable String orderNo) throws UnirestException, ParseException {
        return orderService.getNewOrderDetail(userDetails, orderNo);
    }

    @PutMapping("/api/order-confirm")
    @ApiOperation(value = "구매 확정")
    public ResultInfo confirmOrder(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                   @RequestBody OrderRequestDto.OrderOptionNos orderOptionNos) throws UnirestException, ParseException {
        orderService.confirmOrder(userDetails, orderOptionNos);
        return new ResultInfo(ResultInfo.Code.SUCCESS, "구매 확정 완료");
    }

    @PostMapping("/api/order-cancel/all")
    @ApiOperation(value = "주문 전체 취소하기")
    public ResultInfo cancelOrder(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                              @RequestBody OrderRequestDto.CancelOrder cancelOrderDto) throws UnirestException, ParseException {
        orderService.cancelOrder(userDetails, cancelOrderDto);
        return new ResultInfo(ResultInfo.Code.SUCCESS, "주문 취소 완료");
    }

    /**
     * 주문취소 -> 취소 상품 조회 + 취소 예상금액 조회까지 싹
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 4/2/24
     **/
    @GetMapping("/api/order-cancel/info/{orderNo}")
    @ApiOperation(value = "주문 취소 조회")
    public OrderCancelResponseDto.OrderCancelInfo getCancelOrderInfo(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable String orderNo) throws UnirestException, ParseException {
        return orderService.getCancelOrderInfo(userDetails, orderNo);
    }

    // TODO: 2023/10/05 취소 페이지 들어갈때 상품 정보 내려주자
    @PostMapping("/api/order-cancel/info")
    @ApiOperation(value = "주문 취소 상품 조회")
    public OrderNewResponseDto getCancelOrderProduct(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                     @RequestBody OrderRequestDto.CancelOrderProductInfo cancelOrderProductInfo) throws UnirestException, ParseException {
        return orderService.getCancelOrderProduct(userDetails, cancelOrderProductInfo);
    }

    @PostMapping("/api/order-cancel/{orderNo}")
    @ApiOperation(value = "주문 부분 취소하기")
    public ResultInfo cancelPartialOrder(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                         @PathVariable String orderNo,
                                         @RequestBody OrderRequestDto.CancelOrder cancelOrderDto) throws UnirestException, ParseException {
        orderService.cancelPartialOrder(userDetails, cancelOrderDto, orderNo);
        return new ResultInfo(ResultInfo.Code.SUCCESS, "주문 취소 완료");
    }

    /**
     * 주문상세에서 결제 취소 누르면 실행되는 첫 메소드
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2024/01/02
     **/
    @PostMapping("/api/calculate/order-cancel")
    public OrderCancelResponseDto.CalculateCancelOrder calculateCancelOrder(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                            @RequestBody OrderRequestDto.CancelOrder cancelOrderDto) throws UnirestException, ParseException {
        return orderService.calculateCancelOrder(userDetails, cancelOrderDto);
    }

    /**
     * 상품상세에서 상품 바로 주문하기
     *
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/06/08
     **/
    @PostMapping("/api/order")
    @ApiOperation(value = "주문서 작성하기")
    public ResultInfo createOrder(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                  @RequestBody OrderRequestDto.CreateOrder dto) throws UnirestException, ParseException {
        OrderResponseDto.CreateOrderSheet order = orderService.createOrder(userDetails, dto);
        return new ResultInfo(ResultInfo.Code.SUCCESS, "주문서 작성 완료", order);
    }

    /**
     * 주문서 가져오기 사용하는지 확인하고 아니면 삭제
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/12/30
     **/
    @GetMapping("/api/order/{orderSheetNo}")
    @ApiOperation(value = "주문서 가져오기")
    public OrderResponseDto.ResponseDto getOrderSheet(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                      @PathVariable String orderSheetNo) throws UnirestException, ParseException {

        return orderService.getOrderSheet(userDetails, orderSheetNo);

    }

    @GetMapping("/api/address-search")
    @ApiOperation(value = "주소 검색")
    public AddressSearchResponseDto searchAddress(@RequestParam(defaultValue = "1") int pageNumber,
                                                  @RequestParam(defaultValue = CustomValue.pageSize) int pageSize,
                                                  @RequestParam(defaultValue = "") String keyword) throws ParseException, UnirestException {

        return orderService.searchAddress(pageNumber, pageSize, keyword);
    }

    @PostMapping("/api/order-sheet/{orderSheetNo}/calculate")
    @ApiOperation(value = "주문에 관련된 비용 계산(상품가,결제 예정,배송비,쿠폰...")
    public ResultInfo calculatePaymentPrice(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                            @RequestBody OrderRequestDto.CalculateOrder dto,
                                            @PathVariable String orderSheetNo) throws UnirestException, ParseException {
        HashMap<String, Object> resultInfo = orderService.calculateAllPaymentPrice(userDetails, dto, orderSheetNo);
        return new ResultInfo(ResultInfo.Code.SUCCESS, "결제액 계산 완료", resultInfo);
    }

    @GetMapping("/api/payment/confirm")
    public ResultInfo paymentConfirm(@RequestParam(defaultValue = "") String orderNo,
                                     @RequestParam(defaultValue = "") String result,
                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {

        if (result.equalsIgnoreCase("SUCCESS")) {
            paymentService.successPayment(orderNo, userDetails);
            return new ResultInfo(ResultInfo.Code.SUCCESS, "결제 완료");
        } else {
            throw new ErrorCustomException(ErrorCode.PAYMENT_FAIL);
        }
    }

    /**
     * 결제 완료 요청 리뉴얼
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2/15/24
     **/
    @PostMapping("/api/payment/confirm")
    public PaymentResponseDto.Confirm paymentConfirm(@RequestBody OrderRequestDto.PaymentConfirm dto,
                                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {

        // TODO: 4/2/24 샵바이 주문 상세 조회가 언제부터 활성화.
        if (dto.getResult().equalsIgnoreCase("SUCCESS")) {
            return paymentService.successPayment(dto, userDetails);
        } else {
            throw new ErrorCustomException(ErrorCode.PAYMENT_FAIL);
        }
    }
}
