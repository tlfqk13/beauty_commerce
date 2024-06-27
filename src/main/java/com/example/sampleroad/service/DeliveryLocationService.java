package com.example.sampleroad.service;

import com.example.sampleroad.common.exception.ErrorCode;
import com.example.sampleroad.common.exception.ErrorCustomException;
import com.example.sampleroad.domain.DeliveryLocation;
import com.example.sampleroad.domain.member.Member;
import com.example.sampleroad.dto.request.DeliveryLocationRequestDto;
import com.example.sampleroad.dto.response.DeliveryLocationResponseDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.example.sampleroad.repository.delivery.DeliveryLocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class DeliveryLocationService {

    private final DeliveryLocationRepository deliveryLocationRepository;

    /**
     * 배송지 등록
     * 기본 배송지로 등록할 경우 - 기존의 기본 배송지를 찾아 false로 업데이트 하고 수정할 배송지 기본 배송지로 수정
     *
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/06/14
     **/
    @Transactional
    public DeliveryLocationResponseDto.DeliveryLocationRegister registerDeliveryLocation(DeliveryLocationRequestDto.CreateDeliveryLocation dto, UserDetailsImpl userDetails) {

        boolean isExists = deliveryLocationRepository.existsByMemberId(userDetails.getMember().getId());

        // 기존에 배송지가 없는 경우 또는 새로운 배송지를 기본 배송지로 설정한 경우
        if (!isExists || dto.getDefaultAddress()) {
            // 기존 기본 배송지가 있는지 확인
            DeliveryLocation defaultAddressList = deliveryLocationRepository.findByMemberAndDefaultAddressIsTrue(userDetails.getMember()).orElse(null);
            if (defaultAddressList != null) {
                // 기존 기본 배송지가 있으면 기본 배송지 상태 해제
                defaultAddressList.updateDefaultAddress(false);
                deliveryLocationRepository.save(defaultAddressList);
            }
        }

        // 새로운 배송지 등록
        DeliveryLocation deliveryLocation = deliveryLocationRepository.save(dto.toEntity(userDetails.getMember()));
        return new DeliveryLocationResponseDto.DeliveryLocationRegister(deliveryLocation.getId());
    }


    public DeliveryLocationResponseDto.AllDeliveryLocations findMemberAllDeliveryLocations(UserDetailsImpl userDetails) {

        List<DeliveryLocationResponseDto.DeliveryLocation> addresses =
                deliveryLocationRepository.findByMember(userDetails.getMember())
                        .stream()
                        .map(DeliveryLocationResponseDto.DeliveryLocation::new)
                        .collect(Collectors.toList());

        return new DeliveryLocationResponseDto.AllDeliveryLocations(addresses.size(), addresses);
    }

    /**
     * 배송지 수정
     * 기본 배송지로 수정할 경우 - 기존의 기본 배송지를 찾아 false로 업데이트 하고 수정할 배송지 기본 배송지로 수정
     *
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/06/14
     **/
    @Transactional
    public void modifyDeliveryLocation(Long addressId, UserDetailsImpl userDetails, DeliveryLocationRequestDto.Update dto) {

        if (dto.getDefaultAddress()) {
            DeliveryLocation defaultAddressList = deliveryLocationRepository.findByMemberAndDefaultAddressIsTrue(userDetails.getMember())
                    .orElseThrow(() -> new ErrorCustomException(ErrorCode.NO_DELIVERY_LOCATION_ERROR));
            defaultAddressList.updateDefaultAddress(false);
        }

        DeliveryLocation deliveryLocation = getDeliveryLocation(addressId);
        deliveryLocation.updateDeliveryLocation(dto);
    }

    /**
     * 배송지 삭제
     * 기본 배송시 삭제할 경우 - 해당 배송지중에 addressId가 가장 높은걸 기본 배송지로 자동 설정
     *
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/06/14
     **/
    @Transactional
    public void removeDeliveryLocation(Long addressId, UserDetailsImpl userDetails) {
        List<DeliveryLocation> deliveryLocations = getDeliveryLocation(userDetails.getMember());

        Optional<DeliveryLocation> locationToDelete = deliveryLocations.stream()
                .filter(d -> Objects.equals(d.getId(), addressId))
                .findFirst();

        if (locationToDelete.isPresent()) {
            DeliveryLocation deletedLocation = locationToDelete.get();
            if (deletedLocation.getDefaultAddress()) {
                DeliveryLocation newDefaultLocation = findNewDefaultLocation(deliveryLocations, addressId);
                newDefaultLocation.updateDefaultAddress(true);
            }
            deliveryLocationRepository.delete(deletedLocation);
        } else {
            throw new ErrorCustomException(ErrorCode.NO_DELIVERY_LOCATION_ERROR);
        }
    }

    private DeliveryLocation findNewDefaultLocation(List<DeliveryLocation> deliveryLocations, Long deletedLocationId) {
        Optional<DeliveryLocation> newDefaultLocation = deliveryLocations.stream()
                .filter(location -> !location.getId().equals(deletedLocationId))
                .findFirst();

        if (newDefaultLocation.isEmpty()) {
            log.error("배송지 삭제 에러: 기본 배송지 삭제 시 새로운 기본 배송지를 찾을 수 없음");
            throw new ErrorCustomException(ErrorCode.NO_DELIVERY_LOCATION_ERROR);
        }

        return newDefaultLocation.get();
    }

    private DeliveryLocation getDeliveryLocation(Long addressId) {
        return deliveryLocationRepository.findById(addressId)
                .orElseThrow(() -> new ErrorCustomException(ErrorCode.NO_DELIVERY_LOCATION_ERROR));
    }

    private List<DeliveryLocation> getDeliveryLocation(Member member) {
        List<DeliveryLocation> deliveryLocations = deliveryLocationRepository.findByMember(member);
        if (deliveryLocations.isEmpty()) {
            throw new ErrorCustomException(ErrorCode.NO_DELIVERY_LOCATION_ERROR);
        }
        return deliveryLocations;
    }
}
