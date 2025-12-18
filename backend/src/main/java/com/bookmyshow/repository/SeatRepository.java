package com.bookmyshow.repository;

import com.bookmyshow.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByShowId(Long showId);
    
    List<Seat> findByShowIdAndStatus(Long showId, Seat.SeatStatus status);
    long countByShowId(Long showId);
    
    Optional<Seat> findByShowIdAndSeatNumber(Long showId, String seatNumber);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Seat s WHERE s.show.id = :showId AND s.seatNumber IN :seatNumbers")
    List<Seat> findByShowIdAndSeatNumbersWithLock(@Param("showId") Long showId, @Param("seatNumbers") List<String> seatNumbers);
    
    void deleteByShowId(Long showId);
}
