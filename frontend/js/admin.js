// Admin Dashboard

document.addEventListener('DOMContentLoaded', async () => {
    if (!await requireAuth() || !requireAdmin()) return;
    
    loadMoviesList();
});

// Tab Management
function showTab(tabName) {
    // Hide all tabs
    document.querySelectorAll('.tab-content').forEach(tab => {
        tab.classList.remove('active');
    });
    
    // Remove active from all buttons
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    
    // Show selected tab
    document.getElementById(tabName + 'Tab').classList.add('active');
    event.target.classList.add('active');
    
    // Load data for selected tab
    switch(tabName) {
        case 'movies':
            loadMoviesList();
            break;
        case 'theaters':
            loadTheatersList();
            break;
        case 'shows':
            loadShowsList();
            break;
        case 'bookings':
            loadAllBookings();
            break;
    }
}

// Movies Management
async function loadMoviesList() {
    try {
        const movies = await apiCall(API_ENDPOINTS.MOVIES);
        displayMoviesList(movies);
    } catch (error) {
        console.error('Error loading movies:', error);
    }
}

function displayMoviesList(movies) {
    const container = document.getElementById('moviesList');
    container.innerHTML = movies.map(movie => `
        <div class="admin-item">
            <div>
                <h4>${movie.title}</h4>
                <p>${movie.genre} | ${movie.language} | ${movie.duration} min</p>
            </div>
            <div class="admin-item-actions">
                <button class="btn-delete" onclick="deleteMovie(${movie.id})">Delete</button>
            </div>
        </div>
    `).join('');
}

function showAddMovieForm() {
    document.getElementById('addMovieModal').classList.add('show');
}

async function addMovie(event) {
    event.preventDefault();
    const form = event.target;
    const formData = new FormData(form);
    
    const movieData = {
        title: formData.get('title'),
        description: formData.get('description'),
        genre: formData.get('genre'),
        language: formData.get('language'),
        duration: parseInt(formData.get('duration')),
        rating: parseFloat(formData.get('rating')) || null,
        releaseDate: formData.get('releaseDate'),
        posterUrl: formData.get('posterUrl'),
        trailerUrl: formData.get('trailerUrl'),
        director: formData.get('director'),
        cast: formData.get('cast').split(',').map(s => s.trim()).filter(s => s),
        active: true
    };
    
    try {
        await apiCall(API_ENDPOINTS.ADMIN_MOVIES, {
            method: 'POST',
            body: JSON.stringify(movieData)
        });
        
        closeModal('addMovieModal');
        form.reset();
        loadMoviesList();
        alert('Movie added successfully!');
    } catch (error) {
        console.error('Error adding movie:', error);
        alert('Error adding movie');
    }
}

async function deleteMovie(id) {
    if (!confirm('Are you sure you want to delete this movie?')) return;
    
    try {
        await apiCall(`${API_ENDPOINTS.ADMIN_MOVIES}/${id}`, {
            method: 'DELETE'
        });
        loadMoviesList();
        alert('Movie deleted successfully!');
    } catch (error) {
        console.error('Error deleting movie:', error);
        alert('Error deleting movie');
    }
}

// Theaters Management
async function loadTheatersList() {
    try {
        const theaters = await apiCall(API_ENDPOINTS.THEATERS);
        displayTheatersList(theaters);
    } catch (error) {
        console.error('Error loading theaters:', error);
    }
}

function displayTheatersList(theaters) {
    const container = document.getElementById('theatersList');
    container.innerHTML = theaters.map(theater => `
        <div class="admin-item">
            <div>
                <h4>${theater.name}</h4>
                <p>${theater.city} | ${theater.address}</p>
                <p>Total Seats: ${theater.totalSeats} (${theater.totalRows} rows × ${theater.seatsPerRow} seats)</p>
            </div>
            <div class="admin-item-actions">
                <button class="btn-delete" onclick="deleteTheater(${theater.id})">Delete</button>
            </div>
        </div>
    `).join('');
}

function showAddTheaterForm() {
    document.getElementById('addTheaterModal').classList.add('show');
}

async function addTheater(event) {
    event.preventDefault();
    const form = event.target;
    const formData = new FormData(form);
    
    const theaterData = {
        name: formData.get('name'),
        city: formData.get('city'),
        address: formData.get('address'),
        seatsPerRow: parseInt(formData.get('seatsPerRow')),
        totalRows: parseInt(formData.get('totalRows')),
        active: true
    };
    
    try {
        await apiCall(API_ENDPOINTS.ADMIN_THEATERS, {
            method: 'POST',
            body: JSON.stringify(theaterData)
        });
        
        closeModal('addTheaterModal');
        form.reset();
        loadTheatersList();
        alert('Theater added successfully!');
    } catch (error) {
        console.error('Error adding theater:', error);
        alert('Error adding theater');
    }
}

