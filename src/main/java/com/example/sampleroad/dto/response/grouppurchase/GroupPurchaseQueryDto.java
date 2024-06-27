package com.example.sampleroad.dto.response.grouppurchase;

import com.example.sampleroad.domain.grouppurchase.GroupPurchaseType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor
@Getter
public class GroupPurchaseQueryDto {

    private int productNo;
    private String productName;
    private String deadLineTime;
    private String imageUrl;
    private GroupPurchaseType memberRoomType;
    private Long memberId;
    private boolean isFull;

    @QueryProjection
    public GroupPurchaseQueryDto(int productNo, String productName,
                                 String deadLineTime, String imageUrl,
                                 GroupPurchaseType memberRoomType,
                                 Long memberId, boolean isFull) {
        this.productNo = productNo;
        this.productName = productName;
        this.deadLineTime = formatOrderDate(deadLineTime);
        this.imageUrl = imageUrl;
        this.memberRoomType = memberRoomType;
        this.memberId = memberId;
        this.isFull = isFull;
    }

    public GroupPurchaseQueryDto(int productNo, String productName, String deadLineTime, Long memberId, boolean isFull) {
        this.productNo = productNo;
        this.productName = productName;
        this.deadLineTime = deadLineTime;
        this.memberId = memberId;
        this.isFull = isFull;
    }

    public GroupPurchaseQueryDto(int productNo, String productName, String deadLineTime) {
        this.productNo = productNo;
        this.productName = productName;
        this.deadLineTime = deadLineTime;
    }

    public GroupPurchaseQueryDto(int productNo, String productName, String deadLineTime, Long memberId) {
        this.productNo = productNo;
        this.productName = productName;
        this.deadLineTime = deadLineTime;
        this.memberId = memberId;
    }

    @NoArgsConstructor
    @Getter
    public static class MemberProfileQueryDto extends GroupPurchaseQueryDto {
        private Long roomId;
        private String memberProfileImgUrl;
        private String memberNickName;

        @QueryProjection
        public MemberProfileQueryDto(int productNo, String productName, String deadLineTime,
                                     Long roomId, String memberProfileImgUrl, String memberNickName,
                                     Long memberId, boolean isFull) {
            super(productNo, productName, deadLineTime, memberId, isFull);
            this.roomId = roomId;
            this.memberProfileImgUrl = memberProfileImgUrl;
            this.memberNickName = memberNickName;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class GroupPurchaseOrderQueryDto extends GroupPurchaseQueryDto {
        private String orderNo;

        @QueryProjection
        public GroupPurchaseOrderQueryDto(int productNo, String productName, String deadLineTime,
                                          Long memberId, boolean isFull,
                                          String orderNo) {
            super(productNo, productName, deadLineTime, memberId, isFull);
            this.orderNo = orderNo;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class GroupPurchaseOrderInfoQueryDto extends GroupPurchaseQueryDto {
        private String orderNo;
        private Long roomId;
        private int roomCapacity;
        private String productImgUrl;

        @QueryProjection
        public GroupPurchaseOrderInfoQueryDto(int productNo, String productName,
                                              String deadLineTime, Long memberId,
                                              String orderNo,
                                              Long roomId, int roomCapacity, String productImgUrl) {
            super(productNo, productName, deadLineTime, memberId);
            this.orderNo = orderNo;
            this.roomId = roomId;
            this.roomCapacity = roomCapacity;
            this.productImgUrl = productImgUrl;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class RoomInfo {
        private Long roomId;
        private int productNo;

        @QueryProjection
        public RoomInfo(Long roomId, int productNo) {
            this.roomId = roomId;
            this.productNo = productNo;
        }
    }

    public static String formatOrderDate(String originalDate) {
        if (originalDate != null) {
            // Desired format
            DateTimeFormatter desiredFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            // Parsing the original string to LocalDateTime
            LocalDateTime dateTime = LocalDateTime.parse(originalDate, desiredFormat);

            // Return the formatted date
            return dateTime.format(desiredFormat);
        } else {
            return null;
        }
    }

}
