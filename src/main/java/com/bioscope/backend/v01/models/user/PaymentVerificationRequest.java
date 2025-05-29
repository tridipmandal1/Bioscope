package com.bioscope.backend.v01.models.user;


import lombok.Data;

@Data
public class PaymentVerificationRequest {

    private String orderId;
    private String paymentId;
    private String signature;
    private String ticketId;
}