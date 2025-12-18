package com.bookmyshow.service;

import com.bookmyshow.model.Seat;
import com.bookmyshow.model.Show;
import com.bookmyshow.repository.SeatRepository;
import com.bookmyshow.repository.ShowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SeatService {
    
    @Autowired
    private SeatRepository seatRepository;
    
    @Autowired
    private ShowRepository showRepository;
    
    /**
     * Cache seat availability - frequently checked before booking
     * Cache evicted after every booking to ensure accuracy
     */
    @Cacheable(value = "seats", key = "'show_' + #showId")
    public List<Seat> getSeatsByShow(Long showId) {
        return seatRepository.findByShowId(showId);
    }

    public long countSeatsByShow(Long showId) {
        return seatRepository.countByShowId(showId);
    }
    
    @Cacheable(value = "seats", key = "'show_' + #showId + '_available'")
    public List<Seat> getAvailableSeats(Long showId) {
        return seatRepository.findByShowIdAndStatus(showId, Seat.SeatStatus.AVAILABLE);
    }
    
    public List<Seat> getBookedSeats(Long showId) {
        return seatRepository.findByShowIdAndStatus(showId, Seat.SeatStatus.BOOKED);
    }
    
    /**
     * Pessimistic locking to prevent double booking
     * Critical for concurrency safety
     */
    @Transactional
    @CacheEvict(value = {"seats", "shows"}, allEntries = true)
    public boolean blockSeats(Long showId, List<String> seatNumbers) {
        List<Seat> seats = seatRepository.findByShowIdAndSeatNumbersWithLock(showId, seatNumbers);
        
        if (seats.size() != seatNumbers.size()) {
            throw new RuntimeException("Some seats not found");
        }
        
        for (Seat seat : seats) {
            if (seat.getStatus() != Seat.SeatStatus.AVAILABLE) {
                throw new RuntimeException("Seat " + seat.getSeatNumber() + " is not available");
            }
            seat.setStatus(Seat.SeatStatus.BLOCKED);
        }
        
        seatRepository.saveAll(seats);
        return true;
    }
    
    /**
     * Confirm seat booking after payment
     * Evict caches to reflect updated availability
     */
    @Transactional
    @CacheEvict(value = {"seats", "shows"}, allEntries = true)
    public void confirmSeats(Long showId, List<String> seatNumbers) {
        List<Seat> seats = seatRepository.findByShowIdAndSeatNumbersWithLock(showId, seatNumbers);
        
        for (Seat seat : seats) {
            seat.setStatus(Seat.SeatStatus.BOOKED);
        }
        
        seatRepository.saveAll(seats);
        
        // Update show available seats
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new RuntimeException("Show not found"));
        show.setAvailableSeats(show.getAvailableSeats() - seatNumbers.size());
        showRepository.save(show);
    }
    
    @Transactional
    @CacheEvict(value = {"seats", "shows"}, allEntries = true)
    public void releaseSeats(Long showId, List<String> seatNumbers) {
        List<Seat> seats = seatRepository.findByShowIdAndSeatNumbersWithLock(showId, seatNumbers);
        
        for (Seat seat : seats) {
            if (seat.getStatus() == Seat.SeatStatus.BLOCKED) {
                seat.setStatus(Seat.SeatStatus.AVAILABLE);
            }
        }
        
        seatRepository.saveAll(seats);
    }
}
