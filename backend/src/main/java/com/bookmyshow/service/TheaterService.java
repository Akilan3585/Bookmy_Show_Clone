package com.bookmyshow.service;

import com.bookmyshow.model.Theater;
import com.bookmyshow.repository.TheaterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TheaterService {
    
    @Autowired
    private TheaterRepository theaterRepository;
    
    /**
     * Cache theaters by city - frequently accessed when selecting shows
     */
    @Cacheable(value = "theaters", key = "'city_' + #city")
    public List<Theater> getTheatersByCity(String city) {
        return theaterRepository.findByCityAndActiveTrue(city);
    }
    
    @Cacheable(value = "theaters", key = "'all'")
    public List<Theater> getAllTheaters() {
        return theaterRepository.findByActiveTrue();
    }
    
    @Cacheable(value = "theaters", key = "#id")
    public Theater getTheaterById(Long id) {
        return theaterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Theater not found with id: " + id));
    }
    
    @Transactional
    @CacheEvict(value = "theaters", allEntries = true)
    public Theater createTheater(Theater theater) {
        theater.setTotalSeats(theater.getSeatsPerRow() * theater.getTotalRows());
        return theaterRepository.save(theater);
    }
    
    @Transactional
    @CacheEvict(value = "theaters", allEntries = true)
    public Theater updateTheater(Long id, Theater theaterDetails) {
        Theater theater = getTheaterById(id);
        theater.setName(theaterDetails.getName());
        theater.setCity(theaterDetails.getCity());
        theater.setAddress(theaterDetails.getAddress());
        theater.setSeatsPerRow(theaterDetails.getSeatsPerRow());
        theater.setTotalRows(theaterDetails.getTotalRows());
        theater.setTotalSeats(theaterDetails.getSeatsPerRow() * theaterDetails.getTotalRows());
        return theaterRepository.save(theater);
    }
    
    @Transactional
    @CacheEvict(value = "theaters", allEntries = true)
    public void deleteTheater(Long id) {
        Theater theater = getTheaterById(id);
        theater.setActive(false);
        theaterRepository.save(theater);
    }
}
