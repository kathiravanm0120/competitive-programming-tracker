import { useEffect, useMemo, useState } from "react"
import { useAuth } from "../context/AuthContext"
import {
    deletePlatformAccount,
    getCodeforcesProfile,
    getCodeforcesRatingHistory,
    getCodeforcesSubmissionSummary,
    getCodeforcesSubmissionCode,
    getCodeChefProfile,
    getAtCoderProfile,
    getLeetCodeProfile,
    getPlatformAccounts,
    getProfile,
    savePlatformAccount,
    updateProfile,
} from "../services/profileService"

const PLATFORMS = [
    "Codeforces",
    "LeetCode",
    "CodeChef",
    "AtCoder",
]

const PLATFORM_META = {
    Codeforces: {
        description: "Track rating, contests and solved problems.",
        placeholder: "your_codeforces_handle",
    },
    LeetCode: {
        description: "Track solved problems, ranking and acceptance.",
        placeholder: "your_leetcode_username",
    },
    CodeChef: {
        description: "Track rating, stars and contest performance.",
        placeholder: "your_codechef_handle",
    },
    AtCoder: {
        description: "Track rating, contests and performance.",
        placeholder: "your_atcoder_username",
    },
}

function Profile() {
    const { user } = useAuth()

    const [profile, setProfile] = useState({
        username: "",
        email: "",
        bio: "",
        profileImage: "",
        createdAt: "",
    })

    const [accounts, setAccounts] = useState([])
    const [loading, setLoading] = useState(true)
    const [savingProfile, setSavingProfile] = useState(false)
    const [savingPlatform, setSavingPlatform] = useState("")
    const [removingPlatform, setRemovingPlatform] = useState("")
    const [syncingCodeforces, setSyncingCodeforces] = useState(false)
    const [syncingRatingHistory, setSyncingRatingHistory] = useState(false)
    const [codeforcesProfile, setCodeforcesProfile] = useState(null)
    const [ratingHistory, setRatingHistory] = useState([])
    const [submissionSummary, setSubmissionSummary] = useState(null)
    const [viewingCode, setViewingCode] = useState(null)
    const [loadingCodeSubmissionId, setLoadingCodeSubmissionId] = useState(null)
    const [syncingSubmissions, setSyncingSubmissions] = useState(false)
    const [syncingLeetCode, setSyncingLeetCode] = useState(false)
    const [leetcodeProfile, setLeetCodeProfile] = useState(null)
    const [syncingCodeChef, setSyncingCodeChef] = useState(false)
    const [codeChefProfile, setCodeChefProfile] = useState(null)
    const [syncingAtCoder, setSyncingAtCoder] = useState(false)
    const [atCoderProfile, setAtCoderProfile] = useState(null)
    const [message, setMessage] = useState("")
    const [error, setError] = useState("")

    const accountMap = useMemo(() => {
        return Object.fromEntries(
            accounts.map((account) => [account.platform, account])
        )
    }, [accounts])

    useEffect(() => {
        loadProfile()
    }, [])

    async function loadProfile() {
        setLoading(true)
        setError("")

        try {
            const [profileData, platformData] = await Promise.all([
                getProfile(),
                getPlatformAccounts(),
            ])

            setProfile({
                username: profileData.username || "",
                email: profileData.email || "",
                bio: profileData.bio || "",
                profileImage: profileData.profileImage || "",
                createdAt: profileData.createdAt || "",
            })

            setAccounts(platformData || [])
        } catch (err) {
            setError(err.message || "Failed to load profile")
        } finally {
            setLoading(false)
        }
    }

    function updateField(name, value) {
        setProfile((current) => ({
            ...current,
            [name]: value,
        }))
    }

    async function handleProfileSubmit(event) {
        event.preventDefault()
        setSavingProfile(true)
        setMessage("")
        setError("")

        try {
            const data = await updateProfile({
                email: profile.email,
                bio: profile.bio,
                profileImage: profile.profileImage,
            })

            setProfile((current) => ({
                ...current,
                ...data,
            }))
            setMessage("Profile updated successfully.")
        } catch (err) {
            setError(err.message || "Failed to update profile")
        } finally {
            setSavingProfile(false)
        }
    }

    function getCodeforcesHandle() {
        return (
            document.getElementById("handle-Codeforces")?.value.trim() ||
            accountMap.Codeforces?.handle?.trim() ||
            codeforcesProfile?.handle?.trim() ||
            ""
        )
    }

    async function handleCodeforcesSync() {
        const handle = getCodeforcesHandle()

        if (!handle) {
            setError("Enter your Codeforces username/handle first.")
            return
        }

        setSyncingCodeforces(true)
        setMessage("")
        setError("")

        try {
            const data = await getCodeforcesProfile(handle)
            setCodeforcesProfile(data)
            setMessage("Codeforces profile synced successfully.")
        } catch (err) {
            setCodeforcesProfile(null)
            setError(err.message || "Failed to sync Codeforces profile")
        } finally {
            setSyncingCodeforces(false)
        }
    }

    async function handleRatingHistorySync() {
        const handle = getCodeforcesHandle()

        if (!handle) {
            setError("Enter your Codeforces username/handle first.")
            return
        }

        setSyncingRatingHistory(true)
        setMessage("")
        setError("")

        try {
            const data = await getCodeforcesRatingHistory(handle)
            setRatingHistory(Array.isArray(data) ? data : [])
            setMessage("Codeforces contest history synced successfully.")
        } catch (err) {
            setRatingHistory([])
            setError(err.message || "Failed to sync Codeforces rating history")
        } finally {
            setSyncingRatingHistory(false)
        }
    }

    function getLeetCodeUsername() {
        return (
            document.getElementById("handle-LeetCode")?.value.trim() ||
            accountMap.LeetCode?.handle?.trim() ||
            leetcodeProfile?.username?.trim() ||
            ""
        )
    }

    function getCodeChefUsername() {
        return (
            document.getElementById("handle-CodeChef")?.value.trim() ||
            accountMap.CodeChef?.handle?.trim() ||
            codeChefProfile?.username?.trim() ||
            ""
        )
    }

    function getAtCoderUsername() {
        return (
            document.getElementById("handle-AtCoder")?.value.trim() ||
            accountMap.AtCoder?.handle?.trim() ||
            atCoderProfile?.username?.trim() ||
            ""
        )
    }

    async function handleCodeChefSync() {
        const username = getCodeChefUsername()

        if (!username) {
            setError("Enter your CodeChef username first.")
            return
        }

        setSyncingCodeChef(true)
        setMessage("")
        setError("")

        try {
            const data = await getCodeChefProfile(username)
            setCodeChefProfile(data)
            setMessage("CodeChef profile synced successfully.")
        } catch (err) {
            setCodeChefProfile(null)
            setError(err.message || "Failed to sync CodeChef profile")
        } finally {
            setSyncingCodeChef(false)
        }
    }

    async function handleAtCoderSync() {
        const username = getAtCoderUsername()

        if (!username) {
            setError("Enter your AtCoder username first.")
            return
        }

        setSyncingAtCoder(true)
        setMessage("")
        setError("")

        try {
            const data = await getAtCoderProfile(username)
            setAtCoderProfile(data)
            setMessage("AtCoder profile synced successfully.")
        } catch (err) {
            setAtCoderProfile(null)
            setError(err.message || "Failed to sync AtCoder profile")
        } finally {
            setSyncingAtCoder(false)
        }
    }

    async function handleLeetCodeSync() {
        const username = getLeetCodeUsername()

        if (!username) {
            setError("Enter your LeetCode username first.")
            return
        }

        setSyncingLeetCode(true)
        setMessage("")
        setError("")

        try {
            const data = await getLeetCodeProfile(username)
            setLeetCodeProfile(data)
            setMessage("LeetCode profile synced successfully.")
        } catch (err) {
            setLeetCodeProfile(null)
            setError(err.message || "Failed to sync LeetCode profile")
        } finally {
            setSyncingLeetCode(false)
        }
    }

    async function handleSubmissionSync() {
        const handle = getCodeforcesHandle()

        if (!handle) {
            setError("Enter your Codeforces username/handle first.")
            return
        }

        setSyncingSubmissions(true)
        setMessage("")
        setError("")

        try {
            const data = await getCodeforcesSubmissionSummary(handle)
            setSubmissionSummary(data)
            setMessage("Codeforces submissions synced successfully.")
        } catch (err) {
            setSubmissionSummary(null)
            setError(err.message || "Failed to sync Codeforces submissions")
        } finally {
            setSyncingSubmissions(false)
        }
    }


    async function handleViewSubmittedCode(submission) {
        const handle = getCodeforcesHandle()

        if (!handle) {
            setError("Connect a Codeforces account before viewing submitted code.")
            return
        }

        setLoadingCodeSubmissionId(submission.id)
        setMessage("")
        setError("")

        try {
            const data = await getCodeforcesSubmissionCode(
                handle,
                submission.contestId,
                submission.id
            )
            setViewingCode({
                ...data,
                problemName: submission.problemName,
                problemIndex: submission.problemIndex,
                contestId: submission.contestId,
                verdict: submission.verdict,
                programmingLanguage: submission.programmingLanguage,
            })
        } catch (err) {
            setViewingCode(null)
            setError(err.message || "Failed to load submitted code")
        } finally {
            setLoadingCodeSubmissionId(null)
        }
    }

    async function handlePlatformSave(platform) {
        const handle =
            document.getElementById(`handle-${platform}`)?.value.trim() || ""
        const profileUrl =
            document.getElementById(`url-${platform}`)?.value.trim() || ""

        if (!handle) {
            setError(`Enter your ${platform} username.`)
            return
        }

        setSavingPlatform(platform)
        setMessage("")
        setError("")

        try {
            const saved = await savePlatformAccount({
                platform,
                handle,
                profileUrl,
            })

            setAccounts((current) => {
                const remaining = current.filter(
                    (account) => account.platform !== platform
                )
                return [...remaining, saved]
            })

            setMessage(`${platform} account saved.`)
        } catch (err) {
            setError(err.message || `Failed to save ${platform} account`)
        } finally {
            setSavingPlatform("")
        }
    }

    async function handlePlatformRemove(platform) {
        const confirmed = window.confirm(
            `Remove your ${platform} account from the tracker?`
        )

        if (!confirmed) return

        setRemovingPlatform(platform)
        setMessage("")
        setError("")

        try {
            await deletePlatformAccount(platform)
            setAccounts((current) =>
                current.filter((account) => account.platform !== platform)
            )

            if (platform === "Codeforces") {
                setCodeforcesProfile(null)
                setRatingHistory([])
                setSubmissionSummary(null)
            }

            if (platform === "LeetCode") {
                setLeetCodeProfile(null)
            }

            if (platform === "CodeChef") {
                setCodeChefProfile(null)
            }

            if (platform === "AtCoder") {
                setAtCoderProfile(null)
            }

            setMessage(`${platform} account removed.`)
        } catch (err) {
            setError(err.message || `Failed to remove ${platform} account`)
        } finally {
            setRemovingPlatform("")
        }
    }

    if (loading) {
        return (
            <div className="rounded-2xl border border-slate-800 bg-slate-900 p-8 text-slate-400">
                Loading profile...
            </div>
        )
    }

    const initial = (profile.username || user?.username || "U")
        .charAt(0)
        .toUpperCase()

    return (
        <div className="space-y-8">
            <div>
                <h1 className="text-3xl font-bold text-white">Profile</h1>
                <p className="mt-2 text-sm text-slate-400">
                    Manage your account details and competitive programming profiles.
                </p>
            </div>

            {message && (
                <div className="rounded-xl border border-emerald-500/30 bg-emerald-500/10 px-4 py-3 text-sm text-emerald-300">
                    {message}
                </div>
            )}

            {error && (
                <div className="rounded-xl border border-red-500/30 bg-red-500/10 px-4 py-3 text-sm text-red-300">
                    {error}
                </div>
            )}

            <section className="grid gap-6 xl:grid-cols-[1.1fr_0.9fr]">
                <form
                    onSubmit={handleProfileSubmit}
                    className="rounded-2xl border border-slate-800 bg-slate-900 p-6"
                >
                    <div className="mb-6 flex items-center gap-4">
                        <div className="flex h-16 w-16 items-center justify-center overflow-hidden rounded-full bg-cyan-500 text-2xl font-bold text-slate-950">
                            {profile.profileImage ? (
                                <img
                                    src={profile.profileImage}
                                    alt={profile.username}
                                    className="h-full w-full object-cover"
                                />
                            ) : (
                                initial
                            )}
                        </div>
                        <div>
                            <h2 className="text-lg font-semibold text-white">
                                Account Information
                            </h2>
                            <p className="text-sm text-slate-500">
                                Username is fixed for now so existing JWT sessions stay valid.
                            </p>
                        </div>
                    </div>

                    <div className="space-y-5">
                        <Field label="Username">
                            <input
                                value={profile.username}
                                disabled
                                className="w-full rounded-lg border border-slate-700 bg-slate-950 px-4 py-3 text-sm text-slate-500"
                            />
                        </Field>

                        <Field label="Email">
                            <input
                                type="email"
                                value={profile.email}
                                onChange={(event) =>
                                    updateField("email", event.target.value)
                                }
                                required
                                className="input"
                            />
                        </Field>

                        <Field label="Profile image URL">
                            <input
                                type="url"
                                value={profile.profileImage}
                                onChange={(event) =>
                                    updateField("profileImage", event.target.value)
                                }
                                placeholder="https://..."
                                className="input"
                            />
                        </Field>

                        <Field label="Bio">
                            <textarea
                                rows="5"
                                maxLength={500}
                                value={profile.bio}
                                onChange={(event) =>
                                    updateField("bio", event.target.value)
                                }
                                placeholder="Tell us about your competitive programming journey..."
                                className="input resize-none"
                            />
                        </Field>

                        <button
                            type="submit"
                            disabled={savingProfile}
                            className="rounded-lg bg-cyan-500 px-5 py-3 text-sm font-semibold text-slate-950 transition hover:bg-cyan-400 disabled:cursor-not-allowed disabled:opacity-60"
                        >
                            {savingProfile ? "Saving..." : "Save Profile"}
                        </button>
                    </div>
                </form>

                <div className="rounded-2xl border border-slate-800 bg-slate-900 p-6">
                    <h2 className="text-lg font-semibold text-white">
                        Account Summary
                    </h2>
                    <div className="mt-5 space-y-4">
                        <SummaryRow label="Username" value={profile.username || "—"} />
                        <SummaryRow label="Email" value={profile.email || "—"} />
                        <SummaryRow
                            label="Member since"
                            value={
                                profile.createdAt
                                    ? new Date(profile.createdAt).toLocaleDateString()
                                    : "—"
                            }
                        />
                        <SummaryRow
                            label="Connected platforms"
                            value={`${accounts.length} / ${PLATFORMS.length}`}
                        />
                    </div>
                </div>
            </section>

            <section>
                <div className="mb-5">
                    <h2 className="text-xl font-semibold text-white">
                        Coding Platform Accounts
                    </h2>
                    <p className="mt-1 text-sm text-slate-500">
                        Save your handles and sync the supported platforms to see live public profile data.
                    </p>
                </div>

                <div className="grid gap-5 lg:grid-cols-2">
                    {PLATFORMS.map((platform) => {
                        const account = accountMap[platform]

                        return (
                            <div
                                key={platform}
                                className="rounded-2xl border border-slate-800 bg-slate-900 p-6"
                            >
                                <div className="flex items-start justify-between gap-4">
                                    <div>
                                        <h3 className="text-lg font-semibold text-white">
                                            {platform}
                                        </h3>
                                        <p className="mt-1 text-sm text-slate-500">
                                            {PLATFORM_META[platform].description}
                                        </p>
                                    </div>
                                    <span
                                        className={`rounded-full px-3 py-1 text-xs font-medium ${
                                            account
                                                ? "bg-emerald-500/10 text-emerald-300"
                                                : "bg-slate-800 text-slate-500"
                                        }`}
                                    >
                                        {account ? "Connected" : "Not connected"}
                                    </span>
                                </div>

                                <div className="mt-5 space-y-4">
                                    <Field label="Username / Handle">
                                        <input
                                            id={`handle-${platform}`}
                                            defaultValue={account?.handle || ""}
                                            placeholder={PLATFORM_META[platform].placeholder}
                                            className="input"
                                        />
                                    </Field>

                                    <Field label="Profile URL (optional)">
                                        <input
                                            id={`url-${platform}`}
                                            type="url"
                                            defaultValue={account?.profileUrl || ""}
                                            placeholder="https://..."
                                            className="input"
                                        />
                                    </Field>

                                    <div className="flex flex-wrap gap-3">
                                        <button
                                            type="button"
                                            onClick={() => handlePlatformSave(platform)}
                                            disabled={savingPlatform === platform}
                                            className="rounded-lg bg-cyan-500 px-4 py-2.5 text-sm font-semibold text-slate-950 transition hover:bg-cyan-400 disabled:cursor-not-allowed disabled:opacity-60"
                                        >
                                            {savingPlatform === platform
                                                ? "Saving..."
                                                : "Save Account"}
                                        </button>

                                        {platform === "Codeforces" && (
                                            <>
                                                <button
                                                    type="button"
                                                    onClick={handleCodeforcesSync}
                                                    disabled={syncingCodeforces}
                                                    className="rounded-lg border border-cyan-500/30 px-4 py-2.5 text-sm font-medium text-cyan-300 transition hover:bg-cyan-500/10 disabled:cursor-not-allowed disabled:opacity-60"
                                                >
                                                    {syncingCodeforces
                                                        ? "Syncing..."
                                                        : "Sync Codeforces"}
                                                </button>

                                                <button
                                                    type="button"
                                                    onClick={handleRatingHistorySync}
                                                    disabled={syncingRatingHistory}
                                                    className="rounded-lg border border-violet-500/30 px-4 py-2.5 text-sm font-medium text-violet-300 transition hover:bg-violet-500/10 disabled:cursor-not-allowed disabled:opacity-60"
                                                >
                                                    {syncingRatingHistory
                                                        ? "Syncing history..."
                                                        : "Sync Contest History"}
                                                </button>

                                                <button
                                                    type="button"
                                                    onClick={handleSubmissionSync}
                                                    disabled={syncingSubmissions}
                                                    className="rounded-lg border border-emerald-500/30 px-4 py-2.5 text-sm font-medium text-emerald-300 transition hover:bg-emerald-500/10 disabled:cursor-not-allowed disabled:opacity-60"
                                                >
                                                    {syncingSubmissions
                                                        ? "Syncing submissions..."
                                                        : "Sync Submissions"}
                                                </button>
                                            </>
                                        )}

                                        {platform === "LeetCode" && (
                                            <button
                                                type="button"
                                                onClick={handleLeetCodeSync}
                                                disabled={syncingLeetCode}
                                                className="rounded-lg border border-amber-500/30 px-4 py-2.5 text-sm font-medium text-amber-300 transition hover:bg-amber-500/10 disabled:cursor-not-allowed disabled:opacity-60"
                                            >
                                                {syncingLeetCode
                                                    ? "Syncing..."
                                                    : "Sync LeetCode"}
                                            </button>
                                        )}

                                        {platform === "CodeChef" && (
                                            <button
                                                type="button"
                                                onClick={handleCodeChefSync}
                                                disabled={syncingCodeChef}
                                                className="rounded-lg border border-orange-500/30 px-4 py-2.5 text-sm font-medium text-orange-300 transition hover:bg-orange-500/10 disabled:cursor-not-allowed disabled:opacity-60"
                                            >
                                                {syncingCodeChef
                                                    ? "Syncing..."
                                                    : "Sync CodeChef"}
                                            </button>
                                        )}

                                        {platform === "AtCoder" && (
                                            <button
                                                type="button"
                                                onClick={handleAtCoderSync}
                                                disabled={syncingAtCoder}
                                                className="rounded-lg border border-violet-500/30 px-4 py-2.5 text-sm font-medium text-violet-300 transition hover:bg-violet-500/10 disabled:cursor-not-allowed disabled:opacity-60"
                                            >
                                                {syncingAtCoder
                                                    ? "Syncing..."
                                                    : "Sync AtCoder"}
                                            </button>
                                        )}

                                        {account && (
                                            <button
                                                type="button"
                                                onClick={() => handlePlatformRemove(platform)}
                                                disabled={removingPlatform === platform}
                                                className="rounded-lg border border-red-500/30 px-4 py-2.5 text-sm font-medium text-red-300 transition hover:bg-red-500/10 disabled:cursor-not-allowed disabled:opacity-60"
                                            >
                                                {removingPlatform === platform
                                                    ? "Removing..."
                                                    : "Remove"}
                                            </button>
                                        )}
                                    </div>

                                    {platform === "Codeforces" && codeforcesProfile && (
                                        <div className="mt-5 grid gap-3 sm:grid-cols-2">
                                            <Metric label="Handle" value={codeforcesProfile.handle || "—"} />
                                            <Metric label="Rating" value={codeforcesProfile.rating ?? "Unrated"} />
                                            <Metric label="Max Rating" value={codeforcesProfile.maxRating ?? "—"} />
                                            <Metric label="Rank" value={formatRank(codeforcesProfile.rank)} />
                                            <Metric label="Max Rank" value={formatRank(codeforcesProfile.maxRank)} />
                                            <Metric label="Contribution" value={codeforcesProfile.contribution ?? "0"} />
                                        </div>
                                    )}

                                    {platform === "LeetCode" && leetcodeProfile && (
                                        <div className="mt-5 grid gap-3 sm:grid-cols-2">
                                            <Metric label="Username" value={leetcodeProfile.username || "—"} />
                                            <Metric label="Ranking" value={formatNumber(leetcodeProfile.ranking)} />
                                            <Metric label="Total Solved" value={leetcodeProfile.totalSolved ?? 0} />
                                            <Metric label="Easy" value={leetcodeProfile.easySolved ?? 0} />
                                            <Metric label="Medium" value={leetcodeProfile.mediumSolved ?? 0} />
                                            <Metric label="Hard" value={leetcodeProfile.hardSolved ?? 0} />
                                            <Metric label="Acceptance" value={`${leetcodeProfile.acceptanceRate ?? 0}%`} />
                                            <Metric label="Submissions" value={formatNumber(leetcodeProfile.totalSubmissions)} />
                                        </div>
                                    )}

                                    {platform === "CodeChef" && codeChefProfile && (
                                        <div className="mt-5 grid gap-3 sm:grid-cols-2">
                                            <Metric label="Username" value={codeChefProfile.username || "—"} />
                                            <Metric label="Rating" value={codeChefProfile.currentRating ?? "—"} />
                                            <Metric label="Highest Rating" value={codeChefProfile.highestRating ?? "—"} />
                                            <Metric label="Stars" value={codeChefProfile.stars ? `${codeChefProfile.stars}★` : "—"} />
                                            <Metric label="Global Rank" value={codeChefProfile.globalRank || "—"} />
                                            <Metric label="Country Rank" value={codeChefProfile.countryRank || "—"} />
                                            <Metric label="Contests" value={codeChefProfile.contestsParticipated ?? 0} />
                                            <Metric label="Problems Solved" value={codeChefProfile.problemsSolved ?? 0} />
                                        </div>
                                    )}

                                    {platform === "AtCoder" && atCoderProfile && (
                                        <div className="mt-5 grid gap-3 sm:grid-cols-2">
                                            <Metric label="Username" value={atCoderProfile.username || "—"} />
                                            <Metric label="Rating" value={atCoderProfile.rating ?? "—"} />
                                            <Metric label="Highest Rating" value={atCoderProfile.highestRating ?? "—"} />
                                            <Metric label="Rank" value={atCoderProfile.rank || "—"} />
                                            <Metric label="Rated Matches" value={atCoderProfile.ratedMatches ?? 0} />
                                            <Metric label="Last Competed" value={atCoderProfile.lastCompeted || "—"} />
                                            <Metric label="Country" value={atCoderProfile.country || "—"} />
                                            <Metric label="Affiliation" value={atCoderProfile.affiliation || "—"} />
                                        </div>
                                    )}
                                </div>
                            </div>
                        )
                    })}
                </div>
            </section>

            {submissionSummary && (
                <section className="rounded-2xl border border-slate-800 bg-slate-900 p-6">
                    <div className="flex flex-col justify-between gap-3 md:flex-row md:items-end">
                        <div>
                            <h2 className="text-xl font-semibold text-white">
                                Codeforces Submission Analytics
                            </h2>
                            <p className="mt-1 text-sm text-slate-500">
                                Latest {submissionSummary.syncedSubmissionCount} submissions synced from Codeforces.
                                Solved count is unique across the synced submissions.
                            </p>
                        </div>
                        <div className="text-sm text-slate-400">
                            Handle: <span className="font-semibold text-white">
                                {submissionSummary.handle}
                            </span>
                        </div>
                    </div>

                    <div className="mt-6 grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
                        <Metric label="Synced Submissions" value={submissionSummary.syncedSubmissionCount ?? 0} />
                        <Metric label="Accepted Submissions" value={submissionSummary.acceptedSubmissionCount ?? 0} />
                        <Metric label="Unique Solved" value={submissionSummary.solvedProblemCount ?? 0} />
                        <Metric label="Distinct Verdicts" value={Object.keys(submissionSummary.verdictCounts || {}).length} />
                    </div>

                    <div className="mt-6 grid gap-5 lg:grid-cols-2">
                        <StatsList title="Verdict Breakdown" data={submissionSummary.verdictCounts || {}} />
                        <StatsList title="Language Breakdown" data={submissionSummary.languageCounts || {}} />
                    </div>

                    <div className="mt-6 overflow-x-auto rounded-xl border border-slate-800">
                        <table className="w-full min-w-[980px] text-left text-sm">
                            <thead className="border-b border-slate-800 bg-slate-950/70 text-xs uppercase tracking-wide text-slate-500">
                                <tr>
                                    <th className="px-4 py-3">Problem</th>
                                    <th className="px-4 py-3">Date</th>
                                    <th className="px-4 py-3">Verdict</th>
                                    <th className="px-4 py-3">Language</th>
                                    <th className="px-4 py-3">Time</th>
                                    <th className="px-4 py-3">Memory</th>
                                    <th className="px-4 py-3">Code</th>
                                </tr>
                            </thead>
                            <tbody>
                                {(submissionSummary.recentSubmissions || []).map((submission) => {
                                    const verdictClass = submission.verdict === "OK"
                                        ? "text-emerald-300"
                                        : submission.verdict === "WRONG_ANSWER"
                                            ? "text-red-300"
                                            : "text-amber-300"

                                    return (
                                        <tr
                                            key={submission.id}
                                            className="border-b border-slate-800 last:border-b-0"
                                        >
                                            <td className="px-4 py-3 font-medium text-white">
                                                <a
                                                    href={`https://codeforces.com/contest/${submission.contestId}/problem/${submission.problemIndex}`}
                                                    target="_blank"
                                                    rel="noreferrer"
                                                    className="hover:text-cyan-300"
                                                >
                                                    {submission.problemIndex}. {submission.problemName}
                                                </a>
                                            </td>
                                            <td className="px-4 py-3 text-slate-400">
                                                {formatUnixDateTime(submission.creationTimeSeconds)}
                                            </td>
                                            <td className={`px-4 py-3 font-semibold ${verdictClass}`}>
                                                {formatVerdict(submission.verdict)}
                                            </td>
                                            <td className="px-4 py-3 text-slate-300">
                                                {submission.programmingLanguage || "—"}
                                            </td>
                                            <td className="px-4 py-3 text-slate-300">
                                                {submission.timeConsumedMillis ?? 0} ms
                                            </td>
                                            <td className="px-4 py-3 text-slate-300">
                                                {formatBytes(submission.memoryConsumedBytes)}
                                            </td>
                                            <td className="px-4 py-3">
                                                <button
                                                    type="button"
                                                    onClick={() => handleViewSubmittedCode(submission)}
                                                    disabled={loadingCodeSubmissionId === submission.id}
                                                    className="rounded-lg border border-cyan-500/30 px-3 py-1.5 text-xs font-semibold text-cyan-300 transition hover:bg-cyan-500/10 disabled:cursor-not-allowed disabled:opacity-50"
                                                >
                                                    {loadingCodeSubmissionId === submission.id
                                                        ? "Loading..."
                                                        : "View Code"}
                                                </button>
                                            </td>
                                        </tr>
                                    )
                                })}
                            </tbody>
                        </table>
                    </div>
                </section>
            )}

            {ratingHistory.length > 0 && (
                <section className="rounded-2xl border border-slate-800 bg-slate-900 p-6">
                    <div className="flex flex-col justify-between gap-3 md:flex-row md:items-end">
                        <div>
                            <h2 className="text-xl font-semibold text-white">
                                Codeforces Rating History
                            </h2>
                            <p className="mt-1 text-sm text-slate-500">
                                {ratingHistory.length} rated contests synced from Codeforces.
                            </p>
                        </div>
                        <div className="text-sm text-slate-400">
                            Current: <span className="font-semibold text-white">
                                {ratingHistory[ratingHistory.length - 1]?.newRating ?? "—"}
                            </span>
                        </div>
                    </div>

                    <RatingChart history={ratingHistory} />

                    <div className="mt-6 overflow-x-auto rounded-xl border border-slate-800">
                        <table className="w-full min-w-[760px] text-left text-sm">
                            <thead className="border-b border-slate-800 bg-slate-950/70 text-xs uppercase tracking-wide text-slate-500">
                                <tr>
                                    <th className="px-4 py-3">Contest</th>
                                    <th className="px-4 py-3">Date</th>
                                    <th className="px-4 py-3">Rank</th>
                                    <th className="px-4 py-3">Change</th>
                                    <th className="px-4 py-3">Rating</th>
                                </tr>
                            </thead>
                            <tbody>
                                {[...ratingHistory].reverse().slice(0, 10).map((contest) => {
                                    const change = contest.newRating - contest.oldRating
                                    const changeClass = change >= 0
                                        ? "text-emerald-300"
                                        : "text-red-300"

                                    return (
                                        <tr
                                            key={`${contest.contestId}-${contest.ratingUpdateTimeSeconds}`}
                                            className="border-b border-slate-800 last:border-b-0"
                                        >
                                            <td className="max-w-[360px] px-4 py-3 font-medium text-white">
                                                <a
                                                    href={`https://codeforces.com/contest/${contest.contestId}`}
                                                    target="_blank"
                                                    rel="noreferrer"
                                                    className="hover:text-cyan-300"
                                                >
                                                    {contest.contestName}
                                                </a>
                                            </td>
                                            <td className="px-4 py-3 text-slate-400">
                                                {formatUnixDate(contest.ratingUpdateTimeSeconds)}
                                            </td>
                                            <td className="px-4 py-3 text-slate-300">
                                                #{contest.rank}
                                            </td>
                                            <td className={`px-4 py-3 font-semibold ${changeClass}`}>
                                                {change >= 0 ? "+" : ""}{change}
                                            </td>
                                            <td className="px-4 py-3 font-semibold text-white">
                                                {contest.newRating}
                                            </td>
                                        </tr>
                                    )
                                })}
                            </tbody>
                        </table>
                    </div>
                </section>
            )}
            {viewingCode && (
                <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/80 p-4 backdrop-blur-sm">
                    <div className="flex max-h-[90vh] w-full max-w-6xl flex-col overflow-hidden rounded-2xl border border-slate-700 bg-slate-950 shadow-2xl">
                        <div className="flex flex-col justify-between gap-3 border-b border-slate-800 px-5 py-4 sm:flex-row sm:items-center">
                            <div>
                                <h2 className="text-lg font-semibold text-white">
                                    {viewingCode.problemIndex}. {viewingCode.problemName}
                                </h2>
                                <p className="mt-1 text-xs text-slate-500">
                                    Submission #{viewingCode.submissionId} · {viewingCode.programmingLanguage || "Codeforces"} · {formatVerdict(viewingCode.verdict)}
                                </p>
                            </div>
                            <div className="flex items-center gap-2">
                                <a
                                    href={viewingCode.submissionUrl}
                                    target="_blank"
                                    rel="noreferrer"
                                    className="rounded-lg border border-slate-700 px-3 py-2 text-xs font-semibold text-slate-300 transition hover:bg-slate-800 hover:text-white"
                                >
                                    Open on Codeforces
                                </a>
                                <button
                                    type="button"
                                    onClick={() => setViewingCode(null)}
                                    className="rounded-lg border border-slate-700 px-3 py-2 text-sm text-slate-400 transition hover:bg-slate-800 hover:text-white"
                                >
                                    Close
                                </button>
                            </div>
                        </div>

                        <div className="min-h-0 overflow-auto bg-[#0b1120]">
                            <pre className="p-5 text-sm leading-6 text-slate-200">
                                <code>{viewingCode.sourceCode}</code>
                            </pre>
                        </div>
                    </div>
                </div>
            )}
        </div>

    )
}

