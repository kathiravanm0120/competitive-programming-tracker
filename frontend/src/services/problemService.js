import { apiRequest } from "./apiClient"

export function getProblems() {
    return apiRequest("/problems")
}

export function createProblem(problem) {
    return apiRequest("/problems", {
        method: "POST",
        body: JSON.stringify(problem),
    })
}

export function updateProblem(id, problem) {
    return apiRequest(`/problems/${id}`, {
        method: "PUT",
        body: JSON.stringify(problem),
    })
}

export function deleteProblem(id) {
    return apiRequest(`/problems/${id}`, {
        method: "DELETE",
    })
}
