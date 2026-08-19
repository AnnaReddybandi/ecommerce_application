import { useState } from "react";

function Navbar({ onMenuClick }) {
    const [showProfile, setShowProfile] = useState(false);

    return (
        <header className="navbar">
            <div className="navbar-left">
                <button
                    className="menu-button"
                    onClick={onMenuClick}
                    aria-label="Open menu"
                >
                    ☰
                </button>

                <h2 className="navbar-title">
                    E-Commerce Admin
                </h2>
            </div>

            <div className="navbar-right">
                <button className="notification-button">
                    🔔
                </button>

                <div className="profile-container">
                    <button
                        className="profile-button"
                        onClick={() => setShowProfile(!showProfile)}
                    >
                        <span className="profile-avatar">
                            A
                        </span>

                        <span className="profile-name">
                            Admin
                        </span>

                        <span>⌄</span>
                    </button>

                    {showProfile && (
                        <div className="profile-menu">
                            <button>Profile</button>
                            <button>Settings</button>
                            <button
                                onClick={() => {
                                    localStorage.removeItem("token");
                                    window.location.href = "/";
                                }}
                            >
                                Logout
                            </button>
                        </div>
                    )}
                </div>
            </div>
        </header>
    );
}

export default Navbar;