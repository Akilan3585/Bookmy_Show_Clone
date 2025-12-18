package com.bookmyshow.dto;

import com.bookmyshow.model.Seat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatDTO {
    private Long id;
    private String seatNumber;
    private Seat.SeatStatus status;

    public static SeatDTO from(com.bookmyshow.model.Seat seat) {
        return new SeatDTO(seat.getId(), seat.getSeatNumber(), seat.getStatus());
    }
}
