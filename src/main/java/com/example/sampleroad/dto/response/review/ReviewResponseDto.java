package com.example.sampleroad.dto.response.review;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@NoArgsConstructor
@Getter
public class ReviewResponseDto {

    @NoArgsConstructor
    @Getter
    public static class ReviewByMember {
        private int reviewNo;
        private int productNo;
        private String productName;
        private String imageUrl;
        private String brandName;
        private String optionTitle;
        private int orderOptionNo;
        private Double rate;
        private String registerDate;
        private String[] reviewTags;
        private String[] reviewImageUrls;
        private String content;
        private int recommendCnt;
        private boolean isLikedReview;

        public boolean getIsLikedReview() {
            return isLikedReview;
        }

        public ReviewByMember(int reviewNo, int productNo, String productName, String imageUrl,
                              String brandName, String optionTitle, int orderOptionNo,
                              Double rate, String registerDate,
                              String[] reviewTags, String[] reviewImageUrls, String content, int recommendCnt) {
            this.reviewNo = reviewNo;
            this.productNo = productNo;
            this.productName = productName;
            this.imageUrl = imageUrl;
            this.brandName = brandName;
            this.optionTitle = optionTitle;
            this.orderOptionNo = orderOptionNo;
            this.rate = rate;
            this.registerDate = registerDate;
            this.reviewTags = reviewTags;
            this.reviewImageUrls = reviewImageUrls;
            this.content = content;
            this.recommendCnt = recommendCnt;
        }

