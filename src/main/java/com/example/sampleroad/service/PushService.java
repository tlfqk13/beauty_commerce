package com.example.sampleroad.service;

import com.example.sampleroad.common.exception.ErrorCode;
import com.example.sampleroad.common.exception.ErrorCustomException;
import com.example.sampleroad.domain.CategoryType;
import com.example.sampleroad.domain.SkinType;
import com.example.sampleroad.domain.member.Member;
import com.example.sampleroad.domain.product.Product;
import com.example.sampleroad.domain.push.*;
import com.example.sampleroad.domain.search.SearchSortType;
import com.example.sampleroad.dto.request.PushMessageRequestDto;
import com.example.sampleroad.dto.response.cart.CartItemQueryDto;
import com.example.sampleroad.dto.response.order.OrdersQueryDto;
import com.example.sampleroad.dto.response.push.NotificationResponseQueryDto;
import com.example.sampleroad.dto.response.push.PushDataResponseDto;
import com.example.sampleroad.dto.response.push.PushResponseQueryDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.example.sampleroad.repository.cart.CartItemRepository;
import com.example.sampleroad.repository.member.MemberRepository;
import com.example.sampleroad.repository.notification.NotificationAgreeRepository;
import com.example.sampleroad.repository.orders.OrdersRepository;
import com.example.sampleroad.repository.push.PushMessageRepository;
import com.example.sampleroad.repository.push.PushReadRepository;
import com.example.sampleroad.repository.push.PushRepository;
import com.example.sampleroad.repository.push.PushTokenRepository;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PushService {

    private final PushTokenRepository pushTokenRepository;
    private final MemberRepository memberRepository;
    private final NotificationAgreeRepository notificationAgreeRepository;
    private final PushRepository pushRepository;
    private final PushReadRepository pushReadRepository;
    private final PushProductService pushProductService;
    private final OrdersRepository ordersRepository;
    private final PushMessageRepository pushMessageRepository;
    private final CartItemRepository cartItemRepository;

    // TODO: 2023-08-23 운영반영시 주석

    @Value("${fcm.key.path}")
    String path;

    @Value("${fcm.key.scope}")
    String scope;


    @Value("${shop-by.best-category-no}")
    int bestCategoryNo;

    @Value("${shop-by.new-category-no}")
    int newCategoryNo;

    @Value("${shop-by.weekly-special-category-no}")
    int weeklySpecialCategoryNo;

/*    String path = "";
    String scope = "";*/

    @PostConstruct
    public FirebaseApp initFirebaseApp() {
        try {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials
                            .fromStream(new ClassPathResource(path)
                                    .getInputStream())
                            .createScoped(List.of(scope)))
                    .build();
            return FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("FireBaseApp Init Failed");
        }
    }

    @Transactional
    public void sendPushNotify(PushMessageRequestDto.Send dto, int pushPageNumber, int pushPageSize) {
        log.info("  =======================푸쉬알림 발송========================");
        log.info("  TARGET INFO [    title    : {}]", dto.getTitle());
        log.info("  TARGET INFO [    body     : {}]", dto.getBody());
        log.info("  =======================푸쉬알림 발송========================");

        // TODO: 2023/08/14 푸쉬알림 보낼 토큰 가져오는 쿼리문
        // TODO: 2023/08/17 푸쉬타입(정보성,광고성)
        // 푸쉬타입(정보성,광고성)에 따라 회원 목록 가져오기
        Page<NotificationResponseQueryDto> memberList;
        Pageable pageable = PageRequest.of(pushPageNumber, pushPageSize);
        if (PushType.ADVERTISE.equals(dto.getPushType())) {
            memberList = notificationAgreeRepository.findByPushType(PushType.ADVERTISE, pageable);
            // 광고성 - 상품 상세 이동, 신상품 출시(나만의 키트 만들기 이동)
        } else {
            // 정보성 - 업데이트, 구매확정, 첫구매 등
            memberList = notificationAgreeRepository.findByPushType(PushType.INFORMATION, pageable);
        }

        Set<PushResponseQueryDto.AgreedMember> agreedMemberList;
        List<Long> memberIds = memberList.get().map(NotificationResponseQueryDto::getMemberId).collect(Collectors.toList());
        if (dto.getMemberType().equals(MemberType.WITHOUT_PURCHASE_HISTORY)) {
            agreedMemberList = sendPushForWithoutPurchaseHistory(pageable);
        } else {
            if (dto.getMemberType().equals(MemberType.HAVE_PURCHASE_HISTORY)) {
                List<Long> filteredMemberIds = hasPurchaseHistory(memberIds);
                // push_token 조회
                agreedMemberList = pushTokenRepository.findByMemberIds(filteredMemberIds);
            } else {
                agreedMemberList = pushTokenRepository.findByMemberIds(memberIds);
            }
        }
        //---------------------------------------------------------
        HashMap<String, String> pushData = new HashMap<>();
        pushData.put("title", dto.getTitle());
        pushData.put("body", dto.getBody());

        try {
            // notification 설정 title,body,image등
            Notification notification = createNotification(dto);
            for (PushResponseQueryDto.AgreedMember member : agreedMemberList) {
                ApnsConfig apnsConfig = createApnsConfig(dto.getTitle(), dto.getBody());
                Message.Builder messageBuilder = Message.builder();
                messageBuilder
                        .setNotification(notification)
                        .setToken(member.getToken())
                        .setApnsConfig(apnsConfig)
                        .putAllData(pushData);
                Message message = messageBuilder.build();
                try {
                    String send = FirebaseMessaging.getInstance().send(message); // 푸시 메시지 보내기
                    log.info("푸쉬 알림 발송 성공: {}", send);
                } catch (Exception e) {
                    // TODO: 2023/11/13 notification true인 사람 삭제에서 토큰 다시 받는게 맞는듯.
                    // push token 삭제하는게 맞는듯
                    log.error("푸쉬 알림 발송 실패__S: {}", e.getMessage());
                    log.info("해당 유저 푸쉬 알림 발송 실페 : {}", member.getMemberId());
                    log.error("푸쉬 알림 발송 실패__E: {}", e.getMessage());
                    log.info("\n");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public void sendPushNotifyForEvent(PushMessageRequestDto.Send dto, int pushPageNumber, int pushPageSize) {
        log.info("  =======================푸쉬알림 발송========================");
        log.info("  TARGET INFO [    title    : {}]", dto.getTitle());
        log.info("  TARGET INFO [    body     : {}]", dto.getBody());
        log.info("  =======================푸쉬알림 발송========================");

        // TODO: 2023/08/14 푸쉬알림 보낼 토큰 가져오는 쿼리문
        // TODO: 2023/08/17 푸쉬타입(정보성,광고성)
        // 푸쉬타입(정보성,광고성)에 따라 회원 목록 가져오기
        Page<NotificationResponseQueryDto> memberList;

        Pageable pageable = PageRequest.of(pushPageNumber, pushPageSize);
        if (PushType.ADVERTISE.equals(dto.getPushType())) {
            memberList = notificationAgreeRepository.findByPushType(PushType.ADVERTISE, pageable);
            // 광고성 - 상품 상세 이동, 신상품 출시(나만의 키트 만들기 이동)
        } else {
            // 정보성 - 업데이트, 구매확정, 첫구매 등
            memberList = notificationAgreeRepository.findByPushType(PushType.INFORMATION, pageable);
        }

        Set<PushResponseQueryDto.AgreedMember> agreedMemberList;

        List<Long> memberIds = memberList.get()
                .map(NotificationResponseQueryDto::getMemberId)
                .collect(Collectors.toList());

        if (dto.getMemberType().equals(MemberType.WITHOUT_PURCHASE_HISTORY)) {
            agreedMemberList = sendPushForHasCart(pageable);
        } else {
            if (dto.getMemberType().equals(MemberType.HAVE_PURCHASE_HISTORY)) {
                List<Long> filteredMemberIds = hasPurchaseHistory(memberIds);
                // push_token 조회
                agreedMemberList = pushTokenRepository.findByMemberIds(filteredMemberIds);
            } else {
                agreedMemberList = pushTokenRepository.findByMemberIds(memberIds);
            }
        }

        if (dto.getSkinType().equals(SkinType.DRY)) {
            agreedMemberList = sendPushForSkinType(pageable);
        }

        log.info("푸쉬 알림 발송 agreedMemberList.size: {}", agreedMemberList.size());

        //---------------------------------------------------------
        HashMap<String, String> pushData = new HashMap<>();
        // TODO: 3/4/24 여기서 pushId를 넣어줘야함
        pushData.put("title", dto.getTitle());
        pushData.put("body", dto.getBody());

        List<String> fcmTokenList = agreedMemberList.stream().map(PushResponseQueryDto.AgreedMember::getToken).collect(Collectors.toList());
        try {
            Push push = createPush(dto);
            pushData.put("pushId", String.valueOf(push.getId()));
            Notification notification = createNotification(dto);
            ApnsConfig apnsConfig = createApnsConfig(dto.getTitle(), dto.getBody());
            MulticastMessage message = MulticastMessage.builder()
                    .setNotification(notification)
                    .setApnsConfig(apnsConfig)
                    .putAllData(pushData)
                    .addAllTokens(fcmTokenList)
                    .build();

            BatchResponse batchResponse = FirebaseMessaging.getInstance().sendMulticast(message);
            List<SendResponse> responses = batchResponse.getResponses();
            for (int i = 0; i < responses.size(); i++) {
                SendResponse sendResponse = responses.get(i);
                if (sendResponse.isSuccessful()) {
                    // 성공적으로 메시지가 전송된 토큰
                    String successfulToken = fcmTokenList.get(i);
                    log.info("successfulToken -> " + successfulToken);
                } else {
                    // 실패한 경우의 처리
                    String failedToken = fcmTokenList.get(i);
                    log.info("failedToken -> " + failedToken);
                    // 실패에 대한 추가적인 처리가 필요한 경우
                }
            }
            log.info("푸쉬 알림 발송 성공");
        } catch (Exception e) {
            log.error("푸쉬 알림 발송 실패: {}", e.getMessage());
            log.info("\n");
        }
    }

    @Transactional
    public void sendPushNotifyForEventIndividual(PushMessageRequestDto.Send dto, int pushPageNumber, int pushPageSize) {
        log.info("  =======================푸쉬알림 발송========================");
        log.info("  TARGET INFO [    title    : {}]", dto.getTitle());
        log.info("  TARGET INFO [    body     : {}]", dto.getBody());
        log.info("  =======================푸쉬알림 발송========================");

        // TODO: 2023/08/14 푸쉬알림 보낼 토큰 가져오는 쿼리문
        // TODO: 2023/08/17 푸쉬타입(정보성,광고성)
        // 푸쉬타입(정보성,광고성)에 따라 회원 목록 가져오기
        List<PushResponseQueryDto.AgreedMember> agreedMemberList;
        List<Long> memberIds = new ArrayList<>();

        memberIds.add(1L);
        memberIds.add(16073L); // 모브
        memberIds.add(35386L); // 옥토
        agreedMemberList = pushTokenRepository.findByMemberIdsList(memberIds);

        //---------------------------------------------------------
        HashMap<String, String> pushData = new HashMap<>();
        pushData.put("title", dto.getTitle());
        pushData.put("body", dto.getBody());

        String originBody = dto.getBody();
        String originTitle = dto.getTitle();

        Map<Long, String> map = agreedMemberList.stream()
                .collect(Collectors.toMap(
                        PushResponseQueryDto.AgreedMember::getMemberId,
                        member -> Optional.ofNullable(member.getMemberName()).orElse(""),
                        (existing, replacement) -> existing)); // Merge function in case of key collision

        try {
            // notification 설정 title,body,image등
            for (PushResponseQueryDto.AgreedMember member : agreedMemberList) {
                Long memberId = member.getMemberId();
                Push push = createPush(dto);
                pushData.put("pushId", String.valueOf(push.getId()));
                String setBody = "";
                if (map.get(memberId) != null) {
                    if(map.get(memberId).isEmpty()){
                         setBody = "(광고) " +"고객님께 "  + dto.getBody();
                    }else {
                         setBody = "(광고) " + map.get(memberId) + "님께 "  + dto.getBody();
                    }
                    String setTitle = dto.getTitle();
                    dto.setTitle(setTitle);  // setTitle 메서드 호출
                    dto.setBody(setBody);  // setTitle 메서드 호출
                    pushData.put("title", dto.getTitle());
                    pushData.put("body", dto.getBody());
                } else {
                    pushData.put("title",dto.getTitle());
                    pushData.put("body", "(광고) 고객님께 " + dto.getBody());
                }

                Notification notification = createNotification(dto);

                ApnsConfig apnsConfig = createApnsConfig(dto.getTitle(), dto.getBody());
                Message.Builder messageBuilder = Message.builder();
                messageBuilder
                        .setNotification(notification)
                        .setToken(member.getToken())
                        .setApnsConfig(apnsConfig)
                        .putAllData(pushData);
                Message message = messageBuilder.build();

                try {
                    String send = FirebaseMessaging.getInstance().send(message); // 푸시 메시지 보내기
                    log.info("푸쉬 알림 발송 성공: {}", send);
                    System.out.println("발송____________S");
                    System.out.println("title_ " + pushData.get("title"));
                    System.out.println("body_ " + pushData.get("body"));
                    System.out.println("발송____________E");
                    dto.setTitle(originTitle);  // setTitle 메서드 호출
                    dto.setBody(originBody);  // setTitle 메서드 호출
                } catch (Exception e) {
                    // TODO: 2023/11/13 notification true인 사람 삭제에서 토큰 다시 받는게 맞는듯.
                    // push token 삭제하는게 맞는듯
                    log.error("푸쉬 알림 발송 실패__S: {}", e.getMessage());
                    log.info("해당 유저 푸쉬 알림 발송 실페 : {}", member.getMemberId());
                    log.error("푸쉬 알림 발송 실패__E: {}", e.getMessage());
                    dto.setTitle(originTitle);  // setTitle 메서드 호출
                    dto.setBody(originBody);  // setTitle 메서드 호출
                    log.info("\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Long> hasPurchaseHistory(List<Long> memberIds) {
        // 구매내역있는 사람만 보낸다.
        List<OrdersQueryDto> ordersMember = ordersRepository.findByMemberIds(memberIds);
        List<Long> ordersMemberIds = ordersMember.stream().map(OrdersQueryDto::getMemberId).collect(Collectors.toList());
        // ordersMemberIds에 있는 ID들을 memberIds 리스트에서 제외합니다.
        List<Long> filteredMemberIds = memberIds.stream()
                .filter(ordersMemberIds::contains)
                .collect(Collectors.toList());
        return filteredMemberIds;
    }

    private Set<PushResponseQueryDto.AgreedMember> sendPushForWithoutPurchaseHistory(Pageable pageable) {

        Page<NotificationResponseQueryDto> notiAgreeMeberIds
                = notificationAgreeRepository.findWithoutPurchaseHistory(pageable);

        List<Long> idsWithPurchaseHistory = notiAgreeMeberIds.stream()
                .map(NotificationResponseQueryDto::getMemberId)
                .collect(Collectors.toList());

        return pushTokenRepository.findByMemberIds(idsWithPurchaseHistory);
        //---------------------------------------------------------
    }

    private Set<PushResponseQueryDto.AgreedMember> sendPushForHasCart(Pageable pageable) {

        Page<NotificationResponseQueryDto> notiAgreeMeberIds
                = notificationAgreeRepository.findHasCart(pageable);

        List<Long> idsWithPurchaseHistory = notiAgreeMeberIds.stream()
                .map(NotificationResponseQueryDto::getMemberId)
                .collect(Collectors.toList());

        return pushTokenRepository.findByMemberIds(idsWithPurchaseHistory);
        //---------------------------------------------------------
    }

    private Set<PushResponseQueryDto.AgreedMember> sendPushForSkinType(Pageable pageable) {
        Page<NotificationResponseQueryDto> notiAgreeMeberIds
                = notificationAgreeRepository.findBySkinCondition(pageable, SkinType.DRY);

        List<Long> idsWithCondition = notiAgreeMeberIds.stream()
                .map(NotificationResponseQueryDto::getMemberId)
                .collect(Collectors.toList());


        return pushTokenRepository.findByMemberIds(idsWithCondition);

    }


    private Push createPush(PushMessageRequestDto.Send dto) {
        Push push = Push.builder()
                .title(dto.getTitle())
                .content(dto.getBody())
                .pushType(dto.getPushType())
                .pushDataType(dto.getPushDataType())
                .productNo(dto.getProductNo())
                .build();

        pushRepository.save(push);

        return push;
    }

    private ApnsConfig createApnsConfig(String title, String body) {
        return ApnsConfig.builder()
                .setAps(Aps.builder()
                        .setAlert(ApsAlert.builder()
                                .setTitle(title)
                                .setBody(body)
                                .build())
                        .build())
                .build();
    }

    private Notification createNotification(PushMessageRequestDto.Send dto) {
        return Notification.builder()
                .setTitle(dto.getTitle())
                .setBody(dto.getBody())
                .setImage(dto.getImage())
                .build();
    }

    @Transactional
    public void createPushToken(UserDetailsImpl userDetails, String pushToken) {
        Member member = getMember(userDetails);

        if (pushToken != null && !pushToken.isEmpty()) {
            if (!pushTokenRepository.existsByMemberId(member.getId())) {
                PushToken token = PushToken.builder()
                        .member(member)
                        .token(pushToken)
                        .build();
                pushTokenRepository.save(token);
            } else {
                Optional<PushToken> token = pushTokenRepository.findFirstByMemberId(member.getId());
                log.info("토큰 업데이트 ~~");
                token.ifPresent(value -> value.updatePushToken(pushToken));
            }
        }
    }

    private Member getMember(UserDetailsImpl userDetails) {
        return memberRepository.findById(userDetails.getMember().getId())
                .orElseThrow(() -> new ErrorCustomException(ErrorCode.NO_USER_ERROR));
    }

    @Transactional
    public PushDataResponseDto sendPushResponse(UserDetailsImpl userDetails, Long pushId) {
        Optional<Push> pushOptional = pushRepository.findById(pushId);

        if (pushOptional.isEmpty()) {
            return new PushDataResponseDto(); // 빈 PushDataResponseDto 반환
        }

        Push push = pushOptional.get();
        log.info("Push Response _______________________S");

        PushType pushType = push.getPushType();
        PushDataType pushDataType = push.getPushDataType();

        PushDataResponseDto pushDataResponseDto = null;

        // 메인 로직에서는 이 메소드를 호출
        if (pushType == PushType.ADVERTISE || pushType == PushType.INFORMATION) {
            pushDataResponseDto = createPushDataResponseDto(pushDataType, push);
        } else {
            // 처리할 수 없는 pushType에 대한 로직
            log.error("Unsupported push type: {}", pushType);
        }

        createPushRead(pushId, userDetails.getMember().getId());

        log.info("Push Response _______________________E");
        return pushDataResponseDto != null ? pushDataResponseDto : new PushDataResponseDto();
    }

    // pushDataResponseDto 생성 로직을 메소드로 분리 (예시)
    private PushDataResponseDto createPushDataResponseDto(PushDataType pushDataType, Push push) {
        switch (pushDataType) {
            case ProductInfo:
                Product product = pushProductService.getProduct(push.getProductNo());
                CategoryType categoryType = product.getCategory().getCategoryDepth1();
                return new PushDataResponseDto(new PushDataResponseDto.ProductInfo(product.getProductNo(), categoryType), pushDataType);
            case CategoryInfo:
                int categoryNumber = push.getProductNo();
                SearchSortType sortType = (categoryNumber == newCategoryNo) ? SearchSortType.RECENT_PRODUCT : SearchSortType.POPULAR;
                return new PushDataResponseDto(new PushDataResponseDto.CategoryInfo(categoryNumber, sortType), pushDataType);
            case DisplayInfo:
                return new PushDataResponseDto(new PushDataResponseDto.DisplayInfo(push.getProductNo()), pushDataType);
            case ExperienceInfo:
                return new PushDataResponseDto(new PushDataResponseDto.ExperienceInfo((long) push.getProductNo()), pushDataType);
            case DownloadCoupon:
            case GroupPurchase:
            case Review:
            case Survey:
            case Update:
            case Cart:
                return new PushDataResponseDto(pushDataType);
            default:
                return new PushDataResponseDto(PushDataType.Update);
        }
    }

    private void createPushRead(Long pushId, Long memberId) {
        PushRead pushRead = PushRead.builder()
                .pushId(pushId)
                .isRead(true)
                .memberId(memberId)
                .build();

        pushReadRepository.save(pushRead);
    }


    public void sendCartProductNotifications(PushMessageRequestDto.Send dto, int pushPageNumber, int pushPageSize) {

        Pageable pageable = PageRequest.of(pushPageNumber, pushPageSize);

        Page<CartItemQueryDto> cartProductOver7days = cartItemRepository.findCartProductOver7days(pageable);

        List<Long> cartMemberIds = cartProductOver7days.stream().map(CartItemQueryDto::getCartItemId).collect(Collectors.toList());

        Page<NotificationResponseQueryDto> memberList = notificationAgreeRepository.findByPushTypeAndMemberIds(PushType.ADVERTISE, pageable, cartMemberIds);
        List<Long> memberIds = memberList.stream().map(NotificationResponseQueryDto::getMemberId).collect(Collectors.toList());
        Set<PushResponseQueryDto.AgreedMember> agreedMemberList = pushTokenRepository.findByMemberIds(memberIds);
        List<String> fcmTokenList = agreedMemberList.stream().map(PushResponseQueryDto.AgreedMember::getToken).collect(Collectors.toList());

        System.out.println("fcmTokenList____________");
        System.out.println("fcmTokenList____________ " + fcmTokenList.size());
        System.out.println("fcmTokenList____________");

        HashMap<String, String> pushData = new HashMap<>();
        pushData.put("title", dto.getTitle());
        pushData.put("body", dto.getBody());

        try {
            Notification notification = createNotification(dto);
            ApnsConfig apnsConfig = createApnsConfig(dto.getTitle(), dto.getBody());
            MulticastMessage message = MulticastMessage.builder()
                    .setNotification(notification)
                    .setApnsConfig(apnsConfig)
                    .putAllData(pushData)
                    .addAllTokens(fcmTokenList)
                    .build();
            FirebaseMessaging.getInstance().sendMulticast(message);
            log.info("푸쉬 알림 발송 성공");
        } catch (Exception e) {
            log.error("푸쉬 알림 발송 실패: {}", e.getMessage());
            log.info("\n");
        }
    }

    public void sendPurchaseIn7DaysAgo(PushMessageRequestDto.Send dto, int pushPageNumber, int pushPageSize) {

        Pageable pageable = PageRequest.of(pushPageNumber, pushPageSize);

        Page<OrdersQueryDto> orderIn7days = ordersRepository.findOrderIn7days(pageable);

        List<Long> orderMemberIds = orderIn7days.stream().map(OrdersQueryDto::getMemberId).collect(Collectors.toList());
        Page<NotificationResponseQueryDto> memberList = notificationAgreeRepository.findByPushTypeAndMemberIds(PushType.ADVERTISE, pageable, orderMemberIds);
        List<Long> memberIds = memberList.stream().map(NotificationResponseQueryDto::getMemberId).collect(Collectors.toList());
        Set<PushResponseQueryDto.AgreedMember> agreedMemberList = pushTokenRepository.findByMemberIds(memberIds);
        List<String> fcmTokenList = agreedMemberList.stream().map(PushResponseQueryDto.AgreedMember::getToken).collect(Collectors.toList());

        log.info("  =======================푸쉬알림 발송========================");
        log.info("  TARGET INFO [    title    : {}]", dto.getTitle());
        log.info("  TARGET INFO [    body     : {}]", dto.getBody());
        log.info("  =======================푸쉬알림 발송========================");

        System.out.println("fcmTokenList____________");
        System.out.println("fcmTokenList____________ " + fcmTokenList.size());
        System.out.println("fcmTokenList____________");

        HashMap<String, String> pushData = new HashMap<>();
        pushData.put("title", dto.getTitle());
        pushData.put("body", dto.getBody());

        try {
            Notification notification = createNotification(dto);
            ApnsConfig apnsConfig = createApnsConfig(dto.getTitle(), dto.getBody());
            MulticastMessage message = MulticastMessage.builder()
                    .setNotification(notification)
                    .setApnsConfig(apnsConfig)
                    .putAllData(pushData)
                    .addAllTokens(fcmTokenList)
                    .build();
            FirebaseMessaging.getInstance().sendMulticast(message);
            log.info("푸쉬 알림 발송 성공");
        } catch (Exception e) {
            log.error("푸쉬 알림 발송 실패: {}", e.getMessage());
            log.info("\n");
        }
    }


    private void sendCartProductPushMessage(Optional<PushMessage> pushMessage, Pageable pageable, List<Long> cartMemberIds) {
        if (pushMessage.isPresent()) {
            log.info("  =======================푸쉬알림 발송========================");
            log.info("  TARGET INFO [    title    : {}]", pushMessage.get().getTitle());
            log.info("  TARGET INFO [    body     : {}]", pushMessage.get().getContent());
            log.info("  =======================푸쉬알림 발송========================");


            PushMessageRequestDto.Send dto = new PushMessageRequestDto.Send(
                    pushMessage.get().getTitle(),
                    pushMessage.get().getContent(),
                    null,//productImageUrl,
                    PushType.ADVERTISE,
                    null,
                    null,
                    null,
                    0
            );

            Page<NotificationResponseQueryDto> memberList = notificationAgreeRepository.findByPushTypeAndMemberIds(PushType.ADVERTISE, pageable, cartMemberIds);
            List<Long> memberIds = memberList.stream().map(NotificationResponseQueryDto::getMemberId).collect(Collectors.toList());
            Set<PushResponseQueryDto.AgreedMember> agreedMemberList = pushTokenRepository.findByMemberIds(memberIds);
            List<String> fcmTokenList = agreedMemberList.stream().map(PushResponseQueryDto.AgreedMember::getToken).collect(Collectors.toList());

            System.out.println("fcmTokenList____________");
            System.out.println("fcmTokenList____________ " + fcmTokenList.size());
            System.out.println("fcmTokenList____________");

            HashMap<String, String> pushData = new HashMap<>();
            pushData.put("title", dto.getTitle());
            pushData.put("body", dto.getBody());

            try {
                Notification notification = createNotification(dto);
                ApnsConfig apnsConfig = createApnsConfig(dto.getTitle(), dto.getBody());
                MulticastMessage message = MulticastMessage.builder()
                        .setNotification(notification)
                        .setApnsConfig(apnsConfig)
                        .putAllData(pushData)
                        .addAllTokens(fcmTokenList)
                        .build();
                //FirebaseMessaging.getInstance().sendMulticast(message);
                log.info("푸쉬 알림 발송 성공");
            } catch (Exception e) {
                log.error("푸쉬 알림 발송 실패: {}", e.getMessage());
                log.info("\n");
            }
        }
    }
}
