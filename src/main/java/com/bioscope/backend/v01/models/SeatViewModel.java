package com.bioscope.backend.v01.models;


import com.bioscope.backend.v01.constants.SeatId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeatViewModel {

    private String showName;
    private String showDate;
    private String showTime;
    private SeatId seatId;
    private String category;
    private Integer allowedPersons;

    public SeatViewModel(String showName, String showDate, String showTime, SeatId seatId) {
        this.showName = showName;
        this.showDate = showDate;
        this.showTime = showTime;
        this.seatId = seatId;
    }

    public SeatViewModel(String showName, String showDate,
                         String showTime, String category,
                         Integer allowedPersons) {
        this.showName = showName;
        this.showDate = showDate;
        this.showTime = showTime;
        this.category = category;
        this.allowedPersons = allowedPersons;
    }
}
