import { apiRequest } from "./apiClient"

export function getAdminStats() {
    return apiRequest("/admin/stats")
}

export function getAdminUsers() {
    return apiRequest("/admin/users")
}

export function setUserEnabled(id, enabled) {
    return apiRequest(
        `/admin/users/${id}/status?enabled=${enabled}`,
        { method: "PUT" }
    )
}

export function setUserRole(id, role) {
    return apiRequest(
        `/admin/users/${id}/role?role=${encodeURIComponent(role)}`,
        { method: "PUT" }
    )
}

export function deleteAdminUser(id) {
    return apiRequest(`/admin/users/${id}`, { method: "DELETE" })
}

export function getAdminProblems() {
    return apiRequest("/admin/problems")
}

export function deleteAdminProblem(id) {
    return apiRequest(`/admin/problems/${id}`, { method: "DELETE" })
}
