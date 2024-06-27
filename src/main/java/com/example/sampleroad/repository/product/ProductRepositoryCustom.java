package com.example.sampleroad.repository.product;

import com.example.sampleroad.dto.response.order.OrderResponseDto;
import com.example.sampleroad.dto.response.product.CustomKitQueryDto;
import com.example.sampleroad.dto.response.product.ProductDetailResponseDto;
import com.example.sampleroad.dto.response.product.ProductQueryDto;
import com.example.sampleroad.dto.response.product.ProductReviewInfoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface ProductRepositoryCustom {
    List<OrderResponseDto.SampleList> findSampleKitListByOrder(Set<Integer> productNo);
    List<ProductDetailResponseDto.SampleList> findSampleListByProductNoIn(List<Integer> productNoList);
    List<ProductDetailResponseDto.SampleList> findSampleList(int kitCategoryNo);

    boolean existsByProductNo(int productNo);

    List<CustomKitQueryDto> findCustomKitItemByProductNos(Set<Integer> productNos);

    Page<ProductQueryDto.SearchProductQueryDto> findBySearchKeywordPaging(Pageable pageable, String searchKeyword);
    Page<ProductQueryDto.SearchProductQueryDto> findBySearchKeywordPaging(Pageable pageable, int brandNo);

    List<ProductReviewInfoDto> findProductReviewInfo(List<Integer> kitItemsProductNos);
    List<ProductQueryDto> findProductCategoryByProductNos(List<Integer> productNos);
    List<ProductQueryDto> findProductCategoryType2ByProductNos(List<Integer> productNos);
    List<ProductQueryDto> findProductCategoryByProductNos(String skinType);
}
