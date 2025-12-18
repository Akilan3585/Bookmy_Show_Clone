package com.bookmyshow.controller;

import com.bookmyshow.dto.ShowRequest;
import com.bookmyshow.model.Booking;
import com.bookmyshow.model.Movie;
import com.bookmyshow.model.Show;
import com.bookmyshow.model.Theater;
import com.bookmyshow.service.BookingService;
import com.bookmyshow.service.MovieService;
import com.bookmyshow.service.ShowService;
import com.bookmyshow.service.TheaterService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    @Autowired
    private MovieService movieService;
    
    @Autowired
    private TheaterService theaterService;
    
    @Autowired
    private ShowService showService;
    
    @Autowired
    private BookingService bookingService;
    
    // Movie Management
    @PostMapping("/movies")
    public ResponseEntity<Movie> createMovie(@Valid @RequestBody Movie movie) {
        return ResponseEntity.ok(movieService.createMovie(movie));
    }
    
    @PutMapping("/movies/{id}")
    public ResponseEntity<Movie> updateMovie(@PathVariable Long id, @Valid @RequestBody Movie movie) {
        return ResponseEntity.ok(movieService.updateMovie(id, movie));
    }
    
    @DeleteMapping("/movies/{id}")
    public ResponseEntity<?> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.ok(new MessageResponse("Movie deleted successfully"));
    }
    
    // Theater Management
    @PostMapping("/theaters")
    public ResponseEntity<Theater> createTheater(@Valid @RequestBody Theater theater) {
        return ResponseEntity.ok(theaterService.createTheater(theater));
    }
    
    @PutMapping("/theaters/{id}")
    public ResponseEntity<Theater> updateTheater(@PathVariable Long id, @Valid @RequestBody Theater theater) {
        return ResponseEntity.ok(theaterService.updateTheater(id, theater));
    }
    
    @DeleteMapping("/theaters/{id}")
    public ResponseEntity<?> deleteTheater(@PathVariable Long id) {
        theaterService.deleteTheater(id);
        return ResponseEntity.ok(new MessageResponse("Theater deleted successfully"));
    }
    
    // Show Management
    @PostMapping("/shows")
    public ResponseEntity<Show> createShow(@Valid @RequestBody ShowRequest request) {
        Show show = showService.createShow(
            request.getMovieId(),
            request.getTheaterId(),
            LocalDate.parse(request.getShowDate()),
            request.getShowTime(),
            request.getPrice()
        );
        return ResponseEntity.ok(show);
    }
    
    @PutMapping("/shows/{id}")
    public ResponseEntity<Show> updateShow(@PathVariable Long id, @Valid @RequestBody Show show) {
        return ResponseEntity.ok(showService.updateShow(id, show));
    }
    
    @DeleteMapping("/shows/{id}")
    public ResponseEntity<?> deleteShow(@PathVariable Long id) {
        showService.deleteShow(id);
        return ResponseEntity.ok(new MessageResponse("Show deleted successfully"));
    }
    
    // Booking Management
    @GetMapping("/bookings")
    public ResponseEntity<List<Booking>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }
    
    static class MessageResponse {
        private String message;
        
        public MessageResponse(String message) {
            this.message = message;
        }
        
        public String getMessage() {
            return message;
        }
    }
}
