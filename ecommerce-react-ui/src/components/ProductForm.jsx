import { useEffect, useState } from "react";

const categories = [
    "ELECTRONICS",
    "FASHION",
    "HOME",
    "BOOKS",
    "BEAUTY",
    "SPORTS",
    "GROCERY",
];

function ProductForm({
                         initialData,
                         onSubmit,
                         onCancel,
                         submitText = "Save Product",
                     }) {
    const [form, setForm] = useState({
        name: "",
        description: "",
        price: "",
        stock: "",
        category: "ELECTRONICS",
        imageUrl: "",
    });

    const [error, setError] = useState("");

    useEffect(() => {
        setForm(initialData ? {
                name: initialData.name || "",
                description: initialData.description || "",
                price: initialData.price || "",
                stock: initialData.stock || "",
                category:
                    initialData.category || "ELECTRONICS",
                imageUrl: initialData.imageUrl || "",
            } : {
                name: "",
                description: "",
                price: "",
                stock: "",
                category: "ELECTRONICS",
                imageUrl: "",
            });
    }, [initialData]);

    const handleChange = (event) => {
        const { name, value } = event.target;

        setForm((previous) => ({
            ...previous,
            [name]: value,
        }));
    };

    const handleSubmit = async (event) => {
        event.preventDefault();
        setError("");

        if (!form.name.trim()) {
            setError("Product name is required.");
            return;
        }

        if (Number(form.price) <= 0) {
            setError("Price must be greater than zero.");
            return;
        }

        if (Number(form.stock) < 0) {
            setError("Stock cannot be negative.");
            return;
        }

        try {
            const imageFile = form.imageFile;
            await onSubmit({
                name: form.name.trim(),
                description: form.description.trim(),
                price: Number(form.price),
                stock: Number(form.stock),
                category: form.category,
                imageUrl: form.imageUrl.trim(),
            }, imageFile);
        } catch (submitError) {
            setError(submitError.response?.data?.message || "Unable to save product.");
        }
    };

    return (
        <form onSubmit={handleSubmit} className="form-panel">
            <div className="form-panel-header">
                <div>
                    <h2>{initialData ? "Edit product" : "Add product"}</h2>
                    <p>Keep pricing and inventory details accurate.</p>
                </div>
                {onCancel && <button type="button" className="icon-button" onClick={onCancel} aria-label="Close form">×</button>}
            </div>
            {error && <div className="form-error">{error}</div>}

            <div className="form-grid">
                <label>Product name
                    <input type="text" name="name" value={form.name} onChange={handleChange} required />
                </label>

                <label>Price
                    <input
                        type="number"
                        name="price"
                        min="0.01"
                        step="0.01"
                        value={form.price}
                        onChange={handleChange}
                        required
                    />
                </label>

                <label>Stock
                    <input
                        type="number"
                        name="stock"
                        min="0"
                        value={form.stock}
                        onChange={handleChange}
                        required
                    />
                </label>

                <label>Category
                    <select name="category" value={form.category} onChange={handleChange}>
                        {categories.map((category) => <option key={category} value={category}>{category}</option>)}
                    </select>
                </label>

                <label className="form-field-wide">Description
                    <textarea name="description" rows="3" value={form.description} onChange={handleChange} />
                </label>

                <label className="form-field-wide">Image URL (optional)
                    <input type="text" name="imageUrl" placeholder="https://example.com/product.jpg or /uploads/products/file.jpg" value={form.imageUrl} onChange={handleChange} />
                </label>

                <label className="form-field-wide">Upload image (optional)
                    <input type="file" accept="image/png,image/jpeg,image/webp" onChange={(event) => setForm((previous) => ({ ...previous, imageFile: event.target.files?.[0] }))} />
                    <small className="field-help">Choose an image file or provide an image URL.</small>
                </label>
            </div>

            <div className="form-actions">
                <button type="submit" className="primary-button">{submitText}</button>
                {onCancel && <button type="button" className="secondary-button" onClick={onCancel}>Cancel</button>}
            </div>
        </form>
    );
}

export default ProductForm;