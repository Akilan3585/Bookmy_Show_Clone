# BookMyShow - Movie Ticket Booking Application

A full-stack movie ticket booking application built with **Java Spring Boot** backend and **Vanilla JavaScript** frontend, featuring JWT authentication, caching, and concurrency-safe seat booking.

## 🎯 Features

### User Features
- **Authentication**: Register, Login with JWT token-based authentication
- **Browse Movies**: View all movies with filters by genre, language
- **Search**: Search movies by title
- **City Selection**: Select city to view theaters and shows
- **Movie Details**: View detailed movie information
- **Show Selection**: Browse shows by date, time, and theater
- **Seat Selection**: Interactive seat map with real-time availability
- **Booking**: Book tickets with seat selection
- **Payment Simulation**: Multiple payment methods (Card, UPI, Net Banking, Wallet)
- **Booking History**: View all past bookings
- **Booking Confirmation**: Get booking reference and confirmation details

### Admin Features
- **Admin Dashboard**: Dedicated admin panel
- **Movie Management**: Add, edit, delete movies
- **Theater Management**: Add, edit, delete theaters
- **Show Management**: Create shows (movie + theater + time)
- **View All Bookings**: Monitor all user bookings

## 🏗️ Architecture

### Backend Architecture

```
bookmyshow/backend/
├── src/main/java/com/bookmyshow/
│   ├── BookMyShowApplication.java          # Main application class
│   ├── config/
│   │   ├── SecurityConfig.java             # Spring Security + JWT configuration
│   │   ├── CacheConfig.java                # Spring Cache configuration
│   │   └── DataLoader.java                 # Sample data initialization
│   ├── controller/
│   │   ├── AuthController.java             # Authentication APIs
│   │   ├── MovieController.java            # Movie APIs
│   │   ├── TheaterController.java          # Theater APIs
│   │   ├── ShowController.java             # Show APIs
│   │   ├── SeatController.java             # Seat APIs
│   │   ├── BookingController.java          # Booking APIs
│   │   └── AdminController.java            # Admin APIs
│   ├── dto/
│   │   ├── SignupRequest.java              # Registration DTO
│   │   ├── LoginRequest.java               # Login DTO
│   │   ├── JwtResponse.java                # JWT response DTO
│   │   ├── BookingRequest.java             # Booking DTO
│   │   └── ShowRequest.java                # Show creation DTO
│   ├── model/
│   │   ├── User.java                       # User entity
│   │   ├── Role.java                       # Role entity (USER/ADMIN)
│   │   ├── Movie.java                      # Movie entity
│   │   ├── Theater.java                    # Theater entity
│   │   ├── Show.java                       # Show entity
│   │   ├── Seat.java                       # Seat entity
│   │   ├── Booking.java                    # Booking entity
│   │   └── Payment.java                    # Payment entity
│   ├── repository/
│   │   ├── UserRepository.java
│   │   ├── RoleRepository.java
│   │   ├── MovieRepository.java
│   │   ├── TheaterRepository.java
│   │   ├── ShowRepository.java
│   │   ├── SeatRepository.java
│   │   ├── BookingRepository.java
│   │   └── PaymentRepository.java
│   ├── security/
│   │   ├── JwtTokenProvider.java           # JWT token generation/validation
│   │   ├── JwtAuthenticationFilter.java    # JWT filter
│   │   └── CustomUserDetailsService.java   # User details service
│   └── service/
│       ├── AuthService.java                # Authentication service
│       ├── MovieService.java               # Movie service with caching
│       ├── TheaterService.java             # Theater service with caching
│       ├── ShowService.java                # Show service with caching
│       ├── SeatService.java                # Seat service with locking
│       └── BookingService.java             # Booking service
└── src/main/resources/
    └── application.properties              # Application configuration
```

### Frontend Architecture

```
bookmyshow/frontend/
├── index.html                  # Home page - Movie listing
├── login.html                  # Login page
├── register.html               # Registration page
├── movie-details.html          # Movie details & show selection
├── seat-selection.html         # Seat selection page
├── payment.html                # Payment page
├── confirmation.html           # Booking confirmation
├── my-bookings.html            # User booking history
├── admin.html                  # Admin dashboard
├── css/
│   └── style.css              # BookMyShow-style responsive CSS
└── js/
    ├── config.js              # API configuration
    ├── auth.js                # Authentication logic
    ├── main.js                # Home page logic
    ├── movie-details.js       # Movie details logic
    ├── seat-selection.js      # Seat selection logic
    ├── payment.js             # Payment logic
    ├── confirmation.js        # Confirmation logic
    ├── my-bookings.js         # Bookings history logic
    └── admin.js               # Admin dashboard logic
```

## 📊 Database Schema

### Entity Relationships

