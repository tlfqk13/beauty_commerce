package com.example.sampleroad.service;

import com.example.sampleroad.common.exception.ErrorCode;
import com.example.sampleroad.common.exception.ErrorCustomException;
import com.example.sampleroad.domain.CategoryType;
import com.example.sampleroad.domain.NoticeType;
import com.example.sampleroad.domain.display.DisplayDesignType;
import com.example.sampleroad.domain.member.Member;
import com.example.sampleroad.domain.order.OrdersItem;
import com.example.sampleroad.domain.survey.*;
import com.example.sampleroad.dto.request.zeroExperience.ZeroExperienceSurveyRequestDto;
import com.example.sampleroad.dto.response.zeroExperienceReview.ZeroExperienceQuestionQueryDto;
import com.example.sampleroad.dto.response.zeroExperienceReview.ZeroExperienceRecommendSurveyQueryDto;
import com.example.sampleroad.dto.response.zeroExperienceReview.ZeroExperienceRecommendSurveyResponseDto;
import com.example.sampleroad.dto.response.zeroExperienceReview.ZeroExperienceSurveyResponseDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.example.sampleroad.repository.zeroExperience.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ZeroExperienceReviewService {

    private final OrderService orderService;
    private final DisplayService displayService;
    private final NoticeService noticeService;
    private final ZeroExperienceRecommendSurveyRepository zeroExperienceRecommendSurveyRepository;
    private final ZeroExperienceSurveyRepository zeroExperienceSurveyRepository;
    private final ZeroExperienceSurveyMemberRepository zeroExperienceSurveyMemberRepository;
    private final QuestionAnswerRepository questionAnswerRepository;
    private final QuestionAnswerMemberRepository questionAnswerMemberRepository;
    private final QuestionAnswerImageRepository questionAnswerImageRepository;
    private final ZeroExperienceQuestionItemRepository zeroExperienceQuestionItemRepository;

    @Transactional
    public ZeroExperienceRecommendSurveyResponseDto getZeroExperienceItems(int pageNumber, int pageSize, UserDetailsImpl userDetails) {
        Long memberId = userDetails.getMember().getId();
        Page<ZeroExperienceRecommendSurveyQueryDto.OrdersItemInfo> items = orderService.getZeroExperienceItemsFromOrders(pageNumber, pageSize, memberId);

        List<Long> ordersItemIds = items.getContent().stream()
                .map(ZeroExperienceRecommendSurveyQueryDto.OrdersItemInfo::getOrdersItemId)
                .collect(Collectors.toList());

        List<ZeroExperienceRecommendSurveyQueryDto.OrdersItemInfo> ordersItemInfoList = new ArrayList<>(items.getContent());
        List<ZeroExperienceRecommendSurveyQueryDto> reviewList = zeroExperienceRecommendSurveyRepository.findByOrdersItemIds(ordersItemIds, memberId);
        List<ZeroExperienceSurveyMember> surveyMemberList = zeroExperienceSurveyMemberRepository.findByMemberIdAndOrdersItem_IdIn(memberId, ordersItemIds);

        Map<Long, Long> ordersItemIdToSurveyIdMap = surveyMemberList.stream()
                .filter(surveyMember -> surveyMember.getOrdersItem() != null && surveyMember.getZeroExperienceSurvey() != null)
                .collect(Collectors.toMap(
                        surveyMember -> surveyMember.getOrdersItem().getId(),
                        surveyMember -> surveyMember.getZeroExperienceSurvey().getId(),
                        (existing, replacement) -> existing
                ));

        // TODO: 2024-01-29 해당 유저의 질문 서베이 참여 필수 ordersItem 조회
        // 주문하고 7일 지난 ordersItem이 있는지 조회
        List<ZeroExperienceQuestionQueryDto.NecessaryOrdersItem> necessaryOrdersItems =
                zeroExperienceQuestionItemRepository.findOrderItem(ordersItemIds, memberId)
                        .stream()
                        .filter(item -> memberId.equals(item.getMemberId()))
                        .collect(Collectors.toList());

        if (!necessaryOrdersItems.isEmpty()) {
            // TODO: 2024-01-29 업데이트 이후에 주문한 사람
            ZeroExperienceQuestionQueryDto.NecessaryOrdersItem necessaryOrdersItem = necessaryOrdersItems.get(0);
            settingIsNecessary(necessaryOrdersItem, ordersItemInfoList);
        }

        String noticeImageUrl = noticeService.getNotice(NoticeType.SURVEY_POLICY);

        List<ZeroExperienceRecommendSurveyResponseDto.ItemInfo> itemInfos = getItemInfos(ordersItemInfoList, reviewList, ordersItemIdToSurveyIdMap);
        if (items.getTotalElements() == 0) {
            int displayNo = displayService.getDisplayEvents(DisplayDesignType.TYPE_A);
            return new ZeroExperienceRecommendSurveyResponseDto(0L, displayNo, noticeImageUrl, null);
        } else {
            return new ZeroExperienceRecommendSurveyResponseDto(items.getTotalElements(), null, noticeImageUrl, itemInfos);
        }
    }

    private void settingIsNecessary(ZeroExperienceQuestionQueryDto.NecessaryOrdersItem necessaryOrdersItem,
                                    List<ZeroExperienceRecommendSurveyQueryDto.OrdersItemInfo> ordersItemInfoList) {

        for (ZeroExperienceRecommendSurveyQueryDto.OrdersItemInfo ordersItemInfo : ordersItemInfoList) {
            if (Objects.equals(ordersItemInfo.getOrdersItemId(), necessaryOrdersItem.getOrdersItemId())) {
                ordersItemInfo.setIsNecessary(true);
            }
        }
    }

    private static List<ZeroExperienceRecommendSurveyResponseDto.ItemInfo> getItemInfos(
            List<ZeroExperienceRecommendSurveyQueryDto.OrdersItemInfo> items,
            List<ZeroExperienceRecommendSurveyQueryDto> reviewList,
            Map<Long, Long> ordersItemIdToSurveyIdMap) {

        // Prepare reviewMap
        Map<Long, ZeroExperienceRecommendSurveyQueryDto> reviewMap = Optional.ofNullable(reviewList)
                .orElse(Collections.emptyList())
                .stream()
                .filter(dto -> dto.getZeroExperienceId() != null)
                .collect(Collectors.toMap(
                        ZeroExperienceRecommendSurveyQueryDto::getOrdersItemId, Function.identity(),
                        (existing, replacement) -> existing));

        // Create ItemInfo objects
        return items.stream()
                .map(item -> createItemInfo(item, reviewMap, ordersItemIdToSurveyIdMap))
                .collect(Collectors.toList());
    }

    private static ZeroExperienceRecommendSurveyResponseDto.ItemInfo createItemInfo(
            ZeroExperienceRecommendSurveyQueryDto.OrdersItemInfo item,
            Map<Long, ZeroExperienceRecommendSurveyQueryDto> reviewMap,
            Map<Long, Long> ordersItemIdToSurveyIdMap) {

        Long ordersItemId = item.getOrdersItemId();
        ZeroExperienceRecommendSurveyQueryDto surveyDto = reviewMap.get(ordersItemId);
        Long zeroExperienceReviewId = (surveyDto != null) ? surveyDto.getZeroExperienceId() : null;
        Long questionSurveyId = (surveyDto != null) ? surveyDto.getQuestionSurveyId() : null;
        Boolean isRecommend = (surveyDto != null) ? surveyDto.getIsRecommend() : null;
        Boolean hasQuestionSurvey = Optional.ofNullable(ordersItemIdToSurveyIdMap.get(ordersItemId)).isPresent();

        return new ZeroExperienceRecommendSurveyResponseDto.ItemInfo(
                zeroExperienceReviewId,
                ordersItemId,
                questionSurveyId,
                item.getBrandName(),
                item.getProductImageUrl(),
                item.getProductName(),
                item.getProductNo(),
                item.getProductPurchaseTime().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")),
                isRecommend, hasQuestionSurvey,
                item.getOrderNo(),
                item.getIsNecessary()
        );
    }

    public ZeroExperienceSurveyResponseDto getZeroExperienceQuestionSurvey(UserDetailsImpl userDetails, Long ordersItemId) {
        // TODO: 2024-01-24 설문지 조회 (질문 + 답변 )
        OrdersItem ordersItem = orderService.getOrdersItem(ordersItemId);
        // TODO: 1/30/24 설문에 필수여부 추가
        boolean hasNecessaryItem = zeroExperienceQuestionItemRepository.existsByMemberId(userDetails.getMember().getId());

        // TODO: 1/26/24 ordersItem 을 무조건 만드는게 1월에 들어가서 그전에 하려면 그냥 product로 검증받아야함
        Optional<ZeroExperienceSurvey> questionSurvey = zeroExperienceSurveyRepository.findByCategoryType(CategoryType.EXPERIENCE);

        List<ZeroExperienceQuestionQueryDto> questionsAndAnswer = zeroExperienceSurveyRepository.findByIdWithQuestionsAndAnswers(questionSurvey.get().getId());

        // Create a set of unique question contents
        Set<String> uniqueQuestionContents = questionsAndAnswer.stream()
                .map(ZeroExperienceQuestionQueryDto::getQuestionContent)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        // Filter the original list to include only unique question contents
        List<ZeroExperienceQuestionQueryDto> uniqueQuestionsAndAnswers = questionsAndAnswer.stream()
                .filter(dto -> uniqueQuestionContents.remove(dto.getQuestionContent()))
                .collect(Collectors.toList());

        List<ZeroExperienceSurveyResponseDto.QuestionDto> questionDtoList = new ArrayList<>();
        Map<Long, List<ZeroExperienceQuestionQueryDto>> groupedByQuestionId = questionsAndAnswer.stream()
                .collect(Collectors.groupingBy(ZeroExperienceQuestionQueryDto::getQuestionId));

        createQuestion(uniqueQuestionsAndAnswers, questionDtoList, groupedByQuestionId);

        return new ZeroExperienceSurveyResponseDto(
                questionSurvey.get().getId(),
                ordersItem.getProduct().getProductNo(),
                ordersItem.getProduct().getImgUrl(),
                ordersItem.getProduct().getProductName(),
                ordersItem.getProduct().getBrandName(),
                hasNecessaryItem,
                questionDtoList);

    }

    private void createQuestion(List<ZeroExperienceQuestionQueryDto> uniqueQuestionsAndAnswers,
                                List<ZeroExperienceSurveyResponseDto.QuestionDto> questionDtoList,
                                Map<Long, List<ZeroExperienceQuestionQueryDto>> groupedByQuestionId) {
        for (ZeroExperienceQuestionQueryDto uniqueQuestionAndAnswer : uniqueQuestionsAndAnswers) {
            List<ZeroExperienceSurveyResponseDto.QuestionAnswerDto> questionAnswerDtoList = groupedByQuestionId.get(uniqueQuestionAndAnswer.getQuestionId()).stream()
                    .map(this::createQuestionAnswerDto)
                    .collect(Collectors.toList());

            ZeroExperienceSurveyResponseDto.QuestionDto questionDto = new ZeroExperienceSurveyResponseDto.QuestionDto(
                    uniqueQuestionAndAnswer.getQuestionId(),
                    uniqueQuestionAndAnswer.getQuestionContent(),
                    uniqueQuestionAndAnswer.getQuestionType(),
                    uniqueQuestionAndAnswer.getSelectMaxCount(),
                    questionAnswerDtoList);

            questionDtoList.add(questionDto);
        }
    }

    private ZeroExperienceSurveyResponseDto.QuestionAnswerDto createQuestionAnswerDto(ZeroExperienceQuestionQueryDto answerDto) {
        if (QuestionType.IMAGE.equals(answerDto.getQuestionType())) {
            List<QuestionAnswerImage> questionAnswerImageList = questionAnswerImageRepository.findByQuestionAnswer_Id(answerDto.getAnswerId());
            String imageUrl1 = questionAnswerImageList.size() > 0 ? questionAnswerImageList.get(0).getAnswerImaUrl() : null;
            String imageUrl2 = questionAnswerImageList.size() > 1 ? questionAnswerImageList.get(1).getAnswerImaUrl() : null;
            return new ZeroExperienceSurveyResponseDto.QuestionAnswerDto(answerDto.getAnswerId(), answerDto.getOptionText(), imageUrl1, imageUrl2);
        } else {
            return new ZeroExperienceSurveyResponseDto.QuestionAnswerDto(answerDto.getAnswerId(), answerDto.getOptionText());
        }
    }


    @Transactional
    public void addRecommendSurvey(UserDetailsImpl userDetails, Long ordersItemId, String isRecommendStr) {
        Member member = userDetails.getMember();
        OrdersItem ordersItem = orderService.getOrdersItem(ordersItemId);
        boolean isRecommend = "true".equals(isRecommendStr);

        ZeroExperienceRecommendSurvey zeroExperienceRecommendSurvey = ZeroExperienceRecommendSurvey.builder()
                .isRecommend(isRecommend)
                .ordersItem(ordersItem)
                .member(member)
                .build();

        zeroExperienceRecommendSurveyRepository.save(zeroExperienceRecommendSurvey);
    }

    @Transactional
    public void modifyRecommendSurvey(UserDetailsImpl userDetails, Long ordersItemId, String isRecommendStr) {
        boolean isRecommend = "true".equals(isRecommendStr);
        zeroExperienceRecommendSurveyRepository.findByOrdersItem_IdAndMember_Id(ordersItemId, userDetails.getMember().getId())
                .ifPresentOrElse(
                        recommendSurvey -> recommendSurvey.updateIsRecommend(isRecommend),
                        () -> {
                            throw new ErrorCustomException(ErrorCode.RECOMMEND_SURVEY_NOT_FOUND);
                        }
                );
    }

    @Transactional
    public void addZeroExperienceQuestionSurvey(UserDetailsImpl userDetails, Long ordersItemId,
                                                ZeroExperienceSurveyRequestDto requestDto) {
        OrdersItem ordersItem = orderService.getOrdersItem(ordersItemId);
        if (ordersItem == null) {
            throw new ErrorCustomException(ErrorCode.ORDERS_ITEM_NOT_FOUND);
        }

        List<Long> allAnswerIds = requestDto.getAllAnswerIds();
        log.info("allAnswerIds Size -> " + allAnswerIds);

        if (allAnswerIds.isEmpty()) {
            throw new ErrorCustomException(ErrorCode.ANSWER_IDS_NOT_FOUND);
        }

        Long questionSurveyId = requestDto.getQuestionSurveyId();
        ZeroExperienceSurvey zeroExperienceSurvey = zeroExperienceSurveyRepository.findById(questionSurveyId)
                .orElseThrow(() -> new ErrorCustomException(ErrorCode.ZERO_EXPERIENCE_QUESTION_SURVEY_NOT_FOUND));

        ZeroExperienceSurveyMember surveyMember = ZeroExperienceSurveyMember.builder()
                .member(userDetails.getMember())
                .zeroExperienceSurvey(zeroExperienceSurvey)
                .ordersItem(ordersItem)
                .build();
        ZeroExperienceSurveyMember savedSurveyMember = zeroExperienceSurveyMemberRepository.save(surveyMember);

        String textAnswer = requestDto.getTextTypeAnswer();
        log.info("textTypeAnswer -> " + textAnswer);
        log.info("textTypeAnswer -> " + textAnswer);
        log.info("textTypeAnswer -> " + textAnswer);

        List<QuestionAnswer> questionAnswerList = questionAnswerRepository.findQuestionAnswerListByAnswerIds(allAnswerIds);
        List<QuestionAnswerMember> questionAnswerMembers = createQuestionAnswerMembers(savedSurveyMember, questionAnswerList, textAnswer);

        questionAnswerMemberRepository.saveAll(questionAnswerMembers);

        // TODO: 2024-01-29 주문하고 최소 7일 지난 데이터로 해야할껄? 
        Optional<ZeroExperienceQuestionItem> questionNecessaryItem = zeroExperienceQuestionItemRepository.findByOrdersItem_Id(ordersItemId);
        questionNecessaryItem.ifPresent(zeroExperienceQuestionItemRepository::delete);

    }

    private List<QuestionAnswerMember> createQuestionAnswerMembers(ZeroExperienceSurveyMember savedSurveyMember,
                                                                   List<QuestionAnswer> questionAnswerList,
                                                                   String textAnswer) {
        return questionAnswerList.stream()
                .filter(Objects::nonNull) // Ensure that the answer is not null
                .map(answer -> createQuestionAnswerMember(savedSurveyMember, answer, textAnswer))
                .collect(Collectors.toList());
    }

    private QuestionAnswerMember createQuestionAnswerMember(ZeroExperienceSurveyMember savedSurveyMember,
                                                            QuestionAnswer answer,
                                                            String textAnswer) {
        // Check if the answer is of type TEXT and set textAnswer accordingly
        log.info("createQuestionAnswerMember ______S_____________________________");
        log.info("createQuestionAnswerMember ______S" + answer.getQuestion().getId());
        log.info("createQuestionAnswerMember ______S " + answer.getQuestion().getQuestionType());
        log.info("createQuestionAnswerMember ______S_____________________________");

        String answerText = (answer.getQuestion() != null && answer.getQuestion().getQuestionType() == QuestionType.TEXT) ? textAnswer : null;

        return QuestionAnswerMember.builder()
                .zeroExperienceSurveyMember(savedSurveyMember)
                .questionAnswer(answer)
                .textAnswer(answerText)
                .build();
    }

    public List<ZeroExperienceRecommendSurveyQueryDto> getZeroExperienceRecommend(int productNo) {
        return zeroExperienceRecommendSurveyRepository.findByProductNo(productNo);
    }

    public boolean getZeroExperienceByIsNecessary(Long memberId) {
        // TODO: 2024-01-29 주문하고 최소 7일 지난 데이터로 해야할껄?
        return zeroExperienceQuestionItemRepository.existsByMemberId(memberId);
    }

    public boolean getZeroExperienceByIsNecessaryFromHome(Long memberId) {
        // TODO: 2024-01-29 주문하고 최소 7일 지난 데이터로 해야할껄?
        return zeroExperienceQuestionItemRepository.existsByMemberId(memberId);
    }
}

