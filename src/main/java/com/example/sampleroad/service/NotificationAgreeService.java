package com.example.sampleroad.service;

import com.example.sampleroad.common.exception.ErrorCode;
import com.example.sampleroad.common.exception.ErrorCustomException;
import com.example.sampleroad.domain.notify.ProductStockNotify;
import com.example.sampleroad.domain.notify.ProductStockNotifyDetail;
import com.example.sampleroad.domain.notify.WeeklyNotify;
import com.example.sampleroad.domain.product.Product;
import com.example.sampleroad.domain.push.NotificationAgree;
import com.example.sampleroad.dto.request.PushNotifyRequestDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.example.sampleroad.repository.notification.NotificationAgreeRepository;
import com.example.sampleroad.repository.notification.ProductStockDetailNotificationRepository;
import com.example.sampleroad.repository.notification.ProductStockNotificationRepository;
import com.example.sampleroad.repository.notification.WeeklyNotificationRepository;
import com.example.sampleroad.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class NotificationAgreeService {

    private final NotificationAgreeRepository notificationAgreeRepository;
    private final WeeklyNotificationRepository weeklyNotificationRepository;
    private final ProductStockDetailNotificationRepository productStockDetailNotificationRepository;
    private final ProductStockNotificationRepository productStockNotificationRepository;
    private final ProductRepository productRepository;

    @Transactional
    public void modifyAdPushNotify(PushNotifyRequestDto pushNotifyRequestDto,
                                   UserDetailsImpl userDetails) {
        NotificationAgree notificationAgree = getNotificationAgree(userDetails);
        notificationAgree.updateNotificationAgree(pushNotifyRequestDto);
    }

    @Transactional
    public void modifySmsPushNotify(PushNotifyRequestDto pushNotifyRequestDto, UserDetailsImpl userDetails) {
        NotificationAgree notificationAgree = getNotificationAgree(userDetails);
        notificationAgree.updateNotificationAgree(pushNotifyRequestDto);
    }

    @Transactional
    public void modifyWeeklyPriceNotify(boolean isNotify, UserDetailsImpl userDetails) {

        // TODO: 1/19/24 혹시나 notificationAgree가 없으면 update 안함 
        Optional<NotificationAgree> notificationAgree = notificationAgreeRepository.findByMemberId(userDetails.getMember().getId());
        Optional<WeeklyNotify> weeklyNotify = weeklyNotificationRepository.findByMemberId(userDetails.getMember().getId());

        // TODO: 2024/01/09 알림 동의 -> 광고 동의 + 주간 특가 동의 = true 
        // TODO: 2024/01/09 주간 특가 동의 false -> 주간 특가 동의만 = false
        if (weeklyNotify.isEmpty()) {
            if (isNotify) {
                WeeklyNotify newWeeklyNotify = WeeklyNotify.builder()
                        .weeklyNotificationAgree(true)
                        .member(userDetails.getMember())
                        .build();
                weeklyNotificationRepository.save(newWeeklyNotify);
            } else {
                notificationAgree.ifPresent(agree -> agree.updateNotificationAgree(true));
            }
        } else {
            weeklyNotify.get().updateNotifyStatus(isNotify);
        }
    }

    private NotificationAgree getNotificationAgree(UserDetailsImpl userDetails) {
        return notificationAgreeRepository.findByMemberId(userDetails.getMember().getId())
                .orElseThrow(() -> new ErrorCustomException(ErrorCode.NO_USER_ERROR));
    }

    @Transactional
    public void registerStockNotify(boolean isNotify, int productNo, UserDetailsImpl userDetails) {
        Product product = getProduct(productNo);
        Long memberId = userDetails.getMember().getId();

        // ProductStockNotify 객체 조회 또는 생성 및 상태 업데이트
        ProductStockNotify productStockNotify = productStockNotificationRepository.findByMemberId(memberId)
                .map(psn -> {
                    psn.updateProductStockNotificationAgree(true);
                    return psn;
                })
                .orElseGet(() -> productStockNotificationRepository.save(new ProductStockNotify(userDetails.getMember(), isNotify)));

        // ProductStockNotifyDetail 객체 처리
        ProductStockNotifyDetail productStockNotifyDetail = productStockDetailNotificationRepository
                .findByMemberIdAndProduct_ProductNo(memberId, productNo)
                .orElseGet(() -> new ProductStockNotifyDetail(isNotify, userDetails.getMember(), product, productStockNotify));

        // 상세 알림 설정 업데이트
        if (productStockNotifyDetail.getId() != null) {
            productStockNotifyDetail.updateProductStockNotificationAgree(isNotify);
        } else {
            productStockDetailNotificationRepository.save(productStockNotifyDetail);
        }
    }


    private static ProductStockNotifyDetail createProductStockNotifyDetail(UserDetailsImpl userDetails, Product product, ProductStockNotify productStockNotify) {
        return ProductStockNotifyDetail.builder()
                .product(product)
                .member(userDetails.getMember())
                .productStockDetailNotificationAgree(true)
                .productStockNotify(productStockNotify)
                .build();
    }

    @Transactional
    public void updateStockNotify(boolean isNotify, UserDetailsImpl userDetails) {
        Optional<ProductStockNotify> pushStockNotify = productStockNotificationRepository.findByMemberId(userDetails.getMember().getId());
        pushStockNotify.ifPresent(productStockNotify -> productStockNotify.updateProductStockNotificationAgree(isNotify));
    }

    /**
     * 해당하는 상품의 재입고 알림여부 조회
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 3/14/24
     **/
    public Map<Integer, Boolean> getProductStockNotification(List<Integer> cartProductNos) {
        List<ProductStockNotifyDetail> productStockNotifies = productStockDetailNotificationRepository.findByProduct_ProductNoIn(cartProductNos);
        return productStockNotifies.stream()
                .collect(Collectors.toMap(
                        notify -> notify.getProduct().getProductNo(), // Key mapper
                        ProductStockNotifyDetail::getProductStockDetailNotificationAgree, // Value mapper
                        (existingValue, newValue) -> existingValue // In case of duplicate keys, keep the existing value
                ));
    }

    /**
     * 해당하는 상품의 재입고 알림여부 조회
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 3/14/24
     **/
    public boolean getProductStockNotification(Long memberId, int productNo) {
        return productStockDetailNotificationRepository.findByMemberIdAndProduct_ProductNo(memberId, productNo)
                .map(ProductStockNotifyDetail::getProductStockDetailNotificationAgree)
                .orElse(false);
    }

    private Product getProduct(int productNo) {
        return productRepository.findByProductNoAndProductInvisible(productNo, false)
                .orElseThrow(() -> new ErrorCustomException(ErrorCode.PRODUCT_NOT_FOUND));
    }
}
