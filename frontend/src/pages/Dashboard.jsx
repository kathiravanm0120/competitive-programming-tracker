import { useEffect, useMemo, useState } from "react"
import StatCard from "../components/StatCard"
import { getDashboard } from "../services/dashboardService"
import { getProblems } from "../services/problemService"

const PLATFORM_ORDER = ["Codeforces", "LeetCode", "CodeChef", "AtCoder"]

function Dashboard() {
    const [dashboard, setDashboard] = useState(null)
    const [recentProblems, setRecentProblems] = useState([])
    const [loading, setLoading] = useState(true)
    const [refreshing, setRefreshing] = useState(false)
    const [error, setError] = useState("")

    useEffect(() => {
        loadDashboard()
    }, [])

    async function loadDashboard(isRefresh = false) {
        if (isRefresh) setRefreshing(true)
        else setLoading(true)

        setError("")

        try {
            const [dashboardData, problemsData] = await Promise.all([
                getDashboard(),
                getProblems(),
            ])

            setDashboard(dashboardData)
            setRecentProblems(
                Array.isArray(problemsData) ? problemsData : []
            )
        } catch (err) {
            setError(err.message || "Unable to load dashboard data.")
        } finally {
            setLoading(false)
            setRefreshing(false)
        }
    }

    const platforms = useMemo(() => {
        const source = Array.isArray(dashboard?.platforms)
            ? dashboard.platforms
            : []

        return PLATFORM_ORDER.map((name) =>
            source.find((item) => item.platform === name) || {
                platform: name,
                connected: false,
                available: false,
                metrics: {},
            }
        )
    }, [dashboard])

    const codeforces = dashboard?.codeforces
    const overall = dashboard?.overall
    const tracker = dashboard?.tracker
    const ratingHistory = codeforces?.ratingHistory || []
    const recentProblemsList = useMemo(
        () => [...recentProblems].slice(-6).reverse(),
        [recentProblems]
    )

    const solvedPercentage = tracker?.totalProblems
        ? Math.round((tracker.solvedProblems / tracker.totalProblems) * 100)
        : 0

    const totalPlatformSolved = platforms.reduce(
        (total, platform) => total + (platform.solved || 0),
        0
    )

    const currentRatingChange = ratingHistory.length > 1
        ? ratingHistory[ratingHistory.length - 1]?.newRating -
          ratingHistory[ratingHistory.length - 2]?.newRating
        : null

    if (loading) {
        return (
            <div className="space-y-6">
                <h1 className="text-3xl font-bold text-white">Dashboard</h1>
                <div className="rounded-2xl border border-slate-800 bg-slate-900 p-8 text-slate-400">
                    Loading your unified platform dashboard...
                </div>
            </div>
        )
    }

    return (
        <div className="space-y-8">
            <header className="flex flex-col gap-4 lg:flex-row lg:items-end lg:justify-between">
                <div>
                    <p className="text-xs font-semibold uppercase tracking-[0.24em] text-cyan-400">
                        Competitive Programming Tracker
                    </p>
                    <h1 className="mt-2 text-3xl font-bold text-white">
                        Unified Dashboard
                    </h1>
                    <p className="mt-2 text-sm text-slate-400">
                        One view of your Codeforces, LeetCode, CodeChef, AtCoder, and tracked-problem progress.
                    </p>
                </div>

                <button
                    type="button"
                    onClick={() => loadDashboard(true)}
                    disabled={refreshing}
                    className="rounded-lg border border-slate-700 bg-slate-900 px-4 py-2.5 text-sm font-semibold text-slate-200 transition hover:border-cyan-500/50 hover:text-cyan-300 disabled:cursor-not-allowed disabled:opacity-50"
                >
                    {refreshing ? "Refreshing..." : "Refresh Dashboard"}
                </button>
            </header>

            {error && (
                <div className="rounded-xl border border-red-500/30 bg-red-500/10 p-4 text-sm text-red-300">
                    {error}
                </div>
            )}

            <section className="grid grid-cols-1 gap-5 sm:grid-cols-2 xl:grid-cols-4">
                <StatCard
                    title="Platforms Connected"
                    value={overall?.platformsConnected ?? 0}
                    subtitle={`${overall?.platformsAvailable ?? 0} currently available`}
                    icon="◈"
                />
                <StatCard
                    title="Platform Solved"
                    value={overall?.totalSolved ?? totalPlatformSolved}
                    subtitle="reported across connected platforms"
                    icon="✓"
                />
                <StatCard
                    title="Platform Contests"
                    value={overall?.totalContests ?? 0}
                    subtitle="available contest totals"
                    icon="♛"
                />
                <StatCard
                    title="Tracked Solved"
                    value={overall?.trackerSolved ?? tracker?.solvedProblems ?? 0}
                    subtitle={`${solvedPercentage}% of tracked problems`}
                    icon="◉"
                />
            </section>

            <section>
                <div className="mb-4">
                    <h2 className="text-xl font-semibold text-white">Platform Overview</h2>
                    <p className="mt-1 text-sm text-slate-500">
                        Statistics are normalized into one dashboard response while platform-specific metrics remain available inside each card.
                    </p>
                </div>

                <div className="grid grid-cols-1 gap-5 md:grid-cols-2 xl:grid-cols-4">
                    {platforms.map((platform) => (
                        <PlatformCard key={platform.platform} platform={platform} />
                    ))}
                </div>
            </section>

            <div className="grid grid-cols-1 gap-6 xl:grid-cols-3">
                <section className="rounded-2xl border border-slate-800 bg-slate-900 p-6 xl:col-span-2">
                    <div className="flex items-start justify-between gap-4">
                        <div>
                            <h2 className="text-lg font-semibold text-white">
                                Codeforces Rating Progress
                            </h2>
                            <p className="mt-1 text-sm text-slate-500">
                                Latest {ratingHistory.length} rated contests.
                            </p>
                        </div>

                        {currentRatingChange !== null && (
                            <div className={
                                currentRatingChange >= 0
                                    ? "rounded-lg bg-emerald-500/10 px-3 py-2 text-sm font-semibold text-emerald-400"
                                    : "rounded-lg bg-red-500/10 px-3 py-2 text-sm font-semibold text-red-400"
                            }>
                                {currentRatingChange >= 0 ? "+" : ""}
                                {currentRatingChange}
                            </div>
                        )}
                    </div>

                    {ratingHistory.length > 0 ? (
                        <RatingChart data={ratingHistory} />
                    ) : (
                        <EmptyState
                            title="No Codeforces rating history"
                            message="Connect Codeforces from your Profile page to see rating progress."
                        />
                    )}
                </section>

                <section className="rounded-2xl border border-slate-800 bg-slate-900 p-6">
                    <h2 className="text-lg font-semibold text-white">Codeforces Submissions</h2>
                    <p className="mt-1 text-sm text-slate-500">Latest synced submission window.</p>

                    {codeforces?.connected ? (
                        <div className="mt-6 space-y-4">
                            <MetricRow label="Handle" value={codeforces.handle || "—"} />
                            <MetricRow label="Accepted" value={codeforces.acceptedSubmissionCount ?? 0} />
                            <MetricRow label="Synced" value={codeforces.syncedSubmissionCount ?? 0} />
                            <MetricRow label="Solved" value={codeforces.solvedProblemCount ?? 0} />
                            <MetricRow label="Contribution" value={codeforces.contribution ?? 0} />
                            {codeforces.error && (
                                <p className="rounded-lg bg-amber-500/10 p-3 text-xs text-amber-300">
                                    {codeforces.error}
                                </p>
                            )}
                        </div>
                    ) : (
                        <EmptyState
                            title="Codeforces not connected"
                            message="Save a Codeforces account from your Profile page."
                        />
                    )}
                </section>
            </div>

            <div className="grid grid-cols-1 gap-6 xl:grid-cols-2">
                <section className="rounded-2xl border border-slate-800 bg-slate-900 p-6">
                    <div className="flex items-center justify-between">
                        <div>
                            <h2 className="text-lg font-semibold text-white">Verdict Breakdown</h2>
                            <p className="mt-1 text-sm text-slate-500">Codeforces submissions by verdict.</p>
                        </div>
                    </div>

                    <div className="mt-6 space-y-4">
                        {Object.entries(codeforces?.verdictCounts || {}).length ? (
                            Object.entries(codeforces.verdictCounts)
                                .sort((a, b) => b[1] - a[1])
                                .slice(0, 8)
                                .map(([verdict, count]) => (
                                    <BarRow
                                        key={verdict}
                                        label={formatVerdict(verdict)}
                                        value={count}
                                        total={codeforces?.syncedSubmissionCount || 0}
                                    />
                                ))
                        ) : (
                            <EmptyState
                                title="No verdict data"
                                message="Sync Codeforces submissions to populate this section."
                            />
                        )}
                    </div>
                </section>

                <section className="rounded-2xl border border-slate-800 bg-slate-900 p-6">
                    <div>
                        <h2 className="text-lg font-semibold text-white">Tracked Problems</h2>
                        <p className="mt-1 text-sm text-slate-500">Your manually tracked problems from PostgreSQL.</p>
                    </div>

                    <div className="mt-6 space-y-3">
                        {recentProblemsList.length ? (
                            recentProblemsList.map((problem) => (
                                <div
                                    key={problem.id}
                                    className="flex items-center justify-between gap-4 rounded-xl border border-slate-800 bg-slate-950 p-4"
                                >
                                    <div className="min-w-0">
                                        <p className="truncate text-sm font-semibold text-white">
                                            {problem.title}
                                        </p>
                                        <p className="mt-1 text-xs text-slate-500">
                                            {problem.platform || "Unknown"} · {problem.topic || "No topic"}
                                        </p>
                                    </div>
                                    <span className={`shrink-0 rounded-full px-2.5 py-1 text-xs font-semibold ${statusClass(problem.status)}`}>
                                        {problem.status || "Unknown"}
                                    </span>
                                </div>
                            ))
                        ) : (
                            <EmptyState
                                title="No tracked problems"
                                message="Add problems from the Problems page to see them here."
                            />
                        )}
                    </div>
                </section>
            </div>

            <section className="rounded-2xl border border-slate-800 bg-slate-900 p-6">
                <div className="flex items-center justify-between gap-4">
                    <div>
                        <h2 className="text-lg font-semibold text-white">Platform Health</h2>
                        <p className="mt-1 text-sm text-slate-500">A quick view of connected data sources.</p>
                    </div>
                    <span className="text-xs text-slate-600">
                        Dashboard data cached briefly to reduce repeated external API calls.
                    </span>
                </div>

                <div className="mt-6 grid gap-3 sm:grid-cols-2 xl:grid-cols-4">
                    {platforms.map((platform) => (
                        <div key={platform.platform} className="flex items-center justify-between rounded-xl border border-slate-800 bg-slate-950 p-4">
                            <div>
                                <p className="text-sm font-semibold text-white">{platform.platform}</p>
                                <p className="mt-1 text-xs text-slate-500">
                                    {platform.handle || "No account saved"}
                                </p>
                            </div>
                            <span className={platform.available ? "rounded-full bg-emerald-500/10 px-2.5 py-1 text-xs font-semibold text-emerald-400" : platform.connected ? "rounded-full bg-amber-500/10 px-2.5 py-1 text-xs font-semibold text-amber-300" : "rounded-full bg-slate-800 px-2.5 py-1 text-xs font-semibold text-slate-500"}>
                                {platform.available ? "Available" : platform.connected ? "Needs sync" : "Not connected"}
                            </span>
                        </div>
                    ))}
                </div>
            </section>
        </div>
    )
}

