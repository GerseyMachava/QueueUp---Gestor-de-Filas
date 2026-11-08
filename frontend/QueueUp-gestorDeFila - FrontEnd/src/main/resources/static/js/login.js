import { redirectIfAuthenticated, login } from './auth.js';

// 1. Executa assim que a página carrega
function init() {
    redirectIfAuthenticated();

    // Adiciona o listener no formulário de login
    const form = document.getElementById("login-form");
    if (form) {
        form.addEventListener("submit", handleSubmit);
    }
}

async function handleSubmit(event) {
    event.preventDefault(); // Evita o recarregamento padrão da página
    clearError();           // Limpa erros anteriores antes de tentar de novo

    const emailInput = document.getElementById("email");
    const passwordInput = document.getElementById("password");

    const email = emailInput ? emailInput.value : "";
    const password = passwordInput ? passwordInput.value : "";

    if (!email || !password) {
        showError("Por favor, preencha todos os campos.");
        return;
    }

    try {
        await login(email, password);


    } catch (error) {
        console.error("Erro ao tentar fazer login:", error);
        showError(error.message || "Falha no login. Verifique suas credenciais.");
    }
}

function showError(message) {
    const errorElement = document.getElementById("login-error");
    if (errorElement) {
        errorElement.textContent = message;
        errorElement.style.display = "block"; // Garante que aparece (caso esteja oculto via CSS)
    }
}

function clearError() {
    const errorElement = document.getElementById("login-error");
    if (errorElement) {
        errorElement.textContent = "";
        errorElement.style.display = "none";
    }
}


document.addEventListener("DOMContentLoaded", init);

