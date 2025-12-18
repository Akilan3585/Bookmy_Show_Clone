// API Configuration
const API_BASE_URL = 'http://localhost:8080/api';

const API_ENDPOINTS = {
    // Auth
    LOGIN: `${API_BASE_URL}/auth/login`,
    SIGNUP: `${API_BASE_URL}/auth/signup`,
    ME: `${API_BASE_URL}/auth/me`,
    
    // Movies
    MOVIES: `${API_BASE_URL}/movies`,
    MOVIE_BY_ID: (id) => `${API_BASE_URL}/movies/${id}`,
    MOVIES_BY_GENRE: (genre) => `${API_BASE_URL}/movies/genre/${genre}`,
    SEARCH_MOVIES: `${API_BASE_URL}/movies/search`,
    
    // Theaters
    THEATERS: `${API_BASE_URL}/theaters`,
    THEATERS_BY_CITY: (city) => `${API_BASE_URL}/theaters/city/${city}`,
    
    // Shows
    SHOWS: `${API_BASE_URL}/shows`,
    SHOW_BY_ID: (id) => `${API_BASE_URL}/shows/${id}`,
    SHOWS_BY_MOVIE: (movieId) => `${API_BASE_URL}/shows/movie/${movieId}`,
    SHOWS_BY_MOVIE_CITY: (movieId, city, date) => 
        `${API_BASE_URL}/shows/movie/${movieId}/city/${city}?date=${date}`,
    
    // Seats
    SEATS_BY_SHOW: (showId) => `${API_BASE_URL}/seats/show/${showId}`,
    AVAILABLE_SEATS: (showId) => `${API_BASE_URL}/seats/show/${showId}/available`,
    
    // Bookings
    BOOKINGS: `${API_BASE_URL}/bookings`,
    BOOKING_BY_ID: (id) => `${API_BASE_URL}/bookings/${id}`,
    CONFIRM_BOOKING: (id) => `${API_BASE_URL}/bookings/${id}/confirm`,
    MY_BOOKINGS: `${API_BASE_URL}/bookings/my-bookings`,
    
    // Admin
    ADMIN_MOVIES: `${API_BASE_URL}/admin/movies`,
    ADMIN_THEATERS: `${API_BASE_URL}/admin/theaters`,
    ADMIN_SHOWS: `${API_BASE_URL}/admin/shows`,
    ADMIN_BOOKINGS: `${API_BASE_URL}/admin/bookings`
};

// Helper function to make API calls
async function apiCall(url, options = {}) {
    const token = localStorage.getItem('token');
    const headers = {
        'Content-Type': 'application/json',
        ...options.headers
    };
    
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }
    
    const response = await fetch(url, {
        ...options,
        headers
    });
    
    if (!response.ok) {
        // Try to parse error message from response
        const contentType = response.headers.get('content-type') || '';
        let errMessage = response.statusText;
        if (contentType.includes('application/json')) {
            const errBody = await response.json();
            errMessage = errBody.message || errBody.error || JSON.stringify(errBody);
        } else {
            const text = await response.text();
            if (text) errMessage = text;
        }

        if (response.status === 401) {
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            window.location.href = 'login.html';
        }
        throw new Error(`HTTP ${response.status}: ${errMessage}`);
    }
    
    return response.json();
}
