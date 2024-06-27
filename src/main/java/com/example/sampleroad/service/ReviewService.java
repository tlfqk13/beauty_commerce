package com.example.sampleroad.service;

import com.example.sampleroad.common.exception.ErrorCode;
import com.example.sampleroad.common.exception.ErrorCustomException;
import com.example.sampleroad.common.utils.CustomValue;
import com.example.sampleroad.common.utils.ShopBy;
import com.example.sampleroad.domain.CategoryType;
import com.example.sampleroad.domain.NoticeType;
import com.example.sampleroad.domain.member.Member;
import com.example.sampleroad.domain.product.Product;
import com.example.sampleroad.domain.review.Review;
import com.example.sampleroad.domain.review.ReviewCrawling;
import com.example.sampleroad.domain.review.ReviewPhoto;
import com.example.sampleroad.domain.review.ReviewTagType;
import com.example.sampleroad.dto.request.ReviewRequestDto;
import com.example.sampleroad.dto.response.product.ProductDetailResponseDto;
import com.example.sampleroad.dto.response.review.ReviewCrawlingResponseDto;
import com.example.sampleroad.dto.response.review.ReviewPhotoQueryDto;
import com.example.sampleroad.dto.response.review.ReviewQueryDto;
import com.example.sampleroad.dto.response.review.ReviewResponseDto;
import com.example.sampleroad.dto.response.survey.SurveyQueryDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.example.sampleroad.repository.member.MemberRepository;
import com.example.sampleroad.repository.product.ProductRepository;
import com.example.sampleroad.repository.review.ReviewCrawlingRepository;
import com.example.sampleroad.repository.review.ReviewPhotoRepository;
import com.example.sampleroad.repository.review.ReviewRepository;
import com.example.sampleroad.repository.survey.SurveyRepository;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ReviewService {

    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewPhotoRepository reviewPhotoRepository;
    private final ProductRepository productRepository;
    private final SurveyRepository surveyRepository;
    private final ReviewCrawlingRepository reviewCrawlingRepository;
    private final ReviewHeartService reviewHeartService;
    private final NoticeService noticeService;
    private final ReviewShopByService reviewShopByService;

    @Value("${shop-by.client-id}")
    String clientId;
    @Value("${shop-by.version-header}")
    String versionHeader;
    @Value("${shop-by.platform-header}")
    String platformHeader;
    @Value("${shop-by.url}")
    String shopByUrl;
    @Value("${shop-by.check-member-url}")
    String profile;
    @Value("${shop-by.server-profile}")
    String serverProfile;

    @Value("${shop-by.kit-category-no}")
    int kitCategoryNo;

    Gson gson = new Gson();

    @Transactional
    public ReviewResponseDto.AddReview addReview(ReviewRequestDto dto, UserDetailsImpl userDetails) throws UnirestException, ParseException {
        Member member = getMember(userDetails);
        int reviewNo;
        Boolean isSampleroadReview = false;
        if (dto.getIsSampleroadReview() != null) {
            isSampleroadReview = dto.getIsSampleroadReview();
        }

        // 특정 인원들은 물품을 구매하지않아도 작성할 수 있게
        if (isSampleroadReview) {
            Random rand = new Random();
            do {
                reviewNo = rand.nextInt(1998789) + 1;
            } while (reviewRepository.existsByReviewNo(reviewNo));
        } else {
            reviewNo = reviewShopByService.shopByAddReview(dto, member.getShopByAccessToken());
        }

        String reviewTags = String.join(",", dto.getReviewTags());
        Product product = getProduct(dto.getProductNo());

        Review review = Review.builder()
                .tag(reviewTags)
                .content(dto.getContent())
                .reviewNo(reviewNo)
                .orderOptionNo(dto.getOrderOptionNo())
                .reviewRate(dto.getRate())
                .product(product)
                .member(member)
                .build();

        // 리뷰에 사진이 있는 경우 ReviewPhoto에 저장
        String[] urls = dto.getUrls();

        for (String reviewPhotoUrl : urls) {
            ReviewPhoto reviewPhoto = ReviewPhoto.builder()
                    .reviewPhotoUrl(reviewPhotoUrl)
                    .review(review)
                    .build();
            reviewPhotoRepository.save(reviewPhoto);
        }

        Review saveReview = reviewRepository.save(review);

        return new ReviewResponseDto.AddReview(saveReview.getId());
    }


    @Transactional
    public void modifyReview(Long reviewId, ReviewRequestDto.Update dto, UserDetailsImpl userDetails) throws UnirestException, ParseException {

        Review review = getReview(reviewId);
        // TODO: 2023/11/13 자체 review인지 조회
        if (dto.getContent() == null) {
            log.info("리뷰 별표만 수정하는 Req..................S");

            List<ReviewPhotoQueryDto.ReviewPhoto> reviewPhotos = reviewPhotoRepository.findReviewPhotoByReviewNo(review.getReviewNo());
            List<String> urlsList = new ArrayList<>();

            for (ReviewPhotoQueryDto.ReviewPhoto photo : reviewPhotos) {
                urlsList.add(photo.getReviewImgUrl()); // 가정: getReviewImgUrl() 메소드가 URL을 반환
            }

            String[] urls = urlsList.toArray(new String[0]); // 크기 0의 배열을 전달하여 크기 맞춤이 자동으로 이루어지도록 함

            ReviewRequestDto.Update reviewRateDto = new ReviewRequestDto.Update(
                    dto.getRate(),
                    review.getContent(),
                    urls
            );
            log.info("리뷰 별표만 수정하는 Req..................E");
            reviewShopByService.shopByReviewUpdate(reviewRateDto, userDetails.getMember().getShopByAccessToken(), review.getProduct().getProductNo(), review.getReviewNo());
        } else {
            reviewShopByService.shopByReviewUpdate(dto, userDetails.getMember().getShopByAccessToken(), review.getProduct().getProductNo(), review.getReviewNo());
            List<ReviewPhotoQueryDto.ReviewPhoto> reviewPhotoByReviewNo =
                    reviewPhotoRepository.findReviewPhotoByReviewNo(review.getReviewNo(), userDetails.getMember().getId());

            String reviewTags = String.join(",", dto.getReviewTags());
            // 리뷰 사진 수정시 포토리뷰 db도 업데이트 해줘야함
            review.updateReview(dto.getContent(), reviewTags);

            // getUrl에는 있는데 reviewPhotoByReviewNo에 없는 url
            List<ReviewPhotoQueryDto.ReviewPhoto> missingReviewPhotos = reviewPhotoByReviewNo.stream()
                    .filter(reviewPhoto -> Arrays.stream(dto.getUrls())
                            .noneMatch(url -> url.equals(reviewPhoto.getReviewImgUrl())))
                    .collect(Collectors.toList());

            // reviewPhotoByReviewNo에 있는데 getUrl에는 없는 url
            List<String> missingUrls = Arrays.stream(dto.getUrls())
                    .filter(url -> reviewPhotoByReviewNo.stream()
                            .noneMatch(reviewPhoto -> url.equals(reviewPhoto.getReviewImgUrl())))
                    .collect(Collectors.toList());

            List<Long> reviewPhotoIds = missingReviewPhotos.stream()
                    .map(ReviewPhotoQueryDto.ReviewPhoto::getReviewId)
                    .collect(Collectors.toList());

            if (!reviewPhotoIds.isEmpty()) {
                reviewPhotoRepository.deleteAllByReviewIdInQuery(reviewPhotoIds);
            }

            for (String reviewPhotoUrl : missingUrls) {
                ReviewPhoto reviewPhoto = ReviewPhoto.builder()
                        .reviewPhotoUrl(reviewPhotoUrl)
                        .review(review)
                        .build();
                reviewPhotoRepository.save(reviewPhoto);
            }
        }

    }

    /**
     * 리뷰 삭제
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 4/11/24
     **/
    @Transactional
    public void removeReview(Long reviewId, UserDetailsImpl userDetails) throws UnirestException, ParseException {

        Review review = getReview(reviewId);
        if (!review.getIsSampleRoadReview()) {
            reviewShopByService.shopByDeleteReview(userDetails.getMember().getShopByAccessToken(), review.getProduct().getProductNo(), review.getReviewNo());
        }
        reviewRepository.deleteById(review.getId());
    }

    /**
     * 리뷰 추천하기
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 4/11/24
     **/
    @Transactional
    public void recommendReviewByReviewNo(int reviewNo, UserDetailsImpl userDetails) throws UnirestException, ParseException {
        Review review = getReview(reviewNo);

        // TODO: 2023/11/13 자체 리뷰 좋아요 관리
        if (!review.getIsSampleRoadReview()) {
            reviewShopByService.shopByReviewRecommend(userDetails.getMember().getShopByAccessToken(), review.getProduct().getProductNo(), reviewNo);
        }

        review.updateReviewRecommendCount(true, review.getRecommendCount());
        reviewHeartService.addHeartByReviewMember(review, userDetails.getMember());
    }

    /**
     * 리뷰 추천 취소하기
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 4/11/24
     **/
    @Transactional
    public void unRecommendReviewByReviewNo(int reviewNo, UserDetailsImpl userDetails) throws UnirestException, ParseException {
        Review review = getReview(reviewNo);

        // TODO: 2023/11/13 자체 리뷰 좋아요 취소 관리
        if (!review.getIsSampleRoadReview()) {
            reviewShopByService.shopByReviewUnRecommend(userDetails.getMember().getShopByAccessToken(), review.getProduct().getProductNo(), reviewNo);
        }

        review.updateReviewRecommendCount(false, review.getRecommendCount());
        reviewHeartService.deleteHeartByReviewMember(review, userDetails.getMember());
    }

    /**
     * 상품상세 -> 외부 쇼핑몰 리뷰 보기
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 1/19/24
     **/
    public ReviewCrawlingResponseDto getProductReviewCrawling(int productNo) {
        Optional<ReviewCrawling> reviewCrawling = reviewCrawlingRepository.findByProductNo(productNo);
        return reviewCrawling.map(crawling -> new ReviewCrawlingResponseDto(crawling.getReviewLink())).orElseGet(ReviewCrawlingResponseDto::new);
    }

    @Transactional
    public void reportReview(int reviewNo, ReviewRequestDto.Report dto, UserDetailsImpl userDetails) throws UnirestException, ParseException {

        Review review = getReview(reviewNo);

        // TODO: 2023/11/13 자체 리뷰 신고 관리
        if (!review.getIsSampleRoadReview()) {
            reviewShopByService.shopByReviewReport(dto, userDetails.getMember().getShopByAccessToken(), review.getProduct().getProductNo());
        }

        review.updateReviewReport(true);
    }

    public ReviewResponseDto.ReviewByMemberAndTotalCount getMemberWrittenReview(int pageNumber, int pageSize, String startYmd, UserDetailsImpl userDetails) throws UnirestException, ParseException {

        // Shopby API를 이용하여 해당 사용자가 작성한 리뷰 목록과 총 개수를 가져옵니다.
        ReviewResponseDto.ReviewByMemberAndTotalCount reviewByMemberAndTotalCount = reviewShopByService.shopbyGetWrittenReview(userDetails.getMember().getShopByAccessToken(), pageNumber, pageSize, startYmd);

        // 가져온 리뷰 목록에서 리뷰 번호들을 수집합니다.
        List<Integer> reviewNoList = reviewByMemberAndTotalCount.getItems().stream()
                .map(ReviewResponseDto.ReviewByMember::getReviewNo)
                .collect(Collectors.toList());

        // 데이터베이스에서 해당 사용자가 작성한 리뷰들을 가져옵니다.
        List<Review> reviews = reviewRepository.findAllByMemberIdAndReviewNoInOrderByIdDesc(userDetails.getMember().getId(), reviewNoList);

        // 해당 사용자의 좋아요 여부를 확인합니다.
        Set<Long> reviewIdsWithHeart = reviewHeartService.findByMemberIdInAndReviewNoIn(userDetails.getMember().getId(), reviewNoList);

        // 리뷰 목록과 데이터베이스에서 가져온 리뷰들을 병합하여 새로운 리스트에 저장합니다.
        List<ReviewResponseDto.ReviewByMember> mergedList = reviewByMemberAndTotalCount.getItems().stream()
                .filter(reviewByMember -> reviews.stream().anyMatch(review -> review.getReviewNo() == reviewByMember.getReviewNo()))
                .map(reviewByMember -> {
                    Review review = reviews.stream().filter(r -> r.getReviewNo() == reviewByMember.getReviewNo()).findFirst()
                            .orElseThrow(() -> new ErrorCustomException(ErrorCode.REVIEW_NOT_FOUND));
                    String tags = review.getTag();
                    String[] reviewTags = (tags != null && !tags.isEmpty()) ? tags.split(",") : new String[0];

                    boolean hasHeart = reviewIdsWithHeart.contains(review.getId());

                    return new ReviewResponseDto.ReviewByMember(reviewByMember, reviewTags, hasHeart);
                })
                .collect(Collectors.toList());

        return new ReviewResponseDto.ReviewByMemberAndTotalCount(reviewByMemberAndTotalCount.getTotalCount(), mergedList);
    }

    public ReviewResponseDto.ReviewableAndTotalCount getReviewableItems(UserDetailsImpl userDetails, int pageNumber, int pageSize) throws UnirestException, ParseException {

        // TODO: 2023/10/30 특정 사람들은 물건을 사지 않아도 작성할 수 있는 리뷰에 조회되도록.
        // 관리자 KIT 수가 많아지면 시간복잡도 증가 가능성 높음
        List<ProductDetailResponseDto.SampleList> sampleList = productRepository.findSampleList(kitCategoryNo);

        List<Integer> excludedProductNos = sampleList.stream()
                .map(ProductDetailResponseDto.SampleList::getProductNo)
                .collect(Collectors.toList());

        ReviewResponseDto.ReviewableAndTotalCount response = shopbyGetReviewableProduct(userDetails.getMember().getShopByAccessToken(), pageNumber, pageSize);

        // Remove items with productNos found in excludedProductNos
        response.getItems().removeIf(item -> excludedProductNos.contains(item.getProductNo()));

        return new ReviewResponseDto.ReviewableAndTotalCount(response.getItems().size(), response.getItems());
    }

    /**
     * 작성 가능한 리뷰 조회
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 4/11/24
     **/
    public ReviewResponseDto.NewReviewableAndTotalCountDto getWritableReviewAll(UserDetailsImpl userDetails) throws UnirestException, ParseException {
        List<ReviewResponseDto.NewReviewableDto> response = new ArrayList<>();

        // TODO: 4/12/24 작성 가능한 리뷰 조회는 페이징 없이 전체 조회
        ReviewResponseDto.NewReviewableAndTotalCount shopbyResponse = reviewShopByService.shopByGetNewReviewableProduct(
                userDetails.getMember().getShopByAccessToken(), 0, 1000);

        List<ReviewResponseDto.NewReviewable> reviewableItems = mapProductIds(shopbyResponse.getItems());

        for (ReviewResponseDto.NewReviewable reviewableItem : reviewableItems) {
            if (reviewableItem.getReviewId() == null) {
                response.add(createNewReviewableDto(reviewableItem));
            }
        }

        String noticeImageUrl = noticeService.getNotice(NoticeType.SURVEY_POLICY);
        return new ReviewResponseDto.NewReviewableAndTotalCountDto(shopbyResponse.getTotalCount(), noticeImageUrl, response);
    }

    /**
     * 작성 완료한 리뷰 조회
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 4/11/24
     **/
    public ReviewResponseDto.NewReviewableAndTotalCountDto getWrittenReviewAll(int pageNumber, int pageSize, UserDetailsImpl userDetails) throws UnirestException, ParseException {

        List<ReviewResponseDto.NewReviewableDto> response = new ArrayList<>();

        // TODO: 2/1/24 이미 작성된 리뷰
        String startYmd = CustomValue.defaultStartYmd;
        ReviewResponseDto.ReviewByMemberAndTotalCount reviewByMemberAndTotalCount =
                reviewShopByService.shopbyGetWrittenReview(userDetails.getMember().getShopByAccessToken(), pageNumber, pageSize, startYmd);

        List<Integer> reviewNoList = reviewByMemberAndTotalCount.getItems().stream()
                .map(ReviewResponseDto.ReviewByMember::getReviewNo)
                .collect(Collectors.toList());

        List<Review> reviewList = reviewRepository.findAllByMemberIdAndReviewNoInOrderByIdDesc(
                userDetails.getMember().getId(), reviewNoList);

        List<Review> filteredReviewList = reviewList.stream()
                .filter(review -> reviewNoList.contains(review.getReviewNo()))
                .collect(Collectors.toList());

        // Merge only if filteredReviewList is not empty
        if (!filteredReviewList.isEmpty()) {
            response.addAll(createMergedList(reviewByMemberAndTotalCount, filteredReviewList));
        }

        String noticeImageUrl = noticeService.getNotice(NoticeType.SURVEY_POLICY);
        return new ReviewResponseDto.NewReviewableAndTotalCountDto(reviewByMemberAndTotalCount.getTotalCount(), noticeImageUrl, response);

    }


    private ReviewResponseDto.NewReviewableDto createNewReviewableDto(ReviewResponseDto.NewReviewable reviewableItem) {
        return new ReviewResponseDto.NewReviewableDto(
                null,
                reviewableItem.getProductName(),
                reviewableItem.getBrandName(),
                reviewableItem.getOrderOptionNo(),
                reviewableItem.getOptionNo(),
                reviewableItem.getProductNo(),
                reviewableItem.getImageUrl(),
                reviewableItem.getOrderDate()
        );
    }

    private List<ReviewResponseDto.NewReviewableDto> createMergedList(ReviewResponseDto.ReviewByMemberAndTotalCount reviewByMemberAndTotalCount, List<Review> filteredReviewList) {

        // Create a map for faster lookup
        Map<Integer, Review> reviewMap = filteredReviewList.stream()
                .collect(Collectors.toMap(Review::getReviewNo, Function.identity()));

        return reviewByMemberAndTotalCount.getItems().stream()
                .map(reviewByMember -> mapToNewReviewableDto(reviewByMember, reviewMap))
                .collect(Collectors.toList());
    }

    private ReviewResponseDto.NewReviewableDto mapToNewReviewableDto(
            ReviewResponseDto.ReviewByMember reviewByMember, Map<Integer, Review> reviewMap) {

        Review review = Optional.ofNullable(reviewMap.get(reviewByMember.getReviewNo()))
                .orElseThrow(() -> new ErrorCustomException(ErrorCode.REVIEW_NOT_FOUND));

        String tags = review.getTag();
        String[] reviewTags = (tags != null && !tags.isEmpty()) ? tags.split(",") : new String[0];

        return new ReviewResponseDto.NewReviewableDto(review.getId(), reviewByMember, reviewTags);
    }


    private List<ReviewResponseDto.NewReviewable> mapProductIds(List<ReviewResponseDto.NewReviewable> items) {
        List<Integer> productNos = items.stream().map(ReviewResponseDto.NewReviewable::getProductNo).collect(Collectors.toList());
        Map<Integer, Long> productNoMap = productRepository.findByProductNoIn(productNos).stream().collect(Collectors.toMap(Product::getProductNo, Product::getId));

        return items.stream()
                .peek(item -> item.setProductId(productNoMap.get(item.getProductNo())))
                .collect(Collectors.toList());
    }

    /**
     * 해당 상품에 대한 모든 리뷰를 조회
     * 리뷰를 조회하면서 조회하는 해당 유저가 좋아요를 했는지 안했는지 확인
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/06/14
     **/
    @Transactional
    public ReviewResponseDto.ProductReviewAll getProductReviewAll(int pageNumber, int pageSize, int productNo, UserDetailsImpl userDetails) throws UnirestException, ParseException {

        // 자체 db에서 리뷰에 대한 정보 가져옴
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<ReviewQueryDto.ReviewInfo> reviewInfoByProductNo = reviewRepository.findReviewInfoByProductNo(pageable, productNo);
        double roundedAverage = 0.0;
        // 리뷰 정보를 가져옵니다.
        Double reviewRateAvg = reviewRepository.findReviewRateAvg(productNo);
        if (reviewRateAvg != null) {
            roundedAverage = getRoundedAverage(reviewRateAvg);
        }

        // 리뷰 목록을 Map 형태로 변환하여 리뷰 번호를 Key로, Review 객체를 Value로 저장
        Map<Integer, ReviewQueryDto.ReviewInfo> reviewMap = reviewInfoByProductNo.stream()
                .collect(Collectors.toMap(ReviewQueryDto.ReviewInfo::getReviewNo, Function.identity()));

        // 회원 번호(MemberNo)를 기반으로 설문 정보를 조회할 수 있는 Map 생성
        List<String> memberNos = reviewInfoByProductNo.stream()
                .map(ReviewQueryDto.ReviewInfo::getMemberNo)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, SurveyQueryDto.SurveyWithMember> surveyMap = surveyRepository.findSurveyWithMember(memberNos)
                .stream()
                .collect(Collectors.toMap(SurveyQueryDto.SurveyWithMember::getMemberId, Function.identity()));

        // 해당 유저가 해당 리뷰에 대해 좋아요 했는지 Set으로 확인
        Set<Integer> reviewNosFromReviewInfos = reviewInfoByProductNo.stream()
                .map(ReviewQueryDto.ReviewInfo::getReviewNo)
                .collect(Collectors.toSet());

        Set<Long> userLikedReviewIds = reviewHeartService.existsByMemberIdAndReviewNoIn(userDetails.getMember().getId(), reviewNosFromReviewInfos, productNo);

        // 해당리뷰의 imageUrl
        Multimap<Long, String> reviewIdToImgUrls = ArrayListMultimap.create();
        List<ReviewPhotoQueryDto.ReviewPhoto> reviewPhotos = reviewPhotoRepository.findReviewPhotoByProductNo(productNo);
        for (ReviewPhotoQueryDto.ReviewPhoto photo : reviewPhotos) {
            reviewIdToImgUrls.put(photo.getReviewId(), photo.getReviewImgUrl());
        }

        // 리뷰 정보와 리뷰 목록을 병합하여 새로운 리스트에 저장
        List<ReviewResponseDto.ProductReviewInfo> mergedList = mergeReviewInfoAndReview(userDetails.getMember().getMemberNo(), reviewIdToImgUrls,
                reviewInfoByProductNo, reviewMap, surveyMap, userLikedReviewIds);
        String reviewTitle;
        if (roundedAverage == 0) {
            reviewTitle = "첫 리뷰어가 되어주세요!";
        } else if (roundedAverage >= 4.5) {
            reviewTitle = "강력추천!";
        } else {
            reviewTitle = "";
        }

        String reviewLink = reviewCrawlingRepository.findByProductNo(productNo)
                .map(ReviewCrawling::getReviewLink)
                .orElse("");

        return new ReviewResponseDto.ProductReviewAll(reviewTitle, reviewInfoByProductNo.getTotalElements(), roundedAverage, mergedList, reviewLink);
    }

    private static double getRoundedAverage(Double reviewRate) {
        // 가장 가까운 0.5 단위로 반올림합니다.
        return Math.round(reviewRate * 2) / 2.0;
    }

    /**
     * 해당 상품에 대한 모든 리뷰를 조회
     * 리뷰를 조회하면서 조회하는 해당 유저가 좋아요를 했는지 안했는지 확인
     *
     * @param
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/06/14
     **/
    private List<ReviewResponseDto.ProductReviewInfo> mergeReviewInfoAndReview(String memberNo, Multimap<Long, String> reviewIdToImgUrls,
                                                                               Page<ReviewQueryDto.ReviewInfo> productReviewInfoList,
                                                                               Map<Integer, ReviewQueryDto.ReviewInfo> reviewMap,
                                                                               Map<Long, SurveyQueryDto.SurveyWithMember> surveyMap,
                                                                               Set<Long> userLikedReviewIds) {

        return productReviewInfoList.stream()
                .map(p -> createProductReviewInfo(memberNo, reviewIdToImgUrls, reviewMap, surveyMap, userLikedReviewIds, p))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private ReviewResponseDto.ProductReviewInfo createProductReviewInfo(String memberNo, Multimap<Long, String> reviewIdToImgUrls,
                                                                        Map<Integer, ReviewQueryDto.ReviewInfo> reviewMap,
                                                                        Map<Long, SurveyQueryDto.SurveyWithMember> surveyMap,
                                                                        Set<Long> userLikedReviewIds,
                                                                        ReviewQueryDto.ReviewInfo reviewInfo) {

        ReviewQueryDto.ReviewInfo review = reviewMap.get(reviewInfo.getReviewNo());
        if (review == null) {
            return null;
        }

        boolean myReview = review.getMemberNo().equals(memberNo);
        Collection<String> imgUrls = reviewIdToImgUrls.get(reviewInfo.getReviewId());
        String[] reviewImgUrls = imgUrls.toArray(new String[0]);

        String tags = review.getTag();
        String[] reviewTags = (tags != null && !tags.isEmpty()) ? tags.split(",") : new String[0];

        boolean hasHeart = userLikedReviewIds.contains(review.getReviewId());
        ReviewResponseDto.UserInfo userInfo = createUserInfoFromSurvey(surveyMap.get(review.getMemberId()), hasHeart);

        // Handle default user info for members who might have left
        if (userInfo == null) {
            userInfo = getDefaultUserInfo(review.getNickName());
        }

        return new ReviewResponseDto.ProductReviewInfo(reviewInfo, myReview, reviewImgUrls, reviewTags, userInfo);
    }

    private ReviewResponseDto.UserInfo getDefaultUserInfo(String nickName) {
        List<String> defaultImages = ("dev".equals(serverProfile)) ? CustomValue.defaultProfileImgTest : CustomValue.defaultProfileImgProd;
        String[] skinTrouble = {"홍조", "트러블", "민감함"};
        return new ReviewResponseDto.UserInfo(
                defaultImages.get(0),
                nickName,
                "건성",
                skinTrouble,
                false
        );
    }


    public ReviewResponseDto.ReviewableTags getReviewTagByProduct(int productNo) {
        Product product = getProduct(productNo);
        return getReviewableTags(product);
    }

    public ReviewResponseDto.ReviewableTags getReviewTagByProduct(Product product) {
        return getReviewableTags(product);
    }

    private ReviewResponseDto.ReviewableTags getReviewableTags(Product product) {
        String noticeImageUrl = noticeService.getNotice(NoticeType.REVIEW_REGISTER_POLICY);

        ReviewTagType reviewTagType;
        if (product.getCategory().getCategoryDepth1().equals(CategoryType.KIT)) {
            reviewTagType = ReviewTagType.SKINCARE;
            return new ReviewResponseDto.ReviewableTags(noticeImageUrl, ReviewTagType.SKINCARE.getTagList());
        } else {
            reviewTagType = ReviewTagType.valueOf(product.getCategory().getCategoryDepth2().toString());
            return new ReviewResponseDto.ReviewableTags(noticeImageUrl, reviewTagType.getTagList());
        }
    }

    public ReviewResponseDto.PhotoReviewList getProductReviewPhotoAll(int pageNumber, int pageSize, String condition, int productNo) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<ReviewPhotoQueryDto.ReviewPhoto> reviewPhotos = reviewPhotoRepository.findReviewPhotoByProductNo(productNo, pageable);
        long totalElements = reviewPhotos.getTotalElements();

        List<ReviewResponseDto.PhotoReview> photoReviews = reviewPhotos.stream()
                .map(reviewPhoto -> new ReviewResponseDto.PhotoReview(reviewPhoto.getReviewNo(), reviewPhoto.getReviewRecommendCnt(), reviewPhoto.getReviewImgUrl()))
                .collect(Collectors.toList());

        Comparator<ReviewResponseDto.PhotoReview> comparator = Comparator.comparingInt(ReviewResponseDto.PhotoReview::getReviewNo);

        if ("liked".equals(condition)) {
            comparator = Comparator.comparingInt(ReviewResponseDto.PhotoReview::getRecommendCnt).reversed();
        } else if ("newest".equals(condition)) {
            comparator = comparator.reversed();
        }

        photoReviews.sort(comparator);

        return new ReviewResponseDto.PhotoReviewList((int) totalElements, photoReviews);
    }


    public ReviewResponseDto.ProductReviewInfo getReviewByReviewNo(UserDetailsImpl userDetails, int productNo, int reviewNo) {
        ReviewQueryDto.ReviewInfo reviewInfo = reviewRepository.findReviewInfoByProductNo(reviewNo, productNo);
        ReviewQueryDto.ReviewWithSurvey reviewWithSurvey = reviewRepository.findReviewWithSurvey(reviewNo, reviewInfo.getMemberNo());

        boolean myReview = reviewInfo.getMemberNo().equals(userDetails.getMember().getMemberNo());
        boolean hasHeart = reviewHeartService.existsByMemberIdAndReviewNo(userDetails.getMember().getId(), reviewNo);
        ReviewResponseDto.UserInfo userInfo =
                new ReviewResponseDto.UserInfo("", "", "", new String[0], false);
        String[] reviewTags = new String[0];
        if (reviewWithSurvey != null) {
            userInfo = createUserInfoFromSurvey(reviewWithSurvey, hasHeart);
            reviewTags = getReviewTags(reviewWithSurvey);
        }

        String[] reviewImgUrls = getReviewImgUrlsByProductNo(productNo, reviewInfo.getReviewId());

        return new ReviewResponseDto.ProductReviewInfo(reviewInfo, myReview, reviewImgUrls, reviewTags, userInfo);
    }

    private String[] getReviewImgUrlsByProductNo(int productNo, Long reviewId) {
        return reviewPhotoRepository.findReviewPhotoByProductNo(productNo).stream()
                .filter(photo -> photo.getReviewId().equals(reviewId))
                .map(ReviewPhotoQueryDto.ReviewPhoto::getReviewImgUrl)
                .toArray(String[]::new);
    }

    private String[] getReviewTags(ReviewQueryDto.ReviewWithSurvey reviewWithSurvey) {
        String tags = reviewWithSurvey.getReviewTag();
        return (tags != null && !tags.isEmpty()) ? tags.split(",") : new String[0];
    }

    private static ReviewResponseDto.UserInfo createUserInfoFromSurvey(ReviewQueryDto.ReviewWithSurvey reviewWithSurvey, boolean hasHeart) {
        String[] skinTrouble = reviewWithSurvey.getSkinTrouble().split(",");
        return new ReviewResponseDto.UserInfo(
                reviewWithSurvey.getProfileImage(),
                reviewWithSurvey.getNickName(),
                reviewWithSurvey.getSkinType(),
                skinTrouble,
                hasHeart);
    }

    private ReviewResponseDto.UserInfo createUserInfoFromSurvey(SurveyQueryDto.SurveyWithMember survey, boolean hasHeart) {
        if (survey == null) {
            return null;
        }
        String[] skinTrouble = survey.getSkinTrouble().split(",");
        return new ReviewResponseDto.UserInfo(
                survey.getProfileImageUrl(),
                survey.getNickName(),
                survey.getSkinType(),
                skinTrouble,
                hasHeart);
    }

    private ReviewResponseDto.ProductReviewInfo shopbyGetProductReviewByReviewNo(int productNo, int reviewNo, String shopByAccessToken) throws UnirestException, ParseException {
        HttpResponse<String> response = Unirest.get(shopByUrl + "/products/" + productNo + "/product-reviews" + "/" + reviewNo)
                .queryString("hasTotalCount", true)
                .header("version", versionHeader)
                .header("clientid", clientId)
                .header("platform", platformHeader)
                .header("accesstoken", shopByAccessToken)
                .asString();

        log.info("리뷰번호로 리뷰조회_____________________________");
        ShopBy.errorMessage(response);
        log.info("리뷰번호로 리뷰조회_____________________________");

        JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
        double rate = jsonObject.get("rate").getAsDouble();
        boolean myReview = jsonObject.get("myReview").getAsBoolean();
        String memberLoginId = jsonObject.get("memberId").getAsString();
        String registerDate = jsonObject.get("registerYmdt").getAsString();
        String registerNo = jsonObject.get("registerNo").getAsString();
        int recommendCnt = jsonObject.get("recommendCnt").getAsInt();
        String content = jsonObject.get("content").getAsString();
        JsonArray fileUrlsArray = jsonObject.getAsJsonArray("fileUrls");
        String[] reviewImageUrls = getReviewImageUrls(fileUrlsArray);
        String imageUrl = jsonObject.get("imageUrl").getAsString();
        imageUrl = "https:" + imageUrl;
        String brandName = jsonObject.get("brandName").getAsString();
        brandName = processBrandName(brandName);

        String productName = jsonObject.get("productName").getAsString();
        JsonObject orderedOption = jsonObject.get("orderedOption").getAsJsonObject();
        Long orderOptionNo = orderedOption.get("orderOptionNo").getAsLong();

        return new ReviewResponseDto.ProductReviewInfo(
                myReview,
                imageUrl, brandName, productName, productNo,
                reviewNo, rate, registerDate, registerNo, orderOptionNo,
                null, content, recommendCnt, reviewImageUrls);

    }

    private String processBrandName(String brandName) {
        int slashIndex = brandName.indexOf('/');
        return slashIndex != -1 ? brandName.substring(0, slashIndex).trim() : brandName.trim();
    }

    private ReviewResponseDto.ReviewableAndTotalCount shopbyGetReviewableProduct(String shopByAccessToken, int pageNumber, int pageSize) throws ParseException, UnirestException {
        HttpResponse<String> response = Unirest.get(shopByUrl + profile + "/order-options/product-reviewable")
                .queryString("pageNumber", pageNumber)
                .queryString("pageSize", pageSize)
                .queryString("hasTotalCount", true)
                .queryString("startDate", CustomValue.defaultStartYmd)
                .queryString("endDate", "")
                .header("version", versionHeader)
                .header("accesstoken", shopByAccessToken)
                .header("clientid", clientId)
                .header("platform", platformHeader)
                .asString();

        log.info("작성 가능한 리뷰조회____________________________");
        ShopBy.errorMessage(response);
        log.info("작성 가능한 리뷰조회____________________________");

        List<ReviewResponseDto.Reviewable> reviewableList = new ArrayList<>();
        JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
        JsonArray productsArray = jsonObject.getAsJsonArray("items");
        int totalCount = jsonObject.get("totalCount").getAsInt();

        for (JsonElement itemElement : productsArray) {
            JsonObject itemObject = itemElement.getAsJsonObject();
            String productName = itemObject.get("productName").getAsString();
            String brandName = itemObject.get("brandName").getAsString();
            brandName = processBrandName(brandName);
            int orderOptionNo = itemObject.get("orderOptionNo").getAsInt();
            int optionNo = itemObject.get("optionNo").getAsInt();
            String optionTitle = itemObject.get("optionTitle").getAsString();
            String orderNo = itemObject.get("orderNo").getAsString();
            int productNo = itemObject.get("productNo").getAsInt();
            String imageUrl = itemObject.get("imageUrl").getAsString();
            imageUrl = "https:" + imageUrl;
            ReviewResponseDto.Reviewable reviewable =
                    new ReviewResponseDto.Reviewable(productName, brandName, orderOptionNo, optionNo
                            , optionTitle, orderNo, productNo, imageUrl);

            reviewableList.add(reviewable);
        }
        return new ReviewResponseDto.ReviewableAndTotalCount(totalCount, reviewableList);
    }

    private static String[] getReviewImageUrls(JsonArray fileUrlsArray) {
        String[] reviewImageUrls = new String[fileUrlsArray.size()];
        for (int i = 0; i < fileUrlsArray.size(); i++) {
            String imgUrl = fileUrlsArray.get(i).getAsString();
            reviewImageUrls[i] = imgUrl;
        }
        return reviewImageUrls;
    }

    private Member getMember(UserDetailsImpl userDetails) {
        return memberRepository.findById(userDetails.getMember().getId())
                .orElseThrow(() -> new ErrorCustomException(ErrorCode.NO_USER_ERROR));
    }

    private Review getReview(int reviewNo) {
        return reviewRepository.findByReviewNo(reviewNo)
                .orElseThrow(() -> new ErrorCustomException(ErrorCode.REVIEW_NOT_FOUND));
    }

    private Review getReview(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ErrorCustomException(ErrorCode.REVIEW_NOT_FOUND));
    }

    /**
     * 상품번호들로 리뷰 조회
     *
     * @param
     * @param userDetails
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 4/4/24
     **/
    public Map<Integer, Long> getReviewByProductNos(List<Integer> orderOptionNos, UserDetailsImpl userDetails) {

        List<Long> orderOptionNosLong = orderOptionNos.stream()
                .map(Long::valueOf)
                .collect(Collectors.toList());

        List<ReviewQueryDto.ReviewInfo> reviewInfo = reviewRepository.findByOrderOptionNosAndMemberId(orderOptionNosLong, userDetails.getMember().getId());

        return reviewInfo.stream()
                .collect(Collectors.toMap(ReviewQueryDto.ReviewInfo::getProductNo, ReviewQueryDto.ReviewInfo::getReviewId));

    }

    private Product getProduct(int productNo) {
        return productRepository.findByProductNoAndProductInvisible(productNo, false)
                .orElseThrow(() -> new ErrorCustomException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    public ReviewResponseDto.NewWrittenReviewInfo getReviewInfoAndProductTag(UserDetailsImpl userDetails, int productNo, Long reviewId) throws UnirestException, ParseException {
        Product product = getProduct(productNo);
        ReviewResponseDto.ReviewableTags reviewTagByProduct = getReviewTagByProduct(product);

        ReviewResponseDto.NewReviewProductInfo reviewProductInfo = new ReviewResponseDto.NewReviewProductInfo(
                product.getImgUrl(),
                product.getBrandName(),
                product.getProductName(),
                product.getProductOptionsNo(),
                reviewTagByProduct.getNoticeImageUrl(),
                reviewTagByProduct.getReviewableTags()
        );

        if (reviewId != null && reviewId != 0) {
            Optional<Review> review = reviewRepository.findById(reviewId);
            if (review.isPresent()) {
                ReviewResponseDto.ProductReviewInfo productReviewInfo = shopbyGetProductReviewByReviewNo(productNo, review.get().getReviewNo(),
                        userDetails.getMember().getShopByAccessToken());

                String tags = review.get().getTag();
                String[] selectedReviewTags = (tags != null && !tags.isEmpty()) ? tags.split(",") : new String[0];

                return new ReviewResponseDto.NewWrittenReviewInfo(
                        reviewProductInfo,
                        review.get().getId(),
                        productReviewInfo.getReviewRate(),
                        productReviewInfo.getOrderOptionNo(),
                        selectedReviewTags,
                        productReviewInfo.getContent(),
                        productReviewInfo.getReviewImgUrls()
                );
            }
        }
        return new ReviewResponseDto.NewWrittenReviewInfo(reviewProductInfo);
    }

    public Tuple getReviewCountAndRateAvg(int productNo) {
        return reviewRepository.findReviewCountAndRateAvg(productNo);
    }

    public boolean existsSampleRoadReviewByProductNo(int productNo) {
        return reviewRepository.existsSampleRoadReviewByProductNo(productNo);
    }
}
