package com.bioscope.backend.v01.services.iface;

import com.bioscope.backend.v01.models.user.PaymentVerificationRequest;
import com.bioscope.backend.v01.models.user.TicketModel;

import java.util.List;

public interface BookingService {

    TicketModel bookEntryPass(String showId, String category, Integer quantity, Double amount);
    TicketModel bookSeats(String showId, List<String> showSeatIds, Double amount);
    boolean verifyPayment(PaymentVerificationRequest verificationRequest);
    void cancelBooking(String showId, List<String> seatIds, String ticketId);
    void cancelEntryPass(TicketModel ticket);
}