```
User (1) -----> (N) Booking
Role (N) <-----> (N) User
Movie (1) -----> (N) Show
Theater (1) -----> (N) Show
Show (1) -----> (N) Seat
Show (1) -----> (N) Booking
Booking (1) -----> (1) Payment
```

### Key Tables
- **users**: User information with roles (USER/ADMIN)
- **roles**: Role definitions
- **movies**: Movie details (title, genre, language, duration, etc.)
- **theaters**: Theater information (name, city, address, seat layout)
- **shows**: Show timings (movie + theater + date + time + price)
- **seats**: Seat availability per show (AVAILABLE/BOOKED/BLOCKED)
- **bookings**: User bookings with status (PENDING/CONFIRMED/CANCELLED)
- **payments**: Payment records with transaction details

## 🔐 Security Implementation

### JWT Authentication
1. User logs in with username/password
2. Backend validates credentials and generates JWT token
3. Token includes username and expiration time
4. Frontend stores token in localStorage
5. All authenticated requests include: `Authorization: Bearer <token>`
6. Backend validates token on each request

### Password Encryption
- BCrypt password encoding (10 rounds)
- Passwords never stored in plain text
- Secure password comparison

### Role-Based Access Control
- **USER Role**: Can browse, book tickets, view own bookings
- **ADMIN Role**: Full access to add/edit/delete movies, theaters, shows

## 💾 Caching Strategy

### Cache Implementation
Using **Spring Cache** with in-memory ConcurrentHashMap (can be replaced with Redis).

### Cache Locations

#### 1. **Movies Cache** (`movies`)
**Purpose**: Reduce database queries for frequently browsed movies
- **Cached Methods**:
  - `getAllMovies()` - Key: `'all'`
  - `getMovieById(id)` - Key: `id`
  - `getMoviesByGenre(genre)` - Key: `'genre_' + genre`
  - `getMoviesByLanguage(lang)` - Key: `'language_' + lang`
- **Eviction**: When admin creates/updates/deletes movies
- **Benefit**: Fast movie browsing without DB hits

#### 2. **Theaters Cache** (`theaters`)
**Purpose**: Quick theater lookup by city
- **Cached Methods**:
  - `getTheatersByCity(city)` - Key: `'city_' + city`
  - `getAllTheaters()` - Key: `'all'`
  - `getTheaterById(id)` - Key: `id`
- **Eviction**: When admin modifies theater data
- **Benefit**: Instant theater list for show selection

#### 3. **Shows Cache** (`shows`)
**Purpose**: Fast show timing retrieval
- **Cached Methods**:
  - `getShowsByMovie(movieId)` - Key: `'movie_' + movieId`
  - `getShowsByMovieAndCityAndDate()` - Key: `'movie_' + movieId + '_city_' + city + '_date_' + date`
  - `getShowById(id)` - Key: `id`
- **Eviction**: After show creation/modification or booking completion
- **Benefit**: Quick show time display

#### 4. **Seats Cache** (`seats`)
**Purpose**: Real-time seat availability
- **Cached Methods**:
  - `getSeatsByShow(showId)` - Key: `'show_' + showId`
  - `getAvailableSeats(showId)` - Key: `'show_' + showId + '_available'`
- **Eviction**: Immediately after seat booking
- **Benefit**: Fast seat map rendering
- **Critical**: Must be evicted on booking to prevent showing wrong availability

### Cache Eviction Flow

```
User Books Tickets:
1. Block seats (with pessimistic lock)
2. Create booking (PENDING status)
3. Evict seats & shows cache (@CacheEvict)
4. User makes payment
5. Confirm seats (BOOKED status)
6. Evict seats & shows cache again
7. Update show available seats

Admin Operations:
1. Add/Update/Delete Movie → Evict movies cache
2. Add/Update/Delete Theater → Evict theaters cache
3. Add/Update/Delete Show → Evict shows cache
```

## 🔒 Concurrency Safety - Seat Booking

### Problem
Multiple users booking same seats simultaneously → Double booking!

### Solution: Pessimistic Locking

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT s FROM Seat s WHERE s.show.id = :showId AND s.seatNumber IN :seatNumbers")
List<Seat> findByShowIdAndSeatNumbersWithLock(...);
```

### Booking Flow with Locking

```
User 1 selects A1, A2
User 2 selects A1, A3 (at same time)

Step 1: User 1 calls blockSeats(showId, [A1, A2])
  → Database LOCKS rows for A1, A2
  → Checks if AVAILABLE → Yes
  → Sets status to BLOCKED
  → Commits transaction
  → Releases lock

Step 2: User 2 calls blockSeats(showId, [A1, A3])
  → Database tries to LOCK A1, A3
  → A1 is already BLOCKED by User 1
  → Transaction throws exception
  → User 2 gets error: "Seat A1 is not available"

