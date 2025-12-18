package com.bookmyshow.repository;

import com.bookmyshow.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findByActiveTrue();
    List<Movie> findByActiveTrueOrderByReleaseDateDesc();
    List<Movie> findByGenreAndActiveTrue(String genre);
    List<Movie> findByLanguageAndActiveTrue(String language);
    List<Movie> findByTitleContainingIgnoreCaseAndActiveTrue(String title);
}
