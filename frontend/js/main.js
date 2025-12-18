// Main Page - Movie Listing

let allMovies = [];
let currentCity = 'Mumbai';

// Load movies on page load
document.addEventListener('DOMContentLoaded', async () => {
    await loadMovies();
    updateCityDisplay();
});

// Load movies from API
async function loadMovies() {
    try {
        const movies = await apiCall(API_ENDPOINTS.MOVIES);
        allMovies = movies;
        displayMovies(movies);
    } catch (error) {
        console.error('Error loading movies:', error);
        document.getElementById('moviesContainer').innerHTML = 
            '<p>Error loading movies. Please try again later.</p>';
    }
}

// Display movies in grid
function displayMovies(movies) {
    const container = document.getElementById('moviesContainer');
    
    if (movies.length === 0) {
        container.innerHTML = '<p>No movies found.</p>';
        return;
    }
    
    container.innerHTML = movies.map(movie => `
        <div class="movie-card" onclick="goToMovieDetails(${movie.id})">
            <img src="${movie.posterUrl || 'https://via.placeholder.com/200x280?text=' + movie.title}" 
                 alt="${movie.title}">
            <div class="movie-info">
                <h3>${movie.title}</h3>
                <div class="movie-meta">
                    <span>${movie.language}</span>
                    ${movie.rating ? `<span class="movie-rating">⭐ ${movie.rating}</span>` : ''}
                </div>
                <div class="movie-meta">
                    <span>${movie.genre}</span>
                    <span>${movie.duration} min</span>
                </div>
            </div>
        </div>
    `).join('');
}

// Filter movies by genre
function filterMovies(genre) {
    // Update active tab
    document.querySelectorAll('.filter-tab').forEach(tab => {
        tab.classList.remove('active');
    });
    event.target.classList.add('active');
    
    if (genre === 'all') {
        displayMovies(allMovies);
    } else {
        const filtered = allMovies.filter(movie => movie.genre === genre);
        displayMovies(filtered);
    }
}

// Search movies
async function searchMovies() {
    const searchTerm = document.getElementById('searchInput').value.trim();
    
    if (!searchTerm) {
        displayMovies(allMovies);
        return;
    }
    
    try {
        const movies = await apiCall(`${API_ENDPOINTS.SEARCH_MOVIES}?title=${searchTerm}`);
        displayMovies(movies);
    } catch (error) {
        console.error('Error searching movies:', error);
    }
}

// Change city
function changeCity() {
    currentCity = document.getElementById('citySelect').value;
    updateCityDisplay();
}

function updateCityDisplay() {
    const heading = document.querySelector('.movies-section h2');
    if (heading) {
        heading.textContent = `Movies in ${currentCity}`;
    }
}

// Navigate to movie details
function goToMovieDetails(movieId) {
    window.location.href = `movie-details.html?id=${movieId}`;
}
