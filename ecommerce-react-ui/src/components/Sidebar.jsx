import { NavLink } from "react-router-dom";

function Sidebar({ isOpen, onClose }) {
    const menuItems = [
        {
            path: "/",
            label: "Dashboard",
            icon: "📊",
        },
        {
            path: "/products",
            label: "Products",
            icon: "📦",
        },
        {
            path: "/customers",
            label: "Customers",
            icon: "👥",
        },
        {
            path: "/cart",
            label: "Shopping Cart",
            icon: "🛒",
        },
        {
            path: "/orders",
            label: "Orders",
            icon: "📋",
        },
        {
            path: "/payments",
            label: "Payments",
            icon: "💳",
        },
    ];

    return (
        <>
            {isOpen && (
                <div
                    className="sidebar-overlay"
                    onClick={onClose}
                />
            )}

            <aside className={`sidebar ${isOpen ? "sidebar-open" : ""}`}>
                <div className="sidebar-header">
                    <h2>ShopAdmin</h2>

                    <button
                        className="sidebar-close"
                        onClick={onClose}
                    >
                        ×
                    </button>
                </div>

                <nav className="sidebar-nav">
                    {menuItems.map((item) => (
                        <NavLink
                            key={item.path}
                            to={item.path}
                            end={item.path === "/"}
                            className={({ isActive }) =>
                                `sidebar-link ${
                                    isActive ? "active" : ""
                                }`
                            }
                            onClick={onClose}
                        >
                            <span className="sidebar-icon">
                                {item.icon}
                            </span>

                            <span>{item.label}</span>
                        </NavLink>
                    ))}
                </nav>
            </aside>
        </>
    );
}

export default Sidebar;