const API_URL = "http://localhost:8080/api";
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
    return fetch(`http://localhost:8080/api${path}`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${localStorage.getItem("token")}`
        },
        body: JSON.stringify(data)
    });
}

export function clearToken() {
    localStorage.removeItem("token");
    window.location.href = "../login.html";
}

export const api = new api();
