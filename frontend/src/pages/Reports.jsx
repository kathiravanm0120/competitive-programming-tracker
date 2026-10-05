import { useEffect, useState } from "react"
import {
    downloadAchievementsCsv,
    downloadGoalsCsv,
    downloadPdfReport,
    downloadPlannerCsv,
    downloadPlatformsCsv,
    downloadProblemsCsv,
    getReportSummary,
} from "../services/reportsService"

function Reports() {
    const [summary, setSummary] = useState(null)
    const [loading, setLoading] = useState(true)
    const [downloading, setDownloading] = useState("")
    const [error, setError] = useState("")

    useEffect(() => {
        loadSummary()
    }, [])

    async function loadSummary() {
        setLoading(true)
        setError("")
        try {
            setSummary(await getReportSummary())
        } catch (err) {
            setError(err.message || "Unable to load report summary.")
        } finally {
            setLoading(false)
        }
    }

    async function handleDownload(key, downloader) {
        setDownloading(key)
        setError("")
        try {
            await downloader()
        } catch (err) {
            setError(err.message || "Unable to download the report.")
        } finally {
            setDownloading("")
        }
    }

    const platforms = summary?.platforms || []
    const generatedAt = summary?.generatedAt
        ? new Date(summary.generatedAt).toLocaleString()
        : "-"

    return (
        <div className="space-y-8">
            <header>
                <p className="text-xs font-semibold uppercase tracking-[0.24em] text-cyan-400">
                    Reports & Export
                </p>
                <h1 className="mt-2 text-3xl font-bold text-white">Progress Reports</h1>
                <p className="mt-2 text-sm text-slate-400">
                    Generate a complete progress report or export your tracker data as CSV files.
                </p>
            </header>

            {error && (
                <div className="rounded-xl border border-red-500/30 bg-red-500/10 p-4 text-sm text-red-300">
                    {error}
                </div>
            )}

            <section className="grid gap-5 sm:grid-cols-2 xl:grid-cols-4">
                <MetricCard title="Tracked Solved" value={summary?.trackedSolvedProblems ?? "-"} />
                <MetricCard title="Platform Solved" value={summary?.totalPlatformSolved ?? "-"} />
                <MetricCard title="Contests" value={summary?.totalContests ?? "-"} />
                <MetricCard title="Achievements" value={`${summary?.achievementsUnlocked ?? "-"}/${summary?.achievementsTotal ?? "-"}`} />
            </section>

            <section className="rounded-2xl border border-slate-800 bg-slate-900 p-6">
                <div className="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
                    <div>
                        <h2 className="text-lg font-semibold text-white">PDF Progress Report</h2>
                        <p className="mt-1 text-sm text-slate-500">
                            Includes overall statistics, all connected platforms, goals, planner progress,
                            achievements, tracked problems, and recent Codeforces submissions.
                        </p>
                        <p className="mt-2 text-xs text-slate-600">
                            Latest summary generated: {loading ? "Loading..." : generatedAt}
                        </p>
                    </div>

                    <button
                        type="button"
                        onClick={() => handleDownload("pdf", downloadPdfReport)}
                        disabled={downloading === "pdf" || loading}
                        className="rounded-lg bg-cyan-500 px-5 py-3 text-sm font-semibold text-slate-950 transition hover:bg-cyan-400 disabled:cursor-not-allowed disabled:opacity-50"
                    >
                        {downloading === "pdf" ? "Generating PDF..." : "Generate PDF Report"}
                    </button>
                </div>
            </section>

            <section className="rounded-2xl border border-slate-800 bg-slate-900 p-6">
                <div>
                    <h2 className="text-lg font-semibold text-white">CSV Exports</h2>
                    <p className="mt-1 text-sm text-slate-500">
                        Export individual parts of your tracker for Excel, Google Sheets, analysis, or backups.
                    </p>
                </div>

                <div className="mt-6 grid gap-4 sm:grid-cols-2 xl:grid-cols-3">
                    <ExportCard
                        title="Problems"
                        description="All manually tracked problems."
                        action="problems"
                        downloading={downloading}
                        onClick={() => handleDownload("problems", downloadProblemsCsv)}
                    />
                    <ExportCard
                        title="Goals"
                        description="Goal targets, progress, dates, and status."
                        action="goals"
                        downloading={downloading}
                        onClick={() => handleDownload("goals", downloadGoalsCsv)}
                    />
                    <ExportCard
                        title="Daily Planner"
                        description="Planner tasks, priorities, dates, and completion."
                        action="planner"
                        downloading={downloading}
                        onClick={() => handleDownload("planner", downloadPlannerCsv)}
                    />
                    <ExportCard
                        title="Platform Statistics"
                        description="Normalized statistics from all connected platforms."
                        action="platforms"
                        downloading={downloading}
                        onClick={() => handleDownload("platforms", downloadPlatformsCsv)}
                    />
                    <ExportCard
                        title="Achievements"
                        description="Achievement progress and unlock status."
                        action="achievements"
                        downloading={downloading}
                        onClick={() => handleDownload("achievements", downloadAchievementsCsv)}
                    />
                </div>
            </section>

            <section className="rounded-2xl border border-slate-800 bg-slate-900 p-6">
                <div>
                    <h2 className="text-lg font-semibold text-white">Current Report Snapshot</h2>
                    <p className="mt-1 text-sm text-slate-500">The same normalized platform data used by the report generator.</p>
                </div>

                <div className="mt-6 overflow-x-auto">
                    <table className="w-full text-left text-sm">
                        <thead className="border-b border-slate-800 text-xs uppercase tracking-wide text-slate-500">
                            <tr>
                                <th className="px-4 py-3">Platform</th>
                                <th className="px-4 py-3">Handle</th>
                                <th className="px-4 py-3">Rating</th>
                                <th className="px-4 py-3">Solved</th>
                                <th className="px-4 py-3">Contests</th>
                                <th className="px-4 py-3">Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            {platforms.map((platform) => (
                                <tr key={platform.platform} className="border-b border-slate-800/70">
                                    <td className="px-4 py-4 font-semibold text-white">{platform.platform}</td>
                                    <td className="px-4 py-4 text-slate-400">{platform.handle || "-"}</td>
                                    <td className="px-4 py-4 text-slate-300">{platform.rating ?? "-"}</td>
                                    <td className="px-4 py-4 text-slate-300">{platform.solved ?? "-"}</td>
                                    <td className="px-4 py-4 text-slate-300">{platform.contests ?? "-"}</td>
                                    <td className="px-4 py-4">
                                        <span className={platform.available ? "rounded-full bg-emerald-500/10 px-2.5 py-1 text-xs font-semibold text-emerald-400" : platform.connected ? "rounded-full bg-amber-500/10 px-2.5 py-1 text-xs font-semibold text-amber-300" : "rounded-full bg-slate-800 px-2.5 py-1 text-xs font-semibold text-slate-500"}>
                                            {platform.available ? "Available" : platform.connected ? "Needs sync" : "Not connected"}
                                        </span>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </section>
        </div>
    )
}

function MetricCard({ title, value }) {
    return (
        <div className="rounded-2xl border border-slate-800 bg-slate-900 p-5">
            <p className="text-sm text-slate-500">{title}</p>
            <p className="mt-3 text-3xl font-bold text-white">
                {value}
            </p>
        </div>
    )
}

function ExportCard({ title, description, action, downloading, onClick }) {
    const active = downloading === action

    return (
        <div className="rounded-xl border border-slate-800 bg-slate-950 p-5">
            <h3 className="font-semibold text-white">{title}</h3>
            <p className="mt-2 min-h-10 text-sm text-slate-500">{description}</p>
            <button
                type="button"
                onClick={onClick}
                disabled={Boolean(downloading)}
                className="mt-5 w-full rounded-lg border border-slate-700 px-4 py-2.5 text-sm font-semibold text-slate-200 transition hover:border-cyan-500/50 hover:text-cyan-300 disabled:cursor-not-allowed disabled:opacity-50"
            >
                {active ? "Exporting..." : `Export ${title} CSV`}
            </button>
        </div>
    )
}

export default Reports
