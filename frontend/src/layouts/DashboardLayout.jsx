import { Outlet } from "react-router-dom"
import Sidebar from "../components/Sidebar"
import Navbar from "../components/Navbar"

function DashboardLayout() {
    return (
        <div className="min-h-screen bg-slate-950 text-white">
            <Sidebar />
            <Navbar />

            <main className="ml-64 pt-16">
                <div className="p-8">
                    <Outlet />
                </div>
            </main>
        </div>
    )
}

export default DashboardLayout
