import { useEffect, useMemo, useState } from "react"
import {
    createGoal,
    deleteGoal,
    getGoals,
    updateGoal,
} from "../services/goalsService"

const initialForm = {
    title: "",
    description: "",
    goalType: "PROBLEMS",
    targetValue: 10,
    currentValue: 0,
    unit: "problems",
    startDate: new Date().toISOString().slice(0, 10),
    endDate: new Date(Date.now() + 6 * 24 * 60 * 60 * 1000)
        .toISOString()
        .slice(0, 10),
    platform: "All",
    topic: "",
    status: "ACTIVE",
}

function Goals() {
    const [goals, setGoals] = useState([])
    const [filter, setFilter] = useState("ALL")
    const [showForm, setShowForm] = useState(false)
    const [editingGoal, setEditingGoal] = useState(null)
    const [form, setForm] = useState(initialForm)
    const [loading, setLoading] = useState(true)
    const [saving, setSaving] = useState(false)
    const [error, setError] = useState("")

    const loadGoals = async () => {
        setLoading(true)
        setError("")
        try {
            const data = await getGoals()
            setGoals(Array.isArray(data) ? data : [])
        } catch (err) {
            setError(err.message || "Failed to load goals")
        } finally {
            setLoading(false)
        }
    }

    useEffect(() => {
        loadGoals()
    }, [])

    const filteredGoals = useMemo(() => {
        if (filter === "ALL") return goals
        return goals.filter((goal) => goal.status === filter)
    }, [goals, filter])

    const activeGoals = goals.filter((goal) => goal.status === "ACTIVE").length
    const completedGoals = goals.filter((goal) => goal.status === "COMPLETED").length
    const overdueGoals = goals.filter((goal) => goal.status === "OVERDUE").length

    const openCreateForm = () => {
        setEditingGoal(null)
        setForm(initialForm)
        setError("")
        setShowForm(true)
    }

    const openEditForm = (goal) => {
        setEditingGoal(goal)
        setForm({
            title: goal.title || "",
            description: goal.description || "",
            goalType: goal.goalType || "CUSTOM",
            targetValue: goal.targetValue ?? 1,
            currentValue: goal.currentValue ?? 0,
            unit: goal.unit || "",
            startDate: goal.startDate || initialForm.startDate,
            endDate: goal.endDate || initialForm.endDate,
            platform: goal.platform || "All",
            topic: goal.topic || "",
            status: ["ACTIVE", "PAUSED", "CANCELLED"].includes(goal.status)
                ? goal.status
                : "ACTIVE",
        })
        setError("")
        setShowForm(true)
    }

    const handleChange = (event) => {
        const { name, value } = event.target
        setForm((current) => ({
            ...current,
            [name]: ["targetValue", "currentValue"].includes(name)
                ? Number(value)
                : value,
        }))
    }

    const handleSubmit = async (event) => {
        event.preventDefault()
        setSaving(true)
        setError("")

        try {
            if (editingGoal) {
                const updated = await updateGoal(editingGoal.id, form)
                setGoals((current) =>
                    current.map((goal) =>
                        goal.id === updated.id ? updated : goal
                    )
                )
            } else {
                const created = await createGoal(form)
                setGoals((current) => [...current, created])
            }

            setShowForm(false)
            setEditingGoal(null)
        } catch (err) {
            setError(err.message || "Failed to save goal")
        } finally {
            setSaving(false)
        }
    }

    const handleDelete = async (id) => {
        if (!window.confirm("Delete this goal?")) return

        try {
            await deleteGoal(id)
            setGoals((current) => current.filter((goal) => goal.id !== id))
        } catch (err) {
            setError(err.message || "Failed to delete goal")
        }
    }

    return (
        <div className="space-y-6">
            <div className="flex flex-col gap-4 md:flex-row md:items-end md:justify-between">
                <div>
                    <p className="text-sm font-medium text-cyan-400">Step 9</p>
                    <h1 className="mt-1 text-3xl font-bold text-white">Goals</h1>
                    <p className="mt-2 text-sm text-slate-400">
                        Set measurable competitive programming targets and track progress.
                    </p>
                </div>

                <button
                    onClick={openCreateForm}
                    className="rounded-lg bg-cyan-500 px-5 py-2.5 text-sm font-semibold text-slate-950 transition hover:bg-cyan-400"
                >
                    + New Goal
                </button>
            </div>

            <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
                <SummaryCard title="Total Goals" value={goals.length} />
                <SummaryCard title="Active" value={activeGoals} />
                <SummaryCard title="Completed" value={completedGoals} />
                <SummaryCard title="Overdue" value={overdueGoals} />
            </div>

            {error && (
                <div className="rounded-xl border border-rose-500/20 bg-rose-500/10 p-4 text-sm text-rose-300">
                    {error}
                </div>
            )}

            <div className="flex flex-wrap gap-2">
                {["ALL", "ACTIVE", "COMPLETED", "OVERDUE", "PAUSED", "CANCELLED"].map(
                    (status) => (
                        <button
                            key={status}
                            onClick={() => setFilter(status)}
                            className={`rounded-full px-4 py-2 text-xs font-semibold transition ${
                                filter === status
                                    ? "bg-cyan-500 text-slate-950"
                                    : "border border-slate-800 bg-slate-900 text-slate-400 hover:text-white"
                            }`}
                        >
                            {status === "ALL" ? "All" : status}
                        </button>
                    )
                )}
            </div>

            {loading ? (
                <LoadingState />
            ) : filteredGoals.length === 0 ? (
                <EmptyState onCreate={openCreateForm} />
            ) : (
                <div className="grid gap-4 xl:grid-cols-2">
                    {filteredGoals.map((goal) => (
                        <GoalCard
                            key={goal.id}
                            goal={goal}
                            onEdit={openEditForm}
                            onDelete={handleDelete}
                        />
                    ))}
                </div>
            )}

            {showForm && (
                <GoalFormModal
                    form={form}
                    editing={Boolean(editingGoal)}
                    saving={saving}
                    onChange={handleChange}
                    onSubmit={handleSubmit}
                    onClose={() => setShowForm(false)}
                />
            )}
        </div>
    )
}

