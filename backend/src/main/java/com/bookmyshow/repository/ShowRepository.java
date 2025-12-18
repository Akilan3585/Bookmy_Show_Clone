package com.bookmyshow.repository;

import com.bookmyshow.model.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {
    List<Show> findByMovieIdAndActiveTrue(Long movieId);
    
    List<Show> findByTheaterIdAndActiveTrue(Long theaterId);
    
    @Query("SELECT s FROM Show s WHERE s.movie.id = :movieId AND s.theater.city = :city AND s.showDate >= :date AND s.active = true ORDER BY s.showDate, s.showTime")
    List<Show> findByMovieAndCityAndDate(@Param("movieId") Long movieId, @Param("city") String city, @Param("date") LocalDate date);
    
    @Query("SELECT s FROM Show s WHERE s.movie.id = :movieId AND s.showDate = :date AND s.active = true ORDER BY s.showTime")
    List<Show> findByMovieIdAndShowDate(@Param("movieId") Long movieId, @Param("date") LocalDate date);
    
    List<Show> findByShowDateAndActiveTrue(LocalDate showDate);
}
