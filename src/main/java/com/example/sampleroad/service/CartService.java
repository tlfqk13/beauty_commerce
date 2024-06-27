package com.example.sampleroad.service;

import com.example.sampleroad.common.exception.ErrorCode;
import com.example.sampleroad.common.exception.ErrorCustomException;
import com.example.sampleroad.common.utils.CustomValue;
import com.example.sampleroad.common.utils.ShopBy;
import com.example.sampleroad.domain.CategoryType;
import com.example.sampleroad.domain.cart.Cart;
import com.example.sampleroad.domain.cart.CartItem;
import com.example.sampleroad.domain.display.DisplayType;
import com.example.sampleroad.domain.home.MoveCase;
import com.example.sampleroad.domain.member.Member;
import com.example.sampleroad.domain.product.EventProductType;
import com.example.sampleroad.domain.product.Product;
import com.example.sampleroad.domain.product.ProductType;
import com.example.sampleroad.domain.search.SearchSortType;
import com.example.sampleroad.dto.request.CartRequestDto;
import com.example.sampleroad.dto.response.BestSellerResponseDto;
import com.example.sampleroad.dto.response.product.*;
import com.example.sampleroad.dto.response.cart.*;
import com.example.sampleroad.dto.response.display.DisplayResponseDto;
import com.example.sampleroad.dto.response.display.DisplaySectionResponseDto;
import com.example.sampleroad.dto.response.order.OrderCalculateCouponResponseDto;
import com.example.sampleroad.dto.response.order.OrderPaymentPriceResponseDto;
import com.example.sampleroad.dto.response.order.OrderResponseDto;
import com.example.sampleroad.dto.response.wishList.WishListResponseDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.example.sampleroad.repository.cart.CartItemRepository;
import com.example.sampleroad.repository.cart.CartRepository;
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

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CartService {
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

    @Value("${shop-by.experience-category-no}")
    int experienceCategoryNo;
    @Value("${shop-by.today-price-category-no}")
    String todayPriceCategoryNo;

    Gson gson = new Gson();

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderService orderService;
    private final EventService eventService;
    private final CouponService couponService;
    private final ProductService productService;
    private final DisplayService displayService;
    private final ZeroExperienceReviewService zeroExperienceReviewService;
    private final NotificationAgreeService notificationAgreeService;
    private final WishListService wishListService;
    private final CustomKitService customKitService;

    /**
     * 장바구니 등록
     * dto의 isCustomKit로 구분해서 등록한다
     * 샘플은 현재 1개씩만 담을 수 있도록 되어 있다.
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/06/28
     **/
    @Transactional
    public HashMap<String, Object> addToCart(CartRequestDto.AddToCart dto, UserDetailsImpl userDetails) throws UnirestException, ParseException {

        HashMap<String, Object> resultMap = new HashMap<>();
        // 상품 검증
        Product product = productService.getProduct(dto.getProductNo());
        boolean firstPurchase = orderService.getFirstPurchase(userDetails);

        // TODO: 1/12/24 첫구매딜 제품 2개 못담게 이걸로
        List<EventProductQueryDto.EventProductInfo> firstDealProductList = eventService.getEventProduct(EventProductType.FIRST_DEAL);
        List<Integer> firstDealProductNos = firstDealProductList.stream().map(EventProductQueryDto.EventProductInfo::getProductNo).collect(Collectors.toList());

        if (!firstPurchase) {
            if (firstDealProductNos.contains(dto.getProductNo())) {
                throw new ErrorCustomException(ErrorCode.ONLY_FIRST_PURCHASE_USER_ITEM);
            }
        }

        // TODO: 3/25/24 장바구니에 팀구매 상품이 담긴 경우
        orderService.validateGroupPurchaseProducts(Collections.singletonList(product.getProductNo()));

        // TODO: 2024/01/02 장바구니에 있는 상품 한번에 다 조회
        List<CartItemQueryDto> findProductByMemberId = cartItemRepository.findProductByMemberId(userDetails.getMember().getId());
        // TODO: 3/25/24 isCustomKit가 false이면 0원 체험 제품
        List<Integer> experienceProductNos = findProductByMemberId.stream().filter(item -> !item.isCustomKit()).map(CartItemQueryDto::getProductNo).collect(Collectors.toList());
        List<Integer> customKitProductNos = findProductByMemberId.stream().filter(CartItemQueryDto::isCustomKit).map(CartItemQueryDto::getProductNo).collect(Collectors.toList());

        // TODO: 2024-01-20 현재 장바구니의 커스텀키트 상품중에 다른 첫구매딜 상품이 있으면
        List<Integer> commonProductNos = customKitProductNos.stream()
                .distinct()
                .filter(firstDealProductNos::contains)
                .collect(Collectors.toList());

        if (firstDealProductNos.contains(dto.getProductNo())) {
            if (!commonProductNos.isEmpty()) {
                throw new ErrorCustomException(ErrorCode.FIRST_DEAL_PRODUCT_LIMIT);
            }
        }

        // TODO: 1/11/24 상품 진열 관리로 오늘의 특가 세팅하기
        checkHoDealItems(product);

        Optional<CartQueryDto> presentCartOpt = cartRepository.findCartByMemberIdAndProductNoAndOptionNo(userDetails.getMember().getId(), dto.getProductNo(), dto.getOptionNo());
        Long cartId;
        if (presentCartOpt.isPresent()) {
            CartQueryDto presentCart = presentCartOpt.get();
            if (dto.getIsCustomKit() && presentCart.getProductNo() == dto.getProductNo()) {
                // Custom kit already in cart
                throw new ErrorCustomException(ErrorCode.ALREADY_INCART_PRODUCT);
            } else if (!dto.getIsCustomKit() && !experienceProductNos.contains(dto.getProductNo())) {
                // Non-custom kit already in cart, update quantity
                cartId = updateToExistingCartItem(dto, presentCart.getCartItemId());
                resultMap.put("cartId", cartId);
            } else if (!dto.getIsCustomKit() && experienceProductNos.contains(dto.getProductNo())) {
                // TODO: 2024/01/02 0원 체험샘플 1개 이상 담으면 막아야함
                if (!presentCart.getIsMultiPurchase()) {
                    throw new ErrorCustomException(ErrorCode.EXPERIENCE_PRODUCT_MAX_COUNT_ZERO);
                }
            }
        } else {
            if (!dto.getIsCustomKit()) {
                // TODO: 2024/01/02 0원 체험샘플 4개 이상 담으면 1회 주문 최대 4개
                if (experienceProductNos.size() >= 4) {
                    throw new ErrorCustomException(ErrorCode.EXPERIENCE_PRODUCT_TOTALLY_COUNT_FOUR);
                } else {
                    cartId = addToCart(dto, userDetails.getMember(), product);
                    resultMap.put("cartId", cartId);
                }
            } else {
                // TODO: 1/11/24 10개 제한을 여기서 ???
                if (customKitProductNos.size() >= 10) {
                    throw new ErrorCustomException(ErrorCode.FULL_INCART_PRODUCT);
                }
                cartId = addToCart(dto, userDetails.getMember(), product);
                resultMap.put("cartId", cartId);
            }
        }
        return resultMap;
    }

    private void checkHoDealItems(Product product) throws UnirestException, ParseException {
        List<DisplaySectionResponseDto.SectionItem> hotDealSectionItems = displayService.getDisplaySectionItems().get(DisplayType.HOT_DEAL);
        for (int i = 1; i < hotDealSectionItems.size(); i++) {
            if (hotDealSectionItems.get(i).getProductNo() == product.getProductNo()) {
                LocalDateTime defaultEndYmdt = hotDealSectionItems.get(0).getSaleEndYmdt();
                LocalDateTime saleEndYmdt = hotDealSectionItems.get(i).getSaleEndYmdt();
                if (saleEndYmdt.isAfter(defaultEndYmdt)) {
                    throw new ErrorCustomException(ErrorCode.IS_BEFORE_WEEKLY_PRICE_PRODUCT);
                }
            }
        }

        // TODO: 4/16/24 카테고리에 오늘의 특가 생성, 오늘의 특가 상품 카테고리 id 수정 필수(운영)
        if (CategoryType.TODAY_SPECIAL_PRICE.equals(product.getCategory().getCategoryDepth2())) {
            throw new ErrorCustomException(ErrorCode.IS_WEEKLY_PRICE_PRODUCT);
        }
    }

    /**
     * 이미 등록된 관리자키트 개수 업데이트
     *
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/06/05
     **/
    private Long updateToExistingCartItem(CartRequestDto.AddToCart dto, Long cartItemId) {

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ErrorCustomException(ErrorCode.CARTITEM_NOT_FOUND));

        int orderCnt = cartItem.getProductCount();

        cartItem.updateCartItemCount(dto.getOrderCnt() + orderCnt);

        return cartItem.getCart().getId();
    }

    private Long addToCart(CartRequestDto.AddToCart dto, Member member, Product product) {
        Cart cart = createNewCart(member, dto.getIsCustomKit());
        cartRepository.save(cart);
        CartItem cartItem = createCartItem(cart, product, dto);
        cartItemRepository.save(cartItem);
        return cart.getId();
    }

    private CartItem createCartItem(Cart cart, Product product, CartRequestDto.AddToCart dto) {
        return CartItem.builder()
                .cart(cart)
                .product(product)
                .productCount(dto.getOrderCnt())
                .productOptionNumber(product.getProductOptionsNo())
                .build();
    }

    private Cart createNewCart(Member member, boolean isCustomKit) {
        return Cart.builder()
                .member(member)
                .cartNo(0)
                .isCustomKit(isCustomKit)
                .build();
    }


    /**
     * 샵바이에 등록된 장바구니 정보를 가져와서 해당 장바구니가 커스텀키트인지 관리자키트인지 분리해서 리턴
     *
     * @param
     * @return CartResponseDto
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/06/12
     **/

    @Transactional
    public CartResponseDto.ResponseDto getCart(UserDetailsImpl userDetails) throws UnirestException, ParseException {

        List<CartQueryDto> cart = cartRepository.findCartInfoByMemberId(userDetails.getMember().getId());

        // 장바구니가 비어있으면 -> 빈 장바구니 바로 return
        if (cart.isEmpty()) {
            return new CartResponseDto.ResponseDto();
        }

        // 장바구니에 중복이 있는지 검증
        List<CartQueryDto> distinctCart = new ArrayList<>(cart.stream()
                .collect(Collectors.toMap(CartQueryDto::getProductNo, Function.identity(), (existing, replacement) -> existing))
                .values());

        Set<Integer> productNosInCart = distinctCart.stream().map(CartQueryDto::getProductNo).collect(Collectors.toSet());
        Set<Integer> nonCustomKitProductNosInCart = distinctCart.stream()
                .filter(item -> !item.isCustomKit())
                .map(CartQueryDto::getProductNo)
                .collect(Collectors.toSet());

        // 커스텀 키트인 상품만 샵바이 요청을 통해 상품 상세 받아옴
        List<CartProductResponseDto.CartProductInfo> cartProductInfos = productNosInCart.isEmpty()
                ? Collections.emptyList()
                : shopbyGetProductListRequest(productNosInCart.stream().mapToInt(Integer::intValue).toArray(), userDetails.getMember().getShopByAccessToken());

        int totalStandardAmt = 0;
        int totalImmediateDiscountAmt = 0;
        for (CartProductResponseDto.CartProductInfo info : cartProductInfos) {
            totalStandardAmt += info.getSalePrice();
            totalImmediateDiscountAmt += info.getImmediateDiscountAmt();
        }

        // productNo를 키로 하고 CartQueryDto 자체를 값으로 하는 Map 생성
        Map<Integer, CartQueryDto> productNoToCartQueryDtoMap = distinctCart.stream()
                .collect(Collectors.toMap(CartQueryDto::getProductNo, Function.identity()));

        // cartProductInfos 리스트를 순회하면서 CartQueryDto의 정보를 사용
        cartProductInfos.forEach(info -> {
            CartQueryDto cartQueryDto = productNoToCartQueryDtoMap.get(info.getProductNo());
            if (cartQueryDto != null) {
                info.setCartId(cartQueryDto.getCartId());
                info.setProductOptionNo(cartQueryDto.getProductOptionNumber());
                info.setOrderCnt(cartQueryDto.getOrderCnt());
            }
        });

        Map<Integer, List<CartResponseDto.OrderProductOption>> productOptionMap = cartProductInfos.stream()
                .map(productInfo -> new CartResponseDto.OrderProductOption(
                        productInfo.getProductName(),
                        productInfo.getProductName(),
                        productInfo.getOrderCnt(),
                        productInfo.getStockCnt(),
                        productInfo.getProductOptionNo(),
                        productInfo.getProductNo(),
                        0, // Replace 0 with the appropriate value
                        productInfo.getSalePrice(),
                        productInfo.getImmediateDiscountAmt()
                ))
                .collect(Collectors.groupingBy(CartResponseDto.OrderProductOption::getProductNo));

        List<CartResponseDto.OrderProduct> customKitProducts = new ArrayList<>();
        List<CartResponseDto.OrderProduct> nonCustomKitProducts = new ArrayList<>();

        // Preprocess the productOptionMap to have direct access to options
        Map<Integer, List<CartResponseDto.OrderProductOption>> preprocessedOptionMap = preprocessOptionMap(cartProductInfos, productOptionMap);

        for (CartProductResponseDto.CartProductInfo cartProductInfo : cartProductInfos) {
            boolean isCustomKit = !nonCustomKitProductNosInCart.contains(cartProductInfo.getProductNo());
            List<CartResponseDto.OrderProductOption> productOptions = preprocessedOptionMap.getOrDefault(cartProductInfo.getProductNo(), Collections.emptyList());

            CartResponseDto.OrderProduct orderProduct = new CartResponseDto.OrderProduct(
                    cartProductInfo.getCartId(),
                    isCustomKit,
                    cartProductInfo.getProductNo(),
                    cartProductInfo.getBrandName(),
                    cartProductInfo.getProductName(),
                    cartProductInfo.getImageUrl(),
                    productOptions
            );

            if (isCustomKit) {
                customKitProducts.add(orderProduct);
            } else {
                nonCustomKitProducts.add(orderProduct);
            }
        }

        return new CartResponseDto.ResponseDto(
                totalStandardAmt, totalImmediateDiscountAmt,
                CustomValue.defaultDeliveryPrice, CustomValue.defaultDeliveryAbovePrice,
                customKitProducts, nonCustomKitProducts);
    }

    public NewCartResponseDto getCart(UserDetailsImpl userDetails, CartRequestDto.ProductNosDto dto) throws UnirestException, ParseException {
        List<NewCartResponseDto.CartSectionResponseDto> sections = new ArrayList<>();
        List<CartQueryDto> cart = cartRepository.findCartInfoByMemberId(userDetails.getMember().getId());
        List<Integer> cartProductNos = cart.stream().map(CartQueryDto::getProductNo).collect(Collectors.toList());

        // TODO: 3/25/24 팀구매 productNos 가져오기
        List<Integer> groupPurchaseProductNos = getGroupPurchaseProductNos();

        // TODO: 3/19/24 최근본 상품 & 찜한 상품에서 품절 제외
        List<ProductResponseDto> allRecentProductListInfo = getRecentProductList(dto, cartProductNos, groupPurchaseProductNos);
        List<WishListResponseDto.WishListProducts> filteredWishListProducts = getWishListProducts(userDetails, cartProductNos, groupPurchaseProductNos);

        // TODO: 3/22/24 찜하기 없으면 -> 피부 서베이에서 어울릴만한 제품들 추천
        //List<MdKitResponseDto> productInfoByUserSurvey = productService.getProductInfoByUserSurvey(userDetails);

        List<NewCartResponseDto.InCartProduct> customKitInCart = new ArrayList<>();
        List<NewCartResponseDto.InCartProduct> nonCustomKitInCart = new ArrayList<>();

        Map<Integer, Boolean> productStockNotification = notificationAgreeService.getProductStockNotification(cartProductNos);

        boolean isMadeFreeDeliverySection = false;
        if (!cart.isEmpty()) {
            List<CartQueryDto> uniqueCart = cart.stream()
                    .collect(Collectors.collectingAndThen(
                            // Collect to a map, using productNo as key. This will keep the first occurrence only because of the merge function.
                            Collectors.toMap(CartQueryDto::getProductNo, Function.identity(), (existing, replacement) -> existing, LinkedHashMap::new),
                            // Convert the values of the map back to a list
                            map -> new ArrayList<>(map.values())
                    ));
            isMadeFreeDeliverySection = getCartProductSection(userDetails, dto, cartProductNos, sections, uniqueCart, customKitInCart, nonCustomKitInCart, productStockNotification);
        }

        getRecommendSection(dto, sections, allRecentProductListInfo, filteredWishListProducts,
                productStockNotification, isMadeFreeDeliverySection,
                groupPurchaseProductNos, cartProductNos);

        return new NewCartResponseDto(CustomValue.defaultDeliveryPrice, CustomValue.defaultDeliveryAbovePrice, sections);
    }

    private List<Integer> getGroupPurchaseProductNos() throws UnirestException, ParseException {
        Map<DisplayType, List<DisplaySectionResponseDto.SectionItem>> displaySectionItems = displayService.getDisplaySectionItems();
        if (displaySectionItems.get(DisplayType.GROUP_PURCHASE) == null) {
            return Collections.emptyList();
        }
        int displayNo = displaySectionItems.get(DisplayType.GROUP_PURCHASE).get(0).getDisplayNo();
        List<DisplayResponseDto.DisplayProductInfoList> displayProductInfoList = displayService.getDisplayDetailInfo(displayNo).getDisplayProductInfoList();


        return displayProductInfoList.get(0).getDisplayProductInfos().stream().map(DisplayResponseDto.DisplayProductInfo::getProductNo).collect(Collectors.toList());
    }

    /**
     * 찜하기 상품 리스트 조회
     * 팀구매, 장바구니 상품 제외해서 5개만
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 3/25/24
     **/
    private List<WishListResponseDto.WishListProducts> getWishListProducts(UserDetailsImpl userDetails, List<Integer> cartProductNos, List<Integer> groupPurchaseProductNos) {
        return wishListService
                .getAllWishList(userDetails, true, 20, 1)
                .getProducts()
                .stream()
                .filter(product -> product.getStockCnt() > 0
                        && !groupPurchaseProductNos.contains(product.getProductNo())
                        && !cartProductNos.contains(product.getProductNo())
                        && !product.getDisplayCategoryNo().equals(todayPriceCategoryNo))
                .limit(5)
                .collect(Collectors.toList());
    }

    /**
     * 최근 본 상품 조회
     * 재고가 없거나 팀구매 상품 제외
     *
     * @param
     * @param groupPurchaseProductNos, cartProductNos
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 3/25/24
     **/
    private List<ProductResponseDto> getRecentProductList(CartRequestDto.ProductNosDto dto, List<Integer> cartProductNos, List<Integer> groupPurchaseProductNos) throws UnirestException {
        return productService.getProductListInfo(dto.getProductNos())
                .stream()
                .filter(product -> product.getStockCnt() > 0
                        && !groupPurchaseProductNos.contains(product.getProductNo())
                        && !cartProductNos.contains(product.getProductNo())
                        && !product.getDisplayCategoryNo().equals(todayPriceCategoryNo))
                .collect(Collectors.toList());
    }

    private void getRecommendSection(CartRequestDto.ProductNosDto dto, List<NewCartResponseDto.CartSectionResponseDto> sections,
                                     List<ProductResponseDto> recentProductListInfo,
                                     List<WishListResponseDto.WishListProducts> wishListProducts,
                                     Map<Integer, Boolean> productStockNotification,
                                     boolean isMadeFreeDeliverySection,
                                     List<Integer> groupPurchaseProductNos, List<Integer> cartProductNos) throws UnirestException, ParseException {
        if (recentProductListInfo.isEmpty() && wishListProducts.isEmpty() && !isMadeFreeDeliverySection) {
            // TODO: 3/14/24 장바구니에 진짜 아무것도 없으면
            addBestSellerSection(dto, sections, productStockNotification, groupPurchaseProductNos, cartProductNos);
        } else if (!isMadeFreeDeliverySection) {
            // TODO: 3/25/24
            addRecentViewedProductsSection(sections, recentProductListInfo, productStockNotification);
        } else {
            addZeroProductsSection(sections, productStockNotification, cartProductNos);
        }
        addWishListProductsSection(sections, wishListProducts, productStockNotification);

    }

    private void addZeroProductsSection(List<NewCartResponseDto.CartSectionResponseDto> sections,
                                        Map<Integer, Boolean> productStockNotification,
                                        List<Integer> cartProductNos) throws UnirestException, ParseException {
        Map<Integer, ProductQueryDto> productMap = getProductMap(cartProductNos);
        long zeroExperienceProductCount = productMap.values().stream()
                .filter(productQueryDto -> CategoryType.KIT.equals(productQueryDto.getCategoryType()))
                .count();
        // TODO: 2024-03-27 증정 샘플을 하나도 담지 않은 유저 -> 증정샘플 유도
        if (zeroExperienceProductCount == 0) {
            addZeroSection(sections, productStockNotification);
        }
    }

    private void addBestSellerSection(CartRequestDto.ProductNosDto dto,
                                      List<NewCartResponseDto.CartSectionResponseDto> sections,
                                      Map<Integer, Boolean> productStockNotification,
                                      List<Integer> groupPurchaseProductNos, List<Integer> cartProductNos) throws UnirestException, ParseException {
        // TODO: 3/15/24 dto.getProductNos -> 장바구니에 담긴 제품이 나오면 안됨
        List<BestSellerResponseDto> bestSellers = productService.getBestSellerByShoppBy();
        List<Integer> productNos = bestSellers.stream()
                .map(BestSellerResponseDto::getProductNo)
                .filter(productNo -> !groupPurchaseProductNos.contains(productNo) && !cartProductNos.contains(productNo))
                .collect(Collectors.toList());

        log.info("장바구니 best 상품 조회 Product Nos: " + productNos.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", ")));

        Map<Integer, ProductQueryDto> productMap = getProductMap(productNos);
        processSection(sections, "이런 상품은 어떠세요?", bestSellers, productMap, productStockNotification);
    }

    private void addRecentViewedProductsSection(List<NewCartResponseDto.CartSectionResponseDto> sections, List<ProductResponseDto> products, Map<Integer, Boolean> productStockNotification) throws UnirestException, ParseException {
        List<Integer> productNos = products.stream().map(ProductResponseDto::getProductNo).collect(Collectors.toList());
        Map<Integer, ProductQueryDto> productMap = getProductMap(productNos);
        processSection(sections, "최근 본 상품", products, productMap, productStockNotification);
    }

    private void addWishListProductsSection(List<NewCartResponseDto.CartSectionResponseDto> sections, List<WishListResponseDto.WishListProducts> wishListProducts, Map<Integer, Boolean> productStockNotification) {
        List<Integer> wishListProductNos = wishListProducts.stream().map(WishListResponseDto.WishListProducts::getProductNo).collect(Collectors.toList());
        Map<Integer, ProductQueryDto> productMap = getProductMap(wishListProductNos);
        // TODO: 2024-03-20 리뷰 정보가 들어가야함
        processWishListSection(sections, wishListProducts, productMap, productStockNotification);
    }

    private boolean getCartProductSection(UserDetailsImpl userDetails, CartRequestDto.ProductNosDto dto,
                                          List<Integer> cartProductNos, List<NewCartResponseDto.CartSectionResponseDto> sections, List<CartQueryDto> uniqueCart,
                                          List<NewCartResponseDto.InCartProduct> customKitInCart,
                                          List<NewCartResponseDto.InCartProduct> nonCustomKitInCart,
                                          Map<Integer, Boolean> productStockNotification) throws UnirestException, ParseException {

        Map<Integer, CartQueryDto> cartItemMap = uniqueCart.stream().collect(Collectors.toMap(
                CartQueryDto::getProductNo, // Key Mapper
                Function.identity()));

        List<CartProductResponseDto.CartProductInfo> cartProductInfosFromShopBy = shopbyGetProductListRequest(
                uniqueCart.stream().mapToInt(CartQueryDto::getProductNo).toArray(), userDetails.getMember().getShopByAccessToken());

        cartProductInfosFromShopBy.forEach(info -> processCartProduct(info, cartItemMap, customKitInCart, nonCustomKitInCart, productStockNotification));
        List<ProductType> productTypeList = new ArrayList<>();
        if (!customKitInCart.isEmpty()) {
            productTypeList = List.of(ProductType.SAMPLE);
            addSectionIfNotEmpty(sections, "전체 상품", customKitInCart, productTypeList, 10);
        }

        if (!nonCustomKitInCart.isEmpty()) {
            productTypeList = List.of(ProductType.KIT, ProductType.ZERO_SAMPLE);
            addSectionIfNotEmpty(sections, "0원 샘플", nonCustomKitInCart, productTypeList, 4);
        } else {
            return true;
        }

        //addZeroSection(sections, nonCustomKitInCart,productStockNotification);
        return recommendProductsForFreeDelivery(dto, cartProductNos, sections, customKitInCart, productStockNotification);
    }

    private void getOrderProductSection(UserDetailsImpl userDetails, List<OrderResponseDto.OrderSectionResponseDto> sections, List<CartQueryDto> cart, Map<Integer, CartQueryDto> cartItemMap, List<OrderResponseDto.InOrderProduct> customKitInCart, List<OrderResponseDto.InOrderProduct> nonCustomKitInCart, boolean isExcludeZeroItem) throws UnirestException, ParseException {
        List<CartProductResponseDto.CartProductInfo> cartProductInfosFromShopBy = shopbyGetProductListRequest(
                cart.stream().mapToInt(CartQueryDto::getProductNo).toArray(), userDetails.getMember().getShopByAccessToken());

        cartProductInfosFromShopBy.forEach(info -> processOrderProduct(info, cartItemMap, customKitInCart, nonCustomKitInCart));

        if (!customKitInCart.isEmpty()) {
            addSectionIfNotEmpty(sections, ProductType.SAMPLE, "전체 상품", customKitInCart);
        }

        if (!nonCustomKitInCart.isEmpty() && !isExcludeZeroItem) {
            addSectionIfNotEmpty(sections, ProductType.ZERO_SAMPLE, "0원 샘플", nonCustomKitInCart);
        }
    }


    private void addZeroSection(List<NewCartResponseDto.CartSectionResponseDto> sections, Map<Integer, Boolean> productStockNotification) throws UnirestException, ParseException {
        Map<DisplayType, List<DisplaySectionResponseDto.SectionItem>> displaySectionItems = displayService.getDisplaySectionItems();
        if (displaySectionItems.get(DisplayType.ZERO_EXPERIENCE) != null) {
            CustomKitResponseDto customKitResponseDto = customKitService.shopbyGetProductListByCategoryNo(experienceCategoryNo, 1, 10, SearchSortType.SALE_YMD);
            // TODO: 4/4/24 품절인 0원 샘플 보여줘야하나 
            List<CustomKitResponseDto.CustomKitItemInfo> sectionItems = customKitResponseDto.getItem();

            List<Integer> productNos = sectionItems.stream().map(CustomKitResponseDto.CustomKitItemInfo::getProductNo).collect(Collectors.toList());
            Map<Integer, ProductQueryDto> productMap = getProductMap(productNos);

            processZeroSection(sections, "최대 4개까지 무료로 드려요! (0원 샘플) ", sectionItems, productMap, productStockNotification);
        }
    }

    private void processCartProduct(CartProductResponseDto.CartProductInfo info, Map<Integer, CartQueryDto> cartItemMap,
                                    List<NewCartResponseDto.InCartProduct> customKitInCart, List<NewCartResponseDto.InCartProduct> nonCustomKitInCart,
                                    Map<Integer, Boolean> productStockNotification) {
        NewCartResponseDto.InCartProduct inCartProduct = createInCartProduct(cartItemMap, info, productStockNotification);
        if (inCartProduct != null) {
            List<NewCartResponseDto.InCartProduct> targetList = cartItemMap.get(info.getProductNo()).isCustomKit() ? customKitInCart : nonCustomKitInCart;
            targetList.add(inCartProduct);
        }
    }

    private void processOrderProduct(CartProductResponseDto.CartProductInfo info, Map<Integer, CartQueryDto> cartItemMap,
                                     List<OrderResponseDto.InOrderProduct> customKitInCart, List<OrderResponseDto.InOrderProduct> nonCustomKitInCart) {
        OrderResponseDto.InOrderProduct inOrderProduct = createInOrderProduct(cartItemMap, info);
        if (inOrderProduct != null) {
            List<OrderResponseDto.InOrderProduct> targetList = cartItemMap.get(info.getProductNo()).isCustomKit() ? customKitInCart : nonCustomKitInCart;
            targetList.add(inOrderProduct);
        }
    }

    private void addSectionIfNotEmpty(List<NewCartResponseDto.CartSectionResponseDto> sections,
                                      String sectionTitle, List<NewCartResponseDto.InCartProduct> products, List<ProductType> productTypeList, int layoutPriority) {
        if (!products.isEmpty()) {
            sections.add(new NewCartResponseDto.CartSectionResponseDto("CartProducts", productTypeList, sectionTitle, products, layoutPriority));
        }
    }

    private void addSectionIfNotEmpty(List<OrderResponseDto.OrderSectionResponseDto> sections,
                                      ProductType productType, String sectionTitle, List<OrderResponseDto.InOrderProduct> products) {
        if (!products.isEmpty()) {
            if (productType.equals(ProductType.ZERO_SAMPLE)) {
                sections.add(new OrderResponseDto.OrderSectionResponseDto(productType.toString(), sectionTitle, products));
            } else {
                sections.add(new OrderResponseDto.OrderSectionResponseDto(productType.toString(), sectionTitle, products));
            }
        }
    }

    /**
     * 배송비 무료 배송금액 20,000원 이하이면 true
     *
     * @param
     * @param cartProductNos
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 3/25/24
     **/
    private boolean recommendProductsForFreeDelivery(CartRequestDto.ProductNosDto dto, List<Integer> cartProductNos, List<NewCartResponseDto.CartSectionResponseDto> sections,
                                                     List<NewCartResponseDto.InCartProduct> products, Map<Integer, Boolean> productStockNotification) throws UnirestException, ParseException {
        int totalCartPrice = products.stream().mapToInt(product -> product.getPrice() - product.getImmediateDiscountAmt()).sum();
        if (totalCartPrice < CustomValue.defaultDeliveryAbovePrice) {
            int additionalPriceNeeded = CustomValue.defaultDeliveryAbovePrice - totalCartPrice;
            // TODO: 3/15/24 장바구니에 담긴 제품이 나오면 안됨
            List<Integer> excludeProductNos = dto.getProductNos();
            excludeProductNos.addAll(cartProductNos);
            List<BestSellerResponseDto> productsForFreeDelivery = productService.getProductForDeliveryPrice(additionalPriceNeeded, excludeProductNos);
            List<Integer> combinedProductNos = getCombinedUniqueProductNos(dto.getProductNos(), productsForFreeDelivery.stream().map(BestSellerResponseDto::getProductNo).collect(Collectors.toList()));
            Map<Integer, ProductQueryDto> productMap = getProductMap(combinedProductNos);
            processSection(sections, "무료배송 임박!", productsForFreeDelivery, productMap, productStockNotification);
            return true;
        }
        return false;
    }

    private List<Integer> getCombinedUniqueProductNos(List<Integer> productNos, List<Integer> wishListProductNos) {
        return Stream.concat(productNos.stream(), wishListProductNos.stream())
                .distinct()
                .collect(Collectors.toList());
    }

    private Map<Integer, ProductQueryDto> getProductMap(List<Integer> productNos) {
        List<ProductQueryDto> productCategoryList = productService.getProductCategory(productNos);
        return productCategoryList.stream()
                .collect(Collectors.toMap(ProductQueryDto::getProductNo, Function.identity()));
    }

    private NewCartResponseDto.InCartProduct createInCartProduct(Map<Integer, CartQueryDto> cartItemMap,
                                                                 CartProductResponseDto.CartProductInfo info,
                                                                 Map<Integer, Boolean> productStockNotification) {
        CartQueryDto cartQueryDto = cartItemMap.get(info.getProductNo());
        Boolean isProductStockNotification = productStockNotification.getOrDefault(info.getProductNo(), false);

        if (cartQueryDto != null) {
            // TODO: 3/14/24 stockCnt == 0 재입고 알림 했는지 여부도 true여야
            int orderCnt = cartQueryDto.getOrderCnt();
            if (orderCnt > info.getStockCnt()) {
                orderCnt = info.getStockCnt();
            }

            ProductType productType = ProductType.SAMPLE;
            if (!cartQueryDto.isCustomKit()) {
                productType = ProductType.ZERO_SAMPLE;
            }

            return new NewCartResponseDto.InCartProduct(
                    cartQueryDto,
                    productType,
                    info.getProductName(),
                    info.getBrandName(),
                    info.getImageUrl(),
                    info.getStockCnt(),
                    orderCnt,
                    cartQueryDto.getProductOptionNumber(),
                    info.getSalePrice(),
                    info.getImmediateDiscountAmt(),
                    isProductStockNotification,
                    Optional.ofNullable(cartQueryDto.getIsMultiPurchase()).orElse(false),
                    Integer.parseInt(info.getDisplayCategoryNo())
            );
        }
        return null;
    }

    private OrderResponseDto.InOrderProduct createInOrderProduct(Map<Integer, CartQueryDto> cartItemMap,
                                                                 CartProductResponseDto.CartProductInfo info) {
        CartQueryDto cartQueryDto = cartItemMap.get(info.getProductNo());
        ProductType productType = ProductType.SAMPLE;

        if (cartQueryDto != null) {
            if (!cartQueryDto.isCustomKit()) {
                productType = ProductType.ZERO_SAMPLE;
            }
            return new OrderResponseDto.InOrderProduct(
                    productType,
                    info.getProductNo(),
                    cartQueryDto.getProductOptionNumber(),
                    info.getBrandName(),
                    info.getProductName(),
                    info.getImageUrl(),
                    cartQueryDto.getOrderCnt(),
                    info.getSalePrice(),
                    info.getImmediateDiscountAmt()
            );
        }
        return null;
    }

    private void processWishListSection(List<NewCartResponseDto.CartSectionResponseDto> sections,
                                        List<? extends ICartProductInfo> products,
                                        Function<ICartProductInfo, Object> productTransformer) {
        List<Object> transformedProducts = products.stream()
                .map(productTransformer)
                .collect(Collectors.toList());

        if (!transformedProducts.isEmpty()) {
            sections.add(new NewCartResponseDto.CartSectionResponseDto("VerticalProducts", "찜한 상품", transformedProducts, MoveCase.WISHLIST));
        }
    }


    private void processWishListSection(List<NewCartResponseDto.CartSectionResponseDto> sections,
                                        List<WishListResponseDto.WishListProducts> wishListProducts,
                                        Map<Integer, ProductQueryDto> productMap,
                                        Map<Integer, Boolean> productStockNotification) {

        processWishListSection(sections, wishListProducts,
                product -> {
                    WishListResponseDto.WishListProducts wishProduct = (WishListResponseDto.WishListProducts) product;
                    ProductQueryDto productQueryDto = productMap.get(wishProduct.getProductNo());
                    return new NewCartResponseDto.WishProduct(
                            wishProduct.getProductType(),
                            wishProduct.getProductNo(),
                            wishProduct.getBrandName(),
                            wishProduct.getProductName(),
                            wishProduct.getImageUrl(),
                            wishProduct.getStockCnt(),
                            productQueryDto.getProductOptionNo(),
                            wishProduct.getSalePrice(),
                            wishProduct.getImmediateDiscountAmt(),
                            productStockNotification.getOrDefault(wishProduct.getProductNo(), false),
                            Optional.ofNullable(productQueryDto.getIsMultiPurchase()).orElse(false),
                            Integer.parseInt(wishProduct.getDisplayCategoryNo()),
                            wishProduct.getReviewRating(),
                            wishProduct.getTotalReviewCount()
                    );
                });
    }

    private void processZeroSection(List<NewCartResponseDto.CartSectionResponseDto> sections, String sectionTitle,
                                    List<CustomKitResponseDto.CustomKitItemInfo> sectionItems,
                                    Map<Integer, ProductQueryDto> productMap,
                                    Map<Integer, Boolean> productStockNotification) {
        List<NewCartResponseDto.InCartProduct> inCartProducts = sectionItems.stream()
                .map(product -> {
                    ProductQueryDto productQueryDto = productMap.get(product.getProductNo());
                    if (productQueryDto == null) {
                        return Optional.<NewCartResponseDto.InCartProduct>empty();
                    }
                    boolean isCustomKit = CategoryType.SAMPLE.equals(productQueryDto.getCategoryType());
                    NewCartResponseDto.InCartProduct inCartProduct = new NewCartResponseDto.InCartProduct(
                            isCustomKit ? ProductType.SAMPLE : ProductType.ZERO_SAMPLE,
                            product.getProductNo(),
                            product.getProductName(),
                            product.getImgUrl(),
                            product.getBrandName(),
                            product.getStockCnt(),
                            productQueryDto.getProductOptionNo(),
                            product.getSalePrice(),
                            product.getImmediateDiscountAmt(),
                            productStockNotification.getOrDefault(product.getProductNo(), false),
                            Optional.ofNullable(productQueryDto.getIsMultiPurchase()).orElse(false),
                            experienceCategoryNo
                    );
                    return Optional.of(inCartProduct);
                })
                .flatMap(Optional::stream) // Optional.empty()를 제거하고, 유효한 객체만 추출
                .collect(Collectors.toList());

        if (!inCartProducts.isEmpty()) {
            sections.add(new NewCartResponseDto.CartSectionResponseDto("HorizontalProducts", sectionTitle, inCartProducts));
        }
    }

    private <T extends ICartProductInfo> void processSection(List<NewCartResponseDto.CartSectionResponseDto> sections,
                                                             String sectionTitle,
                                                             List<T> otherProducts, Map<Integer, ProductQueryDto> productMap,
                                                             Map<Integer, Boolean> productStockNotification) {
        List<NewCartResponseDto.InCartProduct> inCartProducts = otherProducts.stream()
                .map(product -> {
                    ProductQueryDto productQueryDto = productMap.get(product.getProductNo());
                    if (productQueryDto == null) {
                        return Optional.<NewCartResponseDto.InCartProduct>empty();
                    }
                    boolean isCustomKit = CategoryType.SAMPLE.equals(productQueryDto.getCategoryType());
                    NewCartResponseDto.InCartProduct inCartProduct = new NewCartResponseDto.InCartProduct(
                            isCustomKit ? ProductType.SAMPLE : ProductType.ZERO_SAMPLE,
                            product.getProductNo(),
                            product.getProductName(),
                            product.getImageUrl(),
                            product.getBrandName(),
                            product.getStockCnt(),
                            productQueryDto.getProductOptionNo(),
                            product.getSalePrice(),
                            product.getImmediateDiscountAmt(),
                            productStockNotification.getOrDefault(product.getProductNo(), false),
                            Optional.ofNullable(productQueryDto.getIsMultiPurchase()).orElse(false),
                            Integer.parseInt(product.getDisplayCategoryNo())
                    );
                    return Optional.of(inCartProduct);
                })
                .flatMap(Optional::stream) // Optional.empty()를 제거하고, 유효한 객체만 추출
                .collect(Collectors.toList());

        if (!inCartProducts.isEmpty()) {
            sections.add(new NewCartResponseDto.CartSectionResponseDto("HorizontalProducts", sectionTitle, inCartProducts));
        }
    }

    private Map<Integer, List<CartResponseDto.OrderProductOption>> preprocessOptionMap(List<CartProductResponseDto.CartProductInfo> cartProductInfos,
                                                                                       Map<Integer, List<CartResponseDto.OrderProductOption>> productOptionMap) {
        Map<Integer, List<CartResponseDto.OrderProductOption>> preprocessedMap = new HashMap<>();
        for (CartProductResponseDto.CartProductInfo info : cartProductInfos) {
            preprocessedMap.put(info.getProductNo(), productOptionMap.getOrDefault(info.getProductNo(), Collections.emptyList()));
        }
        return preprocessedMap;
    }

    /**
     * 장바구니 삭제
     * 샵바이 장바구니 이용안함 독립
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/12/31
     **/
    @Transactional
    public HashMap<String, Object> deleteCart(CartRequestDto.CartIdsDto cartIdsDto, UserDetailsImpl userDetails) {

        HashMap<String, Object> resultMap = new HashMap<>();

        List<Long> cartIds = cartIdsDto.getCartIds();
        // cartNos에 해당하는 cartItem조회
        List<CartItemQueryDto> cartItemsToDelete = cartItemRepository.findByMemberAndCartIdsIn(userDetails.getMember().getId(), cartIds);

        // 삭제할 cartItem의 id
        List<Long> cartItemIds = cartItemsToDelete.stream()
                .map(CartItemQueryDto::getCartItemId)
                .collect(Collectors.toList());

        List<Integer> customKitProductNos = cartItemsToDelete.stream()
                .map(CartItemQueryDto::getProductNo)
                .collect(Collectors.toList());

        if (customKitProductNos.isEmpty()) {
            return resultMap;
        }

        cartItemRepository.deleteAllByIdInQuery(cartItemIds);
        cartRepository.deleteAllByCartIdsInQuery(cartIds);

        resultMap.put("hasCart", cartRepository.existsByMemberId(userDetails.getMember().getId()));

        return resultMap;
    }

    @Transactional
    public void updateCart(CartRequestDto.UpdateCart dto, UserDetailsImpl userDetails, Long cartId) {

        Cart cart = cartRepository.findByIdAndMemberId(cartId, userDetails.getMember().getId())
                .orElseThrow(() -> new ErrorCustomException(ErrorCode.CART_NOT_FOUND));

        if (cart.getId().equals(cartId)) {
            Integer productOptionNo = 0;
            if (dto.getOptionNo() != null) {
                productOptionNo = dto.getOptionNo();
            } else {
                productOptionNo = dto.getProductOptionNo();
            }
            CartItem cartItem = cartItemRepository.findByCartIdAndProductOptionNumber(cartId, productOptionNo)
                    .orElseThrow(() -> new ErrorCustomException(ErrorCode.CARTITEM_NOT_FOUND));
            if (CategoryType.EXPERIENCE.equals(cartItem.getProduct().getCategory().getCategoryDepth2())) {
                if (!cartItem.getProduct().getIsMultiPurchase()) {
                    throw new ErrorCustomException(ErrorCode.EXPERIENCE_PRODUCT_MAX_COUNT_ZERO);
                }
            }
            cartItem.updateCartItemCount(dto.getOrderCnt());
        }
    }

    /**
     * 장바구니에 담긴 제품들을 주문 + 샵바이에 주문서 작성하기
     *
     * @param
     * @param isExcludeZeroItem
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/06/09
     **/
    @Transactional
    public OrderResponseDto.CreateOrderSheet createCartOrder(UserDetailsImpl userDetails, CartRequestDto.CartIdsDto cartId, boolean isExcludeZeroItem) throws UnirestException, ParseException {

        List<CartQueryDto> cart = cartRepository.findCartByCartIds(userDetails.getMember().getId(), cartId.getCartIds());
        if (cart.isEmpty()) {
            throw new ErrorCustomException(ErrorCode.CART_NOT_FOUND);
        }

        List<Integer> cartProductNos = cart.stream().map(CartQueryDto::getProductNo).collect(Collectors.toList());

        // TODO: 3/21/24 상품정보 내려줘야함
        List<OrderResponseDto.OrderSectionResponseDto> sections = getInCartProductInfo(userDetails, cart, isExcludeZeroItem);

        // TODO: 3/25/24 혹시라도 팀구매 제품있는지 검사
        orderService.validateGroupPurchaseProducts(cartProductNos);
        orderService.checkEventProductPriceCondition(userDetails, cartProductNos);
        orderService.checkZeroPerfumeItems(userDetails,cartProductNos);

        int[] cartNosArray = cartProductNos.stream()
                .mapToInt(Integer::intValue)
                .toArray();

        List<CartResponseDto.Product> productList = new ArrayList<>();
        List<CartQueryDto> customKitCart = new ArrayList<>();
        List<Integer> productNosToRemove = new ArrayList<>();

        for (CartQueryDto cartQueryDto : cart) {
            productList.add(new CartResponseDto.Product(
                    cartQueryDto.getProductNo(),
                    cartQueryDto.getProductOptionNumber(),
                    cartQueryDto.getOrderCnt()
            ));
            if (!cartQueryDto.isCustomKit()) {
                customKitCart.add(cartQueryDto);
                productNosToRemove.add(cartQueryDto.getProductNo());
            }
        }

        if (!customKitCart.isEmpty()) {
            // TODO: 2024-01-29 필수 서베이 체크
            if (!isExcludeZeroItem) {
                // TODO: 2/1/24 제외안하고 주문할게요
                boolean isCheckZeroExperienceSurvey = checkZeroExperienceSurvey(userDetails);
                if (isCheckZeroExperienceSurvey) {
                    // TODO: 2/1/24 필수해야하는 사람
                    throw new ErrorCustomException(ErrorCode.QUESTION_SURVEY_EXISTS);
                }
            } else {
                // TODO: 2/1/24 제외하고 주문할게요
                if (!productNosToRemove.isEmpty()) {
                    productList.removeIf(product -> productNosToRemove.contains(product.getProductNo()));
                }
            }

        }

        log.info("productList Size S________-> " + productList.size());

        CartResponseDto.CreateOrder createOrder = new CartResponseDto.CreateOrder(productList, cartNosArray);

        // 샵바이에 주문서 작성하기
        String orderSheetNo = shopbyCreateOrder(userDetails.getMember().getShopByAccessToken(), createOrder);
        // TODO: 2023/09/05 주문 금액관련 계산 api 요창
        OrderPaymentPriceResponseDto.PaymentInfo paymentInfo = orderService.calculateAllPaymentPrice(userDetails, createOrder, orderSheetNo);
        // TODO: 2023/10/24 주문에 적용할 수 있는 쿠폰  조회 api
        OrderCalculateCouponResponseDto couponByOrderSheetNo = couponService.getCouponByOrderSheetNo(userDetails, orderSheetNo);

        return new OrderResponseDto.CreateOrderSheet(orderSheetNo, paymentInfo, couponByOrderSheetNo, sections);
    }

    private List<OrderResponseDto.OrderSectionResponseDto> getInCartProductInfo(UserDetailsImpl userDetails, List<CartQueryDto> cart, boolean isExcludeZeroItem) throws UnirestException, ParseException {
        List<OrderResponseDto.OrderSectionResponseDto> sections = new ArrayList<>();
        List<OrderResponseDto.InOrderProduct> customKitInCart = new ArrayList<>();
        List<OrderResponseDto.InOrderProduct> nonCustomKitInCart = new ArrayList<>();
        if (!cart.isEmpty()) {
            Map<Integer, CartQueryDto> cartItemMap = cart.stream().collect(Collectors.toMap(CartQueryDto::getProductNo, Function.identity()));
            getOrderProductSection(userDetails, sections, cart, cartItemMap, customKitInCart, nonCustomKitInCart, isExcludeZeroItem);
        }
        return sections;
    }

    private boolean checkZeroExperienceSurvey(UserDetailsImpl userDetails) {
        // Check if it's the first purchase
        boolean firstPurchase = orderService.getFirstPurchase(userDetails);
        if (firstPurchase) {
            return false;
        }
        return zeroExperienceReviewService.getZeroExperienceByIsNecessary(userDetails.getMember().getId());
    }


    /**
     * 주문서 작성하기
     *
     * @return orderSheetNo - 주문서 번호
     * 샵바이 version 1.0.0
     **/
    private String shopbyCreateOrder(String shopByAccessToken, CartResponseDto.CreateOrder createOrder) throws
            UnirestException, ParseException {
        HttpResponse<String> response = Unirest.post(shopByUrl + "/order-sheets")
                .header("accept", acceptHeader)
                .header("version", versionHeader)
                .header("clientid", clientId)
                .header("platform", platformHeader)
                .header("accesstoken", shopByAccessToken)
                .header("content-type", acceptHeader)
                .body(gson.toJson(createOrder))
                .asString();

        ShopBy.errorMessage(response);
        JsonObject resJsonObject = gson.fromJson(response.getBody(), JsonObject.class);
        return resJsonObject.get("orderSheetNo").getAsString();
    }

    private List<CartProductResponseDto.CartProductInfo> shopbyGetProductListRequest(int[] productNos, String
            shopByAccessToken) throws UnirestException, ParseException {

        HttpResponse<String> response = getSearchByNosHttpResponse(productNos, shopByAccessToken);

        JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
        JsonArray productsArray = jsonObject.getAsJsonArray("products");
        List<CartProductResponseDto.CartProductInfo> responseDtos = new ArrayList<>();
        for (JsonElement element : productsArray) {
            JsonObject productObject = element.getAsJsonObject();
            JsonObject baseInfoObject = productObject.getAsJsonObject("baseInfo");
            // JSON 객체에서 필요한 값을 추출
            int productNo = baseInfoObject.get("productNo").getAsInt();
            int stockCnt = baseInfoObject.get("stockCnt").getAsInt();
            String productName = baseInfoObject.get("productName").getAsString();
            String brandName = baseInfoObject.get("brandName").getAsString();
            brandName = processBrandName(brandName);
            String imageUrl = baseInfoObject.getAsJsonArray("imageUrls").get(0).getAsString();
            imageUrl = "https:" + imageUrl;
            String displayCategoryNos = baseInfoObject.get("displayCategoryNos").getAsString();
            if (displayCategoryNos.length() >= 6) {
                displayCategoryNos = displayCategoryNos.substring(0, 6);
            }
            JsonObject priceObject = productObject.getAsJsonObject("price");
            int price = priceObject.get("salePrice").getAsInt();
            int immediateDiscountAmt = priceObject.get("immediateDiscountAmt").getAsInt();

            CartProductResponseDto.CartProductInfo cartProductInfo =
                    new CartProductResponseDto.CartProductInfo(
                            0L, productNo, 0,
                            stockCnt, 0, productName,
                            brandName, imageUrl, displayCategoryNos,
                            price, immediateDiscountAmt);

            responseDtos.add(cartProductInfo);
        }

        return responseDtos;
    }

    private String processBrandName(String brandName) {
        int slashIndex = brandName.indexOf('/');
        return slashIndex != -1 ? brandName.substring(0, slashIndex).trim() : brandName.trim();
    }

    private HttpResponse<String> getSearchByNosHttpResponse(int[] productNos, String shopByAccessToken) throws
            UnirestException, ParseException {
        JSONObject json = new JSONObject();
        json.put("productNos", productNos);
        json.put("hasOptionValues", "true");

        HttpResponse<String> response = Unirest.post(shopByUrl + products + "/search-by-nos")
                .header("accept", acceptHeader)
                .header("version", versionHeader)
                .header("clientid", clientId)
                .header("platform", platformHeader)
                .header("accesstoken", shopByAccessToken)
                .header("content-type", acceptHeader)
                .body(gson.toJson(json))
                .asString();

        ShopBy.errorMessage(response);
        return response;
    }
}





























