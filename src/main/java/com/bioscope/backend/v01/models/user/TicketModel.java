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
    private String showId;
    private String date;
    private String startTime;
    private String category;
    private Integer allowedPersons;
    private List<ShowSeatModel> seats;
    private String qrCode;
    private String orderId; // Razorpay order ID
    private String paymentId; // Razorpay payment ID
    private String paymentStatus; // PENDING, SUCCESS, FAILED
    private Double amount;
}
