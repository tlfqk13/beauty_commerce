package com.example.sampleroad.service;

import com.example.sampleroad.domain.order.Orders;
import com.example.sampleroad.domain.order.OrdersItem;
import com.example.sampleroad.domain.product.Product;
import com.example.sampleroad.domain.survey.ZeroExperienceQuestionItem;
import com.example.sampleroad.dto.response.order.OrderDetailResponseDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.example.sampleroad.repository.orders.OrdersItemRepository;
import com.example.sampleroad.repository.product.ProductRepository;
import com.example.sampleroad.repository.zeroExperience.ZeroExperienceQuestionItemRepository;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentOrderService {
    private final OrderService orderService;
    private final OrdersItemRepository ordersItemRepository;
    private final ProductRepository productRepository;
    private final ZeroExperienceQuestionItemRepository zeroExperienceQuestionItemRepository;

    @Transactional
    public void getOrderResult(UserDetailsImpl userDetails, Orders orders,
                               List<Integer> kitProductNosInCart, boolean isGeneralPurchase) throws UnirestException, ParseException {
        // Fetch order details once and reuse
        log.info("결제 완료 newOrderDetail 호출 시작 - orderNo: {}", orders.getOrderNo());
        OrderDetailResponseDto.OrderDetail newOrderDetail =
                orderService.getNewOrderDetail(userDetails, orders.getOrderNo());
        log.info("결제 완료 newOrderDetail 호출 종료 - orderNo: {}", orders.getOrderNo());

        Map<Integer, OrderDetailResponseDto.InOrderDetailProduct> productMap = new HashMap<>();
        newOrderDetail.getOrderDetailSection().forEach(section -> {
            section.getProducts().forEach(product -> {
                if (product instanceof OrderDetailResponseDto.InOrderDetailProduct) {
                    OrderDetailResponseDto.InOrderDetailProduct inPayProduct = (OrderDetailResponseDto.InOrderDetailProduct) product;
                    productMap.put(inPayProduct.getProductNo(), inPayProduct);
                }
            });
        });

        // productMap의 키셋을 이용하여 productNos 리스트 생성
        List<Integer> productNos = new ArrayList<>(productMap.keySet());

        Map<Long, Long> ordersItem = createOrdersItem(orders, productMap, productNos);
        // TODO: 4/2/24 무료체험 필수 설문 표기
        if (isGeneralPurchase && kitProductNosInCart != null) {
            createQuestionSurveyItems(kitProductNosInCart, ordersItem, userDetails);
        }
        log.info("결제 완료 getOrderResult 호출 종료 - orderNo: {}", orders.getOrderNo());
    }

    private Map<Long, Long> createOrdersItem(Orders orders, Map<Integer, OrderDetailResponseDto.InOrderDetailProduct> productMap, List<Integer> productNos) {
        return getOrderAndCreateOrdersItem(orders, productMap, productNos);
    }

    private Map<Long, Long> getOrderAndCreateOrdersItem(Orders orders, Map<Integer, OrderDetailResponseDto.InOrderDetailProduct> newInPayInfoProductMap, List<Integer> productNos) {
        List<Product> products = productRepository.findByProductNoIn(productNos);
        Map<Integer, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getProductNo, Function.identity()));
        List<OrdersItem> newOrderItems = new ArrayList<>();
        Map<Long, Long> ordersItemIdProductNoMap = new HashMap<>();
        for (Integer productNo : productNos) {
            Product product = productMap.get(productNo);
            if (product != null) {
                OrdersItem ordersItem = OrdersItem.builder()
                        .product(product)
                        .orders(orders)
                        .orderOptionNo(newInPayInfoProductMap.get(productNo).getOrderOptionNo())
                        .productCnt(newInPayInfoProductMap.get(productNo).getOrderCnt())
                        .build();
                newOrderItems.add(ordersItem);
                ordersItemIdProductNoMap.put(product.getId(), ordersItem.getId());

            }
            ordersItemRepository.saveAll(newOrderItems);
        }
        return ordersItemIdProductNoMap;
    }

    private void createQuestionSurveyItems(List<Integer> kitProductNosInCart, Map<Long, Long> ordersItemMap, UserDetailsImpl userDetails) {
        Random random = new Random();
        List<Product> products = productRepository.findByProductNoIn(kitProductNosInCart);

        if (!products.isEmpty()) {
            Product selectedProduct = products.get(random.nextInt(products.size()));
            Optional.ofNullable(ordersItemMap.get(selectedProduct.getId()))
                    .flatMap(ordersItemRepository::findById)
                    .ifPresent(ordersItem -> {
                        ZeroExperienceQuestionItem zeroExperienceQuestionItem =
                                ZeroExperienceQuestionItem.builder()
                                        .ordersItem(ordersItem)
                                        .member(userDetails.getMember())
                                        .build();
                        zeroExperienceQuestionItemRepository.save(zeroExperienceQuestionItem);
                    });
        }
    }
}