function RatingChart({ history }) {
    const width = 960
    const height = 300
    const padding = { top: 24, right: 24, bottom: 36, left: 52 }

    const values = history.map((item) => item.newRating)
    const minValue = Math.min(...values)
    const maxValue = Math.max(...values)
    const range = Math.max(maxValue - minValue, 100)

    const points = history.map((item, index) => {
        const x = history.length === 1
            ? width / 2
            : padding.left +
              (index / (history.length - 1)) *
                  (width - padding.left - padding.right)

        const y = height - padding.bottom -
            ((item.newRating - minValue) / range) *
                (height - padding.top - padding.bottom)

        return { x, y, rating: item.newRating }
    })

    const polyline = points.map((point) => `${point.x},${point.y}`).join(" ")

    const gridValues = [0, 0.5, 1].map((fraction) =>
        Math.round(maxValue - fraction * range)
    )

    return (
        <div className="mt-6 overflow-x-auto rounded-xl border border-slate-800 bg-slate-950 p-4">
            <svg
                viewBox={`0 0 ${width} ${height}`}
                className="min-w-[760px] w-full"
                role="img"
                aria-label="Codeforces rating history chart"
            >
                {gridValues.map((value, index) => {
                    const y = padding.top +
                        (index / 2) *
                            (height - padding.top - padding.bottom)

                    return (
                        <g key={value}>
                            <line
                                x1={padding.left}
                                x2={width - padding.right}
                                y1={y}
                                y2={y}
                                stroke="rgb(51 65 85)"
                                strokeWidth="1"
                            />
                            <text
                                x={padding.left - 10}
                                y={y + 4}
                                textAnchor="end"
                                fill="rgb(148 163 184)"
                                fontSize="12"
                            >
                                {value}
                            </text>
                        </g>
                    )
                })}

                <polyline
                    fill="none"
                    stroke="rgb(34 211 238)"
                    strokeWidth="3"
                    strokeLinejoin="round"
                    strokeLinecap="round"
                    points={polyline}
                />

                {points.map((point, index) => {
                    if (history.length > 80 && index % Math.ceil(history.length / 40) !== 0) {
                        return null
                    }

                    return (
                        <circle
                            key={`${point.x}-${point.y}`}
                            cx={point.x}
                            cy={point.y}
                            r="3"
                            fill="rgb(34 211 238)"
                        />
                    )
                })}
            </svg>
        </div>
    )
}

