package com.example.sampleroad.service;

import com.example.sampleroad.common.utils.ShopBy;
import com.example.sampleroad.domain.Category;
import com.example.sampleroad.dto.response.CategoryResponseDto;
import com.example.sampleroad.dto.response.home.HomeResponseDto;
import com.example.sampleroad.repository.category.CategoryRepository;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;

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

    @Value("${shop-by.event-category-no}")
    int eventCategoryNo;

    @Value("${shop-by.perfume-category-no}")
    int perfumeCategoryNo;

    Gson gson = new Gson();

    public CategoryResponseDto.FlatCategory getAllCategory() {
        List<CategoryResponseDto> dto = shopbyGetAllCategory();
        return new CategoryResponseDto.FlatCategory(dto);
    }

    public HomeResponseDto.HomeCategoryList getHomeCategory() {
        List<Category> categoryList = categoryRepository.findByIsHomeVisibleAndHomeVisibleNumberNotNullOrderByHomeVisibleNumber(true);
        List<HomeResponseDto.CategoryInfo> categoryInfoList = new ArrayList<>();

        for (Category category : categoryList) {
            HomeResponseDto.CategoryInfo categoryInfo = createCategoryInfo(category);
            categoryInfoList.add(categoryInfo);
        }

        return new HomeResponseDto.HomeCategoryList(categoryInfoList);
    }


    private HomeResponseDto.CategoryInfo createCategoryInfo(Category category) {
        int depthNumber = category.getCategoryDepthNumber1() == 1
                ? 1
                : (category.getCategoryDepthNumber3() != 0
                ? category.getCategoryDepthNumber3()
                : (category.getCategoryDepthNumber2() != 0
                ? category.getCategoryDepthNumber2()
                : 0));

        if (category.getCategoryDepthNumber1() == 1) {
            return new HomeResponseDto.CategoryInfo(depthNumber, category.getCategoryName(), category.getIconUrl(), "NEW", "#0099FC", category.getSearchSortType());
        } else if (category.getCategoryDepthNumber3() == perfumeCategoryNo) {
            return new HomeResponseDto.CategoryInfo(depthNumber, category.getCategoryName(), category.getIconUrl(), "HOT", "#FF574C", category.getSearchSortType());
        } else if (category.getCategoryDepthNumber3() == 2) {
            return new HomeResponseDto.CategoryInfo(depthNumber, category.getCategoryName(), category.getIconUrl(), "NEW", "#0099FC", category.getSearchSortType());
        } else {
            return new HomeResponseDto.CategoryInfo(depthNumber, category.getCategoryName(), category.getIconUrl(), null, null, category.getSearchSortType());
        }
    }

    private List<CategoryResponseDto> shopbyGetAllCategory() {
        try {

            HttpResponse<String> response = Unirest.get(shopByUrl + "/categories")
                    .header("accept", acceptHeader)
                    .header("version", versionHeader)
                    .header("clientid", clientId)
                    .header("platform", platformHeader)
                    .header("content-type", acceptHeader)
                    .asString();

            log.info("카테고리 조회_____________________________");
            ShopBy.errorMessage(response);
            log.info("카테고리 조회_____________________________");

            JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
            JsonArray jsonArray = jsonObject.getAsJsonArray("flatCategories");
            List<CategoryResponseDto> result = new ArrayList<>();

            for (JsonElement jsonElement : jsonArray) {
                JsonObject categoryObject = jsonElement.getAsJsonObject();
                String fullCategoryName = categoryObject.get("fullCategoryName").getAsString();

                // TODO: 2023/07/28 상품상세 카테고리 표시 관련
                String prefix = "샘플>";
                if (fullCategoryName.startsWith(prefix)) {
                    fullCategoryName = fullCategoryName.substring(prefix.length());
                }

                String depth2Icon = categoryObject.get("depth2Icon").getAsString();
                String depth3Icon = categoryObject.get("depth3Icon").getAsString();

                int depth1CategoryNo = categoryObject.get("depth1CategoryNo").getAsInt();
                String depth1Label = categoryObject.get("depth1Label").getAsString();
                int depth1DisplayOrder = categoryObject.get("depth1DisplayOrder").getAsInt();

                int depth2CategoryNo = categoryObject.get("depth2CategoryNo").getAsInt();
                String depth2Label = categoryObject.get("depth2Label").getAsString();
                int depth2DisplayOrder = categoryObject.get("depth2DisplayOrder").getAsInt();

                int depth3CategoryNo = categoryObject.get("depth3CategoryNo").getAsInt();
                if (depth3CategoryNo == 0) {
                    depth3CategoryNo = depth2CategoryNo;
                }
                String depth3Label = categoryObject.get("depth3Label").getAsString();
                int depth3DisplayOrder = categoryObject.get("depth3DisplayOrder").getAsInt();

                int depth4CategoryNo = categoryObject.get("depth4CategoryNo").getAsInt();
                String depth4Label = categoryObject.get("depth4Label").getAsString();
                int depth4DisplayOrder = categoryObject.get("depth4DisplayOrder").getAsInt();

                int depth5CategoryNo = categoryObject.get("depth5CategoryNo").getAsInt();
                String depth5Label = categoryObject.get("depth5Label").getAsString();
                int depth5DisplayOrder = categoryObject.get("depth5DisplayOrder").getAsInt();

                if (depth2CategoryNo != eventCategoryNo) {
                    CategoryResponseDto dto = new CategoryResponseDto(
                            depth2Icon, depth3Icon
                            , depth1CategoryNo, depth1Label, depth1DisplayOrder
                            , depth2CategoryNo, depth2Label, depth2DisplayOrder
                            , depth3CategoryNo, depth3Label, depth3DisplayOrder
                            , depth4CategoryNo, depth4Label, depth4DisplayOrder
                            , depth5CategoryNo, depth5Label, depth5DisplayOrder
                            , fullCategoryName);

                    result.add(dto);
                }
            }
            return result;
        } catch (UnirestException | ParseException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<String> findCategoriesByDepthNumbers(List<String> categoryNos) {
        List<Integer> categoryNosInteger = convertStringListToIntegerList(categoryNos);
        List<Category> categoriesByDepthNumbers = categoryRepository.findCategoriesByDepthNumbers(categoryNosInteger,false);
        List<Integer> depth2CategoryNo = categoriesByDepthNumbers.stream().map(Category::getCategoryDepthNumber2).collect(Collectors.toList());

        return categoryRepository.findCategoriesByDepthNumbers(depth2CategoryNo, true)
                .stream()
                .map(category -> String.valueOf(category.getCategoryDepthNumber2()))
                .collect(Collectors.toList());
    }


    private List<Integer> convertStringListToIntegerList(List<String> stringList) {
        return stringList.stream()
                .flatMap(s -> {
                    try {
                        return Stream.of(Integer.parseInt(s));
                    } catch (NumberFormatException e) {
                        // Log the error or handle it as needed
                        System.err.println("Invalid integer format: " + s);
                        return Stream.empty();  // Skip the entry
                    }
                })
                .collect(Collectors.toList());
    }

}
