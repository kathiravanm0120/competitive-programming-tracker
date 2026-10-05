import { useEffect, useMemo, useState } from "react"
import { deleteAdminProblem, getAdminProblems } from "../services/adminService"

function AdminProblems() {
    const [problems, setProblems] = useState([])
    const [search, setSearch] = useState("")
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState("")

    useEffect(() => {
        getAdminProblems()
            .then(setProblems)
            .catch((err) => setError(err.message || "Failed to load problems"))
            .finally(() => setLoading(false))
    }, [])

    const filtered = useMemo(() => {
        const q = search.toLowerCase().trim()
        if (!q) return problems
        return problems.filter((problem) =>
            problem.title?.toLowerCase().includes(q) ||
            problem.platform?.toLowerCase().includes(q) ||
            problem.topic?.toLowerCase().includes(q)
        )
    }, [problems, search])

    async function removeProblem(problem) {
        if (!window.confirm(`Delete ${problem.title}?`)) return
        try {
            await deleteAdminProblem(problem.id)
            setProblems((current) => current.filter((item) => item.id !== problem.id))
        } catch (err) { setError(err.message || "Failed to delete problem") }
    }

    return (
        <div className="space-y-6">
            <div>
                <p className="text-sm font-medium text-cyan-400">Administration</p>
                <h1 className="mt-1 text-3xl font-bold text-white">Problem Moderation</h1>
            </div>

            {error && <div className="rounded-xl border border-red-500/30 bg-red-500/10 p-4 text-sm text-red-300">{error}</div>}

            <div className="rounded-2xl border border-slate-800 bg-slate-900 p-4">
                <input value={search} onChange={(e) => setSearch(e.target.value)} placeholder="Search title, platform, or topic..." className="w-full rounded-lg border border-slate-700 bg-slate-950 px-4 py-3 text-sm text-white outline-none focus:border-cyan-500" />
            </div>

            <div className="overflow-hidden rounded-2xl border border-slate-800 bg-slate-900">
                {loading ? <div className="p-10 text-center text-slate-500">Loading problems...</div> :
                <div className="overflow-x-auto">
                    <table className="w-full text-left text-sm">
                        <thead className="border-b border-slate-800 text-xs uppercase tracking-wide text-slate-500">
                            <tr><th className="px-5 py-4">Problem</th><th className="px-5 py-4">Platform</th><th className="px-5 py-4">Difficulty</th><th className="px-5 py-4">Status</th><th className="px-5 py-4">Action</th></tr>
                        </thead>
                        <tbody>
                            {filtered.map((problem) => (
                                <tr key={problem.id} className="border-b border-slate-800 last:border-0">
                                    <td className="px-5 py-4"><p className="font-medium text-white">{problem.title}</p><p className="text-xs text-slate-500">{problem.topic} · by {problem.username}</p></td>
                                    <td className="px-5 py-4 text-slate-300">{problem.platform}</td>
                                    <td className="px-5 py-4 text-slate-300">{problem.difficulty}</td>
                                    <td className="px-5 py-4 text-slate-300">{problem.status}</td>
                                    <td className="px-5 py-4"><button onClick={() => removeProblem(problem)} className="rounded-lg border border-red-500/30 px-3 py-2 text-xs text-red-300 hover:bg-red-500/10">Delete</button></td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>}
            </div>
        </div>
    )
}

export default AdminProblems