        public ReviewByMember(ReviewByMember reviewByMember, String[] reviewTags, boolean hasHeart) {
            this.reviewNo = reviewByMember.getReviewNo();
            this.productNo = reviewByMember.getProductNo();
            this.productName = reviewByMember.getProductName();
            this.imageUrl = reviewByMember.getImageUrl();
            this.brandName = reviewByMember.getBrandName();
            this.optionTitle = reviewByMember.getOptionTitle();
            this.orderOptionNo = reviewByMember.getOrderOptionNo();
            this.rate = reviewByMember.getRate();
            this.registerDate = reviewByMember.getRegisterDate();
            this.reviewTags = reviewTags;
            this.reviewImageUrls = reviewByMember.getReviewImageUrls();
            this.content = reviewByMember.getContent();
            this.recommendCnt = reviewByMember.getRecommendCnt();
            this.isLikedReview = hasHeart;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class ReviewByMemberAndTotalCount {
        private int totalCount;
        private List<ReviewByMember> items;

        public ReviewByMemberAndTotalCount(int totalCount, List<ReviewByMember> items) {
            this.totalCount = totalCount;
            this.items = items;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class Reviewable {
        private String productName;
        private String brandName;
        private int productNo;
        private int orderOptionNo;
        private int optionNo;
        private String optionTitle;
        private String orderNo;
        private String imageUrl;

        public Reviewable(String productName, String brandName, int orderOptionNo, int optionNo
                , String optionTitle, String orderNo, int productNo, String imageUrl) {
            this.productName = productName;
            this.brandName = brandName;
            this.productNo = productNo;
            this.orderOptionNo = orderOptionNo;
            this.optionNo = optionNo;
            this.optionTitle = optionTitle;
            this.orderNo = orderNo;
            this.imageUrl = imageUrl;
        }
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class NewReviewable {
        private Long reviewId;
        private String productName;
        private String brandName;
        private Long productId;
        private int productNo;
        private int orderOptionNo;
        private int optionNo;
        private String optionTitle;
        private String orderNo;
        private String imageUrl;
        private String orderDate;

        public NewReviewable(String productName, String brandName,
                             int orderOptionNo, int optionNo,
                             String optionTitle, String orderNo,
                             int productNo, String imageUrl, String orderDate) {
            this.productName = productName;
            this.brandName = brandName;
            this.productNo = productNo;
            this.orderOptionNo = orderOptionNo;
            this.optionNo = optionNo;
            this.optionTitle = optionTitle;
            this.orderNo = orderNo;
            this.imageUrl = imageUrl;
            this.orderDate = orderDate;
        }

        public NewReviewable(Long reviewId, Long productId, String productName, String brandName,
                             int productNo, int orderOptionNo, String imageUrl, String orderDate) {
            this.reviewId = reviewId;
            this.productId = productId;
            this.productName = productName;
            this.brandName = brandName;
            this.productNo = productNo;
            this.orderOptionNo = orderOptionNo;
            this.imageUrl = imageUrl;
            this.orderDate = orderDate;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class NewReviewableDto {
        private Long reviewId;
        private String productName;
        private String brandName;
        private int productNo;
        private int orderOptionNo;
        private int optionNo;
        private String imageUrl;
        private String orderDate;
        private int reviewNo;
        private Double rate;
        private String[] reviewTags;
        private String[] reviewImageUrls;
        private String content;

        public NewReviewableDto(Long reviewId, ReviewByMember reviewByMember, String[] reviewTags) {
            this.reviewId = reviewId;
            this.reviewNo = reviewByMember.getReviewNo();
            this.productNo = reviewByMember.getProductNo();
            this.orderOptionNo = reviewByMember.getOrderOptionNo();
            this.productName = reviewByMember.getProductName();
            this.imageUrl = reviewByMember.getImageUrl();
            this.orderDate = formatOrderDate(reviewByMember.getRegisterDate());
            this.brandName = reviewByMember.getBrandName();
            this.rate = reviewByMember.getRate();
            this.reviewTags = reviewTags;
            this.reviewImageUrls = reviewByMember.getReviewImageUrls();
            this.content = reviewByMember.getContent();
        }

        public NewReviewableDto(Long reviewId, String productName, String brandName,
                                int orderOptionNo, int optionNo, int productNo,
                                String imageUrl, String orderDate) {
            this.reviewId = reviewId;
            this.productName = productName;
            this.brandName = brandName;
            this.orderOptionNo = orderOptionNo;
            this.optionNo = optionNo;
            this.productNo = productNo;
            this.imageUrl = imageUrl;
            this.orderDate = formatOrderDate(orderDate);
        }
    }

    public static String formatOrderDate(String originalDate) {
        if (originalDate != null) {
            // Define the original and target date formats
            DateTimeFormatter originalFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            DateTimeFormatter targetFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

            // Parse the original date string
            LocalDateTime dateTime = LocalDateTime.parse(originalDate, originalFormatter);

            // Format it into the new pattern
            return dateTime.format(targetFormatter);
        } else {
            return null;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class NewReviewableAndTotalCountDto {
        private int totalCount;
        private String noticeImageUrl;
        private List<NewReviewableDto> items;

        public NewReviewableAndTotalCountDto(int totalCount, String noticeImageUrl, List<NewReviewableDto> items) {
            this.totalCount = totalCount;
            this.noticeImageUrl = noticeImageUrl;
            this.items = items;
        }
    }


    @NoArgsConstructor
    @Getter
    public static class ReviewableTags {
        String noticeImageUrl;
        List<String> reviewableTags;

        public ReviewableTags(String noticeImageUrl, List<String> reviewableTags) {
            this.noticeImageUrl = noticeImageUrl;
            this.reviewableTags = reviewableTags;
        }
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class ReviewableAndTotalCount {
        private int totalCount;
        private List<Reviewable> items;

        public ReviewableAndTotalCount(int totalCount, List<Reviewable> items) {
            this.totalCount = totalCount;
            this.items = items;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class NewReviewableAndTotalCount {
        private int totalCount;
        private List<NewReviewable> items;

        public NewReviewableAndTotalCount(int totalCount, List<NewReviewable> items) {
            this.totalCount = totalCount;
            this.items = items;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class ProductReviewAll {
        private String reviewTitle;
        private Long totalCount;
        private double rate;
        private List<ProductReviewInfo> items;
        private String reviewLink;

        public ProductReviewAll(String reviewTitle, Long totalCount, double rate, List<ProductReviewInfo> items,
                                String reviewLink) {
            this.reviewTitle = reviewTitle;
            this.totalCount = totalCount;
            this.rate = rate;
            this.items = items;
            this.reviewLink = reviewLink;
        }

        public ProductReviewAll(Long totalCount, double rate, List<ProductReviewInfo> items) {
            this.totalCount = totalCount;
            this.rate = rate;
            this.items = items;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class ProductReviewInfo {
        private boolean myReview;
        private String productImgUrl;
        private String brandName;
        private String productName;
        private int productNo;
        private int reviewNo;
        private double reviewRate;
        private String registerDate;
        private String registerNo;
        private Long orderOptionNo;
        private String[] tags;
        private String content;
        private int recommendCnt;
        private String[] reviewImgUrls;
        private UserInfo userInfo;

        public boolean getMyReview() {
            return myReview;
        }

        public ProductReviewInfo(boolean myReview, String productImgUrl, String brandName, String productName,
                                 int productNo, int reviewNo, double reviewRate, String registerDate,
                                 String registerNo, String[] tags, String content, int recommendCnt,
                                 String[] reviewImgUrls) {
            this.myReview = myReview;
            this.productImgUrl = productImgUrl;
            this.brandName = brandName;
            this.productName = productName;
            this.productNo = productNo;
            this.reviewNo = reviewNo;
            this.reviewRate = reviewRate;
            this.registerDate = registerDate;
            this.registerNo = registerNo;
            this.tags = tags;
            this.content = content;
            this.recommendCnt = recommendCnt;
            this.reviewImgUrls = reviewImgUrls;
        }

        public ProductReviewInfo(boolean myReview, String productImgUrl, String brandName, String productName,
                                 int productNo, int reviewNo, double reviewRate, String registerDate,
                                 String registerNo, Long orderOptionNo, String[] tags, String content, int recommendCnt,
                                 String[] reviewImgUrls) {
            this.myReview = myReview;
            this.productImgUrl = productImgUrl;
            this.brandName = brandName;
            this.productName = productName;
            this.productNo = productNo;
            this.reviewNo = reviewNo;
            this.reviewRate = reviewRate;
            this.registerDate = registerDate;
            this.registerNo = registerNo;
            this.orderOptionNo = orderOptionNo;
            this.tags = tags;
            this.content = content;
            this.recommendCnt = recommendCnt;
            this.reviewImgUrls = reviewImgUrls;
        }

        public ProductReviewInfo(ReviewQueryDto.ReviewInfo productReviewInfo, boolean myReview,
                                 String[] reviewImgUrls, String[] reviewTags, UserInfo userInfo) {
            this.myReview = myReview;
            this.productImgUrl = productReviewInfo.getProductImgUrl();
            this.brandName = productReviewInfo.getBrandName();
            this.productName = productReviewInfo.getProductName();
            this.productNo = productReviewInfo.getProductNo();
            this.reviewNo = productReviewInfo.getReviewNo();
            this.reviewRate = productReviewInfo.getReviewRate();
            this.registerDate = productReviewInfo.getRegisterDate();
            this.registerNo = productReviewInfo.getMemberNo();
            this.tags = reviewTags;
            this.content = productReviewInfo.getContent();
            this.recommendCnt = productReviewInfo.getRecommendCnt();
            this.reviewImgUrls = reviewImgUrls;
            this.userInfo = userInfo;
        }


    }

    @NoArgsConstructor
    @Getter
    public static class UserInfo {
        private String profileImgUrl;
        private String nickName;
        private String skinType;
        private String[] skinTrouble;
        private Boolean isLikedReview;

        public UserInfo(String profileImgUrl, String nickName, String skinType, String[] skinTrouble,
                        Boolean isLikedReview) {
            this.profileImgUrl = profileImgUrl;
            this.nickName = nickName;
            this.skinType = skinType;
            this.skinTrouble = skinTrouble;
            this.isLikedReview = isLikedReview;
        }

        public boolean getIsLikedReview() {
            return isLikedReview;
        }

        public void setIsLikedReview(boolean likedReview) {
            isLikedReview = likedReview;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class AddReview {
        private Long reviewId;

        public AddReview(Long reviewId) {
            this.reviewId = reviewId;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class PhotoReviewInfo {
        private int reviewNo;
        private int recommendCnt;
        private String imgUrl;
        private String[] imgUrls;

        public PhotoReviewInfo(int reviewNo, int recommendCnt, String[] imgUrls) {
            this.reviewNo = reviewNo;
            this.recommendCnt = recommendCnt;
            this.imgUrls = imgUrls;
        }

        public PhotoReviewInfo(int reviewNo, int recommendCnt, String imgUrl) {
            this.reviewNo = reviewNo;
            this.recommendCnt = recommendCnt;
            this.imgUrl = imgUrl;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class PhotoReview {
        private int reviewNo;
        private int recommendCnt;
        private String imgUrl;

        public PhotoReview(int reviewNo, int recommendCnt, String imgUrl) {
            this.reviewNo = reviewNo;
            this.recommendCnt = recommendCnt;
            this.imgUrl = imgUrl;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class PhotoReviewList {
        private int totalCount;
        private List<PhotoReview> photoReviewList;

        public PhotoReviewList(int totalCount, List<PhotoReview> photoReviewList) {
            this.totalCount = totalCount;
            this.photoReviewList = photoReviewList;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class NewReviewProductInfo {
        private String productImageUrl;
        private String brandName;
        private String productName;
        private int productOptionNo;
        private String noticeImageUrl;
        private List<String> reviewableTags;

        public NewReviewProductInfo(String productImageUrl, String brandName,
                                    String productName, int productOptionNo,
                                    String noticeImageUrl, List<String> reviewableTags) {
            this.productImageUrl = productImageUrl;
            this.brandName = brandName;
            this.productName = productName;
            this.productOptionNo = productOptionNo;
            this.noticeImageUrl = noticeImageUrl;
            this.reviewableTags = reviewableTags;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class NewWrittenReviewInfo extends NewReviewProductInfo {
        private Long reviewId;
        private Double reviewRate;
        private Long orderOptionNo;
        private String[] selectedTags;
        private String contents;
        private String[] reviewImageUrls;

        public NewWrittenReviewInfo(NewReviewProductInfo reviewProductInfo) {
            super(reviewProductInfo.getProductImageUrl(), reviewProductInfo.getBrandName(),
                    reviewProductInfo.getProductName(), reviewProductInfo.getProductOptionNo(),
                    reviewProductInfo.getNoticeImageUrl(), reviewProductInfo.getReviewableTags());
        }

        public NewWrittenReviewInfo(NewReviewProductInfo reviewProductInfo,
                                    Long reviewId,
                                    Double reviewRate, Long orderOptionNo,
                                    String[] selectedTags, String contents,
                                    String[] reviewImageUrls) {
            super(reviewProductInfo.getProductImageUrl(), reviewProductInfo.getBrandName(),
                    reviewProductInfo.getProductName(), reviewProductInfo.getProductOptionNo(),
                    reviewProductInfo.getNoticeImageUrl(), reviewProductInfo.getReviewableTags());
            this.reviewId = reviewId;
            this.reviewRate = reviewRate;
            this.orderOptionNo = orderOptionNo;
            this.selectedTags = selectedTags;
            this.contents = contents;
            this.reviewImageUrls = reviewImageUrls;
        }
    }

}
