# BookMyShow - Low-Level Design (LLD) Document

## Table of Contents
1. [System Overview](#1-system-overview)
2. [Architecture Design](#2-architecture-design)
3. [Class Diagrams](#3-class-diagrams)
4. [Database Design](#4-database-design)
5. [API Design](#5-api-design)
6. [Sequence Diagrams](#6-sequence-diagrams)
7. [Code Skeleton](#7-code-skeleton)
8. [Design Patterns Used](#8-design-patterns-used)

---

## 1. System Overview

### 1.1 Project Description
BookMyShow is a comprehensive movie ticket booking system that allows users to browse movies, select theaters, book seats, and make payments.

### 1.2 Technology Stack
- **Backend Framework**: Spring Boot 3.2.0
- **Language**: Java 21
- **Frontend**: HTML5, CSS3, Vanilla JavaScript
- **Database**: H2 (Development), MySQL (Production)
- **Security**: JWT Authentication, Spring Security
- **ORM**: Spring Data JPA, Hibernate
- **Build Tool**: Maven
- **Caching**: Spring Cache

### 1.3 Core Features
- User Registration & Authentication
- Movie Browsing & Search
- Theater & Show Management
- Seat Selection & Booking
- Payment Processing
- Booking History Management
- Admin Panel for Content Management

---

## 2. Architecture Design

### 2.1 System Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                     PRESENTATION LAYER (Frontend)                │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │  index.html  │  │ booking.html │  │  admin.html  │          │
│  │  login.html  │  │ payment.html │  │profile.html  │          │
│  └──────────────┘  └──────────────┘  └──────────────┘          │
│         JavaScript (auth.js, main.js, booking.js)               │
└─────────────────────────────────────────────────────────────────┘
                              ↓ HTTP REST API
┌─────────────────────────────────────────────────────────────────┐
│                    SECURITY LAYER (Spring Security)              │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │  JWT Authentication Filter → JWT Token Provider          │   │
│  │  Security Configuration → Custom UserDetailsService      │   │
│  └──────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                    CONTROLLER LAYER (REST API)                   │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │  AuthController    │  MovieController  │ ShowController  │   │
│  │  BookingController │  TheaterController│ AdminController │   │
│  │  SeatController    │  PaymentController                  │   │
│  └──────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                      SERVICE LAYER (Business Logic)              │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │  AuthService       │  MovieService     │ ShowService     │   │
│  │  BookingService    │  TheaterService   │ SeatService     │   │
│  │  PaymentService    │  CacheService                       │   │
│  └──────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                   REPOSITORY LAYER (Data Access)                 │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │  UserRepository    │  MovieRepository  │ ShowRepository  │   │
│  │  BookingRepository │  TheaterRepository│ SeatRepository  │   │
│  │  PaymentRepository │  RoleRepository                     │   │
│  └──────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
                              ↓ JPA/Hibernate
┌─────────────────────────────────────────────────────────────────┐
│                      DATABASE LAYER                              │
│              H2 Database (In-Memory) / MySQL                     │
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 Layered Architecture Pattern

```
┌────────────────────────────────────────────┐
│         Presentation Layer                  │  → User Interface
├────────────────────────────────────────────┤
│         Controller Layer                    │  → Request Handling
├────────────────────────────────────────────┤
│         Service Layer                       │  → Business Logic
├────────────────────────────────────────────┤
│         Repository Layer                    │  → Data Access
├────────────────────────────────────────────┤
│         Domain/Model Layer                  │  → Entities
└────────────────────────────────────────────┘
```

---

## 3. Class Diagrams

### 3.1 Core Entity Class Diagram

```
┌─────────────────────────────────┐
│           User                   │
├─────────────────────────────────┤
│ - id: Long                       │
│ - username: String               │
│ - email: String                  │
│ - password: String               │
│ - firstName: String              │
│ - lastName: String               │
│ - phone: String                  │
│ - city: String                   │
│ - roles: Set<Role>               │
│ - createdAt: LocalDateTime       │
│ - updatedAt: LocalDateTime       │
├─────────────────────────────────┤
│ + getters/setters                │
└─────────────────────────────────┘
              │ 1
              │
              │ *
              ▼
┌─────────────────────────────────┐
│          Booking                 │
├─────────────────────────────────┤
│ - id: Long                       │
│ - user: User                     │
│ - show: Show                     │
│ - seatNumbers: List<String>      │
│ - numberOfSeats: Integer         │
│ - totalAmount: Double            │
│ - bookingReference: String       │
│ - status: BookingStatus          │
│ - payment: Payment               │
│ - createdAt: LocalDateTime       │
├─────────────────────────────────┤
│ + calculateTotal(): Double       │
│ + confirmBooking(): void         │
│ + cancelBooking(): void          │
└─────────────────────────────────┘
              │ *
              │
              │ 1
              ▼
┌─────────────────────────────────┐
│            Show                  │
├─────────────────────────────────┤
│ - id: Long                       │
│ - movie: Movie                   │
│ - theater: Theater               │
│ - showDate: LocalDate            │
│ - showTime: LocalTime            │
│ - price: Double                  │
│ - availableSeats: Integer        │
│ - active: Boolean                │
│ - createdAt: LocalDateTime       │
├─────────────────────────────────┤
│ + updateAvailability(): void     │
│ + getAvailableSeats(): Integer   │
└─────────────────────────────────┘
       │ *               │ *
       │                 │
       │ 1               │ 1
       ▼                 ▼
┌──────────────────┐  ┌──────────────────┐
│     Movie        │  │     Theater      │
├──────────────────┤  ├──────────────────┤
│ - id: Long       │  │ - id: Long       │
│ - title: String  │  │ - name: String   │
│ - description    │  │ - city: String   │
│ - director       │  │ - address        │
│ - genre: String  │  │ - totalSeats     │
│ - duration: Int  │  │ - totalRows      │
│ - rating: Double │  │ - seatsPerRow    │
│ - releaseDate    │  │ - active         │
│ - posterUrl      │  │ - createdAt      │
│ - trailerUrl     │  └──────────────────┘
│ - language       │
│ - active         │
└──────────────────┘

┌─────────────────────────────────┐
│            Seat                  │
├─────────────────────────────────┤
│ - id: Long                       │
│ - show: Show                     │
│ - seatNumber: String             │
│ - row: String                    │
│ - column: Integer                │
│ - status: SeatStatus             │
│ - createdAt: LocalDateTime       │
├─────────────────────────────────┤
│ + book(): void                   │
│ + release(): void                │
│ + isAvailable(): Boolean         │
└─────────────────────────────────┘

┌─────────────────────────────────┐
│          Payment                 │
├─────────────────────────────────┤
│ - id: Long                       │
│ - booking: Booking               │
│ - amount: Double                 │
│ - paymentMethod: PaymentMethod   │
│ - transactionId: String          │
│ - status: PaymentStatus          │
│ - createdAt: LocalDateTime       │
├─────────────────────────────────┤
│ + processPayment(): Boolean      │
│ + refund(): void                 │
└─────────────────────────────────┘

┌─────────────────────────────────┐
│            Role                  │
├─────────────────────────────────┤
│ - id: Long                       │
│ - name: RoleName (ADMIN/USER)    │
├─────────────────────────────────┤
│ + getAuthority(): String         │
└─────────────────────────────────┘
```

### 3.2 Enum Classes

```java
public enum BookingStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    EXPIRED
}

public enum SeatStatus {
    AVAILABLE,
    BOOKED,
    LOCKED
}

public enum PaymentMethod {
    CREDIT_CARD,
    DEBIT_CARD,
    UPI,
    NET_BANKING,
    WALLET
}

public enum PaymentStatus {
    PENDING,
    SUCCESS,
    FAILED,
    REFUNDED
}

public enum RoleName {
    ROLE_USER,
    ROLE_ADMIN
}
```

---

## 4. Database Design

### 4.1 Entity Relationship Diagram (ERD)

```
┌─────────────┐         ┌─────────────┐         ┌─────────────┐
│    users    │ 1     * │  bookings   │ *     1 │    shows    │
│─────────────│◄────────│─────────────│─────────┤─────────────│
│ PK id       │         │ PK id       │         │ PK id       │
│    username │         │ FK user_id  │         │ FK movie_id │
│    email    │         │ FK show_id  │         │ FK theater_id│
│    password │         │ FK payment_id│        │    show_date│
│    phone    │         │ seat_numbers│         │    show_time│
│    city     │         │ total_amount│         │    price    │
│    created_at│        │    status   │         │ available_seats│
└─────────────┘         │booking_reference│     │    active   │
      │                 │ created_at  │         │ created_at  │
      │ *               └─────────────┘         └─────────────┘
      │                        │                       │
      │ *                      │ 1                     │ *
      ▼                        │ 1                     │
┌─────────────┐               │                       │ 1
│ user_roles  │               ▼                       ▼
│─────────────│         ┌─────────────┐         ┌─────────────┐
│ FK user_id  │         │  payments   │         │   movies    │
│ FK role_id  │         │─────────────│         │─────────────│
└─────────────┘         │ PK id       │         │ PK id       │
      │                 │ FK booking_id│        │    title    │
      │ *               │    amount   │         │ description │
      │                 │payment_method│        │    genre    │
      │ 1               │transaction_id│        │    director │
      ▼                 │    status   │         │    duration │
┌─────────────┐         │ created_at  │         │    rating   │
│    roles    │         └─────────────┘         │ release_date│
│─────────────│                                 │  poster_url │
│ PK id       │         ┌─────────────┐         │ trailer_url │
│    name     │         │    seats    │         │   language  │
└─────────────┘         │─────────────│         │    active   │
                        │ PK id       │         │ created_at  │
                        │ FK show_id  │         └─────────────┘
                        │seat_number  │               │
                        │    row      │               │ *
                        │   column    │               │
                        │   status    │               │ 1
                        │ created_at  │               ▼
                        └─────────────┘         ┌─────────────┐
                                                │  theaters   │
                                                │─────────────│
                                                │ PK id       │
                                                │    name     │
                                                │    city     │
                                                │   address   │
                                                │total_seats  │
                                                │ total_rows  │
                                                │seats_per_row│
                                                │   active    │
                                                │ created_at  │
                                                └─────────────┘
```

### 4.2 Database Schema (SQL)

```sql
-- Users Table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    phone VARCHAR(15),
    city VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_username (username)
);

-- Roles Table
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(20) UNIQUE NOT NULL
);

-- User Roles Junction Table
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Movies Table
CREATE TABLE movies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    genre VARCHAR(50),
    director VARCHAR(100),
    duration INTEGER,
    rating DECIMAL(3,1),
    release_date DATE,
    poster_url VARCHAR(500),
    trailer_url VARCHAR(500),
    language VARCHAR(50),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_genre (genre),
    INDEX idx_title (title),
    INDEX idx_active (active)
);

-- Theaters Table
CREATE TABLE theaters (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    city VARCHAR(50) NOT NULL,
    address VARCHAR(255),
    total_seats INTEGER NOT NULL,
    total_rows INTEGER,
    seats_per_row INTEGER,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_city (city),
    INDEX idx_active (active)
);

-- Shows Table
CREATE TABLE shows (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    movie_id BIGINT NOT NULL,
    theater_id BIGINT NOT NULL,
    show_date DATE NOT NULL,
    show_time TIME NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    available_seats INTEGER NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE,
    FOREIGN KEY (theater_id) REFERENCES theaters(id) ON DELETE CASCADE,
    INDEX idx_show_date (show_date),
    INDEX idx_movie_theater (movie_id, theater_id),
    INDEX idx_active (active)
);

-- Seats Table
CREATE TABLE seats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    show_id BIGINT NOT NULL,
    seat_number VARCHAR(10) NOT NULL,
    row_letter VARCHAR(5),
    column_number INTEGER,
    status VARCHAR(20) DEFAULT 'AVAILABLE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (show_id) REFERENCES shows(id) ON DELETE CASCADE,
    UNIQUE KEY unique_seat_show (show_id, seat_number),
    INDEX idx_status (status)
);

-- Bookings Table
CREATE TABLE bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    show_id BIGINT NOT NULL,
    seat_numbers TEXT NOT NULL,
    number_of_seats INTEGER NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    booking_reference VARCHAR(50) UNIQUE NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (show_id) REFERENCES shows(id) ON DELETE CASCADE,
    INDEX idx_user (user_id),
    INDEX idx_status (status),
    INDEX idx_booking_reference (booking_reference)
);

-- Payments Table
CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT UNIQUE NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_method VARCHAR(50),
    transaction_id VARCHAR(100) UNIQUE,
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    INDEX idx_transaction (transaction_id),
    INDEX idx_status (status)
);
```

---

## 5. API Design

### 5.1 Authentication APIs

```
POST /api/auth/signup
Request Body:
{
    "username": "string",
    "email": "string",
    "password": "string",
    "firstName": "string",
    "lastName": "string",
    "phone": "string",
    "city": "string"
}
Response: 200 OK
{
    "message": "User registered successfully"
}

POST /api/auth/signin
Request Body:
{
    "username": "string",
    "password": "string"
}
Response: 200 OK
{
    "token": "jwt_token_string",
    "type": "Bearer",
    "username": "string",
    "email": "string",
    "roles": ["ROLE_USER"]
}
```

### 5.2 Movie APIs

```
GET /api/movies
Response: 200 OK
[
    {
        "id": 1,
        "title": "string",
        "description": "string",
        "genre": "string",
        "duration": 120,
        "rating": 8.5,
        "posterUrl": "string",
        "active": true
    }
]

GET /api/movies/{id}
Response: 200 OK
{
    "id": 1,
    "title": "string",
    "description": "string",
    "director": "string",
    "genre": "string",
    "duration": 120,
    "rating": 8.5,
    "releaseDate": "2024-01-01",
    "posterUrl": "string",
    "trailerUrl": "string"
}

GET /api/movies/search?keyword={keyword}
Response: 200 OK
[...]
```

### 5.3 Show APIs

```
GET /api/shows/movie/{movieId}
Response: 200 OK
[
    {
        "id": 1,
        "movieId": 1,
        "theaterId": 1,
        "theaterName": "string",
        "showDate": "2024-12-18",
        "showTime": "18:30:00",
        "price": 250.00,
        "availableSeats": 45
    }
]

GET /api/shows/{id}
Response: 200 OK
{
    "id": 1,
    "movie": {...},
    "theater": {...},
    "showDate": "2024-12-18",
    "showTime": "18:30:00",
    "price": 250.00,
    "availableSeats": 45
}
```

### 5.4 Seat APIs

```
GET /api/seats/show/{showId}
Response: 200 OK
[
    {
        "id": 1,
        "seatNumber": "A1",
        "row": "A",
        "column": 1,
        "status": "AVAILABLE"
    }
]
```

### 5.5 Booking APIs

```
POST /api/bookings
Headers: Authorization: Bearer {token}
Request Body:
{
    "showId": 1,
    "seatNumbers": ["A1", "A2"],
    "paymentMethod": "CREDIT_CARD"
}
Response: 201 CREATED
{
    "id": 1,
    "bookingReference": "BMS123456",
    "totalAmount": 500.00,
    "status": "CONFIRMED"
}

GET /api/bookings/user
Headers: Authorization: Bearer {token}
Response: 200 OK
[
    {
        "id": 1,
        "bookingReference": "BMS123456",
        "movieTitle": "string",
        "theaterName": "string",
        "showDate": "2024-12-18",
        "showTime": "18:30:00",
        "seats": ["A1", "A2"],
        "totalAmount": 500.00,
        "status": "CONFIRMED"
    }
]

DELETE /api/bookings/{id}
Headers: Authorization: Bearer {token}
Response: 200 OK
{
    "message": "Booking cancelled successfully"
}
```

### 5.6 Admin APIs

```
POST /api/admin/movies
Headers: Authorization: Bearer {admin_token}
Request Body: { movie details }
Response: 201 CREATED

POST /api/admin/shows
Headers: Authorization: Bearer {admin_token}
Request Body: { show details }
Response: 201 CREATED

PUT /api/admin/movies/{id}
DELETE /api/admin/movies/{id}
PUT /api/admin/shows/{id}
DELETE /api/admin/shows/{id}
```

---

## 6. Sequence Diagrams

### 6.1 User Registration Flow

```
User        Frontend      AuthController    AuthService    UserRepository    Database
 │              │                │               │               │              │
 │─Register────>│                │               │               │              │
 │              │─POST /signup──>│               │               │              │
 │              │                │─register()───>│               │              │
 │              │                │               │─checkEmail()─>│              │
 │              │                │               │               │─SELECT──────>│
 │              │                │               │               │<─result──────│
 │              │                │               │<─exists?──────│              │
 │              │                │               │─encodePass()──│              │
 │              │                │               │─createUser()─>│              │
 │              │                │               │               │─INSERT──────>│
 │              │                │               │               │<─success─────│
 │              │                │               │<─user─────────│              │
 │              │                │<─success──────│               │              │
 │              │<─200 OK────────│               │               │              │
 │<─success────│                │               │               │              │
```

### 6.2 Movie Booking Flow

```
User    Frontend    BookingController  BookingService  SeatRepository  ShowRepository  DB
 │         │              │                  │               │               │         │
 │─Book───>│              │                  │               │               │         │
 │         │─POST────────>│                  │               │               │         │
 │         │  /bookings   │─createBooking()─>│               │               │         │
 │         │              │                  │─lockSeats()──>│               │         │
 │         │              │                  │               │─UPDATE───────>│         │
 │         │              │                  │               │<─success──────│         │
 │         │              │                  │<─locked───────│               │         │
 │         │              │                  │─updateShow()─>│               │         │
 │         │              │                  │               │─UPDATE───────>│         │
 │         │              │                  │               │<─success──────│         │
 │         │              │                  │─createBooking()─────────────────────────>│
 │         │              │                  │<─booking──────────────────────────────────│
 │         │              │                  │─processPayment()                          │
 │         │              │<─booking─────────│                                           │
 │         │<─201─────────│                                                              │
 │<─success│                                                                             │
```

### 6.3 User Authentication Flow

```
User    Frontend   AuthController  AuthService  JwtTokenProvider  UserDetailsService
 │         │            │               │              │                  │
 │─Login──>│            │               │              │                  │
 │         │─POST──────>│               │              │                  │
 │         │  /signin   │─authenticate()│              │                  │
 │         │            │───────────────>│              │                  │
 │         │            │                │─loadUser()──────────────────────>│
 │         │            │                │              │<─UserDetails─────│
 │         │            │                │─verifyPass() │                  │
 │         │            │                │─generateJWT()>│                  │
 │         │            │                │<─token───────│                  │
 │         │            │<─JwtResponse───│              │                  │
 │         │<─200 OK────│                │              │                  │
 │<─token──│                                                               │
```

---

## 7. Code Skeleton

### 7.1 Entity Classes

```java
// User.java
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(max = 50)
    @Column(unique = true, nullable = false)
    private String username;
    
    @NotBlank
    @Size(max = 100)
    @Email
    @Column(unique = true, nullable = false)
    private String email;
    
    @NotBlank
    @JsonIgnore
    private String password;
    
    @Size(max = 50)
    private String firstName;
    
    @Size(max = 50)
    private String lastName;
    
    @Size(max = 15)
    private String phone;
    
    @Size(max = 50)
    private String city;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

// Movie.java
@Entity
@Table(name = "movies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(max = 200)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private String genre;
    private String director;
    private Integer duration;
    private Double rating;
    private LocalDate releaseDate;
    private String posterUrl;
    private String trailerUrl;
    private String language;
    private Boolean active = true;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

// Theater.java
@Entity
@Table(name = "theaters")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Theater {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    private String name;
    
    @NotBlank
    private String city;
    
    private String address;
    private Integer totalSeats;
    private Integer totalRows;
    private Integer seatsPerRow;
    private Boolean active = true;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

// Show.java
@Entity
@Table(name = "shows")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Show {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    private Movie movie;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id")
    private Theater theater;
    
    private LocalDate showDate;
    private LocalTime showTime;
    private Double price;
    private Integer availableSeats;
    private Boolean active = true;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

// Booking.java
@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id")
    private Show show;
    
    @Column(name = "seat_numbers")
    private List<String> seatNumbers;
    
    private Integer numberOfSeats;
    private Double totalAmount;
    
    @Column(unique = true)
    private String bookingReference;
    
    @Enumerated(EnumType.STRING)
    private BookingStatus status;
    
    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL)
    private Payment payment;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

// Seat.java
@Entity
@Table(name = "seats")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id")
    private Show show;
    
    private String seatNumber;
    private String row;
    private Integer column;
    
    @Enumerated(EnumType.STRING)
    private SeatStatus status = SeatStatus.AVAILABLE;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

// Payment.java
@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;
    
    private Double amount;
    
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    
    @Column(unique = true)
    private String transactionId;
    
    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.PENDING;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

// Role.java
@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(unique = true)
    private RoleName name;
}
```

### 7.2 Repository Layer

```java
// UserRepository.java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}

// MovieRepository.java
@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findByActiveTrue();
    List<Movie> findByGenre(String genre);
    List<Movie> findByTitleContainingIgnoreCase(String keyword);
    @Query("SELECT m FROM Movie m WHERE m.active = true AND " +
           "(LOWER(m.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(m.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Movie> searchMovies(@Param("keyword") String keyword);
}

// ShowRepository.java
@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {
    List<Show> findByMovieIdAndActiveTrue(Long movieId);
    List<Show> findByTheaterIdAndActiveTrue(Long theaterId);
    List<Show> findByShowDateAndActiveTrue(LocalDate showDate);
    @Query("SELECT s FROM Show s WHERE s.movie.id = :movieId " +
           "AND s.theater.city = :city AND s.active = true")
    List<Show> findShowsByMovieAndCity(@Param("movieId") Long movieId, 
                                        @Param("city") String city);
}

// BookingRepository.java
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);
    Optional<Booking> findByBookingReference(String bookingReference);
    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);
}

// SeatRepository.java
@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByShowId(Long showId);
    List<Seat> findByShowIdAndStatus(Long showId, SeatStatus status);
    Optional<Seat> findByShowIdAndSeatNumber(Long showId, String seatNumber);
}

// PaymentRepository.java
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByBookingId(Long bookingId);
    Optional<Payment> findByTransactionId(String transactionId);
}

// RoleRepository.java
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}

// TheaterRepository.java
@Repository
public interface TheaterRepository extends JpaRepository<Theater, Long> {
    List<Theater> findByCity(String city);
    List<Theater> findByActiveTrue();
}
```

### 7.3 Service Layer

```java
// AuthService.java
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    public String registerUser(SignupRequest signupRequest) {
        // Check if username exists
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            throw new RuntimeException("Username already taken");
        }
        
        // Check if email exists
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new RuntimeException("Email already in use");
        }
        
        // Create new user
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setFirstName(signupRequest.getFirstName());
        user.setLastName(signupRequest.getLastName());
        user.setPhone(signupRequest.getPhone());
        user.setCity(signupRequest.getCity());
        user.setCreatedAt(LocalDateTime.now());
        
        // Assign default role
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRoles(Set.of(userRole));
        
        userRepository.save(user);
        return "User registered successfully";
    }

    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
            )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<String> roles = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toList());
        
        return new JwtResponse(jwt, user.getUsername(), user.getEmail(), roles);
    }
}

// MovieService.java
@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;

    public List<Movie> getAllActiveMovies() {
        return movieRepository.findByActiveTrue();
    }

    public Movie getMovieById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found"));
    }

    public List<Movie> searchMovies(String keyword) {
        return movieRepository.searchMovies(keyword);
    }

    public List<Movie> getMoviesByGenre(String genre) {
        return movieRepository.findByGenre(genre);
    }

    public Movie createMovie(Movie movie) {
        movie.setActive(true);
        movie.setCreatedAt(LocalDateTime.now());
        return movieRepository.save(movie);
    }

    public Movie updateMovie(Long id, Movie movieDetails) {
        Movie movie = getMovieById(id);
        // Update fields
        movie.setTitle(movieDetails.getTitle());
        movie.setDescription(movieDetails.getDescription());
        movie.setGenre(movieDetails.getGenre());
        movie.setUpdatedAt(LocalDateTime.now());
        return movieRepository.save(movie);
    }

    public void deleteMovie(Long id) {
        Movie movie = getMovieById(id);
        movie.setActive(false);
        movieRepository.save(movie);
    }
}

// BookingService.java
@Service
@RequiredArgsConstructor
@Transactional
public class BookingService {
    private final BookingRepository bookingRepository;
    private final ShowRepository showRepository;
    private final SeatRepository seatRepository;
    private final PaymentRepository paymentRepository;

    public Booking createBooking(Long userId, Long showId, 
                                  List<String> seatNumbers,
                                  PaymentMethod paymentMethod) {
        // Validate show
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new RuntimeException("Show not found"));
        
        // Lock seats
        for (String seatNumber : seatNumbers) {
            Seat seat = seatRepository.findByShowIdAndSeatNumber(showId, seatNumber)
                    .orElseThrow(() -> new RuntimeException("Seat not found"));
            
            if (seat.getStatus() != SeatStatus.AVAILABLE) {
                throw new RuntimeException("Seat " + seatNumber + " not available");
            }
            
            seat.setStatus(SeatStatus.BOOKED);
            seatRepository.save(seat);
        }
        
        // Update show availability
        show.setAvailableSeats(show.getAvailableSeats() - seatNumbers.size());
        showRepository.save(show);
        
        // Create booking
        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setShow(show);
        booking.setSeatNumbers(seatNumbers);
        booking.setNumberOfSeats(seatNumbers.size());
        booking.setTotalAmount(show.getPrice() * seatNumbers.size());
        booking.setBookingReference(generateBookingReference());
        booking.setStatus(BookingStatus.PENDING);
        booking.setCreatedAt(LocalDateTime.now());
        
        booking = bookingRepository.save(booking);
        
        // Process payment
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(booking.getTotalAmount());
        payment.setPaymentMethod(paymentMethod);
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setTransactionId(generateTransactionId());
        payment.setCreatedAt(LocalDateTime.now());
        paymentRepository.save(payment);
        
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setPayment(payment);
        
        return bookingRepository.save(booking);
    }

    public List<Booking> getUserBookings(Long userId) {
        return bookingRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new RuntimeException("Cannot cancel booking");
        }
        
        // Release seats
        Show show = booking.getShow();
        for (String seatNumber : booking.getSeatNumbers()) {
            Seat seat = seatRepository.findByShowIdAndSeatNumber(
                    show.getId(), seatNumber).orElseThrow();
            seat.setStatus(SeatStatus.AVAILABLE);
            seatRepository.save(seat);
        }
        
        // Update show
        show.setAvailableSeats(show.getAvailableSeats() + 
                                booking.getNumberOfSeats());
        showRepository.save(show);
        
        // Update booking status
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    private String generateBookingReference() {
        return "BMS" + System.currentTimeMillis();
    }

    private String generateTransactionId() {
        return "TXN" + UUID.randomUUID().toString();
    }
}
```

### 7.4 Controller Layer

```java
// AuthController.java
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        try {
            String message = authService.registerUser(signupRequest);
            return ResponseEntity.ok(Map.of("message", message));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            JwtResponse response = authService.authenticateUser(loginRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials"));
        }
    }
}

// MovieController.java
@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MovieController {
    private final MovieService movieService;

    @GetMapping
    public ResponseEntity<List<Movie>> getAllMovies() {
        return ResponseEntity.ok(movieService.getAllActiveMovies());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable Long id) {
        return ResponseEntity.ok(movieService.getMovieById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Movie>> searchMovies(@RequestParam String keyword) {
        return ResponseEntity.ok(movieService.searchMovies(keyword));
    }

    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<Movie>> getMoviesByGenre(@PathVariable String genre) {
        return ResponseEntity.ok(movieService.getMoviesByGenre(genre));
    }
}

// BookingController.java
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createBooking(
            @Valid @RequestBody BookingRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = (User) userDetails;
            Booking booking = bookingService.createBooking(
                user.getId(),
                request.getShowId(),
                request.getSeatNumbers(),
                request.getPaymentMethod()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(booking);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Booking>> getUserBookings(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        return ResponseEntity.ok(bookingService.getUserBookings(user.getId()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> cancelBooking(@PathVariable Long id) {
        try {
            bookingService.cancelBooking(id);
            return ResponseEntity.ok(Map.of("message", "Booking cancelled"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
```

### 7.5 Security Configuration

```java
// JwtTokenProvider.java
@Component
public class JwtTokenProvider {
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private long jwtExpirationMs;

    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

// SecurityConfig.java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/movies/**").permitAll()
                .requestMatchers("/api/shows/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

### 7.6 DTO Classes

```java
// SignupRequest.java
@Data
public class SignupRequest {
    @NotBlank
    private String username;
    
    @NotBlank
    @Email
    private String email;
    
    @NotBlank
    @Size(min = 6)
    private String password;
    
    private String firstName;
    private String lastName;
    private String phone;
    private String city;
}

// LoginRequest.java
@Data
public class LoginRequest {
    @NotBlank
    private String username;
    
    @NotBlank
    private String password;
}

// JwtResponse.java
@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String username;
    private String email;
    private List<String> roles;
}

// BookingRequest.java
@Data
public class BookingRequest {
    @NotNull
    private Long showId;
    
    @NotEmpty
    private List<String> seatNumbers;
    
    @NotNull
    private PaymentMethod paymentMethod;
}
```

---

## 8. Design Patterns Used

### 8.1 Repository Pattern
- Abstracts data access logic
- Provides interface for CRUD operations
- Used: `UserRepository`, `MovieRepository`, etc.

### 8.2 Service Layer Pattern
- Encapsulates business logic
- Provides transaction management
- Used: `AuthService`, `BookingService`, etc.

### 8.3 DTO (Data Transfer Object) Pattern
- Transfers data between layers
- Prevents over-exposure of entities
- Used: `SignupRequest`, `JwtResponse`, etc.

### 8.4 Dependency Injection
- Spring IoC container
- Constructor injection with `@RequiredArgsConstructor`
- Loose coupling between components

### 8.5 Builder Pattern
- Used in JWT token creation
- Entity construction with Lombok

### 8.6 Strategy Pattern
- Different payment methods
- Authentication strategies

### 8.7 MVC (Model-View-Controller) Pattern
- Model: Entity classes
- View: Frontend HTML/JS
- Controller: REST Controllers

### 8.8 Singleton Pattern
- Spring beans are singleton by default
- Service classes, repositories

---

## 9. Key Design Decisions

### 9.1 Database Design
- Normalized schema (3NF)
- Proper indexing on frequently queried columns
- Foreign key constraints for referential integrity
- Soft delete (active flag) instead of hard delete

### 9.2 Security
- JWT-based stateless authentication
- Password encryption with BCrypt
- Role-based access control (RBAC)
- CORS configuration for frontend-backend communication

### 9.3 Transaction Management
- `@Transactional` for booking operations
- Ensures atomicity in seat booking
- Rollback on failure

### 9.4 Error Handling
- Custom exception classes
- Global exception handler
- Meaningful error messages

### 9.5 API Design
- RESTful principles
- Consistent naming conventions
- Proper HTTP status codes
- Versioned API (`/api/v1/`)

---

## 10. Future Enhancements

1. **Caching**: Redis for session management and frequently accessed data
2. **Queue System**: RabbitMQ for booking requests
3. **Email Notifications**: Send booking confirmations
4. **Payment Gateway Integration**: Razorpay, Stripe
5. **Seat Lock Mechanism**: Temporary seat locking during booking
6. **Real-time Updates**: WebSocket for seat availability
7. **Analytics Dashboard**: Admin analytics and reports
8. **Microservices**: Break into smaller services
9. **Docker**: Containerization
10. **CI/CD**: Automated deployment pipeline

---

## Conclusion

This Low-Level Design document provides a comprehensive blueprint for the BookMyShow application, covering architecture, database design, API specifications, and code implementation. The design follows industry best practices and SOLID principles, ensuring scalability, maintainability, and extensibility.

---

**Document Version**: 1.0  
**Last Updated**: December 18, 2025  
**Author**: BookMyShow Development Team
