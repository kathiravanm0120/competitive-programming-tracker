import { useEffect, useMemo, useState } from "react"
import { getGoals } from "../services/goalsService"
import {
    createPlannerTask,
    deletePlannerTask,
    getPlannerTasks,
    updatePlannerTask,
} from "../services/plannerService"

function formatDate(date) {
    return date.toISOString().slice(0, 10)
}

function shiftDate(value, days) {
    const date = new Date(`${value}T00:00:00`)
    date.setDate(date.getDate() + days)
    return formatDate(date)
}

function prettyDate(value) {
    return new Date(`${value}T00:00:00`).toLocaleDateString(undefined, {
        weekday: "long",
        day: "numeric",
        month: "short",
        year: "numeric",
    })
}

const initialForm = (date) => ({
    title: "",
    description: "",
    taskDate: date,
    priority: "MEDIUM",
    completed: false,
    goalId: "",
})

function DailyPlanner() {
    const today = formatDate(new Date())
    const [selectedDate, setSelectedDate] = useState(today)
    const [tasks, setTasks] = useState([])
    const [goals, setGoals] = useState([])
    const [showForm, setShowForm] = useState(false)
    const [editingTask, setEditingTask] = useState(null)
    const [form, setForm] = useState(initialForm(today))
    const [loading, setLoading] = useState(true)
    const [saving, setSaving] = useState(false)
    const [error, setError] = useState("")

    const loadTasks = async () => {
        setLoading(true)
        setError("")
        try {
            const data = await getPlannerTasks(selectedDate)
            setTasks(Array.isArray(data) ? data : [])
        } catch (err) {
            setError(err.message || "Failed to load planner tasks")
        } finally {
            setLoading(false)
        }
    }

    const loadGoals = async () => {
        try {
            const data = await getGoals()
            setGoals(Array.isArray(data) ? data : [])
        } catch {
            // Planner can still work without goal suggestions.
        }
    }

    useEffect(() => {
        loadTasks()
    }, [selectedDate])

    useEffect(() => {
        loadGoals()
    }, [])

    const completedCount = tasks.filter((task) => task.completed).length
    const progress = tasks.length
        ? Math.round((completedCount / tasks.length) * 100)
        : 0

    const sortedTasks = useMemo(
        () => [...tasks].sort((a, b) => {
            const priority = { HIGH: 0, MEDIUM: 1, LOW: 2 }
            if (a.completed !== b.completed) return a.completed ? 1 : -1
            return (priority[a.priority] ?? 1) - (priority[b.priority] ?? 1)
        }),
        [tasks]
    )

    const openCreateForm = () => {
        setEditingTask(null)
        setForm(initialForm(selectedDate))
        setError("")
        setShowForm(true)
    }

    const openEditForm = (task) => {
        setEditingTask(task)
        setForm({
            title: task.title || "",
            description: task.description || "",
            taskDate: task.taskDate || selectedDate,
            priority: task.priority || "MEDIUM",
            completed: task.completed || false,
            goalId: task.goalId ? String(task.goalId) : "",
        })
        setError("")
        setShowForm(true)
    }

    const handleChange = (event) => {
        const { name, value, type, checked } = event.target
        setForm((current) => ({
            ...current,
            [name]: type === "checkbox" ? checked : value,
        }))
    }

    const handleSubmit = async (event) => {
        event.preventDefault()
        setSaving(true)
        setError("")

        const payload = {
            ...form,
            goalId: form.goalId ? Number(form.goalId) : null,
        }

        try {
            if (editingTask) {
                await updatePlannerTask(editingTask.id, payload)
            } else {
                await createPlannerTask(payload)
            }

            setShowForm(false)
            setEditingTask(null)
            await loadTasks()
        } catch (err) {
            setError(err.message || "Failed to save task")
        } finally {
            setSaving(false)
        }
    }

    const toggleTask = async (task) => {
        try {
            const updated = await updatePlannerTask(task.id, {
                title: task.title,
                description: task.description || "",
                taskDate: task.taskDate,
                priority: task.priority,
                completed: !task.completed,
                goalId: task.goalId || null,
            })

            setTasks((current) =>
                current.map((item) =>
                    item.id === updated.id ? updated : item
                )
            )
        } catch (err) {
            setError(err.message || "Failed to update task")
        }
    }

    const handleDelete = async (id) => {
        if (!window.confirm("Delete this planner task?")) return

        try {
            await deletePlannerTask(id)
            setTasks((current) => current.filter((task) => task.id !== id))
        } catch (err) {
            setError(err.message || "Failed to delete task")
        }
    }

    return (
        <div className="space-y-6">
            <div className="flex flex-col gap-4 lg:flex-row lg:items-end lg:justify-between">
                <div>
                    <p className="text-sm font-medium text-cyan-400">Step 9</p>
                    <h1 className="mt-1 text-3xl font-bold text-white">Daily Planner</h1>
                    <p className="mt-2 text-sm text-slate-400">
                        Turn your goals into focused daily tasks.
                    </p>
                </div>

                <button
                    onClick={openCreateForm}
                    className="rounded-lg bg-cyan-500 px-5 py-2.5 text-sm font-semibold text-slate-950 hover:bg-cyan-400"
                >
                    + Add Task
                </button>
            </div>

            {error && (
                <div className="rounded-xl border border-rose-500/20 bg-rose-500/10 p-4 text-sm text-rose-300">
                    {error}
                </div>
            )}

            <div className="rounded-2xl border border-slate-800 bg-slate-900 p-5">
                <div className="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
                    <div className="flex items-center gap-2">
                        <button
                            onClick={() => setSelectedDate(shiftDate(selectedDate, -1))}
                            className="rounded-lg border border-slate-800 px-3 py-2 text-slate-300 hover:text-white"
                        >
                            ←
                        </button>
                        <input
                            type="date"
                            value={selectedDate}
                            onChange={(event) => setSelectedDate(event.target.value)}
                            className="input w-auto"
                        />
                        <button
                            onClick={() => setSelectedDate(shiftDate(selectedDate, 1))}
                            className="rounded-lg border border-slate-800 px-3 py-2 text-slate-300 hover:text-white"
                        >
                            →
                        </button>
                        <button
                            onClick={() => setSelectedDate(today)}
                            className="rounded-lg border border-slate-800 px-3 py-2 text-xs font-semibold text-slate-300 hover:text-white"
                        >
                            Today
                        </button>
                    </div>

                    <div className="min-w-[240px]">
                        <div className="flex items-center justify-between text-xs">
                            <span className="text-slate-500">Daily progress</span>
                            <span className="font-semibold text-cyan-300">{progress}%</span>
                        </div>
                        <div className="mt-2 h-2 overflow-hidden rounded-full bg-slate-800">
                            <div className="h-full rounded-full bg-cyan-400" style={{ width: `${progress}%` }} />
                        </div>
                    </div>
                </div>

                <div className="mt-4">
                    <h2 className="text-lg font-semibold text-white">{prettyDate(selectedDate)}</h2>
                    <p className="mt-1 text-sm text-slate-500">
                        {completedCount} of {tasks.length} tasks completed
                    </p>
                </div>
            </div>

            {loading ? (
                <div className="rounded-2xl border border-slate-800 bg-slate-900 p-10 text-center text-sm text-slate-500">
                    Loading planner...
                </div>
            ) : sortedTasks.length === 0 ? (
                <div className="rounded-2xl border border-dashed border-slate-800 bg-slate-900 p-10 text-center">
                    <h2 className="text-lg font-semibold text-white">No tasks planned</h2>
                    <p className="mt-2 text-sm text-slate-500">Add a few focused tasks for this day.</p>
                    <button onClick={openCreateForm} className="mt-5 rounded-lg bg-cyan-500 px-5 py-2.5 text-sm font-semibold text-slate-950 hover:bg-cyan-400">
                        Add Task
                    </button>
                </div>
            ) : (
                <div className="space-y-3">
                    {sortedTasks.map((task) => (
                        <TaskCard
                            key={task.id}
                            task={task}
                            onToggle={toggleTask}
                            onEdit={openEditForm}
                            onDelete={handleDelete}
                        />
                    ))}
                </div>
            )}

            {showForm && (
                <TaskFormModal
                    form={form}
                    goals={goals}
                    editing={Boolean(editingTask)}
                    saving={saving}
                    onChange={handleChange}
                    onSubmit={handleSubmit}
                    onClose={() => setShowForm(false)}
                />
            )}
        </div>
    )
}

