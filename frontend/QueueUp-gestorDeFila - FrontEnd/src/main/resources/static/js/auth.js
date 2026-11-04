async function login(email, password) {

    const res = await fetch(
        `Http://localhost:8080/api/auth/login`, {
        method: "POST",
        headers: { "Content-Type": 'application/json' },
        body: JSON.stringify({ email, password }),
    });

    const responseData = await res.json();
    localStorage.setItem("token", responseData.data.token);

    if (!res.ok) {
        throw new Error(`Error HTTP ${res.status}`)
    }

}


