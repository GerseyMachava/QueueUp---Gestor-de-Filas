import { api, setToken, clearToken, isAuthenticated } from './api.js';

export async function login(email, password) {
    try {
        const response = await api.post("/auth/login", { email, password });
        const token = response.token || response.data?.token;

        if (token) {
            setToken(token);
            window.location.href = "dashboard.html";
        } else {
            throw new Error("Token não retornado pelo servidor.");
        }
    } catch (error) {
        throw new Error(error.message || "Credenciais inválidas.");
    }
}

export function logout() {
    clearToken();
}

function requireAuth() {
    if (!isAuthenticated()) {
        window.location.href = "login.html";
    }
}

export function redirectIfAuthenticated() {
    if (isAuthenticated()) {
        window.location.href = "dashboard.html";
    }
}