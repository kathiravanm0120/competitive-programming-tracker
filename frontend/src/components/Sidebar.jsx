import { NavLink } from "react-router-dom"
import { useAuth } from "../context/AuthContext"

function Sidebar() {
    const { isAdmin } = useAuth()
    const linkClass = ({ isActive }) =>
        `flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-medium transition ${
            isActive
                ? "bg-cyan-500/10 text-cyan-400"
                : "text-slate-400 hover:bg-slate-800 hover:text-white"
        }`

    const comingSoonClass =
        "flex cursor-not-allowed items-center gap-3 rounded-lg px-3 py-2.5 text-sm text-slate-600"

    return (
        <aside className="fixed left-0 top-0 h-screen w-64 border-r border-slate-800 bg-slate-950 px-4 py-6">
            <div className="mb-8 px-3">
                <h1 className="text-xl font-bold text-cyan-400">CP Tracker</h1>
                <p className="mt-1 text-xs text-slate-500">
                    Competitive Programming
                </p>
            </div>

            <nav className="space-y-2">
                <NavLink to="/" end className={linkClass}>
                    <span>⌂</span>
                    Dashboard
                </NavLink>

                <NavLink to="/problems" className={linkClass}>
                    <span>◈</span>
                    Problems
                </NavLink>

                <NavLink to="/profile" className={linkClass}>
                    <span>◉</span>
                    Profile
                </NavLink>

                <NavLink to="/goals" className={linkClass}>
                    <span>◎</span>
                    Goals
                </NavLink>

                <NavLink to="/planner" className={linkClass}>
                    <span>☑</span>
                    Daily Planner
                </NavLink>

                <div className={comingSoonClass}>
                    <span>♛</span>
                    Contests
                    <span className="ml-auto text-[10px]">Soon</span>
                </div>

                <div className={comingSoonClass}>
                    <span>◉</span>
                    Platforms
                    <span className="ml-auto text-[10px]">Soon</span>
                </div>

                <div className={comingSoonClass}>
                    <span>⌁</span>
                    Analytics
                    <span className="ml-auto text-[10px]">Soon</span>
                </div>

                <NavLink to="/achievements" className={linkClass}>
                    <span>★</span>
                    Achievements
                </NavLink>

                <NavLink to="/notifications" className={linkClass}>
                    <span>🔔</span>
                    Notifications
                </NavLink>

                <NavLink to="/reports" className={linkClass}>
                    <span>▣</span>
                    Reports
                </NavLink>

                {isAdmin && (
                    <div className="mt-5 border-t border-slate-800 pt-5">
                        <p className="px-3 pb-2 text-[10px] font-semibold uppercase tracking-[0.2em] text-slate-600">Administration</p>
                        <NavLink to="/admin" end className={linkClass}>
                            <span>⚙</span>
                            Admin Dashboard
                        </NavLink>
                        <NavLink to="/admin/users" className={linkClass}>
                            <span>♙</span>
                            Users
                        </NavLink>
                        <NavLink to="/admin/problems" className={linkClass}>
                            <span>▦</span>
                            Problems
                        </NavLink>
                    </div>
                )}
            </nav>

            <div className="absolute bottom-6 left-4 right-4">
                <div className={comingSoonClass}>
                    <span>⚙</span>
                    Settings
                    <span className="ml-auto text-[10px]">Soon</span>
                </div>
            </div>
        </aside>
    )
}

export default Sidebar