Result: Only User 1 gets the booking
```

### Transaction Flow

```
@Transactional
bookSeats() {
  1. Lock seats (PESSIMISTIC_WRITE)
  2. Check all seats AVAILABLE
  3. If not → Throw exception (rollback)
  4. Set seats to BLOCKED
  5. Create booking (PENDING)
  6. Commit transaction
}

@Transactional
confirmBooking() {
  1. Lock seats again
  2. Set seats to BOOKED
  3. Update show available seats
  4. Create payment record
  5. Set booking to CONFIRMED
  6. Commit transaction
}
```

## 🚀 How to Run

### Prerequisites
- Java 21 or higher
- Maven 3.6+
- Any modern web browser

### Backend Setup

1. **Navigate to backend directory**:
   ```bash
   cd bookmyshow/backend
   ```

2. **Build the project**:
   ```bash
   mvn clean install
   ```

3. **Run the application**:
   ```bash
   mvn spring-boot:run
   ```

4. **Backend will start on**: `http://localhost:8080`

5. **Access H2 Console** (for database inspection):
   - URL: `http://localhost:8080/h2-console`
   - JDBC URL: `jdbc:h2:mem:bookmyshow`
   - Username: `sa`
   - Password: (leave empty)

### Frontend Setup

1. **Navigate to frontend directory**:
   ```bash
   cd bookmyshow/frontend
   ```

2. **Open with a local server** (required for CORS):
   
   **Option 1 - VS Code Live Server**:
   - Install "Live Server" extension
   - Right-click on `index.html`
   - Select "Open with Live Server"

   **Option 2 - Python HTTP Server**:
   ```bash
   python -m http.server 8000
   ```
   Then open: `http://localhost:8000`

   **Option 3 - Node.js HTTP Server**:
   ```bash
   npx http-server -p 8000
   ```
   Then open: `http://localhost:8000`

3. **Frontend will be available at**: `http://localhost:8000` (or your server port)

### Database Configuration

**Default**: H2 In-Memory Database (auto-configured)

**To use MySQL**:
1. Create MySQL database:
   ```sql
   CREATE DATABASE bookmyshow;
   ```

2. Edit `backend/src/main/resources/application.properties`:
   ```properties
   # Comment H2 config
   # spring.datasource.url=jdbc:h2:mem:bookmyshow
   
   # Uncomment MySQL config
   spring.datasource.url=jdbc:mysql://localhost:3306/bookmyshow?createDatabaseIfNotExist=true
   spring.datasource.username=root
   spring.datasource.password=your_password
   spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
   ```

## 👤 Demo Credentials

### Admin User
- **Username**: `admin`
- **Password**: `admin123`
- **Access**: Full admin dashboard

### Regular User
- **Username**: `john`
- **Password**: `john123`
- **Access**: User features only

## 📝 API Endpoints

### Authentication
- `POST /api/auth/signup` - Register new user
- `POST /api/auth/login` - User login
- `GET /api/auth/me` - Get current user

### Movies
- `GET /api/movies` - Get all movies
- `GET /api/movies/{id}` - Get movie by ID
- `GET /api/movies/genre/{genre}` - Get movies by genre
- `GET /api/movies/search?title={title}` - Search movies

### Theaters
- `GET /api/theaters` - Get all theaters
- `GET /api/theaters/city/{city}` - Get theaters by city

### Shows
- `GET /api/shows/{id}` - Get show by ID
- `GET /api/shows/movie/{movieId}` - Get shows by movie
- `GET /api/shows/movie/{movieId}/city/{city}?date={date}` - Get shows by movie, city, and date

### Seats
- `GET /api/seats/show/{showId}` - Get all seats for show
- `GET /api/seats/show/{showId}/available` - Get available seats

### Bookings (Requires Authentication)
- `POST /api/bookings` - Create booking
- `POST /api/bookings/{id}/confirm` - Confirm booking with payment
- `GET /api/bookings/my-bookings` - Get user bookings

### Admin (Requires Admin Role)
- `POST /api/admin/movies` - Add movie
- `PUT /api/admin/movies/{id}` - Update movie
- `DELETE /api/admin/movies/{id}` - Delete movie
- `POST /api/admin/theaters` - Add theater
- `POST /api/admin/shows` - Add show
- `GET /api/admin/bookings` - Get all bookings

## 🎬 Booking Flow Explanation

### Step-by-Step User Journey

1. **Browse Movies** (`index.html`)
   - User views movie list (cached from `movies` cache)
   - Filters by genre/language
   - Searches by title
   - Clicks movie card

2. **View Movie Details** (`movie-details.html`)
   - Shows movie information (cached)
   - Displays theaters and show timings by city
   - Shows fetched from `shows` cache
   - User selects date and show time
   - Clicks show time button

