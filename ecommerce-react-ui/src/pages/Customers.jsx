import { useEffect, useState } from "react";
import customerService from "../services/customerService";
import Loading from "../components/Loading";
import ErrorMessage from "../components/ErrorMessage";
import CustomerForm from "../components/CustomerForm";

function Customers() {
    const [customers, setCustomers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [editingCustomer, setEditingCustomer] = useState(null);
    const [showForm, setShowForm] = useState(false);
    const [actionError, setActionError] = useState("");

    const loadCustomers = async () => {
        try {
            setLoading(true);
            setError("");

            const response =
                await customerService.getAllCustomers();

            const data =
                Array.isArray(response)
                    ? response
                    : response?.data ??
                    response?.content ??
                    [];

            setCustomers(data);
        } catch (err) {
            console.error(err);
            setError("Unable to load customers.");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadCustomers();
    }, []);

    const handleSave = async (customer) => {
        if (editingCustomer) {
            const id = editingCustomer.id ?? editingCustomer.customerId;
            const updated = await customerService.updateCustomer(id, customer);
            setCustomers((previous) => previous.map((item) =>
                (item.id ?? item.customerId) === id ? updated : item
            ));
        } else {
            const created = await customerService.createCustomer(customer);
            setCustomers((previous) => [...previous, created]);
        }
        setEditingCustomer(null);
        setShowForm(false);
    };

    const handleDelete = async (id) => {
        const confirmed = window.confirm(
            "Are you sure you want to delete this customer?"
        );

        if (!confirmed) {
            return;
        }

        try {
            await customerService.deleteCustomer(id);

            setCustomers((previous) =>
                previous.filter(
                    (customer) =>
                        (customer.id ??
                            customer.customerId) !== id
                )
            );
        } catch (err) {
            console.error(err);
            setActionError(err.response?.data?.message || "Unable to delete customer.");
        }
    };

    if (loading) {
        return <Loading message="Loading customers..." />;
    }

    if (error) {
        return (
            <ErrorMessage
                message={error}
                onRetry={loadCustomers}
            />
        );
    }

    return (
        <div className="page">
            <div className="page-header">
                <div>
                    <h1>Customers</h1>
                    <p>
                        Manage registered customers.
                    </p>
                </div>

                <button className="primary-button" onClick={() => { setActionError(""); setEditingCustomer(null); setShowForm((visible) => !visible); }}>
                    + Add Customer
                </button>
            </div>

            {showForm && (
                <CustomerForm
                    initialData={editingCustomer}
                    onSubmit={handleSave}
                    onCancel={() => { setEditingCustomer(null); setShowForm(false); }}
                    submitText={editingCustomer ? "Update Customer" : "Create Customer"}
                />
            )}

            {actionError && <div className="action-error">{actionError}</div>}

            <div className="table-container">
                <table>
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Email</th>
                        <th>Phone</th>
                        <th>Actions</th>
                    </tr>
                    </thead>

                    <tbody>
                    {customers.map((customer) => {
                        const id =
                            customer.id ??
                            customer.customerId;

                        const name =
                            customer.name ??
                            customer.customerName ??
                            "—";

                        const email =
                            customer.email ?? "—";

                        const phone =
                            customer.phone ??
                            customer.phoneNumber ??
                            "—";

                        return (
                            <tr key={id}>
                                <td>{id}</td>
                                <td>{name}</td>
                                <td>{email}</td>
                                <td>{phone}</td>
                                <td>
                                    <button className="edit-button" onClick={() => { setActionError(""); setEditingCustomer(customer); setShowForm(true); }}>
                                        Edit
                                    </button>

                                    <button
                                        className="delete-button"
                                        onClick={() =>
                                            handleDelete(
                                                id
                                            )
                                        }
                                    >
                                        Delete
                                    </button>
                                </td>
                            </tr>
                        );
                    })}
                    </tbody>
                </table>

                {customers.length === 0 && (
                    <div className="empty-state">
                        <h3>No customers found</h3>
                    </div>
                )}
            </div>
        </div>
    );
}

export default Customers;