package com.example.sampleroad.controller;

import com.example.sampleroad.common.utils.CustomValue;
import com.example.sampleroad.common.utils.ResultInfo;
import com.example.sampleroad.dto.request.ReviewRequestDto;
import com.example.sampleroad.dto.response.review.ReviewCrawlingResponseDto;
import com.example.sampleroad.dto.response.review.ReviewResponseDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.example.sampleroad.service.ReviewService;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Api(tags = {"리뷰 관련 api Controller"})
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/api/review")
    @ApiOperation(value = "리뷰 등록 api")
    public ResultInfo addReview(@RequestBody ReviewRequestDto dto,
                                @AuthenticationPrincipal UserDetailsImpl userDetails) throws UnirestException, ParseException {
        ReviewResponseDto.AddReview reviewNo = reviewService.addReview(dto, userDetails);
        return new ResultInfo(ResultInfo.Code.CREATED, "리뷰 등록 완료", reviewNo);
    }

    @GetMapping("/api/review-info/{productNo}")
    @ApiOperation(value = "리뷰 정보 조회 + 리뷰 테그 결합 api")
    public ReviewResponseDto.NewWrittenReviewInfo getReviewInfoAndProductTag(@PathVariable int productNo,
                                                                             @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                             @RequestParam(defaultValue = "0") Long reviewId) throws UnirestException, ParseException {

        return reviewService.getReviewInfoAndProductTag(userDetails, productNo, reviewId);

    }


    @GetMapping("/api/reviewable")
    @ApiOperation(value = "내가 작성 가능한 리뷰 조회 api")
    public ReviewResponseDto.ReviewableAndTotalCount getReviewableItems(
            @RequestParam(defaultValue = CustomValue.pageNumber) int pageNumber,
            @RequestParam(defaultValue = CustomValue.pageSize) int pageSize,
            @AuthenticationPrincipal UserDetailsImpl userDetails) throws UnirestException, ParseException {
        return reviewService.getReviewableItems(userDetails, pageNumber, pageSize);
    }

    @GetMapping("/api/review/{productNo}/product-review/{reviewNo}")
    @ApiOperation(value = "리뷰No로 리뷰 조회 api")
    public ReviewResponseDto.ProductReviewInfo getReviewByReviewNo(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                   @PathVariable int productNo,
                                                                   @PathVariable int reviewNo) throws UnirestException, ParseException {

        return reviewService.getReviewByReviewNo(userDetails, productNo, reviewNo);
    }

    @GetMapping("/api/review-tag/{productNo}")
    @ApiOperation(value = "해당 상품의 가능한 리뷰 테그 조회 api")
    public ReviewResponseDto.ReviewableTags getReviewTagByProduct(@PathVariable int productNo) {
        return reviewService.getReviewTagByProduct(productNo);
    }

    @GetMapping("/api/review")
    @ApiOperation(value = "내가 작성한 리뷰 조회 api 페이징")
    public ReviewResponseDto.ReviewByMemberAndTotalCount getAllReviewByMember(
            @RequestParam(defaultValue = CustomValue.pageNumber) int pageNumber,
            @RequestParam(defaultValue = CustomValue.pageSize) int pageSize,
            @RequestParam(defaultValue = CustomValue.defaultStartYmd) String startYmd,
            @AuthenticationPrincipal UserDetailsImpl userDetails) throws UnirestException, ParseException {
        return reviewService.getMemberWrittenReview(pageNumber, pageSize, startYmd, userDetails);
    }

    @PostMapping("/api/review-recommend/{reviewNo}")
    @ApiOperation(value = "리뷰 추천하기 api")
    public ResultInfo recommendReviewByReviewNo(@PathVariable int reviewNo,
                                                @AuthenticationPrincipal UserDetailsImpl userDetails) throws UnirestException, ParseException {
        reviewService.recommendReviewByReviewNo(reviewNo, userDetails);
        return new ResultInfo(ResultInfo.Code.CREATED, "리뷰 추천 완료");
    }

    @PutMapping("/api/review-recommend/{reviewNo}")
    @ApiOperation(value = "리뷰 추천 취소하기 api")
    public ResultInfo unRecommendReviewByReviewNo(@PathVariable int reviewNo,
                                                  @AuthenticationPrincipal UserDetailsImpl userDetails) throws UnirestException, ParseException {
        reviewService.unRecommendReviewByReviewNo(reviewNo, userDetails);
        return new ResultInfo(ResultInfo.Code.SUCCESS, "리뷰 추천 취소완료");
    }

    @PostMapping("/api/review-report/{reviewNo}")
    @ApiOperation(value = "리뷰 신고하기 api")
    public ResultInfo reportReviewByReviewNo(@PathVariable int reviewNo,
                                             @RequestBody ReviewRequestDto.Report dto,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails) throws UnirestException, ParseException {
        reviewService.reportReview(reviewNo, dto, userDetails);
        return new ResultInfo(ResultInfo.Code.CREATED, "리뷰 신고 완료", reviewNo);

    }

    @GetMapping("/api/review-all/{productNo}")
    @ApiOperation(value = "상품 리뷰 조회 api")
    public ReviewResponseDto.ProductReviewAll getProductReviewAll(@RequestParam(defaultValue = CustomValue.pageNumber) int pageNumber,
                                                                  @RequestParam(defaultValue = CustomValue.pageSize) int pageSize,
                                                                  @PathVariable int productNo,
                                                                  @AuthenticationPrincipal UserDetailsImpl userDetails) throws UnirestException, ParseException {
        return reviewService.getProductReviewAll(pageNumber, pageSize, productNo, userDetails);
    }

    @GetMapping("/api/review-photo/{productNo}")
    @ApiOperation(value = "상품 리뷰 사진 모아보기 api")
    public ReviewResponseDto.PhotoReviewList getProductReviewPhotoAll(@RequestParam(defaultValue = CustomValue.pageNumber) int pageNumber,
                                                                      @RequestParam(defaultValue = CustomValue.pageSize) int pageSize,
                                                                      @RequestParam(defaultValue = "newest") String condition,
                                                                      @PathVariable int productNo) throws UnirestException, ParseException {
        return reviewService.getProductReviewPhotoAll(pageNumber, pageSize, condition, productNo);
    }

    @GetMapping("/api/external/review/{productNo}")
    @ApiOperation(value = "상품 크롤링 리뷰 조회 api")
    public ReviewCrawlingResponseDto getProductReviewCrawling(@PathVariable int productNo) {
        return reviewService.getProductReviewCrawling(productNo);
    }

    @GetMapping("/api/new/review-all")
    @ApiOperation(value = "리뷰 리뉴얼 전체 리뷰 조회 api")
    public ReviewResponseDto.NewReviewableAndTotalCountDto getReviewAll(@RequestParam(defaultValue = CustomValue.pageNumber) int pageNumber,
                                                                        @RequestParam(defaultValue = CustomValue.pageSize) int pageSize,
                                                                        @RequestParam(defaultValue = "false") boolean isWritable,
                                                                        @AuthenticationPrincipal UserDetailsImpl userDetails) throws UnirestException, ParseException {
        if (isWritable) {
            // TODO: 4/12/24 작성가능한은 페이징 없이 전체
            return reviewService.getWritableReviewAll(userDetails);
        } else {
            return reviewService.getWrittenReviewAll(pageNumber, pageSize, userDetails);
        }
    }

    @PutMapping("/api/review/{reviewId}")
    @ApiOperation(value = "리뷰 수정 api")
    public ResultInfo modifyReviewByMemberNo(@PathVariable Long reviewId,
                                             @RequestBody ReviewRequestDto.Update dto,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails) throws UnirestException, ParseException {
        reviewService.modifyReview(reviewId, dto, userDetails);
        return new ResultInfo(ResultInfo.Code.SUCCESS, "리뷰 수정 완료", reviewId);

    }

    @DeleteMapping("/api/review/{reviewId}")
    @ApiOperation(value = "리뷰 삭제 api")
    public ResultInfo removeReviewByReviewId(@PathVariable Long reviewId,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails) throws UnirestException, ParseException {
        reviewService.removeReview(reviewId, userDetails);
        return new ResultInfo(ResultInfo.Code.SUCCESS, "리뷰 삭제 완료", reviewId);

    }
}