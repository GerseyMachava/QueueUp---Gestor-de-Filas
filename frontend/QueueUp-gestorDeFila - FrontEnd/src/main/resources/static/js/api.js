const API_URL = "http://localhost:8080/api";

localStorage.removeItem("token");

class ApiClient {
    async request(path, options = {}) {
        const token = sessionStorage.getItem("token");

        const headers = {
            "Content-Type": "application/json",
            ...options.headers
        };

        if (token) {
            headers.Authorization = `Bearer ${token}`;
        }

        const response = await fetch(`${API_URL}${path}`, {
            ...options,
            headers
        });

        let data = null;
        const contentType = response.headers.get("content-type");
        if (contentType && contentType.includes("application/json")) {
            data = await response.json();
        }

        if (!response.ok) {
            throw new Error(data?.message || `Erro HTTP: ${response.status}`);
        }

        return data;
    }

    get(path) {
        return this.request(path, { method: "GET" });
    }

    post(path, bodyData) {
        return this.request(path, {
            method: "POST",
            body: JSON.stringify(bodyData)
        });
    }

    put(path, bodyData) {
        return this.request(path, {
            method: "PUT",
            body: JSON.stringify(bodyData)
        });
    }

    delete(path) {
        return this.request(path, { method: "DELETE" });
    }
}

export function setToken(token) {
    sessionStorage.setItem("token", token);
}

export function getToken() {
    return sessionStorage.getItem("token");
}

export function isAuthenticated() {
    return !!sessionStorage.getItem("token");
}

export function clearToken() {
    sessionStorage.removeItem("token");
    window.location.href = "login.html";
}

export const api = new ApiClient();