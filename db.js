/**
 * VolunteerVerse — db.js
 * Centralized data management using localStorage.
 */

const DB = {
    // --- Collections ---
    KEYS: {
        USERS: 'allUsers',
        OPPORTUNITIES: 'opportunities',
        APPLICATIONS: 'applications',
        CURRENT_USER: 'user',
        IS_LOGGED_IN: 'isLoggedIn',
        USER_ROLE: 'userRole'
    },

    // --- Core Methods ---
    get(key) {
        try {
            const data = localStorage.getItem(key);
            return data ? JSON.parse(data) : null;
        } catch (e) {
            console.error(`Error reading ${key} from localStorage:`, e);
            return null;
        }
    },

    set(key, value) {
        try {
            localStorage.setItem(key, JSON.stringify(value));
        } catch (e) {
            console.error(`Error writing ${key} to localStorage:`, e);
        }
    },

    // --- User Methods ---
    getUsers() {
        return this.get(this.KEYS.USERS) || [];
    },

    saveUser(user) {
        const users = this.getUsers();
        const index = users.findIndex(u => u.email === user.email);
        if (index !== -1) {
            users[index] = { ...users[index], ...user };
        } else {
            user.id = user.id || Date.now();
            users.push(user);
        }
        this.set(this.KEYS.USERS, users);
        // Update current user if it's the one logged in
        const current = this.getCurrentUser();
        if (current && current.email === user.email) {
            this.set(this.KEYS.CURRENT_USER, { ...current, ...user });
        }
    },

    getCurrentUser() {
        return this.get(this.KEYS.CURRENT_USER);
    },

    // --- Opportunity Methods ---
    getOpportunities() {
        return this.get(this.KEYS.OPPORTUNITIES) || [];
    },

    saveOpportunity(opp) {
        const opps = this.getOpportunities();
        opp.id = opp.id || Date.now();
        opps.unshift(opp); // Newest first
        this.set(this.KEYS.OPPORTUNITIES, opps);
        return opp;
    },

    // --- Application Methods ---
    getApplications() {
        return this.get(this.KEYS.APPLICATIONS) || [];
    },

    applyToOpportunity(student, opportunity, motivation) {
        const apps = this.getApplications();
        const newApp = {
            id: Date.now(),
            studentId: student.id,
            studentName: student.name,
            studentEmail: student.email,
            opportunityId: opportunity.id,
            title: opportunity.title,
            ngo: opportunity.ngo,
            date: new Date().toLocaleDateString('en-IN', { month: 'short', day: 'numeric', year: 'numeric' }),
            status: 'pending',
            motivation: motivation
        };
        apps.push(newApp);
        this.set(this.KEYS.APPLICATIONS, apps);
        return newApp;
    },

    updateApplicationStatus(appId, status) {
        const apps = this.getApplications();
        const index = apps.findIndex(a => a.id === appId);
        if (index !== -1) {
            apps[index].status = status;
            this.set(this.KEYS.APPLICATIONS, apps);
            return true;
        }
        return false;
    },

    getApplicationsByNGO(ngoName) {
        const name = ngoName.toLowerCase().trim();
        return this.getApplications().filter(a => (a.ngo || '').toLowerCase().trim() === name);
    },

    getApplicationsByStudent(studentEmail) {
        return this.getApplications().filter(a => a.studentEmail === studentEmail);
    }
};

// Initialize some demo data if empty
if (!localStorage.getItem(DB.KEYS.OPPORTUNITIES)) {
    const demoOpps = [
        { id: 1, title: 'Tech Mentor for Schoolkids', ngo: 'Vidya Initiative', verified: true, skills: ['tech', 'teaching'], date: '2026-03-15', location: 'Remote', duration: '4 weeks' },
        { id: 2, title: 'Urban Reforestation Drive', ngo: 'Prakriti Foundation', verified: true, skills: ['environment'], date: '2026-03-10', location: 'Mumbai', duration: '1 day' },
        { id: 3, title: 'English Tutor for Refugees', ngo: 'Global Bridge India', verified: false, skills: ['teaching'], date: '2026-03-20', location: 'Delhi', duration: 'On-going' }
    ];
    DB.set(DB.KEYS.OPPORTUNITIES, demoOpps);
}

window.DB = DB; // Make global for other scripts
