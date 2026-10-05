import { BrowserRouter, Route, Routes } from "react-router-dom"

import Dashboard from "./pages/Dashboard"
import Problems from "./pages/Problems"
import Login from "./pages/Login"
import Register from "./pages/Register"
import Profile from "./pages/Profile"
import Goals from "./pages/Goals"
import DailyPlanner from "./pages/DailyPlanner"
import Achievements from "./pages/Achievements"
import Notifications from "./pages/Notifications"
import Reports from "./pages/Reports"

import AdminDashboard from "./pages/AdminDashboard"
import AdminUsers from "./pages/AdminUsers"
import AdminProblems from "./pages/AdminProblems"

import DashboardLayout from "./layouts/DashboardLayout"
import ProtectedRoute from "./components/ProtectedRoute"
import AdminRoute from "./components/AdminRoute"

import { AuthProvider } from "./context/AuthContext"

function App() {
    return (
        <BrowserRouter>
            <AuthProvider>
                <Routes>
                    {/* Public routes */}
                    <Route path="/login" element={<Login />} />
                    <Route path="/register" element={<Register />} />

                    {/* Protected user routes */}
                    <Route element={<ProtectedRoute />}>
                        <Route element={<DashboardLayout />}>
                            <Route path="/" element={<Dashboard />} />
                            <Route path="/problems" element={<Problems />} />
                            <Route path="/profile" element={<Profile />} />
                            <Route path="/goals" element={<Goals />} />
                            <Route path="/planner" element={<DailyPlanner />} />
                            <Route path="/achievements" element={<Achievements />} />
                            <Route path="/notifications" element={<Notifications />} />
                            <Route path="/reports" element={<Reports />} />
                        </Route>
                    </Route>

                    {/* Protected admin routes */}
                    <Route element={<ProtectedRoute />}>
                        <Route element={<AdminRoute />}>
                            <Route element={<DashboardLayout />}>
                                <Route path="/admin" element={<AdminDashboard />} />
                                <Route path="/admin/users" element={<AdminUsers />} />
                                <Route path="/admin/problems" element={<AdminProblems />} />
                            </Route>
                        </Route>
                    </Route>

                    {/* Unknown route */}
                    <Route path="*" element={<Login />} />
                </Routes>
            </AuthProvider>
        </BrowserRouter>
    )
}

export default App
