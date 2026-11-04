const API_URL = "http://localhost:8080/api";
const token = localStorage.getItem("token")

class api {
    async request(path, options = {}) {
        const token = localStorage.getItem("token")

        const headers = {
            "Content-Type": "application/json",
            ...options.headers
        };
        if (token) {
            headers.Authorization = `Bearer ${token}`;
        };

        const response = await fetch(`
            ${API_URL}${path}`, {
            ...options,
            headers
        });
        const apiResponse = await response.json();
        if (!response.ok) {
            throw new Error(apiResponse.message || `HTTP ${response.status}`);
        }

    }

}
function post(path, data) {
    return fetch(`${API_URL}${path}`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${localStorage.getItem("token")}`
        },
        body: JSON.stringify(data)
    });
}
function get(path) {
    return fetch(`${API_URL}${path}`, {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
        }
    });
}

function put(path, data) {
    return fetch(`${API_URL}${path}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify(data)
    });

}

function deleteItem(path) {
    return fetch(`${API_URL}${path}`, {
        method: "DELETE",
        headers: {
            "Content - Type": "application/json",
            "Authorization": `Bearer ${token}`
        },

    })
}

function setToken(token) {
    localStorage.setItem("token", token);
}

function getToken() {
    return localStorage.getItem("token");
}

function isAuthenticated() {
    return !!localStorage.getItem("token");
}


export function clearToken() {
    localStorage.removeItem("token");
    window.location.href = "../login.html";
}


export const api = new api();
