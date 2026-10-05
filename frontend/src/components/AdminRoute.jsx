import { Navigate, Outlet } from "react-router-dom"
import { useAuth } from "../context/AuthContext"

function AdminRoute() {
    const { isAuthenticated, isAdmin, loading } = useAuth()

    if (loading) {
        return (
            <div className="flex min-h-screen items-center justify-center bg-slate-950 text-slate-400">
                Checking admin access...
            </div>
        )
    }

    if (!isAuthenticated) {
        return <Navigate to="/login" replace />
    }

    if (!isAdmin) {
        return <Navigate to="/" replace />
    }

    return <Outlet />
}

export default AdminRoute
