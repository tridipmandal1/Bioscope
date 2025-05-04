package com.bioscope.backend.v01.models.host;

import lombok.Data;
import java.util.List;

@Data
public class RowData {
    private String rowIndex;
    private Integer seatsInRow;
    private Integer priceInRow;
    private List<Integer> passageFollowed;
    private String category;
}