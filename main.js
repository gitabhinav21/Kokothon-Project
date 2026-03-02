/**
 * VolunteerVerse — main.js
 * Shared utilities: session management, navbar, animations.
 * Each page handles its own page-specific logic inline.
 */

document.addEventListener('DOMContentLoaded', () => {
    initNavbar();
    initScrollAnimations();
    initCounters();
    initDemoData();
    initToasts();
});



/* ─── Navbar / Session ───────────────────────────────────── */

/**
 * Dynamically updates the public navbar based on login state.
 * Dashboard pages use their own sidebar — this only affects .nav-links.
 */
function initNavbar() {
    const navLinks = document.getElementById('navLinks');
    if (!navLinks) return; // Dashboard pages don't have #navLinks

    const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true';
    const userRole = localStorage.getItem('userRole');

    if (isLoggedIn) {
        const dashboardUrl = userRole === 'student' ? 'student-dashboard.html' : 'ngo-dashboard.html';
        navLinks.innerHTML = `
            <li><a href="opportunities.html">Opportunities</a></li>
            <li><a href="index.html#features">Features</a></li>
            <li><a href="about.html">About Us</a></li>
            <li><a href="${dashboardUrl}" class="btn btn-primary">Dashboard</a></li>
            <li><a href="#" onclick="logout()" class="btn btn-outline">Logout</a></li>
        `;
    }
}

/**
 * Global logout — clears session and redirects to home.
 * Called by both public navbar and dashboard sidebar logout buttons.
 */
function logout() {
    localStorage.removeItem('isLoggedIn');
    localStorage.removeItem('userRole');
    localStorage.removeItem('user');
    window.location.href = 'index.html';
}

/* ─── Scroll Animations ─────────────────────────────────── */

function initScrollAnimations() {
    const observer = new IntersectionObserver(
        (entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.classList.add('reveal');
                }
            });
        },
        { threshold: 0.1 }
    );

    document.querySelectorAll('.feature-card').forEach(el => observer.observe(el));
}

/* ─── Counter Animation ─────────────────────────────────── */

function initCounters() {
    const counterObserver = new IntersectionObserver(
        (entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    animateCounter(entry.target);
                    counterObserver.unobserve(entry.target); // Only animate once
                }
            });
        },
        { threshold: 0.3 }
    );

    document.querySelectorAll('.counter').forEach(el => counterObserver.observe(el));
}

function animateCounter(el) {
    const target = parseInt(el.getAttribute('data-target'), 10);
    const duration = 2000;
    const stepTime = 20;
    const steps = duration / stepTime;
    const increment = target / steps;
    let current = 0;

    const timer = setInterval(() => {
        current += increment;
        if (current >= target) {
            el.innerText = target.toLocaleString() + '+';
            clearInterval(timer);
        } else {
            el.innerText = Math.floor(current).toLocaleString() + '+';
        }
    }, stepTime);
}

/* ─── Demo Data ─────────────────────────────────────────── */

function initDemoData() {
    if (!localStorage.getItem('opportunities')) {
        const demoOpps = [
            { id: 1, title: 'Beach Cleanup', ngo: 'Ocean Guardians', date: 'Mar 15', skills: ['environment', 'teamwork'], location: 'Juhu Beach' },
            { id: 2, title: 'Old Age Home Visit', ngo: 'Care Foundation', date: 'Mar 20', skills: ['empathy', 'communication'], location: 'Bandra' },
            { id: 3, title: 'Tech Literacy Workshop', ngo: 'Cyber Shiksha', date: 'Mar 25', skills: ['teaching', 'technology'], location: 'Online' }
        ];
        localStorage.setItem('opportunities', JSON.stringify(demoOpps));
    }

    if (!localStorage.getItem('applications')) {
        const demoApps = [
            { id: 101, title: 'Sustainability Drive', ngo: 'Green Earth', date: '2026-03-01', status: 'pending' },
            { id: 102, title: 'Youth Mentorship', ngo: 'Rising Stars', date: '2026-02-28', status: 'matched' }
        ];
        localStorage.setItem('applications', JSON.stringify(demoApps));
    }
}

/* ─── Security Utilities ────────────────────────────────── */

/**
 * Hashes a password using SHA-256 via Web Crypto API.
 */
async function hashPassword(password) {
    const msgUint8 = new TextEncoder().encode(password);
    const hashBuffer = await crypto.subtle.digest('SHA-256', msgUint8);
    const hashArray = Array.from(new Uint8Array(hashBuffer));
    return hashArray.map(b => b.toString(16).padStart(2, '0')).join('');
}

/**
 * Strict Gmail validation.
 */
function validateGmail(email) {
    return /^[a-zA-Z0-9._%+-]+@gmail\.com$/.test(email);
}

/* ─── UI Utilities ──────────────────────────────────────── */

function initToasts() {
    if (!document.getElementById('toastContainer')) {
        const container = document.createElement('div');
        container.id = 'toastContainer';
        container.className = 'toast-container';
        document.body.appendChild(container);
    }
}

function showToast(message, type = 'success') {
    const container = document.getElementById('toastContainer');
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;

    // Lucide icon based on type
    const iconName = type === 'success' ? 'check-circle' : 'alert-circle';
    toast.innerHTML = `<i data-lucide="${iconName}"></i> <span>${message}</span>`;

    container.appendChild(toast);
    lucide.createIcons();

    // Auto remove
    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateY(-20px)';
        setTimeout(() => toast.remove(), 300);
    }, 4000);
}

/* ─── Auth Protection ───────────────────────────────────── */

function checkAuth() {
    const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true';
    if (!isLoggedIn && (window.location.pathname.includes('dashboard'))) {
        window.location.href = 'login.html';
    }
}
async function hashPassword(password) {
    const msgBuffer = new TextEncoder().encode(password);
    const hashBuffer = await crypto.subtle.digest('SHA-256', msgBuffer);
    const hashArray = Array.from(new Uint8Array(hashBuffer));
    return hashArray.map(b => b.toString(16).padStart(2, '0')).join('');
}


