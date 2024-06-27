package com.example.sampleroad.service;

import com.example.sampleroad.common.exception.ErrorCode;
import com.example.sampleroad.common.exception.ErrorCustomException;
import com.example.sampleroad.domain.NoticeType;
import com.example.sampleroad.domain.grouppurchase.GroupPurchaseType;
import com.example.sampleroad.domain.product.ProductType;
import com.example.sampleroad.dto.request.order.OrderRequestDto;
import com.example.sampleroad.dto.response.grouppurchase.GroupPurchaseQueryDto;
import com.example.sampleroad.dto.response.grouppurchase.GroupPurchaseResponseDto;
import com.example.sampleroad.dto.response.order.OrderResponseDto;
import com.example.sampleroad.dto.response.order.OrdersQueryDto;
import com.example.sampleroad.dto.response.product.ProductDetailResponseDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.example.sampleroad.repository.orders.OrdersRepository;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class GroupPurchaseOrderService {

    private final OrderService orderService;
    private final GroupPurchaseService groupPurchaseService;
    private final ProductService productService;
    private final NoticeService noticeService;
    private final OrdersRepository ordersRepository;

    @Transactional
    public GroupPurchaseResponseDto.CreateOrderSheet createOrder(UserDetailsImpl userDetails, OrderRequestDto.GroupPurchaseOrder dto) throws UnirestException, ParseException {
        if (dto.getProducts().isEmpty()) {
            throw new ErrorCustomException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        OrderResponseDto.CreateOrderSheet order = orderService.createOrder(userDetails, dto);
        List<GroupPurchaseResponseDto.GroupPurchaseOrderSectionResponseDto> sections = getGroupPurchaseOrderSection(userDetails, dto);
        Long roomId = dto.getRoomId();
        GroupPurchaseType memberRoomType = (roomId != null) ? GroupPurchaseType.ROOM_JOIN : GroupPurchaseType.ROOM_MAKE;

        // 로그 메시지와 방 처리 로직을 조건에 따라 분리
        if (roomId != null) {
            log.info("방 참여입니다");
            groupPurchaseService.joinGroupPurchaseRoom(roomId, userDetails, memberRoomType);
        } else {
            log.info("방 개설입니다");
            roomId = groupPurchaseService.makeGroupPurchaseRoom(dto.getProducts().get(0).getProductNo(), userDetails, memberRoomType);
        }

        return new GroupPurchaseResponseDto.CreateOrderSheet(order.getOrderSheetNo(), roomId,
                order.getPaymentInfo(), order.getCouponInfo(), sections);
    }

    private List<GroupPurchaseResponseDto.GroupPurchaseOrderSectionResponseDto> getGroupPurchaseOrderSection(UserDetailsImpl userDetails, OrderRequestDto.GroupPurchaseOrder dto) throws UnirestException, ParseException {
        List<GroupPurchaseResponseDto.InGroupPurchaseProduct> purchaseInfoProductInfoList = new ArrayList<>();
        ProductType groupPurchase = ProductType.GROUP_PURCHASE;

        for (OrderRequestDto.Product productOrder : dto.getProducts()) {
            int productNo = productOrder.getProductNo(); // `int` to `long` 변경에 따른 코드 수정 필요 없음
            ProductDetailResponseDto productInfo = productService.getProductInfo(userDetails, productNo).getProductInfo();
            String[] imageUrls = productInfo.getImageUrls();
            GroupPurchaseResponseDto.InGroupPurchaseProduct productPurchaseInfo = new GroupPurchaseResponseDto.InGroupPurchaseProduct(
                    groupPurchase,
                    productNo,
                    productOrder.getOptionNo(),
                    productInfo.getProductName(),
                    productInfo.getBrandName(),
                    imageUrls[0],
                    productInfo.getSalePrice(),
                    productInfo.getImmediateDiscountAmt(),
                    productOrder.getOrderCnt()
            );
            purchaseInfoProductInfoList.add(productPurchaseInfo);
        }

        List<GroupPurchaseResponseDto.GroupPurchaseOrderSectionResponseDto> sections = new ArrayList<>();
        sections.add(new GroupPurchaseResponseDto.GroupPurchaseOrderSectionResponseDto(groupPurchase.toString(), "팀 구매 주문 상품", purchaseInfoProductInfoList));
        return sections;
    }

    /**
     * 팀구매 현황 , 주문내역 조회
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2/26/24
     **/
    @Transactional
    public GroupPurchaseResponseDto.PurchaseInfo getGroupPurchasePurchaseInfo(UserDetailsImpl userDetails) throws UnirestException, ParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // TODO: 2/26/24 마감시간 안지났고, 주문상태 cancel 아닌애들
        List<GroupPurchaseQueryDto.GroupPurchaseOrderInfoQueryDto> groupPurchaseRooms = groupPurchaseService.getGroupPurchaseRooms(userDetails.getMember().getId());
        String noticeImageUrl = noticeService.getNotice(NoticeType.GROUP_PURCHASE_INFO);

        if (groupPurchaseRooms.isEmpty()) {
            return new GroupPurchaseResponseDto.PurchaseInfo(Collections.emptyList(), noticeImageUrl);
        }

        Set<Integer> productNos = groupPurchaseRooms.stream().map(GroupPurchaseQueryDto::getProductNo).collect(Collectors.toSet());
        Map<Integer, Integer> productImmediateDiscountAmtMap = productService.getProductListInfo(productNos);

        List<Long> roomIds = groupPurchaseRooms.stream().map(GroupPurchaseQueryDto.GroupPurchaseOrderInfoQueryDto::getRoomId).collect(Collectors.toList());
        List<GroupPurchaseQueryDto.GroupPurchaseOrderInfoQueryDto> groupPurchaseRoomMembers = groupPurchaseService.getGroupPurchaseRoomMember(roomIds);
        Map<Long, List<GroupPurchaseQueryDto.GroupPurchaseOrderInfoQueryDto>> roomIdToMembersMap = groupPurchaseRoomMembers.stream()
                .collect(Collectors.groupingBy(GroupPurchaseQueryDto.GroupPurchaseOrderInfoQueryDto::getRoomId));

        // TODO: 2/29/24 ordersItem이 있는지 확인쿼리 필요
        List<String> orderNos = groupPurchaseRoomMembers
                .stream()
                .filter(o -> o.getMemberId().equals(userDetails.getMember().getId()))
                .map(GroupPurchaseQueryDto.GroupPurchaseOrderInfoQueryDto::getOrderNo)
                .collect(Collectors.toList());


        List<OrdersQueryDto.OrderCntQueryDto> orderCntByOrderNos = ordersRepository
                .findOrderCntByOrderNos(orderNos, userDetails.getMember().getId());

        createOrdersItem(userDetails, orderNos, orderCntByOrderNos);

        Map<String, Integer> orderCntMap = orderCntByOrderNos.stream().collect(Collectors.toMap(OrdersQueryDto.OrderCntQueryDto::getOrderNo, OrdersQueryDto.OrderCntQueryDto::getOrderCnt));
        // 제품 정보 리스트를 생성합니다.
        List<GroupPurchaseResponseDto.PurchaseInfoProductInfo> productInfoList = groupPurchaseRooms.stream()
                .filter(room -> LocalDateTime.parse(room.getDeadLineTime(), formatter).isAfter(LocalDateTime.now())) // 마감 시간이 지나지 않은 방만 필터링
                .map(room -> {
                    Long roomId = room.getRoomId();
                    List<GroupPurchaseQueryDto.GroupPurchaseOrderInfoQueryDto> groupPurchaseRoomMember = roomIdToMembersMap.getOrDefault(roomId, Collections.emptyList());
                    int remainingCapacity = room.getRoomCapacity() - groupPurchaseRoomMember.size();
                    return new GroupPurchaseResponseDto.PurchaseInfoProductInfo(
                            room.getProductNo(),
                            room.getProductName(),
                            room.getDeadLineTime(),
                            room.getProductImgUrl(),
                            productImmediateDiscountAmtMap.getOrDefault(room.getProductNo(), 0),
                            orderCntMap.getOrDefault(room.getOrderNo(), 1), // Assuming this is a placeholder value
                            remainingCapacity,
                            room.getOrderNo()
                    );
                })
                .collect(Collectors.toList());
        // 최종 결과를 PurchaseInfo 객체로 포장하여 반환합니다.
        return new GroupPurchaseResponseDto.PurchaseInfo(productInfoList, noticeImageUrl);
    }

    private void createOrdersItem(UserDetailsImpl userDetails, List<String> orderNos, List<OrdersQueryDto.OrderCntQueryDto> orderCntByOrderNos) throws UnirestException, ParseException {
        // TODO: 3/26/24 현재 ordersItem이 생성된 내 주문이 있는지 확인
        Set<String> existingOrderNos = orderCntByOrderNos.stream()
                .map(OrdersQueryDto.OrderCntQueryDto::getOrderNo)
                .collect(Collectors.toSet());

        // TODO: 3/26/24 현재 ordersItem이 생성안된 orderNo 찾기
        List<String> nonMatchingOrderNos = orderNos.stream()
                .filter(orderNo -> !existingOrderNos.contains(orderNo))
                .collect(Collectors.toList());

        if (!nonMatchingOrderNos.isEmpty()) {
            // TODO: 2/29/24 ordersItem 만들기용
            for (String nonMatchingOrderNo : nonMatchingOrderNos) {
                orderService.getOrderDetail(userDetails, nonMatchingOrderNo);
            }
        }
    }

}
