import { apiRequest, getToken, clearAuth } from "./apiClient"

export function getReportSummary() {
    return apiRequest("/reports/summary")
}

async function downloadFile(path, fallbackName) {
    const token = getToken()

    const response = await fetch(
        `${import.meta.env.VITE_API_URL || "http://localhost:8080/api"}${path}`,
        {
            headers: {
                ...(token ? { Authorization: `Bearer ${token}` } : {}),
            },
        }
    )

    if (response.status === 401) {
        clearAuth()
        window.location.href = "/login"
        throw new Error("Your session has expired. Please log in again.")
    }

    if (!response.ok) {
        const text = await response.text()
        throw new Error(text || `Request failed with status ${response.status}`)
    }

    const blob = await response.blob()
    const disposition = response.headers.get("Content-Disposition") || ""
    const match = disposition.match(/filename\*?=(?:UTF-8''|\")?([^;\"]+)/i)
    const filename = match?.[1]
        ? decodeURIComponent(match[1].trim())
        : fallbackName

    const url = URL.createObjectURL(blob)
    const link = document.createElement("a")
    link.href = url
    link.download = filename
    document.body.appendChild(link)
    link.click()
    link.remove()
    URL.revokeObjectURL(url)
}

export function downloadPdfReport() {
    return downloadFile(
        "/reports/export/pdf",
        "competitive-programming-report.pdf"
    )
}

export function downloadProblemsCsv() {
    return downloadFile("/reports/export/problems/csv", "problems.csv")
}

export function downloadGoalsCsv() {
    return downloadFile("/reports/export/goals/csv", "goals.csv")
}

export function downloadPlannerCsv() {
    return downloadFile(
        "/reports/export/planner/csv",
        "planner-tasks.csv"
    )
}

export function downloadPlatformsCsv() {
    return downloadFile(
        "/reports/export/platforms/csv",
        "platform-statistics.csv"
    )
}

export function downloadAchievementsCsv() {
    return downloadFile(
        "/reports/export/achievements/csv",
        "achievements.csv"
    )
}
