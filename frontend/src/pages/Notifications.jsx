import { useEffect, useMemo, useState } from "react"
import {
    deleteNotification,
    getNotifications,
    markAllNotificationsAsRead,
    markNotificationAsRead,
} from "../services/notificationsService"

function Notifications() {
    const [notifications, setNotifications] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState("")

    const loadNotifications = async () => {
        setLoading(true)
        setError("")

        try {
            const data = await getNotifications()
            setNotifications(Array.isArray(data) ? data : [])
        } catch (err) {
            setError(err.message || "Failed to load notifications")
        } finally {
            setLoading(false)
        }
    }

    useEffect(() => {
        loadNotifications()
    }, [])

    const unreadCount = useMemo(
        () => notifications.filter((item) => !item.read).length,
        [notifications]
    )

    const handleRead = async (notification) => {
        if (notification.read) return

        try {
            const updated = await markNotificationAsRead(notification.id)
            setNotifications((current) =>
                current.map((item) =>
                    item.id === updated.id ? updated : item
                )
            )
        } catch (err) {
            setError(err.message || "Failed to mark notification as read")
        }
    }

    const handleReadAll = async () => {
        try {
            await markAllNotificationsAsRead()
            setNotifications((current) =>
                current.map((item) => ({ ...item, read: true }))
            )
        } catch (err) {
            setError(err.message || "Failed to mark notifications as read")
        }
    }

    const handleDelete = async (id) => {
        try {
            await deleteNotification(id)
            setNotifications((current) =>
                current.filter((item) => item.id !== id)
            )
        } catch (err) {
            setError(err.message || "Failed to delete notification")
        }
    }

    const formatDate = (value) => {
        if (!value) return ""
        return new Date(value).toLocaleString()
    }

    return (
        <div className="space-y-6">
            <div className="flex flex-col justify-between gap-4 md:flex-row md:items-end">
                <div>
                    <h1 className="text-3xl font-bold text-white">Notifications</h1>
                    <p className="mt-2 text-sm text-slate-400">
                        Stay updated on achievements, goals, and planner activity.
                    </p>
                </div>

                <div className="flex items-center gap-3">
                    <span className="rounded-full border border-slate-800 bg-slate-900 px-3 py-1.5 text-xs text-slate-400">
                        {unreadCount} unread
                    </span>
                    <button
                        type="button"
                        onClick={handleReadAll}
                        className="rounded-lg border border-slate-700 px-4 py-2 text-sm text-slate-300 transition hover:bg-slate-800 hover:text-white"
                    >
                        Mark all as read
                    </button>
                </div>
            </div>

            {error && (
                <div className="rounded-xl border border-red-500/30 bg-red-500/10 px-4 py-3 text-sm text-red-300">
                    {error}
                </div>
            )}

            {loading ? (
                <div className="rounded-2xl border border-slate-800 bg-slate-900 p-8 text-slate-400">
                    Loading notifications...
                </div>
            ) : notifications.length === 0 ? (
                <div className="rounded-2xl border border-slate-800 bg-slate-900 p-12 text-center">
                    <div className="text-4xl">🔔</div>
                    <h2 className="mt-4 text-lg font-semibold text-white">
                        You're all caught up
                    </h2>
                    <p className="mt-2 text-sm text-slate-500">
                        New achievements and progress updates will appear here.
                    </p>
                </div>
            ) : (
                <div className="space-y-3">
                    {notifications.map((notification) => (
                        <NotificationCard
                            key={notification.id}
                            notification={notification}
                            onRead={handleRead}
                            onDelete={handleDelete}
                            formatDate={formatDate}
                        />
                    ))}
                </div>
            )}
        </div>
    )
}

function NotificationCard({
    notification,
    onRead,
    onDelete,
    formatDate,
}) {
    const icon =
        notification.type === "ACHIEVEMENT"
            ? "🏆"
            : notification.type === "GOAL"
                ? "🎯"
                : notification.type === "PLANNER"
                    ? "✅"
                    : "🔔"

    return (
        <article
            onClick={() => onRead(notification)}
            className={`group cursor-pointer rounded-2xl border p-5 transition ${
                notification.read
                    ? "border-slate-800 bg-slate-900/70"
                    : "border-cyan-500/20 bg-cyan-500/5"
            }`}
        >
            <div className="flex items-start gap-4">
                <div className="flex h-11 w-11 shrink-0 items-center justify-center rounded-xl bg-slate-950 text-xl">
                    {icon}
                </div>

                <div className="min-w-0 flex-1">
                    <div className="flex flex-wrap items-center gap-2">
                        <h2 className="font-semibold text-white">
                            {notification.title}
                        </h2>
                        {!notification.read && (
                            <span className="rounded-full bg-cyan-500/10 px-2 py-0.5 text-[10px] font-semibold uppercase tracking-wide text-cyan-300">
                                New
                            </span>
                        )}
                    </div>

                    <p className="mt-2 text-sm leading-6 text-slate-400">
                        {notification.message}
                    </p>

                    <div className="mt-3 flex flex-wrap items-center gap-3 text-xs text-slate-600">
                        <span>{formatDate(notification.createdAt)}</span>
                        {notification.link && (
                            <span className="text-cyan-500">
                                Open related page →
                            </span>
                        )}
                    </div>
                </div>

                <button
                    type="button"
                    onClick={(event) => {
                        event.stopPropagation()
                        onDelete(notification.id)
                    }}
                    className="rounded-md px-2 py-1 text-xs text-slate-600 opacity-0 transition hover:bg-slate-800 hover:text-red-300 group-hover:opacity-100"
                    aria-label="Delete notification"
                >
                    Delete
                </button>
            </div>
        </article>
    )
}

export default Notifications
