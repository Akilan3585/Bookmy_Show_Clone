package com.bookmyshow.service;

import com.bookmyshow.model.Movie;
import com.bookmyshow.model.Show;
import com.bookmyshow.model.Theater;
import com.bookmyshow.model.Seat;
import com.bookmyshow.repository.ShowRepository;
import com.bookmyshow.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ShowService {
    
    @Autowired
    private ShowRepository showRepository;
    
    @Autowired
    private SeatRepository seatRepository;
    
    @Autowired
    private MovieService movieService;
    
    @Autowired
    private TheaterService theaterService;
    
    /**
     * Cache shows for a movie - frequently accessed when browsing show timings
     */
    @Cacheable(value = "shows", key = "'movie_' + #movieId")
    public List<Show> getShowsByMovie(Long movieId) {
        return showRepository.findByMovieIdAndActiveTrue(movieId);
    }
    
    /**
     * Cache shows by movie, city and date - most common query pattern
     */
    @Cacheable(value = "shows", key = "'movie_' + #movieId + '_city_' + #city + '_date_' + #date")
    public List<Show> getShowsByMovieAndCityAndDate(Long movieId, String city, LocalDate date) {
        return showRepository.findByMovieAndCityAndDate(movieId, city, date);
    }
    
    @Cacheable(value = "shows", key = "#id")
    public Show getShowById(Long id) {
        return showRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Show not found with id: " + id));
    }
    
    public List<Show> getShowsByTheater(Long theaterId) {
        return showRepository.findByTheaterIdAndActiveTrue(theaterId);
    }
    
    @Transactional
    @CacheEvict(value = "shows", allEntries = true)
    public Show createShow(Long movieId, Long theaterId, LocalDate showDate, 
                          String showTime, Double price) {
        Movie movie = movieService.getMovieById(movieId);
        Theater theater = theaterService.getTheaterById(theaterId);
        
        Show show = new Show();
        show.setMovie(movie);
        show.setTheater(theater);
        show.setShowDate(showDate);
        show.setShowTime(java.time.LocalTime.parse(showTime));
        show.setPrice(price);
        show.setAvailableSeats(theater.getTotalSeats());
        
        Show savedShow = showRepository.save(show);
        
        // Create seats for this show
        createSeatsForShow(savedShow, theater);
        
        return savedShow;
    }
    
    private void createSeatsForShow(Show show, Theater theater) {
        List<Seat> seats = new ArrayList<>();
        char rowChar = 'A';
        
        for (int row = 0; row < theater.getTotalRows(); row++) {
            for (int seatNum = 1; seatNum <= theater.getSeatsPerRow(); seatNum++) {
                Seat seat = new Seat();
                seat.setShow(show);
                seat.setSeatNumber(rowChar + String.valueOf(seatNum));
                seat.setStatus(Seat.SeatStatus.AVAILABLE);
                seats.add(seat);
            }
            rowChar++;
        }
        
        seatRepository.saveAll(seats);
    }
    
    @Transactional
    @CacheEvict(value = "shows", allEntries = true)
    public Show updateShow(Long id, Show showDetails) {
        Show show = getShowById(id);
        show.setShowDate(showDetails.getShowDate());
        show.setShowTime(showDetails.getShowTime());
        show.setPrice(showDetails.getPrice());
        return showRepository.save(show);
    }
    
    @Transactional
    @CacheEvict(value = "shows", allEntries = true)
    public void deleteShow(Long id) {
        Show show = getShowById(id);
        show.setActive(false);
        showRepository.save(show);
    }
}
