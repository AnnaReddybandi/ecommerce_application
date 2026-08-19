import { useEffect, useState } from "react";
import productService from "../services/productService";
import customerService from "../services/customerService";
import orderService from "../services/orderService";
import cartService from "../services/cartService";
import paymentService from "../services/paymentService";
import Loading from "../components/Loading";

const extractData = (response) => {
    if (Array.isArray(response)) return response;
    if (Array.isArray(response?.data)) return response.data;
    if (Array.isArray(response?.content)) return response.content;
    return [];
};

function Dashboard() {
    const [stats, setStats] = useState({
        products: 0,
        customers: 0,
        orders: 0,
        carts: 0,
        payments: 0,
        revenue: 0,
        pendingPayments: 0,
    });
    const [recentOrders, setRecentOrders] = useState([]);
    const [lowStockProducts, setLowStockProducts] = useState([]);

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const loadDashboard = async () => {
        try {
            setLoading(true);
            setError("");

            const results = await Promise.allSettled([
                productService.getAllProducts(),
                customerService.getAllCustomers(),
                orderService.getAllOrders(),
                cartService.getAllCarts(),
                paymentService.getAllPayments(),
            ]);

            const [productsResult, customersResult, ordersResult, cartsResult, paymentsResult] = results;
            const products = productsResult.status === "fulfilled" ? extractData(productsResult.value) : [];
            const customers = customersResult.status === "fulfilled" ? extractData(customersResult.value) : [];
            const orders = ordersResult.status === "fulfilled" ? extractData(ordersResult.value) : [];
            const carts = cartsResult.status === "fulfilled" ? extractData(cartsResult.value) : [];
            const payments = paymentsResult.status === "fulfilled" ? extractData(paymentsResult.value) : [];
            const successfulPayments = payments.filter((payment) => payment.status === "SUCCESS");

            setStats({
                products: products.length,
                customers: customers.length,
                orders: orders.length,
                carts: carts.length,
                payments: payments.length,
                revenue: successfulPayments.reduce((total, payment) => total + Number(payment.amount || 0), 0),
                pendingPayments: payments.filter((payment) => payment.status === "PENDING").length,
            });
            setRecentOrders([...orders].sort((left, right) => new Date(right.createdAt || right.orderDate) - new Date(left.createdAt || left.orderDate)).slice(0, 5));
            setLowStockProducts(products.filter((product) => product.status !== "INACTIVE" && Number(product.stock) <= 10).sort((left, right) => left.stock - right.stock).slice(0, 5));

            if (results.every((result) => result.status === "rejected")) {
                throw new Error("All dashboard requests failed");
            }
        } catch (err) {
            console.error(err);
            setError(
                "Unable to load dashboard information."
            );
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadDashboard();
    }, []);

    if (loading) {
        return <Loading message="Loading dashboard..." />;
    }

    return (
        <div className="page">
            <div className="page-header">
                <div>
                    <h1>Dashboard</h1>
                    <p>
                        Welcome to your e-commerce
                        administration panel.
                    </p>
                </div>
                <button className="secondary-button" onClick={loadDashboard} disabled={loading}>
                    {loading ? "Refreshing..." : "Refresh dashboard"}
                </button>
            </div>

            {error && <div className="action-error">Some dashboard data could not be loaded. Refresh to try again.</div>}

            <div className="stats-grid">
                <div className="stat-card">
                    <div className="stat-icon">📦</div>
                    <div>
                        <p>Total Products</p>
                        <h2>{stats.products}</h2>
                    </div>
                </div>

                <div className="stat-card">
                    <div className="stat-icon">👥</div>
                    <div>
                        <p>Total Customers</p>
                        <h2>{stats.customers}</h2>
                    </div>
                </div>

                <div className="stat-card">
                    <div className="stat-icon">📋</div>
                    <div>
                        <p>Total Orders</p>
                        <h2>{stats.orders}</h2>
                    </div>
                </div>
                <div className="stat-card">
                    <div className="stat-icon">🛒</div>
                    <div><p>Shopping Carts</p><h2>{stats.carts}</h2></div>
                </div>
                <div className="stat-card">
                    <div className="stat-icon">💳</div>
                    <div><p>Payments</p><h2>{stats.payments}</h2></div>
                </div>
                <div className="stat-card">
                    <div className="stat-icon">₹</div>
                    <div><p>Successful Revenue</p><h2>₹{stats.revenue.toLocaleString("en-IN")}</h2></div>
                </div>
            </div>

            <div className="dashboard-columns">
                <section className="dashboard-panel">
                    <div className="section-heading"><div><h2>Recent orders</h2><p>Latest order activity</p></div><span className="dashboard-count">{stats.pendingPayments} pending payments</span></div>
                    {recentOrders.length === 0 ? <div className="dashboard-empty">No orders available.</div> : <div className="dashboard-list">{recentOrders.map((order) => <div className="dashboard-list-row" key={order.id}><div><strong>Order #{order.id}</strong><span>{order.customerName || `Customer #${order.customerId}`}</span></div><div><strong>₹{Number(order.totalAmount || 0).toLocaleString("en-IN")}</strong><span className="status-badge">{order.status}</span></div></div>)}</div>}
                </section>
                <section className="dashboard-panel">
                    <div className="section-heading"><div><h2>Stock attention</h2><p>Active products with 10 or fewer units</p></div><span className="dashboard-count">{lowStockProducts.length} items</span></div>
                    {lowStockProducts.length === 0 ? <div className="dashboard-empty">Stock levels look healthy.</div> : <div className="dashboard-list">{lowStockProducts.map((product) => <div className="dashboard-list-row" key={product.id}><div><strong>{product.name}</strong><span>{product.category}</span></div><strong className="stock-low">{product.stock} left</strong></div>)}</div>}
                </section>
            </div>
        </div>
    );
}

export default Dashboard;