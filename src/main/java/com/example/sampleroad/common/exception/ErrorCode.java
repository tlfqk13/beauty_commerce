package com.example.sampleroad.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 로그인 및 권한 관련에러
    NO_USER_ERROR(404, "A001", "해당 유저를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_TOKEN_TYPE(400, "A002", "토큰 타입이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),

    REFRESH_TOKEN_EXPIRE(401, "A002", "토큰 만료 로그인을 다시 해주세요.", HttpStatus.UNAUTHORIZED),

    // Validation 체크 에러
    ALREADY_EMAIL_ERROR(409, "V001", "사용중인 이메일입니다.", HttpStatus.CONFLICT),
    ALREADY_USER_ERROR(409, "V002", "존재하는 사용자입니다.", HttpStatus.CONFLICT),
    ALREADY_WISHLIST_ERROR(400, "V003", "이미 찜하신 상품입니다.", HttpStatus.BAD_REQUEST),

    MEMBER_NOT_FOUND(404, "A004", "회원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    PRODUCT_NOT_FOUND(404, "V005", "존재하지 않는 상품입니다.", HttpStatus.NOT_FOUND),
    REVIEW_NOT_FOUND(404, "V006", "존재하지 않는 리뷰입니다.", HttpStatus.NOT_FOUND),

    ALREADY_SURVEY_ERROR(409, "V007", "이미 존재하는 설문입니다.", HttpStatus.CONFLICT),
    NOT_MATCHED_CI(404, "V008", "일치하지 않는 ci입니다.", HttpStatus.NOT_FOUND),

    NO_DELIVERY_LOCATION_ERROR(404, "V009", "존재하지 않는 배송지입니다.", HttpStatus.NOT_FOUND),
    NO_PRODUCT_WISHLIST_ERROR(404, "V010", "찜목록에 존재하지 않는 상품입니다.", HttpStatus.NOT_FOUND),

    NO_CERTIFICATION_ERROR(500, "V011", "본인인증 실패", HttpStatus.INTERNAL_SERVER_ERROR),
    MEMBERIDANDPASSWORDCHECK(400, "V012", "아이디 또는 비밀번호를 다시 한번 확인해 주시기 바랍니다.", HttpStatus.BAD_REQUEST),
    NO_CERTIFICATION(400, "V013", "인증이 먼저 진행되어야 합니다.", HttpStatus.BAD_REQUEST),
    ALREADY_CI(409, "V014", "사용중인 ci입니다.", HttpStatus.CONFLICT),
    NO_CI_ERROR(200, "V015", "사용 가능한 ci입니다.", HttpStatus.OK),
    ALREADY_USE_LOGIN_ID(409, "V016", "사용중인 로그인 ID입니다.", HttpStatus.CONFLICT),
    NO_USE_LOGIN_ID_ERROR(200, "V017", "사용 가능한 로그인 ID입니다.", HttpStatus.OK),
    NO_USE_NICKNAME_ERROR(200, "V018", "사용 가능한 닉네임입니다.", HttpStatus.OK),
    ALREADY_USE_NICKNAME_ERROR(409, "V019", "사용중인 닉네임입니다.", HttpStatus.CONFLICT),
    NO_USE_EMAIL_ERROR(200, "V020", "사용 가능한 이메일입니다.", HttpStatus.OK),
    ALREADY_USE_EMAIL_ERROR(409, "V021", "사용중인 이메일입니다.", HttpStatus.CONFLICT),
    NO_SURVEY_FOUND(404, "V022", "존재하지 않는 설문입니다.", HttpStatus.NOT_FOUND),
    WITHDRAWAL_MEMBER(400, "V022", "탈퇴 경과 30일 이전 계정입니다.", HttpStatus.BAD_REQUEST),
    CUSTOMKIT_NOT_FOUND(404, "V023", "커스텀키트 장바구니가 없습니다", HttpStatus.NOT_FOUND),
    CART_NOT_FOUND(404, "V024", "장바구니가 없습니다", HttpStatus.NOT_FOUND),
    CARTITEM_NOT_FOUND(404, "V025", "장바구니 아이템이 없습니다", HttpStatus.NOT_FOUND),
    ALREADY_HEART_REVIEW(409, "V026", "이미 추천하신 리뷰입니다", HttpStatus.CONFLICT),
    ALREADY_INCART_PRODUCT(409, "V027", "이미 장바구니에 담긴 상품입니다", HttpStatus.CONFLICT),
    FULL_INCART_PRODUCT(409, "V028", "최소 1개부터 최대 10개까지 선택 가능합니다.", HttpStatus.CONFLICT),
    INCORRECT_ORIGINAL_PASSWORD(400, "V029", "현재 비밀번호를 다시 한번 확인해 주시기 바랍니다", HttpStatus.BAD_REQUEST),
    ORDERSHEET_NOT_FOUND(400, "V030", "해당 주문을 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    PAYMENT_FAIL(400, "V031", "결제가 실패하였습니다", HttpStatus.CONFLICT),
    OVER_STOCK(400, "V032", "재고가 부족합니다", HttpStatus.CONFLICT),
    CALL_CUSTOMER_INFORMATION(409, "V033", "고객센터에 문의 해주세요", HttpStatus.CONFLICT),
    ORDER_NUMBER_NOT_FOUND(400, "V034", "주문번호를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    OVER_SEND_AUTHENTICATION(409, "V035", "인증번호 요청 횟수를 5회 초과하였습니다", HttpStatus.CONFLICT),
    IMAGE_FIELD_UPLOAD_FAIL(409, "V035", "사진 업로드를 실패하였습니다", HttpStatus.CONFLICT),
    NOT_REGISTER_PHOTO(404, "V037", "등록되지 않는 사진입니다", HttpStatus.NOT_FOUND),
    ALREADY_REGISTER_PRODUCT(400, "V038", "이미 등록된 상품입니다", HttpStatus.CONFLICT),
    NOT_REGISTER_CATEGORY(404, "V040", "등록되지않은 카테고리입니다", HttpStatus.NOT_FOUND),
    APPLE_MEMBER_NOT_FOUND(404, "V041", "애플 로그인으로 등록된 회원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    NOT_AFTER_30_DAYS_WITHDRAWAL_DATE(400, "V042", "탈퇴 후 30일 이후 재가입 가능합니다.\n" +
            "자세한 문의는 고객센터로 문의바랍니다.", HttpStatus.CONFLICT),
    UNDER_14YEARS_MEMBER(400, "V043", "만 14세 미만 이용자는 가입할 수 없습니다. ", HttpStatus.CONFLICT),
    POPUP_NOT_FOUND(404, "V044", "존재하지 않는 팝업입니다.", HttpStatus.NOT_FOUND),
    MINIMUM_AMOUNT_NOT_MET(400, "V045", "첫 구매 딜 구매조건(1만원)을 확인해주세요.", HttpStatus.CONFLICT),
    SOLD_OUT_PRODUCT_REMOVED(400, "V046", "품절된 상품을 삭제 후 주문해 주세요.", HttpStatus.BAD_REQUEST),
    BANNER_NOT_FOUND(404, "V047", "존재하지 않는 배너입니다.", HttpStatus.NOT_FOUND),
    EXPERIENCE_NOT_FOUND(404, "V048", "존재하지 않는 체험단입니다.", HttpStatus.NOT_FOUND),
    ALREADY_REGISTER_EXPERIENCE(409, "V049", "이미 신청하신 체험단입니다.", HttpStatus.CONFLICT),
    LIMIT_REGISTER_EXPERIENCE(409, "V050", "신청자 초과로 마감되었습니다. 다른 체험으로 돌아오겠습니다. 감사합니다", HttpStatus.CONFLICT),
    ALREADY_FINISH_EXPERIENCE(409, "V051", "이미 종료된 체험단입니다.", HttpStatus.CONFLICT),
    DO_NOT_REGISTER_EXPERIENCE(409, "V052", "고객님의 신청 내역이 없는 체험단입니다.", HttpStatus.CONFLICT),
    ONLY_FIRST_PURCHASE_USER_ITEM(409, "V053", "해당 상품은 첫 구매딜 전용 상품입니다", HttpStatus.CONFLICT),
    PLEASE_ORDER_BUY_PRODUCT(409, "V054", "상품을 고르고 주문해주세요 감사합니다.", HttpStatus.CONFLICT),
    EXPERIENCE_PRODUCT_MAX_COUNT_ZERO(409, "V055", "해당 상품은 최대 1개까지 구매할 수 있습니다.", HttpStatus.CONFLICT),
    EXPERIENCE_PRODUCT_TOTALLY_COUNT_FOUR(409, "V056", "0원 샘플 상품은 1회 주문 당 4개까지 구매 가능합니다.", HttpStatus.CONFLICT),
    PURCHASE_CONDITION_PRODUCT(409, "V057", "해당 제품은 1만원 최소 구매 조건을 달성해야합니다", HttpStatus.CONFLICT),
    DISPLAY_NOT_FOUND(404, "V058", "존재하지 않는 기획전입니다", HttpStatus.NOT_FOUND),
    LOTTO_APPLY_FINISH(400, "V059", "응모가 완료되었습니다", HttpStatus.CONFLICT),
    TODAY_ALREADY_REGISTER_LOTTO(400, "V060", "오늘 이미 신청 완료되었습니다!! 감사합니다.", HttpStatus.CONFLICT),
    FIRST_DEAL_PRODUCT_LIMIT(400, "V061", "첫 구매 딜 상품은 한 가지 상품만 구매할 수 있습니다.", HttpStatus.CONFLICT),
    IS_BEFORE_WEEKLY_PRICE_PRODUCT(400, "V062", "오늘의 특가 진행 예정 상품은 장바구니에 담을 수 없습니다.", HttpStatus.CONFLICT),
    NOTIFICATION_AGREE_ISSUE(404, "V063", "알림 설정 관련해서 고객센터로 문의바랍니다.", HttpStatus.NOT_FOUND),
    ORDERS_ITEM_NOT_FOUND(404, "V064", "주문내역에 존재하지 않는 상품입니다.", HttpStatus.NOT_FOUND),
    RECOMMEND_SURVEY_NOT_FOUND(404, "V065", "설문조사에 먼저 참여해주세요", HttpStatus.NOT_FOUND),
    ZERO_EXPERIENCE_QUESTION_SURVEY_NOT_FOUND(404, "V066", "존재하지 않는 설문 조사입니다. 관리자에게 문의해주세요", HttpStatus.NOT_FOUND),
    ANSWER_IDS_NOT_FOUND(404, "V067", "정해진 답변을 골라주세요. 관리자에게 문의하세요", HttpStatus.NOT_FOUND),
    QUESTION_SURVEY_EXISTS(400, "V068", "샘플 서베이를 진행해 주세요.", HttpStatus.CONFLICT),
    ALREADY_REGISTER_LOTTO(400, "V069", "이미 참여하신 이벤트입니다!! 감사합니다.", HttpStatus.CONFLICT),
    DO_NOT_MEMBER_REGISTER_LOTTO(400, "V070", "이벤트 응모 대상자가 아닙니다.", HttpStatus.CONFLICT),
    GROUP_PURCHASE_ROOM_FULL(400, "V071", "", HttpStatus.CONFLICT),
    GROUP_PURCHASE_ALREADY(400, "V072", "", HttpStatus.CONFLICT),
    NOT_REGISTER_GROUP_PURCHASE_ROOM(400, "V073", "존재하지 않는 팀 구매 방입니다. 다른 방에 참여해주세요!", HttpStatus.CONFLICT),
    ALREADY_REGISTER_GROUP_PURCHASE_ROOM(400, "V074", "이미 참여중이신 팀 구매방입니다. 다른 방에 참여해주세요!", HttpStatus.CONFLICT),
    ERROR_GROUP_PURCHASE_ROOM(400, "V075", "팀 구매 에러입니다. 고객센터에 문의주세요!", HttpStatus.CONFLICT),
    PLEASE_VERSION_UPDATE(400, "V076", "팀구매 상품 구매를 위해 최신 버전으로 업데이트를 진행해 주세요!", HttpStatus.CONFLICT),
    NECESSARY_VERSION_UPDATE(400, "V077", "업데이트를 위해 앱스토어로 이동합니다", HttpStatus.CONFLICT),
    UNNECESSARY_VERSION_UPDATE(400, "V078", "새로운 업데이트 소식이 있어요 업데이트를 진행해 주세요!", HttpStatus.CONFLICT),
    GROUP_PURCHASE_PRODUCT_NOT_ADD_CART(400, "V079", "팀구매 상품은 장바구니에 담을 수 없습니다", HttpStatus.CONFLICT),
    EMERGENCY_REPAIR(503, "E001", "긴급 점검", HttpStatus.INTERNAL_SERVER_ERROR),
    APPLE_LOGIN_USER(400,"V080","애플 로그인 유저입니다. 애플 로그인을 해주세요",HttpStatus.CONFLICT),
    KAKAO_LOGIN_USER(400,"V081","카카오 로그인 유저입니다. 카카오 로그인을 해주세요",HttpStatus.CONFLICT),
    IS_WEEKLY_PRICE_PRODUCT(400, "V062", "오늘의 특가 진행 상품은 진행 기간에만 장바구니에 담을 수 있습니다.", HttpStatus.CONFLICT),
    PURCHASE_ZERO_PERFUME_PRODUCT_CONDITION(400, "V082", "0원 향수는 최소 1만원 구매조건이 있습니다.",HttpStatus.CONFLICT);

    private int statusCode;
    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}

