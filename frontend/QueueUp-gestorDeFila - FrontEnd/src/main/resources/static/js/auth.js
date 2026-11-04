import { clearToken } from "./api.js";

async function login(email, password) {
    const res = await fetch(
        "http://localhost:8080/api/auth/login",
        {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ email, password })
        }
    );

    if (!res.ok) {
        throw new Error(`HTTP ${res.status}`);
    }

    const responseData = await res.json();

    localStorage.setItem(
        "token",
        responseData.data.token
    );
}

function logout() {
    clearToken();
}

