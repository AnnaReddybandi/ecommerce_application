import { useState } from "react";
import { Outlet } from "react-router-dom";
import Navbar from "../components/Navbar";
import Sidebar from "../components/Sidebar";

function AdminLayout() {
    const [sidebarOpen, setSidebarOpen] = useState(false);

    const handleMenuClick = () => {
        setSidebarOpen(true);
    };

    const handleSidebarClose = () => {
        setSidebarOpen(false);
    };

    return (
        <div className="admin-layout">
            <Sidebar
                isOpen={sidebarOpen}
                onClose={handleSidebarClose}
            />

            <div className="main-wrapper">
                <Navbar onMenuClick={handleMenuClick} />

                <main className="main-content">
                    <Outlet />
                </main>
            </div>
        </div>
    );
}

export default AdminLayout;