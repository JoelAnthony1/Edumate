// Check if user is authenticated
function isAuthenticated() {
    return localStorage.getItem('token') !== null;
}

// Get user role from JWT token
function getUserRole() {
    const token = localStorage.getItem('token');
    if (token) {
        try {
            // Decode JWT token (get payload part)
            const payload = JSON.parse(atob(token.split('.')[1]));
            return payload.role;
        } catch (e) {
            console.error('Error decoding token:', e);
            return null;
        }
    }
    return null;
}

// Update navigation based on authentication status
function updateNavigation() {
    const navLinks = document.querySelector('.nav-links');
    const role = getUserRole();

    if (isAuthenticated()) {
        let dashboardLink = '';
        if (role === 'TEACHER') {
            dashboardLink = '<a href="pages/teacher-dashboard.html">Teacher Dashboard</a>';
        } else if (role === 'STUDENT') {
            dashboardLink = '<a href="pages/student-dashboard.html">Student Dashboard</a>';
        }

        navLinks.innerHTML = `
            <a href="index.html">Home</a>
            ${dashboardLink}
            <a href="#" onclick="logout()">Logout</a>
        `;
    } else {
        navLinks.innerHTML = `
            <a href="index.html">Home</a>
            <a href="pages/login.html">Login</a>
            <a href="pages/register.html">Register</a>
        `;
    }
}

// Protect routes that require authentication
function protectRoute() {
    if (!isAuthenticated()) {
        window.location.href = '/pages/login.html';
        return;
    }

    // Check for specific role requirements
    const role = getUserRole();
    const currentPath = window.location.pathname;

    if (currentPath.includes('teacher-dashboard') && role !== 'TEACHER') {
        window.location.href = '/index.html';
    } else if (currentPath.includes('student-dashboard') && role !== 'STUDENT') {
        window.location.href = '/index.html';
    }
}

// Load user profile data
async function loadUserProfile() {
    if (!isAuthenticated()) return;

    try {
        const response = await fetch('http://localhost:8080/api/users/profile', {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });

        if (response.ok) {
            const userData = await response.json();
            displayUserProfile(userData);
        }
    } catch (error) {
        console.error('Error loading user profile:', error);
    }
}

// Display user profile information
function displayUserProfile(userData) {
    const profileSection = document.getElementById('userProfile');
    if (profileSection) {
        profileSection.innerHTML = `
            <h2>Welcome, ${userData.name}!</h2>
            <p>Email: ${userData.email}</p>
            <p>Role: ${userData.role}</p>
        `;
    }
}

// Show/hide elements based on user role
function updateUIByRole() {
    const role = getUserRole();
    const teacherElements = document.querySelectorAll('.teacher-only');
    const studentElements = document.querySelectorAll('.student-only');

    teacherElements.forEach(element => {
        element.style.display = role === 'TEACHER' ? 'block' : 'none';
    });

    studentElements.forEach(element => {
        element.style.display = role === 'STUDENT' ? 'block' : 'none';
    });
}

// Handle form submissions with loading state
function handleFormSubmit(formId, submitCallback) {
    const form = document.getElementById(formId);
    if (form) {
        form.addEventListener('submit', async (e) => {
            e.preventDefault();
            const submitButton = form.querySelector('button[type="submit"]');
            const originalText = submitButton.textContent;
            
            try {
                submitButton.textContent = 'Loading...';
                submitButton.disabled = true;
                await submitCallback(e);
            } finally {
                submitButton.textContent = originalText;
                submitButton.disabled = false;
            }
        });
    }
}

// Show notification messages
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.textContent = message;

    document.body.appendChild(notification);

    setTimeout(() => {
        notification.remove();
    }, 3000);
}

// Initialize page
document.addEventListener('DOMContentLoaded', () => {
    updateNavigation();
    updateUIByRole();
    loadUserProfile();

    // Add event listeners for dynamic elements
    document.addEventListener('click', (e) => {
        if (e.target.matches('.logout-button')) {
            logout();
        }
    });
});

// Handle page visibility changes
document.addEventListener('visibilitychange', () => {
    if (document.visibilityState === 'visible') {
        // Refresh data when page becomes visible
        loadUserProfile();
    }
});

// Export functions for use in other scripts
window.isAuthenticated = isAuthenticated;
window.getUserRole = getUserRole;
window.showNotification = showNotification;