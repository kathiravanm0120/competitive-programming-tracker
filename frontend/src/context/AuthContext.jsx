import { createContext, useContext, useEffect, useMemo, useState } from "react"

const AuthContext = createContext(null)
const API_ROOT = import.meta.env.VITE_API_URL || "http://localhost:8080/api"
const API_URL = `${API_ROOT}/auth`

function getStoredUser() {
    const username = localStorage.getItem("username")
    const role = localStorage.getItem("role") || "USER"
    return username ? { username, role } : null
}

async function readResponse(response) {
    const text = await response.text()
    let data = {}
    try {
        data = text ? JSON.parse(text) : {}
    } catch {
        data = { error: text }
    }

    if (!response.ok) {
        throw new Error(
            data.message ||
            data.error ||
            `Request failed with status ${response.status}`
        )
    }

    return data
}

export function AuthProvider({ children }) {
    const [user, setUser] = useState(getStoredUser)
    const [loading, setLoading] = useState(Boolean(localStorage.getItem("token")))

    useEffect(() => {
        async function loadCurrentUser() {
            const token = localStorage.getItem("token")
            if (!token) {
                setLoading(false)
                return
            }

            try {
                const response = await fetch(`${API_URL}/me`, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                })

                if (!response.ok) {
                    throw new Error("Session expired")
                }

                const data = await response.json()
                localStorage.setItem("username", data.username)
                localStorage.setItem("role", data.role || "USER")
                setUser({ username: data.username, role: data.role || "USER" })
            } catch {
                localStorage.removeItem("token")
                localStorage.removeItem("username")
                localStorage.removeItem("role")
                setUser(null)
            } finally {
                setLoading(false)
            }
        }

        loadCurrentUser()
    }, [])

    const login = async (username, password) => {
        const response = await fetch(`${API_URL}/login`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username, password }),
        })

        const data = await readResponse(response)

        localStorage.setItem("token", data.token)
        localStorage.setItem("username", data.username)
        localStorage.setItem("role", data.role || "USER")

        setUser({ username: data.username, role: data.role || "USER" })

        return data
    }

    const register = async (username, email, password) => {
        const response = await fetch(`${API_URL}/register`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username, email, password }),
        })
        return readResponse(response)
    }

    const logout = () => {
        localStorage.removeItem("token")
        localStorage.removeItem("username")
        localStorage.removeItem("role")
        setUser(null)
    }

    const value = useMemo(() => ({
        user,
        login,
        register,
        logout,
        loading,
        isAuthenticated: Boolean(user && localStorage.getItem("token")),
        isAdmin: user?.role === "ADMIN",
    }), [user, loading])

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    )
}

export function useAuth() {
    const context = useContext(AuthContext)
    if (!context) {
        throw new Error("useAuth must be used inside AuthProvider")
    }
    return context
}
