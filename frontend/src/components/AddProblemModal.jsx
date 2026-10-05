import { useEffect, useState } from "react"

const EMPTY_FORM = {
    title: "",
    platform: "Codeforces",
    url: "",
    difficulty: "Easy",
    topic: "Arrays",
    status: "Solved",
    notes: "",
    favorite: false,
}

function AddProblemModal({
    onClose,
    onAdd,
    initialProblem = null,
    loading = false,
}) {
    const [form, setForm] = useState(EMPTY_FORM)

    useEffect(() => {
        if (!initialProblem) {
            setForm(EMPTY_FORM)
            return
        }

        setForm({
            title: initialProblem.title || "",
            platform: initialProblem.platform || "Codeforces",
            url: initialProblem.url || "",
            difficulty: initialProblem.difficulty || "Easy",
            topic: initialProblem.topic || "Arrays",
            status: initialProblem.status || "Solved",
            notes: initialProblem.notes || "",
            favorite: Boolean(initialProblem.favorite),
        })
    }, [initialProblem])

    const handleChange = (event) => {
        const {
            name,
            value,
            type,
            checked,
        } = event.target

        setForm((current) => ({
            ...current,
            [name]: type === "checkbox" ? checked : value,
        }))
    }

    const handleSubmit = (event) => {
        event.preventDefault()

        if (!form.title.trim()) {
            alert("Please enter a problem title.")
            return
        }

        onAdd({
            ...form,
            title: form.title.trim(),
            url: form.url.trim(),
            notes: form.notes.trim(),
        })
    }

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 p-4 backdrop-blur-sm">
            <div className="max-h-[90vh] w-full max-w-2xl overflow-y-auto rounded-2xl border border-slate-700 bg-slate-900 shadow-2xl">
                <div className="flex items-center justify-between border-b border-slate-800 px-6 py-5">
                    <div>
                        <h2 className="text-xl font-semibold text-white">
                            {initialProblem ? "Edit Problem" : "Add Problem"}
                        </h2>

                        <p className="mt-1 text-sm text-slate-500">
                            {initialProblem
                                ? "Update the problem details."
                                : "Add a problem to your competitive programming tracker."}
                        </p>
                    </div>

                    <button
                        type="button"
                        onClick={onClose}
                        disabled={loading}
                        className="text-2xl text-slate-500 transition hover:text-white disabled:opacity-50"
                    >
                        ×
                    </button>
                </div>

                <form onSubmit={handleSubmit} className="space-y-5 p-6">
                    <Field
                        label="Problem Title"
                        name="title"
                        value={form.title}
                        onChange={handleChange}
                        placeholder="e.g. Two Sum"
                    />

                    <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                        <SelectField
                            label="Platform"
                            name="platform"
                            value={form.platform}
                            onChange={handleChange}
                            options={[
                                "Codeforces",
                                "LeetCode",
                                "CodeChef",
                                "AtCoder",
                                "Other",
                            ]}
                        />

                        <SelectField
                            label="Difficulty"
                            name="difficulty"
                            value={form.difficulty}
                            onChange={handleChange}
                            options={["Easy", "Medium", "Hard"]}
                        />
                    </div>

                    <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                        <Field
                            label="Problem URL"
                            name="url"
                            type="url"
                            value={form.url}
                            onChange={handleChange}
                            placeholder="https://..."
                        />

                        <SelectField
                            label="Topic"
                            name="topic"
                            value={form.topic}
                            onChange={handleChange}
                            options={[
                                "Arrays",
                                "Strings",
                                "Binary Search",
                                "Greedy",
                                "Dynamic Programming",
                                "Graphs",
                                "Trees",
                                "Data Structures",
                                "Number Theory",
                                "Math",
                                "Bit Manipulation",
                                "Geometry",
                            ]}
                        />
                    </div>

                    <SelectField
                        label="Status"
                        name="status"
                        value={form.status}
                        onChange={handleChange}
                        options={["Solved", "Attempted", "Unsolved"]}
                    />

                    <div>
                        <label className="mb-2 block text-sm font-medium text-slate-300">
                            Notes
                        </label>

                        <textarea
                            name="notes"
                            value={form.notes}
                            onChange={handleChange}
                            rows="4"
                            placeholder="Write your approach, important observations..."
                            className="w-full resize-none rounded-lg border border-slate-700 bg-slate-950 px-4 py-2.5 text-sm text-white outline-none placeholder:text-slate-600 focus:border-cyan-500"
                        />
                    </div>

                    <label className="flex cursor-pointer items-center gap-3">
                        <input
                            type="checkbox"
                            name="favorite"
                            checked={form.favorite}
                            onChange={handleChange}
                            className="h-4 w-4 accent-cyan-500"
                        />

                        <span className="text-sm text-slate-300">
                            Add to favorites
                        </span>
                    </label>

                    <div className="flex justify-end gap-3 border-t border-slate-800 pt-5">
                        <button
                            type="button"
                            onClick={onClose}
                            disabled={loading}
                            className="rounded-lg border border-slate-700 px-5 py-2.5 text-sm text-slate-300 transition hover:bg-slate-800 disabled:opacity-50"
                        >
                            Cancel
                        </button>

                        <button
                            type="submit"
                            disabled={loading}
                            className="rounded-lg bg-cyan-500 px-5 py-2.5 text-sm font-semibold text-slate-950 transition hover:bg-cyan-400 disabled:cursor-not-allowed disabled:opacity-60"
                        >
                            {loading
                                ? "Saving..."
                                : initialProblem
                                    ? "Save Changes"
                                    : "Add Problem"}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    )
}

function Field({
    label,
    name,
    type = "text",
    value,
    onChange,
    placeholder,
}) {
    return (
        <div>
            <label className="mb-2 block text-sm font-medium text-slate-300">
                {label}
            </label>

            <input
                name={name}
                type={type}
                value={value}
                onChange={onChange}
                placeholder={placeholder}
                className="w-full rounded-lg border border-slate-700 bg-slate-950 px-4 py-2.5 text-sm text-white outline-none placeholder:text-slate-600 focus:border-cyan-500"
            />
        </div>
    )
}

function SelectField({
    label,
    name,
    value,
    onChange,
    options,
}) {
    return (
        <div>
            <label className="mb-2 block text-sm font-medium text-slate-300">
                {label}
            </label>

            <select
                name={name}
                value={value}
                onChange={onChange}
                className="w-full rounded-lg border border-slate-700 bg-slate-950 px-4 py-2.5 text-sm text-white outline-none focus:border-cyan-500"
            >
                {options.map((option) => (
                    <option key={option} value={option}>
                        {option}
                    </option>
                ))}
            </select>
        </div>
    )
}

export default AddProblemModal
