import { useEffect, useMemo, useState } from "react"
import { getAchievements } from "../services/achievementsService"

function Achievements() {
    const [achievements, setAchievements] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState("")

    const loadAchievements = async () => {
        setLoading(true)
        setError("")

        try {
            const data = await getAchievements()
            setAchievements(Array.isArray(data) ? data : [])
        } catch (err) {
            setError(err.message || "Failed to load achievements")
        } finally {
            setLoading(false)
        }
    }

    useEffect(() => {
        loadAchievements()
    }, [])

    const unlocked = useMemo(
        () => achievements.filter((item) => item.unlocked),
        [achievements]
    )

    if (loading) {
        return (
            <div className="rounded-2xl border border-slate-800 bg-slate-900 p-8 text-slate-400">
                Loading achievements...
            </div>
        )
    }

    return (
        <div className="space-y-8">
            <div className="flex flex-col justify-between gap-4 md:flex-row md:items-end">
                <div>
                    <h1 className="text-3xl font-bold text-white">Achievements</h1>
                    <p className="mt-2 text-sm text-slate-400">
                        Turn your progress into milestones and badges.
                    </p>
                </div>

                <div className="rounded-xl border border-slate-800 bg-slate-900 px-4 py-3 text-sm text-slate-300">
                    <span className="font-semibold text-cyan-300">{unlocked.length}</span> unlocked
                    <span className="mx-2 text-slate-600">/</span>
                    {achievements.length} total
                </div>
            </div>

            {error && (
                <div className="rounded-xl border border-red-500/30 bg-red-500/10 px-4 py-3 text-sm text-red-300">
                    {error}
                </div>
            )}

            <div className="grid gap-5 sm:grid-cols-2 xl:grid-cols-3">
                {achievements.map((achievement) => (
                    <AchievementCard key={achievement.code} achievement={achievement} />
                ))}
            </div>
        </div>
    )
}

function AchievementCard({ achievement }) {
    const current = achievement.currentValue ?? 0
    const target = achievement.targetValue ?? 1
    const progress = achievement.progressPercent ?? 0

    return (
        <article
            className={`rounded-2xl border p-5 ${
                achievement.unlocked
                    ? "border-cyan-500/30 bg-cyan-500/5"
                    : "border-slate-800 bg-slate-900"
            }`}
        >
            <div className="flex items-start gap-4">
                <div className="flex h-12 w-12 shrink-0 items-center justify-center rounded-xl bg-slate-950 text-2xl">
                    {achievement.unlocked ? achievement.icon : "🔒"}
                </div>

                <div className="min-w-0 flex-1">
                    <div className="flex items-start justify-between gap-2">
                        <div>
                            <h2 className="font-semibold text-white">{achievement.title}</h2>
                            <p className="mt-1 text-xs uppercase tracking-wide text-slate-500">
                                {achievement.category}
                            </p>
                        </div>

                        {achievement.unlocked && (
                            <span className="rounded-full bg-emerald-500/10 px-2.5 py-1 text-xs font-semibold text-emerald-300">
                                Unlocked
                            </span>
                        )}
                    </div>
                </div>
            </div>

            <p className="mt-4 text-sm leading-6 text-slate-400">
                {achievement.description}
            </p>

            <div className="mt-5">
                <div className="mb-2 flex items-center justify-between text-xs">
                    <span className="text-slate-500">Progress</span>
                    <span className="font-semibold text-slate-300">
                        {Math.min(current, target)} / {target}
                    </span>
                </div>

                <div className="h-2 overflow-hidden rounded-full bg-slate-800">
                    <div
                        className={`h-full rounded-full transition-all ${
                            achievement.unlocked ? "bg-cyan-400" : "bg-slate-500"
                        }`}
                        style={{ width: `${progress}%` }}
                    />
                </div>
            </div>
        </article>
    )
}

export default Achievements
