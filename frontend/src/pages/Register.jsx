import { useState } from "react"
import { Link, Navigate, useNavigate } from "react-router-dom"
import { useAuth } from "../context/AuthContext"
import { AuthPageShell, ErrorBox, Field } from "./Login"

function Register() {
    const { register, login, isAuthenticated } = useAuth()
    const navigate = useNavigate()

    const [username, setUsername] = useState("")
    const [email, setEmail] = useState("")
    const [password, setPassword] = useState("")
    const [confirmPassword, setConfirmPassword] = useState("")
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState("")

    if (isAuthenticated) {
        return <Navigate to="/" replace />
    }

    const handleSubmit = async (event) => {
        event.preventDefault()
        setError("")

        if (!username.trim() || !email.trim() || !password) {
            setError("Fill in all required fields.")
            return
        }

        if (password.length < 6) {
            setError("Password must contain at least 6 characters.")
            return
        }

        if (password !== confirmPassword) {
            setError("Passwords do not match.")
            return
        }

        try {
            setLoading(true)

            await register(
                username.trim(),
                email.trim(),
                password
            )

            await login(username.trim(), password)
            navigate("/", { replace: true })
        } catch (err) {
            setError(err.message || "Registration failed.")
        } finally {
            setLoading(false)
        }
    }

    return (
        <AuthPageShell
            title="Create your account"
            subtitle="Start tracking your competitive programming journey."
        >
            <form onSubmit={handleSubmit} className="space-y-5">
                {error && <ErrorBox message={error} />}

                <Field
                    label="Username"
                    value={username}
                    onChange={setUsername}
                    placeholder="your_username"
                    autoComplete="username"
                />

                <Field
                    label="Email"
                    type="email"
                    value={email}
                    onChange={setEmail}
                    placeholder="you@example.com"
                    autoComplete="email"
                />

                <Field
                    label="Password"
                    type="password"
                    value={password}
                    onChange={setPassword}
                    placeholder="At least 6 characters"
                    autoComplete="new-password"
                />

                <Field
                    label="Confirm Password"
                    type="password"
                    value={confirmPassword}
                    onChange={setConfirmPassword}
                    placeholder="Repeat your password"
                    autoComplete="new-password"
                />

                <button
                    disabled={loading}
                    className="w-full rounded-lg bg-cyan-500 px-4 py-3 text-sm font-semibold text-slate-950 transition hover:bg-cyan-400 disabled:cursor-not-allowed disabled:opacity-60"
                >
                    {loading ? "Creating account..." : "Create Account"}
                </button>

                <p className="text-center text-sm text-slate-400">
                    Already have an account?{" "}
                    <Link
                        to="/login"
                        className="font-medium text-cyan-400 hover:text-cyan-300"
                    >
                        Sign in
                    </Link>
                </p>
            </form>
        </AuthPageShell>
    )
}

export default Register
