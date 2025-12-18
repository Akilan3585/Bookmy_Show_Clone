package com.bookmyshow.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class BookingRequest {
    @NotNull
    private Long showId;
    
    @NotEmpty
    private List<String> seatNumbers;
    
    @NotNull
    private String paymentMethod; // CARD, UPI, NET_BANKING, WALLET
}