function SummaryCard({ title, value }) {
    return (
        <div className="rounded-2xl border border-slate-800 bg-slate-900 p-5">
            <p className="text-sm text-slate-500">{title}</p>
            <p className="mt-2 text-3xl font-bold text-white">{value}</p>
        </div>
    )
}

function GoalCard({ goal, onEdit, onDelete }) {
    const statusClass = {
        ACTIVE: "bg-cyan-500/10 text-cyan-300",
        COMPLETED: "bg-emerald-500/10 text-emerald-300",
        OVERDUE: "bg-rose-500/10 text-rose-300",
        PAUSED: "bg-amber-500/10 text-amber-300",
        CANCELLED: "bg-slate-800 text-slate-400",
    }[goal.status] || "bg-slate-800 text-slate-400"

    return (
        <article className="rounded-2xl border border-slate-800 bg-slate-900 p-5">
            <div className="flex items-start justify-between gap-4">
                <div className="min-w-0">
                    <h2 className="truncate text-lg font-semibold text-white">{goal.title}</h2>
                    <div className="mt-2 flex flex-wrap gap-2 text-[11px] text-slate-500">
                        <span className="rounded-full border border-slate-800 px-2.5 py-1">
                            {goal.goalType}
                        </span>
                        {goal.platform && goal.platform !== "All" && (
                            <span className="rounded-full border border-slate-800 px-2.5 py-1">
                                {goal.platform}
                            </span>
                        )}
                        {goal.topic && (
                            <span className="rounded-full border border-slate-800 px-2.5 py-1">
                                {goal.topic}
                            </span>
                        )}
                    </div>
                </div>

                <span className={`shrink-0 rounded-full px-2.5 py-1 text-[11px] font-semibold ${statusClass}`}>
                    {goal.status}
                </span>
            </div>

            {goal.description && (
                <p className="mt-4 text-sm leading-6 text-slate-400">{goal.description}</p>
            )}

            <div className="mt-5">
                <div className="mb-2 flex items-center justify-between text-xs">
                    <span className="text-slate-500">
                        {goal.currentValue} / {goal.targetValue} {goal.unit || ""}
                    </span>
                    <span className="font-semibold text-cyan-300">{goal.progressPercent}%</span>
                </div>
                <div className="h-2 overflow-hidden rounded-full bg-slate-800">
                    <div
                        className="h-full rounded-full bg-cyan-400 transition-all"
                        style={{ width: `${goal.progressPercent}%` }}
                    />
                </div>
            </div>

            <div className="mt-5 grid grid-cols-2 gap-3 text-xs">
                <Info label="Start" value={goal.startDate} />
                <Info label="Deadline" value={goal.endDate} />
            </div>

            <div className="mt-5 flex justify-end gap-2">
                <button
                    onClick={() => onEdit(goal)}
                    className="rounded-lg border border-slate-700 px-4 py-2 text-xs font-semibold text-slate-300 hover:text-white"
                >
                    Edit
                </button>
                <button
                    onClick={() => onDelete(goal.id)}
                    className="rounded-lg border border-rose-500/20 px-4 py-2 text-xs font-semibold text-rose-300 hover:bg-rose-500/10"
                >
                    Delete
                </button>
            </div>
        </article>
    )
}

function Info({ label, value }) {
    return (
        <div className="rounded-xl border border-slate-800 bg-slate-950 p-3">
            <p className="text-[10px] uppercase tracking-wider text-slate-600">{label}</p>
            <p className="mt-1 text-slate-300">{value}</p>
        </div>
    )
}

