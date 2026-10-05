import { apiRequest } from "./apiClient"

export function getProfile() {
    return apiRequest("/profile")
}

export function updateProfile(profile) {
    return apiRequest("/profile", {
        method: "PUT",
        body: JSON.stringify(profile),
    })
}

export function getPlatformAccounts() {
    return apiRequest("/profile/platforms")
}

export function savePlatformAccount(account) {
    return apiRequest("/profile/platforms", {
        method: "PUT",
        body: JSON.stringify(account),
    })
}

export function deletePlatformAccount(platform) {
    return apiRequest(`/profile/platforms/${encodeURIComponent(platform)}`, {
        method: "DELETE",
    })
}

export function getCodeforcesProfile(handle) {
    return apiRequest(`/platforms/codeforces/${encodeURIComponent(handle)}`)
}

export function getCodeforcesRatingHistory(handle) {
    return apiRequest(
        `/platforms/codeforces/${encodeURIComponent(handle)}/rating`
    )
}

export function getCodeforcesSubmissionSummary(handle) {
    return apiRequest(
        `/platforms/codeforces/${encodeURIComponent(handle)}/submissions`
    )
}

export function getLeetCodeProfile(username) {
    return apiRequest(
        `/platforms/leetcode/${encodeURIComponent(username)}`
    )
}

export function getCodeChefProfile(username) {
    return apiRequest(
        `/platforms/codechef/${encodeURIComponent(username)}`
    )
}

export function getAtCoderProfile(username) {
    return apiRequest(
        `/platforms/atcoder/${encodeURIComponent(username)}`
    )
}

export function getCodeforcesSubmissionCode(handle, contestId, submissionId) {
    return apiRequest(
        `/platforms/codeforces/${encodeURIComponent(handle)}/submissions/${submissionId}/code?contestId=${encodeURIComponent(contestId)}`
    )
}
