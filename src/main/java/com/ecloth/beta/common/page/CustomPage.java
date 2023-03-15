package com.ecloth.beta.common.page;

import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.swing.*;
import java.util.Locale;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CustomPage {

    private int pageNumber;
    private int recordSize;
    private String sortBy;
    private String sortOrder;

    public static CustomPage of(String sortBy, String sortOrder, Page<?> pageResult){
        return CustomPage.builder()
                .pageNumber(pageResult.getNumber())
                .recordSize(pageResult.getNumberOfElements())
                .sortBy(sortBy)
                .sortOrder(sortOrder)
                .build();
    }

    public int getStartIdx(){
        return getEndIdx() - recordSize + 1;
    }

    public int getEndIdx(){
        return pageNumber * recordSize - 1;
    }

}
