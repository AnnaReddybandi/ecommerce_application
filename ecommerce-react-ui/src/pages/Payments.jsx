import { useEffect, useState } from "react";
import paymentService from "../services/paymentService";
import orderService from "../services/orderService";
import Loading from "../components/Loading";
import ErrorMessage from "../components/ErrorMessage";

function Payments() {
    const [payments, setPayments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [actionError, setActionError] = useState("");
    const [bill, setBill] = useState(null);

    const loadPayments = async () => {
        try {
            setLoading(true);
            setError("");

            const response =
                await paymentService.getAllPayments();

            const data =
                Array.isArray(response)
                    ? response
                    : response?.data ??
                    response?.content ??
                    [];

            setPayments(data);
        } catch (err) {
            console.error(err);
            setError("Unable to load payments.");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadPayments();
    }, []);

    const processPayment = async (id) => {
        try {
            setActionError("");
            const updated = await paymentService.processPayment(id);
            setPayments((previous) => previous.map((payment) => (payment.id ?? payment.paymentId) === id ? updated : payment));
            const order = await orderService.getOrderById(updated.orderId);
            setBill({ payment: updated, order });
        } catch (err) {
            console.error(err);
            setActionError(err.response?.data?.message || "Unable to process payment.");
        }
    };

    const printBill = () => {
        window.print();
    };

    const deletePayment = async (id) => {
        if (!window.confirm("Delete this payment?")) return;
        try {
            await paymentService.deletePayment(id);
            setPayments((previous) => previous.filter((payment) => (payment.id ?? payment.paymentId) !== id));
        } catch (err) {
            console.error(err);
            setActionError("Unable to delete payment.");
        }
    };

    if (loading) {
        return <Loading message="Loading payments..." />;
    }

    if (error) {
        return (
            <ErrorMessage
                message={error}
                onRetry={loadPayments}
            />
        );
    }

    return (
        <div className="page">
            <div className="page-header">
                <div>
                    <h1>Payments</h1>
                    <p>
                        View customer payment
                        transactions.
                    </p>
                </div>
            </div>

            {actionError && <div className="action-error">{actionError}</div>}

            {bill && (
                <section className="success-bill" aria-live="polite">
                    <div className="bill-success-icon">✓</div>
                    <div className="bill-header">
                        <div>
                            <p className="bill-eyebrow">Payment successful</p>
                            <h2>Order #{bill.order.id} confirmed</h2>
                            <p className="bill-message">Your payment was completed successfully. Keep this bill for your records.</p>
                        </div>
                        <button className="primary-button no-print" onClick={printBill}>Print Bill</button>
                    </div>
                    <div className="bill-details">
                        <div><span>Payment ID</span><strong>#{bill.payment.id}</strong></div>
                        <div><span>Transaction ID</span><strong>{bill.payment.transactionId || "-"}</strong></div>
                        <div><span>Customer</span><strong>{bill.order.customerName || `Customer #${bill.order.customerId}`}</strong></div>
                        <div><span>Payment method</span><strong>{bill.payment.method}</strong></div>
                        <div><span>Payment status</span><strong className="bill-paid">SUCCESS</strong></div>
                        <div><span>Order total</span><strong>₹{Number(bill.payment.amount).toLocaleString("en-IN")}</strong></div>
                    </div>
                    <div className="bill-address"><span>Shipping address</span><strong>{bill.order.shippingAddress}</strong></div>
                </section>
            )}

            <div className="info-panel">
                <strong>Payments are created during checkout.</strong>
                <span>Use the Shopping Cart page to add products, checkout, and create the payment record automatically.</span>
            </div>

            <div className="table-container">
                <table>
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Order</th>
                        <th>Amount</th>
                        <th>Method</th>
                        <th>Status</th>
                        <th>Created</th>
                        <th>Actions</th>
                    </tr>
                    </thead>

                    <tbody>
                    {payments.map((payment) => {
                        const id =
                            payment.id ??
                            payment.paymentId;

                        const order =
                            payment.orderId ??
                            payment.order?.id ??
                            "—";

                        const amount =
                            payment.amount ?? 0;

                        const method =
                            payment.paymentMethod ??
                            payment.method ??
                            "—";

                        const status =
                            payment.status ??
                            payment.paymentStatus ??
                            "—";

                        return (
                            <tr key={id}>
                                <td>{id}</td>
                                <td>{order}</td>
                                <td>
                                    ₹
                                    {Number(
                                        amount
                                    ).toLocaleString(
                                        "en-IN"
                                    )}
                                </td>
                                <td>{method}</td>
                                <td>
                                        <span className="status-badge">
                                            {status}
                                        </span>
                                </td>
                                <td>
                                    {payment.createdAt ??
                                        "—"}
                                </td>
                                <td>
                                    {status === "PENDING" && <button className="primary-button" onClick={() => processPayment(id)}>Process</button>}
                                    <button className="delete-button" onClick={() => deletePayment(id)}>Delete</button>
                                </td>
                            </tr>
                        );
                    })}
                    </tbody>
                </table>

                {payments.length === 0 && (
                    <div className="empty-state">
                        <h3>No payments found</h3>
                    </div>
                )}
            </div>
        </div>
    );
}

export default Payments;