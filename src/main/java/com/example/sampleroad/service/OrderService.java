package com.example.sampleroad.service;

import com.example.sampleroad.common.exception.ErrorCode;
import com.example.sampleroad.common.exception.ErrorCustomException;
import com.example.sampleroad.common.utils.CustomValue;
import com.example.sampleroad.common.utils.ShopBy;
import com.example.sampleroad.domain.CategoryType;
import com.example.sampleroad.domain.DeliveryLocation;
import com.example.sampleroad.domain.cart.Cart;
import com.example.sampleroad.domain.cart.CartItem;
import com.example.sampleroad.domain.claim.ClaimStatus;
import com.example.sampleroad.domain.grouppurchase.GroupPurchaseRoomMember;
import com.example.sampleroad.domain.member.Member;
import com.example.sampleroad.domain.order.OrderStatus;
import com.example.sampleroad.domain.order.OrderType;
import com.example.sampleroad.domain.order.Orders;
import com.example.sampleroad.domain.order.OrdersItem;
import com.example.sampleroad.domain.product.EventProductType;
import com.example.sampleroad.domain.product.Product;
import com.example.sampleroad.domain.product.ProductType;
import com.example.sampleroad.dto.request.CartRequestDto;
import com.example.sampleroad.dto.request.CouponRequestDto;
import com.example.sampleroad.dto.request.customkit.CustomKitRequestDto;
import com.example.sampleroad.dto.request.order.OrderCalculatePaymentPriceRequestDto;
import com.example.sampleroad.dto.request.order.OrderRequestDto;
import com.example.sampleroad.dto.response.AddressSearchResponseDto;
import com.example.sampleroad.dto.response.DeliveryLocationResponseDto;
import com.example.sampleroad.dto.response.cart.CartResponseDto;
import com.example.sampleroad.dto.response.grouppurchase.GroupPurchaseQueryDto;
import com.example.sampleroad.dto.response.order.*;
import com.example.sampleroad.dto.response.product.EventProductQueryDto;
import com.example.sampleroad.dto.response.product.IProduct;
import com.example.sampleroad.dto.response.product.ProductDetailResponseDto;
import com.example.sampleroad.dto.response.product.ProductQueryDto;
import com.example.sampleroad.dto.response.wishList.WishListResponseDto;
import com.example.sampleroad.dto.response.zeroExperienceReview.ZeroExperienceRecommendSurveyQueryDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.example.sampleroad.repository.cart.CartItemRepository;
import com.example.sampleroad.repository.cart.CartRepository;
import com.example.sampleroad.repository.delivery.DeliveryLocationRepository;
import com.example.sampleroad.repository.member.MemberRepository;
import com.example.sampleroad.repository.orders.OrdersItemRepository;
import com.example.sampleroad.repository.orders.OrdersRepository;
import com.example.sampleroad.repository.product.ProductRepository;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class OrderService {

    private final MemberRepository memberRepository;
    private final DeliveryLocationRepository deliveryLocationRepository;
    private final ProductRepository productRepository;
    private final OrdersRepository ordersRepository;
    private final OrdersItemRepository ordersItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final EventService eventService;
    private final CouponService couponService;
    private final GroupPurchaseService groupPurchaseService;
    private final ProductShopByService productShopByService;
    private final OrderShopByService orderShopByService;
    private final ReviewService reviewService;


    @Value("${shop-by.client-id}")
    String clientId;

    @Value("${shop-by.accept-header}")
    String acceptHeader;

    @Value("${shop-by.version-header}")
    String versionHeader;

    @Value("${shop-by.platform-header}")
    String platformHeader;

    @Value("${shop-by.url}")
    String shopByUrl;

    @Value("${shop-by.check-member-url}")
    String profile;

    @Value("${shop-by.orders-url}")
    String ordersUrl;

    @Value("${shop-by.products}")
    String products;

    @Value("${shop-by.sampleKit-img}")
    String sampleKitImg;

    @Value("${shop-by.zero-perfume-category-no}")
    String zeroPerfumeCategoryNo;

    Gson gson = new Gson();

    public OrderResponseDto.OrderListShopby getOrderListShopby(UserDetailsImpl userDetails, int pageNumber, int pageSize) throws UnirestException, ParseException {
        OrderResponseDto.OrderListShopby orderListShopby = shopbyGetOrderList(userDetails.getMember().getShopByAccessToken(), pageNumber, pageSize, null);

        Set<Integer> productNos = extractProductNos(orderListShopby);
        List<Integer> sampleProductNos = getSampleProductNos(productNos);

        // TODO: 2/19/24 팀 구매 제품인지 여부
        Set<Integer> groupPurchaseProductNos = null;
        if (!productNos.isEmpty()) {
            groupPurchaseProductNos = groupPurchaseService.groupPurchaseProduct();
        }

        processOrderListGroups(orderListShopby, sampleProductNos, groupPurchaseProductNos);

        return orderListShopby;
    }


    public OrderResponseDto.NewOrderListShopby getNewOrderListShopby(UserDetailsImpl userDetails, int pageNumber, int pageSize) throws UnirestException, ParseException {
        OrderResponseDto.NewOrderListShopby newOrderList = orderShopByService.shopbyGetOrderList(
                userDetails.getMember().getShopByAccessToken(), pageNumber, pageSize, null);

        Set<Integer> groupPurchaseProductNos = groupPurchaseService.groupPurchaseProduct();

        List<OrderResponseDto.NewOrderListGroup> updatedGroups = new ArrayList<>();

        for (OrderResponseDto.NewOrderListGroup group : newOrderList.getItems()) {
            OrderType orderType = OrderType.GENERAL_PURCHASE;

            int productNo = group.getOrderProduct().getProductNo();

            if (groupPurchaseProductNos.contains(productNo)) {
                orderType = OrderType.GROUP_PURCHASE;
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                OrderStatus groupPurchaseType = getGroupPurchaseType(group.getOrderProduct().getOrderNo(), formatter);
                if (!group.getOrderProduct().getOrderStatus().equals(OrderStatus.CANCEL_DONE)) {
                    group.getOrderProduct().setGroupPurchaseType(groupPurchaseType);
                }
            }

            OrderResponseDto.NewOrderListGroup updatedGroup = new OrderResponseDto.NewOrderListGroup(
                    orderType,
                    group.getTotalProductCount(),
                    group.getOrderProduct());

            updatedGroups.add(updatedGroup);
        }
        return new OrderResponseDto.NewOrderListShopby(newOrderList.getTotalCount(), updatedGroups);
    }

    private Set<Integer> extractProductNos(OrderResponseDto.OrderListShopby orderListShopby) {
        return orderListShopby.getItems().stream()
                .flatMap(group -> group.getNonCustomKitList().stream())
                .map(OrderResponseDto.Products::getProductNo)
                .collect(Collectors.toSet());
    }

    private List<Integer> getSampleProductNos(Set<Integer> productNos) {
        return productRepository.findByProductNoIn(productNos).stream()
                .filter(item -> CategoryType.SAMPLE.equals(item.getCategory().getCategoryDepth1()))
                .map(Product::getProductNo)
                .collect(Collectors.toList());
    }

    private void processOrderListGroups(OrderResponseDto.OrderListShopby orderListShopby, List<Integer> sampleProductNos, Set<Integer> groupPurchaseProductNos) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Set<Integer> sampleProductNosSet = new HashSet<>(sampleProductNos);
        Map<Integer, String> groupPurchaseProductImgUrl = new HashMap<>();
        if (groupPurchaseProductNos != null) {
            groupPurchaseProductImgUrl = loadGroupPurchaseProductImgUrl(groupPurchaseProductNos);
        }

        // 각 그룹을 독립적으로 처리합니다.
        for (OrderResponseDto.OrderListGroup group : orderListShopby.getItems()) {
            List<OrderResponseDto.Products> customKitList = new ArrayList<>();
            List<OrderResponseDto.Products> remainingProducts = new ArrayList<>();

            // 한 번의 호출로 제품을 분류합니다.
            partitionProducts(group.getNonCustomKitList(), sampleProductNosSet, groupPurchaseProductNos, customKitList, remainingProducts, groupPurchaseProductImgUrl);

            // 각 그룹에 대한 리스트와 주문 유형을 설정합니다.
            group.setCustomKitList(customKitList);
            group.setNonCustomKitList(remainingProducts);
            group.setOrderType(determineOrderType(groupPurchaseProductNos, remainingProducts) ? OrderType.GROUP_PURCHASE : OrderType.GENERAL_PURCHASE);
        }

        updateGroupPurchaseTypes(orderListShopby.getItems().stream()
                .flatMap(group -> group.getNonCustomKitList().stream())
                .collect(Collectors.toList()), groupPurchaseProductNos, formatter);
    }

    private void partitionProducts(List<OrderResponseDto.Products> products, Set<Integer> sampleProductNosSet, Set<Integer> groupPurchaseProductNos, List<OrderResponseDto.Products> customKitList, List<OrderResponseDto.Products> remainingProducts, Map<Integer, String> groupPurchaseProductImgUrl) {
        for (OrderResponseDto.Products product : products) {
            if (sampleProductNosSet.contains(product.getProductNo()) && !groupPurchaseProductNos.contains(product.getProductNo())) {
                product.setIsCustomKit(true);
                customKitList.add(product);
            } else {
                if (groupPurchaseProductNos.contains(product.getProductNo())) {
                    product.setImageUrl(groupPurchaseProductImgUrl.getOrDefault(product.getProductNo(), ""));
                }
                remainingProducts.add(product);
            }
        }
    }

    private boolean determineOrderType(Set<Integer> groupPurchaseProductNos, List<OrderResponseDto.Products> products) {
        return products.stream().anyMatch(product -> groupPurchaseProductNos.contains(product.getProductNo()));
    }

    private Map<Integer, String> loadGroupPurchaseProductImgUrl(Set<Integer> groupPurchaseProductNos) {
        if (groupPurchaseProductNos.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Product> groupProductList = productRepository.findByProductNoIn(groupPurchaseProductNos);
        return groupProductList.stream().collect(Collectors.toMap(Product::getProductNo, Product::getImgUrl));
    }

    private void updateGroupPurchaseTypes(List<OrderResponseDto.Products> products, Set<Integer> groupPurchaseProductNos, DateTimeFormatter formatter) {
        // 주문 번호를 기반으로 한 번에 모든 관련 멤버 프로필을 가져옵니다.
        List<String> orderNos = products.stream().map(OrderResponseDto.Products::getOrderNo).collect(Collectors.toList());
        Map<String, GroupPurchaseQueryDto.GroupPurchaseOrderQueryDto> groupPurchaseRoomMembers = groupPurchaseService.getGroupPurchaseRoomMembersWithOrderNos(orderNos, true);

        // 가져온 멤버 프로필을 사용하여 각 제품의 상태를 업데이트합니다.
        for (OrderResponseDto.Products product : products) {
            if (groupPurchaseProductNos.contains(product.getProductNo())) {
                GroupPurchaseQueryDto.GroupPurchaseOrderQueryDto memberOrder = groupPurchaseRoomMembers.get(product.getOrderNo());
                if (memberOrder != null) {
                    updateProductGroupPurchaseType(product, memberOrder, formatter);
                }
            } else {
                product.setGroupPurchaseType(null);
            }
        }
    }

    private void updateProductGroupPurchaseType(OrderResponseDto.Products product, GroupPurchaseQueryDto.GroupPurchaseOrderQueryDto groupPurchaseOrderQueryDto, DateTimeFormatter formatter) {
        // 마감 시간을 LocalDateTime으로 파싱합니다.
        LocalDateTime deadLineTime = LocalDateTime.parse(groupPurchaseOrderQueryDto.getDeadLineTime(), formatter);

        if (groupPurchaseOrderQueryDto.isFull()) {
            product.setGroupPurchaseType(OrderStatus.GROUP_PURCHASE_FINISH);
        } else {
            if (deadLineTime.isBefore(LocalDateTime.now().minusDays(1L))) {
                product.setGroupPurchaseType(OrderStatus.GROUP_PURCHASE_FINISH);
            } else {
                product.setGroupPurchaseType(OrderStatus.GROUP_PURCHASE_READY);
            }
        }
        if (OrderStatus.CANCEL_DONE.equals(product.getOrderStatusType())) {
            product.setGroupPurchaseType(null);
        }
    }

    private LocalDateTime getDeadlineTimeForProduct(String orderNo, DateTimeFormatter formatter) {
        GroupPurchaseQueryDto.MemberProfileQueryDto groupPurchaseRoomMember = groupPurchaseService.getGroupPurchaseRoomMemberWithOrderNo(orderNo, false);

        // First, check if groupPurchaseRoomMember is not null
        if (groupPurchaseRoomMember != null) {
            // Check if the room is full; if so, return yesterday's date as the deadline
            if (groupPurchaseRoomMember.isFull()) {
                return LocalDateTime.now().minusDays(1L);
            }

            // If the room is not full and a deadline time is available, parse and return it
            if (groupPurchaseRoomMember.getDeadLineTime() != null) {
                return LocalDateTime.parse(groupPurchaseRoomMember.getDeadLineTime(), formatter);
            }
        }

        // Return yesterday's date as a default or for null cases, implying a finished status or past deadline
        return LocalDateTime.now().minusDays(1L);
    }

    @Transactional
    public void confirmOrder(UserDetailsImpl userDetails, OrderRequestDto.OrderOptionNos orderOptionNos) throws UnirestException, ParseException {
        Member member = getMember(userDetails);
        for (Integer orderOptionNo : orderOptionNos.getOrderOptionNos()) {
            shopbyConfirmOrder(member.getShopByAccessToken(), orderOptionNo);
        }

        OrdersItemQueryDto.OrderIdByOrdersItem orderId = ordersItemRepository.findOrderIdByOrdersItemId(orderOptionNos.getOrderOptionNos(), userDetails.getMember().getId());

        if (orderId != null) {
            Optional<Orders> orders = ordersRepository.findById(orderId.getOrderId());
            orders.ifPresent(value -> value.updateOrderStatus(OrderStatus.BUY_CONFIRM));
        }
    }

    /**
     * 메소드의 설명을 여기에 작성한다.
     *
     * @param userDetails, 주문생성(products,cartNos)
     * @return orderSheet 번호
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/06/08
     **/
    @Transactional
    public OrderResponseDto.CreateOrderSheet createOrder(UserDetailsImpl userDetails, OrderRequestDto.CreateOrder createOrder) throws UnirestException, ParseException {
        List<OrderRequestDto.Product> orderProducts = createOrder.getProducts();
        List<Integer> productNos = orderProducts.stream()
                .map(OrderRequestDto.Product::getProductNo)
                .collect(Collectors.toList());

        List<OrderResponseDto.OrderSectionResponseDto> sections = getOrderSection(userDetails, createOrder);
        checkEventProductPriceCondition(userDetails, productNos);

        // TODO: 5/22/24 장바구니에서 주문 넘어가는 로직
        checkZeroPerfumeItems(userDetails, productNos);

        String orderSheetNo = shopbyCreateOrder(userDetails.getMember().getShopByAccessToken(), createOrder);
        OrderPaymentPriceResponseDto.PaymentInfo paymentInfo = calculateAllPaymentPrice(userDetails, createOrder, orderSheetNo);
        OrderCalculateCouponResponseDto couponByOrderSheetNo = couponService.getCouponByOrderSheetNo(userDetails, orderSheetNo);

        return new OrderResponseDto.CreateOrderSheet(orderSheetNo, paymentInfo, couponByOrderSheetNo, sections);
    }

    private List<OrderResponseDto.OrderSectionResponseDto> getOrderSection(UserDetailsImpl userDetails, OrderRequestDto.CreateOrder createOrder) throws UnirestException, ParseException {
        List<OrderResponseDto.InOrderProduct> inOrderProductsList = new ArrayList<>();
        // TODO: 3/21/24 productType 수정해야함
        ProductType productType = ProductType.SAMPLE;

        for (OrderRequestDto.Product productOrder : createOrder.getProducts()) {
            int productNo = productOrder.getProductNo(); // `int` to `long` 변경에 따른 코드 수정 필요 없음
            ProductDetailResponseDto productInfo = productShopByService.getProductInfo(userDetails, productNo);
            String[] imageUrls = productInfo.getImageUrls();
            OrderResponseDto.InOrderProduct inOrderProduct = new OrderResponseDto.InOrderProduct(
                    productType,
                    productInfo.getProductNo(),
                    productOrder.getOptionNo(),
                    productInfo.getBrandName(),
                    productInfo.getProductName(),
                    imageUrls[0],
                    productOrder.getOrderCnt(),
                    productInfo.getSalePrice(),
                    productInfo.getImmediateDiscountAmt()
            );

            inOrderProductsList.add(inOrderProduct);
        }

        List<OrderResponseDto.OrderSectionResponseDto> sections = new ArrayList<>();
        sections.add(new OrderResponseDto.OrderSectionResponseDto(productType.toString(), "팀 구매 주문 상품", inOrderProductsList));
        return sections;
    }


    public void validateGroupPurchaseProducts(List<Integer> productNos) throws UnirestException, ParseException {
        if (!productNos.isEmpty() && hasGroupPurchaseProduct(productNos)) {
            throw new ErrorCustomException(ErrorCode.GROUP_PURCHASE_PRODUCT_NOT_ADD_CART);
        }
    }

    private boolean hasGroupPurchaseProduct(List<Integer> productNos) throws UnirestException, ParseException {
        Set<Integer> groupPurchaseProductNos = groupPurchaseService.groupPurchaseProduct();
        return !groupPurchaseProductNos.isEmpty() && hasGroupPurchaseProductMatch(productNos, groupPurchaseProductNos);
    }

    public boolean hasGroupPurchaseProductMatch(List<Integer> productNos, Set<Integer> groupPurchaseProduct) {
        return !Collections.disjoint(productNos, groupPurchaseProduct);
    }

    /**
     * 팀 구매 주문하기
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2/15/24
     **/
    public OrderResponseDto.CreateOrderSheet createOrder(UserDetailsImpl userDetails, OrderRequestDto.GroupPurchaseOrder dto) throws UnirestException, ParseException {

        OrderRequestDto.CreateOrder createOrder = new OrderRequestDto.CreateOrder(dto.getProducts());
        String orderSheetNo = shopbyCreateOrder(userDetails.getMember().getShopByAccessToken(), createOrder);
        OrderPaymentPriceResponseDto.PaymentInfo paymentInfo = this.calculateAllPaymentPrice(userDetails, createOrder, orderSheetNo);
        OrderCalculateCouponResponseDto couponByOrderSheetNo = couponService.getCouponByOrderSheetNo(userDetails, orderSheetNo);
        return new OrderResponseDto.CreateOrderSheet(orderSheetNo, paymentInfo, couponByOrderSheetNo);
    }

    public OrderResponseDto.ResponseDto getOrderSheet(UserDetailsImpl userDetails, String orderSheetNo) throws UnirestException, ParseException {
        log.info("주문서 조회하기 사용___________?");
        log.info("주문서 조회하기 사용___________?");
        log.info("주문서 조회하기 사용___________?");
        Optional<DeliveryLocation> deliveryLocation = deliveryLocationRepository.findByMemberAndDefaultAddress(userDetails.getMember(), true);
        DeliveryLocationResponseDto.DeliveryLocation defaultDeliveryLocation = deliveryLocation
                .map(DeliveryLocationResponseDto.DeliveryLocation::new)
                .orElseGet(DeliveryLocationResponseDto.DeliveryLocation::new);

        // 샘플키트, 관리자키트 구분해서
        // orderSheet에 적힌 주문정보 샵바이에서 조회
        OrderResponseDto.getOrderSheet orderSheetItemListShopby = shopbyGetOrderSheet(userDetails.getMember().getShopByAccessToken(), orderSheetNo);
        List<OrderResponseDto.Orderer> ordererList = orderSheetItemListShopby.getOrderer();

        List<OrderResponseDto.Orderer> customKitProducts = ordererList.stream()
                .filter(OrderResponseDto.Orderer::getIsCustomKit)
                .collect(Collectors.toList());

        List<OrderResponseDto.Orderer> nonCustomKitProducts = ordererList.stream()
                .filter(orderProduct -> !orderProduct.getIsCustomKit())
                .collect(Collectors.toList());

        // 관리자가 만든 샘플리스트 경우 키트 구성을 보여줘야해서 해당 productNo추출
        List<Integer> productNoList = nonCustomKitProducts.stream()
                .map(OrderResponseDto.Orderer::getProductNo)
                .collect(Collectors.toList());

        List<ProductDetailResponseDto.SampleList> sampleList = productRepository.findSampleListByProductNoIn(productNoList);

        // 동일한 상품 번호(getProductNo())를 가지는 OrderProduct 객체들이 그룹화되어 맵에 저장됩니다.
        Map<Integer, List<OrderResponseDto.Orderer>> groupedDataMap = nonCustomKitProducts.stream()
                .collect(Collectors.groupingBy(OrderResponseDto.Orderer::getProductNo));

        for (OrderResponseDto.Orderer orderProduct : nonCustomKitProducts) {
            List<OrderResponseDto.Orderer> innerList = groupedDataMap.get(orderProduct.getProductNo());

            List<ProductDetailResponseDto.SampleList> filteredList = sampleList.stream()
                    .filter(sample -> innerList.stream()
                            .anyMatch(innerOrderProduct -> innerOrderProduct.getProductNo() == sample.getSampleKitProductNo()))
                    .collect(Collectors.toList());

            // setter 불가피하게 사용
            orderProduct.setSampleList(filteredList);
        }

        return new OrderResponseDto.ResponseDto(defaultDeliveryLocation, customKitProducts, nonCustomKitProducts);
    }

    public AddressSearchResponseDto searchAddress(int pageNumber, int pageSize, String keyword) throws ParseException, UnirestException {
        return shopbyGetSearchAddress(pageNumber, pageSize, keyword);
    }


    public OrderDetailResponseDto.OrderDetail getNewOrderDetail(UserDetailsImpl userDetails, String orderNo) throws UnirestException, ParseException {

        updateIsOrdersItem(userDetails, orderNo);

        OrderDetailResponseDto.NewOrderDetail newOrderDetail = orderShopByService.shopbyGetOrderDetail(userDetails.getMember().getShopByAccessToken(), orderNo);
        List<OrderDetailResponseDto.NewInPayInfoProduct> inPayInfoProductList = newOrderDetail.getInPayInfoProductList();
        if (OrderStatus.CANCEL_DONE.equals(newOrderDetail.getOrderInfo().getOrderStatusType())) {
            List<Integer> claimNos = inPayInfoProductList.stream().map(OrderDetailResponseDto.NewInPayInfoProduct::getClaimNo).collect(Collectors.toList());
            OrderResponseDto.CancelInfo cancelInfo = shopbyGetOrderCancelDetail(userDetails.getMember().getShopByAccessToken(), claimNos.get(0));
            newOrderDetail.getPayInfo().setCancelInfo(cancelInfo);
        }

        List<Integer> productNos = inPayInfoProductList.stream()
                .map(OrderDetailResponseDto.NewInPayInfoProduct::getProductNo)
                .collect(Collectors.toList());

        List<Integer> orderOptionNos = inPayInfoProductList.stream()
                .map(OrderDetailResponseDto.NewInPayInfoProduct::getOrderOptionNo)
                .collect(Collectors.toList());

        // TODO: 4/4/24 리뷰 정보가 들어간다? reviewId null 유무로 -> 쓰기,수정 요청 구분
        Map<Integer, Long> reviewByProductNos = new HashMap<>();
        if (!OrderStatus.CANCEL_DONE.equals(newOrderDetail.getOrderInfo().getOrderStatusType())) {
            reviewByProductNos = reviewService.getReviewByProductNos(orderOptionNos, userDetails);
        }

        List<Integer> filterOrderOptionNos = inPayInfoProductList.stream()
                .filter(item ->
                        item.getOrderStatusType().equals(OrderStatus.PAY_DONE) ||
                                item.getOrderStatusType().equals(OrderStatus.PRODUCT_PREPARE) ||
                                item.getOrderStatusType().equals(OrderStatus.DELIVERY_PREPARE) ||
                                item.getOrderStatusType().equals(OrderStatus.DELIVERY_ING) ||
                                item.getOrderStatusType().equals(OrderStatus.DELIVERY_DONE))
                .map(OrderDetailResponseDto.NewInPayInfoProduct::getOrderOptionNo)
                .collect(Collectors.toList());

        List<OrderDetailResponseDto.InOrderDetailProduct> customKitInCart = new ArrayList<>();
        List<OrderDetailResponseDto.InOrderDetailProduct> nonCustomKitInCart = new ArrayList<>();
        List<ProductQueryDto> productList = productRepository.findProductCategoryType2ByProductNos(productNos);

        Map<Integer, ProductQueryDto> productToMap = productList.stream()
                .collect(Collectors.toMap(ProductQueryDto::getProductNo, Function.identity()));

        createOrderDetailDto(orderNo, inPayInfoProductList, productToMap, customKitInCart, newOrderDetail, nonCustomKitInCart, reviewByProductNos);

        List<OrderDetailResponseDto.OrderDetailSectionResponseDto> orderDetailSections = getOrderDetailSectionResponseDtos(customKitInCart, nonCustomKitInCart);

        return new OrderDetailResponseDto.OrderDetail(
                newOrderDetail.getOrderInfo(),
                newOrderDetail.getShippingInfo(),
                newOrderDetail.getPayInfo(),
                orderDetailSections,
                filterOrderOptionNos
        );
    }

    private void updateIsOrdersItem(UserDetailsImpl userDetails, String orderNo) {
        ordersRepository.findByOrderNoAndMemberId(orderNo, userDetails.getMember().getId())
                .ifPresent(order -> {
                    if (!order.getIsMadeOrdersItem()) {
                        order.updateIsMadeOrdersItem(true);
                        ordersRepository.save(order); // Ensure changes are persisted.
                    }
                });
    }

    private void createOrderDetailDto(String orderNo, List<OrderDetailResponseDto.NewInPayInfoProduct> inPayInfoProductList,
                                      Map<Integer, ProductQueryDto> productToMap,
                                      List<OrderDetailResponseDto.InOrderDetailProduct> customKitInCart,
                                      OrderDetailResponseDto.NewOrderDetail newOrderDetail,
                                      List<OrderDetailResponseDto.InOrderDetailProduct> nonCustomKitInCart,
                                      Map<Integer, Long> reviewByProductNos) {

        for (OrderDetailResponseDto.NewInPayInfoProduct newInPayInfoProduct : inPayInfoProductList) {
            ProductQueryDto productQueryDto = productToMap.get(newInPayInfoProduct.getProductNo());
            CategoryType categoryType = productQueryDto != null ? productQueryDto.getCategoryType() : null;
            ProductType productType = mapCategoryToProductType(categoryType);

            OrderDetailResponseDto.InOrderDetailProduct inOrderDetailProduct = new OrderDetailResponseDto.InOrderDetailProduct(
                    productType,
                    newInPayInfoProduct.getOrderStatusType(),
                    newInPayInfoProduct.getProductNo(),
                    newInPayInfoProduct.getBrandName(),
                    newInPayInfoProduct.getProductName(),
                    newInPayInfoProduct.getProductImgUrl(),
                    newInPayInfoProduct.getOrderCnt(),
                    newInPayInfoProduct.getProductOptionNo(),
                    newInPayInfoProduct.getOrderOptionNo(),
                    newInPayInfoProduct.getProductStandardPrice(),
                    newInPayInfoProduct.getProductImmediateDiscountedPrice(),
                    reviewByProductNos.get(newInPayInfoProduct.getProductNo())
            );

            if (productType.equals(ProductType.SAMPLE)) {
                customKitInCart.add(inOrderDetailProduct);
            } else if (productType.equals(ProductType.GROUP_PURCHASE)) {
                newOrderDetail.getOrderInfo().setOrderType(OrderType.GROUP_PURCHASE);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                OrderStatus groupPurchaseType = getGroupPurchaseType(orderNo, formatter);

                if (OrderStatus.CANCEL_DONE.equals(newInPayInfoProduct.getOrderStatusType())) {
                    groupPurchaseType = null;
                }
                inOrderDetailProduct.setGroupPurchaseStatusType(groupPurchaseType);
                nonCustomKitInCart.add(inOrderDetailProduct);
            } else {
                nonCustomKitInCart.add(inOrderDetailProduct);
            }
        }
    }

    private static List<OrderDetailResponseDto.OrderDetailSectionResponseDto> getOrderDetailSectionResponseDtos(List<OrderDetailResponseDto.InOrderDetailProduct> customKitInCart, List<OrderDetailResponseDto.InOrderDetailProduct> nonCustomKitInCart) {
        List<OrderDetailResponseDto.OrderDetailSectionResponseDto> orderDetailSections = new ArrayList<>();
        if (!customKitInCart.isEmpty()) {
            // Adding "전체 상품" section with customKitInCart products
            orderDetailSections.add(new OrderDetailResponseDto.OrderDetailSectionResponseDto(
                    "전체 상품", // Consider using a constant or enum if this string is used in multiple places
                    customKitInCart
            ));
        }

        // Adding "0원 샘플" section with nonCustomKitInCart products
        if (!nonCustomKitInCart.isEmpty()) {
            orderDetailSections.add(new OrderDetailResponseDto.OrderDetailSectionResponseDto(
                    "0원 샘플", // Consider using a constant or enum if this string is used in multiple places
                    nonCustomKitInCart
            ));
        }
        return orderDetailSections;
    }

    private ProductType mapCategoryToProductType(CategoryType categoryType) {
        // Default mapping for null and other undefined categories
        if (categoryType == null) return ProductType.SAMPLE;

        switch (categoryType) {
            case GROUP_PURCHASE:
                return ProductType.GROUP_PURCHASE;
            case EXPERIENCE:
            case KIT:
                return ProductType.ZERO_SAMPLE;
            default:
                return ProductType.SAMPLE;
        }
    }

    @Transactional
    public OrderDetailResponseDto.OrderDetail getOrderDetail(UserDetailsImpl userDetails, String orderNo) throws UnirestException, ParseException {
        boolean isDetail = true;
        OrderResponseDto.OrderDetail orderDetail = shopbyGetOrderDetail(userDetails.getMember().getShopByAccessToken(), orderNo, isDetail);
        OrderStatus orderStatus = orderDetail.getOrderInfo().getOrderStatusType();
        List<Integer> productNos = orderDetail.getProductNos();
        List<Integer> orderOptionNos = orderDetail.getOrderOptionNos();
        List<Integer> claimNos = orderDetail.getClaimNos();
        List<Integer> finalOrderOptionNos = orderOptionNos;
        Map<Integer, Integer> productToOrderCntMap = new HashMap<>();

        // TODO: 2023/12/30 상품no로 orderOptionNos 가져옴
        Map<Integer, Integer> productToOrderOptionMap = IntStream.range(0, productNos.size())
                .boxed()
                .collect(Collectors.toMap(productNos::get, orderOptionNos::get));

        Map<Integer, Integer> productToClaimNoMap = IntStream.range(0, productNos.size())
                .filter(i -> claimNos.get(i) != null)
                .boxed()
                .collect(Collectors.toMap(productNos::get, claimNos::get));

        List<OrderResponseDto.InPayInfoProduct> inPayInfoProductList = orderDetail.getInPayInfoProductList();
        Map<Integer, OrderResponseDto.InPayInfoProduct> productPayInfoMap = inPayInfoProductList.stream()
                .collect(Collectors.toMap(OrderResponseDto.InPayInfoProduct::getProductNo, Function.identity()));

        // TODO: 2/28/24 취소 상세 조회
        OrderResponseDto.CancelInfo cancelInfo = null;
        if (orderStatus.equals(OrderStatus.CANCEL_DONE)) {
            cancelInfo = shopbyGetOrderCancelDetail(userDetails.getMember().getShopByAccessToken(), claimNos.get(0));

        }

        // Handling Group Purchase Product
        GroupPurchaseQueryDto.MemberProfileQueryDto groupPurchaseProduct = fetchGroupPurchaseProduct(orderNo, orderStatus);
        OrderType orderType = OrderType.GENERAL_PURCHASE;

        List<OrderResponseDto.SampleKitGroup> sampleKitList = new ArrayList<>();
        List<OrderResponseDto.CustomKitGroup> customKitListList = new ArrayList<>();
        List<ProductQueryDto> customKitProducts = new ArrayList<>();
        List<ProductQueryDto> nonCustomKitProducts = new ArrayList<>();

        if (groupPurchaseProduct != null) {
            List<OrderResponseDto.NextActionDetail> nextActionsForSampleKit = orderDetail.getNextActionList().stream()
                    .map(nextAction -> new OrderResponseDto.NextActionDetail(nextAction.getProductNo(), nextAction.getNextActionType()))
                    .collect(Collectors.toList());

            OrderResponseDto.SampleKitGroup sampleKitGroup = handleGroupPurchaseGroup(orderNo, productNos, productPayInfoMap,
                    productToOrderOptionMap, nextActionsForSampleKit, productToClaimNoMap);
            sampleKitList.add(sampleKitGroup);
            orderType = OrderType.GROUP_PURCHASE;
        } else {
            List<ProductQueryDto> productList = productRepository.findProductCategoryType2ByProductNos(productNos);
            for (ProductQueryDto product : productList) {
                // categoryDepth1의 값에 따라 리스트 분류
                switch (product.getCategoryType()) {
                    case EXPERIENCE:
                    case GROUP_PURCHASE:
                    case KIT:
                        nonCustomKitProducts.add(product);
                        break;
                    default:
                        customKitProducts.add(product);
                        break;
                }
            }

            Set<Integer> nonCustomKitProductNos = nonCustomKitProducts.stream().map(ProductQueryDto::getProductNo).collect(Collectors.toSet());
            List<OrderResponseDto.SampleList> sampleKitListByOrder = productRepository.findSampleKitListByOrder(nonCustomKitProductNos);
            Map<Integer, List<OrderResponseDto.SampleList>> groupedSampleLists = sampleKitListByOrder.stream()
                    .collect(Collectors.groupingBy(OrderResponseDto.SampleList::getSampleKitProductNo));

            List<OrderResponseDto.NextActionDetail> nextActionsForSampleKit = orderDetail.getNextActionList().stream()
                    .filter(nextAction -> nonCustomKitProductNos.contains(nextAction.getProductNo()))
                    .map(nextAction -> new OrderResponseDto.NextActionDetail(nextAction.getProductNo(), nextAction.getNextActionType()))
                    .collect(Collectors.toList());

            sampleKitList = groupedSampleLists.entrySet().stream()
                    .map(entry -> {
                        List<OrderResponseDto.SampleList> sampleList = entry.getValue();
                        String sampleKitName = Optional.ofNullable(sampleList.get(0).getSampleKitName())
                                .orElse("MD 추천 키트"); // "기본값"은 원하는 기본값으로 대체하세요.

                        OrderResponseDto.SampleKitGroup kitGroup =
                                new OrderResponseDto.SampleKitGroup(sampleKitName, entry.getKey(), Collections.emptyList());

                        kitGroup.setNextActions(new ArrayList<>(nextActionsForSampleKit));
                        List<Integer> claimNo = sampleList.stream()
                                .map(OrderResponseDto.SampleList::getSampleKitProductNo)
                                .map(productToClaimNoMap::get)
                                .distinct()
                                .collect(Collectors.toList());

                        kitGroup.setClaimNo(claimNo);

                        List<Integer> orderOptionNoList = sampleList.stream()
                                .map(OrderResponseDto.SampleList::getSampleKitProductNo)
                                .map(productToOrderOptionMap::get)
                                .filter(Objects::nonNull)
                                .distinct()
                                .collect(Collectors.toList());

                        kitGroup.setOrderOptionNo(orderOptionNoList.isEmpty() ? null : orderOptionNoList);

                        for (Integer integer : orderOptionNoList) {
                            finalOrderOptionNos.remove(integer);
                        }

                        OrderResponseDto.InPayInfoProduct inPayInfoProduct = productPayInfoMap.get(entry.getKey());

                        if (inPayInfoProduct != null) {
                            kitGroup.setSampleKitOrderCnt(inPayInfoProduct.getOrderCnt());
                            kitGroup.setSampleKitImage(inPayInfoProduct.getProductImgUrl());
                            kitGroup.setSampleKitBrandName(inPayInfoProduct.getBrandName());
                            kitGroup.setPrice(inPayInfoProduct.getProductStandardPrice());
                            kitGroup.setImmediatePrice(inPayInfoProduct.getProductImmediateDiscountedPrice());
                            kitGroup.setOrderStatusType(inPayInfoProduct.getOrderStatusType());
                            kitGroup.setClaimStatusType(inPayInfoProduct.getClaimStatusType());
                            kitGroup.setRetrieveInvoiceUrl(inPayInfoProduct.getRetrieveInvoiceUrl());
                            productToOrderCntMap.put(inPayInfoProduct.getProductNo(), inPayInfoProduct.getOrderCnt());
                        }

                        return kitGroup;
                    })
                    .collect(Collectors.toList());
        }

        List<Integer> customKitProductNos = customKitProducts.stream()
                .map(ProductQueryDto::getProductNo).collect(Collectors.toList());

        List<OrderResponseDto.customKitList> customKitList = new ArrayList<>();
        List<Product> customKitProductList = productRepository.findByProductNoIn(customKitProductNos);

        // TODO: 3/26/24 주문상세 -> ordersItem 생성
        createOrdersItem(userDetails, orderNo, productNos, productToOrderCntMap, productToOrderOptionMap);

        for (Product product : customKitProductList) {
            OrderResponseDto.customKitList customKitInfo = new OrderResponseDto.customKitList(
                    product.getId(),
                    product.getProductNo(),
                    product.getProductName(),
                    product.getBrandName(),
                    product.getImgUrl()
            );
            customKitList.add(customKitInfo);
        }

        Set<String> nextActionTypes = orderDetail.getNextActionList().stream()
                .filter(na -> customKitList.stream().anyMatch(ck -> ck.getProductNo() == na.getProductNo()))
                .map(OrderResponseDto.NextActionDetail::getNextActionType)
                .collect(Collectors.toSet());

        List<Integer> claimNoList = customKitList.stream()
                .map(OrderResponseDto.customKitList::getProductNo)
                .map(productToClaimNoMap::get)
                .filter(Objects::nonNull) // null 값 제외
                .collect(Collectors.toList());

        orderDetail.getInPayInfoProductList().stream()
                .filter(p -> customKitList.stream().anyMatch(ck -> ck.getProductNo() == p.getProductNo()))
                .forEach(p -> {
                    OrderResponseDto.customKitList customKit = customKitList.stream()
                            .filter(ck -> ck.getProductNo() == p.getProductNo())
                            .findFirst()
                            .orElse(null);

                    if (customKit != null) {
                        customKit.setPrice(p.getProductStandardPrice());
                        customKit.setImmediatePrice(p.getProductImmediateDiscountedPrice());
                    }
                });


        if (customKitList.isEmpty()) {
            orderOptionNos = Collections.emptyList();
        }

        List<OrderResponseDto.InPayInfoProduct> customKitInPayInfoProduct = orderDetail.getInPayInfoProductList().stream()
                .filter(p -> customKitList.stream().anyMatch(ck -> ck.getProductNo() == p.getProductNo()))
                .collect(Collectors.toList());

        // TODO: 2023/08/04 상품삭제경우 에러발생하는 포인트
        OrderStatus orderStatusType = null;
        ClaimStatus claimStatusType = null;
        OrderResponseDto.InPayInfoProduct payInfo = null;
        if (!customKitInPayInfoProduct.isEmpty()) {
            orderStatusType = customKitInPayInfoProduct.get(0).getOrderStatusType();
            claimStatusType = customKitInPayInfoProduct.get(0).getClaimStatusType();
            // TODO: 2023/08/04 상품삭제경우 에러발생하는 포인트
            payInfo = productPayInfoMap.get(customKitInPayInfoProduct.get(0).getProductNo());
        }

        String retrieveInvoiceUrl = "";
        if (payInfo != null) {
            retrieveInvoiceUrl = payInfo.getRetrieveInvoiceUrl();
        }

        OrderResponseDto.CustomKitGroup customKitGroup =
                new OrderResponseDto.CustomKitGroup(retrieveInvoiceUrl, orderStatusType, claimStatusType, finalOrderOptionNos, claimNoList, customKitList, nextActionTypes);
        customKitListList.add(customKitGroup);

        for (OrderResponseDto.customKitList customKitItemQueryDto : customKitList) {
            productToOrderCntMap.put(customKitItemQueryDto.getProductNo(), 1);
        }

        getOrderAndCreateOrdersItem(userDetails, orderNo, productNos, productToOrderCntMap, productToOrderOptionMap);

        if (customKitGroup.getCustomKitList().isEmpty()) {
            customKitListList = new ArrayList<>();
        }

        OrderResponseDto.PayInfo responsePayInfo = new OrderResponseDto.PayInfo(orderDetail.getPayInfo(), cancelInfo);
        OrderResponseDto.OrderInfo orderInfo = new OrderResponseDto.OrderInfo(orderDetail.getOrderInfo(), orderType);

        return new OrderDetailResponseDto.OrderDetail(orderInfo, orderDetail, responsePayInfo, sampleKitList, customKitListList);

    }

    private void createOrdersItem(UserDetailsImpl userDetails, String orderNo, List<Integer> productNos, Map<Integer, Integer> productToOrderCntMap, Map<Integer, Integer> productToOrderOptionMap) {
        List<OrdersItemQueryDto> ordersItem = ordersItemRepository.findByOrderNoAndMemberIdAndOrderStatus(orderNo, userDetails.getMember().getId(), OrderStatus.CANCEL_DONE);
        if (ordersItem.isEmpty()) {
            getOrderAndCreateOrdersItem(userDetails, orderNo, productNos, productToOrderCntMap, productToOrderOptionMap);
        }
    }

    private GroupPurchaseQueryDto.MemberProfileQueryDto fetchGroupPurchaseProduct(String orderNo, OrderStatus orderStatusType) {
        if (OrderStatus.CANCEL_DONE.equals(orderStatusType)) {
            return groupPurchaseService.getCancelGroupPurchaseRoomMemberWithOrderNo(orderNo);
        } else {
            return groupPurchaseService.getGroupPurchaseRoomMemberWithOrderNo(orderNo, true);
        }
    }

    private OrderResponseDto.SampleKitGroup handleGroupPurchaseGroup(String orderNo, List<Integer> productNos,
                                                                     Map<Integer, OrderResponseDto.InPayInfoProduct> productPayInfoMap,
                                                                     Map<Integer, Integer> productToOrderOptionMap,
                                                                     List<OrderResponseDto.NextActionDetail> nextActionsForSampleKit,
                                                                     Map<Integer, Integer> productToClaimNoMap) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<Product> productList = productRepository.findByProductNoIn(productNos);
        OrderResponseDto.InPayInfoProduct payInfo = productPayInfoMap.get(productList.get(0).getProductNo());


        List<Integer> orderOptionNos = new ArrayList<>();
        List<Integer> claimNos = new ArrayList<>();
        OrderResponseDto.SampleKitGroup kitGroup = null;
        if (payInfo != null) {

            OrderStatus groupPurchaseType = getGroupPurchaseType(orderNo, formatter);
            if (OrderStatus.CANCEL_DONE.equals(payInfo.getOrderStatusType())) {
                groupPurchaseType = null;
            }

            Integer orderOptionNo = productToOrderOptionMap.get(productList.get(0).getProductNo());
            Integer claimNo = productToClaimNoMap.get(productList.get(0).getProductNo());
            orderOptionNos.add(orderOptionNo);
            claimNos.add(claimNo);
            kitGroup = new OrderResponseDto.SampleKitGroup(
                    productList.get(0).getImgUrl(),
                    productList.get(0).getBrandName(),
                    productList.get(0).getProductName(),
                    productList.get(0).getProductNo(),
                    payInfo.getRetrieveInvoiceUrl(),
                    payInfo.getOrderCnt(),
                    payInfo.getProductStandardPrice(),
                    payInfo.getProductImmediateDiscountedPrice(),
                    payInfo.getOrderStatusType(),
                    groupPurchaseType,
                    payInfo.getClaimStatusType(),
                    orderOptionNos,
                    claimNos,
                    Collections.emptyList(),
                    nextActionsForSampleKit
            );
        }
        return kitGroup;
    }

    private OrderStatus getGroupPurchaseType(String orderNo, DateTimeFormatter formatter) {
        LocalDateTime deadLineTime = getDeadlineTimeForProduct(orderNo, formatter);
        if (deadLineTime.isBefore(LocalDateTime.now())) {
            return OrderStatus.GROUP_PURCHASE_FINISH;
        } else {
            return OrderStatus.GROUP_PURCHASE_READY;
        }
    }

    private void getOrderAndCreateOrdersItem(UserDetailsImpl userDetails, String orderNo, List<Integer> productNos, Map<Integer, Integer> productToOrderCntMap, Map<Integer, Integer> productToOrderOptionMap) {
        Optional<Orders> orders = ordersRepository.findByOrderNoAndMemberIdAndIsMadeOrdersItem(orderNo, userDetails.getMember().getId(), false);
        orders.ifPresent(value -> createOrdersItem(productNos, productToOrderOptionMap, productToOrderCntMap, userDetails.getMember().getId(), value));
    }

    private void createOrdersItem(List<Integer> productNos, Map<Integer, Integer> productToOrderOptionMap,
                                  Map<Integer, Integer> productToOrderCntMap, Long memberId, Orders orders) {

        // Fetch products based on productNos.
        List<Product> products = productRepository.findByProductNoIn(productNos);
        Map<Integer, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getProductNo, Function.identity()));

        // Fetch existing order items for the given order option numbers.
        List<OrdersItem> ordersItemList = ordersItemRepository.findByOrders_Id(orders.getId());

        // TODO: 2024/01/02 orderItem이 안만들어져있으면
        if (ordersItemList.isEmpty()) {
            // TODO: 2024/01/02 과도기 지나면 사라져야하는 로직
            List<OrdersItem> newOrderItems = new ArrayList<>();
            for (Integer productNo : productNos) {
                Product product = productMap.get(productNo);
                if (product != null) {
                    OrdersItem ordersItem = OrdersItem.builder()
                            .product(product)
                            .orders(orders)
                            .orderOptionNo(productToOrderOptionMap.get(productNo))
                            .productCnt(productToOrderCntMap.getOrDefault(productNo, 1))
                            .build();
                    newOrderItems.add(ordersItem);
                }
                ordersItemRepository.saveAll(newOrderItems);
            }
        } else {
            for (OrdersItem ordersItem : ordersItemList) {
                // TODO: 2024/01/02 쿼리 존나 나감;;
                log.info("ordersOptionsNo----update------s");
                int productNo = ordersItem.getProduct().getProductNo();
                Integer orderOptionNo = productToOrderOptionMap.get(productNo);
                if (orderOptionNo != null) {
                    ordersItem.updateOrdersOptionNo(orderOptionNo);
                }
                log.info("ordersOptionsNo----update------e");
            }
        }
        orders.updateIsMadeOrdersItem(true);
    }

    /**
     * 취소 완료된 결과 response 만들어야함
     *
     * @param
     * @author sondong-gyu
     * @version 1.0.0
     * @date 4/2/24
     **/
    @Transactional
    public void cancelOrder(UserDetailsImpl userDetails, OrderRequestDto.CancelOrder cancelOrderDto) throws UnirestException, ParseException {
        Member member = getMember(userDetails);
        String orderNo = cancelOrderDto.getOrderNo();
        sendShopByCancelRequest(member.getShopByAccessToken(), orderNo, cancelOrderDto, true);

        List<OrdersItemQueryDto> ordersItem = getOrdersItem(userDetails, cancelOrderDto);
        log.info("주문 취소 시작_____S");
        updateOrderStatus(ordersItem, userDetails);

        // TODO: 2/20/24 팀 구매 취소하면서 방에서 빼기
        updateGroupPurchasePayment(orderNo);

        // TODO: 2023/10/04 취소 상품 다시 장바구니에 담기
        if (cancelOrderDto.getIsRestoreCart()) {
            restoreItemsToCart(userDetails, ordersItem);
        }
        log.info("주문 취소 종료_____E");
    }

    private void updateGroupPurchasePayment(String orderNo) {
        GroupPurchaseQueryDto.MemberProfileQueryDto roomMemberWithOrderNo = groupPurchaseService.getGroupPurchaseRoomMemberWithOrderNo(orderNo, true);
        // TODO: 2/23/24 주문 취소하면 GroupPurchaseRoomMember를 삭제하는게 맞음
        if (roomMemberWithOrderNo != null) {
            Optional<GroupPurchaseRoomMember> groupPurchaseRoomMember =
                    groupPurchaseService.getGroupPurchaseRoomMember(roomMemberWithOrderNo.getRoomId(), roomMemberWithOrderNo.getMemberId());
            groupPurchaseRoomMember.ifPresent(purchaseRoomMember -> purchaseRoomMember.updatePaymentFinish(false));
        }
    }

    @Transactional
    public void cancelPartialOrder(UserDetailsImpl userDetails, OrderRequestDto.CancelOrder cancelOrderDto, String orderNo) throws UnirestException, ParseException {

        if (!ordersItemRepository.existsByOrderNo(orderNo, userDetails.getMember().getId())) {
            this.getOrderDetail(userDetails, orderNo);
        }

        List<OrdersItemQueryDto> ordersItem = getOrdersItem(userDetails, cancelOrderDto);

        log.info("주문 취소 시작_____S");
        notifyShopbyForCancellation(userDetails, cancelOrderDto, ordersItem);

        updateOrderStatus(ordersItem, userDetails);

        if (cancelOrderDto.getIsRestoreCart()) {
            restoreItemsToCart(userDetails, ordersItem);
        }

        log.info("주문 취소 종료_____E");
    }

    private List<OrdersItemQueryDto> getOrdersItem(UserDetailsImpl userDetails, OrderRequestDto.CancelOrder cancelOrderDto) {

        // Orders 조회 로직 개선
        Optional<Orders> ordersOptional = ordersRepository.findByOrderNoAndMemberId(cancelOrderDto.getOrderNo(), userDetails.getMember().getId());
        if (ordersOptional.isEmpty()) {
            // 적절한 예외 처리 또는 반환값 처리
            return Collections.emptyList();
        }

        // OrdersItem 조회 로직
        return ordersItemRepository.findAllOrdersItem(ordersOptional.get().getId(), userDetails.getMember().getId());
    }

    private List<OrdersItemQueryDto> getOrdersItem(UserDetailsImpl userDetails, String orderNo) {

        // Orders 조회 로직 개선
        Optional<Orders> ordersOptional = ordersRepository.findByOrderNoAndMemberId(orderNo, userDetails.getMember().getId());
        if (ordersOptional.isEmpty()) {
            // 적절한 예외 처리 또는 반환값 처리
            return Collections.emptyList();
        }

        // OrdersItem 조회 로직
        return ordersItemRepository.findAllOrdersItem(ordersOptional.get().getId(), userDetails.getMember().getId());
    }

    private void notifyShopbyForCancellation(UserDetailsImpl userDetails, OrderRequestDto.CancelOrder cancelOrderDto, List<OrdersItemQueryDto> ordersItem) throws UnirestException, ParseException {
        List<OrderRequestDto.ClaimedProductOptions> claimedProductOptionsList = ordersItem.stream().map(ordersItemQueryDto ->
                        new OrderRequestDto.ClaimedProductOptions(ordersItemQueryDto.getOrderOptionNo(), ordersItemQueryDto.getProductCnt()))
                .collect(Collectors.toList());

        shopbyCancelOrders(userDetails.getMember().getShopByAccessToken(), cancelOrderDto, claimedProductOptionsList);
    }

    private void updateOrderStatus(List<OrdersItemQueryDto> ordersItem, UserDetailsImpl userDetails) {
        Optional<Orders> optionalOrders = ordersRepository.findByOrderNoAndMemberId(ordersItem.get(0).getOrderNo(), userDetails.getMember().getId());
        optionalOrders.ifPresent(orders -> orders.updateOrderStatus(OrderStatus.CANCEL_DONE));
    }

    private void restoreItemsToCart(UserDetailsImpl userDetails, List<OrdersItemQueryDto> ordersItem) {
        // TODO: 4/1/24 지금 다시 담으면 orderCnt가 1로 고정이에요
        if (ordersItem.isEmpty()) return;

        List<Integer> productNos = ordersItem.stream()
                .map(OrdersItemQueryDto::getProductNo)
                .distinct()  // Remove duplicates to reduce database load
                .collect(Collectors.toList());

        if (productNos.isEmpty()) return;  // Early exit if no product numbers are found

        List<Product> products = productRepository.findByProductNoIn(productNos);

        clearExistingCart(userDetails);

        Map<Integer, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getProductNo, Function.identity()));

        Map<Long, Integer> ordersItemMap = ordersItem.stream()
                .collect(Collectors.toMap(OrdersItemQueryDto::getProductId, OrdersItemQueryDto::getProductCnt));

        List<Cart> cartList = new ArrayList<>(products.size());
        List<CartItem> cartItemList = new ArrayList<>(products.size());

        for (Product product : products) {
            boolean isCustomKit = CategoryType.SAMPLE.equals(product.getCategory().getCategoryDepth1());
            int productCnt = ordersItemMap.getOrDefault(product.getId(), 0);

            Cart newCart = createNewCart(userDetails.getMember(), isCustomKit);
            CartItem cartItem = createCartItem(newCart, product, productCnt);

            cartList.add(newCart);
            cartItemList.add(cartItem);
        }

        cartRepository.saveAll(cartList);
        cartItemRepository.saveAll(cartItemList);
    }


    private List<OrdersItemQueryDto> filterOrdersByProductNo(List<OrdersItemQueryDto> ordersItemList, int productNo) {
        return ordersItemList.stream()
                .filter(item -> item.getProductNo() == productNo)
                .collect(Collectors.toList());
    }

    /**
     * 장바구니 가져오기
     * 샵바이 version 1.0.0
     **/
    private List<CartResponseDto.OrderProductOption> shopbyGetCartNo(String shopByAccessToken) throws UnirestException, ParseException {
        HttpResponse<String> response = Unirest.get(shopByUrl + "/cart")
                .header("accept", acceptHeader)
                .header("version", versionHeader)
                .header("clientid", clientId)
                .header("platform", platformHeader)
                .header("accesstoken", shopByAccessToken)
                .header("content-type", acceptHeader)
                .asString();

        log.info("장바구니 조회_____________________________");
        ShopBy.errorMessage(response);
        log.info("장바구니 조회_____________________________");

        List<CartResponseDto.OrderProductOption> orderProductOptionList = new ArrayList<>();
        JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);

        JsonArray deliveryArrays = jsonObject.getAsJsonObject().getAsJsonArray("deliveryGroups");
        if (deliveryArrays.size() == 0) {
            return orderProductOptionList;
        }

        JsonElement deliveryElement = jsonObject.getAsJsonObject().getAsJsonArray("deliveryGroups").get(0);
        JsonArray orderProducts = deliveryElement.getAsJsonObject().getAsJsonArray("orderProducts");

        for (JsonElement orderProduct : orderProducts) {
            JsonObject orderProductObject = orderProduct.getAsJsonObject();
            JsonArray orderProductOptions = orderProductObject.getAsJsonArray("orderProductOptions");
            CartResponseDto.OrderProductOption optionDto = null;
            for (JsonElement orderProductOption : orderProductOptions) {
                JsonObject orderProductOptionObject = orderProductOption.getAsJsonObject();
                int productOptionNo = orderProductOptionObject.get("optionNo").getAsInt();
                int cartNo = orderProductOptionObject.get("cartNo").getAsInt();
                int stockCnt = orderProductOptionObject.get("stockCnt").getAsInt();
                int orderCnt = orderProductOptionObject.get("orderCnt").getAsInt();

                optionDto = new CartResponseDto.OrderProductOption(productOptionNo, cartNo, stockCnt, orderCnt);
                orderProductOptionList.add(optionDto);
            }
        }
        return orderProductOptionList;
    }

    private void clearExistingCart(UserDetailsImpl userDetails) {
        cartItemRepository.deleteByMemberId(userDetails.getMember().getId());
        cartRepository.deleteByMemberId(userDetails.getMember().getId());
    }

    /**
     * 장바구니 등록하기
     * 샵바이 version 1.0.0
     **/
    private void shopbyAddToCart(CartRequestDto.AddToCart dto, String shopByAccessToken) throws UnirestException, ParseException {

        List<CartRequestDto.AddToCart> convertedList = new ArrayList<>();
        convertedList.add(dto);

        HttpResponse<String> response = Unirest.post(shopByUrl + "/cart")
                .header("accept", acceptHeader)
                .header("version", versionHeader)
                .header("clientid", clientId)
                .header("platform", platformHeader)
                .header("accesstoken", shopByAccessToken)
                .header("content-type", acceptHeader)
                .body(gson.toJson(convertedList))
                .asString();

        log.info(response.getBody());
        ShopBy.errorMessage(response);
        log.info("장바구니 담기_____________________________E");
    }

    private Cart createNewCart(Member member, boolean isCustomKit) {
        return Cart.builder()
                .member(member)
                .cartNo(0)
                .isCustomKit(isCustomKit)
                .build();
    }

    private CartItem createCartItem(Cart cart, Product product, int orderCnt) {
        return CartItem.builder()
                .cart(cart)
                .product(product)
                .productCount(orderCnt)
                .productOptionNumber(product.getProductOptionsNo())
                .build();
    }

    private void sendShopByCancelRequest(String shopByAccessToken, String endpoint, OrderRequestDto.CancelOrder cancelOrderDto, boolean isTotal) throws ParseException, UnirestException {
        JSONObject json = createCancelOrderJson(cancelOrderDto);
        if (!isTotal) {
            ordersUrl = "/order-options";
        }
        HttpResponse<String> response = Unirest.post(shopByUrl + profile + ordersUrl + "/" + endpoint + "/claims/cancel")
                .header("accept", acceptHeader)
                .header("version", versionHeader)
                .header("clientid", clientId)
                .header("accesstoken", shopByAccessToken)
                .header("platform", platformHeader)
                .header("content-type", acceptHeader)
                .body(gson.toJson(json))
                .asString();

        log.info("주문 취소 _____________________________");

        if (response.getStatus() != 204) {
            ShopBy.errorMessage(response);
        }
    }

    public OrderPaymentPriceResponseDto.PaymentInfo calculateAllPaymentPrice(UserDetailsImpl userDetails, OrderRequestDto.CreateOrder createOrder, String orderSheetNo) throws ParseException, UnirestException {
        return calculatePayment(userDetails, createOrder.getProducts(), null, null, orderSheetNo);
    }

    /**
     * cartService 에서 주문 금액관련 계산 api 요청
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/10/19
     **/
    public OrderPaymentPriceResponseDto.PaymentInfo calculateAllPaymentPrice(UserDetailsImpl userDetails, CartResponseDto.CreateOrder createOrder, String orderSheetNo) throws UnirestException, ParseException {
        return calculatePayment(userDetails, createOrder.getProducts(), null, null, orderSheetNo);
    }

    public HashMap<String, Object> calculateAllPaymentPrice(UserDetailsImpl userDetails, OrderRequestDto.CalculateOrder createOrder, String orderSheetNo) throws ParseException, UnirestException {
        OrderPaymentPriceResponseDto.PaymentInfo paymentInfo;

        if (createOrder.getCouponInfo() != null) {
            if (createOrder.getCouponInfo().getProductCoupons() != null) {
                log.info("---> " + createOrder.getCouponInfo().getProductCoupons().get(0).getProductNo());
                log.info("---> " + createOrder.getCouponInfo().getProductCoupons().get(0).getCouponIssueNo());
            }
            log.info("---> " + createOrder.getCouponInfo().getCartCouponIssueNo());
        }

        if (createOrder.getProducts().isEmpty()) {
            paymentInfo = new OrderPaymentPriceResponseDto.PaymentInfo(); // 빈 경우 초기화
        } else {
            boolean hasOrderOptionNoZero = createOrder.getProducts().stream().anyMatch(product -> product.getOptionNo() == 0);
            log.info("---> " + hasOrderOptionNoZero);
            Map<Integer, Integer> productOrderCntMap = createOrder.getProducts().stream()
                    .collect(Collectors.toMap(OrderRequestDto.Product::getProductNo, OrderRequestDto.Product::getOrderCnt));

            if (hasOrderOptionNoZero) {
                List<Integer> productNoList = createOrder.getProducts().stream()
                        .map(OrderRequestDto.Product::getProductNo)
                        .collect(Collectors.toList());

                List<Product> products = productRepository.findByProductNoIn(productNoList);
                // createOrder.getCouponInfo() null 가능성이 있기 때문에 사용하는 곳에서 null check 필수
                paymentInfo = calculatePaymentDirectOrder(userDetails, products, createOrder.getDeliveryLocation(), createOrder.getCouponInfo(), productOrderCntMap, orderSheetNo);
            } else {
                paymentInfo = calculatePayment(userDetails, createOrder.getProducts(), createOrder.getDeliveryLocation(), createOrder.getCouponInfo(), orderSheetNo);
            }
        }

        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("paymentInfo", paymentInfo);
        resultMap.put("orderSheetNo", orderSheetNo);

        return resultMap;
    }

    private OrderPaymentPriceResponseDto.PaymentInfo calculatePaymentDirectOrder(UserDetailsImpl userDetails, List<Product> products
            , OrderRequestDto.CreateDeliveryLocation deliveryLocation, CouponRequestDto.CouponCalculate couponInfo, Map<Integer, Integer> productOrderCntMap, String orderSheetNo) throws UnirestException, ParseException {
        List<OrderCalculatePaymentPriceRequestDto.PayProductParams> payProductParamsList = products.stream()
                .map(product -> new OrderCalculatePaymentPriceRequestDto.PayProductParams(
                        product.getProductNo(),
                        product.getProductOptionsNo(),
                        productOrderCntMap.get(product.getProductNo())))
                .collect(Collectors.toList());

        return getCalculatePaymentInfo(userDetails, orderSheetNo, deliveryLocation, couponInfo, payProductParamsList);
    }

    public OrderPaymentPriceResponseDto.PaymentInfo calculateAllPaymentPrice(UserDetailsImpl userDetails
            , CustomKitRequestDto.CreateOrder createOrder, String orderSheetNo) throws ParseException, UnirestException {
        return calculatePayment(userDetails, createOrder.getProducts(), null, null, orderSheetNo);
    }

    public OrderCancelResponseDto.CalculateCancelOrder calculateCancelOrder(UserDetailsImpl userDetails, OrderRequestDto.CancelOrder cancelOrderDto) throws UnirestException, ParseException {
        List<OrderRequestDto.ClaimedProductOptions> claimedProductOptionsList = getClaimedProductOptions(userDetails, cancelOrderDto);
        return shopbyCalculateCancelOrder(userDetails.getMember().getShopByAccessToken(), claimedProductOptionsList, cancelOrderDto);
    }

    public OrderCancelResponseDto.CalculateCancelOrder calculateCancelOrder(UserDetailsImpl userDetails, List<Integer> orderOptionNos, String orderNo) throws UnirestException, ParseException {
        List<OrderRequestDto.ClaimedProductOptions> claimedProductOptionsList = getClaimedProductOptions(userDetails, orderOptionNos, orderNo);
        return orderShopByService.shopbyCalculateCancelOrder(userDetails.getMember().getShopByAccessToken(), claimedProductOptionsList);
    }

    private String checkEndDateDifference(String useEndYmdt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime endDateTime = LocalDateTime.parse(useEndYmdt, formatter);
        LocalDateTime currentDateTime = LocalDateTime.now();

        Duration duration = Duration.between(currentDateTime, endDateTime);
        long diffInDays = duration.toDays();

        if (diffInDays < 1) {
            return "1일 미만 ";
        } else {
            return "D-" + diffInDays;
        }
    }

    private List<OrderRequestDto.ClaimedProductOptions> getClaimedProductOptions(UserDetailsImpl userDetails, List<Integer> orderOptionNos, String orderNo) {

        // TODO: 2023/10/16 orderNo로 customKit에서 productId 가져오고 그걸로 ordersItem에서 찾아도 될듯
        List<OrdersItemQueryDto> ordersItem = getOrdersItem(userDetails, orderNo);

        List<OrderRequestDto.ClaimedProductOptions> claimedProductOptionsList = new ArrayList<>();

        for (OrdersItemQueryDto ordersItemQueryDto : ordersItem) {
            OrderRequestDto.ClaimedProductOptions claimedProductOptions =
                    new OrderRequestDto.ClaimedProductOptions(ordersItemQueryDto.getOrderOptionNo(), ordersItemQueryDto.getProductCnt());

            claimedProductOptionsList.add(claimedProductOptions);
        }
        return claimedProductOptionsList;
    }

    private List<OrderRequestDto.ClaimedProductOptions> getClaimedProductOptions(UserDetailsImpl userDetails, OrderRequestDto.CancelOrder cancelOrderDto) {

        // TODO: 2023/10/16 orderNo로 customKit에서 productId 가져오고 그걸로 ordersItem에서 찾아도 될듯
        List<OrdersItemQueryDto> ordersItem = getOrdersItem(userDetails, cancelOrderDto);

        List<OrderRequestDto.ClaimedProductOptions> claimedProductOptionsList = new ArrayList<>();

        for (OrdersItemQueryDto ordersItemQueryDto : ordersItem) {
            OrderRequestDto.ClaimedProductOptions claimedProductOptions =
                    new OrderRequestDto.ClaimedProductOptions(ordersItemQueryDto.getOrderOptionNo(), ordersItemQueryDto.getProductCnt());

            claimedProductOptionsList.add(claimedProductOptions);
        }
        return claimedProductOptionsList;
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

    private OrderCancelResponseDto.CalculateCancelOrder shopbyCalculateCancelOrder(String shopByAccessToken, List<OrderRequestDto.ClaimedProductOptions> claimedProductOptionsList, OrderRequestDto.CancelOrder cancelOrderDto) throws UnirestException, ParseException {
        JSONObject json = createCancelOrderJson(cancelOrderDto);
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

    /**
     * 메소드의 설명을 여기에 작성한다.
     * 주문 취소 페이진 진입 - 상품정보 불러오기
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/10/05
     **/
    @Transactional
    public OrderNewResponseDto getCancelOrderProduct(UserDetailsImpl userDetails, OrderRequestDto.CancelOrderProductInfo cancelOrderProductInfo) throws UnirestException, ParseException {
        OrderDetailResponseDto.OrderDetail orderDetail = getOrderDetail(userDetails, cancelOrderProductInfo.getOrderNo());
        List<OrderNewResponseDto.ItemGroup> itemGroups = new ArrayList<>();

        // CustomKitGroup 처리
        handleCustomKitGroup(orderDetail, itemGroups);


        // SampleKitGroups 처리
        orderDetail.getSampleKitGroup().forEach(sampleKitGroup -> {
            List<OrderCancelResponseDto.ProductList> productList = null;
            if (sampleKitGroup.getSampleList() != null) {
                productList = sampleKitGroup.getSampleList().stream()
                        .map(sample -> new OrderCancelResponseDto.ProductList(
                                sample.getProductNo(),
                                sample.getProductName(),
                                sample.getBrandName(),
                                sample.getImageUrl()
                        )).collect(Collectors.toList());
            }
            OrderNewResponseDto.ItemGroup itemGroup = new OrderNewResponseDto.ItemGroup(
                    false,
                    sampleKitGroup.getSampleKitImage(),
                    sampleKitGroup.getSampleKitName(),
                    sampleKitGroup.getSampleKitBrandName(),
                    sampleKitGroup.getSampleKitOrderCnt(),
                    sampleKitGroup.getPrice(),
                    sampleKitGroup.getImmediatePrice(),
                    productList
            );
            itemGroups.add(itemGroup);
        });

        return new OrderNewResponseDto(itemGroups);
    }

    /**
     * 주문 취소 할꺼니깐 상품 조회
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 4/2/24
     **/
    public OrderCancelResponseDto.OrderCancelInfo getCancelOrderInfo(UserDetailsImpl userDetails, String orderNo) throws UnirestException, ParseException {
        OrderDetailResponseDto.OrderDetail newOrderDetail = getNewOrderDetail(userDetails, orderNo);
        OrderCancelResponseDto.CalculateCancelOrder calculateCancelOrder = calculateCancelOrder(userDetails, newOrderDetail.getOrderOptionNos(), orderNo);
        OrderType orderType = newOrderDetail.getOrderInfo().getOrderType();
        return new OrderCancelResponseDto.OrderCancelInfo(orderType, newOrderDetail.getOrderDetailSection(), newOrderDetail.getOrderOptionNos(), calculateCancelOrder);
    }

    private void handleCustomKitGroup(OrderDetailResponseDto.OrderDetail orderDetail, List<OrderNewResponseDto.ItemGroup> itemGroups) {
        if (orderDetail.getCustomKitGroup().isEmpty()) {
            return;
        }

        OrderResponseDto.CustomKitGroup customKitList = orderDetail.getCustomKitGroup().get(0);

        if (customKitList == null) return;

        int sampleKitPrice = 0;
        int sampleKitImmediatePrice = 0;
        List<OrderCancelResponseDto.ProductList> customKitProductList = new ArrayList<>();
        boolean isCustomKit = true;
        if (OrderType.GROUP_PURCHASE.equals(orderDetail.getOrderInfo().getOrderType())) {
            isCustomKit = false;
        }

        for (OrderResponseDto.customKitList kitList : customKitList.getCustomKitList()) {
            customKitProductList.add(new OrderCancelResponseDto.ProductList(
                    kitList.getProductNo(),
                    kitList.getProductName(),
                    kitList.getBrandName(),
                    kitList.getImageUrl(),
                    kitList.getPrice(),
                    kitList.getImmediatePrice()
            ));

            sampleKitPrice += kitList.getPrice();
            sampleKitImmediatePrice += kitList.getImmediatePrice();
        }

        itemGroups.add(new OrderNewResponseDto.ItemGroup(
                isCustomKit, sampleKitImg,
                "나만의 키트", "나만의 키트",
                1,
                sampleKitPrice, sampleKitImmediatePrice,
                customKitProductList
        ));
    }


    private OrderPaymentPriceResponseDto.PaymentInfo calculatePayment(UserDetailsImpl userDetails, List<? extends IProduct> products
            , OrderRequestDto.CreateDeliveryLocation deliveryLocation, CouponRequestDto.CouponCalculate couponInfo, String orderSheetNo) throws ParseException, UnirestException {
        List<OrderCalculatePaymentPriceRequestDto.PayProductParams> payProductParamsList = products.stream()
                .map(product -> new OrderCalculatePaymentPriceRequestDto.PayProductParams(
                        product.getProductNo(),
                        product.getOptionNo(),
                        product.getOrderCnt()))
                .collect(Collectors.toList());
        return getCalculatePaymentInfo(userDetails, orderSheetNo, deliveryLocation, couponInfo, payProductParamsList);
    }

    private OrderPaymentPriceResponseDto.PaymentInfo getCalculatePaymentInfo(UserDetailsImpl userDetails, String orderSheetNo
            , OrderRequestDto.CreateDeliveryLocation deliveryLocation, CouponRequestDto.CouponCalculate couponInfo, List<OrderCalculatePaymentPriceRequestDto.PayProductParams> payProductParamsList) throws ParseException, UnirestException {
        Optional<DeliveryLocation> defaultDeliveryLocationOpt = findMemberDefaultDeliveryLocation(userDetails.getMember());
        OrderCalculatePaymentPriceRequestDto.ShippingAddresses shippingAddresses = createShippingAddresses(deliveryLocation, defaultDeliveryLocationOpt, payProductParamsList);
        List<OrderCalculatePaymentPriceRequestDto.ShippingAddresses> shippingAddressesList = Collections.singletonList(shippingAddresses);

        return shopbyCalculateAllPaymentPrice(orderSheetNo, shippingAddressesList, couponInfo, userDetails.getMember().getShopByAccessToken());
    }

    private OrderCalculatePaymentPriceRequestDto.ShippingAddresses createShippingAddresses(OrderRequestDto.CreateDeliveryLocation deliveryLocation
            , Optional<DeliveryLocation> defaultDeliveryLocationOpt, List<OrderCalculatePaymentPriceRequestDto.PayProductParams> payProductParamsList) {
        if (deliveryLocation != null) {
            return new OrderCalculatePaymentPriceRequestDto.ShippingAddresses(true,
                    new OrderCalculatePaymentPriceRequestDto.ShippingAddress(deliveryLocation.getReceiverName(),
                            CustomValue.defaultCountryCd,
                            deliveryLocation.getReceiverZipCode(), deliveryLocation.getReceiverAddress(),
                            deliveryLocation.getReceiverDetailAddress(), deliveryLocation.getReceiverJibunAddress(),
                            deliveryLocation.getReceiverContact()), payProductParamsList);
        } else if (defaultDeliveryLocationOpt.isPresent()) {
            DeliveryLocation defaultDeliveryLocation = defaultDeliveryLocationOpt.get();
            return new OrderCalculatePaymentPriceRequestDto.ShippingAddresses(true,
                    new OrderCalculatePaymentPriceRequestDto.ShippingAddress(defaultDeliveryLocation.getReceiverName(),
                            CustomValue.defaultCountryCd,
                            defaultDeliveryLocation.getReceiverZipCode(), defaultDeliveryLocation.getReceiverAddress(),
                            defaultDeliveryLocation.getReceiverDetailAddress(), defaultDeliveryLocation.getReceiverJibunAddress(),
                            defaultDeliveryLocation.getReceiverContact()), payProductParamsList);
        } else {
            return new OrderCalculatePaymentPriceRequestDto.ShippingAddresses(false,
                    new OrderCalculatePaymentPriceRequestDto.ShippingAddress(CustomValue.defaultReceiverName,
                            CustomValue.defaultCountryCd,
                            CustomValue.defaultDeliveryZipCode, CustomValue.defaultDeliveryAddress,
                            CustomValue.defaultDeliveryAddressDetail, CustomValue.defaultDeliveryJibunAddress,
                            null), payProductParamsList);
        }
    }

    /**
     * 쿠폰 빛 배송지 정보가 적용된 금액 조회하기
     * /order-sheets/{orderSheetNo}/calculate
     *
     * @param
     * @param orderSheetNo
     * @param shippingAddresses
     * @param couponInfo
     * @param shopByAccessToken
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/09/05
     **/
    private OrderPaymentPriceResponseDto.PaymentInfo shopbyCalculateAllPaymentPrice(String orderSheetNo, List<OrderCalculatePaymentPriceRequestDto.ShippingAddresses> shippingAddresses,
                                                                                    CouponRequestDto.CouponCalculate couponInfo, String shopByAccessToken) throws ParseException, UnirestException {

        JSONObject json = new JSONObject();
        json.put("shippingAddresses", shippingAddresses);
        if (couponInfo != null) {
            json.put("couponRequest", couponInfo);
        }

        HttpResponse<String> response = Unirest.post(shopByUrl + "/order-sheets/" + orderSheetNo + "/calculate")
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
        JsonObject paymentInfoJson = jsonObject.getAsJsonObject("paymentInfo");

        int cartAmt = paymentInfoJson.get("cartAmt").getAsInt();
        int cartCouponAmt = paymentInfoJson.get("cartCouponAmt").getAsInt();
        int deliveryCouponAmt = paymentInfoJson.get("deliveryCouponAmt").getAsInt();
        int paymentAmt = paymentInfoJson.get("paymentAmt").getAsInt();// 즉시할인 된 결제 총액 (결제 예상금액)
        int productAmt = paymentInfoJson.get("productAmt").getAsInt(); // 즉시할인된 상품들 합한 금액
        int productCouponAmt = paymentInfoJson.get("productCouponAmt").getAsInt(); //
        int totalAdditionalDiscountAmt = paymentInfoJson.get("totalAdditionalDiscountAmt").getAsInt();
        int totalImmediateDiscountAmt = paymentInfoJson.get("totalImmediateDiscountAmt").getAsInt(); // 즉시 할인 금액
        int totalStandardAmt = paymentInfoJson.get("totalStandardAmt").getAsInt();// 즉시할인 안된 모든 상품 금액

        //int accumulationAmt = paymentInfoJson.get("accumulationAmt").isJsonNull() ? 0 : paymentInfoJson.get("accumulationAmt").getAsInt();// 보유 적립금
        //int availableMaxAccumulationAmt = paymentInfoJson.get("availableMaxAccumulationAmt").isJsonNull() ? 0 : paymentInfoJson.get("availableMaxAccumulationAmt").getAsInt(); // 사용가능 적립금

        JsonArray deliveryGroupsArray = jsonObject.getAsJsonArray("deliveryGroups");
        int deliveryAmt = 0;
        int aboveDeliveryAmt = 0;
        int baseDeliveryAmt = 0;
        int remoteDeliveryAmt = 0;
        for (JsonElement deliveryGroupElement : deliveryGroupsArray) {
            JsonObject deliveryGroup = deliveryGroupElement.getAsJsonObject();
            JsonObject deliveryCondition = deliveryGroup.getAsJsonObject("deliveryCondition");
            deliveryAmt = deliveryCondition.get("deliveryAmt").getAsInt();
            aboveDeliveryAmt = deliveryCondition.get("aboveDeliveryAmt").getAsInt();
            baseDeliveryAmt = deliveryCondition.get("baseDeliveryAmt").getAsInt();
            remoteDeliveryAmt = deliveryCondition.get("remoteDeliveryAmt").getAsInt();
        }

        OrderPaymentPriceResponseDto.DeliveryCondition deliveryCondition =
                new OrderPaymentPriceResponseDto.DeliveryCondition(deliveryAmt, aboveDeliveryAmt, baseDeliveryAmt, remoteDeliveryAmt);

        return new OrderPaymentPriceResponseDto.PaymentInfo(
                deliveryCondition, cartAmt, cartCouponAmt, deliveryCouponAmt,
                paymentAmt, productAmt, productCouponAmt,
                totalAdditionalDiscountAmt, totalImmediateDiscountAmt, totalStandardAmt
        );
    }

    private OrderResponseDto.CancelInfo shopbyGetOrderCancelDetail(String shopByAccessToken, Integer claimNo) throws UnirestException, ParseException {
        ///profile/claims/{claimNo}/result
        HttpResponse<String> response = Unirest.get(shopByUrl + profile + "/claims/" + claimNo + "/result")
                .queryString("claimType", "CANCEL_DONE")
                .header("accept", acceptHeader)
                .header("version", versionHeader)
                .header("clientid", clientId)
                .header("accesstoken", shopByAccessToken)
                .header("platform", platformHeader)
                .header("content-type", acceptHeader)
                .asString();

        log.info("주문 취소 상세 조회_____________________________");
        ShopBy.errorMessage(response);
        log.info("주문 취소 상세 조회_____________________________");

        JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
        JsonObject claimPriceInfoJson = jsonObject.getAsJsonObject("claimPriceInfo");
        int refundMainPayAmt = claimPriceInfoJson.get("refundMainPayAmt").getAsInt();

        String claimReasonDetail = jsonObject.get("claimReasonDetail").getAsString();
        String claimReasonType = ""; // 초기화
        String additionalText = ""; // 초기화

        String patternString = "(상품이 품절되었어요|상품이 마음에 들지 않아요|배송지를 변경하고 싶어요|서비스가 불만족스러워요|주문을 잘못했어요)(.*)";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(claimReasonDetail);

        if (matcher.find()) {
            claimReasonType = matcher.group(1); // 첫 번째 그룹: 고정된 사유
            additionalText = matcher.group(2).trim(); // 두 번째 그룹: 사유 뒤의 추가적인 문자열
        }

        // CancelInfo 객체 생성 및 반환
        return new OrderResponseDto.CancelInfo(claimReasonType, additionalText, refundMainPayAmt);
    }

    private OrderResponseDto.OrderDetail shopbyGetOrderDetail(String shopByAccessToken, String orderNo, boolean isDetail) throws
            UnirestException, ParseException {
        HttpResponse<String> response = Unirest.get(shopByUrl + profile + ordersUrl + "/" + orderNo)
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

        List<Integer> productNos = new ArrayList<>();
        List<Integer> orderOptionNos = new ArrayList<>();
        List<Integer> claimNos = new ArrayList<>();
        List<OrderResponseDto.NextActionDetail> nextActions = new ArrayList<>();
        List<OrderResponseDto.InPayInfoProduct> inPayInfoProducts = new ArrayList<>();

        JsonElement partnerElement = jsonObject.getAsJsonArray("orderOptionsGroupByPartner").get(0); /// 묶음 배송 옵션인거같은데...
        JsonArray deliveryArray = partnerElement.getAsJsonObject().getAsJsonArray("orderOptionsGroupByDelivery");

        for (JsonElement deliveryArrayElement : deliveryArray) {
            JsonElement deliveryElement = deliveryArrayElement.getAsJsonObject();
            String retrieveInvoiceUrl = deliveryElement.getAsJsonObject().get("retrieveInvoiceUrl").isJsonNull() ? "" :
                    deliveryElement.getAsJsonObject().get("retrieveInvoiceUrl").getAsString();
            JsonArray orderOptionsArray = deliveryElement.getAsJsonObject().getAsJsonArray("orderOptions");

            for (JsonElement orderOptionElement : orderOptionsArray) {
                JsonObject orderOptionObject = orderOptionElement.getAsJsonObject();
                int productNo = orderOptionObject.get("productNo").getAsInt();
                int orderOptionNo = orderOptionObject.get("orderOptionNo").getAsInt();
                String productName = orderOptionObject.get("productName").getAsString();
                String brandName = orderOptionObject.get("brandName").getAsString();
                if (brandName.equals("")) {
                    brandName = orderOptionObject.get("brandNameEn").getAsString();
                }
                String productImgUrl = orderOptionObject.get("imageUrl").getAsString();
                productImgUrl = "https:" + productImgUrl;
                Integer claimNo = orderOptionObject.get("claimNo").isJsonNull() ? null : orderOptionObject.get("claimNo").getAsInt();
                int orderCnt = orderOptionObject.get("orderCnt").getAsInt();
                OrderStatus orderStatusType = OrderStatus.valueOf(orderOptionObject.get("orderStatusType").getAsString());
                String claimStatus = orderOptionObject.get("claimStatusType").isJsonNull() ? null : orderOptionObject.get("claimStatusType").getAsString();
                ClaimStatus claimStatusType = null;
                if (claimStatus != null) {
                    claimStatusType = ClaimStatus.valueOf(claimStatus);
                }

                JsonArray nextActionsArray = orderOptionObject.getAsJsonArray("nextActions");

                JsonObject priceObject = orderOptionObject.getAsJsonObject("price");
                int productStandardPrice = priceObject.get("standardPrice").getAsInt();
                int productImmediateDiscountedPrice = priceObject.get("immediateDiscountedPrice").getAsInt();

                OrderResponseDto.InPayInfoProduct payInfo =
                        new OrderResponseDto.InPayInfoProduct(productName, productImgUrl, productNo, productStandardPrice, productImmediateDiscountedPrice
                                , orderCnt, orderStatusType, claimStatusType, retrieveInvoiceUrl, brandName);

                if (isDetail) {
                    for (JsonElement nextActionsElement : nextActionsArray) {
                        JsonObject nextActionsObject = nextActionsElement.getAsJsonObject();
                        String nextActionType = nextActionsObject.get("nextActionType").getAsString();
                        OrderResponseDto.NextActionDetail nextAction = new OrderResponseDto.NextActionDetail(productNo, nextActionType);
                        nextActions.add(nextAction);
                    }
                }

                inPayInfoProducts.add(payInfo);
                productNos.add(productNo);
                claimNos.add(claimNo);
                orderOptionNos.add(orderOptionNo);
            }
        }

        OrderResponseDto.ShippingAddress shippingAddressDto =
                new OrderResponseDto.ShippingAddress(receiverZipCd, receiverAddress, receiverJibunAddress, receiverDetailAddress
                        , receiverName, receiverContact1, addressName, deliveryMemo);

        List<OrderStatus> orderStatusList = inPayInfoProducts.stream().map(OrderResponseDto.InPayInfoProduct::getOrderStatusType).collect(Collectors.toList());
        OrderStatus orderStatusType = null;

        // Loop through the orderStatusList once
        for (OrderStatus status : orderStatusList) {
            if (status == OrderStatus.PAY_DONE) {
                // If PAY_DONE is found, set orderStatusType to PAY_DONE and exit the loop
                orderStatusType = OrderStatus.PAY_DONE;
                break;
            } else if ((status == OrderStatus.DELIVERY_ING || status == OrderStatus.PRODUCT_PREPARE || status == OrderStatus.DELIVERY_PREPARE) && orderStatusType == null) {
                // If DELIVERY_ING, PRODUCT_PREPARE, or DELIVERY_PREPARE is found first, set orderStatusType to DELIVERY_ING
                orderStatusType = OrderStatus.DELIVERY_ING;
            } else if (status == OrderStatus.BUY_CONFIRM && orderStatusType == null) {
                // If BUY_CONFIRM is found and orderStatusType is still null, set it to BUY_CONFIRM
                orderStatusType = OrderStatus.BUY_CONFIRM;
            }
        }

        // If orderStatusType is still null after the loop, check productPayInfos
        if (orderStatusType == null) {
            orderStatusType = (!inPayInfoProducts.isEmpty() && inPayInfoProducts.get(0).getOrderStatusType() != null)
                    ? inPayInfoProducts.get(0).getOrderStatusType()
                    : OrderStatus.PAY_DONE; // Default to PAY_DONE if productPayInfos is empty or the first item's status is null
        }

        OrderResponseDto.OrderInfo orderInfo = new OrderResponseDto.OrderInfo(orderNo, orderDate, orderStatusType);

        OrderResponseDto.PayInfo payInfo = new OrderResponseDto.PayInfo(totalProductAmt, deliveryAmt, standardAmt, immediateDiscountAmt,
                couponDiscountAmount, pointDiscountAmount, payTypeLabel);

        return new OrderResponseDto.OrderDetail(orderInfo, shippingAddressDto, payInfo, orderOptionNos,
                productNos, claimNos, nextActions, inPayInfoProducts);

    }

    private AddressSearchResponseDto shopbyGetSearchAddress(int pageNumber, int pageSize, String keyword) throws
            UnirestException, ParseException {
        HttpResponse<String> response = Unirest.get(shopByUrl + "/addresses/search")
                .queryString("pageNumber", pageNumber)
                .queryString("pageSize", pageSize)
                .queryString("keyword", keyword)
                .header("accept", acceptHeader)
                .header("version", versionHeader)
                .header("clientid", clientId)
                .header("platform", platformHeader)
                .header("content-type", acceptHeader)
                .asString();

        ShopBy.errorMessage(response);
        log.info("search-keyword -> " + keyword);

        JsonObject resJsonObject = gson.fromJson(response.getBody(), JsonObject.class);
        JsonArray jsonArray = resJsonObject.getAsJsonArray("items");
        int totalCount = resJsonObject.get("totalCount").getAsInt();
        List<AddressSearchResponseDto.AddressSearch> addressSearches = new ArrayList<>();

        for (JsonElement element : jsonArray) {
            JsonObject jsonObject = element.getAsJsonObject();
            String address = jsonObject.get("address").getAsString();
            String roadAddress = jsonObject.get("roadAddress").getAsString();
            String jibunAddress = jsonObject.get("jibunAddress").getAsString();
            String zipCode = jsonObject.get("zipCode").getAsString();

            AddressSearchResponseDto.AddressSearch addressSearch =
                    new AddressSearchResponseDto.AddressSearch(address, roadAddress, jibunAddress, zipCode);

            addressSearches.add(addressSearch);
        }

        return new AddressSearchResponseDto(totalCount, addressSearches);
    }

    private OrderResponseDto.getOrderSheet shopbyGetOrderSheet(String shopByAccessToken, String orderSheetNo) throws UnirestException, ParseException {

        HttpResponse<String> response = Unirest.get(shopByUrl + "/order-sheets" + "/" + orderSheetNo + "?includeMemberAddress=true")
                .header("accept", acceptHeader)
                .header("version", versionHeader)
                .header("clientid", clientId)
                .header("platform", platformHeader)
                .header("accesstoken", shopByAccessToken)
                .header("content-type", acceptHeader)
                .asString();

        ShopBy.errorMessage(response);
        JsonObject resJsonObject = gson.fromJson(response.getBody(), JsonObject.class);

        JsonArray deliveryGroupsJsonArray = resJsonObject.getAsJsonArray("deliveryGroups");
        List<OrderResponseDto.Orderer> ordererList = new ArrayList<>();
        String imageUrl = null;
        for (JsonElement deliveryGroupElement : deliveryGroupsJsonArray) {
            JsonObject deliveryGroupJsonObject = deliveryGroupElement.getAsJsonObject();
            JsonArray orderProducts = deliveryGroupJsonObject.getAsJsonArray("orderProducts");

            OrderResponseDto.Orderer orderer = null;
            for (JsonElement orderProductElement : orderProducts) {
                JsonObject orderProductJsonObject = orderProductElement.getAsJsonObject();
                imageUrl = orderProductJsonObject.get("imageUrl").getAsString();
                imageUrl = "https:" + imageUrl;
                int productNo = orderProductJsonObject.get("productNo").getAsInt();
                String brandName = orderProductJsonObject.get("brandName").getAsString();
                brandName = processBrandName(brandName);
                String productName = orderProductJsonObject.get("productName").getAsString();
                orderer = new OrderResponseDto.Orderer(false, productNo, brandName, productName);
                ordererList.add(orderer);
            }
        }

        return new OrderResponseDto.getOrderSheet(ordererList);
    }

    private String processBrandName(String brandName) {
        int slashIndex = brandName.indexOf('/');
        return slashIndex != -1 ? brandName.substring(0, slashIndex).trim() : brandName.trim();
    }

    /**
     * 주문서 작성하기
     * 샵바이 version 1.0.0
     *
     * @return orderSheetNo - 주문서 번호
     **/
    private String shopbyCreateOrder(String shopByAccessToken, OrderRequestDto.CreateOrder dto) throws UnirestException, ParseException {
        HttpResponse<String> response = Unirest.post(
                        shopByUrl + "/order-sheets")
                .header("accept", acceptHeader)
                .header("version", versionHeader)
                .header("clientid", clientId)
                .header("platform", platformHeader)
                .header("accesstoken", shopByAccessToken)
                .header("content-type", acceptHeader)
                .body(gson.toJson(dto))
                .asString();

        ShopBy.errorMessage(response);

        JsonObject resJsonObject = gson.fromJson(response.getBody(), JsonObject.class);
        return resJsonObject.get("orderSheetNo").getAsString();
    }

    private void shopbyConfirmOrder(String shopByAccessToken, int orderOptionNo) throws
            UnirestException, ParseException {
        HttpResponse<String> response = Unirest.put(shopByUrl + "/profile" + "/order-options" +
                        "/" + orderOptionNo +
                        "/confirm")
                .header("accept", acceptHeader)
                .header("version", versionHeader)
                .header("clientid", clientId)
                .header("platform", platformHeader)
                .header("accesstoken", shopByAccessToken)
                .header("content-type", acceptHeader)
                .asString();

        if (response.getStatus() != 204) {
            ShopBy.errorMessage(response);
        }
    }

    private void shopbyCancelOrders(String shopByAccessToken, OrderRequestDto.CancelOrder cancelOrderDto, List<OrderRequestDto.ClaimedProductOptions> claimedProductOptionsList) throws UnirestException, ParseException {

        JSONObject json = new JSONObject();
        json.put("claimType", "CANCEL");
        json.put("claimReasonType", cancelOrderDto.getClaimReasonType());
        json.put("claimReasonDetail", cancelOrderDto.getClaimReasonDetail());
        json.put("bankAccountInfo", cancelOrderDto.getBankAccountInfo());
        json.put("saveBankAccountInfo", false);
        json.put("responsibleObjectType", null);
        json.put("claimedProductOptions", claimedProductOptionsList);
        json.put("refundsImmediately", true);

        HttpResponse<String> response = Unirest.post(shopByUrl + "/profile/claims/cancel")
                .header("accept", acceptHeader)
                .header("version", versionHeader)
                .header("clientid", clientId)
                .header("platform", platformHeader)
                .header("accesstoken", shopByAccessToken)
                .header("content-type", acceptHeader)
                .body(gson.toJson(json))
                .asString();

        if (response.getStatus() != 204) {
            ShopBy.errorMessage(response);
        }
    }

    public OrderResponseDto.OrderListShopby shopbyGetOrderList(String shopByAccessToken, int pageNumber, int pageSize, OrderStatus orderStatusType) throws UnirestException, ParseException {

        String orderRequestTypes = "";

        if (OrderStatus.PAY_DONE.equals(orderStatusType)) {
            orderRequestTypes = "Pay Done";
        }

        HttpResponse<String> response = Unirest.get(shopByUrl + profile + ordersUrl)
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
        log.info("response-> " + response.getBody());
        log.info("배송/주문 조회_____________________________");

        List<OrderResponseDto.OrderListGroup> shopbyList = new ArrayList<>();
        JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
        JsonArray productsArray = jsonObject.getAsJsonArray("items");
        int totalCount = jsonObject.get("totalCount").getAsInt();

        for (JsonElement itemElement : productsArray) {
            JsonObject itemObject = itemElement.getAsJsonObject();
            List<OrderResponseDto.Products> productsList = new ArrayList<>();
            String orderDate = itemObject.get("orderYmdt").getAsString();
            JsonArray orderProducts = itemObject.getAsJsonArray("orderOptions");
            OrderResponseDto.Products products = null;
            for (JsonElement orderProductElement : orderProducts) {
                String orderNo = itemObject.get("orderNo").getAsString();
                JsonObject orderProductJsonObject = orderProductElement.getAsJsonObject();
                Integer claimNo = orderProductJsonObject.get("claimNo").isJsonNull() ? null : orderProductJsonObject.get("claimNo").getAsInt();
                int orderOptionNo = orderProductJsonObject.get("orderOptionNo").getAsInt();
                int productsNo = orderProductJsonObject.get("productNo").getAsInt();
                String imageUrl = orderProductJsonObject.get("imageUrl").getAsString();
                imageUrl = "https:" + imageUrl;
                String brandName = orderProductJsonObject.get("brandName").getAsString();
                brandName = processBrandName(brandName);
                int orderCount = orderProductJsonObject.get("orderCnt").getAsInt();
                String optionTitle = orderProductJsonObject.get("productName").getAsString();
                OrderStatus orderStatus = OrderStatus.valueOf(orderProductJsonObject.get("orderStatusType").getAsString());
                String claimStatusType = orderProductJsonObject.get("claimStatusType").isJsonNull() ? null : orderProductJsonObject.get("claimStatusType").getAsString();
                ClaimStatus claimStatus = null;
                if (claimStatusType != null) {
                    claimStatus = ClaimStatus.valueOf(claimStatusType);
                }
                String invoiceNo = orderProductJsonObject.getAsJsonObject("delivery").get("invoiceNo").isJsonNull() ? "" :
                        orderProductJsonObject.getAsJsonObject("delivery").get("invoiceNo").getAsString();
                String retrieveInvoiceUrl = orderProductJsonObject.getAsJsonObject("delivery").get("retrieveInvoiceUrl").isJsonNull() ? "" :
                        orderProductJsonObject.getAsJsonObject("delivery").get("retrieveInvoiceUrl").getAsString();
                String deliveryCompanyTypeLabel = orderProductJsonObject.getAsJsonObject("delivery")
                        .get("deliveryCompanyTypeLabel").isJsonNull() ? "" : orderProductJsonObject.getAsJsonObject("delivery").get("deliveryCompanyTypeLabel").getAsString();
                int productPrice = orderProductJsonObject.getAsJsonObject("price").get("buyAmt").getAsInt();
                int salePrice = orderProductJsonObject.getAsJsonObject("price").get("standardAmt").getAsInt();

                JsonArray nextActions = orderProductJsonObject.getAsJsonArray("nextActions");

                // Gson을 사용하여 JsonArray를 List<NextAction>으로 변환
                List<OrderResponseDto.NextAction> nextActionList = new Gson().fromJson(nextActions, new TypeToken<List<OrderResponseDto.NextAction>>() {
                }.getType());

                products = new OrderResponseDto.Products(false,
                        orderNo, orderDate, orderOptionNo, claimNo,
                        productsNo, imageUrl, brandName, orderCount,
                        orderStatus, claimStatus, productPrice, salePrice,
                        optionTitle, invoiceNo, retrieveInvoiceUrl, deliveryCompanyTypeLabel,
                        nextActionList);

                productsList.add(products);
            }
            OrderResponseDto.OrderListGroup orderListGroup =
                    new OrderResponseDto.OrderListGroup(null, null, productsList);
            shopbyList.add(orderListGroup);
        }
        return new OrderResponseDto.OrderListShopby(totalCount, shopbyList);
    }

    /**
     * 첫 구매 대상인지 조회
     *
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/12/18
     **/
    public boolean getFirstPurchase(UserDetailsImpl userDetails) {
        List<OrderStatus> orderStatusList = Arrays.asList(OrderStatus.PAY_DONE, OrderStatus.BUY_CONFIRM);
        return !ordersRepository.existsByMemberIdAndOrderStatusIn(userDetails.getMember().getId(), orderStatusList);
    }

    public void checkZeroPerfumeItems(UserDetailsImpl userDetails, List<Integer> productNos) throws UnirestException, ParseException {
        int[] productNoArray = productNos.stream().mapToInt(Integer::intValue).toArray();
        List<WishListResponseDto.WishListProducts> productsList = productShopByService.shopbyGetProductListByProductNo(productNoArray);

        boolean hasZeroPerfumeItem = productsList.stream()
                .anyMatch(product -> zeroPerfumeCategoryNo.equals(product.getDisplayCategoryNo()));

        if (hasZeroPerfumeItem) {
            eventService.checkedZeroPerfumeProductMinimumAmt(userDetails, productNoArray);
        }
    }

    public void checkEventProductPriceCondition(UserDetailsImpl userDetails, List<Integer> cartProductNos) throws UnirestException, ParseException {
        List<EventProductQueryDto.EventProductInfo> eventProductList = eventService.getEventProduct(EventProductType.ALL);
        List<Integer> firstDealProductNos = eventProductList.stream()
                .filter(product -> product.getEventProductType() == EventProductType.FIRST_DEAL)
                .map(EventProductQueryDto.EventProductInfo::getProductNo)
                .collect(Collectors.toList());

        boolean hasFirstDealProducts = !Collections.disjoint(firstDealProductNos, cartProductNos);
        if (hasFirstDealProducts) {
            // TODO: 2024-01-03 첫구매딜 상품이 장바구니에 있으면 최소 금액 체크
            eventService.checkedFirstDealProductMinimumAmt(userDetails, cartProductNos);
        }

        List<Integer> purchaseConditionProductNos = eventProductList.stream()
                .filter(product -> product.getEventProductType() == EventProductType.PURCHASE_CONDITION)
                .map(EventProductQueryDto.EventProductInfo::getProductNo)
                .collect(Collectors.toList());

        // TODO: 2024-01-03 구매하는데 조건이 달린 상품 조회
        // TODO: 2024-01-03 ex) 틴트 본품 0원 샘플은 1만원 이상 구매 시 선택 가능하도록
        boolean hasProductsConditionProduct = !Collections.disjoint(purchaseConditionProductNos, cartProductNos);
        if (hasProductsConditionProduct) {
            // TODO: 2024-01-03 장바구니에 구매조건 상품이 있는 경우
            eventService.checkedPurchaseConditionProductMinimumAmt(userDetails, cartProductNos);
        }
    }

    private Member getMember(UserDetailsImpl userDetails) {
        return memberRepository.findById(userDetails.getMember().getId())
                .orElseThrow(() -> new ErrorCustomException(ErrorCode.NO_USER_ERROR));
    }

    public Optional<DeliveryLocation> findMemberDefaultDeliveryLocation(Member member) {
        return deliveryLocationRepository.findByMemberAndDefaultAddress(member, true);
    }

    public Page<ZeroExperienceRecommendSurveyQueryDto.OrdersItemInfo> getZeroExperienceItemsFromOrders(int pageNumber, int pageSize, Long memberId) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        return ordersItemRepository.findOrdersItemByMemberIdAndKitCategory(pageable, memberId);
    }

    public OrdersItem getOrdersItem(Long ordersItemId) {
        return ordersItemRepository.findById(ordersItemId)
                .orElseThrow(() -> new ErrorCustomException(ErrorCode.ORDERS_ITEM_NOT_FOUND));
    }

}
