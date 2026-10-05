import { apiRequest } from "./apiClient"

export function getPlannerTasks(date) {
    const query = date ? `?date=${encodeURIComponent(date)}` : ""
    return apiRequest(`/planner/tasks${query}`)
}

export function createPlannerTask(task) {
    return apiRequest("/planner/tasks", {
        method: "POST",
        body: JSON.stringify(task),
    })
}

export function updatePlannerTask(id, task) {
    return apiRequest(`/planner/tasks/${id}`, {
        method: "PUT",
        body: JSON.stringify(task),
    })
}

export function deletePlannerTask(id) {
    return apiRequest(`/planner/tasks/${id}`, {
        method: "DELETE",
    })
}
