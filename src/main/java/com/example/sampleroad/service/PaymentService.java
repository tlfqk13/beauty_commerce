package com.example.sampleroad.service;

import com.example.sampleroad.common.exception.ErrorCode;
import com.example.sampleroad.common.exception.ErrorCustomException;
import com.example.sampleroad.domain.grouppurchase.GroupPurchaseRoomMember;
import com.example.sampleroad.domain.order.Orders;
import com.example.sampleroad.domain.product.ProductType;
import com.example.sampleroad.dto.request.order.OrderRequestDto;
import com.example.sampleroad.dto.response.PaymentResponseDto;
import com.example.sampleroad.dto.response.cart.CartQueryDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.example.sampleroad.repository.cart.CartItemRepository;
import com.example.sampleroad.repository.cart.CartRepository;
import com.example.sampleroad.repository.orders.OrdersRepository;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrdersRepository ordersRepository;
    private final GroupPurchaseService groupPurchaseService;
    private final ProductPurchaseService productPurchaseService;
    private final PaymentOrderService paymentOrderService;

    @Transactional
    public void successPayment(String orderNo, UserDetailsImpl userDetails) {
        log.info("결제 완료 시작 - orderNo: {}", orderNo);
        try {
            Long memberId = userDetails.getMember().getId();
            Orders orders = createOrders(orderNo, userDetails);
            processNonGroupPurchasePayment(memberId, orders, userDetails);
            log.info("결제 완료 종료 - orderNo: {}", orderNo);
        } catch (Exception e) {
            log.error("결제 완료 처리 중 오류 발생 - message: {}", e.getMessage());
            throw new ErrorCustomException(ErrorCode.CALL_CUSTOMER_INFORMATION);
        }
    }

    @Transactional
    public PaymentResponseDto.Confirm successPayment(OrderRequestDto.PaymentConfirm dto, UserDetailsImpl userDetails) {
        log.info("결제 완료 시작 - orderNo: {}", dto.getOrderNo());
        log.info("결제 완료 시작 - getRoomId: {}", dto.getRoomId());
        try {
            if (dto.getProductType() != null && ProductType.GROUP_PURCHASE.equals(dto.getProductType())) {
                // TODO: 2024-04-03 팀구매도 ordersItem 생성 해야해
                Orders orders = createOrders(dto.getOrderNo(), userDetails);
                Optional<GroupPurchaseRoomMember> groupPurchaseRoomMember =
                        groupPurchaseService.updateRoomMemberFinishPayment(dto.getRoomId(), userDetails.getMember().getId(), orders);
                paymentOrderService.getOrderResult(userDetails,orders,null,false);
                log.info("팀 구매 결제 완료 종료 - orderNo: {}", dto.getOrderNo());
                return groupPurchaseService.successPayment(dto, groupPurchaseRoomMember, userDetails);
            } else {
                successPayment(dto.getOrderNo(), userDetails);
                log.info("일반 결제 완료 종료 - orderNo: {}", dto.getOrderNo());
                return productPurchaseService.successPayment();
            }
        } catch (Exception e) {
            log.error("결제 완료 처리 중 오류 발생 - message: {}", e.getMessage());
            throw new ErrorCustomException(ErrorCode.CALL_CUSTOMER_INFORMATION);
        }
    }

    private void processNonGroupPurchasePayment(Long memberId, Orders orders, UserDetailsImpl userDetails) throws UnirestException, ParseException {
        List<CartQueryDto> cartList = cartRepository.findCartInfoByMemberId(memberId);
        deleteCart(memberId, cartList);
        List<Integer> kitProductNosInCart = extractKitProductNos(cartList);
        // TODO: 2024-04-03 ordersItem 생성
        log.info("결제 완료 getOrderResult 호출 시작 - orderNo: {}", orders.getOrderNo());
        paymentOrderService.getOrderResult(userDetails, orders, kitProductNosInCart, true);
    }

    private List<Integer> extractKitProductNos(List<CartQueryDto> cartList) {
        return cartList.stream()
                .filter(cartProduct -> !cartProduct.isCustomKit())
                .map(CartQueryDto::getProductNo)
                .collect(Collectors.toList());
    }

    private Orders createOrders(String orderNo, UserDetailsImpl userDetails) {
        Orders orders = Orders.builder()
                .orderNo(orderNo)
                .member(userDetails.getMember())
                .isMadeOrdersItem(false)
                .build();
        ordersRepository.save(orders);
        return orders;
    }

    /**
     * 결제 완료 이후, 카트에 담긴 커스텀키트 삭제
     *
     * @param
     * @param
     * @param cartList
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/07/03
     **/
    private void deleteCart(Long memberId, List<CartQueryDto> cartList) {
        // 스트림을 한 번만 사용하여 필요한 데이터를 추출합니다.
        Set<Long> cartItemIds = new HashSet<>();
        Set<Long> cartIds = new HashSet<>();
        cartList.forEach(item -> {
            cartItemIds.add(item.getCartItemId());
            cartIds.add(item.getCartId());
        });

        // 삭제 작업을 수행합니다.
        if (!cartItemIds.isEmpty()) {
            cartItemRepository.deleteAllByIdInQuery(cartItemIds);
        }
        if (!cartIds.isEmpty()) {
            cartRepository.deleteAllByCartIdsInQuery(cartIds);
        }
    }
}
