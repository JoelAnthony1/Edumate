const API_URL = 'http://localhost:8080/api';

// Handle login form submission
const loginForm = document.getElementById('loginForm');
if (loginForm) {
    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;

        try {
            const response = await fetch(`${API_URL}/auth/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ email, password }),
            });

            if (response.ok) {
                const data = await response.json();
                localStorage.setItem('token', data.token);
                window.location.href = '../index.html'; // Redirect to home page after login
            } else {
                alert('Invalid credentials');
            }
        } catch (error) {
            console.error('Error:', error);
            alert('An error occurred during login');
        }
    });
}

// Handle registration form submission
const registerForm = document.getElementById('registerForm');
if (registerForm) {
    registerForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const name = document.getElementById('name').value;
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;
        const role = document.getElementById('role').value;

        try {
            const response = await fetch(`${API_URL}/auth/register`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    name,
                    email,
                    password,
                    role
                }),
            });

            if (response.ok) {
                alert('Registration successful! Please login.');
                window.location.href = 'login.html'; // Redirect to login page after registration
            } else {
                const data = await response.json();
                alert(data.message || 'Registration failed');
            }
        } catch (error) {
            console.error('Error:', error);
            alert('An error occurred during registration');
        }
    });
}

// Check authentication status and update UI
function checkAuth() {
    const token = localStorage.getItem('token');
    const navLinks = document.querySelector('.nav-links');
    const loginBtn = document.getElementById('login-btn');
    const registerBtn = document.getElementById('register-btn');
    const logoutBtn = document.getElementById('logout-btn');
    const dashboardLink = document.getElementById('dashboard-link');
    const dashboardBtn = document.getElementById('dashboard-btn');

    if (token) {
        // User is logged in
        navLinks.innerHTML = `
            <a href="index.html">Home</a>
            <a href="home" id="dashboard-link">Dashboard</a>
            <a href="#" id="logout-btn" onclick="logout()">Logout</a>
        `;

        // Show/hide buttons based on authentication status
        loginBtn.style.display = 'none';
        registerBtn.style.display = 'none';
        dashboardBtn.style.display = 'inline-block';
    } else {
        // User is not logged in
        navLinks.innerHTML = `
            <a href="index.html">Home</a>
            <a href="pages/login.html" id="login-link">Login</a>
            <a href="pages/register.html" id="register-link">Register</a>
        `;

        // Show login/register buttons and hide logout
        loginBtn.style.display = 'inline-block';
        registerBtn.style.display = 'inline-block';
        dashboardBtn.style.display = 'none';
    }
}

// Logout function
function logout() {
    localStorage.removeItem('token');
    window.location.href = 'pages/login.html'; // Redirect to login page after logout
}

// Call checkAuth when the page loads
document.addEventListener('DOMContentLoaded', checkAuth);
