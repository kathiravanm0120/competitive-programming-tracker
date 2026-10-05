import { useEffect, useState } from "react"
import { Link } from "react-router-dom"
import { getAdminStats } from "../services/adminService"

const cards = [
    ["Total Users", "totalUsers"],
    ["Active Users", "activeUsers"],
    ["Problems", "totalProblems"],
    ["Platform Accounts", "totalPlatformAccounts"],
    ["Goals", "totalGoals"],
    ["Planner Tasks", "totalPlannerTasks"],
    ["Completed Tasks", "completedPlannerTasks"],
    ["Notifications", "totalNotifications"],
]

function AdminDashboard() {
    const [stats, setStats] = useState(null)
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState("")

    useEffect(() => {
        getAdminStats()
            .then(setStats)
            .catch((err) => setError(err.message || "Failed to load admin statistics"))
            .finally(() => setLoading(false))
    }, [])

    return (
        <div className="space-y-6">
            <div>
                <p className="text-sm font-medium text-cyan-400">Administration</p>
                <h1 className="mt-1 text-3xl font-bold text-white">Admin Dashboard</h1>
                <p className="mt-2 text-sm text-slate-500">Manage users, tracked problems, and system statistics.</p>
            </div>

            {error && <div className="rounded-xl border border-red-500/30 bg-red-500/10 p-4 text-sm text-red-300">{error}</div>}

            {loading ? (
                <div className="rounded-2xl border border-slate-800 bg-slate-900 p-10 text-center text-slate-500">Loading admin statistics...</div>
            ) : (
                <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
                    {cards.map(([label, key]) => (
                        <div key={key} className="rounded-2xl border border-slate-800 bg-slate-900 p-5">
                            <p className="text-sm text-slate-500">{label}</p>
                            <p className="mt-2 text-3xl font-bold text-white">{stats?.[key] ?? 0}</p>
                        </div>
                    ))}
                </div>
            )}

            <div className="grid gap-4 md:grid-cols-2">
                <Link to="/admin/users" className="rounded-2xl border border-slate-800 bg-slate-900 p-6 transition hover:border-cyan-500/40 hover:bg-slate-900/80">
                    <h2 className="text-lg font-semibold text-white">User Management</h2>
                    <p className="mt-2 text-sm text-slate-500">View users, disable accounts, change roles, or remove users.</p>
                </Link>
                <Link to="/admin/problems" className="rounded-2xl border border-slate-800 bg-slate-900 p-6 transition hover:border-cyan-500/40 hover:bg-slate-900/80">
                    <h2 className="text-lg font-semibold text-white">Problem Moderation</h2>
                    <p className="mt-2 text-sm text-slate-500">Review tracked problems and remove unwanted records.</p>
                </Link>
            </div>
        </div>
    )
}

export default AdminDashboard
