package com.bioscope.backend.v01.models.user;

import com.bioscope.backend.v01.models.host.ShowSeatModel;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketModel {

    private String id;
    private String hostId;
    private String showName;
    private String date;
    private String startTime;
    private String category;
    private Integer allowedPersons;
    private List<ShowSeatModel> seats;
    private String qrCode;
}
