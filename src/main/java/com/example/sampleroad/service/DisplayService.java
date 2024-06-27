package com.example.sampleroad.service;

import com.example.sampleroad.common.exception.ErrorCode;
import com.example.sampleroad.common.exception.ErrorCustomException;
import com.example.sampleroad.common.utils.ShopBy;
import com.example.sampleroad.domain.DisplaySectionType;
import com.example.sampleroad.domain.display.Display;
import com.example.sampleroad.domain.display.DisplayDesignType;
import com.example.sampleroad.domain.display.DisplayDetailImage;
import com.example.sampleroad.domain.display.DisplayType;
import com.example.sampleroad.dto.response.display.DisplayResponseDto;
import com.example.sampleroad.dto.response.display.DisplaySectionResponseDto;
import com.example.sampleroad.repository.display.DisplayDetailImageRepository;
import com.example.sampleroad.repository.display.DisplayRepository;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DisplayService {

    @Value("${shop-by.client-id}")
    String clientId;
    @Value("${shop-by.url}")
    String shopByUrl;
    @Value("${shop-by.accept-header}")
    String acceptHeader;
    @Value("${shop-by.version-header}")
    String versionHeader;
    @Value("${shop-by.platform-header}")
    String platformHeader;
    @Value("${shop-by.section-no}")
    int sectionNo;
    Gson gson = new Gson();

    private final DisplayRepository displayRepository;
    private final DisplayDetailImageRepository displayDetailImageRepository;

    public DisplayResponseDto.DisplayList getDisplayEvents() throws UnirestException, ParseException {
        DisplayResponseDto.DisplayList shopByDisplayList = shopbyGetDisplayEvents();
        List<Display> displayList = displayRepository.findByIsVisible(true);
        Map<Integer, String> displayMainImageMap = displayList.stream().collect(Collectors.toMap(Display::getDisplayNo, Display::getDisplayMainImageUrl));
        Map<Integer, String> displayBannerImageMap = displayList.stream().collect(Collectors.toMap(Display::getDisplayNo, Display::getDisplayBannerImageUrl));
        List<DisplayResponseDto.DisplayInfo> displayInfoList = new ArrayList<>();
        for (int i = 0; i < shopByDisplayList.getDisplayInfoList().size(); i++) {
            DisplayResponseDto.DisplayInfo displayInfo =
                    new DisplayResponseDto.DisplayInfo(
                            shopByDisplayList.getDisplayInfoList().get(i).getEventNo(),
                            shopByDisplayList.getDisplayInfoList().get(i).getLabel(),
                            shopByDisplayList.getDisplayInfoList().get(i).getEventId(),
                            displayMainImageMap.get(shopByDisplayList.getDisplayInfoList().get(i).getEventNo()), // mainImageUrl
                            displayBannerImageMap.get(shopByDisplayList.getDisplayInfoList().get(i).getEventNo()), // bannerImageUrl
                            shopByDisplayList.getDisplayInfoList().get(i).getProgressStatus(),
                            false
                    );
            displayInfoList.add(displayInfo);
        }

        return new DisplayResponseDto.DisplayList(shopByDisplayList.getTotalCount(), shopByDisplayList.getTotalPage(), displayInfoList);
    }

    public DisplayResponseDto getDisplayEventDetail(int displayNo) throws UnirestException, ParseException {
        // Fetching necessary data
        DisplayResponseDto.DisplayDetailInfo displayDetailInfo = getDisplayDetailInfo(displayNo);
        List<DisplayResponseDto.DisplayProductInfoList> displayProductInfoList = displayDetailInfo.getDisplayProductInfoList();
        List<DisplayResponseDto.DisplayCoupon> displayCouponList = displayDetailInfo.getDisplayCouponList();
        List<DisplayDetailImage> displayDetailImage = displayDetailImageRepository.findByDisplay_DisplayNoOrderByImagePosAsc(displayNo);
        Map<Integer, List<DisplayResponseDto.DisplayProductInfo>> indexToProductInfoMap = createIndexToProductInfoMap(displayProductInfoList);
        Map<Integer, DisplayResponseDto.DisplayCoupon> indexToCouponInfoMap = createIndexToCouponInfoMap(displayCouponList);

        Optional<Display> display = displayRepository.findByDisplayNo(displayNo);
        if (display.isEmpty()) {
            throw new ErrorCustomException(ErrorCode.DISPLAY_NOT_FOUND);
        }

        DisplayResponseDto.DisplayInfo displayInfo = new DisplayResponseDto.DisplayInfo(
                displayDetailInfo.getDisplayInfo().getEventNo(),
                displayDetailInfo.getDisplayInfo().getLabel(),
                displayDetailInfo.getDisplayInfo().getEventId(),
                display.get().getDisplayMainImageUrl(),
                displayDetailInfo.getDisplayInfo().getProgressStatus(),
                !displayDetailInfo.getDisplayCouponList().isEmpty()
        );

        displayDetailInfo.setDisplayInfo(displayInfo);

        List<DisplayResponseDto.SectionInfo> sectionInfoList = new ArrayList<>();
        if (DisplayDesignType.TYPE_B.equals(display.get().getDisplayDesignType())) {
            buildSectionListForTypeB(displayDetailInfo, displayDetailImage, sectionInfoList, indexToProductInfoMap, displayProductInfoList, indexToCouponInfoMap);
        } else {
            buildSectionListForTypeA(displayDetailInfo, displayDetailImage, sectionInfoList, indexToProductInfoMap, displayProductInfoList);
        }
        return new DisplayResponseDto(displayDetailInfo.getDisplayInfo().getEventNo(), displayDetailInfo.getDisplayInfo().getLabel(),
                displayDetailInfo.getShareDescription(), sectionInfoList);
    }

    public DisplayResponseDto.DisplayDetailInfo getDisplayDetailInfo(int displayNo) throws UnirestException, ParseException {
        return shopbyGetDisplayEventDetail(displayNo);
    }

    private static void addProductSections(Map<Integer, List<DisplayResponseDto.DisplayProductInfo>> indexToProductInfoMap, List<DisplayResponseDto.SectionInfo> sectionInfoList, int index) {
        for (int i = 0; i < indexToProductInfoMap.get(index).size(); i++) {
            sectionInfoList.add(new DisplayResponseDto.SectionInfo(
                    DisplaySectionType.ITEM_SECTION, indexToProductInfoMap.get(index).get(i)
            ));
        }
    }

    private static void addCouponSection(Map<Integer, DisplayResponseDto.DisplayCoupon> indexToCouponInfoMap, List<DisplayResponseDto.SectionInfo> sectionInfoList, int index) {
        sectionInfoList.add(new DisplayResponseDto.SectionInfo(DisplaySectionType.COUPON_SECTION, indexToCouponInfoMap.get(index)));
    }

    private void buildSectionListForTypeB(DisplayResponseDto.DisplayDetailInfo displayDetailInfo, List<DisplayDetailImage> displayDetailImage,
                                          List<DisplayResponseDto.SectionInfo> sectionInfoList,
                                          Map<Integer, List<DisplayResponseDto.DisplayProductInfo>> indexToProductInfoMap,
                                          List<DisplayResponseDto.DisplayProductInfoList> displayProductInfoList,
                                          Map<Integer, DisplayResponseDto.DisplayCoupon> indexToCouponInfoMap) {

        String displayMainImgUrl = displayDetailInfo.getDisplayInfo().getImageUrl();
        addMainImageSections(displayMainImgUrl, sectionInfoList);
        int couponSize = indexToCouponInfoMap.size();

        for (int index = 0; index < couponSize; index++) {
            if (displayDetailInfo.getDisplayInfo().getHasCoupon()) {
                if (index == 0) {
                    addTitleSections("기획전 특별 구폰", sectionInfoList); // 쿠폰 타이틀 섹션
                }
                addCouponSection(indexToCouponInfoMap, sectionInfoList, index); // 쿠폰 섹션
            }
        }

        // TODO: 2/1/24 이미지 섹션
        for (int index = 0; index < displayDetailImage.size(); index++) {
            addImageSections(displayDetailImage, sectionInfoList, index); // 상세 이미지 1
        }

        // TODO: 2/1/24 상품 섹션
        List<String> allSectionLabels = displayProductInfoList.stream()
                .map(DisplayResponseDto.DisplayProductInfoList::getSectionLabel)
                .collect(Collectors.toList());
        int productSectionSize = allSectionLabels.size();
        // Adding title and product sections based on productSectionSize
        for (int index = 0; index < productSectionSize; index++) {
            if (index < allSectionLabels.size()) {
                addTitleSections(allSectionLabels, sectionInfoList, index); // Adding title section
            }
            if (index < indexToProductInfoMap.size()) {
                addProductSections(indexToProductInfoMap, sectionInfoList, index); // Adding product section
            }
        }
    }

    private void addTitleSections(String couponTitle, List<DisplayResponseDto.SectionInfo> sectionInfoList) {
        sectionInfoList.add(createSectionInfo(couponTitle));
    }

    private void buildSectionListForTypeA(DisplayResponseDto.DisplayDetailInfo displayDetailInfo, List<DisplayDetailImage> displayDetailImage, List<DisplayResponseDto.SectionInfo> sectionInfoList,
                                          Map<Integer, List<DisplayResponseDto.DisplayProductInfo>> indexToProductInfoMap, List<DisplayResponseDto.DisplayProductInfoList> displayProductInfoList) {

        String displayMainImgUrl = displayDetailInfo.getDisplayInfo().getImageUrl();
        addMainImageSections(displayMainImgUrl, sectionInfoList);
        List<String> allSectionLabels = displayProductInfoList.stream()
                .map(DisplayResponseDto.DisplayProductInfoList::getSectionLabel)
                .collect(Collectors.toList());

        addTitleSections(allSectionLabels, sectionInfoList, 0); // 상품 섹션 label
        addProductSections(indexToProductInfoMap, sectionInfoList, 0); // 상품 섹션
    }

    private Map<Integer, List<DisplayResponseDto.DisplayProductInfo>> createIndexToProductInfoMap(List<DisplayResponseDto.DisplayProductInfoList> displayProductInfoList) {
        Map<Integer, List<DisplayResponseDto.DisplayProductInfo>> map = new HashMap<>();
        for (int i = 0; i < displayProductInfoList.size(); i++) {
            map.put(i, displayProductInfoList.get(i).getDisplayProductInfos());
        }
        return map;
    }

    private Map<Integer, DisplayResponseDto.DisplayCoupon> createIndexToCouponInfoMap(List<DisplayResponseDto.DisplayCoupon> displayCouponList) {
        Map<Integer, DisplayResponseDto.DisplayCoupon> map = new HashMap<>();
        for (int i = 0; i < displayCouponList.size(); i++) {
            map.put(i, displayCouponList.get(i));
        }
        return map;
    }


    private void addTitleSections(List<String> allSectionLabels, List<DisplayResponseDto.SectionInfo> sectionInfoList, int index) {
        if (index < sectionInfoList.size()) {
            sectionInfoList.add(createSectionInfo(allSectionLabels.get(index)));
        }
    }

    private DisplayResponseDto.SectionInfo createSectionInfo(String sectionsLabel) {
        return new DisplayResponseDto.SectionInfo(DisplaySectionType.TITLE_SECTION, null, sectionsLabel);
    }

    private void addImageSections(List<DisplayDetailImage> displayDetailImage, List<DisplayResponseDto.SectionInfo> sectionInfoList, int index) {
        if (index < displayDetailImage.size()) {
            sectionInfoList.add(createSectionInfo(displayDetailImage.get(index)));
        }
    }

    private void addMainImageSections(String mainImageUrl, List<DisplayResponseDto.SectionInfo> sectionInfoList) {
        DisplayResponseDto.SectionInfo sectionInfo = new DisplayResponseDto.SectionInfo(DisplaySectionType.IMAGE_SECTION, mainImageUrl, null);
        sectionInfoList.add(sectionInfo);
    }


    private static DisplayResponseDto.SectionInfo createSectionInfo(DisplayDetailImage displayDetailImage) {
        return new DisplayResponseDto.SectionInfo(DisplaySectionType.IMAGE_SECTION, displayDetailImage.getImageUrl(), null);
    }

    public DisplayResponseDto.DisplayDetailInfo shopbyGetDisplayEventDetail(int eventNo) throws UnirestException, ParseException {
        HttpResponse<String> response = Unirest.get(shopByUrl + "/display/events/" + eventNo)
                .queryString("order", "ADMIN_SETTING")
                .queryString("soldout", true)
                .header("accept", acceptHeader)
                .header("version", versionHeader)
                .header("clientid", clientId)
                .header("platform", platformHeader)
                .header("content-type", acceptHeader)
                .asString();

        if (response.getStatus() != 200) {
            ShopBy.errorMessage(response);
        }

        List<DisplayResponseDto.DisplayCoupon> downloadAbleCouponInfoList = new ArrayList<>();
        JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
        int resEventNo = jsonObject.get("eventNo").getAsInt();
        String label = jsonObject.get("label").getAsString();
        String eventId = jsonObject.get("id").isJsonNull() ? null : jsonObject.get("id").getAsString();
        String shareDescription = jsonObject.get("promotionText").getAsString();
        String mobileImageUrl = jsonObject.get("mobileimageUrl").getAsString();
        String endYmdt = jsonObject.get("endYmdt").getAsString();

        DisplayResponseDto.DisplayInfo displayInfo = new DisplayResponseDto.DisplayInfo(
                resEventNo, label, eventId, endYmdt);

        List<DisplayResponseDto.DisplayProductInfoList> displayProductInfoLists = getDisplayProductInfoLists(jsonObject);
        List<DisplayResponseDto.DisplayCoupon> displayCouponList = getDisplayCouponList(downloadAbleCouponInfoList, jsonObject);

        return new DisplayResponseDto.DisplayDetailInfo(
                displayInfo, displayProductInfoLists, displayCouponList, shareDescription);

    }

    private static List<DisplayResponseDto.ImageSection> getDisplayImage(JsonObject jsonObject, String mobileImageUrl, String pcImageUrl) {
        JsonObject top = jsonObject.get("top").getAsJsonObject();
        JsonObject pc = top.get("pc").getAsJsonObject();
        JsonObject mobile = top.get("mobile").getAsJsonObject();

        String topPcImgUrl = pc.get("url").getAsString();
        String topMobileImgUrl = mobile.get("url").getAsString();

        List<DisplayResponseDto.ImageSection> imageSectionList = new ArrayList<>();
        // Create ImageSection objects
        if (!topPcImgUrl.isEmpty()) {
            List<DisplayResponseDto.ImageUrl> firstImgUrl = Collections.singletonList(new DisplayResponseDto.ImageUrl(topPcImgUrl));
            imageSectionList.add(new DisplayResponseDto.ImageSection(DisplaySectionType.IMAGE_SECTION, firstImgUrl));
        }
        if (!topMobileImgUrl.isEmpty()) {
            List<DisplayResponseDto.ImageUrl> secondImgUrl = Arrays.asList(new DisplayResponseDto.ImageUrl(topMobileImgUrl), new DisplayResponseDto.ImageUrl(mobileImageUrl));
            imageSectionList.add(new DisplayResponseDto.ImageSection(DisplaySectionType.IMAGE_SECTION, secondImgUrl));
        }
        if (!pcImageUrl.isEmpty()) {
            List<DisplayResponseDto.ImageUrl> thirdImgUrl = Collections.singletonList(new DisplayResponseDto.ImageUrl(pcImageUrl));
            imageSectionList.add(new DisplayResponseDto.ImageSection(DisplaySectionType.IMAGE_SECTION, thirdImgUrl));
        }
        return imageSectionList;
    }

    private static List<DisplayResponseDto.DisplayProductInfoList> getDisplayProductInfoLists(JsonObject jsonObject) {
        List<DisplayResponseDto.DisplayProductInfoList> displayProductInfoLists = new ArrayList<>();
        JsonArray section = jsonObject.getAsJsonArray("section");
        for (JsonElement sectionElement : section) {
            JsonObject item = sectionElement.getAsJsonObject();
            JsonArray products = item.getAsJsonArray("products");
            String sectionLabel = item.get("label").getAsString();

            List<DisplayResponseDto.DisplayProductInfo> displayProductInfoList = new ArrayList<>();
            for (JsonElement productElement : products) {
                JsonObject productItem = productElement.getAsJsonObject();

                int productNo = productItem.get("productNo").getAsInt();
                String productName = productItem.get("productName").getAsString();
                String brandNameKo = productItem.get("brandNameKo").getAsString();
                int salePrice = productItem.get("salePrice").getAsInt();
                int immediateDiscountAmt = productItem.get("immediateDiscountAmt").getAsInt();
                int stockCnt = productItem.get("stockCnt").getAsInt();
                String imageUrl = productItem.getAsJsonArray("imageUrls").get(0).getAsString();
                imageUrl = "https:" + imageUrl;

                DisplayResponseDto.DisplayProductInfo displayProductInfo = new DisplayResponseDto.DisplayProductInfo(
                        sectionLabel, productNo, productName, brandNameKo, salePrice, immediateDiscountAmt, stockCnt, imageUrl
                );
                displayProductInfoList.add(displayProductInfo);
            }

            DisplayResponseDto.DisplayProductInfoList displayProductInfoListObject =
                    new DisplayResponseDto.DisplayProductInfoList(sectionLabel, displayProductInfoList);
            displayProductInfoLists.add(displayProductInfoListObject);
        }
        return displayProductInfoLists;
    }

    private List<DisplayResponseDto.DisplayCoupon> getDisplayCouponList(List<DisplayResponseDto.DisplayCoupon> downloadAbleCouponInfoList, JsonObject jsonObject) {
        JsonObject coupon = jsonObject.get("coupon").getAsJsonObject();
        JsonArray coupons = coupon.getAsJsonArray("coupons");
        List<DisplayResponseDto.DisplayCoupon> displayCouponList = new ArrayList<>();

        NumberFormat numberFormat = NumberFormat.getInstance();

        for (JsonElement element : coupons) {
            JsonObject couponItem = element.getAsJsonObject();
            int couponNo = couponItem.get("couponNo").getAsInt();
            String couponName = couponItem.get("couponName").getAsString();
            String couponType = couponItem.get("couponType").getAsString();
            boolean isDownloadable = couponItem.get("downloadable").getAsBoolean();

            JsonObject discountInfoObject = couponItem.getAsJsonObject("discountInfo");
            JsonObject useConstraintObject = couponItem.getAsJsonObject("useConstraint");

            int discountRate = getJsonInt(discountInfoObject, "discountRate");
            int discountAmt = getJsonInt(discountInfoObject, "discountAmt");
            int minSalePrice = getJsonInt(useConstraintObject, "minSalePrice");
            int maxDiscountAmt = getJsonInt(discountInfoObject, "maxDiscountAmt");

            String minSalePriceStr = formatPrice(numberFormat, minSalePrice) + "원 이상 구매 시 사용가능";
            String maxDiscountAmtStr = "최대 " + formatPrice(numberFormat, discountAmt != 0 ? discountAmt : maxDiscountAmt) + "원 할인";
            String couponTitle = discountAmt != 0 ? formatPrice(numberFormat, discountAmt) + "원" : discountRate + "%";

            DisplayResponseDto.DisplayCoupon downloadAbleCouponInfo = new DisplayResponseDto.DisplayCoupon(
                    couponNo, couponName, couponType, discountRate, discountAmt,
                    minSalePriceStr, isDownloadable, maxDiscountAmtStr, couponTitle);

            downloadAbleCouponInfoList.add(downloadAbleCouponInfo);
            displayCouponList.add(downloadAbleCouponInfo);
        }

        return displayCouponList;
    }

    // Helper methods as previously defined
    private int getJsonInt(JsonObject jsonObject, String key) {
        return jsonObject.get(key).isJsonNull() ? 0 : jsonObject.get(key).getAsInt();
    }

    private String formatPrice(NumberFormat formatter, int price) {
        return formatter.format(price);
    }

    private DisplayResponseDto.DisplayList shopbyGetDisplayEvents() throws UnirestException, ParseException {
        HttpResponse<String> response = Unirest.get(shopByUrl + "/display/events")
                .queryString("page.number", 1)
                .queryString("page.size", 5)
                .queryString("progressStatus", "ING")
                .header("accept", acceptHeader)
                // TODO: 2024-01-03 header의 'Version' 값을 2.0으로 요청해야 정상 동작합니다.
                .header("version", "2.0")
                .header("clientid", clientId)
                .header("platform", platformHeader)
                .header("content-type", acceptHeader)
                .asString();

        if (response.getStatus() != 200) {
            ShopBy.errorMessage(response);
        }

        List<DisplayResponseDto.DisplayInfo> displayInfoList = new ArrayList<>();

        JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
        int totalCount = jsonObject.get("totalCount").getAsInt();
        int totalPage = jsonObject.get("totalPage").getAsInt();
        JsonArray contents = jsonObject.getAsJsonObject().getAsJsonArray("contents");
        for (int i = 0; i < contents.size(); i++) {
            JsonObject item = contents.get(i).getAsJsonObject();
            int eventNo = item.get("eventNo").getAsInt();
            String label = item.get("label").getAsString();
            String eventId = item.get("id").getAsString();
            //String promotionText = item.get("promotionText").getAsString();
            String progressStatus = item.get("progressStatus").getAsString();

            DisplayResponseDto.DisplayInfo displayInfo = new DisplayResponseDto.DisplayInfo(
                    eventNo, label, eventId, null, progressStatus, null
            );
            displayInfoList.add(displayInfo);
        }

        return new DisplayResponseDto.DisplayList(totalCount, totalPage, displayInfoList);


    }

    /**
     * 0원 샘플체험은 moveKeyNumber에 기획전 상세 번호 필요
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 1/11/24
     **/
    public Integer getDisPlayDetail(DisplayDesignType displayDesignType, boolean isVisible) {
        int categoryNoInteger = 0;
        Optional<Display> freeSampleDisplay = displayRepository.findByDisplayDesignTypeAndIsVisible(displayDesignType, isVisible);
        if (freeSampleDisplay.isPresent()) {
            categoryNoInteger = freeSampleDisplay.get().getDisplayNo();
        }

        return categoryNoInteger;
    }

    /**
     * 홈 주간특가를 위한 샵바이 상품 진열관리 상품 조회
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 1/11/24
     **/
    public Map<DisplayType, List<DisplaySectionResponseDto.SectionItem>> getDisplaySectionItems() {
        // TODO: 3/11/24 주간특가, 홈 상품 커스텀 조합만 조회
        List<DisplayType> displayTypeList = Arrays.asList(
                DisplayType.NORMAL_ITEM_DISPLAY,
                DisplayType.HOT_DEAL,
                DisplayType.GROUP_PURCHASE,
                DisplayType.ZERO_EXPERIENCE
        );

        List<Display> displayList = displayRepository.findByIsVisibleAndDisplayTypeIn(true, displayTypeList);

        // DisplayType별로 그룹화하고 각 타입별로 첫 번째 Display 객체만 선택
        Map<DisplayType, Display> groupedByDisplayTypeWithFirstOnly = displayList.stream()
                .collect(Collectors.toMap(
                        Display::getDisplayType, // 키는 DisplayType
                        Function.identity(), // 값은 Display 객체 자체
                        (existing, replacement) -> existing // 충돌 발생 시, 기존 값을 유지 (즉, 첫 번째 요소 선택)
                ));

        // DisplayType을 키로 하고, DisplaySectionResponseDto.SectionItem 리스트를 값으로 하는 맵 초기화
        Map<DisplayType, List<DisplaySectionResponseDto.SectionItem>> displayTypeSectionItemsMap = new HashMap<>();

        // HOT_DEAL과 NORMAL_ITEM_DISPLAY 처리
        Arrays.asList(DisplayType.HOT_DEAL, DisplayType.NORMAL_ITEM_DISPLAY).forEach(displayType -> {
            if (groupedByDisplayTypeWithFirstOnly.containsKey(displayType)) {
                List<DisplaySectionResponseDto.SectionItem> sectionItems = null;
                try {
                    sectionItems = shopbyGetDisplaySectionItems(
                            groupedByDisplayTypeWithFirstOnly.get(displayType).getDisplayNo()
                    );
                } catch (UnirestException | ParseException e) {
                    throw new RuntimeException(e);
                }
                displayTypeSectionItemsMap.put(displayType, sectionItems);
            }
        });

        // ZERO_EXPERIENCE와 GROUP_PURCHASE 처리
        Arrays.asList(DisplayType.ZERO_EXPERIENCE, DisplayType.GROUP_PURCHASE).forEach(displayType -> {
            if (groupedByDisplayTypeWithFirstOnly.containsKey(displayType)) {
                DisplayResponseDto.DisplayInfo displayInfo = null;
                try {
                    displayInfo = shopbyGetDisplayEventDetail(
                            groupedByDisplayTypeWithFirstOnly.get(displayType).getDisplayNo()
                    ).getDisplayInfo();
                } catch (UnirestException | ParseException e) {
                    throw new RuntimeException(e);
                }

                List<DisplaySectionResponseDto.SectionItem> sectionItems = new ArrayList<>();
                sectionItems.add(new DisplaySectionResponseDto.SectionItem(displayInfo.getEventNo()));

                displayTypeSectionItemsMap.put(displayType, sectionItems);
            }
        });
        return displayTypeSectionItemsMap;
    }

    /**
     * 상품 진열 조회하기
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 4/15/24
     **/
    private List<DisplaySectionResponseDto.SectionItem> shopbyGetDisplaySectionItems(int sectionNo) throws UnirestException, ParseException {
        HttpResponse<String> response = Unirest.get(shopByUrl + "/display/sections/" + sectionNo)
                .queryString("by", "")
                .queryString("direction", "ASC")
                .queryString("soldout", true)
                .queryString("saleStatus", "")
                .queryString("pageNumber", 1)
                .queryString("pageSize", 10)
                .queryString("hasTotalCount", false)
                .queryString("hasOptionValues", false)
                .queryString("includeStopProduct", false)
                .header("accept", acceptHeader)
                .header("version", "1.1")
                .header("clientid", clientId)
                .header("platform", platformHeader)
                .header("content-type", acceptHeader)
                .asString();

        if (response.getStatus() != 200) {
            ShopBy.errorMessage(response);
        }

        List<DisplaySectionResponseDto.SectionItem> productList = new ArrayList<>();

        JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
        JsonArray productsArray = jsonObject.getAsJsonArray("products");

        for (JsonElement element : productsArray) {
            JsonObject productObject = element.getAsJsonObject();
            // JSON 객체에서 필요한 값을 추출
            int productNo = productObject.get("productNo").getAsInt();
            String productName = productObject.get("productName").getAsString();
            String brandName = productObject.get("brandNameKo").getAsString();
            String imageUrl = productObject.getAsJsonArray("listImageUrls").get(0).getAsString();
            imageUrl = "https:" + imageUrl;
            String displayCategoryNos = productObject.get("displayCategoryNos").getAsString();
            if (displayCategoryNos.length() >= 6) {
                displayCategoryNos = displayCategoryNos.substring(0, 6);
            }
            int salePrice = productObject.get("salePrice").getAsInt();
            int immediateDiscountAmt = productObject.get("immediateDiscountAmt").getAsInt();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String saleStartYmdtStr = productObject.get("sectionProductStartYmdt").getAsString();
            String saleEndYmdtStr = productObject.get("sectionProductEndYmdt").getAsString();
            LocalDateTime saleEndYmdt = LocalDateTime.parse(saleEndYmdtStr, formatter);
            LocalDateTime saleStartYmdt = saleEndYmdt.minusDays(1);

            DisplaySectionResponseDto.SectionItem product =
                    new DisplaySectionResponseDto.SectionItem(productNo, productName, brandName, imageUrl, displayCategoryNos,
                            salePrice, immediateDiscountAmt, saleStartYmdt, saleEndYmdt);
            productList.add(product);
        }

        return productList;
    }

    public int getDisplayEvents(DisplayDesignType displayDesignType) {
        Optional<Display> display = displayRepository.findByDisplayDesignTypeAndIsVisible(displayDesignType, true);
        int displayNo = 0;
        if (display.isPresent()) {
            displayNo = display.get().getDisplayNo();
        }
        return displayNo;
    }
}