function TaskCard({ task, onToggle, onEdit, onDelete }) {
    const priorityClass = {
        HIGH: "bg-rose-500/10 text-rose-300",
        MEDIUM: "bg-amber-500/10 text-amber-300",
        LOW: "bg-emerald-500/10 text-emerald-300",
    }[task.priority] || "bg-slate-800 text-slate-400"

    return (
        <article className={`rounded-2xl border border-slate-800 bg-slate-900 p-4 ${task.completed ? "opacity-70" : ""}`}>
            <div className="flex items-start gap-4">
                <button
                    onClick={() => onToggle(task)}
                    className={`mt-1 flex h-6 w-6 shrink-0 items-center justify-center rounded-full border ${
                        task.completed
                            ? "border-emerald-400 bg-emerald-400 text-slate-950"
                            : "border-slate-700 text-transparent hover:border-cyan-400"
                    }`}
                    aria-label={task.completed ? "Mark incomplete" : "Mark complete"}
                >
                    ✓
                </button>

                <div className="min-w-0 flex-1">
                    <div className="flex flex-wrap items-center gap-2">
                        <h2 className={`text-sm font-semibold ${task.completed ? "text-slate-500 line-through" : "text-white"}`}>
                            {task.title}
                        </h2>
                        <span className={`rounded-full px-2.5 py-1 text-[10px] font-semibold ${priorityClass}`}>
                            {task.priority}
                        </span>
                    </div>

                    {task.description && (
                        <p className="mt-1 text-sm text-slate-500">{task.description}</p>
                    )}

                    {task.goalTitle && (
                        <p className="mt-2 text-xs text-slate-600">Goal: {task.goalTitle}</p>
                    )}
                </div>

                <div className="flex shrink-0 gap-2">
                    <button onClick={() => onEdit(task)} className="rounded-lg border border-slate-800 px-3 py-2 text-xs text-slate-400 hover:text-white">
                        Edit
                    </button>
                    <button onClick={() => onDelete(task.id)} className="rounded-lg border border-rose-500/20 px-3 py-2 text-xs text-rose-300 hover:bg-rose-500/10">
                        Delete
                    </button>
                </div>
            </div>
        </article>
    )
}