function PlatformCard({ platform }) {
    const metrics = platform.metrics || {}

    return (
        <article className="rounded-2xl border border-slate-800 bg-slate-900 p-5">
            <div className="flex items-start justify-between gap-3">
                <div>
                    <h3 className="text-base font-semibold text-white">{platform.platform}</h3>
                    <p className="mt-1 text-xs text-slate-500">
                        {platform.handle || "No account saved"}
                    </p>
                </div>

                <span className={platform.available ? "rounded-full bg-emerald-500/10 px-2.5 py-1 text-[11px] font-semibold text-emerald-400" : platform.connected ? "rounded-full bg-amber-500/10 px-2.5 py-1 text-[11px] font-semibold text-amber-300" : "rounded-full bg-slate-800 px-2.5 py-1 text-[11px] font-semibold text-slate-500"}>
                    {platform.available ? "Connected" : platform.connected ? "Unavailable" : "Not connected"}
                </span>
            </div>

            {platform.available ? (
                <div className="mt-5 space-y-3">
                    <MetricRow label="Rating" value={platform.rating ?? "—"} />
                    <MetricRow label="Max Rating" value={platform.maxRating ?? "—"} />
                    <MetricRow label="Rank" value={platform.rank || "—"} />
                    <MetricRow label="Solved" value={platform.solved ?? "—"} />
                    <MetricRow label="Contests" value={platform.contests ?? "—"} />

                    {platform.platform === "LeetCode" && (
                        <div className="grid grid-cols-3 gap-2 pt-2">
                            <MiniMetric label="Easy" value={metrics.easySolved ?? 0} />
                            <MiniMetric label="Medium" value={metrics.mediumSolved ?? 0} />
                            <MiniMetric label="Hard" value={metrics.hardSolved ?? 0} />
                        </div>
                    )}

                    {platform.platform === "CodeChef" && (
                        <div className="pt-2 text-xs text-slate-500">
                            ⭐ {metrics.stars ?? "—"} stars · Global rank {platform.rank || "—"}
                        </div>
                    )}

                    {platform.platform === "AtCoder" && (
                        <div className="pt-2 text-xs text-slate-500">
                            Rated matches {metrics.ratedMatches ?? platform.contests ?? "—"}
                        </div>
                    )}
                </div>
            ) : platform.connected ? (
                <div className="mt-5 rounded-xl bg-amber-500/10 p-4 text-sm text-amber-300">
                    {platform.error || "The platform is connected, but its public data could not be loaded right now."}
                </div>
            ) : (
                <div className="mt-5 rounded-xl bg-slate-950 p-4 text-sm text-slate-500">
                    Save this platform account from your Profile page to include it here.
                </div>
            )}
        </article>
    )
}

