package com.example.sampleroad.domain.search;

import com.example.sampleroad.common.utils.TimeStamped;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "SEARCHKEYWORD")
@Builder
@AllArgsConstructor
public class SearchKeyword extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SEARCHKEYWORD_ID")
    private Long id;

    @Column(name = "SEARCH_WORD")
    private String searchWord;

    @Column(name = "SEARCH_COUNT")
    private int searchCount;

    @Column(name = "IS_VISIBLE")
    private boolean isVisible;

    public boolean getIsVisible() {
        return isVisible;
    }

    @Builder
    public SearchKeyword(String keyword) {
        this.searchWord = searchWord;
    }

    public void updateSearchCount() {
        this.searchCount += 1;
    }
}
