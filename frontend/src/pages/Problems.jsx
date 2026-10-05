import { useEffect, useMemo, useState } from "react"
import AddProblemModal from "../components/AddProblemModal"
import {
    createProblem,
    deleteProblem,
    getProblems,
    updateProblem,
} from "../services/problemService"

function Problems() {
    const [problems, setProblems] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState("")
    const [actionError, setActionError] = useState("")

    const [search, setSearch] = useState("")
    const [difficulty, setDifficulty] = useState("All")
    const [status, setStatus] = useState("All")
    const [platform, setPlatform] = useState("All")

    const [showModal, setShowModal] = useState(false)
    const [editingProblem, setEditingProblem] = useState(null)
    const [saving, setSaving] = useState(false)

    useEffect(() => {
        loadProblems()
    }, [])

    async function loadProblems() {
        try {
            setLoading(true)
            setError("")
            const data = await getProblems()
            setProblems(Array.isArray(data) ? data : [])
        } catch (err) {
            console.error("Failed to load problems:", err)
            setError(err.message || "Failed to load problems.")
        } finally {
            setLoading(false)
        }
    }

    const filteredProblems = useMemo(() => {
        const searchText = search.trim().toLowerCase()

        return problems.filter((problem) => {
            const matchesSearch =
                !searchText ||
                problem.title?.toLowerCase().includes(searchText) ||
                problem.topic?.toLowerCase().includes(searchText) ||
                problem.platform?.toLowerCase().includes(searchText)

            const matchesDifficulty =
                difficulty === "All" ||
                problem.difficulty === difficulty

            const matchesStatus =
                status === "All" ||
                problem.status === status

            const matchesPlatform =
                platform === "All" ||
                problem.platform === platform

            return (
                matchesSearch &&
                matchesDifficulty &&
                matchesStatus &&
                matchesPlatform
            )
        })
    }, [problems, search, difficulty, status, platform])

    const platforms = useMemo(
        () => [...new Set(problems.map((problem) => problem.platform).filter(Boolean))],
        [problems]
    )

    const handleSaveProblem = async (problem) => {
        try {
            setSaving(true)
            setActionError("")

            if (editingProblem) {
                const updated = await updateProblem(
                    editingProblem.id,
                    problem
                )

                setProblems((current) =>
                    current.map((item) =>
                        item.id === updated.id ? updated : item
                    )
                )

                setEditingProblem(null)
            } else {
                const created = await createProblem(problem)
                setProblems((current) => [...current, created])
                setShowModal(false)
            }
        } catch (err) {
            console.error("Failed to save problem:", err)
            setActionError(err.message || "Failed to save problem.")
        } finally {
            setSaving(false)
        }
    }

    async function handleDeleteProblem(id) {
        if (!window.confirm("Are you sure you want to delete this problem?")) {
            return
        }

        try {
            setActionError("")
            await deleteProblem(id)

            setProblems((current) =>
                current.filter((problem) => problem.id !== id)
            )
        } catch (err) {
            console.error("Failed to delete problem:", err)
            setActionError(err.message || "Failed to delete problem.")
        }
    }

    function openEdit(problem) {
        setActionError("")
        setEditingProblem(problem)
    }

    function openAdd() {
        setActionError("")
        setShowModal(true)
    }

    function closeModals() {
        if (saving) return
        setShowModal(false)
        setEditingProblem(null)
    }

    if (loading) {
        return (
            <div className="flex min-h-[400px] items-center justify-center">
                <div className="text-center">
                    <div className="mx-auto h-8 w-8 animate-spin rounded-full border-2 border-slate-700 border-t-cyan-400" />
                    <p className="mt-4 text-sm text-slate-400">
                        Loading problems...
                    </p>
                </div>
            </div>
        )
    }

    if (error) {
        return (
            <div className="space-y-6">
                <PageHeader />

                <div className="rounded-xl border border-red-500/30 bg-red-500/10 p-6">
                    <h2 className="font-semibold text-red-400">
                        Failed to load problems
                    </h2>

                    <p className="mt-2 text-sm text-red-300">{error}</p>

                    <button
                        type="button"
                        onClick={loadProblems}
                        className="mt-4 rounded-lg bg-red-500 px-4 py-2 text-sm font-semibold text-white transition hover:bg-red-400"
                    >
                        Try Again
                    </button>
                </div>
            </div>
        )
    }

    return (
        <div className="space-y-6">
            <PageHeader />

            {actionError && (
                <div className="rounded-xl border border-red-500/30 bg-red-500/10 px-5 py-4 text-sm text-red-300">
                    {actionError}
                </div>
            )}

            <div className="grid grid-cols-1 gap-4 sm:grid-cols-3">
                <SummaryCard title="Total Problems" value={problems.length} />
                <SummaryCard
                    title="Solved"
                    value={problems.filter((p) => p.status === "Solved").length}
                />
                <SummaryCard
                    title="Attempted"
                    value={problems.filter((p) => p.status === "Attempted").length}
                />
            </div>

            <div className="space-y-4 rounded-xl border border-slate-800 bg-slate-900 p-4">
                <div className="flex flex-col gap-3 lg:flex-row">
                    <input
                        type="search"
                        placeholder="Search title, topic, or platform..."
                        value={search}
                        onChange={(event) => setSearch(event.target.value)}
                        className="flex-1 rounded-lg border border-slate-700 bg-slate-950 px-4 py-2.5 text-sm text-white outline-none placeholder:text-slate-500 focus:border-cyan-500"
                    />

                    <select
                        value={difficulty}
                        onChange={(event) => setDifficulty(event.target.value)}
                        className="rounded-lg border border-slate-700 bg-slate-950 px-4 py-2.5 text-sm text-white outline-none focus:border-cyan-500"
                    >
                        <option value="All">All Difficulties</option>
                        <option value="Easy">Easy</option>
                        <option value="Medium">Medium</option>
                        <option value="Hard">Hard</option>
                    </select>

                    <select
                        value={status}
                        onChange={(event) => setStatus(event.target.value)}
                        className="rounded-lg border border-slate-700 bg-slate-950 px-4 py-2.5 text-sm text-white outline-none focus:border-cyan-500"
                    >
                        <option value="All">All Statuses</option>
                        <option value="Solved">Solved</option>
                        <option value="Attempted">Attempted</option>
                        <option value="Unsolved">Unsolved</option>
                    </select>

                    <select
                        value={platform}
                        onChange={(event) => setPlatform(event.target.value)}
                        className="rounded-lg border border-slate-700 bg-slate-950 px-4 py-2.5 text-sm text-white outline-none focus:border-cyan-500"
                    >
                        <option value="All">All Platforms</option>
                        {platforms.map((item) => (
                            <option key={item} value={item}>
                                {item}
                            </option>
                        ))}
                    </select>

                    <button
                        type="button"
                        onClick={openAdd}
                        className="rounded-lg bg-cyan-500 px-5 py-2.5 text-sm font-semibold text-slate-950 transition hover:bg-cyan-400"
                    >
                        + Add Problem
                    </button>
                </div>
            </div>

            <div className="overflow-hidden rounded-xl border border-slate-800 bg-slate-900">
                <div className="overflow-x-auto">
                    <table className="w-full min-w-[850px] text-left text-sm">
                        <thead className="border-b border-slate-800 bg-slate-900 text-slate-500">
                            <tr>
                                <th className="px-6 py-4 font-medium">Problem</th>
                                <th className="px-6 py-4 font-medium">Platform</th>
                                <th className="px-6 py-4 font-medium">Difficulty</th>
                                <th className="px-6 py-4 font-medium">Topic</th>
                                <th className="px-6 py-4 font-medium">Status</th>
                                <th className="px-6 py-4 text-center font-medium">
                                    Actions
                                </th>
                            </tr>
                        </thead>

                        <tbody>
                            {filteredProblems.map((problem) => (
                                <tr
                                    key={problem.id}
                                    className="border-b border-slate-800 transition hover:bg-slate-800/40"
                                >
                                    <td className="px-6 py-4">
                                        <div className="flex items-center gap-2">
                                            {problem.favorite && (
                                                <span
                                                    className="text-yellow-400"
                                                    title="Favorite"
                                                >
                                                    ★
                                                </span>
                                            )}

                                            <div>
                                                <p className="font-medium text-white">
                                                    {problem.title}
                                                </p>

                                                {problem.url && (
                                                    <a
                                                        href={problem.url}
                                                        target="_blank"
                                                        rel="noreferrer"
                                                        className="mt-1 block text-xs text-cyan-400 hover:text-cyan-300"
                                                    >
                                                        Open problem ↗
                                                    </a>
                                                )}
                                            </div>
                                        </div>
                                    </td>

                                    <td className="px-6 py-4 text-slate-400">
                                        {problem.platform}
                                    </td>

                                    <td className="px-6 py-4">
                                        <DifficultyBadge
                                            difficulty={problem.difficulty}
                                        />
                                    </td>

                                    <td className="px-6 py-4 text-slate-400">
                                        {problem.topic}
                                    </td>

                                    <td className="px-6 py-4">
                                        <StatusBadge status={problem.status} />
                                    </td>

                                    <td className="px-6 py-4">
                                        <div className="flex items-center justify-center gap-2">
                                            <button
                                                type="button"
                                                onClick={() => openEdit(problem)}
                                                title="Edit problem"
                                                className="rounded-md px-2.5 py-1.5 text-sm text-cyan-400 transition hover:bg-cyan-500/10"
                                            >
                                                ✏️
                                            </button>

                                            <button
                                                type="button"
                                                onClick={() =>
                                                    handleDeleteProblem(problem.id)
                                                }
                                                title="Delete problem"
                                                className="rounded-md px-2.5 py-1.5 text-sm text-red-400 transition hover:bg-red-500/10"
                                            >
                                                🗑️
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>

                {filteredProblems.length === 0 && (
                    <div className="p-12 text-center">
                        <p className="text-slate-500">No problems found.</p>
                        <p className="mt-2 text-sm text-slate-600">
                            {problems.length === 0
                                ? "Add your first competitive programming problem."
                                : "Try changing your search or filters."}
                        </p>
                    </div>
                )}
            </div>

            {showModal && (
                <AddProblemModal
                    onClose={closeModals}
                    onAdd={handleSaveProblem}
                    loading={saving}
                />
            )}

            {editingProblem && (
                <AddProblemModal
                    initialProblem={editingProblem}
                    onClose={closeModals}
                    onAdd={handleSaveProblem}
                    loading={saving}
                />
            )}
        </div>
    )
}

function PageHeader() {
    return (
        <div>
            <h1 className="text-3xl font-bold text-white">Problems</h1>
            <p className="mt-2 text-sm text-slate-400">
                Track and organize your competitive programming problems.
            </p>
        </div>
    )
}

function SummaryCard({ title, value }) {
    return (
        <div className="rounded-xl border border-slate-800 bg-slate-900 p-5">
            <p className="text-sm text-slate-500">{title}</p>
            <p className="mt-2 text-2xl font-bold text-white">{value}</p>
        </div>
    )
}

function DifficultyBadge({ difficulty }) {
    const styles = {
        Easy: "bg-emerald-500/10 text-emerald-400",
        Medium: "bg-yellow-500/10 text-yellow-400",
        Hard: "bg-red-500/10 text-red-400",
    }

    return (
        <span
            className={`rounded-md px-2.5 py-1 text-xs font-medium ${
                styles[difficulty] || "bg-slate-500/10 text-slate-400"
            }`}
        >
            {difficulty}
        </span>
    )
}

function StatusBadge({ status }) {
    const styles = {
        Solved: "bg-cyan-500/10 text-cyan-400",
        Attempted: "bg-orange-500/10 text-orange-400",
        Unsolved: "bg-slate-500/10 text-slate-400",
    }

    return (
        <span
            className={`rounded-md px-2.5 py-1 text-xs ${
                styles[status] || styles.Unsolved
            }`}
        >
            {status}
        </span>
    )
}

export default Problems