async function deleteTheater(id) {
    if (!confirm('Are you sure you want to delete this theater?')) return;
    
    try {
        await apiCall(`${API_ENDPOINTS.ADMIN_THEATERS}/${id}`, {
            method: 'DELETE'
        });
        loadTheatersList();
        alert('Theater deleted successfully!');
    } catch (error) {
        console.error('Error deleting theater:', error);
        alert('Error deleting theater');
    }
}

// Shows Management
async function loadShowsList() {
    try {
        const shows = await apiCall(API_ENDPOINTS.SHOWS);
        displayShowsList(shows);
        
        // Load movies and theaters for add show form
        const movies = await apiCall(API_ENDPOINTS.MOVIES);
        const theaters = await apiCall(API_ENDPOINTS.THEATERS);
        populateShowSelects(movies, theaters);
    } catch (error) {
        console.error('Error loading shows:', error);
    }
}

function displayShowsList(shows) {
    const container = document.getElementById('showsList');
    container.innerHTML = shows.map(show => {
        const showDate = new Date(show.showDate).toLocaleDateString();
        return `
            <div class="admin-item">
                <div>
                    <h4>${show.movie.title} @ ${show.theater.name}</h4>
                    <p>Date: ${showDate} | Time: ${show.showTime}</p>
                    <p>Price: ₹${show.price} | Available Seats: ${show.availableSeats}</p>
                </div>
                <div class="admin-item-actions">
                    <button class="btn-delete" onclick="deleteShow(${show.id})">Delete</button>
                </div>
            </div>
        `;
    }).join('');
}

function populateShowSelects(movies, theaters) {
    const movieSelect = document.getElementById('showMovieSelect');
    const theaterSelect = document.getElementById('showTheaterSelect');
    
    movieSelect.innerHTML = '<option value="">Select Movie</option>' +
        movies.map(m => `<option value="${m.id}">${m.title}</option>`).join('');
    
    theaterSelect.innerHTML = '<option value="">Select Theater</option>' +
        theaters.map(t => `<option value="${t.id}">${t.name} - ${t.city}</option>`).join('');
}

function showAddShowForm() {
    document.getElementById('addShowModal').classList.add('show');
}

async function addShow(event) {
    event.preventDefault();
    const form = event.target;
    const formData = new FormData(form);
    
    const showData = {
        movieId: parseInt(formData.get('movieId')),
        theaterId: parseInt(formData.get('theaterId')),
        showDate: formData.get('showDate'),
        showTime: formData.get('showTime'),
        price: parseFloat(formData.get('price'))
    };
    
    try {
        await apiCall(API_ENDPOINTS.ADMIN_SHOWS, {
            method: 'POST',
            body: JSON.stringify(showData)
        });
        
        closeModal('addShowModal');
        form.reset();
        loadShowsList();
        alert('Show added successfully!');
    } catch (error) {
        console.error('Error adding show:', error);
        alert('Error adding show');
    }
}

async function deleteShow(id) {
    if (!confirm('Are you sure you want to delete this show?')) return;
    
    try {
        await apiCall(`${API_ENDPOINTS.ADMIN_SHOWS}/${id}`, {
            method: 'DELETE'
        });
        loadShowsList();
        alert('Show deleted successfully!');
    } catch (error) {
        console.error('Error deleting show:', error);
        alert('Error deleting show');
    }
}

// Bookings Management
async function loadAllBookings() {
    try {
        const bookings = await apiCall(API_ENDPOINTS.ADMIN_BOOKINGS);
        displayAllBookings(bookings);
    } catch (error) {
        console.error('Error loading bookings:', error);
    }
}

function displayAllBookings(bookings) {
    const container = document.getElementById('allBookingsList');
    container.innerHTML = bookings.map(booking => {
        const showDate = new Date(booking.show.showDate).toLocaleDateString();
        return `
            <div class="admin-item">
                <div>
                    <h4>Booking ${booking.bookingReference}</h4>
                    <p><strong>User:</strong> ${booking.user.username}</p>
                    <p><strong>Movie:</strong> ${booking.show.movie.title} @ ${booking.show.theater.name}</p>
                    <p><strong>Date:</strong> ${showDate} | <strong>Time:</strong> ${booking.show.showTime}</p>
                    <p><strong>Seats:</strong> ${booking.seatNumbers.join(', ')}</p>
                    <p><strong>Amount:</strong> ₹${booking.totalAmount}</p>
                    <p><strong>Status:</strong> <span class="booking-status ${booking.status.toLowerCase()}">${booking.status}</span></p>
                </div>
            </div>
        `;
    }).join('');
}

// Modal Management
function closeModal(modalId) {
    document.getElementById(modalId).classList.remove('show');
}

// Close modal on outside click
window.onclick = function(event) {
    if (event.target.classList.contains('modal')) {
        event.target.classList.remove('show');
    }
}