function MiniMetric({ label, value }) {
    return (
        <div className="rounded-lg border border-slate-800 bg-slate-950 p-2 text-center">
            <p className="text-[10px] uppercase tracking-wide text-slate-600">{label}</p>
            <p className="mt-1 text-sm font-semibold text-white">{value}</p>
        </div>
    )
}

function RatingChart({ data }) {
    const width = 860
    const height = 280
    const padding = { top: 18, right: 18, bottom: 34, left: 44 }
    const ratings = data.map((item) => item.newRating || 0)
    const minRating = Math.min(...ratings)
    const maxRating = Math.max(...ratings)
    const range = Math.max(100, maxRating - minRating)

    const points = data.map((item, index) => {
        const x = padding.left +
            (index / Math.max(1, data.length - 1)) *
            (width - padding.left - padding.right)
        const y = padding.top +
            (1 - ((item.newRating - minRating) / range)) *
            (height - padding.top - padding.bottom)
        return { x, y, item }
    })

    const path = points
        .map((point, index) => `${index === 0 ? "M" : "L"} ${point.x.toFixed(1)} ${point.y.toFixed(1)}`)
        .join(" ")

    const gridLines = [0, 1, 2, 3, 4].map((index) => ({
        y: padding.top + (index / 4) * (height - padding.top - padding.bottom),
        value: Math.round(maxRating - (range * index) / 4),
    }))

    return (
        <div className="mt-6 overflow-x-auto rounded-xl border border-slate-800 bg-slate-950 p-3">
            <svg viewBox={`0 0 ${width} ${height}`} className="h-[280px] min-w-[720px] w-full">
                {gridLines.map((line) => (
                    <g key={line.y}>
                        <line x1={padding.left} x2={width - padding.right} y1={line.y} y2={line.y} stroke="currentColor" className="text-slate-800" />
                        <text x="8" y={line.y + 4} className="fill-slate-600 text-[11px]">{line.value}</text>
                    </g>
                ))}

                <path d={path} fill="none" stroke="currentColor" className="text-cyan-400" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round" />

                {points.map((point, index) => (
                    <circle key={`${point.item.contestId}-${index}`} cx={point.x} cy={point.y} r={index === points.length - 1 ? 5 : 3} fill="currentColor" className="text-cyan-400" />
                ))}
            </svg>
        </div>
    )
}

