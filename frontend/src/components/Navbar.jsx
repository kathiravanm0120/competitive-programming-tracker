import { useEffect, useRef, useState } from "react"
import { useNavigate } from "react-router-dom"
import { useAuth } from "../context/AuthContext"
import {
    getNotifications,
    markAllNotificationsAsRead,
    markNotificationAsRead,
} from "../services/notificationsService"

function Navbar() {
    const { user, logout } = useAuth()
    const navigate = useNavigate()
    const username = user?.username || "User"
    const initial = username.charAt(0).toUpperCase()
    const [notifications, setNotifications] = useState([])
    const [open, setOpen] = useState(false)
    const [loading, setLoading] = useState(false)
    const wrapperRef = useRef(null)

    const unreadCount = notifications.filter((item) => !item.read).length

    const loadNotifications = async () => {
        try {
            const data = await getNotifications()
            setNotifications(Array.isArray(data) ? data : [])
        } catch {
            // Notification failures should not break the navbar.
        }
    }

    useEffect(() => {
        loadNotifications()
        const interval = window.setInterval(loadNotifications, 60000)
        return () => window.clearInterval(interval)
    }, [])

    useEffect(() => {
        const handleOutsideClick = (event) => {
            if (wrapperRef.current && !wrapperRef.current.contains(event.target)) {
                setOpen(false)
            }
        }

        document.addEventListener("mousedown", handleOutsideClick)
        return () => document.removeEventListener("mousedown", handleOutsideClick)
    }, [])

    const handleBellClick = async () => {
        setOpen((current) => !current)
        if (!open) {
            setLoading(true)
            await loadNotifications()
            setLoading(false)
        }
    }

    const handleNotificationClick = async (notification) => {
        if (!notification.read) {
            try {
                const updated = await markNotificationAsRead(notification.id)
                setNotifications((current) =>
                    current.map((item) =>
                        item.id === updated.id ? updated : item
                    )
                )
            } catch {
                // Keep the dropdown usable even if read state fails.
            }
        }

        setOpen(false)

        if (notification.link) {
            navigate(notification.link)
        } else {
            navigate("/notifications")
        }
    }

    const handleReadAll = async () => {
        try {
            await markAllNotificationsAsRead()
            setNotifications((current) =>
                current.map((item) => ({ ...item, read: true }))
            )
        } catch {
            // Ignore in navbar.
        }
    }

    return (
        <header className="fixed left-64 right-0 top-0 z-20 h-16 border-b border-slate-800 bg-slate-950/90 px-6 backdrop-blur">
            <div className="flex h-full items-center justify-between">
                <div className="flex w-96 items-center rounded-lg border border-slate-800 bg-slate-900 px-4">
                    <span className="mr-3 text-slate-500">🔍</span>
                    <input
                        type="text"
                        placeholder="Search problems, contests..."
                        className="w-full bg-transparent py-2 text-sm text-white outline-none placeholder:text-slate-500"
                    />
                </div>

                <div className="flex items-center gap-5">
                    <div ref={wrapperRef} className="relative">
                        <button
                            type="button"
                            onClick={handleBellClick}
                            className="relative flex h-10 w-10 items-center justify-center rounded-lg text-slate-400 transition hover:bg-slate-900 hover:text-white"
                            aria-label="Notifications"
                        >
                            🔔
                            {unreadCount > 0 && (
                                <span className="absolute -right-1 -top-1 flex min-h-5 min-w-5 items-center justify-center rounded-full bg-cyan-400 px-1 text-[10px] font-bold text-slate-950">
                                    {unreadCount > 99 ? "99+" : unreadCount}
                                </span>
                            )}
                        </button>

                        {open && (
                            <div className="absolute right-0 mt-3 w-[380px] overflow-hidden rounded-2xl border border-slate-800 bg-slate-900 shadow-2xl shadow-black/40">
                                <div className="flex items-center justify-between border-b border-slate-800 px-4 py-3">
                                    <div>
                                        <h2 className="text-sm font-semibold text-white">
                                            Notifications
                                        </h2>
                                        <p className="mt-0.5 text-xs text-slate-500">
                                            {unreadCount} unread
                                        </p>
                                    </div>

                                    <button
                                        type="button"
                                        onClick={handleReadAll}
                                        className="text-xs text-cyan-400 hover:text-cyan-300"
                                    >
                                        Read all
                                    </button>
                                </div>

                                <div className="max-h-[420px] overflow-y-auto">
                                    {loading ? (
                                        <div className="px-4 py-8 text-center text-sm text-slate-500">
                                            Loading notifications...
                                        </div>
                                    ) : notifications.length === 0 ? (
                                        <div className="px-4 py-8 text-center">
                                            <div className="text-3xl">🔔</div>
                                            <p className="mt-3 text-sm text-slate-400">
                                                No notifications yet.
                                            </p>
                                        </div>
                                    ) : (
                                        notifications.slice(0, 8).map((notification) => (
                                            <button
                                                key={notification.id}
                                                type="button"
                                                onClick={() => handleNotificationClick(notification)}
                                                className={`block w-full border-b border-slate-800 px-4 py-3 text-left transition hover:bg-slate-800/70 ${
                                                    notification.read ? "" : "bg-cyan-500/5"
                                                }`}
                                            >
                                                <div className="flex items-start gap-3">
                                                    <span className="mt-0.5 text-lg">
                                                        {notification.type === "ACHIEVEMENT"
                                                            ? "🏆"
                                                            : notification.type === "GOAL"
                                                                ? "🎯"
                                                                : notification.type === "PLANNER"
                                                                    ? "✅"
                                                                    : "🔔"}
                                                    </span>
                                                    <div className="min-w-0 flex-1">
                                                        <p className="text-sm font-medium text-white">
                                                            {notification.title}
                                                        </p>
                                                        <p className="mt-1 line-clamp-2 text-xs leading-5 text-slate-500">
                                                            {notification.message}
                                                        </p>
                                                    </div>
                                                    {!notification.read && (
                                                        <span className="mt-2 h-2 w-2 shrink-0 rounded-full bg-cyan-400" />
                                                    )}
                                                </div>
                                            </button>
                                        ))
                                    )}
                                </div>

                                <div className="border-t border-slate-800 p-3">
                                    <button
                                        type="button"
                                        onClick={() => {
                                            setOpen(false)
                                            navigate("/notifications")
                                        }}
                                        className="w-full rounded-lg border border-slate-700 px-3 py-2 text-sm text-slate-300 transition hover:bg-slate-800 hover:text-white"
                                    >
                                        View all notifications
                                    </button>
                                </div>
                            </div>
                        )}
                    </div>

                    <div className="flex items-center gap-3">
                        <div className="flex h-9 w-9 items-center justify-center rounded-full bg-cyan-500 font-semibold text-slate-950">
                            {initial}
                        </div>

                        <div className="hidden sm:block">
                            <p className="text-sm font-medium text-white">
                                {username}
                            </p>
                            <p className="text-xs text-slate-500">
                                Competitive Programmer
                            </p>
                        </div>

                        <button
                            type="button"
                            onClick={logout}
                            className="rounded-md px-2 py-1 text-xs text-slate-500 transition hover:bg-slate-800 hover:text-white"
                        >
                            Logout
                        </button>
                    </div>
                </div>
            </div>
        </header>
    )
}

export default Navbar
