// Seat Selection Page

let showId;
let currentShow;
let allSeats = [];
let selectedSeats = [];

document.addEventListener('DOMContentLoaded', async () => {
    if (!await requireAuth()) return;
    
    const urlParams = new URLSearchParams(window.location.search);
    showId = urlParams.get('showId');
    
    if (!showId) {
        window.location.href = 'index.html';
        return;
    }
    
    await loadShowDetails();
    await loadSeats();
});

// Load show details
async function loadShowDetails() {
    try {
        currentShow = await apiCall(API_ENDPOINTS.SHOW_BY_ID(showId));
        displayShowInfo(currentShow);
    } catch (error) {
        console.error('Error loading show details:', error);
        alert('Error loading show details');
        window.history.back();
    }
}

// Display show info
function displayShowInfo(show) {
    const container = document.getElementById('showInfo');
    const showDate = new Date(show.showDate).toLocaleDateString();
    
    container.innerHTML = `
        <h2>${show.movie.title}</h2>
        <p><strong>${show.theater.name}</strong> | ${show.theater.address}</p>
        <p><strong>Date:</strong> ${showDate} | <strong>Time:</strong> ${show.showTime}</p>
        <p><strong>Price per seat:</strong> ₹${show.price}</p>
        <p><strong>Available Seats:</strong> ${show.availableSeats}</p>
    `;
}

// Load seats
async function loadSeats() {
    try {
        allSeats = await apiCall(API_ENDPOINTS.SEATS_BY_SHOW(showId));
        displaySeats(allSeats);
    } catch (error) {
        console.error('Error loading seats:', error);
        document.getElementById('seatMap').innerHTML = 
            '<p>Error loading seats. Please try again.</p>';
    }
}

// Display seats
function displaySeats(seats) {
    // Group seats by row
    const seatsByRow = {};
    seats.forEach(seat => {
        const row = seat.seatNumber[0];
        if (!seatsByRow[row]) {
            seatsByRow[row] = [];
        }
        seatsByRow[row].push(seat);
    });
    
    const seatMap = document.getElementById('seatMap');
    seatMap.innerHTML = Object.keys(seatsByRow).sort().map(row => {
        const rowSeats = seatsByRow[row].sort((a, b) => {
            const numA = parseInt(a.seatNumber.substring(1));
            const numB = parseInt(b.seatNumber.substring(1));
            return numA - numB;
        });
        
        return `
            <div class="seat-row">
                <span class="row-label">${row}</span>
                ${rowSeats.map(seat => {
                    const status = seat.status.toLowerCase();
                    const isSelected = selectedSeats.includes(seat.seatNumber);
                    return `
                        <div class="seat ${isSelected ? 'selected' : status}" 
                             data-seat="${seat.seatNumber}"
                             onclick="toggleSeat('${seat.seatNumber}', '${seat.status}')">
                            ${seat.seatNumber.substring(1)}
                        </div>
                    `;
                }).join('')}
            </div>
        `;
    }).join('');
}

// Toggle seat selection
function toggleSeat(seatNumber, status) {
    if (status !== 'AVAILABLE') return;
    
    const index = selectedSeats.indexOf(seatNumber);
    if (index > -1) {
        selectedSeats.splice(index, 1);
    } else {
        if (selectedSeats.length >= 10) {
            alert('Maximum 10 seats can be selected');
            return;
        }
        selectedSeats.push(seatNumber);
    }
    
    updateSeatSelection();
}

// Update seat selection display
function updateSeatSelection() {
    // Update seat visual
    document.querySelectorAll('.seat').forEach(seat => {
        const seatNumber = seat.dataset.seat;
        if (selectedSeats.includes(seatNumber)) {
            seat.classList.add('selected');
            seat.classList.remove('available');
        } else if (seat.classList.contains('available') || seat.classList.contains('selected')) {
            seat.classList.remove('selected');
            seat.classList.add('available');
        }
    });
    
    // Update summary
    const selectedSeatsDisplay = document.getElementById('selectedSeatsDisplay');
    const totalAmount = document.getElementById('totalAmount');
    const proceedButton = document.getElementById('proceedButton');
    
    if (selectedSeats.length === 0) {
        selectedSeatsDisplay.textContent = 'None';
        totalAmount.textContent = '₹0';
        proceedButton.disabled = true;
    } else {
        selectedSeatsDisplay.textContent = selectedSeats.sort().join(', ');
        const total = selectedSeats.length * currentShow.price;
        totalAmount.textContent = `₹${total}`;
        proceedButton.disabled = false;
    }
}

// Proceed to payment
async function proceedToPayment() {
    if (selectedSeats.length === 0) {
        alert('Please select at least one seat');
        return;
    }
    
    try {
        // Create booking
        const bookingData = {
            showId: parseInt(showId),
            seatNumbers: selectedSeats,
            paymentMethod: 'CARD'
        };
        
        const booking = await apiCall(API_ENDPOINTS.BOOKINGS, {
            method: 'POST',
            body: JSON.stringify(bookingData)
        });
        
        // Store booking ID and proceed to payment
        localStorage.setItem('pendingBooking', JSON.stringify(booking));
        window.location.href = 'payment.html';
        
    } catch (error) {
        console.error('Error creating booking:', error);
        alert(error.message || 'Error creating booking. Please try again.');
    }
}
