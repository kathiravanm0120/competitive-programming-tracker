const API_BASE_URL =
    import.meta.env.VITE_API_URL || "http://localhost:8080/api"

export function getToken() {
    return localStorage.getItem("token")
}

export function clearAuth() {
    localStorage.removeItem("token")
    localStorage.removeItem("username")
    localStorage.removeItem("role")
}

async function parseResponse(response) {
    const text = await response.text()

    let data = null

    if (text) {
        try {
            data = JSON.parse(text)
        } catch {
            data = text
        }
    }

    if (!response.ok) {
        const message =
            (data && typeof data === "object" &&
                (data.message || data.error)) ||
            (typeof data === "string" ? data : null) ||
            `Request failed with status ${response.status}`

        const error = new Error(message)
        error.status = response.status
        throw error
    }

    return data
}

export async function apiRequest(path, options = {}) {
    const token = getToken()

    const headers = {
        Accept: "application/json",
        ...(options.body ? { "Content-Type": "application/json" } : {}),
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
        ...(options.headers || {}),
    }

    const response = await fetch(`${API_BASE_URL}${path}`, {
        ...options,
        headers,
    })

    if (response.status === 401) {
        clearAuth()

        if (!window.location.pathname.startsWith("/login")) {
            window.location.href = "/login"
        }
    }

    if (response.status === 204) {
        return null
    }

    return parseResponse(response)
}
