import { useEffect, useState } from "react";
import orderService from "../services/orderService";
import Loading from "../components/Loading";
import ErrorMessage from "../components/ErrorMessage";

function Orders() {
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [actionError, setActionError] = useState("");

    const loadOrders = async () => {
        try {
            setLoading(true);
            setError("");

            const response =
                await orderService.getAllOrders();

            const data =
                Array.isArray(response)
                    ? response
                    : response?.data ??
                    response?.content ??
                    [];

            setOrders(data);
        } catch (err) {
            console.error(err);
            setError("Unable to load orders.");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadOrders();
    }, []);

    const changeStatus = async (id, action) => {
        try {
            const updated = action === "confirm"
                ? await orderService.confirmOrder(id)
                : await orderService.cancelOrder(id);
            setOrders((previous) => previous.map((order) => (order.id ?? order.orderId) === id ? updated : order));
        } catch (err) {
            console.error(err);
            setActionError(err.response?.data?.message || "Unable to update order.");
        }
    };

    const deleteOrder = async (id) => {
        if (!window.confirm("Delete this order?")) return;
        try {
            await orderService.deleteOrder(id);
            setOrders((previous) => previous.filter((order) => (order.id ?? order.orderId) !== id));
        } catch (err) {
            console.error(err);
            setActionError(err.response?.data?.message || "Unable to delete order.");
        }
    };

    if (loading) {
        return <Loading message="Loading orders..." />;
    }

    if (error) {
        return (
            <ErrorMessage
                message={error}
                onRetry={loadOrders}
            />
        );
    }

    return (
        <div className="page">
            <div className="page-header">
                <div>
                    <h1>Orders</h1>
                    <p>
                        Manage customer orders and order
                        status.
                    </p>
                </div>
            </div>

            {actionError && <div className="action-error">{actionError}</div>}

            <div className="table-container">
                <table>
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Customer</th>
                        <th>Total</th>
                        <th>Status</th>
                        <th>Created</th>
                        <th>Actions</th>
                    </tr>
                    </thead>

                    <tbody>
                    {orders.map((order) => {
                        const id =
                            order.id ??
                            order.orderId;

                        const customer =
                            order.customerName ??
                            order.customerId ??
                            "—";

                        const total =
                            order.totalAmount ??
                            order.total ??
                            0;

                        const status =
                            order.status ??
                            order.orderStatus ??
                            "—";

                        return (
                            <tr key={id}>
                                <td>{id}</td>
                                <td>{customer}</td>
                                <td>
                                    ₹
                                    {Number(
                                        total
                                    ).toLocaleString(
                                        "en-IN"
                                    )}
                                </td>
                                <td>
                                        <span className="status-badge">
                                            {status}
                                        </span>
                                </td>
                                <td>
                                    {order.createdAt ??
                                        "—"}
                                </td>
                                <td>
                                    {status === "PENDING" && <button className="primary-button" onClick={() => changeStatus(id, "confirm")}>Confirm</button>}
                                    {status !== "CANCELLED" && status !== "DELIVERED" && <button className="secondary-button" onClick={() => changeStatus(id, "cancel")}>Cancel</button>}
                                    <button className="delete-button" onClick={() => deleteOrder(id)}>Delete</button>
                                </td>
                            </tr>
                        );
                    })}
                    </tbody>
                </table>

                {orders.length === 0 && (
                    <div className="empty-state">
                        <h3>No orders found</h3>
                    </div>
                )}
            </div>
        </div>
    );
}

export default Orders;