function BarRow({ label, value, total }) {
    const percentage = total ? Math.max(2, Math.round((value / total) * 100)) : 0

    return (
        <div>
            <div className="mb-2 flex items-center justify-between gap-4 text-sm">
                <span className="truncate text-slate-300">{label}</span>
                <span className="shrink-0 text-slate-500">{value}</span>
            </div>
            <div className="h-2 overflow-hidden rounded-full bg-slate-800">
                <div className="h-full rounded-full bg-cyan-500" style={{ width: `${percentage}%` }} />
            </div>
        </div>
    )
}

function MetricRow({ label, value }) {
    return (
        <div className="flex items-center justify-between gap-4 border-b border-slate-800 pb-3 text-sm last:border-0 last:pb-0">
            <span className="text-slate-500">{label}</span>
            <span className="max-w-[65%] truncate text-right font-medium text-white">{value}</span>
        </div>
    )
}

function EmptyState({ title, message }) {
    return (
        <div className="mt-6 rounded-xl border border-dashed border-slate-800 bg-slate-950/50 p-6 text-center">
            <p className="text-sm font-medium text-slate-300">{title}</p>
            <p className="mt-1 text-xs text-slate-600">{message}</p>
        </div>
    )
}

function formatVerdict(verdict) {
    if (!verdict) return "Unknown"
    return verdict.toLowerCase().split("_").map((part) => part.charAt(0).toUpperCase() + part.slice(1)).join(" ")
}

function statusClass(status) {
    switch (status) {
        case "Solved":
            return "bg-emerald-500/10 text-emerald-400"
        case "Attempted":
            return "bg-amber-500/10 text-amber-300"
        case "Unsolved":
            return "bg-red-500/10 text-red-300"
        default:
            return "bg-slate-800 text-slate-300"
    }
}

export default Dashboard
