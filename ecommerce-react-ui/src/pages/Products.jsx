import { useEffect, useState } from "react";
import productService from "../services/productService";
import ProductCard from "../components/ProductCard";
import Loading from "../components/Loading";
import ErrorMessage from "../components/ErrorMessage";
import ProductForm from "../components/ProductForm";

function Products() {
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const [search, setSearch] = useState("");
    const [showForm, setShowForm] = useState(false);
    const [editingProduct, setEditingProduct] = useState(null);
    const [actionError, setActionError] = useState("");

    const loadProducts = async () => {
        try {
            setLoading(true);
            setError("");

            const response =
                await productService.getAllProducts();

            const data =
                Array.isArray(response)
                    ? response
                    : response?.data ??
                    response?.content ??
                    [];

            setProducts(data);
        } catch (err) {
            console.error(err);
            setError("Unable to load products.");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadProducts();
    }, []);

    const handleCreate = async (product, imageFile) => {
        try {
            const created = await productService.createProduct(product);
            if (imageFile && created?.id) {
                await productService.uploadImage(created.id, imageFile);
            }
            setShowForm(false);
            await loadProducts();
        } catch (err) {
            console.error(err);
            throw err;
        }
    };

    const handleSave = async (product, imageFile) => {
        if (!editingProduct) {
            await handleCreate(product, imageFile);
            return;
        }
        const id = editingProduct.id ?? editingProduct.productId;
        let updated = await productService.updateProduct(id, product);
        if (imageFile) {
            updated = await productService.uploadImage(id, imageFile);
        }
        setProducts((previous) => previous.map((item) =>
            (item.id ?? item.productId) === id ? updated : item
        ));
        setEditingProduct(null);
        setShowForm(false);
    };

    const handleDelete = async (id) => {
        const confirmed = window.confirm(
            "Are you sure you want to delete this product?"
        );

        if (!confirmed) {
            return;
        }

        try {
            await productService.deleteProduct(id);

            setProducts((previous) =>
                previous.filter(
                    (product) =>
                        (product.id ??
                            product.productId) !== id
                )
            );
        } catch (err) {
            console.error(err);
            setActionError(err.response?.data?.message || "Unable to delete product.");
        }
    };

    const filteredProducts = products.filter((product) => {
        const name =
            product?.name ??
            product?.productName ??
            "";

        return name
            .toLowerCase()
            .includes(search.toLowerCase());
    });

    if (loading) {
        return <Loading message="Loading products..." />;
    }

    if (error) {
        return (
            <ErrorMessage
                message={error}
                onRetry={loadProducts}
            />
        );
    }

    return (
        <div className="page">
            <div className="page-header">
                <div>
                    <h1>Products</h1>
                    <p>Manage your product catalog.</p>
                </div>

                <button
                    className="primary-button"
                    onClick={() => { setActionError(""); setEditingProduct(null); setShowForm((visible) => !visible); }}
                >
                    + Add Product
                </button>
            </div>

            {showForm && (
                <div className="form-container">
                    <ProductForm
                        onSubmit={handleSave}
                        initialData={editingProduct}
                        onCancel={() => { setEditingProduct(null); setShowForm(false); }}
                        submitText={editingProduct ? "Update Product" : "Create Product"}
                    />
                </div>
            )}

            {actionError && <div className="action-error">{actionError}</div>}

            <div className="toolbar">
                <input
                    type="text"
                    placeholder="Search products..."
                    value={search}
                    onChange={(event) =>
                        setSearch(event.target.value)
                    }
                />
            </div>

            {filteredProducts.length === 0 ? (
                <div className="empty-state">
                    <h3>No products found</h3>
                    <p>
                        There are no products matching your
                        search.
                    </p>
                </div>
            ) : (
                <div className="products-grid">
                    {filteredProducts.map((product) => (
                        <ProductCard
                            key={
                                product.id ??
                                product.productId
                            }
                            product={product}
                            onDelete={handleDelete}
                            onEdit={(item) => { setActionError(""); setEditingProduct(item); setShowForm(true); }}
                        />
                    ))}
                </div>
            )}
        </div>
    );
}

export default Products;