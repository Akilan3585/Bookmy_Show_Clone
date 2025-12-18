package com.bookmyshow.config;

import com.bookmyshow.model.*;
import com.bookmyshow.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Data Loader - Loads sample data on application startup
 */
@Component
public class DataLoader implements CommandLineRunner {
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private MovieRepository movieRepository;
    
    @Autowired
    private TheaterRepository theaterRepository;
    
    @Autowired
    private ShowRepository showRepository;
    
    @Autowired
    private SeatRepository seatRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        loadRoles();
        loadUsers();
        loadMovies();
        loadTheaters();
        loadShows();
        ensureSeatsForShows();
        
        System.out.println("=============================================");
        System.out.println("Sample data loaded successfully!");
        System.out.println("=============================================");
        System.out.println("Admin User: admin / admin123");
        System.out.println("Test User: john / john123");
        System.out.println("=============================================");
    }
    
    private void loadRoles() {
        if (roleRepository.count() == 0) {
            Role userRole = new Role(null, Role.RoleName.ROLE_USER);
            Role adminRole = new Role(null, Role.RoleName.ROLE_ADMIN);
            roleRepository.saveAll(Arrays.asList(userRole, adminRole));
        }
    }
    
    private void loadUsers() {
        if (userRepository.count() == 0) {
            // Create admin user
            Role adminRole = roleRepository.findByName(Role.RoleName.ROLE_ADMIN).get();
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@bookmyshow.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setPhone("1234567890");
            admin.setCity("Mumbai");
            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(adminRole);
            admin.setRoles(adminRoles);
            userRepository.save(admin);
            
            // Create test user
            Role userRole = roleRepository.findByName(Role.RoleName.ROLE_USER).get();
            User user = new User();
            user.setUsername("john");
            user.setEmail("john@example.com");
            user.setPassword(passwordEncoder.encode("john123"));
            user.setFirstName("John");
            user.setLastName("Doe");
            user.setPhone("9876543210");
            user.setCity("Mumbai");
            Set<Role> userRoles = new HashSet<>();
            userRoles.add(userRole);
            user.setRoles(userRoles);
            userRepository.save(user);
        }
    }
    
    private void loadMovies() {
        if (movieRepository.count() == 0) {
            Movie movie1 = new Movie();
            movie1.setTitle("Inception");
            movie1.setDescription("A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O.");
            movie1.setGenre("Sci-Fi");
            movie1.setLanguage("English");
            movie1.setDuration(148);
            movie1.setRating(8.8);
            movie1.setReleaseDate(LocalDate.of(2010, 7, 16));
            movie1.setPosterUrl("https://m.media-amazon.com/images/I/81p+xe8cbnL._AC_SY679_.jpg");
            movie1.setTrailerUrl("https://www.youtube.com/watch?v=YoHD9XEInc0");
            movie1.setDirector("Christopher Nolan");
            movie1.setCast(Arrays.asList("Leonardo DiCaprio", "Joseph Gordon-Levitt", "Ellen Page"));
            
            Movie movie2 = new Movie();
            movie2.setTitle("The Dark Knight");
            movie2.setDescription("When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests.");
            movie2.setGenre("Action");
            movie2.setLanguage("English");
            movie2.setDuration(152);
            movie2.setRating(9.0);
            movie2.setReleaseDate(LocalDate.of(2008, 7, 18));
            movie2.setPosterUrl("https://m.media-amazon.com/images/I/91KkWf50SoL._AC_SY679_.jpg");
            movie2.setTrailerUrl("https://www.youtube.com/watch?v=EXeTwQWrcwY");
            movie2.setDirector("Christopher Nolan");
            movie2.setCast(Arrays.asList("Christian Bale", "Heath Ledger", "Aaron Eckhart"));
            
            Movie movie3 = new Movie();
            movie3.setTitle("Interstellar");
            movie3.setDescription("A team of explorers travel through a wormhole in space in an attempt to ensure humanity's survival.");
            movie3.setGenre("Sci-Fi");
            movie3.setLanguage("English");
            movie3.setDuration(169);
            movie3.setRating(8.6);
            movie3.setReleaseDate(LocalDate.of(2014, 11, 7));
            movie3.setPosterUrl("https://m.media-amazon.com/images/I/91obuWzA3XL._AC_SY679_.jpg");
            movie3.setTrailerUrl("https://www.youtube.com/watch?v=zSWdZVtXT7E");
            movie3.setDirector("Christopher Nolan");
            movie3.setCast(Arrays.asList("Matthew McConaughey", "Anne Hathaway", "Jessica Chastain"));
            
            Movie movie4 = new Movie();
            movie4.setTitle("Jawan");
            movie4.setDescription("A high-octane action thriller which outlines the emotional journey of a man who is set to rectify the wrongs in the society.");
            movie4.setGenre("Action");
            movie4.setLanguage("Hindi");
            movie4.setDuration(169);
            movie4.setRating(7.2);
            movie4.setReleaseDate(LocalDate.of(2023, 9, 7));
            movie4.setPosterUrl("https://m.media-amazon.com/images/I/81dBqg7qFvL._AC_SY679_.jpg");
            movie4.setTrailerUrl("https://www.youtube.com/watch?v=VlYk5jVUhlM");
            movie4.setDirector("Atlee");
            movie4.setCast(Arrays.asList("Shah Rukh Khan", "Nayanthara", "Vijay Sethupathi"));
            
            movieRepository.saveAll(Arrays.asList(movie1, movie2, movie3, movie4));
        }
    }
    
    private void loadTheaters() {
        if (theaterRepository.count() == 0) {
            Theater theater1 = new Theater();
            theater1.setName("PVR Phoenix");
            theater1.setCity("Mumbai");
            theater1.setAddress("High Street Phoenix, Lower Parel");
            theater1.setSeatsPerRow(10);
            theater1.setTotalRows(10);
            theater1.setTotalSeats(100);
            
            Theater theater2 = new Theater();
            theater2.setName("INOX Nariman Point");
            theater2.setCity("Mumbai");
            theater2.setAddress("Nariman Point, South Mumbai");
            theater2.setSeatsPerRow(12);
            theater2.setTotalRows(8);
            theater2.setTotalSeats(96);
            
            Theater theater3 = new Theater();
            theater3.setName("Cinepolis Andheri");
            theater3.setCity("Mumbai");
            theater3.setAddress("Andheri West");
            theater3.setSeatsPerRow(10);
            theater3.setTotalRows(10);
            theater3.setTotalSeats(100);
            
            theaterRepository.saveAll(Arrays.asList(theater1, theater2, theater3));
        }
    }
    
    private void loadShows() {
        if (showRepository.count() == 0) {
            List<Movie> movies = movieRepository.findAll();
            List<Theater> theaters = theaterRepository.findAll();
            
            LocalDate today = LocalDate.now();
            LocalDate tomorrow = today.plusDays(1);
            
            // Create shows for each movie in different theaters
            for (Movie movie : movies) {
                for (Theater theater : theaters) {
                    // Today's shows
                    createShow(movie, theater, today, LocalTime.of(10, 0), 250.0);
                    createShow(movie, theater, today, LocalTime.of(14, 30), 250.0);
                    createShow(movie, theater, today, LocalTime.of(18, 0), 300.0);
                    createShow(movie, theater, today, LocalTime.of(21, 30), 300.0);
                    
                    // Tomorrow's shows
                    createShow(movie, theater, tomorrow, LocalTime.of(10, 0), 250.0);
                    createShow(movie, theater, tomorrow, LocalTime.of(14, 30), 250.0);
                    createShow(movie, theater, tomorrow, LocalTime.of(18, 0), 300.0);
                    createShow(movie, theater, tomorrow, LocalTime.of(21, 30), 300.0);
                }
            }
        }
    }
    
    private void createShow(Movie movie, Theater theater, LocalDate date, LocalTime time, Double price) {
        Show show = new Show();
        show.setMovie(movie);
        show.setTheater(theater);
        show.setShowDate(date);
        show.setShowTime(time);
        show.setPrice(price);
        show.setAvailableSeats(theater.getTotalSeats());
        Show savedShow = showRepository.save(show);
        
        // Create seats for the show
        createSeatsForShow(savedShow, theater);
    }
    
    private void createSeatsForShow(Show show, Theater theater) {
        char rowChar = 'A';
        for (int row = 0; row < theater.getTotalRows(); row++) {
            for (int seatNum = 1; seatNum <= theater.getSeatsPerRow(); seatNum++) {
                Seat seat = new Seat();
                seat.setShow(show);
                seat.setSeatNumber(rowChar + String.valueOf(seatNum));
                seat.setStatus(Seat.SeatStatus.AVAILABLE);
                seatRepository.save(seat);
            }
            rowChar++;
        }
    }

    private void ensureSeatsForShows() {
        List<Show> shows = showRepository.findAll();
        for (Show show : shows) {
            long count = seatRepository.countByShowId(show.getId());
            if (count == 0) {
                createSeatsForShow(show, show.getTheater());
            }
        }
    }
}
