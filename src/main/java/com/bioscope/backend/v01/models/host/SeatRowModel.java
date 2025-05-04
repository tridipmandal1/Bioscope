package com.bioscope.backend.v01.models.host;

import lombok.Data;
import java.util.List;

@Data
public class SeatRowModel {

    private String rowId;
    private String rowIndex;
    private String seatCategory;
    private List<Integer> passageAfterwards;
    private List<SeatModel> seats;
}
