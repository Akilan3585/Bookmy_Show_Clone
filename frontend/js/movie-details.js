// Movie Details Page

let movieId;
let currentMovie;
let selectedDate = 0;
let currentCity = 'Mumbai';

document.addEventListener('DOMContentLoaded', async () => {
    const urlParams = new URLSearchParams(window.location.search);
    movieId = urlParams.get('id');
    
    if (!movieId) {
        window.location.href = 'index.html';
        return;
    }
    
    currentCity = document.getElementById('citySelect')?.value || 'Mumbai';
    await loadMovieDetails();
    await loadShows();
    setupDateButtons();
});

// Load movie details
async function loadMovieDetails() {
    try {
        currentMovie = await apiCall(API_ENDPOINTS.MOVIE_BY_ID(movieId));
        displayMovieDetails(currentMovie);
    } catch (error) {
        console.error('Error loading movie details:', error);
        document.getElementById('movieDetailsContainer').innerHTML = 
            '<p>Error loading movie details.</p>';
    }
}

// Display movie details
function displayMovieDetails(movie) {
    const container = document.getElementById('movieDetailsContainer');
    container.innerHTML = `
        <div class="movie-poster">
            <img src="${movie.posterUrl || 'https://via.placeholder.com/250x350?text=' + movie.title}" 
                 alt="${movie.title}">
        </div>
        <div class="movie-details-content">
            <h1>${movie.title}</h1>
            <p><strong>Genre:</strong> ${movie.genre} | <strong>Duration:</strong> ${movie.duration} min</p>
            <p><strong>Language:</strong> ${movie.language}</p>
            ${movie.rating ? `<p><strong>Rating:</strong> ⭐ ${movie.rating}/10</p>` : ''}
            <p><strong>Release Date:</strong> ${new Date(movie.releaseDate).toLocaleDateString()}</p>
            <p><strong>Director:</strong> ${movie.director}</p>
            ${movie.cast && movie.cast.length > 0 ? 
                `<p><strong>Cast:</strong> ${movie.cast.join(', ')}</p>` : ''}
            <p><strong>Description:</strong></p>
            <p>${movie.description}</p>
        </div>
    `;
}

// Setup date buttons
function setupDateButtons() {
    const today = new Date();
    for (let i = 0; i <= 2; i++) {
        const date = new Date(today);
        date.setDate(date.getDate() + i);
        const btn = document.getElementById(`date${i}`);
        if (btn && i > 0) {
            btn.textContent = date.toLocaleDateString('en-US', { weekday: 'short', day: 'numeric', month: 'short' });
        }
    }
}

// Select date
async function selectDate(dateOffset) {
    selectedDate = dateOffset;
    
    // Update active button
    document.querySelectorAll('.date-btn').forEach((btn, index) => {
        btn.classList.toggle('active', index === dateOffset);
    });
    
    await loadShows();
}

// Change city
function changeCity() {
    currentCity = document.getElementById('citySelect').value;
    loadShows();
}

// Load shows
async function loadShows() {
    const date = new Date();
    date.setDate(date.getDate() + selectedDate);
    const dateStr = date.toISOString().split('T')[0];
    
    try {
        const shows = await apiCall(
            API_ENDPOINTS.SHOWS_BY_MOVIE_CITY(movieId, currentCity, dateStr)
        );
        displayShows(shows);
    } catch (error) {
        console.error('Error loading shows:', error);
        document.getElementById('showsContainer').innerHTML = 
            '<p>No shows available for the selected date and city.</p>';
    }
}

// Display shows grouped by theater
function displayShows(shows) {
    const container = document.getElementById('showsContainer');
    
    if (shows.length === 0) {
        container.innerHTML = '<p>No shows available for the selected date and city.</p>';
        return;
    }
    
    // Group shows by theater
    const theaterShows = {};
    shows.forEach(show => {
        const theaterId = show.theater.id;
        if (!theaterShows[theaterId]) {
            theaterShows[theaterId] = {
                theater: show.theater,
                shows: []
            };
        }
        theaterShows[theaterId].shows.push(show);
    });
    
    // Display grouped shows
    container.innerHTML = Object.values(theaterShows).map(({ theater, shows }) => `
        <div class="theater-shows">
            <div class="theater-name">${theater.name}</div>
            <p style="color: #666; margin-bottom: 10px;">${theater.address}</p>
            <div class="show-times">
                ${shows.map(show => `
                    <a href="seat-selection.html?showId=${show.id}" class="show-time-btn">
                        ${show.showTime}
                        <br>
                        <small>₹${show.price}</small>
                    </a>
                `).join('')}
            </div>
        </div>
    `).join('');
}
