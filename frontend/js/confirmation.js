// Confirmation Page

document.addEventListener('DOMContentLoaded', async () => {
    if (!await requireAuth()) return;
    
    const bookingData = localStorage.getItem('confirmedBooking');
    if (!bookingData) {
        window.location.href = 'index.html';
        return;
    }
    
    const booking = JSON.parse(bookingData);
    displayBookingDetails(booking);
    
    // Clear confirmed booking from storage
    localStorage.removeItem('confirmedBooking');
});

// Display booking details
function displayBookingDetails(booking) {
    const container = document.getElementById('bookingDetails');
    const showDate = new Date(booking.show.showDate).toLocaleDateString();
    
    container.innerHTML = `
        <h3>Booking Details</h3>
        <p><strong>Booking Reference:</strong> ${booking.bookingReference}</p>
        <p><strong>Movie:</strong> ${booking.show.movie.title}</p>
        <p><strong>Theater:</strong> ${booking.show.theater.name}</p>
        <p><strong>Address:</strong> ${booking.show.theater.address}</p>
        <p><strong>Date:</strong> ${showDate}</p>
        <p><strong>Time:</strong> ${booking.show.showTime}</p>
        <p><strong>Seats:</strong> ${booking.seatNumbers.join(', ')}</p>
        <p><strong>Number of Seats:</strong> ${booking.numberOfSeats}</p>
        <hr>
        <p style="font-size: 18px;"><strong>Total Amount Paid: ₹${booking.totalAmount}</strong></p>
        <p><strong>Payment Status:</strong> <span style="color: green;">${booking.payment.status}</span></p>
        <p><strong>Transaction ID:</strong> ${booking.payment.transactionId}</p>
        <p><strong>Payment Method:</strong> ${booking.payment.paymentMethod}</p>
        <hr>
        <p style="margin-top: 15px; color: #666;">
            Please arrive at the theater 15 minutes before show time. 
            Show your booking reference at the counter.
        </p>
    `;
}
