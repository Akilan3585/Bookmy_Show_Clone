package com.bookmyshow.service;

import com.bookmyshow.model.*;
import com.bookmyshow.repository.BookingRepository;
import com.bookmyshow.repository.PaymentRepository;
import com.bookmyshow.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class BookingService {
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ShowService showService;
    
    @Autowired
    private SeatService seatService;
    
    /**
     * Create booking with seat blocking
     * Evict seat and show caches immediately
     */
    @Transactional
    @CacheEvict(value = {"seats", "shows"}, allEntries = true)
    public Booking createBooking(Long userId, Long showId, List<String> seatNumbers) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Show show = showService.getShowById(showId);
        
        // Block seats (with pessimistic locking)
        seatService.blockSeats(showId, seatNumbers);
        
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setShow(show);
        booking.setSeatNumbers(seatNumbers);
        booking.setNumberOfSeats(seatNumbers.size());
        booking.setTotalAmount(show.getPrice() * seatNumbers.size());
        booking.setBookingReference(generateBookingReference());
        booking.setStatus(Booking.BookingStatus.PENDING);
        
        return bookingRepository.save(booking);
    }
    
    /**
     * Confirm booking after successful payment
     * Evict caches to update seat availability
     */
    @Transactional
    @CacheEvict(value = {"seats", "shows"}, allEntries = true)
    public Booking confirmBooking(Long bookingId, String paymentMethod) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        if (booking.getStatus() != Booking.BookingStatus.PENDING) {
            throw new RuntimeException("Booking is not in pending state");
        }
        
        // Confirm seats
        seatService.confirmSeats(booking.getShow().getId(), booking.getSeatNumbers());
        
        // Create payment record
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(booking.getTotalAmount());
        payment.setPaymentMethod(Payment.PaymentMethod.valueOf(paymentMethod.toUpperCase()));
        payment.setStatus(Payment.PaymentStatus.SUCCESS);
        payment.setTransactionId(generateTransactionId());
        
        paymentRepository.save(payment);
        
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        booking.setPayment(payment);
        
        return bookingRepository.save(booking);
    }
    
    @Transactional
    @CacheEvict(value = {"seats", "shows"}, allEntries = true)
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        if (booking.getStatus() == Booking.BookingStatus.PENDING) {
            // Release blocked seats
            seatService.releaseSeats(booking.getShow().getId(), booking.getSeatNumbers());
        }
        
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }
    
    public List<Booking> getUserBookings(Long userId) {
        return bookingRepository.findByUserIdOrderByBookingTimeDesc(userId);
    }
    
    public Booking getBookingByReference(String bookingReference) {
        return bookingRepository.findByBookingReference(bookingReference)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }
    
    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }
    
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
    
    private String generateBookingReference() {
        return "BMS" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private String generateTransactionId() {
        return "TXN" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
    }
}
