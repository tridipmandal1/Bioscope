package com.bioscope.backend.v01.models.host;

import lombok.Data;

import java.util.List;

@Data
public class ScreenRequestModel {

    private String screenName;
    private String arrangementType;
    private Integer capacity;
    private List<RowData> rowData;
}

