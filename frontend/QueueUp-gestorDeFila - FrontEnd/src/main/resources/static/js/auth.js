import { api, setToken, clearToken, isAuthenticated } from './api.js';

async function login(email, password) {
    try {
        const response = await api.post("/auth/login", { email, password });
        

        const token = response.token || response.data?.token;
        
        if (token) {
            setToken(token);
            window.location.href = "../dashboard.html"; 
        } else {
            throw new Error("Token não encontrado na resposta do servidor.");
        }
    } catch (error) {
        console.error("Erro ao tentar fazer login:", error.message || error);
        alert("Falha no login. Verifique suas credenciais.");
    }
}

function logout() {
    clearToken(); 
}

function requireAuth() {
    if (!isAuthenticated()) {
        window.location.href = "../login.html";
    }
}

function redirectIfAuthenticated() {
    if (isAuthenticated()) {
        window.location.href = "../dashboard.html";
    }
}