function TaskFormModal({ form, goals, editing, saving, onChange, onSubmit, onClose }) {
    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 p-4 backdrop-blur-sm">
            <div className="w-full max-w-2xl rounded-2xl border border-slate-700 bg-slate-900 shadow-2xl">
                <div className="flex items-center justify-between border-b border-slate-800 px-6 py-5">
                    <div>
                        <h2 className="text-xl font-semibold text-white">{editing ? "Edit Task" : "Add Planner Task"}</h2>
                        <p className="mt-1 text-sm text-slate-500">Keep the task concrete and achievable.</p>
                    </div>
                    <button onClick={onClose} className="text-2xl text-slate-500 hover:text-white">×</button>
                </div>

                <form onSubmit={onSubmit} className="grid gap-5 p-6 md:grid-cols-2">
                    <label className="space-y-2 md:col-span-2">
                        <span className="text-sm font-medium text-slate-300">Task title</span>
                        <input name="title" value={form.title} onChange={onChange} required className="input" placeholder="Solve 3 binary search problems" />
                    </label>

                    <label className="space-y-2 md:col-span-2">
                        <span className="text-sm font-medium text-slate-300">Description</span>
                        <textarea name="description" value={form.description} onChange={onChange} className="input min-h-24" placeholder="Optional details" />
                    </label>

                    <label className="space-y-2">
                        <span className="text-sm font-medium text-slate-300">Date</span>
                        <input type="date" name="taskDate" value={form.taskDate} onChange={onChange} required className="input" />
                    </label>

                    <label className="space-y-2">
                        <span className="text-sm font-medium text-slate-300">Priority</span>
                        <select name="priority" value={form.priority} onChange={onChange} className="input">
                            <option value="HIGH">High</option>
                            <option value="MEDIUM">Medium</option>
                            <option value="LOW">Low</option>
                        </select>
                    </label>

                    <label className="space-y-2 md:col-span-2">
                        <span className="text-sm font-medium text-slate-300">Link to goal (optional)</span>
                        <select name="goalId" value={form.goalId} onChange={onChange} className="input">
                            <option value="">No linked goal</option>
                            {goals.map((goal) => (
                                <option key={goal.id} value={goal.id}>{goal.title}</option>
                            ))}
                        </select>
                    </label>

                    {editing && (
                        <label className="flex items-center gap-3 md:col-span-2">
                            <input type="checkbox" name="completed" checked={form.completed} onChange={onChange} className="h-4 w-4" />
                            <span className="text-sm text-slate-300">Mark as completed</span>
                        </label>
                    )}

                    <div className="flex justify-end gap-3 md:col-span-2">
                        <button type="button" onClick={onClose} className="rounded-lg border border-slate-700 px-5 py-2.5 text-sm text-slate-300 hover:text-white">Cancel</button>
                        <button disabled={saving} className="rounded-lg bg-cyan-500 px-5 py-2.5 text-sm font-semibold text-slate-950 hover:bg-cyan-400 disabled:opacity-60">
                            {saving ? "Saving..." : editing ? "Save Changes" : "Add Task"}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    )
}

export default DailyPlanner
