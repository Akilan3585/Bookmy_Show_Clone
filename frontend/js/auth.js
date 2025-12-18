// Authentication Functions

// Check if user is logged in and update UI
function checkAuthStatus() {
    const token = localStorage.getItem('token');
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    const authButton = document.getElementById('authButton');
    const userDisplay = document.getElementById('userDisplay');
    
    if (token && user.username) {
        if (authButton) {
            authButton.textContent = 'Logout';
            authButton.onclick = handleLogout;
        }
        if (userDisplay) {
            userDisplay.textContent = `Hello, ${user.username}`;
            userDisplay.style.marginRight = '10px';
        }
        return true;
    } else {
        if (authButton) {
            authButton.textContent = 'Sign In';
            authButton.onclick = () => window.location.href = 'login.html';
        }
        if (userDisplay) {
            userDisplay.textContent = '';
        }
        return false;
    }
}

// Login Handler
async function handleLogin(event) {
    event.preventDefault();
    
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    const errorMessage = document.getElementById('errorMessage');
    
    try {
        const response = await fetch(API_ENDPOINTS.LOGIN, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password })
        });
        
        const data = await response.json();
        
        if (response.ok) {
            localStorage.setItem('token', data.token);
            localStorage.setItem('user', JSON.stringify({
                username: data.username,
                email: data.email,
                role: data.role
            }));
            
            // Redirect based on role
            if (data.role === 'ROLE_ADMIN') {
                window.location.href = 'admin.html';
            } else {
                window.location.href = 'index.html';
            }
        } else {
            errorMessage.textContent = data.message || 'Invalid credentials';
            errorMessage.classList.add('show');
        }
    } catch (error) {
        errorMessage.textContent = 'Login failed. Please try again.';
        errorMessage.classList.add('show');
    }
}

// Register Handler
async function handleRegister(event) {
    event.preventDefault();
    
    const formData = {
        username: document.getElementById('username').value,
        email: document.getElementById('email').value,
        password: document.getElementById('password').value,
        firstName: document.getElementById('firstName').value,
        lastName: document.getElementById('lastName').value,
        phone: document.getElementById('phone').value,
        city: document.getElementById('city').value
    };
    
    const errorMessage = document.getElementById('errorMessage');
    const successMessage = document.getElementById('successMessage');
    
    try {
        const response = await fetch(API_ENDPOINTS.SIGNUP, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        });
        
        const data = await response.json();
        
        if (response.ok) {
            successMessage.textContent = 'Registration successful! Redirecting to login...';
            successMessage.classList.add('show');
            errorMessage.classList.remove('show');
            
            setTimeout(() => {
                window.location.href = 'login.html';
            }, 2000);
        } else {
            errorMessage.textContent = data.message || 'Registration failed';
            errorMessage.classList.add('show');
            successMessage.classList.remove('show');
        }
    } catch (error) {
        errorMessage.textContent = 'Registration failed. Please try again.';
        errorMessage.classList.add('show');
        successMessage.classList.remove('show');
    }
}

// Logout Handler
function handleLogout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    window.location.href = 'index.html';
}

// Handle Auth Button
function handleAuth() {
    const token = localStorage.getItem('token');
    if (token) {
        handleLogout();
    } else {
        window.location.href = 'login.html';
    }
}

// Require Authentication (server-validated)
async function requireAuth() {
    // Quick client check first
    const token = localStorage.getItem('token');
    if (!token || !checkAuthStatus()) {
        alert('Please login to continue');
        window.location.href = 'login.html';
        return false;
    }

    // Validate token with server
    try {
        const me = await apiCall(API_ENDPOINTS.ME);
        localStorage.setItem('user', JSON.stringify(me));
        checkAuthStatus();
        return true;
    } catch (error) {
        // Token invalid or expired
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        alert('Please login to continue');
        window.location.href = 'login.html';
        return false;
    }
}

// Check Admin Role
function requireAdmin() {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    if (user.role !== 'ROLE_ADMIN') {
        alert('Admin access required');
        window.location.href = 'index.html';
        return false;
    }
    return true;
}

// Initialize auth status on page load
document.addEventListener('DOMContentLoaded', checkAuthStatus);
