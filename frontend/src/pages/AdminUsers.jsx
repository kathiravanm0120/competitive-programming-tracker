import { useEffect, useMemo, useState } from "react"
import { getAdminUsers, setUserEnabled, setUserRole, deleteAdminUser } from "../services/adminService"

function AdminUsers() {
    const [users, setUsers] = useState([])
    const [search, setSearch] = useState("")
    const [loading, setLoading] = useState(true)
    const [busy, setBusy] = useState(null)
    const [error, setError] = useState("")

    async function load() {
        setLoading(true)
        setError("")
        try { setUsers(await getAdminUsers()) }
        catch (err) { setError(err.message || "Failed to load users") }
        finally { setLoading(false) }
    }

    useEffect(() => { load() }, [])

    const filtered = useMemo(() => {
        const q = search.toLowerCase().trim()
        if (!q) return users
        return users.filter((user) =>
            user.username.toLowerCase().includes(q) ||
            user.email.toLowerCase().includes(q)
        )
    }, [users, search])

    async function toggleEnabled(user) {
        setBusy(`status-${user.id}`)
        try {
            const updated = await setUserEnabled(user.id, !user.enabled)
            setUsers((current) => current.map((item) => item.id === updated.id ? updated : item))
        } catch (err) { setError(err.message || "Failed to update account") }
        finally { setBusy(null) }
    }

    async function toggleRole(user) {
        setBusy(`role-${user.id}`)
        try {
            const updated = await setUserRole(user.id, user.role === "ADMIN" ? "USER" : "ADMIN")
            setUsers((current) => current.map((item) => item.id === updated.id ? updated : item))
        } catch (err) { setError(err.message || "Failed to update role") }
        finally { setBusy(null) }
    }

    async function removeUser(user) {
        if (!window.confirm(`Delete user ${user.username}? This removes their account and related records according to database relationships.`)) return
        setBusy(`delete-${user.id}`)
        try {
            await deleteAdminUser(user.id)
            setUsers((current) => current.filter((item) => item.id !== user.id))
        } catch (err) { setError(err.message || "Failed to delete user") }
        finally { setBusy(null) }
    }

    return (
        <div className="space-y-6">
            <div>
                <p className="text-sm font-medium text-cyan-400">Administration</p>
                <h1 className="mt-1 text-3xl font-bold text-white">Users</h1>
            </div>

            {error && <div className="rounded-xl border border-red-500/30 bg-red-500/10 p-4 text-sm text-red-300">{error}</div>}

            <div className="rounded-2xl border border-slate-800 bg-slate-900 p-4">
                <input value={search} onChange={(e) => setSearch(e.target.value)} placeholder="Search by username or email..." className="w-full rounded-lg border border-slate-700 bg-slate-950 px-4 py-3 text-sm text-white outline-none focus:border-cyan-500" />
            </div>

            <div className="overflow-hidden rounded-2xl border border-slate-800 bg-slate-900">
                {loading ? <div className="p-10 text-center text-slate-500">Loading users...</div> :
                <div className="overflow-x-auto">
                    <table className="w-full text-left text-sm">
                        <thead className="border-b border-slate-800 text-xs uppercase tracking-wide text-slate-500">
                            <tr><th className="px-5 py-4">User</th><th className="px-5 py-4">Role</th><th className="px-5 py-4">Status</th><th className="px-5 py-4">Usage</th><th className="px-5 py-4">Actions</th></tr>
                        </thead>
                        <tbody>
                            {filtered.map((user) => (
                                <tr key={user.id} className="border-b border-slate-800 last:border-0">
                                    <td className="px-5 py-4"><p className="font-medium text-white">{user.username}</p><p className="text-xs text-slate-500">{user.email}</p></td>
                                    <td className="px-5 py-4"><span className={`rounded-full px-2.5 py-1 text-xs ${user.role === "ADMIN" ? "bg-violet-500/10 text-violet-300" : "bg-slate-800 text-slate-300"}`}>{user.role}</span></td>
                                    <td className="px-5 py-4"><span className={`rounded-full px-2.5 py-1 text-xs ${user.enabled ? "bg-emerald-500/10 text-emerald-300" : "bg-red-500/10 text-red-300"}`}>{user.enabled ? "Active" : "Disabled"}</span></td>
                                    <td className="px-5 py-4 text-xs text-slate-500">Problems {user.problemCount} · Platforms {user.platformAccountCount} · Goals {user.goalCount} · Tasks {user.plannerTaskCount}</td>
                                    <td className="px-5 py-4"><div className="flex flex-wrap gap-2">
                                        <button onClick={() => toggleEnabled(user)} disabled={busy === `status-${user.id}`} className="rounded-lg border border-slate-700 px-3 py-2 text-xs text-slate-300 hover:bg-slate-800 disabled:opacity-50">{user.enabled ? "Disable" : "Enable"}</button>
                                        <button onClick={() => toggleRole(user)} disabled={busy === `role-${user.id}`} className="rounded-lg border border-violet-500/30 px-3 py-2 text-xs text-violet-300 hover:bg-violet-500/10 disabled:opacity-50">{user.role === "ADMIN" ? "Make User" : "Make Admin"}</button>
                                        <button onClick={() => removeUser(user)} disabled={busy === `delete-${user.id}`} className="rounded-lg border border-red-500/30 px-3 py-2 text-xs text-red-300 hover:bg-red-500/10 disabled:opacity-50">Delete</button>
                                    </div></td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>}
            </div>
        </div>
    )
}

export default AdminUsers
