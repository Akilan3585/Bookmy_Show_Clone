package com.bookmyshow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ShowRequest {
    @NotNull
    private Long movieId;
    
    @NotNull
    private Long theaterId;
    
    @NotBlank
    private String showDate; // YYYY-MM-DD
    
    @NotBlank
    private String showTime; // HH:MM
    
    @NotNull
    private Double price;
}
