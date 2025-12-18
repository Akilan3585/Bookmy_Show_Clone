package com.bookmyshow.controller;

import com.bookmyshow.model.Seat;
import com.bookmyshow.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
@CrossOrigin(origins = "*")
public class SeatController {
    
    @Autowired
    private SeatService seatService;
    
    @GetMapping("/show/{showId}")
    public ResponseEntity<java.util.List<com.bookmyshow.dto.SeatDTO>> getSeatsByShow(@PathVariable Long showId) {
        java.util.List<com.bookmyshow.model.Seat> seats = seatService.getSeatsByShow(showId);
        java.util.List<com.bookmyshow.dto.SeatDTO> dtos = seats.stream().map(com.bookmyshow.dto.SeatDTO::from).toList();
        return ResponseEntity.ok(dtos);
    }
    
    @GetMapping("/show/{showId}/available")
    public ResponseEntity<List<Seat>> getAvailableSeats(@PathVariable Long showId) {
        return ResponseEntity.ok(seatService.getAvailableSeats(showId));
    }

    @GetMapping("/show/{showId}/count")
    public ResponseEntity<Long> getSeatCount(@PathVariable Long showId) {
        return ResponseEntity.ok(seatService.countSeatsByShow(showId));
    }
    
    @GetMapping("/show/{showId}/booked")
    public ResponseEntity<List<Seat>> getBookedSeats(@PathVariable Long showId) {
        return ResponseEntity.ok(seatService.getBookedSeats(showId));
    }
}
