import { useState } from "react"
import { Link, Navigate, useLocation, useNavigate } from "react-router-dom"
import { useAuth } from "../context/AuthContext"

function Login() {
    const { login, isAuthenticated } = useAuth()
    const navigate = useNavigate()
    const location = useLocation()

    const [username, setUsername] = useState("")
    const [password, setPassword] = useState("")
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState("")

    if (isAuthenticated) {
        return <Navigate to="/" replace />
    }

    const from = location.state?.from?.pathname || "/"

    const handleSubmit = async (event) => {
        event.preventDefault()
        setError("")

        if (!username.trim() || !password) {
            setError("Enter your username and password.")
            return
        }

        try {
            setLoading(true)
            await login(username.trim(), password)
            navigate(from, { replace: true })
        } catch (err) {
            setError(err.message || "Login failed.")
        } finally {
            setLoading(false)
        }
    }

    return (
        <AuthPageShell
            title="Welcome back"
            subtitle="Sign in to continue tracking your competitive programming progress."
        >
            <form onSubmit={handleSubmit} className="space-y-5">
                {error && <ErrorBox message={error} />}

                <Field
                    label="Username"
                    value={username}
                    onChange={setUsername}
                    placeholder="testuser"
                    autoComplete="username"
                />

                <Field
                    label="Password"
                    type="password"
                    value={password}
                    onChange={setPassword}
                    placeholder="••••••••"
                    autoComplete="current-password"
                />

                <button
                    disabled={loading}
                    className="w-full rounded-lg bg-cyan-500 px-4 py-3 text-sm font-semibold text-slate-950 transition hover:bg-cyan-400 disabled:cursor-not-allowed disabled:opacity-60"
                >
                    {loading ? "Signing in..." : "Sign In"}
                </button>

                <p className="text-center text-sm text-slate-400">
                    Don't have an account?{" "}
                    <Link
                        to="/register"
                        className="font-medium text-cyan-400 hover:text-cyan-300"
                    >
                        Create one
                    </Link>
                </p>
            </form>
        </AuthPageShell>
    )
}

export function AuthPageShell({ title, subtitle, children }) {
    return (
        <div className="flex min-h-screen items-center justify-center bg-slate-950 px-4 py-10 text-white">
            <div className="w-full max-w-md">
                <div className="mb-8 text-center">
                    <p className="text-sm font-semibold tracking-wide text-cyan-400">
                        CP TRACKER
                    </p>
                    <h1 className="mt-3 text-3xl font-bold">{title}</h1>
                    <p className="mt-2 text-sm leading-6 text-slate-400">
                        {subtitle}
                    </p>
                </div>

                <div className="rounded-2xl border border-slate-800 bg-slate-900 p-6 shadow-2xl">
                    {children}
                </div>
            </div>
        </div>
    )
}

function Field({
    label,
    type = "text",
    value,
    onChange,
    placeholder,
    autoComplete,
}) {
    return (
        <label className="block">
            <span className="mb-2 block text-sm font-medium text-slate-300">
                {label}
            </span>
            <input
                type={type}
                value={value}
                onChange={(event) => onChange(event.target.value)}
                placeholder={placeholder}
                autoComplete={autoComplete}
                className="w-full rounded-lg border border-slate-700 bg-slate-950 px-4 py-3 text-sm text-white outline-none placeholder:text-slate-600 focus:border-cyan-500"
            />
        </label>
    )
}

export function ErrorBox({ message }) {
    return (
        <div className="rounded-lg border border-red-500/30 bg-red-500/10 px-4 py-3 text-sm text-red-300">
            {message}
        </div>
    )
}

export { Field }

export default Login
