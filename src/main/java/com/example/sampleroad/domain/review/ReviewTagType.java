package com.example.sampleroad.domain.review;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum ReviewTagType {
    CLEANSING("CLEANSING", Arrays.asList(
            "산뜻함", "가벼움", "세정력 좋음", "향이 좋음",
            "촉촉함", "개운함", "끈적이지 않음", "잔여감 없음", "순함",
            "부드러움", "당김 없음", "거품이 잘 남", "쫀쫀함", "피부 진정",
            "트러블 개선","아침 세안하기 좋음", "트러블이 나지 않음"
    )),

    SKINCARE("SKINCARE", Arrays.asList(
            "끈적이지 않음", "흡수력 빠름", "저자극", "촉촉함",
            "보습", "진정 케어", "피부색 밝아짐", "각질 케어",
            "결 정돈", "순함", "산뜻함", "속건조 개선", "기름지지 않음",
            "가벼움", "부드러움" ,"나이트케어",
            "화장이 안 밀림","매일 쓰기 좋음"
    )),

    SUNCARE("SUNCARE", Arrays.asList(
            "산뜻함", "부드러움", "저자극", "촉촉함",
            "화장이 안 밀림", "흡수 잘 됨", "기름지지 않음", "발림성 좋음",
            "눈 시림 없음", "톤업 효과", "백탁 없음", "향이 좋음",
            "가벼움"
    )),

    ETC("ETC", Arrays.asList(
            "오래쓰는", "튼튼한", "따갑지않은", "보습되는",
            "뻣뻣하지 않은", "향에 만족한", "향 지속되는", "자연스러운",
            "산뜻한", "잔향", "차분해지는", "달달한",
            "거품 퐁퐁", "가렵지 않은", "쿨링되는", "무거운",
            "세정되는"
    )),

    PERFUME("PERFUME", Arrays.asList(
            "우디", "머스크", "프루티",
            "플로럴", "시트러스", "오리엔탈",
            "신선한", "깨끗한", "열대적인",
            "남성스러운", "여성스러운", "고급스러운",
            "지속력", "가벼움", "무게감 있는"
    )),

    MAKEUP("MAKEUP", Arrays.asList(
            "잘 발리는", "자연스러운", "가벼운", "지속되는",
            "커버되는", "색감 좋은", "밀착되는", "발색 좋은",
            "윤기나는", "보습되는", "매트한", "뽀송한",
            "얇게 발리는", "유분 없는", "수분 있는"
    )),

    EMPTY("없음", Collections.emptyList());

    private String title;
    private List<String> tagList;

    ReviewTagType(String title, List<String> tagList) {
        this.title = title;
        this.tagList = tagList;
    }

    public List<String> getTagList() {
        return tagList;
    }
}