function GoalFormModal({ form, editing, saving, onChange, onSubmit, onClose }) {
    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 p-4 backdrop-blur-sm">
            <div className="max-h-[92vh] w-full max-w-3xl overflow-y-auto rounded-2xl border border-slate-700 bg-slate-900 shadow-2xl">
                <div className="flex items-center justify-between border-b border-slate-800 px-6 py-5">
                    <div>
                        <h2 className="text-xl font-semibold text-white">
                            {editing ? "Edit Goal" : "Create Goal"}
                        </h2>
                        <p className="mt-1 text-sm text-slate-500">
                            Make the target measurable so progress can be tracked.
                        </p>
                    </div>
                    <button onClick={onClose} className="text-2xl text-slate-500 hover:text-white">×</button>
                </div>

                <form onSubmit={onSubmit} className="grid gap-5 p-6 md:grid-cols-2">
                    <Field label="Title" className="md:col-span-2">
                        <input name="title" value={form.title} onChange={onChange} required className="input" placeholder="Solve 20 problems this week" />
                    </Field>

                    <Field label="Description" className="md:col-span-2">
                        <textarea name="description" value={form.description} onChange={onChange} className="input min-h-24" placeholder="What do you want to achieve?" />
                    </Field>

                    <Field label="Goal Type">
                        <select name="goalType" value={form.goalType} onChange={onChange} className="input">
                            <option value="PROBLEMS">Problems</option>
                            <option value="RATING">Rating</option>
                            <option value="CONTESTS">Contests</option>
                            <option value="TOPIC">Topic Practice</option>
                            <option value="DAILY_STREAK">Daily Streak</option>
                            <option value="CUSTOM">Custom</option>
                        </select>
                    </Field>

                    <Field label="Unit">
                        <input name="unit" value={form.unit} onChange={onChange} className="input" placeholder="problems / rating / contests" />
                    </Field>

                    <Field label="Target Value">
                        <input type="number" min="1" name="targetValue" value={form.targetValue} onChange={onChange} required className="input" />
                    </Field>

                    <Field label="Current Value">
                        <input type="number" min="0" name="currentValue" value={form.currentValue} onChange={onChange} className="input" />
                    </Field>

                    <Field label="Start Date">
                        <input type="date" name="startDate" value={form.startDate} onChange={onChange} required className="input" />
                    </Field>

                    <Field label="Deadline">
                        <input type="date" name="endDate" value={form.endDate} onChange={onChange} required className="input" />
                    </Field>

                    <Field label="Platform">
                        <select name="platform" value={form.platform} onChange={onChange} className="input">
                            <option value="All">All Platforms</option>
                            <option value="Codeforces">Codeforces</option>
                            <option value="LeetCode">LeetCode</option>
                            <option value="CodeChef">CodeChef</option>
                            <option value="AtCoder">AtCoder</option>
                        </select>
                    </Field>

                    <Field label="Topic">
                        <input name="topic" value={form.topic} onChange={onChange} className="input" placeholder="Dynamic Programming" />
                    </Field>

                    <Field label="Status">
                        <select name="status" value={form.status} onChange={onChange} className="input">
                            <option value="ACTIVE">Active</option>
                            <option value="PAUSED">Paused</option>
                            <option value="CANCELLED">Cancelled</option>
                        </select>
                    </Field>

                    <div className="flex justify-end gap-3 md:col-span-2">
                        <button type="button" onClick={onClose} className="rounded-lg border border-slate-700 px-5 py-2.5 text-sm text-slate-300 hover:text-white">
                            Cancel
                        </button>
                        <button disabled={saving} className="rounded-lg bg-cyan-500 px-5 py-2.5 text-sm font-semibold text-slate-950 hover:bg-cyan-400 disabled:opacity-60">
                            {saving ? "Saving..." : editing ? "Save Changes" : "Create Goal"}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    )
}

function Field({ label, className = "", children }) {
    return (
        <label className={`block space-y-2 ${className}`}>
            <span className="text-sm font-medium text-slate-300">{label}</span>
            {children}
        </label>
    )
}

function LoadingState() {
    return <div className="rounded-2xl border border-slate-800 bg-slate-900 p-10 text-center text-sm text-slate-500">Loading goals...</div>
}

function EmptyState({ onCreate }) {
    return (
        <div className="rounded-2xl border border-dashed border-slate-800 bg-slate-900 p-10 text-center">
            <h2 className="text-lg font-semibold text-white">No goals yet</h2>
            <p className="mt-2 text-sm text-slate-500">Create your first measurable target.</p>
            <button onClick={onCreate} className="mt-5 rounded-lg bg-cyan-500 px-5 py-2.5 text-sm font-semibold text-slate-950 hover:bg-cyan-400">
                Create Goal
            </button>
        </div>
    )
}

export default Goals
