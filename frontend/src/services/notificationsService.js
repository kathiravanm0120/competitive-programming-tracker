import { apiRequest } from "./apiClient"

export function getNotifications() {
    return apiRequest("/notifications")
}

export async function getUnreadNotificationCount() {
    const data = await apiRequest("/notifications/unread-count")
    return data?.count ?? 0
}

export function markNotificationAsRead(id) {
    return apiRequest(`/notifications/${id}/read`, {
        method: "PUT",
    })
}

export function markAllNotificationsAsRead() {
    return apiRequest("/notifications/read-all", {
        method: "PUT",
    })
}

export function deleteNotification(id) {
    return apiRequest(`/notifications/${id}`, {
        method: "DELETE",
    })
}
