// Payment Page

let pendingBooking;

document.addEventListener('DOMContentLoaded', async () => {
    if (!await requireAuth()) return;
    
    const bookingData = localStorage.getItem('pendingBooking');
    if (!bookingData) {
        alert('No booking found');
        window.location.href = 'index.html';
        return;
    }
    
    pendingBooking = JSON.parse(bookingData);
    displayBookingSummary(pendingBooking);
});

// Display booking summary
function displayBookingSummary(booking) {
    const container = document.getElementById('bookingSummary');
    const showDate = new Date(booking.show.showDate).toLocaleDateString();
    
    container.innerHTML = `
        <h3>Booking Summary</h3>
        <p><strong>Movie:</strong> ${booking.show.movie.title}</p>
        <p><strong>Theater:</strong> ${booking.show.theater.name}</p>
        <p><strong>Date:</strong> ${showDate}</p>
        <p><strong>Time:</strong> ${booking.show.showTime}</p>
        <p><strong>Seats:</strong> ${booking.seatNumbers.join(', ')}</p>
        <p><strong>Number of Seats:</strong> ${booking.numberOfSeats}</p>
        <hr>
        <h3><strong>Total Amount: ₹${booking.totalAmount}</strong></h3>
        <p style="margin-top: 10px;"><strong>Booking Reference:</strong> ${booking.bookingReference}</p>
    `;
}

// Make payment
async function makePayment() {
    const paymentMethod = document.querySelector('input[name="paymentMethod"]:checked').value;
    
    try {
        // Confirm booking with payment
        const confirmedBooking = await apiCall(
            API_ENDPOINTS.CONFIRM_BOOKING(pendingBooking.id),
            {
                method: 'POST',
                body: JSON.stringify({ paymentMethod })
            }
        );
        
        // Clear pending booking
        localStorage.removeItem('pendingBooking');
        
        // Store confirmed booking and redirect
        localStorage.setItem('confirmedBooking', JSON.stringify(confirmedBooking));
        window.location.href = 'confirmation.html';
        
    } catch (error) {
        console.error('Error confirming booking:', error);
        alert('Payment failed. Please try again.');
    }
}
