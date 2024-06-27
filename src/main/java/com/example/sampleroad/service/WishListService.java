package com.example.sampleroad.service;

import com.example.sampleroad.common.exception.ErrorCode;
import com.example.sampleroad.common.exception.ErrorCustomException;
import com.example.sampleroad.domain.WishList;
import com.example.sampleroad.domain.product.Product;
import com.example.sampleroad.dto.response.wishList.WishListQueryDto;
import com.example.sampleroad.dto.response.wishList.WishListResponseDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.example.sampleroad.repository.samplekit.SampleKitRepository;
import com.example.sampleroad.repository.wishlist.WishListRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WishListService {

    private final ProductService productService;
    private final ProductShopByService productShopByService;
    private final WishListRepository wishListRepository;
    private final SampleKitRepository sampleKitRepository;

    @Transactional
    public void addProductWishList(int productNo, UserDetailsImpl userDetails) {

        Product product = productService.getProduct(productNo);

        wishListRepository.findByProductIdAndMemberId(product.getId(), userDetails.getMember().getId())
                .ifPresent(wishList -> {
                    throw new ErrorCustomException(ErrorCode.ALREADY_WISHLIST_ERROR);
                });

        WishList wishList = WishList.builder()
                .product(product)
                .member(userDetails.getMember())
                .build();

        wishListRepository.save(wishList);

    }


    /**
     * 찜한 상품 모두 조회하기
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/06/14
     **/

    public WishListResponseDto.AllWishListFromShopby getAllWishList(UserDetailsImpl userDetails, boolean isSorted, int pageSize, int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        // isSorted = true - 날짜 내림차순 = 최신순
        Page<WishListQueryDto> wishList = wishListRepository.findProductIdsByMemberId(userDetails.getMember().getId(), pageable);

        // wishList가 비어있을 때 null 대신에 빈 리스트를 반환합니다.
        if (wishList.isEmpty()) {
            return new WishListResponseDto.AllWishListFromShopby(0, Collections.emptyList());
        }

        int[] productNos = wishList.stream()
                .mapToInt(WishListQueryDto::getProductNo)
                .toArray();

        List<Integer> productNoList = wishList.stream().map(WishListQueryDto::getProductNo)
                .collect(Collectors.toList());

        List<WishListResponseDto.WishListProducts> wishListProducts =
                productShopByService.shopbyGetProductListByProductNo(productNos);

        return new WishListResponseDto.AllWishListFromShopby((int) wishList.getTotalElements(), wishListProducts);
    }

    @Transactional
    public void deleteProductWishList(int productNo, UserDetailsImpl userDetails) {
        Product product = productService.getProduct(productNo);

        WishList wishList = wishListRepository.findByProductIdAndMemberId(product.getId(), userDetails.getMember().getId())
                .orElseThrow(() -> new ErrorCustomException(ErrorCode.NO_PRODUCT_WISHLIST_ERROR));

        wishListRepository.delete(wishList);

    }

    public Map<Integer, Boolean> isInWishlist(Long memberId, Set<Integer> productNos) {
        List<WishListQueryDto> wishList = wishListRepository.findByProductNosAndMemberId(productNos, memberId);

        // wishList에서 모든 productNo를 추출하여 Set으로 변환
        Set<Integer> wishListProductNos = wishList.stream()
                .map(WishListQueryDto::getProductNo)
                .collect(Collectors.toSet());
        // productNos 리스트를 이용하여 각 productNo가 wishList에 있는지 확인
        return productNos.stream()
                .collect(Collectors.toMap(productNo -> productNo, wishListProductNos::contains));
    }
}
