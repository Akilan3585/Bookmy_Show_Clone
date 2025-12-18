package com.bookmyshow.service;

import com.bookmyshow.model.Movie;
import com.bookmyshow.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MovieService {
    
    @Autowired
    private MovieRepository movieRepository;
    
    /**
     * Cache movies list - frequently accessed, rarely changes
     * Reduces DB queries for movie browsing
     */
    @Cacheable(value = "movies", key = "'all'")
    public List<Movie> getAllMovies() {
        return movieRepository.findByActiveTrueOrderByReleaseDateDesc();
    }
    
    /**
     * Cache individual movie details - frequently accessed for movie detail page
     */
    @Cacheable(value = "movies", key = "#id")
    public Movie getMovieById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found with id: " + id));
    }
    
    @Cacheable(value = "movies", key = "'genre_' + #genre")
    public List<Movie> getMoviesByGenre(String genre) {
        return movieRepository.findByGenreAndActiveTrue(genre);
    }
    
    @Cacheable(value = "movies", key = "'language_' + #language")
    public List<Movie> getMoviesByLanguage(String language) {
        return movieRepository.findByLanguageAndActiveTrue(language);
    }
    
    public List<Movie> searchMovies(String title) {
        return movieRepository.findByTitleContainingIgnoreCaseAndActiveTrue(title);
    }
    
    /**
     * Evict all movie caches when new movie is created
     */
    @Transactional
    @CacheEvict(value = "movies", allEntries = true)
    public Movie createMovie(Movie movie) {
        return movieRepository.save(movie);
    }
    
    /**
     * Update cache for specific movie and evict list caches
     */
    @Transactional
    @CacheEvict(value = "movies", allEntries = true)
    public Movie updateMovie(Long id, Movie movieDetails) {
        Movie movie = getMovieById(id);
        movie.setTitle(movieDetails.getTitle());
        movie.setDescription(movieDetails.getDescription());
        movie.setGenre(movieDetails.getGenre());
        movie.setLanguage(movieDetails.getLanguage());
        movie.setDuration(movieDetails.getDuration());
        movie.setRating(movieDetails.getRating());
        movie.setReleaseDate(movieDetails.getReleaseDate());
        movie.setPosterUrl(movieDetails.getPosterUrl());
        movie.setTrailerUrl(movieDetails.getTrailerUrl());
        movie.setDirector(movieDetails.getDirector());
        movie.setCast(movieDetails.getCast());
        return movieRepository.save(movie);
    }
    
    @Transactional
    @CacheEvict(value = "movies", allEntries = true)
    public void deleteMovie(Long id) {
        Movie movie = getMovieById(id);
        movie.setActive(false);
        movieRepository.save(movie);
    }
}
