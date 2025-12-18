// My Bookings Page

document.addEventListener('DOMContentLoaded', async () => {
    if (!await requireAuth()) return;
    await loadMyBookings();
});

// Load user bookings
async function loadMyBookings() {
    try {
        const bookings = await apiCall(API_ENDPOINTS.MY_BOOKINGS);
        displayBookings(bookings);
    } catch (error) {
        console.error('Error loading bookings:', error);
        document.getElementById('bookingsContainer').innerHTML = 
            '<p>Error loading bookings. Please try again later.</p>';
    }
}

// Display bookings
function displayBookings(bookings) {
    const container = document.getElementById('bookingsContainer');
    
    if (bookings.length === 0) {
        container.innerHTML = '<p>No bookings found.</p>';
        return;
    }
    
    container.innerHTML = bookings.map(booking => {
        const showDate = new Date(booking.show.showDate).toLocaleDateString();
        const bookingDate = new Date(booking.bookingTime).toLocaleDateString();
        
        return `
            <div class="booking-card">
                <div class="booking-header">
                    <h3>${booking.show.movie.title}</h3>
                    <span class="booking-status ${booking.status.toLowerCase()}">
                        ${booking.status}
                    </span>
                </div>
                <p><strong>Booking Reference:</strong> ${booking.bookingReference}</p>
                <p><strong>Theater:</strong> ${booking.show.theater.name}</p>
                <p><strong>Show Date:</strong> ${showDate} at ${booking.show.showTime}</p>
                <p><strong>Seats:</strong> ${booking.seatNumbers.join(', ')}</p>
                <p><strong>Total Amount:</strong> ₹${booking.totalAmount}</p>
                <p><strong>Booked On:</strong> ${bookingDate}</p>
                ${booking.payment ? `
                    <p><strong>Payment Status:</strong> ${booking.payment.status}</p>
                    <p><strong>Transaction ID:</strong> ${booking.payment.transactionId}</p>
                ` : ''}
            </div>
        `;
    }).join('');
}