3. **Select Seats** (`seat-selection.html`)
   - **Authentication required** - redirects to login if not logged in
   - Loads seat map (cached from `seats` cache)
   - Shows: Available (gray), Booked (red), Selected (green)
   - User clicks seats to select/deselect (max 10)
   - Shows total amount dynamically
   - Clicks "Proceed to Payment"
   - **Backend blocks seats** with pessimistic lock
   - Creates booking with PENDING status
   - **Evicts seats & shows cache**

4. **Payment** (`payment.html`)
   - Shows booking summary
   - User selects payment method
   - Clicks "Pay Now"
   - **Simulates payment** (no real payment gateway)
   - **Backend confirms booking**:
     - Marks seats as BOOKED
     - Creates payment record
     - Sets booking to CONFIRMED
     - **Evicts seats & shows cache again**

5. **Confirmation** (`confirmation.html`)
   - Shows booking reference
   - Shows all booking details
   - Provides booking reference for theater

6. **View Bookings** (`my-bookings.html`)
   - User can view all past bookings
   - Shows booking status (CONFIRMED/CANCELLED)

### Admin Flow

1. **Login as Admin** (`login.html` with admin credentials)
2. **Admin Dashboard** (`admin.html`)
   - Tab 1: **Movies** - Add/Delete movies
   - Tab 2: **Theaters** - Add/Delete theaters
   - Tab 3: **Shows** - Create shows (movie + theater + time)
   - Tab 4: **Bookings** - View all user bookings
3. **Add Show**:
   - Select movie from dropdown
   - Select theater from dropdown
   - Pick date, time, price
   - Submit
   - **Backend creates show and seat layout**
   - Evicts `shows` cache

## 🧪 Testing the Application

### Test Seat Booking Concurrency

1. Open two browser windows (or incognito + normal)
2. Login as different users in each
3. Navigate to same show
4. In both windows, try to select same seat (e.g., A1)
5. Click "Proceed to Payment" in both simultaneously
6. **Result**: Only one will succeed, other gets error

### Test Caching

1. **Enable SQL logging**: Already enabled in `application.properties`
   ```properties
   spring.jpa.show-sql=true
   ```

2. **Test Movie Cache**:
   - Visit home page → Check console (SQL query runs)
   - Refresh page → Check console (No SQL query - served from cache)
   - Login as admin → Delete a movie
   - Refresh home page → Check console (SQL query runs - cache evicted)

3. **Test Seat Cache**:
   - Open seat selection page → Check console (SQL query)
   - Refresh page → Check console (No SQL - cached)
   - Book seats → Complete booking
   - Go back to seat selection → Check console (SQL query - cache evicted)

## 🛠️ Tech Stack Summary

### Backend
- **Framework**: Spring Boot 3.2.0
- **Language**: Java 21
- **Security**: Spring Security + JWT
- **Database**: H2 (dev) / MySQL (production)
- **ORM**: Spring Data JPA / Hibernate
- **Caching**: Spring Cache (In-memory / Redis)
- **Build Tool**: Maven

### Frontend
- **HTML5**: Semantic markup
- **CSS3**: Custom responsive design (BookMyShow-style)
- **JavaScript**: ES6 Vanilla JS (no frameworks)
- **API Communication**: Fetch API
- **State Management**: localStorage for JWT

## 📚 Key Learning Points

1. **JWT Authentication**: Token-based stateless authentication
2. **Spring Security**: Securing REST APIs with role-based access
3. **Caching**: Using @Cacheable, @CacheEvict for performance
4. **Concurrency**: Pessimistic locking for seat booking
5. **JPA Relationships**: OneToMany, ManyToOne, ManyToMany
6. **REST API Design**: RESTful endpoints with proper HTTP methods
7. **Frontend Architecture**: Modular JavaScript without frameworks
8. **Responsive Design**: Mobile-first CSS approach

## 🐛 Troubleshooting

### Backend Issues

**Port 8080 already in use**:
```properties
# Change in application.properties
server.port=8081
```

**JWT errors**:
- Ensure `app.jwt.secret` is at least 256 bits (32 characters)
- Check token expiration time

### Frontend Issues

**CORS errors**:
- Ensure backend CORS is enabled (already configured)
- Use a local server, not file:// protocol

**API connection failed**:
- Check backend is running on port 8080
- Verify `API_BASE_URL` in `js/config.js`

## 🎯 Future Enhancements

- Redis cache integration
- Email notifications on booking
- Seat layout customization per theater
- Movie reviews and ratings
- Coupon/discount system
- QR code for ticket verification
- Real payment gateway integration
- Mobile responsive improvements
- Push notifications

## 📄 License

This is a demo project for educational purposes.

---

**Enjoy booking movies! 🎬🍿**