function formatUnixDate(seconds) {
    if (!seconds) return "—"
    return new Date(seconds * 1000).toLocaleDateString()
}

function Field({ label, children }) {
    return (
        <label className="block">
            <span className="mb-2 block text-sm font-medium text-slate-300">
                {label}
            </span>
            {children}
        </label>
    )
}

function Metric({ label, value }) {
    return (
        <div className="rounded-xl border border-slate-800 bg-slate-950 p-4">
            <p className="text-xs uppercase tracking-wide text-slate-500">{label}</p>
            <p className="mt-1 truncate text-sm font-semibold text-white">{value}</p>
        </div>
    )
}

function formatNumber(value) {
    if (value === null || value === undefined || value === "") {
        return "—"
    }

    return Number(value).toLocaleString()
}

function formatRank(rank) {
    if (!rank) return "Unrated"
    return rank
        .split(" ")
        .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
        .join(" ")
}

function StatsList({ title, data }) {
    const entries = Object.entries(data).sort((a, b) => b[1] - a[1])
    const max = entries[0]?.[1] || 1

    return (
        <div className="rounded-xl border border-slate-800 bg-slate-950 p-4">
            <h3 className="text-sm font-semibold text-white">{title}</h3>
            <div className="mt-4 space-y-3">
                {entries.slice(0, 8).map(([name, count]) => (
                    <div key={name}>
                        <div className="mb-1 flex items-center justify-between gap-4 text-xs">
                            <span className="truncate text-slate-400">{formatVerdict(name)}</span>
                            <span className="font-semibold text-white">{count}</span>
                        </div>
                        <div className="h-2 overflow-hidden rounded-full bg-slate-800">
                            <div
                                className="h-full rounded-full bg-cyan-400"
                                style={{ width: `${Math.max((count / max) * 100, 4)}%` }}
                            />
                        </div>
                    </div>
                ))}
            </div>
        </div>
    )
}

function formatVerdict(value) {
    if (!value) return "Unknown"
    return value
        .toLowerCase()
        .split("_")
        .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
        .join(" ")
}

function formatUnixDateTime(seconds) {
    if (!seconds) return "—"
    return new Date(seconds * 1000).toLocaleString()
}

function formatBytes(bytes) {
    if (!bytes && bytes !== 0) return "—"
    if (bytes < 1024) return `${bytes} B`
    const kb = bytes / 1024
    if (kb < 1024) return `${kb.toFixed(1)} KB`
    const mb = kb / 1024
    return `${mb.toFixed(1)} MB`
}

function SummaryRow({ label, value }) {
    return (
        <div className="flex items-center justify-between gap-4 border-b border-slate-800 pb-4 last:border-b-0 last:pb-0">
            <span className="text-sm text-slate-500">{label}</span>
            <span className="max-w-[65%] truncate text-right text-sm font-medium text-white">
                {value}
            </span>
        </div>
    )

}

export default Profile
