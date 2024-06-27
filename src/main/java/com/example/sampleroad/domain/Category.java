package com.example.sampleroad.domain;

import com.example.sampleroad.domain.search.SearchSortType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "CATEGORY")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CATEGORY_ID")
    private Long id;

    @Column(name = "CATEGORY_NUMBER_1")
    private int categoryDepthNumber1;

    @Column(name = "CATEGORY_NUMBER_2")
    private int categoryDepthNumber2;

    @Column(name = "CATEGORY_NUMBER_3")
    private int categoryDepthNumber3;

    @Column(name = "CATEGORY_NAME")
    private String categoryName;

    @Column(name = "CATEGORY_TYPE_1")
    @Enumerated(EnumType.STRING)
    private CategoryType categoryDepth1;

    @Column(name = "CATEGORY_TYPE_2")
    @Enumerated(EnumType.STRING)
    private CategoryType categoryDepth2;

    @Column(name = "CATEGORY_TYPE_3")
    @Enumerated(EnumType.STRING)
    private CategoryType categoryDepth3;

    @Column(name = "IS_HOME_VISIBLE")
    private Boolean isHomeVisible;

    @Column(name = "HOME_VISIBLE_NUMBER")
    private int homeVisibleNumber;

    @Column(name = "ICON_URL")
    private String iconUrl;

    @Column(name = "SORT_TYPE")
    @Enumerated(EnumType.STRING)
    private SearchSortType searchSortType;
}


/*
* 카테고리 엔티티
* 카테고리 타입은 1,2,3으로 구분된다
*
* 타입1은 샘플이냐 키트이냐
*    ex) 샘플이냐 관리자키트이냐
* 타입2는 샘플,샘플키트, 스킨케어, 클렌징, 선케어...
* 타입3은 선케어>기본선크림 || 선케어>톤업선크림
*
* */