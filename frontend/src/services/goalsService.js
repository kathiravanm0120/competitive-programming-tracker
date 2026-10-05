import { apiRequest } from "./apiClient"

export function getGoals() {
    return apiRequest("/goals")
}

export function createGoal(goal) {
    return apiRequest("/goals", {
        method: "POST",
        body: JSON.stringify(goal),
    })
}

export function updateGoal(id, goal) {
    return apiRequest(`/goals/${id}`, {
        method: "PUT",
        body: JSON.stringify(goal),
    })
}

export function deleteGoal(id) {
    return apiRequest(`/goals/${id}`, {
        method: "DELETE",
    })
}
