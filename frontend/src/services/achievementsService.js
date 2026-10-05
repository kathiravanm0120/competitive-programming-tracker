import { apiRequest } from "./apiClient"

export function getAchievements() {
    return apiRequest("/achievements")
}
