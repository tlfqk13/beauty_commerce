package com.example.sampleroad.service;

import com.example.sampleroad.common.exception.ErrorCode;
import com.example.sampleroad.common.exception.ErrorCustomException;
import com.example.sampleroad.common.utils.CustomValue;
import com.example.sampleroad.domain.display.DisplayDesignType;
import com.example.sampleroad.domain.grouppurchase.GroupPurchaseRoom;
import com.example.sampleroad.domain.grouppurchase.GroupPurchaseRoomMember;
import com.example.sampleroad.domain.grouppurchase.GroupPurchaseRoomProduct;
import com.example.sampleroad.domain.grouppurchase.GroupPurchaseType;
import com.example.sampleroad.domain.member.Member;
import com.example.sampleroad.domain.order.Orders;
import com.example.sampleroad.domain.product.Product;
import com.example.sampleroad.dto.request.order.OrderRequestDto;
import com.example.sampleroad.dto.response.PaymentResponseDto;
import com.example.sampleroad.dto.response.display.DisplayResponseDto;
import com.example.sampleroad.dto.response.grouppurchase.GroupPurchaseQueryDto;
import com.example.sampleroad.dto.response.grouppurchase.GroupPurchaseResponseDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.example.sampleroad.repository.grouppurchase.GroupPurchaseRoomMemberRepository;
import com.example.sampleroad.repository.grouppurchase.GroupPurchaseRoomProductRepository;
import com.example.sampleroad.repository.grouppurchase.GroupPurchaseRoomRepository;
import com.example.sampleroad.repository.member.MemberRepository;
import com.example.sampleroad.repository.product.ProductRepository;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupPurchaseService {

    private final GroupPurchaseRoomRepository groupPurchaseRoomRepository;
    private final GroupPurchaseRoomMemberRepository groupPurchaseRoomMemberRepository;
    private final GroupPurchaseRoomProductRepository groupPurchaseRoomProductRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final DisplayService displayService;

    @Value("${shop-by.group-purchase-img}")
    String groupPurchaseClosingSoonFirstIndexImgUrl;

    /**
     * 공동구매 방 참여하기 요청
     *
     * @param
     * @param roomId
     * @param memberRoomType
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2/6/24
     **/
    @Transactional
    public void joinGroupPurchaseRoom(Long roomId, UserDetailsImpl userDetails, GroupPurchaseType memberRoomType) {
        // TODO: 2/6/24 룸-맴버의 룸id로 하나 더 만들면 그게 참여
        Long memberId = userDetails.getMember().getId();
        // TODO: 2/15/24 해당방에 내가 신청한 내역이 있는지
        // 결제했는데 또 그방에서 주문? -> 어떻게 처리
        Optional<GroupPurchaseRoomMember> groupPurchaseRoomMember = getGroupPurchaseRoomMember(roomId, memberId);
        if (groupPurchaseRoomMember.isPresent()) {
            log.info(" 이미 내가 들어가있는 방에 또 들어가는 경우 에러처리_____________________");
            if (memberId.equals(groupPurchaseRoomMember.get().getMember().getId())) {
                throw new ErrorCustomException(ErrorCode.ALREADY_REGISTER_GROUP_PURCHASE_ROOM);
            }
        } else {
            Optional<GroupPurchaseRoom> groupPurchaseRoom = groupPurchaseRoomRepository.findById(roomId);
            if (groupPurchaseRoom.isPresent()) {
                log.info(" 들어갈 방 가져와서 거기에 참여_____________________");
                createGroupPurchaseRoomMember(userDetails, groupPurchaseRoom.get(), memberRoomType);
            } else {
                throw new ErrorCustomException(ErrorCode.NOT_REGISTER_GROUP_PURCHASE_ROOM);
            }
        }
    }

    public Optional<GroupPurchaseRoomMember> getGroupPurchaseRoomMember(Long roomId, Long memberId) {
        return groupPurchaseRoomMemberRepository.findByGroupPurchaseRoom_IdAndMember_IdAndIsPaymentFinish(roomId, memberId, true);
    }

    /**
     * 공동구매 방 리스트 조회하기
     * 최대 5개 입니다
     *
     * @param
     * @param productNo
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2/6/24
     **/
    public List<GroupPurchaseRoom> getGroupPurchaseRooms(int productNo) {
        // TODO: 2/15/24 구매확정이 있는 방만 조회
        // 완료된 방은 조회 X
        return groupPurchaseRoomRepository.findGroupPurchaseRoom(productNo);
    }

    /**
     * 공동구매 방 가져오는데 해당 유저 + 진행중인것만
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2/26/24
     **/
    public List<GroupPurchaseQueryDto.GroupPurchaseOrderInfoQueryDto> getGroupPurchaseRooms(Long memberId) {
        return groupPurchaseRoomMemberRepository.findGroupPurchaseRoomMembers(memberId);
    }

    public List<GroupPurchaseQueryDto.MemberProfileQueryDto> getGroupPurchaseRoomMember(int productNo) {
        return groupPurchaseRoomMemberRepository.findGroupPurchaseRoomMembers(productNo);
    }

    public GroupPurchaseQueryDto.MemberProfileQueryDto getGroupPurchaseRoomMemberWithOrderNo(String orderNo, boolean isPaymentFinish) {
        return (isPaymentFinish ?
                groupPurchaseRoomMemberRepository.findPaymentFinishGroupPurchaseRoomMembersByOrderNo(orderNo) :
                groupPurchaseRoomMemberRepository.findGroupPurchaseRoomMembersAllByOrderNo(orderNo))
                .orElse(null);
    }

    public GroupPurchaseQueryDto.MemberProfileQueryDto getCancelGroupPurchaseRoomMemberWithOrderNo(String orderNo) {
        return (groupPurchaseRoomMemberRepository.findCancelGroupPurchaseRoomMembersByOrderNo(orderNo).orElse(null));
    }

    public Map<String, GroupPurchaseQueryDto.GroupPurchaseOrderQueryDto> getGroupPurchaseRoomMembersWithOrderNos(List<String> orderNos, boolean isPaymentFinish) {
        List<GroupPurchaseQueryDto.GroupPurchaseOrderQueryDto> members = isPaymentFinish
                ? groupPurchaseRoomMemberRepository.findPaymentFinishGroupPurchaseRoomMembersByOrderNos(orderNos)
                : groupPurchaseRoomMemberRepository.findGroupPurchaseRoomMembersAllByOrderNos(orderNos);

        return members.stream()
                .collect(Collectors.toMap(GroupPurchaseQueryDto.GroupPurchaseOrderQueryDto::getOrderNo, Function.identity(), (existing, replacement) -> existing));
    }

    /**
     * 공동구매 방 만들기 (N인 팀구매 열기)
     *
     * @param
     * @param memberRoomType
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2/6/24
     **/
    @Transactional
    public Long makeGroupPurchaseRoom(int productNo, UserDetailsImpl userDetails, GroupPurchaseType memberRoomType) {
        // 현재 방 몇개 생성되어있느지 조회
        List<GroupPurchaseRoom> existingRooms = getGroupPurchaseRooms(productNo);
        Optional<GroupPurchaseRoomProduct> groupPurchaseRoomProduct = groupPurchaseRoomProductRepository.findByProduct_ProductNo(productNo);
        int roomMaxCount = 3;

        if (groupPurchaseRoomProduct.isPresent()) {
            roomMaxCount = groupPurchaseRoomProduct.get().getRoomMaxCount();
        }

        if (existingRooms.size() >= roomMaxCount) {
            log.info(" 빈방찾아서 넣기 방 참여 메소드 호출");
            memberRoomType = GroupPurchaseType.ROOM_JOIN;
            return joinEmptyRoomIfExistsOrElseCreateNew(productNo, userDetails, memberRoomType, roomMaxCount);
        } else {
            log.info(" 방 5개 이하의 경우 방 생성");
            return createRoomAndAddMember(productNo, userDetails, memberRoomType);
        }
    }

    private Long joinEmptyRoomIfExistsOrElseCreateNew(int productNo, UserDetailsImpl userDetails, GroupPurchaseType memberRoomType, int roomMaxCount) {
        // TODO: 3/28/24 findEmptyRoom 이게 왜 빈방일까요
        List<GroupPurchaseRoom> emptyRooms = new ArrayList<>();

        if (roomMaxCount == 1) {
            emptyRooms = groupPurchaseRoomRepository.findEmptyRoom(productNo);
        } else {
            emptyRooms = groupPurchaseRoomRepository.findEmptyRoomByMemberId(productNo, userDetails.getMember().getId());
        }

        if (!emptyRooms.isEmpty()) {
            log.info(" 빈방찾아서 넣기 emptyRoom.get");
            GroupPurchaseRoom emptyRoom = emptyRooms.get(0);
            joinGroupPurchaseRoom(emptyRoom.getId(), userDetails, memberRoomType);
            return emptyRoom.getId();
        } else {
            log.info("emptyRoom 이 비어있는 경우 방생성");
            memberRoomType = GroupPurchaseType.ROOM_MAKE;
            return createRoomAndAddMember(productNo, userDetails, memberRoomType);
        }
    }

    private Long createRoomAndAddMember(int productNo, UserDetailsImpl userDetails, GroupPurchaseType memberRoomType) {
        GroupPurchaseRoom groupPurchaseRoom = createGroupPurchaseRoom(productNo);
        createGroupPurchaseRoomMember(userDetails, groupPurchaseRoom, memberRoomType);
        log.info("createRoomAndAddMember - groupPurchaseRoom: {}", groupPurchaseRoom.getId());
        return groupPurchaseRoom.getId();
    }

    private void createGroupPurchaseRoomMember(UserDetailsImpl userDetails, GroupPurchaseRoom gr, GroupPurchaseType memberRoomType) {
        Member member = getMember(userDetails);
        GroupPurchaseRoomMember groupPurchaseRoomMember = GroupPurchaseRoomMember.builder()
                .groupPurchaseRoom(gr)
                .member(member)
                .memberRoomType(memberRoomType)
                .build();

        groupPurchaseRoomMemberRepository.save(groupPurchaseRoomMember);
    }

    private GroupPurchaseRoom createGroupPurchaseRoom(int productNo) {
        Product product = this.getProduct(productNo);
        // TODO: 2024-02-21 roomCapacity를 운영자가 받아야한다?
        Optional<GroupPurchaseRoomProduct> capacityInfo = groupPurchaseRoomProductRepository.findByProduct_ProductNo(productNo);
        int roomCapacity = 2;
        if (capacityInfo.isPresent()) {
            roomCapacity = capacityInfo.get().getRoomCapacity();
        }
        LocalDateTime deadLineTime = LocalDateTime.now().plusDays(1L);

        GroupPurchaseRoom groupPurchaseRoom = GroupPurchaseRoom.builder()
                .roomCapacity(roomCapacity)
                .product(product)
                .deadLine(deadLineTime)
                .build();

        return groupPurchaseRoomRepository.save(groupPurchaseRoom);
    }

    private Member getMember(UserDetailsImpl userDetails) {
        return memberRepository.findById(userDetails.getMember().getId())
                .orElseThrow(() -> new ErrorCustomException(ErrorCode.NO_USER_ERROR));
    }

    public List<GroupPurchaseResponseDto.SectionInfo> getGroupPurchaseSection() throws UnirestException, ParseException {

        List<GroupPurchaseResponseDto.SectionInfo> sectionInfoList = new ArrayList<>();

        // TODO: 2/7/24 임박 단 한자리 -> 내가 들어가면 바로 팀구매 완료되는게 있는 제품 
        createCloseSoonSection(sectionInfoList);

        // TODO: 2/7/24 기획전 조회로
        createGroupProductsSection(sectionInfoList);

        return sectionInfoList;

    }

    public List<GroupPurchaseResponseDto.ProductInfo> getGroupPurchaseProductList(int displayNo) throws UnirestException, ParseException {
        return createGroupProductsSection(displayNo);
    }

    public boolean groupPurchaseProduct(int productNo) throws UnirestException, ParseException {
        Set<Integer> productNos = getGroupPurchaseProductNos();
        return Optional.ofNullable(productNos)
                .map(nos -> nos.contains(productNo))
                .orElse(false);
    }

    private Set<Integer> getGroupPurchaseProductNos() throws UnirestException, ParseException {
        int displayNo = displayService.getDisplayEvents(DisplayDesignType.TYPE_C);
        if (displayNo == 0) {
            return Collections.emptySet();
        }
        DisplayResponseDto.DisplayDetailInfo displayDetailInfo = displayService.shopbyGetDisplayEventDetail(displayNo);
        List<DisplayResponseDto.DisplayProductInfoList> displayProductInfoList = displayDetailInfo.getDisplayProductInfoList();
        List<DisplayResponseDto.DisplayProductInfo> displayProductInfos = displayProductInfoList.get(0).getDisplayProductInfos();

        return displayProductInfos.stream()
                .map(DisplayResponseDto.DisplayProductInfo::getProductNo)
                .collect(Collectors.toSet());
    }


    public Set<Integer> groupPurchaseProduct() throws UnirestException, ParseException {
        return getGroupPurchaseProductNos();
    }

    private void createGroupProductsSection(List<GroupPurchaseResponseDto.SectionInfo> sectionInfoList) throws UnirestException, ParseException {
        int displayNo = displayService.getDisplayEvents(DisplayDesignType.TYPE_C);
        DisplayResponseDto.DisplayDetailInfo displayDetailInfo = displayService.shopbyGetDisplayEventDetail(displayNo);
        List<DisplayResponseDto.DisplayProductInfoList> displayProductInfoList = displayDetailInfo.getDisplayProductInfoList();
        List<DisplayResponseDto.DisplayProductInfo> displayProductInfos = displayProductInfoList.get(0).getDisplayProductInfos();
        String endYmdt = displayDetailInfo.getDisplayInfo().getEndYmdt();

        // Stream to map productNos to memberProfileImgUrls
        List<Integer> productNos = displayProductInfos.stream()
                .map(DisplayResponseDto.DisplayProductInfo::getProductNo)
                .collect(Collectors.toList());

        List<GroupPurchaseResponseDto.ProductInfo> products = getProductInfos(displayProductInfos, endYmdt, productNos);

        GroupPurchaseResponseDto.SectionInfo sectionInfo =
                new GroupPurchaseResponseDto.SectionInfo(GroupPurchaseType.GROUP_PRODUCT, "실시간 인기 팀구매 상품", products);

        sectionInfoList.add(sectionInfo);
    }

    private List<GroupPurchaseResponseDto.ProductInfo> createGroupProductsSection(int displayNo) throws UnirestException, ParseException {
        DisplayResponseDto.DisplayDetailInfo displayDetailInfo = displayService.shopbyGetDisplayEventDetail(displayNo);
        List<DisplayResponseDto.DisplayProductInfoList> displayProductInfoList = displayDetailInfo.getDisplayProductInfoList();
        List<DisplayResponseDto.DisplayProductInfo> displayProductInfos = displayProductInfoList.get(0).getDisplayProductInfos();
        String endYmdt = displayDetailInfo.getDisplayInfo().getEndYmdt();

        // Stream to map productNos to memberProfileImgUrls
        List<Integer> productNos = displayProductInfos.stream()
                .map(DisplayResponseDto.DisplayProductInfo::getProductNo)
                .collect(Collectors.toList());

        return getProductInfos(displayProductInfos, endYmdt, productNos);

    }

    private Map<Integer, List<String>> getLimitedMemberProfileImgUrlsMap(List<Integer> productNos) {
        // 제품 번호에 따른 회원 프로필 이미지 URL 맵핑을 가져오고, 각 제품 번호당 최대 3개의 이미지 URL만 유지합니다.
        return groupPurchaseRoomMemberRepository.findByProductNos(productNos).stream()
                .filter(memberProfile -> memberProfile.getMemberProfileImgUrl() != null)
                .collect(Collectors.groupingBy(
                        GroupPurchaseQueryDto.MemberProfileQueryDto::getProductNo,
                        Collectors.mapping(GroupPurchaseQueryDto.MemberProfileQueryDto::getMemberProfileImgUrl, Collectors.toList())
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream().distinct().limit(3).collect(Collectors.toList())
                ));
    }

    private List<GroupPurchaseResponseDto.ProductInfo> getProductInfos(List<DisplayResponseDto.DisplayProductInfo> displayProductInfos, String endYmdt, List<Integer> productNos) {
        Map<Integer, List<String>> productNoToLimitedMemberProfileImgUrlsMap = getLimitedMemberProfileImgUrlsMap(productNos);

        return displayProductInfos.stream()
                .map(displayProductInfo -> new GroupPurchaseResponseDto.ProductInfo(
                        displayProductInfo.getProductNo(),
                        displayProductInfo.getProductName(),
                        displayProductInfo.getBrandName(),
                        displayProductInfo.getImageUrl(),
                        displayProductInfo.getSalePrice(),
                        displayProductInfo.getImmediateDiscountAmt(),
                        displayProductInfo.getStockCnt(),
                        endYmdt,
                        productNoToLimitedMemberProfileImgUrlsMap.getOrDefault(displayProductInfo.getProductNo(), Collections.emptyList())
                ))
                .collect(Collectors.toList());
    }

    private void createCloseSoonSection(List<GroupPurchaseResponseDto.SectionInfo> sectionInfoList) {
        // groupPurchaseRoomMemberRepository.findLastOneProduct()의 결과를 직접 Stream으로 변환하여 처리
        List<GroupPurchaseResponseDto.ProductInfo> products = getLastGroupPurchaseProductInfos();

        GroupPurchaseResponseDto.ProductInfo specificProduct =
                new GroupPurchaseResponseDto.ProductInfo("열린 팀구매 마감까지", "남은 시간", groupPurchaseClosingSoonFirstIndexImgUrl);

        // 리스트의 맨 앞에 삽입
        products.add(0, specificProduct);

        // SectionInfo 객체 생성 및 sectionInfoList에 추가
        GroupPurchaseResponseDto.SectionInfo sectionInfo = new GroupPurchaseResponseDto.SectionInfo(
                GroupPurchaseType.CLOSING_SOON, "열린 팀구매 마감까지 남은 시간", products);
        sectionInfoList.add(sectionInfo);
    }


    @Transactional
    public void deleteGroupPurchaseRoomMember(GroupPurchaseRoomMember groupPurchaseRoomMember) {
        groupPurchaseRoomMemberRepository.delete(groupPurchaseRoomMember);
    }

    public Product getProduct(int productNo) {
        return productRepository.findByProductNoAndProductInvisible(productNo, false)
                .orElseThrow(() -> new ErrorCustomException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    @Transactional
    public Optional<GroupPurchaseRoomMember> updateRoomMemberFinishPayment(Long roomId, Long memberId, Orders orders) {

        Optional<GroupPurchaseRoomMember> groupPurchaseRoomMember = groupPurchaseRoomMemberRepository.findAllByGroupPurchaseRoom_IdAndMember_Id(roomId, memberId).stream().findFirst();

        if (groupPurchaseRoomMember.isPresent()) {
            int roomCapacity = groupPurchaseRoomMember.get().getGroupPurchaseRoom().getRoomCapacity();
            groupPurchaseRoomMember.get().updatePaymentFinish(true);
            groupPurchaseRoomMember.get().updateOrderId(orders);
            // TODO: 2/16/24 정원 꽉 찬 room 찾아서 isFull 업데이트
            List<GroupPurchaseQueryDto> isFullByRoomId = groupPurchaseRoomMemberRepository.findIsFullByRoomId(roomId);
            if (isFullByRoomId.size() == roomCapacity) {
                groupPurchaseRoomRepository.updateRoomIsFull(roomId);
            }
            return groupPurchaseRoomMember;
        } else {
            throw new ErrorCustomException(ErrorCode.ERROR_GROUP_PURCHASE_ROOM);
        }
    }

    /**
     * 구매 확정하고 RESPONSE 생성하는 메소드
     *
     * @param
     * @param groupPurchaseRoomMember
     * @param userDetails
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2/15/24
     **/
    public PaymentResponseDto.Confirm successPayment(OrderRequestDto.PaymentConfirm dto, Optional<GroupPurchaseRoomMember> groupPurchaseRoomMember, UserDetailsImpl userDetails) {

        List<GroupPurchaseQueryDto> groupPurchaseRoomMemberList = groupPurchaseRoomRepository.findByRoomId(dto.getRoomId());
        List<String> roomMemberImgUrls = new ArrayList<>();

        Map<Long, String> memberImgUrlMap = groupPurchaseRoomMemberList.stream()
                .collect(Collectors.toMap(
                        GroupPurchaseQueryDto::getMemberId,
                        member -> Optional.ofNullable(member.getImageUrl()).orElse(""),
                        (existingValue, newValue) -> existingValue));

        Long currentMemberId = userDetails.getMember().getId();
        String currentUserImgUrl = memberImgUrlMap.getOrDefault(currentMemberId, "");
        roomMemberImgUrls.add(0, currentUserImgUrl);

        if (memberImgUrlMap.size() >= 2) {
            memberImgUrlMap.entrySet().stream()
                    .filter(entry -> !entry.getKey().equals(currentMemberId)) // Exclude the current user
                    .map(Map.Entry::getValue) // Get the image URLs
                    .forEachOrdered(roomMemberImgUrls::add); // Append to the list
            log.info(" if roomMemberImgUrls.Size() : {}", roomMemberImgUrls.size());
        } else {
            roomMemberImgUrls.add(1, " ");
            log.info(" else roomMemberImgUrls.Size() : {}", roomMemberImgUrls.size());
        }

        PaymentResponseDto.RecommendProductList recommendProductList = getRecommendProductList();

        if (groupPurchaseRoomMember.isPresent()) {
            int roomCapacity = groupPurchaseRoomMember.get().getGroupPurchaseRoom().getRoomCapacity();
            // TODO: 2/16/24 방 총원 - 방에 결제 안한애들 포함되어있음
            int remainingCapacity = roomCapacity - groupPurchaseRoomMemberList.size();
            LocalDateTime deadLine = groupPurchaseRoomMember.get().getGroupPurchaseRoom().getDeadLine();
            String localDateStr = String.valueOf(deadLine.toLocalDate());
            String localTimeStr = String.valueOf(deadLine.toLocalTime());
            String deadLineStr = localDateStr + " " + localTimeStr;
            PaymentResponseDto.GroupPurchaseInfo groupPurchaseInfo = new PaymentResponseDto.GroupPurchaseInfo(
                    groupPurchaseRoomMember.get().getMemberRoomType(),
                    remainingCapacity,
                    deadLineStr,
                    roomMemberImgUrls);
            log.info("  roomMemberImg.Size() : {}", roomMemberImgUrls.size());
            return new PaymentResponseDto.Confirm(groupPurchaseInfo, recommendProductList);
        } else {
            return new PaymentResponseDto.Confirm();
        }
    }

    private PaymentResponseDto.RecommendProductList getRecommendProductList() {
        List<GroupPurchaseResponseDto.ProductInfo> products = getLastGroupPurchaseProductInfos();
        String title = CustomValue.groupProductRecommendTitle;
        String subTitle = CustomValue.groupProductRecommendSubTitle;
        log.info("  recommendProductList.Size() : {}", products.size());
        return new PaymentResponseDto.RecommendProductList(title, subTitle, products);
    }

    private List<GroupPurchaseResponseDto.ProductInfo> getLastGroupPurchaseProductInfos() {
        return groupPurchaseRoomMemberRepository.findLastOneProduct().stream()
                .map(groupPurchaseQueryDto -> new GroupPurchaseResponseDto.ProductInfo(
                        groupPurchaseQueryDto.getProductNo(),
                        groupPurchaseQueryDto.getProductName(),
                        groupPurchaseQueryDto.getDeadLineTime(),
                        groupPurchaseQueryDto.getImageUrl()))
                .collect(Collectors.toList());
    }

    /**
     * roomIds 로 groupPurchaseRoomMemberList 조회
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2/29/24
     **/
    public List<GroupPurchaseQueryDto.GroupPurchaseOrderInfoQueryDto> getGroupPurchaseRoomMember(List<Long> roomIds) {
        return groupPurchaseRoomMemberRepository.findGroupPurchaseRoomMembers(roomIds);
    }

    public Optional<GroupPurchaseRoomProduct> getGroupPurchaseProductMaxCnt(int productNo) {
        return groupPurchaseRoomProductRepository.findByProduct_ProductNo(productNo);
    }
}
