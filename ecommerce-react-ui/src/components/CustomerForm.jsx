import { useEffect, useState } from "react";

const emptyCustomer = { name: "", email: "", phone: "", address: "" };

function CustomerForm({ initialData, onSubmit, onCancel, submitText = "Save Customer" }) {
    const [form, setForm] = useState(emptyCustomer);
    const [error, setError] = useState("");

    useEffect(() => {
        setForm(initialData ? {
            name: initialData.name || "",
            email: initialData.email || "",
            phone: initialData.phone || "",
            address: initialData.address || "",
        } : { ...emptyCustomer });
    }, [initialData]);

    const handleChange = (event) => {
        const { name, value } = event.target;
        setForm((previous) => ({ ...previous, [name]: value }));
    };

    const handleSubmit = async (event) => {
        event.preventDefault();
        setError("");
        if (!/^\d{10}$/.test(form.phone)) {
            setError("Phone must contain exactly 10 digits.");
            return;
        }
        try {
            await onSubmit(form);
        } catch (submitError) {
            setError(submitError.response?.data?.message || "Unable to save customer.");
        }
    };

    return (
        <form onSubmit={handleSubmit} className="form-panel">
            <div className="form-panel-header">
                <div>
                    <h2>{initialData ? "Edit customer" : "Add customer"}</h2>
                    <p>Enter the customer details below.</p>
                </div>
                {onCancel && <button type="button" className="icon-button" onClick={onCancel} aria-label="Close form">×</button>}
            </div>
            {error && <div className="form-error">{error}</div>}
            <div className="form-grid">
                <label>Name<input name="name" placeholder="e.g. Priya Sharma" value={form.name} onChange={handleChange} required /></label>
                <label>Email<input name="email" type="email" placeholder="name@example.com" value={form.email} onChange={handleChange} required /></label>
                <label>Phone<input name="phone" inputMode="numeric" placeholder="10-digit phone" value={form.phone} onChange={handleChange} required /></label>
                <label className="form-field-wide">Address<textarea name="address" placeholder="Street, city, state" value={form.address} onChange={handleChange} required /></label>
            </div>
            <div className="form-actions">
                <button type="submit" className="primary-button">{submitText}</button>
                {onCancel && <button type="button" className="secondary-button" onClick={onCancel}>Cancel</button>}
            </div>
        </form>
    );
}

export default CustomerForm;